package io.wyntr.peepster;

/**
 * Created by Siddharth on 4/4/16.
 */
public class ParsePosts {
    private String phone;
    private String video;
    private String user;
    private String userId;
    private String objectId;
    private int views;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getVideo() {
        return video;
    }
    public void setVideo(String video) {
        this.video = video;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getObjectId(){
        return objectId;
    }
    public void setObjectId(String objectId){
        this.objectId = objectId;
    }
    public int getViews() {
        return views;
    }
    public void setViews(int views) {
        this.views = views;
    }
}
