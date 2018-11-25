package com.genie.home.genieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.genie.home.genieapp.model.Room;

import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder> {

    public Context mContext;
    public List<Room> rooms;

    public RoomsAdapter(Context mContext, List<Room> rooms) {
        this.mContext = mContext;
        this.rooms = rooms;
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
        myViewHolder.title.setText(room.getRoomName());
        myViewHolder.count.setText(room.getDevices().size() + " devices");
        myViewHolder.imageView.setImageResource(room.getRoomType().getIconResource());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView count;
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            count = itemView.findViewById(R.id.count);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
