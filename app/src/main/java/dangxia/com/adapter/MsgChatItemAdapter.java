package dangxia.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

import dangxia.com.R;
import dangxia.com.entity.MessageBean;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhuang_ge on 2017/11/26.
 */

public class MsgChatItemAdapter extends RecyclerView.Adapter{

    private List<MessageBean> msgList;

    public List<MessageBean> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MessageBean> msgList) {
        this.msgList = msgList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case MessageBean.TYPE_ACCEPT :
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_accept,parent,false);
                return new MsgHolder(v);
            case MessageBean.TYPE_SEND :
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_send,parent,false);
                return new MsgHolder(v);
            default:return null;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MsgHolder msgHolder = (MsgHolder)holder;
        MessageBean bean = msgList.get(position);
        //填充信息内容
        msgHolder.contentTxt.setText(bean.getContent());
        //填充时间
        msgHolder.dateTxt.setText(bean.getTime());
        //填充头像
        // TODO: 2017/11/26 动态适配头像
        if(bean.getType() == MessageBean.TYPE_SEND) {
            msgHolder.headerImg.setImageResource(R.mipmap.doge);
        } else {
            msgHolder.headerImg.setImageResource(R.mipmap.doge2);
            Log.i("123", "");
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).getType();
    }

    private class MsgHolder extends RecyclerView.ViewHolder {
        TextView dateTxt;
        BubbleTextView contentTxt;
        CircleImageView headerImg;

        public MsgHolder(View itemView) {
            super(itemView);

            dateTxt = (TextView) itemView.findViewById(R.id.chat_item_date);
            contentTxt = (BubbleTextView) itemView.findViewById(R.id.chat_item_content_image);
            headerImg = (CircleImageView) itemView.findViewById(R.id.chat_item_header);
        }
    }

}
