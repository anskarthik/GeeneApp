package com.genie.home.genieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.genie.home.genieapp.model.NetworkDevice;

import java.util.List;

public class DeviceScanAdapter extends RecyclerView.Adapter<DeviceScanAdapter.MyViewHolder> {

    private final DeviceSelectListener deviceSelectListener;
    private Context mContext;
    private List<NetworkDevice> devices;

    public DeviceScanAdapter(Context mContext, List<NetworkDevice> devices, DeviceSelectListener deviceSelectListener) {
        this.mContext = mContext;
        this.devices = devices;
        this.deviceSelectListener = deviceSelectListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_scan_cards_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        NetworkDevice device = devices.get(i);
        myViewHolder.deviceSelectListener = deviceSelectListener;
        myViewHolder.device = device;
        myViewHolder.macId.setText(String.valueOf(device.getMacId()));
        myViewHolder.addressView.setText("Host: " + device.getAddress());
        myViewHolder.imageView.setImageResource(device.getDeviceType().getIconResource());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public interface DeviceSelectListener {
        void onDeviceSelected(NetworkDevice device);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public NetworkDevice device;
        public TextView macId;
        public TextView addressView;
        public ImageView imageView;
        public DeviceSelectListener deviceSelectListener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            macId = itemView.findViewById(R.id.mac_id);
            imageView = itemView.findViewById(R.id.imageView);
            addressView = itemView.findViewById(R.id.address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceSelectListener.onDeviceSelected(device);
                }
            });
        }
    }
}
