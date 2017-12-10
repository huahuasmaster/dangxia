package dangxia.com.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import dangxia.com.R;
import dangxia.com.dto.UserDto;
import dangxia.com.utils.HttpCallbackListener;
import dangxia.com.utils.HttpUtil;
import dangxia.com.utils.UrlHandler;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.register_btn)
    Button loginBtn;

    @BindView(R.id.go_register_btn)
    Button goRegisterBtn;

    @BindView(R.id.account_edit)
    EditText phoneEdit;

    @BindView(R.id.password_edit)
    EditText pwdEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        phoneEdit.setFocusable(true);
        phoneEdit.requestFocus();
        goRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone = phoneEdit.getText().toString().trim();
                final String password = pwdEdit.getText().toString().trim();
                RequestBody body = new FormBody.Builder()
                        .add("phone", phone)
                        .add("password", password)
                        .build();
                String url = UrlHandler.getLoginUrl();
                HttpUtil.getInstance().post(url, body, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        UserDto userDto = new Gson().fromJson(response, UserDto.class);
                        if (userDto == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(phoneEdit, "登录失败，请检查。", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            LoginActivity.this.getSharedPreferences("login_data", Context.MODE_PRIVATE)
                                    .edit().putLong("phone", userDto.getPhone())
                                    .putString("name", userDto.getName()).apply();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(phoneEdit, "登录失败，请检查。", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });

        //检查权限，动态申请未给予的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_SMS);
        }
//        if(ContextCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.RECEIVE_SMS) !=PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.RECEIVE_SMS);
//        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
