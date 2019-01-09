package com.genie.home.genieapp.mqtt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static android.support.constraint.Constraints.TAG;

public class MqttHelper {

    private static MqttConnectOptions getMqttConnectionOption(String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        if (username != null && password != null) {
            mqttConnectOptions.setUserName("username");
            mqttConnectOptions.setPassword("password".toCharArray());
        }

        return mqttConnectOptions;
    }

    public static MqttAndroidClient getMqttClient(Context context, String brokerUrl,
                                                  String clientId, String username, String password) throws MqttException {

        MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);

        IMqttToken token = mqttAndroidClient.connect(getMqttConnectionOption(username, password));
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "Success");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Failure " + exception.toString());
            }
        });

        return mqttAndroidClient;
    }

    public static void publishMessage(@NonNull MqttAndroidClient client,
                                      @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {

        byte[] payload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(payload);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }

    public void disconnect(@NonNull MqttAndroidClient client) throws MqttException {

        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Successfully disconnected");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(TAG, "Failed to disconnected " + throwable.toString());
            }
        });
    }

}