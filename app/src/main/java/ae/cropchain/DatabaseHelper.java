package ae.cropchain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by biome on 3/17/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "CropChainDB.db";

    // Table Names
    private static final String TABLE_USER = "Users";
    private static final String TABLE_PRODUCT = "Products";

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ( " +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name TEXT, " +
            "Email TEXT NOT NULL, " +
            "Password TEXT, " +
            "CONSTRAINT uq_email UNIQUE (Email) "+
            ")";
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + " ( " +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "Name TEXT, " +
            "Rate INTEGER, " +
            "RateUpdatedAt TEXT NOT NULL," +
            "RateUpdatedByUserId INTEGER NOT NULL, " +
            "IsActive INTEGER NOT NULL, " +
            "SortOrder INTEGER default 0 not null, " +
            "CONSTRAINT CHK_Products CHECK (Name <> '' AND IsActive BETWEEN 0 AND 1), "+
            "CONSTRAINT uq_product UNIQUE(Name)" +
            ")";

    private static final String EMPTY_TABLE_USER = "DELETE FROM " + TABLE_USER;
    private static final String EMPTY_TABLE_PRODUCT = "DELETE FROM " + TABLE_PRODUCT;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_PRODUCT);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        // create new tables
        onCreate(db);
    }

    public void emptyDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(EMPTY_TABLE_USER);
        db.execSQL(EMPTY_TABLE_PRODUCT);
    }

  // User Functions

   public User getLoggedInUser(){
        User usr = new User();
        String selectQuery = "SELECT * FROM " + TABLE_USER + " LIMIT 1 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst())
        {
            usr.setId(c.getLong(c.getColumnIndex("Id")));
            usr.setName(c.getString(c.getColumnIndex("Name")));
            usr.setEmail(c.getString(c.getColumnIndex("Email")));
            usr.setPassword(c.getString(c.getColumnIndex("Password")));
        }
        return usr;
    }


    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Id", user.getId());
        values.put("Name", user.getName());
        values.put("Email", user.getEmail());
        values.put("Password", user.getPassword());
        // insert row
        long id = db.insert(TABLE_USER, null, values);
        return id;
    }
// Product Functions
    public void emptyProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(EMPTY_TABLE_PRODUCT);
    }

    public long createProduct(Product prod) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Id", prod.getId());
        values.put("Name", prod.getName());
        values.put("Rate", prod.getRate());
        values.put("RateUpdatedAt",prod.getRateUpdatedAt());
        values.put("RateUpdatedByUserId",prod.getRateUpdatedByUserId());
        values.put("IsActive",prod.getIsActive());
        values.put("SortOrder",prod.getSortOrder());

        // insert row
        long id = db.insert(TABLE_PRODUCT, null, values);
        return id;
    }

    public List<Product> getProducts() {
        List<Product> Products = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE IsActive = 1 Order By SortOrder, Name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null)
            if (c.moveToFirst()) {
                do {
                    Product td = new Product();
                    td.setId(c.getInt(c.getColumnIndex("Id")));
                    td.setName(c.getString(c.getColumnIndex("Name")));
                    td.setRate(c.getInt(c.getColumnIndex("Rate")));
                    td.setRateUpdatedAt(c.getString(c.getColumnIndex("RateUpdatedAt")));
                    td.setRateUpdatedByUserId(c.getInt(c.getColumnIndex("RateUpdatedByUserId")));
                    td.setIsActive(c.getInt(c.getColumnIndex("IsActive")));
                    td.setSortOrder(c.getInt(c.getColumnIndex("SortOrder")));
                    // adding to todo list
                    Products.add(td);
                } while (c.moveToNext());
            }
        return Products;
    }

    public Product getProduct(long id) {
        Product prod = new Product();
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE IsActive = 1 AND Id = " + Long.toString(id) + " Order By SortOrder, Name";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst())
            {
                prod.setId(c.getInt(c.getColumnIndex("Id")));
                prod.setName(c.getString(c.getColumnIndex("Name")));
                prod.setRate(c.getInt(c.getColumnIndex("Rate")));
                prod.setRateUpdatedAt(c.getString(c.getColumnIndex("RateUpdatedAt")));
                prod.setRateUpdatedByUserId(c.getInt(c.getColumnIndex("RateUpdatedByUserId")));
                prod.setIsActive(c.getInt(c.getColumnIndex("IsActive")));
                prod.setSortOrder(c.getInt(c.getColumnIndex("SortOrder")));
            }
        return prod;
    }


    public long updateProduct(Product prod) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Name", prod.getName());
        values.put("Rate", prod.getRate());
        values.put("RateUpdatedAt", prod.getRateUpdatedAt());
        values.put("RateUpdatedByUserId", prod.getRateUpdatedByUserId());
        values.put("IsActive", prod.getIsActive());
        values.put("SortOrder", prod.getSortOrder());
        // insert row
        return db.update(TABLE_PRODUCT, values,   " Id = ?",
                new String[] { String.valueOf(prod.getId()) });
    }

    public int getProductCount(){
        int cnt = 0;
        String selectQuery = "SELECT  COUNT(*) AS ProdCount FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()){
            cnt = c.getInt(c.getColumnIndex("ProdCount"));
        }
        return cnt;
    }

    public int getProductRateDigits(){
        int cnt = 0;
        String selectQuery = "SELECT MAX(LENGTH(Rate)) as cnt FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.moveToFirst()){
            cnt = c.getInt(c.getColumnIndex("cnt"));
        }
        return cnt;
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
