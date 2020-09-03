package com.stackrage.gofeds;

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

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Integer> photoList;
    private ArrayList<String> receiverIdList;
    private ArrayList<String> usernameList;
    private ArrayList<String> lastchatList;
    private ArrayList<String> timeList;
    private ArrayList<String> roomIdList;

    public ChatAdapter(Context ctx, ArrayList<Integer> photos, ArrayList<String> receiverIds, ArrayList<String> names, ArrayList<String> lastchats, ArrayList<String> times, ArrayList<String> roomids) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.photoList = photos;
        this.receiverIdList = receiverIds;
        this.usernameList = names;
        this.lastchatList = lastchats;
        this.timeList = times;
        this.roomIdList = roomids;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chatlayout, parent, false);
        ChatAdapter.MyViewHolder holder = new ChatAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, final int position) {
//        Picasso.get().load(photoList.get(position)).into(holder.image);
        holder.image.setImageResource(R.drawable.user);
        holder.tv_username.setText(usernameList.get(position));
        holder.tv_lastchat.setText(lastchatList.get(position));
        holder.tv_time.setText(timeList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("RoomId", roomIdList.get(position));
                intent.putExtra("ReceiverId", receiverIdList.get(position));
                intent.putExtra("ReceiverUser", usernameList.get(position));
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
        TextView tv_lastchat;
        TextView tv_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.iv_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_lastchat = itemView.findViewById(R.id.tv_lastchat);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

}
