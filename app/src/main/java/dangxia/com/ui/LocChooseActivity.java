package dangxia.com.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import dangxia.com.R;
import dangxia.com.utils.location.LocationUtil;
import dangxia.com.view.PopupMenuUtil;

public class LocChooseActivity extends AppCompatActivity {

    String TAG = "locchoose";


    //地图控件
    private TextureMapView mapView;

    //操作地图的封装类
    private BaiduMap baiduMap;

    private FloatingActionButton fab;

    public LocationClient mLocationClient;

    private boolean isFirstLocate = true;
    private double targetLatitude;
    private double targetLongtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_choose);
        fab = (FloatingActionButton) findViewById(R.id.confirm_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("latitude", targetLatitude);
                intent.putExtra("longitude", targetLongtitude);
                Log.i(TAG, "onClick: 返回了新坐标:" + targetLatitude + "," + targetLongtitude);
                setResult(0x13, intent);
                finish();
            }
        });
        mapView = (TextureMapView) findViewById(R.id.mapview);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());

        //初始化坐标数据
        targetLatitude = LocationUtil.getInstance().getLatitude();
        targetLongtitude = LocationUtil.getInstance().getLongitude();
        LatLng ll = new LatLng(targetLatitude, targetLongtitude);

        //移动地图的中心点
        Log.i(TAG, "onReceiveLocation: 地图的位置移动到：" + ll.toString());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(19f);
        baiduMap.animateMapStatus(update);

        //添加一个可拖动的marker
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        MarkerOptions options = new MarkerOptions().position(ll).icon(bdGround).draggable(true);
        options.animateType(MarkerOptions.MarkerAnimateType.drop);
        final Marker mMarker = (Marker) baiduMap.addOverlay(options);
        Log.i(TAG, "navigateTo: 添加marker成功");
        baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (marker == mMarker) {
                    LatLng latLng = marker.getPosition();
                    targetLatitude = latLng.latitude;
                    targetLongtitude = latLng.longitude;
                    Log.i(TAG, "onMarkerDragEnd: 更新位置" + latLng.toString());
                } else {
                    Log.i(TAG, "onMarkerDragEnd: 不是指定的marker");
                }
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

        final Snackbar snackbar = Snackbar.make(fab, "按住并拖拽小红点以选取任务执行地点",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("知道了", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
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

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("确定退出么")
                .content("坐标将不会保存")
                .positiveText("继续退出")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                }).show();
    }
}
