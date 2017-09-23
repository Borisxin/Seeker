package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class JoinHistoryItem {
    private String Picture;
    private String Activity_name;
    private String Time;
    private Boolean isUsed;
    private String ID;
    private String category;
    public JoinHistoryItem(String Picture,String Activity_name,String Time,String ID,Boolean isused,String category){
        this.Picture=Picture;
        this.Activity_name=Activity_name;
        this.Time=Time;
        this.ID=ID;
        this.isUsed=isused;
        this.category=category;
    }
    public String getID(){
        return ID;
    }
    public String getPicture(){
        return Picture;
    }
    public String getActivity_name(){
        return Activity_name;
    }
    public String getTime(){
        return Time;
    }
    public String getCategory(){
        return category;
    }
    public Boolean getIsUsed(){
        return isUsed;
    }
    public void setIsUsed(Boolean isUsed){
        this.isUsed=isUsed;
    }
}
