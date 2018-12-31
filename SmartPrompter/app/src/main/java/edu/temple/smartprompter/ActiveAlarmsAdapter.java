package edu.temple.smartprompter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
        Alarm currentAlarm = mDataset.get(position);
        if (currentAlarm.getStatus().equals(Alarm.STATUS.Active)) {
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