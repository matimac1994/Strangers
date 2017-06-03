package com.strangersteam.strangers.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strangersteam.strangers.R;
import com.strangersteam.strangers.model.StrangersEvent;
import com.strangersteam.strangers.model.StrangersEventListItem;
import com.strangersteam.strangers.model.StrangersEventMarker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyEventsListAdapter extends RecyclerView.Adapter<MyEventsListAdapter.ViewHolder> {

    private List<StrangersEventListItem> strangersEventList;

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleTextView;
        private TextView descriptionTextView;

        private ViewHolder(LinearLayout linearLayout){
            super(linearLayout);
            titleTextView = (TextView) linearLayout.findViewById(R.id.my_events_title);
            descriptionTextView = (TextView) linearLayout.findViewById(R.id.my_events_description);
        }
    }

    public MyEventsListAdapter(List<StrangersEventListItem> strangersEventList){
        this.strangersEventList = strangersEventList;
    }

    @Override
    public MyEventsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_events_card_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(linearLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        StrangersEventListItem event = strangersEventList.get(position);
        viewHolder.titleTextView.setText(event.getTitle() + " "  + event.getUnreadMsg());
        viewHolder.descriptionTextView.setText(new SimpleDateFormat("HH:mm EEEE, dd-MMM-yyyy", new Locale("pl","PL")).format(new Date(event.getDate().getTimeInMillis())));
        viewHolder.descriptionTextView.append("\n" + event.getWhere());
    }

    @Override
    public int getItemCount() {
        return strangersEventList == null ? 0 : strangersEventList.size();
    }


}
