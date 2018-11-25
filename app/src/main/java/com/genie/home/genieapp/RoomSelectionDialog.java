package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

public class RoomSelectionDialog extends AlertDialog.Builder {

    public RoomSelectionDialog(Context context, final RoomSelectionListener listener, final List<String> roomNames) {
        super(context);

        setTitle("Select a room");
        setItems(roomNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onSelection(roomNames.get(which));
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

    public interface RoomSelectionListener {
        void onSelection(String roomName);
    }
}
