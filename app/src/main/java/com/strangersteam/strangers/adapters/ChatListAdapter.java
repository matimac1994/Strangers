package com.strangersteam.strangers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.strangersteam.strangers.R;
import com.strangersteam.strangers.model.StrangerEventMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<StrangerEventMessage>{
    private final Context context;
    private LayoutInflater layoutInflater;
    private List<StrangerEventMessage> messages;


    public ChatListAdapter(@NonNull Context context, @NonNull List<StrangerEventMessage> messages) {
        super(context, R.layout.chat_row_layout, messages);
        super.setNotifyOnChange(true);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.messages = messages;
        //this.messages.addAll(messages);
    }

    private class ViewHolder{
        TextView user;
        TextView message;
        TextView timeStamp;
        StrangerEventMessage strangerEventMessage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StrangerEventMessage strangerEventMessage = this.messages.get(position);
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.chat_row_layout, parent,false);
            holder.user = (TextView) convertView.findViewById(R.id.chat_row_user);
            holder.message = (TextView) convertView.findViewById(R.id.chat_row_message);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.chat_row_timestamp);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.user.setText(strangerEventMessage.getUser().getNick());
        holder.message.setText(strangerEventMessage.getContent());
        holder.timeStamp.setText(formatTimeStamp(strangerEventMessage.getDate().getTimeInMillis()));
        holder.strangerEventMessage = strangerEventMessage;

        return convertView;
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    public void addMessages(List<StrangerEventMessage> newMessages){
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public static String formatTimeStamp(long timeStamp){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM, hh:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }

    public void clearMessager(){
        this.messages.clear();
        notifyDataSetChanged();
    }

    public void setMessages(List<StrangerEventMessage> eventMessages) {
        this.clearMessager();
        this.addMessages(eventMessages);
    }
}
