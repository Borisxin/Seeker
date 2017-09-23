package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class MyCard {
    private Double Latitude;
    private Double Longitude;
    private String Picture;
    private String Title;
    private String Text;
    private Boolean IsinRange;
    private String Time;
    public MyCard(Double Latitude, Double Longitude, String Picture, String Title, String Text, Boolean IsinRange,String Time){
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.Picture=Picture;
        this.Title=Title;
        this.Text=Text;
        this.IsinRange=IsinRange;
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
    public String getTitle(){
        return Title;
    }
    public String getText(){
        return Text;
    }
    public Boolean getIsinRange(){
        return IsinRange;
    }
    public void setIsinRange(Boolean isinRange){
        this.IsinRange=isinRange;
    }
    public void setPicture(String picture){
        this.Picture=picture;
    }
}
