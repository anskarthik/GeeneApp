package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.genie.home.genieapp.common.TcpHelper;
import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.mqtt.IotService;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class SimpleDeviceActionDialog extends AlertDialog.Builder {

    private Device device;
    private Handler handler;
    private MqttAndroidClient mqttClient;

    public SimpleDeviceActionDialog(final Context context, final Device device) {
        super(context);

//        try {
//            String clientId = MqttClient.generateClientId();
//            mqttClient = MqttHelper.getMqttClient(context, "tcp://192.168.1.4:1883", clientId, null, null);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }

        this.device = device;

        handler = new Handler(context.getMainLooper());

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.simple_device_action_dialog, null);
        setView(dialogLayout);

        Button onBtn = dialogLayout.findViewById(R.id.btn_on);
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TcpHelper.sendDataToDevice(
                        device.getMacId(),
                        "on",
                        new TcpHelper.TcpResponseListener() {
                            @Override
                            public boolean onResponse(String response) {
                                if (response.toLowerCase().contains("successful")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,
                                                    "device turn on successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return !response.trim().isEmpty();
                            }

                            @Override
                            public void onException(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device turn on failed - " +
                                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
//                try {
//                    MqttHelper.publishMessage(mqttClient, "on", 1, device.getMacId());
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                IotService.publishMessage(device.getMacId(), "on",
                        new MyRunnable<String>() {
                            @Override
                            public void run(String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device turn on published", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<String>() {
                            @Override
                            public void run(final String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<Exception>() {
                            @Override
                            public void run(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });
        Button offBtn = dialogLayout.findViewById(R.id.btn_off);
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TcpHelper.sendDataToDevice(
                        device.getMacId(),
                        "off",
                        new TcpHelper.TcpResponseListener() {
                            @Override
                            public boolean onResponse(String response) {
                                if (response.toLowerCase().contains("successful")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,
                                                    "device turn off successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return !response.trim().isEmpty();
                            }

                            @Override
                            public void onException(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device turn off failed - " +
                                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

//                try {
//                    MqttHelper.publishMessage(mqttClient, "off", 1, device.getMacId());
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                IotService.publishMessage(device.getMacId(), "off",
                        new MyRunnable<String>() {
                            @Override
                            public void run(String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device turn off published", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<String>() {
                            @Override
                            public void run(final String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<Exception>() {
                            @Override
                            public void run(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });
        Button toggleBtn = dialogLayout.findViewById(R.id.btn_toggle);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TcpHelper.sendDataToDevice(
                        device.getMacId(),
                        "toggle",
                        new TcpHelper.TcpResponseListener() {
                            @Override
                            public boolean onResponse(String response) {
                                if (response.toLowerCase().contains("successful")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context,
                                                    "device toggle successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                return !response.trim().isEmpty();
                            }

                            @Override
                            public void onException(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device toggle failed - " +
                                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
//                try {
//                    MqttHelper.publishMessage(mqttClient, "toggle", 1, device.getMacId());
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                IotService.publishMessage(device.getMacId(), "toggle",
                        new MyRunnable<String>() {
                            @Override
                            public void run(String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,
                                                "device toggle published", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<String>() {
                            @Override
                            public void run(final String s) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },
                        new MyRunnable<Exception>() {
                            @Override
                            public void run(final Exception e) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    public AlertDialog show() {
        AlertDialog alertDialog = super.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        return alertDialog;
    }
}
