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

    private TaskDto mTask;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);

        mTask = (TaskDto) getIntent().getSerializableExtra("task_dto");
        if (mTask == null) {
            Toast.makeText(this, "此任务已过时。", Toast.LENGTH_SHORT).show();
            finish();
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //直接接单按钮
        confirmDialog = new MaterialDialog.Builder(this)
                .title("确认接单？")
                .content("请确保您已认真阅读发布者需求。")
                .negativeText("取消")
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = UrlHandler.takeOrder();
                        RequestBody body = new FormBody.Builder()
                                .add("senderId", String.valueOf(UrlHandler.getUserId()))
                                .add("taskId", String.valueOf(mTask.getId()))
                                .build();
                        HttpUtil.getInstance().post(url, body, new HttpCallbackListener() {
                            @Override
                            public void onFinish(final String response) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TaskDetailActivity.this,
                                                "接单成功！", Toast.LENGTH_SHORT).show();
                                        goChat(Integer.parseInt(response));

                                    }
                                });
                            }
                        });
                    }
                })
                .build();

        orderTakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTask.getRequireVerify() == 0) {
                    confirmDialog.show();
                } else {
                    confirmDialog = new MaterialDialog.Builder(TaskDetailActivity.this)
                            .title("确认接单？")
                            .content("您的个人信息将展示给发布者，供其审核。")
                            .negativeText("取消")
                            .positiveText("确认")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    RequestBody body = new FormBody.Builder().build();
                                    String url = UrlHandler.initConversation(mTask.getId());
                                    HttpUtil.getInstance().put(url, body, new HttpCallbackListener() {
                                        @Override
                                        public void onFinish(String response) {
                                            goChat(Integer.parseInt(response));
                                        }
                                    });
                                }
                            })
                            .build();
                    confirmDialog.show();
                }
            }
        });
        price.setText("￥" + mTask.getPrice());
        name.setText(mTask.getPublisherName());
        content.setText(mTask.getContent());

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
}
