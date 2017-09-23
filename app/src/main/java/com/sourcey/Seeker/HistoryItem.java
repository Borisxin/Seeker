package com.sourcey.Seeker;

/**
 * Created by carl on 2017/4/12.
 */

public class HistoryItem {
    private Double Latitude;
    private Double Longitude;
    private String NewPicture;
    private String OldPicture;
    private String Title;
    private String Text;
    public HistoryItem(Double Latitude, Double Longitude, String NewPicture,String OldPicture,String Title, String Text){
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.NewPicture=NewPicture;
        this.OldPicture=OldPicture;
        this.Title=Title;
        this.Text=Text;
    }
    public Double getLatitude(){
        return Latitude;
    }
    public Double getLongitude(){
        return Longitude;
    }
    public String getNewPicture(){
        return NewPicture;
    }
    public String getOldPicture(){
        return OldPicture;
    }
    public String getTitle(){
        return Title;
    }
    public String getText(){
        return Text;
    }
    public void setNewPicture(String newPicture){
        this.NewPicture=newPicture;
    }
    public void setOldPicture(String oldPicture){
        this.OldPicture=oldPicture;
    }
}
