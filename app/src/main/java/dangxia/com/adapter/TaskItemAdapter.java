package dangxia.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dangxia.com.R;
import dangxia.com.adapter.listener.TaskItemCallBackListener;
import dangxia.com.entity.TaskDto;
import dangxia.com.utils.http.UrlHandler;
import dangxia.com.utils.location.DistanceUtil;
import dangxia.com.utils.location.LocationUtil;

public class TaskItemAdapter extends RecyclerView.Adapter {
    private List<TaskDto> taskDtos;
    private Context context;
    private TaskItemCallBackListener listener;

    public TaskItemCallBackListener getListener() {
        return listener;
    }

    public void setListener(TaskItemCallBackListener listener) {
        this.listener = listener;
    }

    public List<TaskDto> getTaskDtos() {
        return taskDtos;
    }

    public void setTaskDtos(List<TaskDto> taskDtos) {
        this.taskDtos = taskDtos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_item, viewGroup, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        MyHolder myHolder = (MyHolder) viewHolder;
        final TaskDto dto = taskDtos.get(i);
        myHolder.content.setText(dto.getContent());
        myHolder.distance.setText("" + DistanceUtil.km(dto.getLatitude(), dto.getLongitude(),
                LocationUtil.getInstance().getLatitude(), LocationUtil.getInstance().getLongitude()) + "km");
        // TODO: 2018/2/21 为头像新增点击事件
        myHolder.icon.setImageResource(dto.getPublisher() == 2 ?
                R.mipmap.doge : R.mipmap.doge2);

        myHolder.name.setText(dto.getPublisherName());
        myHolder.price.setText("￥" + dto.getPrice());
        myHolder.time.setText(dto.getPublishDate());
        if (listener != null) {
            myHolder.wholeView.setOnClickListener(view -> listener.onMain(dto));
            myHolder.icon.setOnClickListener(view -> listener.onIcon(i));
        }

    }

    @Override
    public int getItemCount() {
        return taskDtos.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.content)
        TextView content;

        @BindView(R.id.time)
        TextView time;

        @BindView(R.id.distance)
        TextView distance;

        @BindView(R.id.whole_view)
        CardView wholeView;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            distance = itemView.findViewById(R.id.distance);
            wholeView = itemView.findViewById(R.id.whole_view);
        }
    }
}
