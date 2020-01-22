package com.tmendes.birthdaydroid.helpers;

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

    private static final String DATABASE_NAME = "contacts.db";
    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_CONTACT_ID = "cid";
    private static final String CONTACTS_COLUMN_FAVORITE = "favorite";
    private static final String CONTACTS_COLUMN_IGNORE = "ignored";

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

    public long insertContact(long dbID, String cid, boolean favorite, boolean ignored) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cid", cid);
        contentValues.put("favorite", favorite);
        contentValues.put("ignored", ignored);

        long dbIDToReturn = dbID;

        if (dbID != -1) {
            db.update("contacts", contentValues, "id = ? ",
                    new String[] { Long.toString(dbID) } );
        } else {
            dbIDToReturn = db.insertOrThrow("contacts", null, contentValues);
        }

        return dbIDToReturn;
    }

    private void deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public void cleanDb(final HashMap<String, DBContact> contactList) {
        for (Map.Entry<String, DBContact> contact: contactList.entrySet()) {
            this.deleteContact(contact.getValue().getId());
        }
    }

    public HashMap<String, DBContact> getAllCotacts() {
        HashMap<String, DBContact> hashMap = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            int id = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID));
            String cid = res.getString(res.getColumnIndex(CONTACTS_COLUMN_CONTACT_ID));
            boolean favorite = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_FAVORITE)) == 1;
            boolean ignore = res.getInt(res.getColumnIndex(CONTACTS_COLUMN_IGNORE)) == 1;
            hashMap.put(cid, new DBContact(id, favorite, ignore));
            res.moveToNext();
        }

        res.close();
        db.close();

        return hashMap;
    }
}
