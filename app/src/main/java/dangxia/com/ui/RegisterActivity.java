package dangxia.com.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import dangxia.com.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EventHandler eventHandler;

    @BindView(R.id.account_edit)
    EditText phoneEdit;

    @BindView(R.id.password_edit)
    EditText pwdEdit;

    @BindView(R.id.v_code_edit)
    EditText vCodeEdit;

    @BindView(R.id.send_vcode_btn)
    Button sendVcodeBtn;

    @BindView(R.id.register_btn)
    Button registerBtn;

    @BindView(R.id.back_btn)
    View backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        phoneEdit.setFocusable(true);
        phoneEdit.requestFocus();
        registerBtn.setOnClickListener(this);
        sendVcodeBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    final String msg = throwable.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        // 这里是验证成功的回调，可以处理验证成功后您自己的逻辑，需要注意的是这里不是主线程
                        Log.i("1234", "获取成功！");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "发送成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        Log.i("1234", "验证成功！");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "验证成功！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        });
                    }
                }
            }
        };

        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }


    @Override
    public void onClick(View view) {
        String phone = phoneEdit.getText().toString().trim();
        switch (view.getId()) {
            case R.id.send_vcode_btn:
                if (phone.length() != 11) {
                    Toast.makeText(this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.getVerificationCode("86", phone);
                }
                break;
            case R.id.register_btn:
                String vCode = vCodeEdit.getText().toString().trim();
                SMSSDK.submitVerificationCode("86", phone, vCode);
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }
}
