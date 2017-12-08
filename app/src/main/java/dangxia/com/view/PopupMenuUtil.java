package dangxia.com.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import dangxia.com.R;


/**
 * Created by mj
 * on 2016/10/28.
 */
public class PopupMenuUtil {

    private static final String TAG = "PopupMenuUtil";

    public static final String QUICK_TASK = "快速需求";
    public static final String COMMON_TASK = "长期需求";
    private String type;

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
    private boolean allowSend = false;
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
        if(type.equals(QUICK_TASK)) {
            rootView = LayoutInflater.from(context).inflate(R.layout.popup_quick_task, null);
        } else if(type.equals(COMMON_TASK)) {
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
        rlClick = (RelativeLayout) rootView.findViewById(R.id.pop_rl_click);
        ivBtn = (ImageView) rootView.findViewById(R.id.pop_iv_img);
        cardView = (CardView) rootView.findViewById(R.id.card_back);
        transBack = rootView.findViewById(R.id.trans_back);
        desEdit = (EditText) rootView.findViewById(R.id.description_edittext);
        priceEdit = (EditText) rootView.findViewById(R.id.price_edit);
        auditSwitch = (Switch) rootView.findViewById(R.id.audit_switch);
        locationEdit = (EditText) rootView.findViewById(R.id.location_edit);
        priceEdit.addTextChangedListener(textWatcher);
        desEdit.setFocusable(true);
        desEdit.requestFocus();
        desEdit.addTextChangedListener(textWatcher);
        locationEdit.setText(
                context.getSharedPreferences("location_sp",Context.MODE_PRIVATE)
                .getString("location","")
        );
        transBack.setOnClickListener(new MViewClick(0, context));
//        llTest1 = (LinearLayout) rootView.findViewById(R.id.test1);
//        llTest2 = (LinearLayout) rootView.findViewById(R.id.test2);
//        llTest3 = (LinearLayout) rootView.findViewById(R.id.test3);
//        llTest4 = (LinearLayout) rootView.findViewById(R.id.test4);
//        llTest5 = (LinearLayout) rootView.findViewById(R.id.test5);
//        llTest6 = (LinearLayout) rootView.findViewById(R.id.test6);
//        llTest7 = (LinearLayout) rootView.findViewById(R.id.test7);
//        llTest8 = (LinearLayout) rootView.findViewById(R.id.test8);

        rlClick.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //如果允许发送
                        if(allowSend) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    final Snackbar snackbar = Snackbar
                                            .make(showOnView,"发送"+type+"成功!",Snackbar.LENGTH_SHORT);
                                    FrameLayout.LayoutParams  params =
                                            (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                                    params.setMargins(0,0,0,dip2px(context,56f));
                                    snackbar.getView().setLayoutParams(params);
                                    snackbar.setAction("撤销", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
                            },300);
                        }
                        new MViewClick(0,context).onClick(view);

                    }
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
        if(locationEdit !=null && !popupWindow.isShowing()) {
            locationEdit.setText(location);
        } 
    }

    /**
     * 弹起 popupWindow
     *
     * @param context context
     * @param parent  parent
     */
    public void _show(Context context, View parent) {
        _show(context,parent,QUICK_TASK);
    }

    public void _show(Context context,View parent,String type) {
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
