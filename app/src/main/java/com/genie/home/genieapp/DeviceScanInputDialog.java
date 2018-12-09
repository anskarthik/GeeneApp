package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.genie.home.genieapp.discovery.DeviceDiscovery;
import com.genie.home.genieapp.model.NetworkDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceScanInputDialog extends AlertDialog.Builder {

    private final Handler handler;
    private final List<NetworkDevice> scanDeviceList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeviceScanAdapter deviceScanAdapter;
    private List<String> existingMacIds;
    private DeviceDiscovery deviceDiscovery;

    private AlertDialog alertDialog;

    public DeviceScanInputDialog(Context context, List existingMacIds, final SelectionListener listener) {
        super(context);

        this.existingMacIds = existingMacIds;
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.device_scan_input_dialog, null);
        setView(dialogLayout);

        recyclerView = dialogLayout.findViewById(R.id.recycler_view);
        handler = new Handler(context.getMainLooper());

        deviceScanAdapter = new DeviceScanAdapter(getContext(), scanDeviceList, new DeviceScanAdapter.DeviceSelectListener() {
            @Override
            public void onDeviceSelected(NetworkDevice device) {
                alertDialog.dismiss();
                if (listener != null) {
                    listener.onOk(device);
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager =
                new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(deviceScanAdapter);
    }

    @Override
    public AlertDialog show() {
        alertDialog = super.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        deviceDiscovery = new DeviceDiscovery(new DeviceDiscovery.DeviceDiscoveryListener() {
            Map<String, NetworkDevice> deviceMap = new ConcurrentHashMap<>();

            @Override
            public void onException(Exception e) {
                Log.e(this.getClass().getName(), "Exception in deviceDiscovery", e);
            }

            @Override
            public void onDeviceDiscovered(NetworkDevice device) {
                if (deviceMap.containsKey(device.getMacId()) || existingMacIds.contains(device.getMacId())) {
                    return;
                }
                deviceMap.put(device.getMacId(), device);
                scanDeviceList.add(device);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        deviceScanAdapter.notifyDataSetChanged();
                        recyclerView.refreshDrawableState();
                    }
                });
            }
        });
        deviceDiscovery.start();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                deviceDiscovery.stop();
            }
        });

        return alertDialog;
    }

    public interface SelectionListener {
        void onOk(NetworkDevice device);

        void onCancel();
    }
}
