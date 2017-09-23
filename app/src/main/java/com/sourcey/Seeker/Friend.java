package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class Friend {
    private String account;
    private String nickname;
    private String gender;
    private String ischeck;
    private String photo;
    public Friend(String account,String nickname,String gender,String ischeck,String photo){
        this.account=account;
        this.nickname=nickname;
        this.gender=gender;
        this.ischeck=ischeck;
        this.photo=photo;
    }
    public String getPhoto(){
        return photo;
    }
    public void setPhoto(String photo){
        this.photo=photo;
    }
    public String getAccount(){
        return account;
    }
    public void setAccount(String account){
        this.account=account;
    }
    public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){
        this.nickname=nickname;
    }
    public String getGender(){
        return gender;
    }
    public void setGender(String gender){
        this.gender=gender;
    }
    public String getIscheck(){
        return ischeck;
    }
    public void setIscheck(String ischeck){
        this.ischeck=ischeck;
    }
}
