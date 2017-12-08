package dangxia.com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dangxia.com.R;
import dangxia.com.adapter.MsgPreviewAdapter;
import dangxia.com.adapter.listener.SwipeItemCallbackListener;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class MessageFragment extends Fragment{

    private static MessageFragment fragment;

    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private MsgPreviewAdapter adapter = new MsgPreviewAdapter();
    private SwipeItemCallbackListener listener;

    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        if(fragment == null) {
            fragment = new MessageFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message,null);
        v.findViewById(R.id.msg_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getContext(),ChatActivity.class));
            }
        });
        return v;
    }
}
