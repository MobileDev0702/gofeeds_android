package com.stackrage.gofeds.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.stackrage.gofeds.FeedDetailActivity;
import com.stackrage.gofeds.R;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> photoList;
    private ArrayList<String> titleList;
    private ArrayList<String> timeList;
    private ArrayList<String> urlList;

    public FeedAdapter(Context ctx, ArrayList<String> photos, ArrayList<String> titles, ArrayList<String> times, ArrayList<String> urls) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.photoList = photos;
        this.titleList = titles;
        this.timeList = times;
        this.urlList = urls;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feedlayout, parent, false);
        FeedAdapter.MyViewHolder holder = new FeedAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Picasso.get().load(photoList.get(position)).into(holder.image);
        holder.title.setText(titleList.get(position));
        holder.time.setText(timeList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FeedDetailActivity.class);
                intent.putExtra("Url", urlList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView image;
        TextView title;
        TextView time;
        ImageView favorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.iv_feed_image);
            title = itemView.findViewById(R.id.tv_title);
            time = itemView.findViewById(R.id.tv_time);
            favorite = itemView.findViewById(R.id.iv_favorite);
        }
    }

}
