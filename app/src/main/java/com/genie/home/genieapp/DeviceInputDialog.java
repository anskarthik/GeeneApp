package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.genie.home.genieapp.model.Device;

public class DeviceInputDialog extends AlertDialog.Builder implements AdapterView.OnItemSelectedListener {

    private TextView textView;
    private Spinner spinnerView;
    private String deviceType;

    public DeviceInputDialog(Context context, final TextInputDialogListener listener) {
        super(context);

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.device_input_dialog, null);
        setView(dialogLayout);

        textView = dialogLayout.findViewById(R.id.inp_text_view);
        spinnerView = dialogLayout.findViewById(R.id.device_type_spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{"Light", "Fan"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(spinnerAdapter);

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    Device device = new Device(String.valueOf(textView.getText()));
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deviceType = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
