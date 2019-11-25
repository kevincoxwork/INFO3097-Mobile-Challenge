package ca.kevinscode.td_challenge.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBTrackItemAdapter {

    static final String KEY_ROWID = "id";
    static final String KEY_MERCHANT_NAME = "merchantName";
    static final String KEY_STREET = "street";
    static final String KEY_CITY = "city";
    static final String KEY_DATE = "date";
    static final String KEY_COST = "cost";



    static final String TAG = "TD_Challenge_Kevin_Cox";

    static final String DATABASE_NAME = "MyDB";
    static final String DATABASE_TABLE = "track_items";

    static final String DATABASE_CREATE =
            "create table track_items (id integer primary key autoincrement, "
                    + "merchantName text, street text, city text, date text, cost double)";

    Context context;

    DatabaseHelperItem DBHelper;
    SQLiteDatabase db;

    public DBTrackItemAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelperItem(context);
    }



    private static class DatabaseHelperItem extends SQLiteOpenHelper
    {
        DatabaseHelperItem(Context context)
        {
            super(context, DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DBAdapter.DATABASE_CREATE);

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
            db.execSQL("DROP TABLE IF EXISTS " + DBAdapter.DATABASE_TABLE);

            onCreate(db);
        }

    }
    //---opens the database---
    public DBTrackItemAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertTrackItem(TrackItem trackItem)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MERCHANT_NAME, trackItem.getMerchantName());
        initialValues.put(KEY_STREET, trackItem.getStreet());
        initialValues.put(KEY_CITY, trackItem.getCity());
        initialValues.put(KEY_DATE, trackItem.getDate());
        initialValues.put(KEY_COST, trackItem.getCost());
        long responce = db.insert(DATABASE_TABLE, null, initialValues);

        Location location = getLocationByRowID(trackItem.getForienKeyId());


        //simulate calculating average since we don't have proper consolidated results
        location.setCurrencyAmount(((location.getCurrencyAmount() * 1000) + trackItem.getCost()) / 1001);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(DBAdapter.KEY_ROWID, location.get_id());
        updatedValues.put(DBAdapter.KEY_LONG, location.getLocationLongitude());
        updatedValues.put(DBAdapter.KEY_COUNTRY_LOCATION, location.getLocationCountry());
        updatedValues.put(DBAdapter.KEY_M_CAT_CODE, location.getMerchantCategoryCode());
        updatedValues.put(DBAdapter.KEY_DESC, location.getDescription());
        updatedValues.put(DBAdapter.KEY_TYPE, location.getType());
        updatedValues.put(DBAdapter.KEY_MERCHANT_NAME, location.getMerchantName());
        updatedValues.put(DBAdapter.KEY_AMOUNT, location.getCurrencyAmount());
        updatedValues.put(DBAdapter.KEY_PROV_REG, location.getLocationRegion());
        updatedValues.put(DBAdapter.KEY_SOURCE, location.getSource());
        updatedValues.put(DBAdapter.KEY_CITY, location.getLocationCity());
        updatedValues.put(DBAdapter.KEY_DATE_TIME, location.getOriginationDateTime());
        updatedValues.put(DBAdapter.KEY_POSTAL, location.getLocationPostalCode());
        updatedValues.put(DBAdapter.KEY_CUST_ID, location.getCustomerId());
        updatedValues.put(DBAdapter.KEY_MERCHANT_ID, location.getMerchantId());
        updatedValues.put(DBAdapter.KEY_LAT, location.getLocationLatitude());
        updatedValues.put(DBAdapter.KEY_ID, location.getId());
        updatedValues.put(DBAdapter.KEY_STREET, location.getLocationStreet());
        updatedValues.put(DBAdapter.KEY_ACC_ID, location.getAccountId());
        updatedValues.put(DBAdapter.KEY_TAGS, location.getCategoryTags());
        
        db.update(DBAdapter.DATABASE_TABLE, updatedValues, DBAdapter.KEY_ROWID + "=\"" + location.get_id() + "\"", null);

        return responce;
    }

    public Location getLocationByRowID(String rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DBAdapter.DATABASE_TABLE, new String[] {DBAdapter.KEY_ROWID,
                                DBAdapter.KEY_LONG,
                                DBAdapter.KEY_COUNTRY_LOCATION,
                                DBAdapter.KEY_M_CAT_CODE,
                                DBAdapter.KEY_DESC,
                                DBAdapter.KEY_TYPE,
                                DBAdapter.KEY_MERCHANT_NAME,
                                DBAdapter.KEY_AMOUNT,
                                DBAdapter.KEY_PROV_REG,
                                DBAdapter.KEY_SOURCE,
                                DBAdapter.KEY_CITY,
                                DBAdapter.KEY_DATE_TIME,
                                DBAdapter.KEY_POSTAL,
                                DBAdapter.KEY_CUST_ID,
                                DBAdapter.KEY_MERCHANT_ID,
                                DBAdapter.KEY_LAT,
                                DBAdapter.KEY_ID,
                                DBAdapter.KEY_STREET,
                                DBAdapter.KEY_ACC_ID,
                                DBAdapter.KEY_TAGS, }, DBAdapter.KEY_ROWID + "=\"" + rowId + "\"", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return new Location(mCursor);
    }



    //---retrieves all the contacts---
    public List<TrackItem> getAllTrackingInfo()
    {
        List<TrackItem> items = new ArrayList<>();
        Cursor mCursor =  db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_MERCHANT_NAME,
                KEY_STREET, KEY_CITY, KEY_DATE, KEY_COST
        }, null, null, null, null, KEY_ROWID + " DESC");


        while(mCursor.moveToNext()){
            items.add(new TrackItem(mCursor));
        }
        return items;
    }


}
