package dangxia.com.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import dangxia.com.R;

public class TaskDetailActivity extends AppCompatActivity {

    private View backBtn;

    private MaterialDialog confirmDialog;

    private Button orderTakeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        backBtn = findViewById(R.id.back_btn);
        confirmDialog = new MaterialDialog.Builder(this)
                .title("确认接单？")
                .content("请确保您已认真阅读发布者需求。")
                .negativeText("取消")
                .positiveText("确认")
                .build();
        orderTakeBtn = (Button) findViewById(R.id.order_take_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        orderTakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.show();
            }
        });
        orderTakeBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                confirmDialog = new MaterialDialog.Builder(TaskDetailActivity.this)
                        .title("确认接单？")
                        .content("您的个人信息将展示给发布者，供其审核。")
                        .negativeText("取消")
                        .positiveText("确认")
                        .build();
                confirmDialog.show();
                return false;
            }
        });
    }
}
