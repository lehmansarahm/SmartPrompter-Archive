package edu.temple.smartprompter.alarms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    public Context mContext;
    public TextView mTextView;

    public AlarmViewHolder(Context ctx, TextView v) {
        super(v);
        mContext = ctx;
        mTextView = v;
    }

}