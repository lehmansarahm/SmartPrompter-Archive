package edu.temple.smartprompter_v3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.temple.smartprompter_v3.R;
import edu.temple.smartprompter_v3.data.Alarm;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    public interface AlarmSelectionListener {
        void OnAlarmSelected(Alarm alarm);
    }

    private final List<Alarm> mValues;
    private final AlarmSelectionListener mListener;

    public AlarmRecyclerViewAdapter(List<Alarm> items,
                                    AlarmSelectionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mContentView.setText(mValues.get(position).toString());
        holder.mDateTimeView.setText(mValues.get(position).getAlarmDateTimeString());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.OnAlarmSelected(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDateTimeView;
        public Alarm mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mDateTimeView = view.findViewById(R.id.dateTime);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
