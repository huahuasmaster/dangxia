package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dangxia.com.R;
import dangxia.com.adapter.MsgChatItemAdapter;
import dangxia.com.entity.MessageBean;

public class ChatActivity extends AppCompatActivity {

    private EasyRecyclerView chatList;
    private Button sendBtn;
    private List<MessageBean> msgList = new ArrayList<>();
    private EditText presendET;
    private Button confirmBtn;
    private TextView name;
    private TextView taskDetail;
    private MsgChatItemAdapter adapter;
    private View backBtn;
    private boolean ordered = false;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatList = (EasyRecyclerView) findViewById(R.id.chat_list);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        presendET = (EditText) findViewById(R.id.edit_text);
        sendBtn = (Button) findViewById(R.id.emotion_send);
        backBtn = findViewById(R.id.back_btn);
        name = (TextView) findViewById(R.id.chat_name);
        taskDetail = (TextView) findViewById(R.id.task_detail);
        initData();
        adapter = new MsgChatItemAdapter();
        adapter.setMsgList(msgList);
        chatList.setAdapter(adapter);
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.setLayoutManager(new LinearLayoutManager(this));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(presendET.getText())) {
                    sendMsg(presendET.getText().toString());
                    presendET.setText("");
//                    presendET.clearComposingText();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        presendET.addTextChangedListener(new MyTextWatcher());
        findViewById(R.id.check_info_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this,OthersInfoActivity.class));
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(confirmBtn,"您的需求已被成功接单！",Snackbar.LENGTH_SHORT);
                snackbar.setAction("查看订单", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ChatActivity.this,OrderDetailActivity.class));
                    }
                });
                snackbar.show();
                ordered = true;
                confirmBtn.setText("查看订单");
                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ChatActivity.this,OrderDetailActivity.class));
                    }
                });
            }
        });
    }

    private void initData() {
        msgList.add(new MessageBean(MessageBean.TYPE_ACCEPT,"你好，也许我能提供帮助。","",
                MessageBean.STATE_UNREAD,"2017-11-26 18:44:30","","",0L,"1"));
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void sendMsg(String content) {
        MessageBean msg = new MessageBean(content,format.format(new Date()),"2");
        adapter.getMsgList().add(msg);
        adapter.notifyItemRangeChanged(adapter.getMsgList().size()-2,1);
        adapter.notifyItemInserted(adapter.getMsgList().size()-1);
        chatList.scrollToPosition(adapter.getMsgList().size()-1);
    }
    private void checkEdit() {
        if(TextUtils.isEmpty(presendET.getText())) {
            sendBtn.setEnabled(false);
        } else {
            sendBtn.setEnabled(true);
        }
    }
    class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkEdit();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkEdit();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkEdit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatList.scrollToPosition(adapter.getMsgList().size()-1);
            }
        });
    }
}
