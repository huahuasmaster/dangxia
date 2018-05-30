package dangxia.com.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dangxia.com.R;
import dangxia.com.adapter.TaskItemAdapter;
import dangxia.com.entity.TaskDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;

public class HistoryActivity extends AppCompatActivity {

    @OnClick(R.id.back_btn)
    void back() {
        finish();
    }

    @BindView(R.id.title_history)
    TextView title;

    @BindView(R.id.rv_history)
    RecyclerView recyclerView;

    private TaskItemAdapter adapter;
    private List<TaskDto> taskDtoList;
    private boolean showProvidedServices;// 是否展现提供过的服务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_of_history);
        ButterKnife.bind(this);
        showProvidedServices = getIntent().getBooleanExtra("showProvidedServices", true);
        title.setText(showProvidedServices ? "提供过的服务" : "被完成的需求");
        String url = showProvidedServices ? UrlHandler.getServed() : UrlHandler.getBeServed();
        HttpUtil.getInstance().get(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                taskDtoList = new Gson().fromJson(response, new TypeToken<List<TaskDto>>() {
                }.getType());
                if (taskDtoList != null) {
                    runOnUiThread(() -> {
                        if (adapter == null) {
                            adapter = new TaskItemAdapter();
                            adapter.setTaskDtos(taskDtoList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.setTaskDtos(taskDtoList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                Logger.e("获取历史数据失败", e.getMessage());
            }
        });
    }
}
