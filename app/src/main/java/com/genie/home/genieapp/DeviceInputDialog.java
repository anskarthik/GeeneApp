package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.genie.home.genieapp.model.Device;

public class DeviceInputDialog extends AlertDialog.Builder {

    private TextView macIdTextView;
    private TextView nameTextView;
    private Spinner spinnerView;
    private Device.DeviceType deviceType;

    public DeviceInputDialog(Context context, final TextInputDialogListener listener) {
        this(context, null, listener);
    }

    public DeviceInputDialog(Context context, Device defaultDevice, final TextInputDialogListener listener) {
        super(context);

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.device_input_dialog, null);
        setView(dialogLayout);

        macIdTextView = dialogLayout.findViewById(R.id.inp_mac_id_text_view);
        nameTextView = dialogLayout.findViewById(R.id.inp_name_text_view);
        spinnerView = dialogLayout.findViewById(R.id.device_type_spinner);

        final ArrayAdapter<Device.DeviceType> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Device.DeviceType.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(spinnerAdapter);

        if (defaultDevice != null) {
            macIdTextView.setText(defaultDevice.getMacId());
            macIdTextView.setEnabled(false);
            spinnerView.setSelection(
                    spinnerAdapter.getPosition(defaultDevice.getDeviceType()));
            spinnerView.setEnabled(false);
        }

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    Device device = new Device(String.valueOf(macIdTextView.getText()),
                            String.valueOf(nameTextView.getText()));
                    deviceType = (Device.DeviceType) spinnerView.getSelectedItem();
                    device.setDeviceType(deviceType);
                    listener.onOk(device);
                }
            }
        });

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onCancel();
                }
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

    public interface TextInputDialogListener {
        void onOk(Device device);

        void onCancel();
    }
}
