package edu.temple.smartprompter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ActiveAlarmsAdapter extends RecyclerView.Adapter<AlarmViewHolder> {

    private String[] mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ActiveAlarmsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_alarm_item, parent, false);
        AlarmViewHolder vh = new AlarmViewHolder(parent.getContext(), v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.mContext,
                        "Clicked: " + holder.mTextView.getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}