package com.genie.home.genieapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomDao {

    public static List<Room> getAllRooms(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query("room", new String[]{"name", "type"},
                    null, null, null, null, "_id ASC");

            List<Room> rooms = new ArrayList<>();
            while (cursor.moveToNext()) {
                Room room = new Room(cursor.getString(0));
                room.setRoomType(Room.RoomType.valueOf(cursor.getString(1)));

                List<Device> devices = DeviceDao.getDevicesInARoom(db, room.getRoomName());
                for (Device device : devices) {
                    room.addDevice(device);
                }

                rooms.add(room);
            }

            return rooms;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void addRoom(SQLiteDatabase db, Room room) {
        ContentValues contentValue = new ContentValues();
        contentValue.put("name", room.getRoomName());
        contentValue.put("type", room.getRoomType().name());

        for (Device device : room.getDevices()) {
            DeviceDao.updateRoom(db, device, room.getRoomName());
        }
        db.insert("room", null, contentValue);
    }
}
