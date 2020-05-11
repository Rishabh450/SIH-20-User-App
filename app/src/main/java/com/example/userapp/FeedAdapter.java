package com.example.userapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context mContext;
    List<Feed> mFeed;

    public FeedAdapter(Context mContext, List<Feed> mFeed) {
        this.mContext = mContext;
        this.mFeed = mFeed;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.feed_layout, parent, false);

        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Feed feed = mFeed.get(position);

        holder.text.setText(feed.getContent());

        if(feed.getImageURL().equals("")){

            holder.imageView.setVisibility(View.GONE);

        }

        Glide.with(mContext).load(feed.getImageURL()).into(holder.imageView);


        long milliseconds = Long.parseLong(feed.getTime());
        String simpleDateFormat = DateFormat.getDateTimeInstance().format(milliseconds);
        holder.date.setText(simpleDateFormat);

//        holder.date.setText(feed.getTime());

    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView text, date;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.feedText);
            date = itemView.findViewById(R.id.feedDate);
            imageView = itemView.findViewById(R.id.feedImage);

        }

    }

}
