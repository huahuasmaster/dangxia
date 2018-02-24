package dangxia.com.entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

public class MessageDto extends DataSupport {

    private int id;

    private String content;

    private String date;

    private int senderId;

    private int receiverId;

    private int type;//0->文字消息（默认）、1->语音消息、2->图片消息

    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
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

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
