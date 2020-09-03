package com.stackrage.gofeds.ui.forum;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stackrage.gofeds.AnswerListActivity;
import com.stackrage.gofeds.R;

import java.util.ArrayList;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Integer> avatarList;
    private ArrayList<String> usernameList;
    private ArrayList<String> questionList;
    private ArrayList<String> answerList;
    private ArrayList<String> quesIdList;

    public ForumAdapter(Context ctx, ArrayList<Integer> avatars, ArrayList<String> names, ArrayList<String> questions, ArrayList<String> answers, ArrayList<String> quesids) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.avatarList = avatars;
        this.usernameList = names;
        this.questionList = questions;
        this.answerList = answers;
        this.quesIdList = quesids;
    }

    @NonNull
    @Override
    public ForumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.forumlayout, parent, false);
        ForumAdapter.MyViewHolder holder = new ForumAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumAdapter.MyViewHolder holder, final int position) {
//        Picasso.get().load(photoList.get(position)).into(holder.image);
        holder.iv_avatar.setImageResource(R.drawable.user);
        holder.tv_username.setText(usernameList.get(position));
        holder.tv_question.setText(questionList.get(position));
        holder.tv_answer.setText(answerList.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnswerListActivity.class);
                intent.putExtra("Question_Id", quesIdList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView iv_avatar;
        TextView tv_username;
        TextView tv_question;
        TextView tv_answer;

        public MyViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linearLayout);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_question = itemView.findViewById(R.id.tv_question);
            tv_answer = itemView.findViewById(R.id.tv_answer);
        }
    }

}