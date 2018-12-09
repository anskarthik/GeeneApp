package com.genie.home.genieapp.model;

import android.support.annotation.NonNull;

public class NetworkDevice extends Device {

    private String address;

    public NetworkDevice(String address, @NonNull String macId, String name) {
        super(macId, name);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "NetworkDevice{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
