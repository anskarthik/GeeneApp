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

import com.genie.home.genieapp.model.Room;

public class RoomInputDialog extends AlertDialog.Builder {

    private TextView textView;
    private Spinner spinnerView;
    private Room.RoomType roomType;

    public RoomInputDialog(Context context, final TextInputDialogListener listener) {
        super(context);

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.room_input_dialog, null);
        setView(dialogLayout);

        textView = dialogLayout.findViewById(R.id.inp_text_view);
        spinnerView = dialogLayout.findViewById(R.id.room_type_spinner);

        ArrayAdapter<Room.RoomType> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Room.RoomType.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(spinnerAdapter);

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    Room room = new Room(String.valueOf(textView.getText()));
                    roomType = (Room.RoomType) spinnerView.getSelectedItem();
                    room.setRoomType(roomType);
                    listener.onOk(room);
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
        void onOk(Room room);

        void onCancel();
    }
}
