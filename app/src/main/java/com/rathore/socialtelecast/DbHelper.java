package com.rathore.socialtelecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ajeet on 28/7/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SocialTelecast";
    //table name
    private static final String TABLE_GROUPNAME = "groupname";

    private static final String KEY_GROUP_ID = "key_group_id";
    private static final String KEY_GROUP_NAME = "key_group_name";
    private static final String KEY_GROUPMEMBER_EMAIL = "key_group_email";
    private static final String KEY_BTN_ADD = "key_btn_add";


    private final Context context;
    private SQLiteOpenHelper mDbHelper;
    private SQLiteDatabase mDb;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DbHelper open() {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        mDbHelper.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_GROUPNAME = "CREATE TABLE " + TABLE_GROUPNAME + "("
                + KEY_GROUP_ID + " INTEGER PRIMARY KEY ,"
                + KEY_GROUP_NAME + " TEXT NOT NULL ,"
                + KEY_GROUPMEMBER_EMAIL + " INTEGER,"
                + KEY_BTN_ADD + " TEXT NOT NULL" + ")";


        db.execSQL(CREATE_TABLE_GROUPNAME);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_GROUPNAME);

    }

    public void addHome(String name,String groupid,String btnAdd) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_NAME, name);
        values.put(KEY_GROUPMEMBER_EMAIL, groupid);
        values.put(KEY_BTN_ADD, btnAdd);

        db.insert(TABLE_GROUPNAME, null, values);
    }

    public void deleteGroupName(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GROUPNAME + " WHERE " + KEY_GROUP_ID + "='" + id + "'");
        db.close();
    }

    public void updateGroup(String name, String groupid) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_GROUP_NAME, name);

        db.update(TABLE_GROUPNAME, values, KEY_GROUP_ID + " = ?", new String[]
                {String.valueOf(groupid)});
    }

    public HashMap<String, ArrayList<String>> getGroupDetails() {
        HashMap<String, ArrayList<String>> groupDetailList = new HashMap<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GROUPNAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Cursor cursor = db.query(TABLE_GROUPNAME, new String[] {KEY_GROUPMEMBER_EMAIL},null,null,null,null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String groupName=cursor.getString(cursor.getColumnIndex(KEY_GROUP_NAME));
                String email=cursor.getString(cursor.getColumnIndex(KEY_GROUPMEMBER_EMAIL));
                if (groupDetailList.containsKey(groupName)){
                    ArrayList<String> emailList=groupDetailList.get(groupName);
                    emailList.add(email);
                }else {
                    ArrayList<String> emailList=new ArrayList<>();
                    emailList.add(email);
                    groupDetailList.put(groupName,emailList);
                }

                /*HashMap<String, String> map = new HashMap<>();

                map.put("group_id", cursor.getString(cursor.getColumnIndex(KEY_GROUP_ID)));
                map.put("group_name", cursor.getString(cursor.getColumnIndex(KEY_GROUP_NAME)));
                map.put("group_member", cursor.getString(cursor.getColumnIndex(KEY_GROUPMEMBER_EMAIL)));

                groupDetailList.add(map);*/

                Log.d("GET_DATA", "" + groupDetailList.size());

            } while (cursor.moveToNext());
        }
        // return vender Detail list
        return groupDetailList;
    }


}
