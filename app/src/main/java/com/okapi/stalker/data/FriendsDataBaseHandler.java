package com.okapi.stalker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.okapi.stalker.fragment.adapters.MyFriendsAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsDataBaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "friendsDB";
    // Contacts table name
    private static final String TABLE_FRIENDS = "friends";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String FRIEND_KEY = "key";
    public static MyFriendsAdapter myFriendsAdapter;

    public FriendsDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + FRIEND_KEY + " TEXT)";
        db.execSQL(CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        // Create tables again
        onCreate(db);
    }

    // Adding new friend
    public void addFriend(String key) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_KEY, key); // Friend Key

        // Inserting Row
        db.insert(TABLE_FRIENDS, null, values);
        db.close(); // Closing database connection
        myFriendsAdapter.init();
    }

    // Getting single friend
    public String getFriend(String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FRIENDS, new String[]{KEY_ID,
                        FRIEND_KEY}, FRIEND_KEY + "=?",
                new String[]{key}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String friend = cursor.getString(1);
        myFriendsAdapter.init();
        // return friend
        return friend;
    }

    // Getting All friends
    public List<String> getAllFriends() {
        List<String> friendsList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FRIENDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String friend = cursor.getString(1);
                // Adding contact to list
                friendsList.add(friend);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return friends list
        return friendsList;
    }

    // Getting friends Count
    public int getFriendsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FRIENDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    // Deleting single friend
    public void deleteFriend(String friend) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FRIENDS, FRIEND_KEY + " = ?",
                new String[]{String.valueOf(friend)});
        db.close();
        myFriendsAdapter.init();
    }

    public void deleteAllFriends() {
        List<String> list = getAllFriends();
        for (String key : list) {
            deleteFriend(key);
        }
        myFriendsAdapter.init();
    }


}
