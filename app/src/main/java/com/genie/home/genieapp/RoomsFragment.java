package com.genie.home.genieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.genie.home.genieapp.dao.DeviceDao;
import com.genie.home.genieapp.dao.GenieDatabaseHelper;
import com.genie.home.genieapp.dao.RoomDao;
import com.genie.home.genieapp.model.Device;
import com.genie.home.genieapp.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RoomsFragment extends Fragment {

    private static final List<Room> rooms = new ArrayList<>();

    private static SQLiteOpenHelper genieDBHelper;
    private static SQLiteDatabase db;

    private OnFragmentInteractionListener mListener;
    private static RoomsAdapter adapter;
    private static RecyclerView recyclerView;

    public RoomsFragment() {
        // Required empty public constructor
    }

    public static void onRoomTableUpdated() {
        rooms.clear();
        rooms.addAll(RoomDao.getAllRooms(db));

        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
    }

    private int getSpanCount(Context context, int dpCardWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / dpCardWidth);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
        if (db != null) {
            db.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RoomInputDialog(getContext(), new RoomInputDialog.TextInputDialogListener() {
                    @Override
                    public void onOk(Room room) {
                        RoomDao.addRoom(db, room);
                        onRoomTableUpdated();
                    }

                    @Override
                    public void onCancel() {
                    }
                }).setTitle("Add a room").show();
            }
        });

        genieDBHelper = new GenieDatabaseHelper(view.getContext());
        db = genieDBHelper.getWritableDatabase();

        RoomsAdapter.RoomAdapterListener roomAdapterListener = new RoomsAdapter.RoomAdapterListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item, final Room room) {
                switch (item.getItemId()) {
                    case R.id.action_add_device:
                        List<Device> devices = DeviceDao.getAllDevices(db);
                        final Map<String, Device> deviceMap = new HashMap<>();
                        List<String> deviceNames = new ArrayList<>();
                        boolean[] deviceInRoom = new boolean[devices.size()];

                        for (int i = 0; i < devices.size(); i++) {
                            Device device = devices.get(i);
                            deviceMap.put(device.getName(), device);
                            deviceNames.add(device.getName());
                            deviceInRoom[i] = room.getRoomName().equals(device.getRoomName());
                        }

                        new DeviceSelectionDialog(getContext(), new DeviceSelectionDialog.DeviceSelectionListener() {
                            @Override
                            public void onSelected(List<String> deviceNamesToAdd, List<String> deviceNamesToRemove) {
                                for (String deviceName : deviceNamesToAdd) {
                                    DeviceDao.updateRoom(db, deviceMap.get(deviceName), room.getRoomName());
                                }
                                for (String deviceName : deviceNamesToRemove) {
                                    DeviceDao.updateRoom(db, deviceMap.get(deviceName), null);
                                }
                                onRoomTableUpdated();
                                DevicesFragment.onDeviceTableChanged();
                            }
                        }, deviceNames, deviceInRoom).show();
                        return true;
                    case R.id.action_delete_room:
                        new AlertDialog.Builder(getContext()).setTitle("Are you sure?")
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RoomDao.deleteRoom(db, room);
                                        onRoomTableUpdated();
                                        DevicesFragment.onDeviceTableChanged();
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
        adapter = new RoomsAdapter(view.getContext(), rooms, roomAdapterListener);

        int spanCount = getSpanCount(view.getContext(), 180);
        RecyclerView.LayoutManager mLayoutManager =
                new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        onRoomTableUpdated();

        return view;
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;

                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
