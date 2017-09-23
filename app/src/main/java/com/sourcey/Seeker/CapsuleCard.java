package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class CapsuleCard {
    private Double Latitude;
    private Double Longitude;
    private String Picture;
    private String Video;
    private String Audio;
    private String Title;
    private String Text;
    private String OpenTime;
    private Boolean IsinRange;
    private Boolean Expired;
    private String Time;
    public CapsuleCard(Double Latitude, Double Longitude, String Picture, String Video, String Audio, String Title, String Text, String OpenTime, Boolean IsinRange, Boolean Expired,String Time){
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.Picture=Picture;
        this.Video=Video;
        this.Audio=Audio;
        this.Title=Title;
        this.Text=Text;
        this.OpenTime=OpenTime;
        this.IsinRange=IsinRange;
        this.Expired=Expired;
        this.Time=Time;
    }
    public String getTime(){
        return Time;
    }
    public Double getLatitude(){
        return Latitude;
    }
    public Double getLongitude(){
        return Longitude;
    }
    public String getPicture(){
        return Picture;
    }
    public String getVideo(){
        return Video;
    }
    public String getAudio(){
        return Audio;
    }
    public String getTitle(){
        return Title;
    }
    public String getText(){
        return Text;
    }
    public String getOpenTime(){
        return OpenTime;
    }
    public Boolean getIsinRange(){
        return IsinRange;
    }
    public Boolean getExpired(){
        return Expired;
    }
    public void setIsinRange(Boolean isinRange){
        this.IsinRange=isinRange;
    }
    public void setPicture(String picture){
        this.Picture=picture;
    }
    public void setVideo(String video){
        this.Video=video;
    }
    public void setAudio(String audio){
        this.Audio=audio;
    }
}
