package com.strangersteam.strangers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.strangersteam.strangers.R;
import com.strangersteam.strangers.model.StrangerUser;

import java.util.List;

public class AttendersListAdapter extends RecyclerView.Adapter<AttendersListAdapter.ViewHolder> {

    private List<StrangerUser> attendersList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nickTextView;

        public ViewHolder(LinearLayout l) {
            super(l);
            imageView = (ImageView) l.findViewById(R.id.attender_photo_img_view);
            nickTextView = (TextView) l.findViewById(R.id.attender_nick_tv);
        }
    }

    public AttendersListAdapter(List<StrangerUser> attendersList) {
        this.attendersList = attendersList;
    }

    @Override
    public AttendersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attender_view, parent, false);

        ViewHolder vh = new ViewHolder(l);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StrangerUser user = attendersList.get(position);
        holder.nickTextView.setText(user.getNick());
        holder.imageView.setImageResource(user.getPhotoResId());
    }

    @Override
    public int getItemCount() {
        return attendersList.size();
    }

}
