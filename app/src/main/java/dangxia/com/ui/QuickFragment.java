package dangxia.com.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.github.library.bubbleview.BubbleTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dangxia.com.R;
import dangxia.com.entity.QuickTaskBean;
import dangxia.com.view.PopupMenuUtil;

/**
 * "一秒火速"页面
 * Created by zhuang_ge on 2017/11/17.
 */

public class QuickFragment extends Fragment{

    private static QuickFragment fragment;

    private SharedPreferences locationSp;

    //地图控件
    private TextureMapView mapView;

    //操作地图的封装类
    private BaiduMap baiduMap;

    public LocationClient mLocationClient;


    private List<QuickTaskBean> taskBeans = new ArrayList<>();

    private Map<Marker,QuickTaskBean> map = new HashMap<>();
    private boolean isFirstLocate = true;

    private CardView bottomLabel;
    private ImageView closeBtn;
    public QuickFragment() {
    }

    public static QuickFragment newInstance() {
        if(fragment == null) {
            fragment = new QuickFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quick,null);

        mapView = (TextureMapView) v.findViewById(R.id.bmapView);
        bottomLabel = (CardView) v.findViewById(R.id.bottom_label);
        closeBtn = (ImageView) v.findViewById(R.id.close_btn);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
        setLocationRefresh();
        initLables();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                QuickTaskBean taskBean = map.get(marker);
                if(taskBean != null ) {
//                    Toast.makeText(getContext(),
//                            "点击了"+taskBean.getContent(),Toast.LENGTH_SHORT).show();
                    bottomLabel.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomLabel.setVisibility(View.INVISIBLE);
            }
        });
        bottomLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),TaskDetailActivity.class));
            }
        });
        locationSp = getActivity().getSharedPreferences("location_sp", Context.MODE_PRIVATE);
        return v;
    }

    public void reRun() {
        mLocationClient.stop();
        mLocationClient.start();
        setLocationRefresh();
        navigateTo(31.248493,120.608842);
        initLables();
    }

    /**
     * 请求网络数据，更新标签
     */
    private void initLables() {
        baiduMap.clear();
        // TODO: 2017/11/20 将测试数据替换为网络请求
        taskBeans.add(new QuickTaskBean(1,1,100.0f,"求王者带飞","",
                31.248493,120.608842));
        taskBeans.add(new QuickTaskBean(2,1,5.0f,"来个人唠嗑","",
                31.249493,120.608942));
        for(QuickTaskBean taskBean : taskBeans) {
            addMarker(taskBean);
        }
    }

    private void addMarker(QuickTaskBean taskBean) {
        LatLng point = new LatLng(taskBean.getLatitude(),taskBean.getLongtitude());
        BubbleTextView textView = (BubbleTextView) LayoutInflater
                .from(getContext()).inflate(R.layout.new_label,null);
        textView.setText("￥"+taskBean.getPrice()+" "+taskBean.getContent());
        textView.setAlpha(0.87f);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(textView);

        OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);

        Marker marker = (Marker) baiduMap.addOverlay(options);
        //将生成的maker保存
        map.put(marker,taskBean);
    }

    //每五秒钟更新自己的位置
    private void setLocationRefresh() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);//需要获取详细的地址信息
        option.setAddrType("all");
        option.setIsNeedLocationDescribe(true);
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
    }

    private void navigateTo(double latitude, double longitude) {
        if (isFirstLocate) {
//            Toast.makeText(getContext(), "nav to 纬度" + latitude + " 经度" + longitude, Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(latitude, longitude);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(19f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        //生成标注自己位置的小圆点
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(latitude);
        locationBuilder.longitude(longitude);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                //将地图定位至当前位置
                locationSp.edit().putString("latitude",""+bdLocation.getLatitude()).apply();
                locationSp.edit().putString("longitude",""+bdLocation.getLongitude()).apply();
                StringBuilder simplePositon = new StringBuilder();
                simplePositon.append(bdLocation.getStreet())
                        .append(bdLocation.getStreetNumber());
                if(bdLocation.getAddrStr() != null) {
                    locationSp.edit().putString("location", simplePositon.toString()).apply();
                    PopupMenuUtil.getInstance().refreshLocation(simplePositon.toString());
                } else {
                    PopupMenuUtil.getInstance().refreshLocation("");

                }
//                Toast.makeText(getContext(),simplePositon.toString(),Toast.LENGTH_LONG).show();
                navigateTo(31.248493,120.608842);
//                navigateTo(bdLocation.getLatitude(),bdLocation.getLongitude());
            }
        }
    }

}
