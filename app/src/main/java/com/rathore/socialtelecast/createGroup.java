package com.rathore.socialtelecast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class createGroup extends MainActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String GroupNameEd, GroupMemberEd, BtnADD;
    private EditText mGroupName;
    private EditText mGroupMemberEmail;
    private Button mBtnAdd;
    private Button mBtnDone;
    private TextView mTVGroupMember;
    private TextView mTVGroupName;
    private DbHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        db = new DbHelper(createGroup.this);

        mGroupName = (EditText) findViewById(R.id.edGroupName);
        mGroupMemberEmail = (EditText) findViewById(R.id.edGroupMemberEmail);
        mTVGroupName = (TextView) findViewById(R.id.tvGroupName);
        mTVGroupMember = (TextView) findViewById(R.id.tvGroupMemberEmail);

        mBtnAdd = (Button) findViewById(R.id.btnAddEmail);
        mBtnDone = (Button) findViewById(R.id.btnDone);
        click();



    }


    private void click() {

        mBtnAdd.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAddEmail:
                addGroupDetail();
                break;
            case R.id.btnDone:
                //Intent intent=new Intent();
                setResult(RESULT_OK);
                finish();
                break;


        }
    }


    private void addGroupDetail() {

        GroupNameEd = mGroupName.getText().toString().trim();
        GroupMemberEd = mGroupMemberEmail.getText().toString().trim();
        BtnADD = mBtnAdd.getText().toString().trim();

        if (!GroupNameEd.equalsIgnoreCase("")) {

            if (!GroupMemberEd.equalsIgnoreCase("")) {

                if (!BtnADD.equalsIgnoreCase("")) {

                    db.addHome(GroupNameEd, GroupMemberEd, BtnADD);
                    mGroupMemberEmail.setText("");
                    Log.d("Insert", "Inserting...");
                    Toast.makeText(this, "Insert Sucessfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(createGroup.this, "Enter some email id", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(createGroup.this, "Enter the group name", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //

}
