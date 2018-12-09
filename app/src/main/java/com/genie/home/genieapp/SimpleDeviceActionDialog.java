package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.genie.home.genieapp.model.Device;

public class SimpleDeviceActionDialog extends AlertDialog.Builder {

    private Device device;
    private Button toggleBtn;

    public SimpleDeviceActionDialog(final Context context, Device device) {
        super(context);

        this.device = device;

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.simple_device_action_dialog, null);
        setView(dialogLayout);

        toggleBtn = dialogLayout.findViewById(R.id.btn_toggle);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "toggling this device", Toast.LENGTH_SHORT).show();
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
