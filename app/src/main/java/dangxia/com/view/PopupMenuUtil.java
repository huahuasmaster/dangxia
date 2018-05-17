package dangxia.com.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dangxia.com.R;
import dangxia.com.entity.PriceSection;
import dangxia.com.entity.TaskClassDto;
import dangxia.com.ui.CommunityFragment;
import dangxia.com.ui.LocChooseActivity;
import dangxia.com.ui.QuickFragment;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import dangxia.com.utils.location.LocationUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;


/**
 * Created by mj
 * on 2016/10/28.
 */
public class PopupMenuUtil {

    private static final String TAG = "PopupMenuUtil";

    public static final String QUICK_TASK = "快速需求";
    public static final String COMMON_TASK = "长期需求";

    private static List<TaskClassDto> classDtoList;
    private int taskClassId = -1;
    private String type;
    public static final int REQUEST_FOR_LOC = 0x12;
    private static String[] classes;

    public static PopupMenuUtil getInstance() {
        return MenuUtilHolder.INSTANCE;
    }

    private static class MenuUtilHolder {
        public static PopupMenuUtil INSTANCE = new PopupMenuUtil();
    }

    private View rootView;
    private PopupWindow popupWindow;
    private static View showOnView;
    private RelativeLayout rlClick;
    private ImageView ivBtn;
    private View transBack;
    private EditText priceEdit;
    private EditText desEdit;
    private EditText locationEdit;
    private Switch auditSwitch;
    private ImageView goChooseLoc;
    private Button chooseClassBtn;
    private boolean allowSend = false;//允许发送
    private boolean isQuick = false;//是否为快速需求
    private boolean classChoosed = false;//已经选择过大类
    private boolean priceNoticed = false;//提醒过价格
    private boolean chooseNoticed = false;//提醒过去选择分类
    private double tarLatitude;
    private double tarLongitude;
    private MaterialDialog chooseClassesDialog;
    private MaterialDialog PriceDialog;
    private MaterialDialog noticeChooseDialog;
    private PriceSection priceSection;
    //    private LinearLayout llTest1, llTest2, llTest3, llTest4, llTest5, llTest6, llTest7, llTest8;
    private CardView cardView;

    public static View getShowOnView() {
        return showOnView;
    }

    public static void setShowOnView(View showOnView) {
        PopupMenuUtil.showOnView = showOnView;
    }

    /**
     * 动画执行的 属性值数组
     */
    float animatorProperty[] = null;
    /**
     * 第一排图 距离屏幕底部的距离
     */
    int top = 0;
    /**
     * 第二排图 距离屏幕底部的距离
     */
    int bottom = 0;

    /**
     * 卡片的高度
     */
    int cardHeight = 0;

    /**
     * 创建 popupWindow 内容
     *
     * @param context context
     */
    private void _createView(final Context context) {
        //判断发布哪种需求
        if (type.equals(QUICK_TASK)) {
            isQuick = true;
            rootView = LayoutInflater.from(context).inflate(R.layout.popup_quick_task, null);
        } else if (type.equals(COMMON_TASK)) {
            isQuick = false;
            rootView = LayoutInflater.from(context).inflate(R.layout.popup_task, null);
        }

        popupWindow = new PopupWindow(rootView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //设置为失去焦点 方便监听返回键的监听
        popupWindow.setFocusable(true);
        allowSend = false;
        // 如果想要popupWindow 遮挡住状态栏可以加上这句代码
        //popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);

        if (animatorProperty == null) {
            top = dip2px(context, 310);
            bottom = dip2px(context, 210);
            cardHeight = dip2px(context, 0);
            animatorProperty = new float[]{bottom, 60, -30, -20 - 10, 0};
        }

        initLayout(context);
    }

    private void checkContent() {
        if (!TextUtils.isEmpty(priceEdit.getText()) &&
                !TextUtils.isEmpty(desEdit.getText()) &&
                !TextUtils.isEmpty(locationEdit.getText())) {
            //当描述和价格以及地址都已输入时
            allowSend = true;
            ivBtn.setImageResource(R.drawable.ic_check_white_24dp);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 0f, -12f);
            objectAnimator.setDuration(200);
            objectAnimator.start();
        } else {
            allowSend = false;
            ivBtn.setImageResource(R.mipmap.add);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 0f, 135f);
            objectAnimator.setDuration(200);
            objectAnimator.start();
        }
    }

    /**
     * dp转化为px
     *
     * @param context  context
     * @param dipValue dp value
     * @return 转换之后的px值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 初始化 view
     */
    private void initLayout(final Context context) {
        tarLatitude = LocationUtil.getInstance().getLatitude();
        tarLongitude = LocationUtil.getInstance().getLongitude();
        chooseClassBtn = (Button) rootView.findViewById(R.id.choose_class_btn);
        rlClick = (RelativeLayout) rootView.findViewById(R.id.pop_rl_click);
        ivBtn = (ImageView) rootView.findViewById(R.id.pop_iv_img);
        cardView = (CardView) rootView.findViewById(R.id.card_back);
        transBack = rootView.findViewById(R.id.trans_back);
        desEdit = (EditText) rootView.findViewById(R.id.description_edittext);
        priceEdit = (EditText) rootView.findViewById(R.id.price_edit);
        auditSwitch = (Switch) rootView.findViewById(R.id.audit_switch);
        locationEdit = (EditText) rootView.findViewById(R.id.location_edit);
        goChooseLoc = (ImageView) rootView.findViewById(R.id.go_choose_loc);
        goChooseLoc.setOnClickListener(view -> {
            Intent intent = new Intent(context, LocChooseActivity.class);
            ((Activity) context).startActivityForResult(intent, REQUEST_FOR_LOC);
        });
        priceEdit.addTextChangedListener(textWatcher);
        desEdit.setFocusable(true);
        desEdit.requestFocus();
        desEdit.addTextChangedListener(textWatcher);
        locationEdit.setText(
                context.getSharedPreferences("location_sp", Context.MODE_PRIVATE)
                        .getString("location", "")
        );
        transBack.setOnClickListener(new MViewClick(0, context));

        chooseClassesDialog = new MaterialDialog.Builder(rootView.getContext())
                .title("请选择需求类型")
                .cancelable(false)
                .items(new String[]{"无类别"})
                .itemsCallback((dialog, itemView, position, text) -> {
                    taskClassId = position;
                    afterChoose(text.toString());
                })
                .build();
        if (classDtoList != null) {
            chooseClassesDialog.setItems(classes);
        }
        noticeChooseDialog = new MaterialDialog.Builder(rootView.getContext())
                .title("请选择类别")
                .cancelable(true)
                .content("选择类别之后可以帮您预估价格")
                .positiveText("好的").build();
        PriceDialog = new MaterialDialog.Builder(rootView.getContext())
                .title("价格预估")
                .cancelable(false)
                .positiveText("直接填入")
                .negativeText("我自己来")
                .onPositive((dialog, which) -> {
                    if (priceSection != null) {
                        priceEdit.setText("" + ((priceSection.getMax() + priceSection.getMin()) / 2));
                    }
                })
                .build();
        chooseClassBtn.setOnClickListener(view -> chooseClassesDialog.show());
        if (classDtoList == null) { //对大类列表进行初始化
            HttpUtil.getInstance().get(UrlHandler.getTaskClasses(),
                    new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {

                            classDtoList = new Gson()
                                    .fromJson(response, new TypeToken<List<TaskClassDto>>() {
                                    }.getType());
                            classes = new String[classDtoList.size()];
                            for (int i = 0; i < classes.length; i++) {
                                classes[i] = classDtoList.get(i).getName();
                            }
                            chooseClassesDialog.setItems(classes);

                        }
                    });

        }


        priceEdit.setOnFocusChangeListener((view, b) -> {
            if (!b) return;
            if (priceNoticed) return;
            //最正常的情况->弹出价格提醒弹窗
            if (classChoosed && priceSection != null) {
                PriceDialog.show();
                priceNoticed = true;
            }
            //用户未选择分类则提醒选择分类
            if (!classChoosed && !chooseNoticed) {
                noticeChooseDialog.show();
                chooseNoticed = true;
            }
        });
//        llTest1 = (LinearLayout) rootView.findViewById(R.id.test1);
//        llTest2 = (LinearLayout) rootView.findViewById(R.id.test2);
//        llTest3 = (LinearLayout) rootView.findViewById(R.id.test3);
//        llTest4 = (LinearLayout) rootView.findViewById(R.id.test4);
//        llTest5 = (LinearLayout) rootView.findViewById(R.id.test5);
//        llTest6 = (LinearLayout) rootView.findViewById(R.id.test6);
//        llTest7 = (LinearLayout) rootView.findViewById(R.id.test7);
//        llTest8 = (LinearLayout) rootView.findViewById(R.id.test8);

        rlClick.setOnClickListener(
                view -> {
                    //如果允许发送
                    if (allowSend) {
//                            new Timer().schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    final Snackbar snackbar = Snackbar
//                                            .make(showOnView,"发送"+type+"成功!",Snackbar.LENGTH_SHORT);
//                                    FrameLayout.LayoutParams  params =
//                                            (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
//                                    params.setMargins(0,0,0,dip2px(context,56f));
//                                    snackbar.getView().setLayoutParams(params);
//                                    snackbar.setAction("撤销", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            snackbar.dismiss();
//                                        }
//                                    });
//                                    snackbar.show();
//                                }
//                            },300);
                        RequestBody body = new FormBody.Builder()
                                .add("type", isQuick ? "0" : "1")
                                .add("publishDate", "" + new Date().getTime())
                                .add("endDate", "" + (new Date().getTime() + 2880000))
                                .add("content", desEdit.getText().toString())
                                .add("requireVerify", auditSwitch.isChecked() ? "1" : "0")
                                .add("location", locationEdit.getText().toString())
                                .add("latitude", "" + tarLatitude)
                                .add("longitude", "" + tarLongitude)
                                .add("price", priceEdit.getText().toString())
                                .build();

                        Log.i(TAG, "onClick: " + tarLatitude + "," + tarLongitude);
                        HttpUtil.getInstance().post(UrlHandler.postTask(), body,
                                new HttpCallbackListener() {
                                    @Override
                                    public void onFinish(String response) {
                                        ((Activity) context).runOnUiThread(() ->
                                                Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT)
                                                        .show());

                                    }
                                });
                    }
                    //关闭弹窗
                    new MViewClick(0, context).onClick(view);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (isQuick) {
                                Log.i(TAG, "onClick: 刷新quick");
                                QuickFragment.newInstance().reRun();
                            } else {
                                Log.i(TAG, "onClick: 刷新community");
                                CommunityFragment.newInstance().refreshAllTask();
                            }
                        }
                    }, 500);


                });

//        llTest1.setOnClickListener(new MViewClick(1, context));
//        llTest2.setOnClickListener(new MViewClick(2, context));
//        llTest3.setOnClickListener(new MViewClick(3, context));
//        llTest4.setOnClickListener(new MViewClick(4, context));
//        llTest5.setOnClickListener(new MViewClick(5, context));
//        llTest6.setOnClickListener(new MViewClick(6, context));
//        llTest7.setOnClickListener(new MViewClick(7, context));
//        llTest8.setOnClickListener(new MViewClick(8, context));

    }

    private void afterChoose(String taskClass) {
        classChoosed = true;
        int id = -1;
        for (TaskClassDto d : classDtoList) {
            if (d.getName().equals(taskClass)) {
                id = d.getId() + 1;
                break;
            }
        }
        Log.i(TAG, "afterChoose: classId=" + id);
        chooseClassBtn.setText(taskClass);

        //todo 提交关键词和任务类别 查询价格
        RequestBody body = new FormBody.Builder()
                .add("classId", String.valueOf(id))
                .add("date", String.valueOf(new Date().getTime()))
                .add("content", desEdit.getText().toString())
                .build();
        HttpUtil.getInstance().post(UrlHandler.getPriceEvaluation(),
                body, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        priceSection = new Gson().fromJson(response, PriceSection.class);
                        PriceDialog.setContent("为您预估出的价格为" + priceSection.getMin() + "-"
                                + priceSection.getMax() + ",如果您发布的任务为跑腿代购类，请在加上商品的价格。");
                        Log.i(TAG, "onFinish: 价格查询完毕" + priceSection.toString());
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 点击事件
     */
    private class MViewClick implements View.OnClickListener {

        public int index;
        public Context context;

        public MViewClick(int index, Context context) {
            this.index = index;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            if (index == 0) {
                //加号按钮点击之后的执行
                _rlClickAction();
            } else {
                showToast(context, "index=" + index);
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkContent();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkContent();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkContent();
        }
    };
    Toast toast = null;

    /**
     * 防止toast 多次被创建
     *
     * @param context context
     * @param str     str
     */
    private void showToast(Context context, String str) {
        if (toast == null) {
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }

    /**
     * 刚打开popupWindow 执行的动画
     */
    private void _openPopupWindowAction() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 0f, 135f);
        objectAnimator.setDuration(200);
        objectAnimator.start();
        _startAnimation(cardView, 430, animatorProperty);

//        _startAnimation(llTest1, 500, animatorProperty);
//        _startAnimation(llTest2, 430, animatorProperty);
//        _startAnimation(llTest3, 430, animatorProperty);
//        _startAnimation(llTest4, 500, animatorProperty);
//
//        _startAnimation(llTest5, 500, animatorProperty);
//        _startAnimation(llTest6, 430, animatorProperty);
//        _startAnimation(llTest7, 430, animatorProperty);
//        _startAnimation(llTest8, 500, animatorProperty);
    }


    /**
     * 关闭 popupWindow执行的动画
     */
    public void _rlClickAction() {
        if (ivBtn != null && rlClick != null) {

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 135f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.start();

            _closeAnimation(cardView, 300, top);
//            _closeAnimation(llTest1, 300, top);
//            _closeAnimation(llTest2, 200, top);
//            _closeAnimation(llTest3, 200, top);
//            _closeAnimation(llTest4, 300, top);
//            _closeAnimation(llTest5, 300, bottom);
//            _closeAnimation(llTest6, 200, bottom);
//            _closeAnimation(llTest7, 200, bottom);
//            _closeAnimation(llTest8, 300, bottom);

            rlClick.postDelayed(new Runnable() {
                @Override
                public void run() {
                    _close();
                }
            }, 300);

        }
    }

    public void refreshLocation(String location) {
        if (locationEdit != null && !popupWindow.isShowing()) {
            locationEdit.setText(location);
        }
    }

    public void refreshLocation(double latitude, double longitude) {
        tarLongitude = longitude;
        tarLatitude = latitude;
    }

    /**
     * 弹起 popupWindow
     *
     * @param context context
     * @param parent  parent
     */
    public void _show(Context context, View parent) {
        _show(context, parent, QUICK_TASK);
    }

    public void _show(Context context, View parent, String type) {
        this.type = type;
        _createView(context);
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
            _openPopupWindowAction();
        }
    }

    /**
     * 关闭popupWindow
     */

    public void _close() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * @return popupWindow 是否显示了
     */
    public boolean _isShowing() {
        if (popupWindow == null) {
            return false;
        } else {
            return popupWindow.isShowing();
        }
    }

    /**
     * 关闭 popupWindow 时的动画
     *
     * @param view     mView
     * @param duration 动画执行时长
     * @param next     平移量
     */
    private void _closeAnimation(View view, int duration, int next) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0f, next);
        anim.setDuration(duration);
        anim.start();
    }

    /**
     * 启动动画
     *
     * @param view     view
     * @param duration 执行时长
     * @param distance 执行的轨迹数组
     */
    private void _startAnimation(View view, int duration, float[] distance) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", distance);
        anim.setDuration(duration);
        anim.start();
    }


}
