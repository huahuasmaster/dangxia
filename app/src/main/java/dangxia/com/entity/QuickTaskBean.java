package dangxia.com.entity;

/**
 * 单条“一秒火速”信息
 * Created by zhuang_ge on 2017/11/20.
 */

public class QuickTaskBean {
    private int id;
    private int userId;
    //打赏的价格
    private float price;
    private String content;
    private String sendTime;
    //纬度
    private double latitude;
    //经度
    private double longtitude;

    public QuickTaskBean(int id, int userId, float price, String content, String sendTime, double latitude, double longtitude) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.content = content;
        this.sendTime = sendTime;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public QuickTaskBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
