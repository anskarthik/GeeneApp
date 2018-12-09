package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Room> rooms;
    private RoomAdapterListener menuListener;

    public RoomsAdapter(Context mContext, List<Room> rooms, RoomAdapterListener menuListener) {
        this.mContext = mContext;
        this.rooms = rooms;
        this.menuListener = menuListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.room_cards_layout, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Room room = rooms.get(i);
        myViewHolder.room = room;
        myViewHolder.listener = menuListener;
        myViewHolder.title.setText(room.getRoomName());
        myViewHolder.count.setText(room.getDevices().size() + " devices");
        myViewHolder.imageView.setImageResource(room.getRoomType().getIconResource());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public interface RoomAdapterListener {
        boolean onMenuItemClick(MenuItem item, Room room);

        void onDeviceClick(Device device);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public RoomAdapterListener listener;
        public Room room;
        public TextView title;
        public TextView count;
        public ImageView imageView;
        public ImageView optionsMenu;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.count);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> deviceNames = new ArrayList<>();
                    final List<Device> devices = new ArrayList<>(room.getDevices());
                    for (Device device : devices) {
                        deviceNames.add(device.getName());
                    }

                    new AlertDialog.Builder(itemView.getContext()).setTitle("Devices")
                            .setItems(deviceNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.onDeviceClick(devices.get(which));
                                }
                            }).show();
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
                            return listener.onMenuItemClick(item, room);
                        }
                    });
                    popupMenu.inflate(R.menu.menu_room_card);
                    popupMenu.show();
                }
            });
        }
    }
}
