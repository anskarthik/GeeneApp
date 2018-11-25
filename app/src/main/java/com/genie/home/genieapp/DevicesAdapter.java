package com.genie.home.genieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.genie.home.genieapp.model.Device;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Device> devices;
    private DeviceMenuListener menuListener;

    public DevicesAdapter(Context mContext, List<Device> devices, DeviceMenuListener menuListener) {
        this.mContext = mContext;
        this.devices = devices;
        this.menuListener = menuListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_cards_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Device device = devices.get(i);
        myViewHolder.device = device;
        myViewHolder.menuListener = menuListener;
        myViewHolder.title.setText(device.getName());
        myViewHolder.macId.setText(String.valueOf(device.getMacId()));
        myViewHolder.roomTextView.setText("Room: " + (device.getRoomName() == null ? "" : device.getRoomName()));
        myViewHolder.imageView.setImageResource(device.getDeviceType().getIconResource());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public interface DeviceMenuListener {
        boolean onMenuItemClick(MenuItem item, Device device);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Device device;
        public TextView title;
        public TextView macId;
        public TextView roomTextView;
        public ImageView imageView;
        public ImageView optionsMenu;
        public DevicesAdapter.DeviceMenuListener menuListener;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            macId = itemView.findViewById(R.id.mac_id);
            imageView = itemView.findViewById(R.id.imageView);
            roomTextView = itemView.findViewById(R.id.room);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(),
                            device.getDeviceType() + " controls, still work in progress ...",
                            Toast.LENGTH_SHORT).show();
                }
            });

            optionsMenu = itemView.findViewById(R.id.options_menu);
            optionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), optionsMenu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            return menuListener.onMenuItemClick(item, device);
                        }
                    });
                    popupMenu.inflate(R.menu.menu_device_card);
                    popupMenu.show();
                }
            });
        }
    }
}
