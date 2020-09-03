package com.stackrage.gofeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.stackrage.gofeds.ui.feed.FeedFragment;
import com.stackrage.gofeds.ui.profile.ProfileFragment;

import java.util.ArrayList;

public class PortAdapter extends RecyclerView.Adapter<PortAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private String[] portList;
    private ArrayList<Boolean> checkList;
    private Boolean isCurrent;
    private Boolean isType;
    private ProfileFragment profileFragment;

    public PortAdapter(Context ctx, String[] ports, ArrayList<Boolean> checks, Boolean current, Boolean type, ProfileFragment fragment) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.portList = ports;
        this.checkList = checks;
        this.isCurrent = current;
        this.isType = type;
        this.profileFragment = fragment;
    }

    @NonNull
    @Override
    public PortAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.portlayout, parent, false);
        PortAdapter.MyViewHolder holder = new PortAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PortAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(portList[position]);
        if (checkList.get(position)) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.INVISIBLE);
        }
        holder.portCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isType) {
                    if (isCurrent) {
                        ((SignupActivity)context).onClickCurrentPort(position);

                    } else {
                        ((SignupActivity)context).onClickDesirePort(position);
                    }
                } else {
                    if (isCurrent) {
                        ((ProfileFragment)profileFragment).onClickCurrentPort(position);

                    } else {
                        ((ProfileFragment)profileFragment).onClickDesirePort(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return portList.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout portCell;
        TextView name;
        ImageView check;

        public MyViewHolder(View itemView) {
            super(itemView);
            portCell = itemView.findViewById(R.id.ll_port);
            name = itemView.findViewById(R.id.tv_portname);
            check = itemView.findViewById(R.id.iv_check);
        }
    }

}