package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import dangxia.com.R;
import dangxia.com.adapter.MsgChatItemAdapter;
import dangxia.com.entity.ConversationDto;
import dangxia.com.entity.TaskDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class TaskDetailActivity extends AppCompatActivity {

    private MaterialDialog confirmDialog;

    @BindView(R.id.order_take_btn)
    Button orderTakeBtn;

    @BindView(R.id.name_detail)
    TextView name;

    @BindView(R.id.content_detail)
    TextView content;

    @BindView(R.id.price_detail)
    TextView price;

    @BindView(R.id.mapview_task_detail)
    TextureMapView mapView;

    private TaskDto mTask;

    public LocationClient mLocationClient;
    private BaiduMap baiduMap;

    public static final int NO_RELATIONSHIP = 0;//自己只是吃瓜群众，跟这个任务暂时并无关系
    public static final int ACEEPTED = 1;//自己承接下来的任务
    public static final int PUBLISHED = 2;//自己发布的任务

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        mTask = (TaskDto) getIntent().getSerializableExtra("task_dto");
        int taskRelation = getIntent().getIntExtra("task_relation", 0);
        if (taskRelation != NO_RELATIONSHIP) {
            orderTakeBtn.setEnabled(false);
        }
        if (mTask == null) {
            Toast.makeText(this, "此任务已过时。", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.i("taskdetail", "onCreate: " + mTask.toString());

        findViewById(R.id.back_btn).setOnClickListener(view -> finish());
        //直接接单按钮
        confirmDialog = new MaterialDialog.Builder(this)
                .title("确认接单？")
                .content("请确保您已认真阅读发布者需求。")
                .negativeText("取消")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    String url = UrlHandler.takeOrder();
                    RequestBody body = new FormBody.Builder()
                            .add("senderId", String.valueOf(UrlHandler.getUserId()))
                            .add("taskId", String.valueOf(mTask.getId()))
                            .build();
                    HttpUtil.getInstance().post(url, body, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            runOnUiThread(() -> {
                                Toast.makeText(TaskDetailActivity.this,
                                        "接单成功！", Toast.LENGTH_SHORT).show();
                                goChat(Integer.parseInt(response));

                            });
                        }
                    });
                })
                .build();

        orderTakeBtn.setOnClickListener(view -> {
            if (mTask.getRequireVerify() == 0) {
                confirmDialog.show();
            } else {
                confirmDialog = new MaterialDialog.Builder(TaskDetailActivity.this)
                        .title("确认接单？")
                        .content("您的个人信息将展示给发布者，供其审核。")
                        .negativeText("取消")
                        .positiveText("确认")
                        .onPositive((dialog, which) -> {
                            RequestBody body = new FormBody.Builder().build();
                            String url = UrlHandler.initConversation(mTask.getId());
                            HttpUtil.getInstance().put(url, body, new HttpCallbackListener() {
                                @Override
                                public void onFinish(String response) {
                                    goChat(Integer.parseInt(response));
                                }
                            });
                        })
                        .build();
                confirmDialog.show();
            }
        });
        price.setText("￥" + mTask.getPrice());
        name.setText(mTask.getPublisherName());
        content.setText(mTask.getContent());

        //配置组件
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());
        LatLng ll = new LatLng(mTask.getLatitude(), mTask.getLongitude());

        //移动地图的中心点
        Log.i("task_detail", "onReceiveLocation: 地图的位置移动到：" + ll.toString());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(19f);
        baiduMap.animateMapStatus(update);

        //添加一个marker
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        MarkerOptions options = new MarkerOptions().position(ll).icon(bdGround).draggable(false);
        options.animateType(MarkerOptions.MarkerAnimateType.drop);
        final Marker mMarker = (Marker) baiduMap.addOverlay(options);
    }

    private void goChat(final int conId) {

        // TODO: 2018/2/26 修改接单按钮样式
        HttpUtil.getInstance().get(UrlHandler.getCon(conId),
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        ConversationDto dto = new Gson().fromJson(response, ConversationDto.class);
                        Intent intent = new Intent(TaskDetailActivity.this, ChatActivity.class);
                        intent.putExtra("con_id", conId);
                        Log.i("detail", "onFinish: " + dto.toString());
                        intent.putExtra("con", dto);
                        startActivity(intent);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
