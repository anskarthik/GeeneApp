package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.genie.home.genieapp.dao.DeviceDao;
import com.genie.home.genieapp.dao.GenieDatabaseHelper;
import com.genie.home.genieapp.dao.RoomDao;
import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.NetworkDevice;
import com.genie.home.genieapp.model.Room;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DevicesFragment extends Fragment {

    private static final List<Device> devices = new ArrayList<>();

    private SQLiteOpenHelper genieDBHelper;
    private static SQLiteDatabase db;

    private OnFragmentInteractionListener mListener;
    private static DevicesAdapter adapter;
    private static RecyclerView recyclerView;

    public DevicesFragment() {
        // Required empty public constructor
    }

    public static void onDeviceTableChanged() {
        devices.clear();
        devices.addAll(DeviceDao.getAllDevices(db));
        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> existingMacIds = new ArrayList<>();
                for (Device device : devices) {
                    existingMacIds.add(device.getMacId());
                }
                new DeviceScanInputDialog(getContext(), existingMacIds, new DeviceScanInputDialog.SelectionListener() {
                    @Override
                    public void onOk(NetworkDevice networkDevice) {
                        new DeviceInputDialog(getContext(), networkDevice, new DeviceInputDialog.TextInputDialogListener() {
                            @Override
                            public void onOk(Device device) {
                                DeviceDao.addNewDevice(db, device);
                                onDeviceTableChanged();
                            }

                            @Override
                            public void onCancel() {
                            }
                        }).setTitle("Name your device").show();
                    }

                    @Override
                    public void onCancel() {
                    }
                }).setTitle("New network devices").show();
            }
        });

        genieDBHelper = new GenieDatabaseHelper(view.getContext());
        db = genieDBHelper.getWritableDatabase();

        DevicesAdapter.DeviceSelectListener deviceSelectListener = new DevicesAdapter.DeviceSelectListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item, final Device device) {
                switch (item.getItemId()) {
                    case R.id.action_add_to_room:
                        List<Room> rooms = RoomDao.getAllRooms(db);
                        List<String> roomNames = new ArrayList<>();
                        for (Room room : rooms) {
                            roomNames.add(room.getRoomName());
                        }
                        new RoomSelectionDialog(getContext(), new RoomSelectionDialog.RoomSelectionListener() {
                            @Override
                            public void onSelection(String roomName) {
                                DeviceDao.updateRoom(db, device, roomName);
                                onDeviceTableChanged();
                                RoomsFragment.onRoomTableUpdated();
                            }
                        }, roomNames).show();
                        return true;
                    case R.id.action_remove_from_room:
                        new AlertDialog.Builder(getContext()).setTitle("Are you sure?")
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeviceDao.updateRoom(db, device, null);
                                        onDeviceTableChanged();
                                        RoomsFragment.onRoomTableUpdated();
                                    }
                                }).show();
                        return true;
                    case R.id.action_delete_device:
                        new AlertDialog.Builder(getContext()).setTitle("Are you sure?")
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeviceDao.removeDevice(db, device);
                                        onDeviceTableChanged();
                                        RoomsFragment.onRoomTableUpdated();
                                    }
                                }).show();
                        return true;
                    default:
                        return true;
                }
            }

            @Override
            public void onDeviceClick(Device device) {
                new SimpleDeviceActionDialog(getContext(), device)
                        .setTitle(device.getDeviceType().name() + ": " + device.getName())
                        .show();
            }
        };

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new DevicesAdapter(view.getContext(), devices, deviceSelectListener);

        RecyclerView.LayoutManager mLayoutManager =
                new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        onDeviceTableChanged();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
