package dangxia.com.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lichfaker.log.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dangxia.com.R;
import dangxia.com.adapter.MsgChatItemAdapter;
import dangxia.com.dto.ConversationDto;
import dangxia.com.dto.MessageDto;
import dangxia.com.dto.TaskDto;
import dangxia.com.utils.http.HttpCallbackListener;
import dangxia.com.utils.http.HttpUtil;
import dangxia.com.utils.http.UrlHandler;
import dangxia.com.utils.mqtt.MqttManager;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "chat";
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

    private boolean isFirstRun = true;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @OnClick(R.id.task_detail)
    void onTaskDetailClick() {
        //当且仅当自己是任务的发布者，并且订单未生效时才能修改价格
        if (!ordered && owner) {
            changePriceDialog.show();
        } else {//其余情况下，只是刷新价格和订单状态
//            String url = UrlHandler.getCurrentPrice(mTask.getId());
//            HttpUtil.getInstance().get(url, new HttpCallbackListener() {
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void onFinish(String response) {
//                    try {
//                        double newPrice = Double.parseDouble(response);
//                        if (newPrice != mTask.getPrice()) {
//                            mTask.setPrice(newPrice);
//                            runOnUiThread(() -> {
//                                taskDetail.setText("￥" + newPrice + " " + mTask.getContent());
//                                Toast.makeText(ChatActivity.this,
//                                        "价格已更新为" + newPrice + "元", Toast.LENGTH_SHORT).show();
//                            });
//                        }
//                    } catch (NumberFormatException e) {
//                        onError(e);
//                    }
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    super.onError(e);
//                    Log.i(TAG, "onError: 刷新价格失败" + e.getMessage());
//                }
//            });
            HttpUtil.getInstance().get(UrlHandler.getCon(mConversation.getId()), new HttpCallbackListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onFinish(String response) {
                    ConversationDto conversationDto =
                            new Gson().fromJson(response, ConversationDto.class);
                    if (conversationDto != null && mConversation.getId() == conversationDto.getId()) {
                        //进行比较，看看价格或者订单状态有没有更改
                        if (mTask.getPrice() != conversationDto.getTask().getPrice()) {
                            // 价格已发生改变
                            runOnUiThread(() -> {
                                double newPrice = conversationDto.getTask().getPrice();
                                taskDetail.setText("￥" + newPrice + " " + mTask.getContent());
                                Toast.makeText(ChatActivity.this,
                                        "价格已更新为" + newPrice + "元", Toast.LENGTH_SHORT).show();
                            });
                        }
                        if (conversationDto.getTask().getOrderId() != -1) {
                            // 订单状态已发生改变
                            runOnUiThread(() -> {
                                ordered = true;
                                confirmBtn.setEnabled(true);
                                confirmBtn.setVisibility(View.VISIBLE);
                                confirmBtn.setText("查看订单");
                                confirmBtn.setOnClickListener(checkOrder);
                                Toast.makeText(ChatActivity.this,
                                        "订单已建立", Toast.LENGTH_SHORT).show();
                            });
                        }
                        mConversation = conversationDto;
                        mTask = mConversation.getTask();
                    }
                }
            });
        }

    }

    @OnClick(R.id.back_btn)
    void back() {
        finish();
    }

    private boolean ordered = false;
    private boolean owner = false;
    private View.OnClickListener checkOrder;
    private ConversationDto mConversation;
    private TaskDto mTask;
    private MaterialDialog changePriceDialog;
    private int conId;

//    @SuppressLint("SimpleDateFormat")
//    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mConversation = (ConversationDto) getIntent().getSerializableExtra("con");
        if (mConversation == null) {
            Toast.makeText(this, "不存在的会话，请重试。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            owner = mConversation.getInitiatorId() != UrlHandler.getUserId();
            mTask = mConversation.getTask();
            ordered = mTask.getOrderId() != -1;
        }
        Log.i("chat", "onCreate: " + conId);
        Log.i("chat", "onCreate: " + mConversation.toString());
        checkOrder = view -> {
            Intent intent = new Intent(ChatActivity.this,
                    OrderDetailActivity.class);
            intent.putExtra("taskId", mTask.getId());
            startActivity(intent);
        };
        name.setText(owner ?
                mConversation.getInitiatorName() : mConversation.getPublisherName());
        conId = mConversation.getId();
        changePriceDialog = new MaterialDialog
                .Builder(ChatActivity.this)
                .title("修改价格")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .positiveText("确认")
                .input("请输入新价格", "" + mTask.getPrice(), false,
                        (dialog, input) -> changePrice(Double.parseDouble(input.toString()))).build();
        taskDetail.setText("￥" + mTask.getPrice() + " " + mTask.getContent());
        if (ordered) {
            confirmBtn.setText("查看订单");
            confirmBtn.setOnClickListener(checkOrder);
        } else if (!owner) {
            confirmBtn.setVisibility(View.INVISIBLE);
        } else {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //订单授权
                    RequestBody body = new FormBody.Builder()
                            .add("senderId", String.valueOf(mConversation.getInitiatorId()))
                            .add("taskId", String.valueOf(mTask.getId()))
                            .build();
                    HttpUtil.getInstance().post(UrlHandler.takeOrder(), body, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            if (response.equals("" + mConversation.getId())) {

                                final Snackbar snackbar = Snackbar.make(confirmBtn,
                                        "您的需求已被成功接单！", Snackbar.LENGTH_SHORT);
                                snackbar.setAction("查看订单", checkOrder);
                                runOnUiThread(() -> {
                                    snackbar.show();
                                    ordered = true;
                                    confirmBtn.setText("查看订单");
                                    confirmBtn.setOnClickListener(checkOrder);
                                });
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            super.onError(e);
                            runOnUiThread(() -> Toast.makeText(ChatActivity.this, "确认失败，请稍后再试", Toast.LENGTH_SHORT).show());
                        }
                    });

                }
            });
        }
        initMsgData();


        sendBtn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(presendET.getText())) {
                sendMsg(presendET.getText().toString());
                presendET.setText("");
//                    presendET.clearComposingText();
            }
        });

        presendET.addTextChangedListener(new MyTextWatcher());
        findViewById(R.id.check_info_btn).setOnClickListener(view -> startActivity(new Intent(ChatActivity.this, OthersInfoActivity.class)));


    }

    private void initMsgData() {
        Date lastDate = null;
        if (adapter == null) {
            adapter = new MsgChatItemAdapter();
            //先在本地数据库中查找
//            List<MessageDto> local = DataSupport.findBySQL("select * from messagedto as msg where msg.conversation_id = "+mConversation.getId());
            List<MessageDto> local = DataSupport.where("conversationId = ?", "" + mConversation.getId())
                    .order("date").find(MessageDto.class);
            if (local != null && local.size() > 0) {
                msgList = local;
                Log.i(TAG, "initMsgData: 本地数据有" + local.size());
                try {
                    lastDate = DATE_FORMAT.parse(local.get(local.size() - 1).getDate());
                } catch (ParseException e) {
                    lastDate = null;
                }
            } else {
                Log.i(TAG, "initMsgData: 在本地数据库中没有数据");
            }
            adapter.setMsgList(msgList);
            chatList.setAdapter(adapter);
            chatList.setItemAnimator(new DefaultItemAnimator());
            chatList.setLayoutManager(new LinearLayoutManager(this));
        }
        HttpUtil.getInstance().get(lastDate == null ? UrlHandler.getMsgList(conId) : UrlHandler.getMsgList(conId, lastDate.getTime()),
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        msgList = new Gson().fromJson(response, new TypeToken<List<MessageDto>>() {
                        }.getType());
                        if (msgList != null) {
                            //批量存入数据库
                            Log.i(TAG, "onFinish: 从服务器获取的新数据有" + msgList.size());
                            for (MessageDto dto : msgList) {
                                dto.save();
                                insertAndScroll(dto);
                            }

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
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if (response.equals("1")) {
                            messageDto.save();
                        }
                    }
                });
        insertAndScroll(messageDto);
    }

    /**
     * 插入一条新消息并滚到底部
     *
     * @param messageDto
     */
    private void insertAndScroll(MessageDto messageDto) {
        runOnUiThread(() -> {
            adapter.getMsgList().add(messageDto);
            if (messageDto.getType() == -1) {
                return;
            }
            adapter.notifyItemRangeChanged(adapter.getMsgList().size() - 2, 1);
            adapter.notifyItemInserted(adapter.getMsgList().size() - 1);
            scrollToBottom();
        });

    }

    private void checkEdit() {
        sendBtn.setEnabled(!TextUtils.isEmpty(presendET.getText()));
    }

    private void changePrice(double newPrice) {
        RequestBody body = new FormBody.Builder()
                .add("taskId", String.valueOf(mTask.getId()).trim())
                .add("newPrice", String.valueOf(newPrice).trim())
                .add("receiverId", String.valueOf(mConversation.getInitiatorId()))
                .build();
        HttpUtil.getInstance().post(UrlHandler.changePrice(), body, new HttpCallbackListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(String response) {
                if (response.equals("1")) {
                    runOnUiThread(() -> {
                        Snackbar.make(taskDetail, "修改成功", Snackbar.LENGTH_SHORT).show();
                        mTask.setPrice(newPrice);
                        taskDetail.setText("￥" + mTask.getPrice() + " " + mTask.getContent());
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                Logger.e(e.getMessage());
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MqttManager.getInstance().setNeedNotify(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatList.scrollToPosition(adapter.getMsgList().size() - 1);
    }

    @Subscribe()//监听eventbus事件
    public void onEvent(MessageDto messageDto) {
        Log.i("chat", "onEvent: 监听到busevent" + messageDto.toString());
        //将消息反序列化为MessageDto
        runOnUiThread(() -> {
            //在本地数据库中写入
            messageDto.save();
            if (messageDto.getType() != -1) {
                insertAndScroll(messageDto);
            } else if (messageDto.getContent().contains(MessageDto.PRICE_CHANGED)
                    || messageDto.getContent().contains(MessageDto.ORDER_CREATED)) {
                Log.i(TAG, "onEvent: 需要刷新一下状态");
                onTaskDetailClick();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MqttManager.getInstance().setNeedNotify(false);
    }

}
