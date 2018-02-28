package dangxia.com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import dangxia.com.R;
import dangxia.com.adapter.ConversationAdapter;
import dangxia.com.adapter.listener.SwipeItemCallbackListener;
import dangxia.com.entity.ConversationDto;
import dangxia.com.entity.MessageDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;

/**
 * Created by zhuang_ge on 2017/11/17.
 */

public class MessageFragment extends Fragment{

    private static MessageFragment fragment;

    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private ConversationAdapter adapter = new ConversationAdapter();
    private SwipeItemCallbackListener listener;
    private List<ConversationDto> dtos = new ArrayList<>();

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
//        v.findViewById(R.id.msg_test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().startActivity(new Intent(getContext(),ChatActivity.class));
//            }
//        });
        rv = (RecyclerView) v.findViewById(R.id.smr);
        srl = (SwipeRefreshLayout) v.findViewById(R.id.srl);
        adapter = new ConversationAdapter();
        listener = new SwipeItemCallbackListener() {
            @Override
            public void onDelete(int position) {

            }

            @Override
            public void onEdit(int position) {

            }

            @Override
            public void onMain(int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("con_id", adapter.getDtos().get(position).getId());
                intent.putExtra("con", adapter.getDtos().get(position));
                getActivity().startActivity(intent);
            }
        };
        adapter.setListener(listener);
        adapter.setDtos(dtos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initMsgList();
            }
        });
        initMsgList();
        return v;
    }

    //从服务器拉取与自己有关的所有信息，并储存在数据库中
    private void initMsgList() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        HttpUtil.getInstance().get(UrlHandler.getConversationAboutMe(),
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        dtos = new Gson().fromJson(response, new TypeToken<List<ConversationDto>>() {
                        }.getType());
                        adapter.setDtos(dtos);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                if (srl.isRefreshing()) {
                                    srl.setRefreshing(false);
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (srl.isRefreshing()) {
                                    srl.setRefreshing(false);
                                }
                            }
                        });
//                        Toast.makeText(getContext(),"拉取消息列表失败",Toast.LENGTH_SHORT).show();
                    }
                });

//        List<MessageDto> list =DataSupport.findAll(MessageDto.class);
//        Log.i("msg", "initMsgList: "+list.size());
    }
}
