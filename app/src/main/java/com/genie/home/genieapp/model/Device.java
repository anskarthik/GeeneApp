package com.genie.home.genieapp.model;

import android.support.annotation.NonNull;

import com.genie.home.genieapp.R;

import java.io.Serializable;

public class Device implements Serializable {

    private String macId;
    private String name;
    private DeviceType deviceType;

    public Device(@NonNull String macId, String name) {
        this.macId = macId;
        this.name = name;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return getMacId().equals(device.getMacId());
    }

    @Override
    public int hashCode() {
        return getMacId().hashCode();
    }

    public enum DeviceType {
        LIGHT("Light", R.drawable.ic_light),
        FAN("Fan", R.drawable.ic_fan),
        AIR_CONDITIONER("Air conditioner", R.drawable.ic_air_conditioner),
        EXHAUST("Exhaust", R.drawable.ic_exhaust);

        private String name;
        private int icon;

        DeviceType(String name, int icon) {
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
