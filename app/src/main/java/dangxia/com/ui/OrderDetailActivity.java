package dangxia.com.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import dangxia.com.R;

public class OrderDetailActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private MaterialDialog confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        confirmDialog = new MaterialDialog.Builder(this)
                .title("确认需求已完成？")
                .content("欠款将直接打入对方账户。")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Snackbar.make(fab,"交易成功！",Snackbar.LENGTH_SHORT).show();
                    }
                })
                .build();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            confirmDialog.show();
            }
        });
    }
}
