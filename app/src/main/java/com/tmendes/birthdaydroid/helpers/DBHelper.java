package com.tmendes.birthdaydroid.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.tmendes.birthdaydroid.DBContact;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_CONTACT_ID = "cid";
    public static final String CONTACTS_COLUMN_FAVORITE = "favorite";
    public static final String CONTACTS_COLUMN_IGNORE = "ignored";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, cid string, favorite bool,ignored bool)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact(String cid, boolean favorite, boolean ignored) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cid", cid);
        contentValues.put("favorite", favorite);
        contentValues.put("ignored", ignored);
        db.insertOrThrow("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(String cid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where cid="+cid+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String cid, boolean favorite, boolean ignored) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cid", cid);
        contentValues.put("favorite", favorite);
        contentValues.put("ignored", ignored);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    private Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void cleanDb(final HashMap<String, DBContact> contactList) {
        for (Map.Entry<String, DBContact> contact: contactList.entrySet()) {
            this.deleteContact(contact.getValue().getId());
        }
    }

    public HashMap<String, DBContact> getAllCotacts() {
        HashMap<String, DBContact> hashMap = new HashMap<String, DBContact>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            int id = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID));
            String cid = res.getString(res.getColumnIndex(CONTACTS_COLUMN_CONTACT_ID));
            boolean favorite = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_FAVORITE)) == 1;
            boolean ignore = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_IGNORE)) == 1;
            hashMap.put(cid, new DBContact(id, cid, favorite, ignore));
            res.moveToNext();
        }
        return hashMap;
    }
}
