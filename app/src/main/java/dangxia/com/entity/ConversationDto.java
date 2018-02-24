package dangxia.com.entity;


public class ConversationDto {
    private int id;

    private int taskId;

    private int initiatorId;

    private String initiatorName;

    private String publisherName;

    private String lastDate;

    private String lastWords;

    private String initDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(int initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getLastWords() {
        return lastWords;
    }

    public void setLastWords(String lastWords) {
        this.lastWords = lastWords;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    @Override
    public String toString() {
        return "ConversationDto{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", initiatorId=" + initiatorId +
                ", initiatorName='" + initiatorName + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", lastDate='" + lastDate + '\'' +
                ", lastWords='" + lastWords + '\'' +
                ", initDate='" + initDate + '\'' +
                '}';
    }
}
