package com.genie.home.genieapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.Room;

public class GenieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "genie";
    private static final int DB_VERSION = 1;

    public GenieDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE room_type(\n" +
                "    _id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "    type TEXT, \n" +
                "    CONSTRAINT room_type_unique UNIQUE (type));");
        for (Room.RoomType type : Room.RoomType.values()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("type", type.name());
            db.insert("room_type", null, contentValue);
        }

        db.execSQL("CREATE TABLE device_type(\n" +
                "    _id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "    type TEXT, \n" +
                "    CONSTRAINT device_type_unique UNIQUE (type));");
        for (Device.DeviceType type : Device.DeviceType.values()) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("type", type.name());
            db.insert("device_type", null, contentValue);
        }

        db.execSQL("CREATE TABLE room (\n" +
                "    _id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "    name TEXT, \n" +
                "    type TEXT,\n" +
                "    CONSTRAINT room_name_unique UNIQUE (name),\n" +
                "    FOREIGN KEY(type) REFERENCES room_type(type));");
        db.execSQL("CREATE TABLE device (\n" +
                "    mac_id TEXT PRIMARY KEY, \n" +
                "    name TEXT,\n" +
                "    room_name TEXT, \n" +
                "    type TEXT,\n" +
                "    CONSTRAINT device_name_unique UNIQUE (name),\n" +
                "    FOREIGN KEY(room_name) REFERENCES room(type),\n" +
                "    FOREIGN KEY(type) REFERENCES device_type(type));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
