package ca.kevinscode.td_challenge.ui.database;

import android.database.Cursor;

public class TrackItem {
    private int id;
    private String merchantName;
    private String street;
    private String city;
    private String date;
    private String forienKeyId;

    public TrackItem(int id,String merchantName, String street, String city, String date, double cost, String forienKeyId) {
        this.id = id;
        this.merchantName = merchantName;
        this.street = street;
        this.city = city;
        this.date = date;
        this.cost = cost;
        this.forienKeyId = forienKeyId;
    }

    public TrackItem(String merchantName, String street, String city, String date, double cost, String forienKeyId) {
        this.merchantName = merchantName;
        this.street = street;
        this.city = city;
        this.date = date;
        this.cost = cost;
        this.forienKeyId = forienKeyId;
    }

    public TrackItem(Cursor passedCursor){
        this.id = passedCursor.getInt(0);
        this.merchantName = passedCursor.getString(1);
        this.street = passedCursor.getString(2);
        this.city = passedCursor.getString(3);
        this.date = passedCursor.getString(4);
        this.cost = passedCursor.getDouble(5);
    }

    public String getForienKeyId() {
        return forienKeyId;
    }

    public void setForienKeyId(String forienKeyId) {
        this.forienKeyId = forienKeyId;
    }


    private double cost;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
