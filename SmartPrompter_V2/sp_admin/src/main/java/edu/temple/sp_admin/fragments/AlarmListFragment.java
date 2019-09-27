package edu.temple.sp_admin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.adapters.AlarmRecyclerViewAdapter;
import edu.temple.sp_res_lib.obj.Alarm;

public class AlarmListFragment extends Fragment {

    private static final String ARG_ITEM_LIST = "item_list";
    private static final String ARG_HEADER_TEXT = "header_text";

    private OnListItemSelectionListener mListener;
    private List<Alarm> mItems;
    private String mHeaderText;

    public AlarmListFragment() {
    }

    public static AlarmListFragment newInstance(ArrayList<Alarm> items, String headerText) {
        AlarmListFragment fragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEM_LIST, items);
        args.putString(ARG_HEADER_TEXT, headerText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItems = getArguments().getParcelableArrayList(ARG_ITEM_LIST);
            mHeaderText = getArguments().getString(ARG_HEADER_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new AlarmRecyclerViewAdapter(mItems, mListener));

        TextView headerText = view.findViewById(R.id.alarm_list_header);
        headerText.setText(mHeaderText);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListItemSelectionListener) {
            mListener = (OnListItemSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListItemSelectionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListItemSelectionListener {
        void OnListItemSelected(Alarm item);
    }

}