package dangxia.com.ui;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dangxia.com.R;
import dangxia.com.utils.http.UrlHandler;


public class IpConfigActivity extends AppCompatActivity {

    @BindView(R.id.new_ip)
    EditText newIpEdit;

    @BindView(R.id.switch_oncloud)
    Switch aSwitch;

    @OnClick(R.id.check)
    void onClick() {
        UrlHandler.setLocalIp(newIpEdit.getText().toString());

        Toast.makeText(IpConfigActivity.this, "新ip:" + UrlHandler.getIp(), Toast.LENGTH_SHORT)
                .show();
        finish();
    }

    @OnCheckedChanged(R.id.switch_oncloud)
    void onChecked(CompoundButton compoundButton, boolean b) {
        UrlHandler.setOnCloud(b);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_config);
        ButterKnife.bind(this);
        newIpEdit.setText(UrlHandler.getLocalIp());
        aSwitch.setChecked(UrlHandler.isOnCloud());
    }
}
