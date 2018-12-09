package com.genie.home.genieapp.discovery;

import android.util.Log;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.NetworkDevice;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DeviceDiscovery {

    public static final int PORT = 54323;
    public static final String MULTICAST_ADDR = "233.255.255.255";

    private MulticastReceiver discoveryThread;

    public DeviceDiscovery(DeviceDiscoveryListener listener) {
        this.discoveryThread = new MulticastReceiver(listener);
    }

    public synchronized void start() {
        if (!discoveryThread.isAlive()) {
            discoveryThread.start();
        }
    }

    public synchronized void stop() {
        Log.i(DeviceDiscovery.class.getName(), "Stopping DeviceDiscovery");
        discoveryThread.stopReceiving();
    }

    public interface DeviceDiscoveryListener {
        void onException(Exception e);

        void onDeviceDiscovered(NetworkDevice device);
    }

    public class MulticastReceiver extends Thread {
        private final DeviceDiscoveryListener listener;
        private MulticastSocket socket = null;
        private byte[] buf = new byte[256];
        private volatile boolean receiving = true;

        public MulticastReceiver(DeviceDiscoveryListener listener) {
            this.listener = listener;
        }

        public void run() {
            try {
                socket = new MulticastSocket(PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_ADDR);
                socket.joinGroup(group);

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (receiving) {
                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (received.toLowerCase().startsWith("GenieDevice,".toLowerCase())) {
                        String[] tokens = received.split(",");
                        NetworkDevice device = new NetworkDevice(packet.getAddress().getHostAddress(),
                                tokens[2].trim(), "");
                        device.setDeviceType(Device.DeviceType.valueOf(tokens[1].trim().toUpperCase()));
                        listener.onDeviceDiscovered(device);
                    }

                    packet.setLength(buf.length);
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (Exception e) {
                listener.onException(e);
            }
        }

        public void stopReceiving() {
            receiving = false;
        }
    }

}
