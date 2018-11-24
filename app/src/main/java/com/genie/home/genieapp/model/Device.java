package com.genie.home.genieapp.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {

    private String macId;
    private String deviceType;

    public Device(@NonNull String macId) {
        this.macId = macId;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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
}
