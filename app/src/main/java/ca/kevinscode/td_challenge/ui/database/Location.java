package ca.kevinscode.td_challenge.ui.database;

import android.database.Cursor;

public class    Location{
    private String _id;
    private double locationLongitude;
    private String locationCountry;
    private int merchantCategoryCode;
    private String description;
    private String type;
    private String merchantName;
    private double currencyAmount;
    private String locationRegion;
    private String source;
    private String locationCity;
    private String originationDateTime;
    private String locationPostalCode;
    private String customerId;
    private String merchantId;
    private double locationLatitude;
    private String id;
    private String locationStreet;
    private String accountId;
    private String categoryTags;

    public Location(String _id, double locationLongitude, String locationCountry, int merchantCategoryCode, String description, String type, String merchantName, double currencyAmount, String locationRegion, String source, String locationCity, String originationDateTime, String locationPostalCode, String customerId, String merchantId, double locationLatitude, String id, String locationStreet, String accountId, String categoryTags) {
        this._id = _id;
        this.locationLongitude = locationLongitude;
        this.locationCountry = locationCountry;
        this.merchantCategoryCode = merchantCategoryCode;
        this.description = description;
        this.type = type;
        this.merchantName = merchantName;
        this.currencyAmount = currencyAmount;
        this.locationRegion = locationRegion;
        this.source = source;
        this.locationCity = locationCity;
        this.originationDateTime = originationDateTime;
        this.locationPostalCode = locationPostalCode;
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.locationLatitude = locationLatitude;
        this.id = id;
        this.locationStreet = locationStreet;
        this.accountId = accountId;
        this.categoryTags = categoryTags;
    }

    public Location(Cursor passedCursor){
        this._id = passedCursor.getString(0);
        this.locationLongitude = passedCursor.getDouble(1);
        this.locationCountry = passedCursor.getString(2);
        this.merchantCategoryCode = passedCursor.getInt(3);
        this.description = passedCursor.getString(4);
        this.type = passedCursor.getString(5);
        this.merchantName = passedCursor.getString(6);
        this.currencyAmount = passedCursor.getDouble(7);
        this.locationRegion = passedCursor.getString(8);
        this.source = passedCursor.getString(9);
        this.locationCity = passedCursor.getString(10);
        this.originationDateTime = passedCursor.getString(11);
        this.locationPostalCode = passedCursor.getString(12);
        this.customerId = passedCursor.getString(13);
        this.merchantId = passedCursor.getString(14);
        this.locationLatitude = passedCursor.getDouble(15);
        this.id = passedCursor.getString(16);
        this.locationStreet = passedCursor.getString(17);
        this.accountId = passedCursor.getString(18);
        this.categoryTags = passedCursor.getString(19);
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public int getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(int merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public double getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getLocationRegion() {
        return locationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getOriginationDateTime() {
        return originationDateTime;
    }

    public void setOriginationDateTime(String originationDateTime) {
        this.originationDateTime = originationDateTime;
    }

    public String getLocationPostalCode() {
        return locationPostalCode;
    }

    public void setLocationPostalCode(String locationPostalCode) {
        this.locationPostalCode = locationPostalCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCategoryTags() {
        return categoryTags;
    }

    public void setCategoryTags(String categoryTags) {
        this.categoryTags = categoryTags;
    }
}