package dangxia.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aitsuki.swipe.SwipeItemLayout;

import java.util.List;

import dangxia.com.R;
import dangxia.com.adapter.listener.SwipeItemCallbackListener;
import dangxia.com.entity.ConversationDto;
import dangxia.com.utils.http.UrlHandler;

/**
 * Created by zhuang_ge on 2017/11/26.
 */

public class ConversationAdapter extends RecyclerView.Adapter {
    private List<ConversationDto> dtos;
    private SwipeItemCallbackListener listener;

    public List<ConversationDto> getDtos() {
        return dtos;
    }

    public void setDtos(List<ConversationDto> dtos) {
        this.dtos = dtos;
    }

    public SwipeItemCallbackListener getListener() {
        return listener;
    }

    public void setListener(SwipeItemCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_preview_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ConversationDto dto = dtos.get(position);
        MyHolder myHolder = (MyHolder) holder;
        myHolder.content.setText(dto.getLastWords());
        myHolder.date.setText(dto.getLastDate());
        int iconId;
        // TODO: 2018/4/6 转化成真正的头像加载 
        if (UrlHandler.getUserId() == 2) {
            iconId = R.mipmap.doge2;
        } else {
            iconId = R.mipmap.doge;
        }
        myHolder.icon.setImageResource(iconId);
        myHolder.title.setText(dto.getInitiatorId() == UrlHandler.getUserId() ?
                dto.getPublisherName() : dto.getInitiatorName() + "(申请接单)");
        myHolder.wholeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMain(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dtos.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView date;
        ImageView icon;
        TextView title;
        SwipeItemLayout wholeView;

        public MyHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content_msg);
            title = (TextView) itemView.findViewById(R.id.title_msg);
            date = (TextView) itemView.findViewById(R.id.date);
            icon = (ImageView) itemView.findViewById(R.id.icon_msg);
            wholeView = (SwipeItemLayout) itemView.findViewById(R.id.whole_view);
        }
    }
}
