package com.genie.home.genieapp.model;

import com.genie.home.genieapp.R;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Room implements Serializable {

    private String roomName;
    private RoomType roomType;
    private Set<Device> devices;

    public Room(String roomName) {
        this.roomName = roomName;
        this.devices = new HashSet<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device device) {
        this.devices.add(device);
    }

    public void removeDevice(Device device) {
        this.devices.remove(device);
    }

    public enum RoomType {
        DRAWING_ROOM("Drawing room", R.drawable.ic_drawing_room),
        DINING_ROOM("Dining room", R.drawable.ic_dining_room),
        BEDROOM("Bedroom", R.drawable.ic_bedroom),
        KITCHEN("Kitchen", R.drawable.ic_kitchen),
        BATHROOM("Bathroom", R.drawable.ic_bathroom);

        private String name;
        private int icon;

        RoomType(String name, int icon) {
            this.name = name;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getIconResource() {
            return icon;
        }
    }
}
