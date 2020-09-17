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

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> avatarList;
    private ArrayList<String> usernameList;
    private ArrayList<String> answerList;
    private ArrayList<Integer> voteList;
    private ArrayList<String> idList;

    public AnswerAdapter(Context ctx, ArrayList<String> avatars, ArrayList<String> usernames, ArrayList<String> answers, ArrayList<Integer> votes, ArrayList<String> ids) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.avatarList = avatars;
        this.usernameList = usernames;
        this.answerList = answers;
        this.voteList = votes;
        this.idList = ids;
    }

    @NonNull
    @Override
    public AnswerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.answerlayout, parent, false);
        AnswerAdapter.MyViewHolder holder = new AnswerAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.MyViewHolder holder, final int position) {
        Picasso.get().load(avatarList.get(position)).into(holder.image);
//        holder.image.setImageResource(R.drawable.user);
        holder.tv_username.setText(usernameList.get(position));
        holder.tv_answer.setText(answerList.get(position));
        holder.tv_vote.setText(voteList.get(position).toString());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("Id", idList.get(position));
                context.startActivity(intent);
            }
        });
        holder.iv_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AnswerListActivity)context).onClickUpVote(position);
            }
        });
        holder.iv_downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AnswerListActivity)context).onClickDownVote(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        CircleImageView image;
        TextView tv_username;
        TextView tv_answer;
        ImageView iv_upvote;
        ImageView iv_downvote;
        TextView tv_vote;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            image = itemView.findViewById(R.id.iv_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            iv_upvote = itemView.findViewById(R.id.iv_upvote);
            tv_vote = itemView.findViewById(R.id.tv_votecount);
            iv_downvote = itemView.findViewById(R.id.iv_downvote);
        }
    }
}
