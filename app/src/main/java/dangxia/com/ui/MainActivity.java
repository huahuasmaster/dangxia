package dangxia.com.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;
import java.util.List;

import dangxia.com.R;
import dangxia.com.entity.QuickTaskBean;
import dangxia.com.entity.TabEntity;
import dangxia.com.view.PopupMenuUtil;

public class MainActivity extends AppCompatActivity {

    //左边的两个页面
    private ArrayList<Fragment> fragments = new ArrayList<>();
    //页面的标题
    private String[] titles = {"圈子","火速","","消息","我的"};

    //左边页面的两套图标
    private int[] unselectedIcons = {R.mipmap.community,R.mipmap.quick,R.mipmap.quick,
            R.mipmap.message,R.mipmap.mine};
    private int[] selectedIcons = {R.mipmap.community_selected,R.mipmap.quick_selected,R.mipmap.quick_selected,
            R.mipmap.message_selected,R.mipmap.mine_selected};

    private CommonTabLayout tab;

    private ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();

    private RelativeLayout rlClick;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        initTab();


        //检查权限，动态申请未给予的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            initRl();
        }




    }

    private void initTab(){
        //装填fragment
        fragments.add(CommunityFragment.newInstance());
        fragments.add(QuickFragment.newInstance());
        fragments.add(EmptyFragment.newInstance());
        fragments.add(MessageFragment.newInstance());
        fragments.add(MineFragment.newInstance());

        //装填底部tab数据源
        for(int i = 0;i < titles.length;i++) {
            tabEntities.add(new TabEntity(titles[i],
                    selectedIcons[i],
                    unselectedIcons[i]));
        }

        //初始化tabLayout,设置图标大小与字体大小
        tab = (CommonTabLayout) findViewById(R.id.bottom_left);

        tab.setTabData(tabEntities,this,R.id.fl_change, fragments);
        tab.setIconGravity(Gravity.TOP);
        tab.setTextsize(12.0F);
        tab.setIconHeight(25.0F);
        tab.setIconMargin(3F);
        tab.setIconWidth(25.0F);
//        tab.setCurrentTab(1);

    }
    private void initRl() {
        ivImage = (ImageView) findViewById(R.id.iv_img);
        rlClick = (RelativeLayout) findViewById(R.id.rl_click);
        rlClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenuUtil.setShowOnView(ivImage);
                if(tab.getCurrentTab() > 0) {
                    PopupMenuUtil.getInstance()._show(MainActivity.this, ivImage);
                } else {
                    PopupMenuUtil.getInstance()._show(MainActivity.this, ivImage,PopupMenuUtil.COMMON_TASK);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 当popupWindow 正在展示的时候 按下返回键 关闭popupWindow 否则关闭activity
        if (PopupMenuUtil.getInstance()._isShowing()) {
            PopupMenuUtil.getInstance()._rlClickAction();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
//                    initTab();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//                            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(LaunchIntent);
//                        }
//                    }, 1000);// 1秒钟后重启应用
                    QuickFragment.newInstance().reRun();
                    initRl();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


}
