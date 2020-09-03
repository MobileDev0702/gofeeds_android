package com.stackrage.gofeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<MessageInfo> messageInfos = new ArrayList<>();

    public MessageAdapter(Context ctx, ArrayList<MessageInfo> messageInfos) {
        this.context = ctx;
        this.messageInfos = messageInfos;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        return messageInfos.get(position).getUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.my_message, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.their_message, parent, false);
            return new TheirViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.messageBody.setText(messageInfos.get(position).getMessage());
        } else {
            TheirViewHolder viewHolder = (TheirViewHolder)holder;
            viewHolder.messageBody.setText(messageInfos.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messageBody;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.message_body);
        }
    }

    class TheirViewHolder extends RecyclerView.ViewHolder {

        TextView messageBody;

        public TheirViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.message_body);
        }
    }
}
