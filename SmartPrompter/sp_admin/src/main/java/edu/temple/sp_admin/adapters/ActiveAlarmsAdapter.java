package edu.temple.sp_admin.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.temple.sp_admin.R;
import edu.temple.sp_admin.fragments.AlarmViewHolder;

import edu.temple.sp_res_lib.alarms.Alarm;
import edu.temple.sp_res_lib.utils.Constants;

public class ActiveAlarmsAdapter extends RecyclerView.Adapter<AlarmViewHolder> {

    public interface AlarmDetailsListener {
        void onAlarmSelected(int position);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    private List<Alarm> mDataset;
    private AlarmDetailsListener mListener;

    public ActiveAlarmsAdapter(List<Alarm> myDataset, AlarmDetailsListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_active_alarm, parent, false);
        AlarmViewHolder vh = new AlarmViewHolder(parent.getContext(), v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, final int position) {
        Log.i(Constants.LOG_TAG, "Binding new line item view for alarm at position: "
                + position);

        Alarm currentAlarm = mDataset.get(position);
        if (currentAlarm.getStatus().equals(Alarm.STATUS.Active.toString())) {
            Log.d(Constants.LOG_TAG, "Alarm at position: " + position
                    + " is active!  Updating line item background color.");
            holder.mTextView.setBackgroundColor(Color.GREEN);
        }

        holder.mTextView.setText(currentAlarm.toString());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAlarmSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}