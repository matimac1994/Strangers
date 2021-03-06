package com.strangersteam.strangers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strangersteam.strangers.R;
import com.strangersteam.strangers.ShowEventActivity;
import com.strangersteam.strangers.model.StrangersEventListItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UserEventsListAdapter extends RecyclerView.Adapter<UserEventsListAdapter.ViewHolder> {

    private List<StrangersEventListItem> strangersEventList;
    private Context mContext;

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView infoTextView;
        private TextView unreadedMsgsTextView;
        private LinearLayout unreaedMsgsTable;

        private ViewHolder(LinearLayout linearLayout){
            super(linearLayout);
            titleTextView = (TextView) linearLayout.findViewById(R.id.my_events_title);
            infoTextView = (TextView) linearLayout.findViewById(R.id.my_events_description);
            unreadedMsgsTextView = (TextView) linearLayout.findViewById(R.id.my_events_unreaded_msgs);
            unreaedMsgsTable = (LinearLayout) linearLayout.findViewById(R.id.my_events_unreaded_msgs_table);
        }

    }

    public UserEventsListAdapter(Context context, List<StrangersEventListItem> strangersEventList){
        mContext = context;
        this.strangersEventList = strangersEventList;
    }

    @Override
    public UserEventsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_events_card_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final StrangersEventListItem event = strangersEventList.get(position);
        viewHolder.titleTextView.setText(event.getTitle());
        viewHolder.infoTextView.setText(new SimpleDateFormat("dd-MMM-yyyy, \nHH:mm EEEE, ", new Locale("pl","PL")).format(new Date(event.getDate().getTimeInMillis())));
        viewHolder.infoTextView.append("\n" + event.getWhere());
        viewHolder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowEventActivity.class);
                intent.putExtra(ShowEventActivity.EVENT_ID, event.getId());
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });

        if(event.getUnreadMsg()==0){
            viewHolder.unreaedMsgsTable.setVisibility(View.GONE);
        }else{
            viewHolder.unreaedMsgsTable.setVisibility(View.VISIBLE);
            viewHolder.unreadedMsgsTextView.setText(event.getUnreadMsg());
        }
    }

    @Override
    public int getItemCount() {
        return strangersEventList == null ? 0 : strangersEventList.size();
    }


}
