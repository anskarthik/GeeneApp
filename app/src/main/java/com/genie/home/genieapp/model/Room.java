package com.genie.home.genieapp.model;

import java.util.HashSet;
import java.util.Set;

public class Room {

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
        DRAWING_ROOM("Dining room"), DINING_ROOM("Dining room"),
        BEDROOM("Bed room"), KITCHEN("Kitchen"), BATHROOM("Bathroom");

        private String name;

        RoomType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
