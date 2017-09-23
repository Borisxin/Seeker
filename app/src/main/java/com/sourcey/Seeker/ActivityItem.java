package com.sourcey.Seeker;


/**
 * Created by carl on 2017/6/8.
 */

public class ActivityItem {
    private String Picture;
    private Double Latitude;
    private Double Longitude;
    private String Activity_Name;
    private String Nature;
    private String Content;
    private String StartDate;
    private String FinishDate;
    private String Address;
    private String Phone;
    private String Store_Name;
    private String distance;
    public ActivityItem(String Picture,Double Latitude,Double Longitude,String Activity_Name,String Nature,String Content,String StartDate,String FinishDate,String Address,String Phone,String Store_Name,String distance){
        this.Picture=Picture;
        this.Latitude=Latitude;
        this.Longitude=Longitude;
        this.Activity_Name=Activity_Name;
        this.Nature=Nature;
        this.Content=Content;
        this.StartDate=StartDate;
        this.FinishDate=FinishDate;
        this.Address=Address;
        this.Phone=Phone;
        this.Store_Name=Store_Name;
        this.distance=distance;
    }
    public String getPicture(){
        return Picture;
    }
    public Double getLatitude(){
        return Latitude;
    }
    public Double getLongitude(){
        return Longitude;
    }
    public String getActivity_Name(){
        return Activity_Name;
    }
    public String getDistance()
    {
        return distance;
    }
    public String getNature(){
        return Nature;
    }
    public String getContent(){
        return Content;
    }
    public String getStartDate(){
        return StartDate;
    }
    public String getFinishDate(){
        return FinishDate;
    }
    public String getAddress(){
        return Address;
    }
    public String getPhone(){
        return Phone;
    }
    public String getStore_Name(){
        return Store_Name;
    }
}
