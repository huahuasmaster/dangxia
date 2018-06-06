package dangxia.com.dto;

import org.litepal.crud.DataSupport;

public class MessageDto extends DataSupport {

    public static final String PRICE_CHANGED = "$price_changed$";
    public static final String ORDER_CREATED = "$order_created$";

    private int msgId;

    private int conversationId;

    private String content;

    private String date;

    private int sender;

    private String senderName;

    private int type;//0->文字消息（默认）、1->语音消息、2->图片消息

    private int status;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int senderId) {
        this.sender = senderId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "msgId=" + msgId +
                ", conversationId=" + conversationId +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", sender=" + sender +
                ", senderName='" + senderName + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
