package com.genie.home.genieapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genie.home.genieapp.dao.DeviceDao;
import com.genie.home.genieapp.dao.GenieDatabaseHelper;
import com.genie.home.genieapp.model.Device;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DevicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DevicesFragment extends Fragment {

    private List<Device> devices = new ArrayList<>();

    private SQLiteOpenHelper genieDBHelper;
    private SQLiteDatabase db;

    private OnFragmentInteractionListener mListener;
    private DevicesAdapter adapter;
    private RecyclerView recyclerView;

    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeviceInputDialog(getContext(), new DeviceInputDialog.TextInputDialogListener() {
                    @Override
                    public void onOk(Device device) {
                        DeviceDao.addNewDevice(db, device);
                        devices.clear();
                        devices.addAll(DeviceDao.getAllDevices(db));
                        adapter.notifyDataSetChanged();
                        recyclerView.refreshDrawableState();
                    }

                    @Override
                    public void onCancel() {
                    }
                }).setTitle("Add a device").show();
            }
        });

        genieDBHelper = new GenieDatabaseHelper(view.getContext());
        db = genieDBHelper.getWritableDatabase();
        devices.addAll(DeviceDao.getAllDevices(db));

        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new DevicesAdapter(view.getContext(), devices);

        RecyclerView.LayoutManager mLayoutManager =
                new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
