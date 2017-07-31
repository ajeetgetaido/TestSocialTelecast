package com.rathore.socialtelecast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ajeet on 29/7/17.
 */

public class AdapterParticularGroupMember extends RecyclerView.Adapter<AdapterParticularGroupMember.Holder>{


    private final Context context;
    private final ArrayList<String> EmailList;

    public AdapterParticularGroupMember(Context context, ArrayList<String> categoryDetailList) {
        Log.d("Length", "" + categoryDetailList.size());
        this.context = context;
        this.EmailList = categoryDetailList;
    }


    @Override
    public AdapterParticularGroupMember.Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.groupname, parent, false);
        AdapterParticularGroupMember.Holder holder = new AdapterParticularGroupMember.Holder(view);
        holder.tvEmailAdtp=(TextView) view.findViewById(R.id.createGroupNameTxt);
        holder.linearLayoutPgdma=(LinearLayout) view.findViewById(R.id.creategroupLLt);
        return holder;

    }

    @Override
    public void onBindViewHolder(AdapterParticularGroupMember.Holder holder, int position) {
        holder.tvEmailAdtp.setText(EmailList.get(position));
    }

    @Override
    public int getItemCount() {
        return EmailList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvEmailId)
        TextView tvEmailAdtp;
        @BindView(R.id.linearLayouttPGM)
        LinearLayout linearLayoutPgdma;



        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
