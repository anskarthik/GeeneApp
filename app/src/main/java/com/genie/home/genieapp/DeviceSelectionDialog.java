package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceSelectionDialog extends AlertDialog.Builder {

    private boolean[] initialCheckedDeviceNames;
    private boolean[] finalCheckedDeviceNames;

    public DeviceSelectionDialog(Context context, final DeviceSelectionListener listener,
                                 final List<String> deviceNames, final boolean[] checkedDeviceNames) {
        super(context);
        this.initialCheckedDeviceNames = Arrays.copyOf(checkedDeviceNames, checkedDeviceNames.length);
        this.finalCheckedDeviceNames = Arrays.copyOf(checkedDeviceNames, checkedDeviceNames.length);

        setTitle("Select devices");
        setMultiChoiceItems(deviceNames.toArray(new String[0]), checkedDeviceNames,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        finalCheckedDeviceNames[which] = isChecked;
                    }
                });

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> deviceNamesToAdd = new ArrayList<>();
                List<String> deviceNamesToRemove = new ArrayList<>();

                for (int i = 0; i < deviceNames.size(); i++) {
                    if (initialCheckedDeviceNames[i] != finalCheckedDeviceNames[i]) {
                        if (finalCheckedDeviceNames[i]) {
                            deviceNamesToAdd.add(deviceNames.get(i));
                        } else {
                            deviceNamesToRemove.add(deviceNames.get(i));
                        }
                    }
                }
                listener.onSelected(deviceNamesToAdd, deviceNamesToRemove);
            }
        });

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public interface DeviceSelectionListener {
        void onSelected(List<String> deviceNamesToAdd, List<String> deviceNamesToRemove);
    }
}
