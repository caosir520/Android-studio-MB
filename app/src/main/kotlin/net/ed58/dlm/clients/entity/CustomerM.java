package net.ed58.dlm.clients.entity;

/**
 * 用户信息的bean
 * Created by Administrator on 2017/11/28/028.
 */

public class CustomerM {


    /**
     * userId : 402881ea5ec92926015ec92affd80008
     * nickName : 18521306134
     * head :
     * mobile : 18521306134
     * sex : 3
     * infoCompleted : false
     * status : 0
     * createTime : 2017-11-14 17:22:59
     * lastModifyTime : 2017-11-14 17:22:59
     * authStatus : 1
     * sendTimes : 0
     * deliveryTimes : 0
     * asSenderRating : 0
     * asDeliveryManRating : 0
     * id : 8a8080885fb99478015fb9d6bef9000c
     */

    private String userId;
    private String nickName;
    private String head="";
    private String mobile;
    private int sex;
    private boolean infoCompleted;
    private int status;
    private String createTime;
    private String lastModifyTime;
    private int authStatus;
    private int sendTimes;
    private int deliveryTimes;
    private int asSenderRating;
    private int asDeliveryManRating;
    private String id;
    private java.lang.String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isInfoCompleted() {
        return infoCompleted;
    }

    public void setInfoCompleted(boolean infoCompleted) {
        this.infoCompleted = infoCompleted;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public int getSendTimes() {
        return sendTimes;
    }

    public void setSendTimes(int sendTimes) {
        this.sendTimes = sendTimes;
    }

    public int getDeliveryTimes() {
        return deliveryTimes;
    }

    public void setDeliveryTimes(int deliveryTimes) {
        this.deliveryTimes = deliveryTimes;
    }

    public int getAsSenderRating() {
        return asSenderRating;
    }

    public void setAsSenderRating(int asSenderRating) {
        this.asSenderRating = asSenderRating;
    }

    public int getAsDeliveryManRating() {
        return asDeliveryManRating;
    }

    public void setAsDeliveryManRating(int asDeliveryManRating) {
        this.asDeliveryManRating = asDeliveryManRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
