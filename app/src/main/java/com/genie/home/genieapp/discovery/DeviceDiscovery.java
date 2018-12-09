package com.genie.home.genieapp.discovery;

import android.util.Log;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.NetworkDevice;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceDiscovery {

    public static final int PORT = 54323;
    public static final String MULTICAST_ADDR = "233.255.255.255";

    private MulticastReceiver discoveryThread;

    private static Map<String, NetworkDevice> discoveredDevices = new ConcurrentHashMap<>();

    private static final DeviceDiscoveryListener longRunningListener;
    private static final DeviceDiscovery longRunningInstance;

    static {
        longRunningListener = new DeviceDiscoveryListener() {
            @Override
            public void onException(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            longRunningInstance.start();
                        } catch (InterruptedException ignored) {
                        }
                    }
                }).start();
            }

            @Override
            public void onDeviceDiscovered(NetworkDevice device) {
            }
        };

        longRunningInstance = new DeviceDiscovery(longRunningListener);
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
        return discoveredDevices.get(macId);
    }

    public static HashMap<String, NetworkDevice> getDevices() {
        return new HashMap<>(discoveredDevices);
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
                                Integer.valueOf(tokens[3].trim()), tokens[2].trim(), "");
                        device.setDeviceType(Device.DeviceType.valueOf(tokens[1].trim().toUpperCase()));

                        discoveredDevices.put(device.getMacId(), device);
                        listener.onDeviceDiscovered(device);
                    }

                    packet.setLength(buf.length);
                }
                socket.leaveGroup(group);
                socket.close();
            } catch (Exception e) {
                listener.onException(e);
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }

        public void stopReceiving() {
            receiving = false;
        }
    }

}
