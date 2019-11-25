package ca.kevinscode.td_challenge.ui.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ca.kevinscode.td_challenge.R;


public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_LONG = "locationLongitude";
    static final String KEY_COUNTRY_LOCATION = "locationCountry";
    static final String KEY_M_CAT_CODE = "merchantCategoryCode";
    static final String KEY_DESC = "description";
    static final String KEY_TYPE = "type";
    static final String KEY_MERCHANT_NAME = "merchantName";
    static final String KEY_AMOUNT = "currencyAmount";
    static final String KEY_PROV_REG = "locationRegion";
    static final String KEY_SOURCE = "source";
    static final String KEY_CITY = "locationCity";
    static final String KEY_DATE_TIME = "originationDateTime";
    static final String KEY_POSTAL = "locationPostalCode";
    static final String KEY_CUST_ID = "customerId";
    static final String KEY_MERCHANT_ID = "merchantId";
    static final String KEY_LAT = "locationLatitude";
    static final String KEY_ID = "id";
    static final String KEY_STREET = "locationStreet";
    static final String KEY_ACC_ID = "accountId";
    static final String KEY_TAGS = "categoryTags";


    static final String TAG = "TD_Challenge_Kevin_Cox";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "consolidated_endpoint_records";
    static final int DATABASE_VERSION = 9;

    static final String DATABASE_CREATE =
            "create table consolidated_endpoint_records (_id text primary key, "
                    + "locationLongitude double, locationCountry text, merchantCategoryCode integer, description text, type text, merchantName text," +
                    "currencyAmount double,  locationRegion text, source text, locationCity text, originationDateTime text, locationPostalCode text," +
                    "customerId text, merchantId text, locationLatitude double, id text,  locationStreet text, accountId text, categoryTags text)";

    Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }



    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DBTrackItemAdapter.DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DBTrackItemAdapter.DATABASE_TABLE);
            onCreate(db);
        }

    }
    public void loadCVSIntoDB(){
       //if no records in the db
        long result = DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);

        if (result == 0){
            try {
                InputStream raw = context.getResources().openRawResource(R.raw.consolidated);

                int currentLineNumber  = 0;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(raw, "UTF8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        if (currentLineNumber == 0){
                            currentLineNumber++;
                            continue;
                        }
                        for (int i =0; i < values.length; i++){
                            values[i] = values[i].replaceAll("\"", "");
                            if (values[i].equals("")){
                                values[i] = "0";
                            }
                        }
                        insertLocation(new Location(values[0], Double.parseDouble(values[1]), values[2], Integer.parseInt(values[3]), values[4], values[5],
                                values[6], Double.parseDouble(values[7]), values[8], values[9], values[10], values[11], values[12],
                                values[13], values[14], Double.parseDouble(values[15]), values[16], values[17], values[18], values[19]));

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        loadCVSIntoDB();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertLocation(Location passedLocation)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, passedLocation.get_id());
        initialValues.put(KEY_LONG, passedLocation.getLocationLongitude());
        initialValues.put(KEY_COUNTRY_LOCATION, passedLocation.getLocationCountry());
        initialValues.put(KEY_M_CAT_CODE, passedLocation.getMerchantCategoryCode());
        initialValues.put(KEY_DESC, passedLocation.getDescription());
        initialValues.put(KEY_TYPE, passedLocation.getType());
        initialValues.put(KEY_MERCHANT_NAME, passedLocation.getMerchantName());
        initialValues.put(KEY_AMOUNT, passedLocation.getCurrencyAmount());
        initialValues.put(KEY_PROV_REG, passedLocation.getLocationRegion());
        initialValues.put(KEY_SOURCE, passedLocation.getSource());
        initialValues.put(KEY_CITY, passedLocation.getLocationCity());
        initialValues.put(KEY_DATE_TIME, passedLocation.getOriginationDateTime());
        initialValues.put(KEY_POSTAL, passedLocation.getLocationPostalCode());
        initialValues.put(KEY_CUST_ID, passedLocation.getCustomerId());
        initialValues.put(KEY_MERCHANT_ID, passedLocation.getMerchantId());
        initialValues.put(KEY_LAT, passedLocation.getLocationLatitude());
        initialValues.put(KEY_ID, passedLocation.getId());
        initialValues.put(KEY_STREET, passedLocation.getLocationStreet());
        initialValues.put(KEY_ACC_ID, passedLocation.getAccountId());
        initialValues.put(KEY_TAGS, passedLocation.getCategoryTags());

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteLocation(String rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=\"" + rowId + "\"",null) > 0;
    }

    //---retrieves all the contacts---
    public List<Location> getAllLocations()
    {
        List<Location> locations = new ArrayList<>();
        Cursor mCursor =  db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_LONG,
                KEY_COUNTRY_LOCATION,
                KEY_M_CAT_CODE,
                KEY_DESC,
                KEY_TYPE,
                KEY_MERCHANT_NAME,
                KEY_AMOUNT,
                KEY_PROV_REG,
                KEY_SOURCE,
                KEY_CITY,
                KEY_DATE_TIME,
                KEY_POSTAL,
                KEY_CUST_ID,
                KEY_MERCHANT_ID,
                KEY_LAT,
                KEY_ID,
                KEY_STREET,
                KEY_ACC_ID,
                KEY_TAGS,
        }, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        while(mCursor.moveToNext()){
            locations.add(new Location(mCursor));
        }
        return locations;
    }
    public Location getLocationByNameandLocation(String merchantName, String street, String city){
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_LONG,
                                KEY_COUNTRY_LOCATION,
                                KEY_M_CAT_CODE,
                                KEY_DESC,
                                KEY_TYPE,
                                KEY_MERCHANT_NAME,
                                KEY_AMOUNT,
                                KEY_PROV_REG,
                                KEY_SOURCE,
                                KEY_CITY,
                                KEY_DATE_TIME,
                                KEY_POSTAL,
                                KEY_CUST_ID,
                                KEY_MERCHANT_ID,
                                KEY_LAT,
                                KEY_ID,
                                KEY_STREET,
                                KEY_ACC_ID,
                                KEY_TAGS, }, KEY_MERCHANT_NAME + "=\"" + merchantName + "\" AND " + KEY_STREET + "=\"" + street + "\" AND " + KEY_CITY + "=\"" + city + "\"" , null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        //  mCursor.moveToFirst();
        return new Location(mCursor);
    }

    public List<Location> getAllLocationsByCity (String cityName){
        List<Location> locations = new ArrayList<>();
        Cursor mCursor =  db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_LONG,
                KEY_COUNTRY_LOCATION,
                KEY_M_CAT_CODE,
                KEY_DESC,
                KEY_TYPE,
                KEY_MERCHANT_NAME,
                KEY_AMOUNT,
                KEY_PROV_REG,
                KEY_SOURCE,
                KEY_CITY,
                KEY_DATE_TIME,
                KEY_POSTAL,
                KEY_CUST_ID,
                KEY_MERCHANT_ID,
                KEY_LAT,
                KEY_ID,
                KEY_STREET,
                KEY_ACC_ID,
                KEY_TAGS,
        }, KEY_CITY + "=\"" + cityName + "\"", null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        while(mCursor.moveToNext()){
            locations.add(new Location(mCursor));
        }
        return locations;
    }

    //---retrieves a particular contact---
    public Location getLocationByRowID(String rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_LONG,
                                KEY_COUNTRY_LOCATION,
                                KEY_M_CAT_CODE,
                                KEY_DESC,
                                KEY_TYPE,
                                KEY_MERCHANT_NAME,
                                KEY_AMOUNT,
                                KEY_PROV_REG,
                                KEY_SOURCE,
                                KEY_CITY,
                                KEY_DATE_TIME,
                                KEY_POSTAL,
                                KEY_CUST_ID,
                                KEY_MERCHANT_ID,
                                KEY_LAT,
                                KEY_ID,
                                KEY_STREET,
                                KEY_ACC_ID,
                                KEY_TAGS, }, KEY_ROWID + "=\"" + rowId + "\"", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
      //  mCursor.moveToFirst();
        return new Location(mCursor);
    }

    //---updates a contact---
    public boolean updateContact(Location passedLocation) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, passedLocation.get_id());
        initialValues.put(KEY_LONG, passedLocation.getLocationLongitude());
        initialValues.put(KEY_COUNTRY_LOCATION, passedLocation.getLocationCountry());
        initialValues.put(KEY_M_CAT_CODE, passedLocation.getMerchantCategoryCode());
        initialValues.put(KEY_DESC, passedLocation.getDescription());
        initialValues.put(KEY_TYPE, passedLocation.getType());
        initialValues.put(KEY_MERCHANT_NAME, passedLocation.getMerchantName());
        initialValues.put(KEY_AMOUNT, passedLocation.getCurrencyAmount());
        initialValues.put(KEY_PROV_REG, passedLocation.getLocationRegion());
        initialValues.put(KEY_SOURCE, passedLocation.getSource());
        initialValues.put(KEY_CITY, passedLocation.getLocationCity());
        initialValues.put(KEY_DATE_TIME, passedLocation.getOriginationDateTime());
        initialValues.put(KEY_POSTAL, passedLocation.getLocationPostalCode());
        initialValues.put(KEY_CUST_ID, passedLocation.getCustomerId());
        initialValues.put(KEY_MERCHANT_ID, passedLocation.getMerchantId());
        initialValues.put(KEY_LAT, passedLocation.getLocationLatitude());
        initialValues.put(KEY_ID, passedLocation.getId());
        initialValues.put(KEY_STREET, passedLocation.getLocationStreet());
        initialValues.put(KEY_ACC_ID, passedLocation.getAccountId());
        initialValues.put(KEY_TAGS, passedLocation.getCategoryTags());
        return db.update(DATABASE_TABLE, initialValues, KEY_ROWID + "=\"" + passedLocation.get_id() + "\"", null) > 0;
    }


    public List<Location> getLocationsInRange(double distanceRange, LatLng currentLocation, String currentLocationCity){

        List<Location> locations;
        if(currentLocationCity.equals("")){
            locations = this.getAllLocations();
        }else{
            //get locations by the city first
            locations= this.getAllLocationsByCity(currentLocationCity);
        }



        //filter list
        List<Location> toBeRemoved = new ArrayList<>();
        for (int i =0; i < locations.size(); i++){
            double distance = distanceBetweenPoints(currentLocation, new LatLng(locations.get(i).getLocationLatitude(), locations.get(i).getLocationLongitude()));

            //new ApiDirectionsAsyncTask().execute(currentLocation, new LatLng(locations.get(i).getLocationLatitude(), locations.get(i).getLocationLongitude()));
            if (distance > distanceRange ){
                toBeRemoved.add(locations.get(i));
            }
            //remove online transactions
            else if (locations.get(i).getLocationLatitude() == 0 && locations.get(i).getLocationLongitude() == 0){
                toBeRemoved.add(locations.get(i));
            }
        }
        locations.removeAll(toBeRemoved);

        return locations;
    }

    public double distanceBetweenPoints (LatLng point1, LatLng point2){


        final int RADIUS_OF_EARTH = 6371; // Radius of the earth

        double latDistance = Math.toRadians(point2.latitude - point1.latitude);
        double lonDistance = Math.toRadians(point2.longitude - point1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS_OF_EARTH * c;
        return distance;
    }
}
