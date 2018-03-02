package dangxia.com.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import dangxia.com.R;
import dangxia.com.entity.OrderDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class OrderDetailActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    MaterialDialog confirmDialog;
    MaterialDialog waitDialog;

    @BindView(R.id.content_order)
    TextView content;

    @BindView(R.id.price_order)
    TextView price;

    @BindView(R.id.date_order)
    TextView date;

    @BindView(R.id.executor_order)
    TextView executorName;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.add_order)
    TextView add;

    private OrderDto orderDto;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        taskId = getIntent().getIntExtra("taskId", -1);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        waitDialog = new MaterialDialog.Builder(this)
                .title("查询中")
                .progress(true, 0)
                .autoDismiss(false)
                .build();
        if (taskId == -1) {
            waitDialog.dismiss();
            Toast.makeText(this, "不存在的任务，请重试", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        confirmDialog = new MaterialDialog.Builder(this)
                .title("确认需求已完成？")
                .content("欠款将直接打入对方账户。")
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String url = UrlHandler.finishOrder(orderDto.getId());
                        RequestBody body = new FormBody.Builder()
                                .add("date", String.valueOf(new Date().getTime()))
                                .build();
                        HttpUtil.getInstance().post(url, body, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                if (response.equals("1")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(fab, "交易成功！", Snackbar.LENGTH_SHORT).show();
                                            fab.setEnabled(false);
                                            title.setText("订单详情（已完成）");
                                        }
                                    });

                                }
                            }
                        });
                    }
                })
                .build();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.show();
            }
        });
        initViews();
    }

    private void initViews() {
        waitDialog.show();
        String url = UrlHandler.getOrderByTask(taskId);
        HttpUtil.getInstance().get(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                orderDto = new Gson().fromJson(response, OrderDto.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (orderDto != null) {
                            content.setText(orderDto.getTaskDto().getContent());
                            price.setText("￥" + orderDto.getTaskDto().getPrice());
                            executorName.setText(orderDto.getExecutorName());
                            date.setText(orderDto.getOrderDate());
                            add.setText(orderDto.getTaskDto().getLocation());
                            if (orderDto.getStatus() > 0) {
                                fab.setVisibility(View.INVISIBLE);
                                title.setText("订单详情（已完成）");
                            }
                            if (waitDialog.isShowing()) {
                                waitDialog.dismiss();
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
            }
        });
    }
}
