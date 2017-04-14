package com.strangersteam.strangers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.strangersteam.strangers.R;
import com.strangersteam.strangers.model.StrangerUser;

import java.util.List;

import static com.strangersteam.strangers.R.id.imageView;

public class AttendersListAdapter extends RecyclerView.Adapter<AttendersListAdapter.ViewHolder> {

    private Context mContext;
    private List<StrangerUser> attendersList;

    public AttendersListAdapter(Context mContext, List<StrangerUser> attendersList){
        this.mContext = mContext;
        this.attendersList = attendersList;
    }

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
        //holder.imageView.setImageResource(user.getPhotoResId());

        //Kompresja (chyba) żeby RecyclerView się nie zacinało
        Picasso.with(mContext).load(user.getPhotoResId())
                .fit()
                .centerInside()
                .error(R.drawable.temp_logo_picture)
                .placeholder(R.drawable.temp_logo_picture)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return attendersList.size();
    }

}
