package com.genie.home.genieapp.model;

import android.support.annotation.NonNull;

public class NetworkDevice extends Device {

    private String address;

    private int port;

    public NetworkDevice(String address, int port, @NonNull String macId, String name) {
        super(macId, name);
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "NetworkDevice{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
