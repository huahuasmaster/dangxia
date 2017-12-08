package dangxia.com.entity;

/**
 * 作者：Rance on 2016/12/14 14:13
 * 邮箱：rance935@163.com
 */
public class MessageBean {
    public static final int TYPE_SEND = 0;
    public static final int TYPE_ACCEPT = 1;
    public static final int STATE_UNREAD = 2;
    public static final int STATE_READ = 3;
    private int type;
    private String content;
    private String filepath;
    private int sendState;
    private String time;
    private String header;
    private String imageUrl;
    private long voiceTime;
    private String msgId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", filepath='" + filepath + '\'' +
                ", sendState=" + sendState +
                ", time='" + time + '\'' +
                ", header='" + header + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", voiceTime=" + voiceTime +
                ", msgId='" + msgId + '\'' +
                '}';
    }

    public MessageBean(int type, String content, String filepath, int sendState, String time, String header, String imageUrl, long voiceTime, String msgId) {
        this.type = type;
        this.content = content;
        this.filepath = filepath;
        this.sendState = sendState;
        this.time = time;
        this.header = header;
        this.imageUrl = imageUrl;
        this.voiceTime = voiceTime;
        this.msgId = msgId;
    }

    public MessageBean(String content, String time, String msgId) {
        this(MessageBean.TYPE_SEND,content,"",STATE_READ,time,"","",0L,msgId);
    }
}
