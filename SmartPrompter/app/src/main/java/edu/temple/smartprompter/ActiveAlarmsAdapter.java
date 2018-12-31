package edu.temple.smartprompter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
                .inflate(R.layout.active_alarm_item, parent, false);
        AlarmViewHolder vh = new AlarmViewHolder(parent.getContext(), v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, final int position) {
        holder.mTextView.setText(mDataset.get(position).toString());
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