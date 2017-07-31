package com.rathore.socialtelecast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rathore.socialtelecast.ParticularGroupMember.EMAIL_LIST_KEY;

/**
 * Created by ajeet on 28/7/17.
 */

public class createGroupAdapter extends RecyclerView.Adapter<createGroupAdapter.Holder> {

    private final Context context;
    private final FragmentManager fm;
    public HashMap<String, ArrayList<String>> DetailList = new HashMap<>();
    private HashMap<String, String> map;
    //private String ID;
    private FragmentTransaction transaction;


    public createGroupAdapter(Context context, HashMap<String, ArrayList<String>> categoryDetailList, FragmentManager fm) {
        Log.d("Length", "" + categoryDetailList.size());
        this.context = context;
        this.DetailList = categoryDetailList;
        this.fm = fm;
    }

    @Override
    public createGroupAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.groupname, parent, false);
        createGroupAdapter.Holder holder = new createGroupAdapter.Holder(view);
        holder.mCreateGroupTxt = (TextView) view.findViewById(R.id.createGroupNameTxt);
        holder.mCreateGroupLLT = (LinearLayout) view.findViewById(R.id.creategroupLLt);
        return holder;


    }


    @Override
    public void onBindViewHolder(final createGroupAdapter.Holder holder, final int position) {
        //map = DetailList.get(position);
        //ID = map.get("key_group_id");

        holder.mCreateGroupTxt.setText((String) DetailList.keySet().toArray()[position]);
        holder.mCreateGroupLLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParticularGroupMemberActivity(DetailList.get(DetailList.keySet().toArray()[position]));
            }
        });

    }

    @Override
    public int getItemCount() {
        return DetailList.size();
    }

    private void startParticularGroupMemberActivity(ArrayList<String> emailList) {
        Intent memberListIntent = new Intent(context, ParticularGroupMember.class);
        memberListIntent.putStringArrayListExtra(EMAIL_LIST_KEY, emailList);
        context.startActivity(memberListIntent);
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.createGroupNameTxt)
        TextView mCreateGroupTxt;
        @BindView(R.id.creategroupLLt)
        LinearLayout mCreateGroupLLT;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
