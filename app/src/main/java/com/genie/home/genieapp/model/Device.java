package com.genie.home.genieapp.model;

import java.io.Serializable;

public class Device implements Serializable {

    private long macId;
    private String deviceType;

    public Device(long macId) {
        this.macId = macId;
    }

    public long getMacId() {
        return macId;
    }

    public void setMacId(long macId) {
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

        return getMacId() == device.getMacId();
    }

    @Override
    public int hashCode() {
        return (int) (getMacId() ^ (getMacId() >>> 32));
    }
}
