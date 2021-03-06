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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<UserInfo> userInfos;

    public ChatAdapter(Context ctx, ArrayList<UserInfo> info) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.userInfos = info;
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
        Picasso.get().load(userInfos.get(position).getPhoto()).into(holder.image);
//        holder.image.setImageResource(R.drawable.user);
        holder.tv_username.setText(userInfos.get(position).getName());
        holder.tv_lastchat.setText(userInfos.get(position).getLastMsg());
        holder.tv_time.setText(userInfos.get(position).getTime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("RoomId", userInfos.get(position).getRoomId());
                intent.putExtra("ReceiverId", userInfos.get(position).getReceiverId());
                intent.putExtra("ReceiverUser", userInfos.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        CircleImageView image;
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
