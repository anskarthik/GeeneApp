package com.genie.home.genieapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.genie.home.genieapp.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceDao {

    public static List<Device> getAllDevices(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query("device", new String[]{"mac_id", "name", "type", "room_name"},
                    null, null, null, null, null);

            List<Device> devices = new ArrayList<>();
            while (cursor.moveToNext()) {
                Device device = new Device(cursor.getString(0), cursor.getString(1));
                device.setDeviceType(Device.DeviceType.valueOf(cursor.getString(2)));
                device.setRoomName(cursor.getString(3));

                devices.add(device);
            }

            return devices;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static List<Device> getDevicesInARoom(SQLiteDatabase db, String roomName) {
        Cursor cursor = null;
        try {
            cursor = db.query("device", new String[]{"mac_id", "name", "type"},
                    "room_name=?", new String[]{roomName}, null, null, null);

            List<Device> devices = new ArrayList<>();
            while (cursor.moveToNext()) {
                Device device = new Device(cursor.getString(0), cursor.getString(1));
                device.setDeviceType(Device.DeviceType.valueOf(cursor.getString(2)));

                devices.add(device);
            }

            return devices;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Device getByMacId(SQLiteDatabase db, String macId) {
        Cursor cursor = null;
        try {
            cursor = db.query("device", new String[]{"mac_id", "name", "type"},
                    "mac_id=?", new String[]{macId}, null, null, null);

            if (cursor.moveToFirst()) {
                Device device = new Device(cursor.getString(0), cursor.getString(1));
                device.setDeviceType(Device.DeviceType.valueOf(cursor.getString(2)));

                return device;
            }

            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Device getByName(SQLiteDatabase db, String deviceName) {
        Cursor cursor = null;
        try {
            cursor = db.query("device", new String[]{"mac_id", "name", "type"},
                    "name=?", new String[]{deviceName}, null, null, null);

            if (cursor.moveToFirst()) {
                Device device = new Device(cursor.getString(0), cursor.getString(1));
                device.setDeviceType(Device.DeviceType.valueOf(cursor.getString(2)));

                return device;
            }

            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void addNewDevice(SQLiteDatabase db, Device device) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("mac_id", device.getMacId());
        contentValues.put("name", device.getName());
        contentValues.put("type", device.getDeviceType().name());

        db.insert("device", null, contentValues);
    }

    public static void updateRoom(SQLiteDatabase db, Device device, String roomName) {
        if (getByMacId(db, device.getMacId()) == null) {
            addNewDevice(db, device);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("room_name", roomName);
        db.update("device", contentValues, "mac_id=?", new String[]{device.getMacId()});
    }

    public static void removeDevice(SQLiteDatabase db, Device device) {
        db.delete("device", "mac_id=?", new String[]{device.getMacId()});
    }
}
