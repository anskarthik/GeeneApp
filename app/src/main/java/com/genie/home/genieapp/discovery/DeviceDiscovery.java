package com.genie.home.genieapp.discovery;

import android.util.Log;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.NetworkDevice;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DeviceDiscovery {

    public static final int PORT = 54323;
    public static final String MULTICAST_ADDR = "233.255.255.255";

    private MulticastReceiver discoveryThread;

    private static final DeviceDiscovery longRunningInstance = new DeviceDiscovery(null);
    private static Cache<String, NetworkDevice> discoveredDevices = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    static {
        longRunningInstance.start();
    }

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

    public static NetworkDevice getDevice(String macId) {
        return discoveredDevices.getIfPresent(macId);
    }

    public static Map<String, NetworkDevice> getDevices() {
        return discoveredDevices.asMap();
    }

    public interface DeviceDiscoveryListener {
        void onException(Exception e);

        void onDeviceDiscovered(NetworkDevice device);
    }

    public class MulticastReceiver extends Thread {
        private static final int SOCKET_TIMEOUT = 10 * 1000;
        private static final int SOCKET_ERROR_TIMEOUT = 5 * 1000;

        private final DeviceDiscoveryListener listener;
        private volatile boolean receiving = true;

        public MulticastReceiver(DeviceDiscoveryListener listener) {
            this.listener = listener;
        }

        public void run() {
            while (receiving) {
                byte[] buf = new byte[1024];
                MulticastSocket socket = null;
                InetAddress group = null;
                try {
                    group = InetAddress.getByName(MULTICAST_ADDR);
                    socket = new MulticastSocket(PORT);
                    socket.setSoTimeout(SOCKET_TIMEOUT);
                    socket.joinGroup(group);

                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    while (receiving) {
                        if (socket.isClosed()) {
                            throw new SocketException("Socket is closed");
                        }

                        socket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        String[] tokens = received.split(",");
                        if (tokens[0].equalsIgnoreCase("GenieDevice")) {
                            NetworkDevice device = new NetworkDevice(packet.getAddress().getHostAddress(),
                                    Integer.valueOf(tokens[3].trim()), tokens[2].trim(), "");
                            device.setDeviceType(Device.DeviceType.valueOf(tokens[1].trim().toUpperCase()));

                            discoveredDevices.put(device.getMacId(), device);
                            if (listener != null) {
                                listener.onDeviceDiscovered(device);
                            }
                        }

                        packet.setLength(buf.length);
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "IOException raised", e);
                    if (listener != null) {
                        listener.onException(e);
                    }
                } finally {
                    if (socket != null) {
                        try {
                            socket.leaveGroup(group);
                            socket.close();
                        } catch (Exception ignored) {
                        }
                    }
                }

                try {
                    Thread.sleep(SOCKET_ERROR_TIMEOUT);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public void stopReceiving() {
            receiving = false;
        }
    }

}
