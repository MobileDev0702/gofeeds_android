package com.stackrage.gofeds.ui.connections;

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

import com.stackrage.gofeds.R;
import com.stackrage.gofeds.UserProfileActivity;

import java.util.ArrayList;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Integer> photoList;
    private ArrayList<String> idList;
    private ArrayList<String> usernameList;
    private ArrayList<String> locationList;

    public ConnectionAdapter(Context ctx, ArrayList<Integer> photos, ArrayList<String> ids, ArrayList<String> names, ArrayList<String> locations) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.photoList = photos;
        this.idList = ids;
        this.usernameList = names;
        this.locationList = locations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.connectionlayout, parent, false);
        ConnectionAdapter.MyViewHolder holder = new ConnectionAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
//        Picasso.get().load(photoList.get(position)).into(holder.image);
        holder.image.setImageResource(R.drawable.user);
        holder.tv_username.setText(usernameList.get(position));
        holder.tv_location.setText(locationList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("Id", idList.get(position));
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
        TextView tv_username;
        TextView tv_location;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.iv_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_location = itemView.findViewById(R.id.tv_location);
        }
    }

}
