package com.genie.home.genieapp.common;

import com.genie.home.genieapp.discovery.DeviceDiscovery;
import com.genie.home.genieapp.model.NetworkDevice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpHelper {

    private static final int SOCKET_TIMEOUT = 5 * 1000;
    private static final int SOCKET_CONN_TIMEOUT = 5 * 1000;

    public static void sendDataToDevice(final String macId, final String data, final TcpResponseListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket clientSocket = null;
                try {
                    NetworkDevice device = DeviceDiscovery.getDevice(macId);
                    if (device == null) {

                        throw new IOException("Device heartbeat not received on the network yet, try again later");
                    }

                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(device.getAddress(), device.getPort()),
                            SOCKET_CONN_TIMEOUT);
                    clientSocket.setSoTimeout(SOCKET_TIMEOUT);

                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    outToServer.write(data.getBytes("UTF-8"));
                    outToServer.flush();

                    StringBuilder sb = new StringBuilder();
                    int c;
                    while ((c = inFromServer.read()) != -1) sb.append((char) c);
                    String response = sb.toString();
                    listener.onResponse(response);

                } catch (IOException e) {
                    listener.onException(e);
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }).start();
    }

    public interface TcpResponseListener {
        boolean onResponse(String response);

        void onException(Exception e);
    }
}
