package edu.temple.mci_res_lib2.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.temple.mci_res_lib2.R;
import edu.temple.mci_res_lib2.alarms.Alarm;
import edu.temple.mci_res_lib2.alarms.MCIAlarmManager;

import static edu.temple.mci_res_lib2.utils.Constants.INTENT_PARAM_ALARM_ID;

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<Alarm> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Alarm item = (Alarm) view.getTag();
            Context context = view.getContext();

            Intent intent = MCIAlarmManager.getIntentForAlarmStatus(context, item.getId());
            intent.putExtra(INTENT_PARAM_ALARM_ID, item.getId());
            context.startActivity(intent);
        }
    };

    public SimpleItemRecyclerViewAdapter(List<Alarm> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).getPosition());
        holder.mContentView.setText(mValues.get(position).toString());

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}