package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class NewFriend {
    private String account;
    private String nickname;
    private String photo;
    public NewFriend(String account,String nickname,String photo){
        this.account=account;
        this.nickname=nickname;

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
}
