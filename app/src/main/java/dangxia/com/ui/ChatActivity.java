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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;

import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dangxia.com.R;
import dangxia.com.adapter.MsgChatItemAdapter;
import dangxia.com.entity.ConversationDto;
import dangxia.com.entity.MessageDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.chat_list)
    EasyRecyclerView chatList;

    @BindView(R.id.emotion_send)
    Button sendBtn;

    private List<MessageDto> msgList = new ArrayList<>();

    @BindView(R.id.edit_text)
    EditText presendET;

    @BindView(R.id.confirm_btn)
    Button confirmBtn;

    @BindView(R.id.chat_name)
    TextView name;

    @BindView(R.id.task_detail)
    TextView taskDetail;

    private MsgChatItemAdapter adapter;

    @BindView(R.id.back_btn)
    View backBtn;
    private boolean ordered = false;
    private boolean owner = false;
    private View.OnClickListener checkOrder;
    private ConversationDto mConversation;
    private int conId;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        mConversation = (ConversationDto) getIntent().getSerializableExtra("con");
        if (mConversation == null) {
            Toast.makeText(this, "不存在的会话，请重试。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            owner = mConversation.getInitiatorId() != UrlHandler.getUserId();
            ordered = mConversation.getTask().getOrderId() != -1;
        }
        Log.i("chat", "onCreate: " + conId);
        Log.i("chat", "onCreate: " + mConversation.toString());
        checkOrder = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this,
                        OrderDetailActivity.class);
                intent.putExtra("taskId", mConversation.getTask().getId());
                startActivity(intent);
            }
        };
        name.setText(owner ?
                mConversation.getInitiatorName() : mConversation.getPublisherName());
        conId = mConversation.getId();
        if (ordered) {
            confirmBtn.setText("查看订单");
            confirmBtn.setOnClickListener(checkOrder);
        } else if (!owner) {
            confirmBtn.setVisibility(View.INVISIBLE);
        } else {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    RequestBody body = new FormBody.Builder()
                            .add("senderId", String.valueOf(mConversation.getInitiatorId()))
                            .add("taskId", String.valueOf(mConversation.getTask().getId()))
                            .build();
                    HttpUtil.getInstance().post(UrlHandler.takeOrder(), body, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            if (response.equals("" + mConversation.getId())) {

                                final Snackbar snackbar = Snackbar.make(confirmBtn,
                                        "您的需求已被成功接单！", Snackbar.LENGTH_SHORT);
                                snackbar.setAction("查看订单", checkOrder);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        snackbar.show();
                                    }
                                });
                                ordered = true;
                                confirmBtn.setText("查看订单");
                                confirmBtn.setOnClickListener(checkOrder);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            super.onError(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChatActivity.this, "确认失败，请稍后再试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            });
        }
        initMsgData();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(presendET.getText())) {
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
                startActivity(new Intent(ChatActivity.this, OthersInfoActivity.class));
            }
        });


    }

    private void initMsgData() {
        if (adapter == null) {
            adapter = new MsgChatItemAdapter();
            adapter.setMsgList(msgList);
            chatList.setAdapter(adapter);
            chatList.setItemAnimator(new DefaultItemAnimator());
            chatList.setLayoutManager(new LinearLayoutManager(this));
        }
        HttpUtil.getInstance().get(UrlHandler.getMsgList(conId), new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                msgList = new Gson().fromJson(response, new TypeToken<List<MessageDto>>() {
                }.getType());
                if (msgList != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setMsgList(msgList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void sendMsg(String content) {
        MessageDto messageDto = new MessageDto();
        messageDto.setContent(content);
        messageDto.setSender(UrlHandler.getUserId());
        messageDto.setDate(new Date().toString());
        RequestBody body = new FormBody.Builder()
                .add("senderId", String.valueOf(UrlHandler.getUserId()))
                .add("content", content)
                .add("date", String.valueOf(new Date().getTime()))
                .add("type", "0")
                .build();
        HttpUtil.getInstance().post(UrlHandler.pushMsg(conId), body,
                null);
        adapter.getMsgList().add(messageDto);
        adapter.notifyItemRangeChanged(adapter.getMsgList().size() - 2, 1);
        adapter.notifyItemInserted(adapter.getMsgList().size() - 1);
        chatList.scrollToPosition(adapter.getMsgList().size() - 1);
    }

    private void checkEdit() {
        if (TextUtils.isEmpty(presendET.getText())) {
            sendBtn.setEnabled(false);
        } else {
            sendBtn.setEnabled(true);
        }
    }

    class MyTextWatcher implements TextWatcher {

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
                chatList.scrollToPosition(adapter.getMsgList().size() - 1);
            }
        });
    }
}
