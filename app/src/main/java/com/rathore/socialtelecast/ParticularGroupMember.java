package com.rathore.socialtelecast;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class ParticularGroupMember extends MainActivity implements View.OnClickListener{

    private static final String TAG = "createGroup";

    public static final String EMAIL_LIST_KEY = "email_list";

    private static final int SELECT_PICTURE = 100;

    private TextView mTvList;
    private RecyclerView mRecyclerPGM;
    private Button btnPGMA;
    private FloatingActionButton btnFloatPGMA;
    private EditText editTextPGM;
    private View view;
    AppCompatImageView imgView;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
//folder into google drive

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("New folder").build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(callback);
        //write file into google drive

        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);

        Button btnSend = (Button) findViewById(R.id.btnParticularGroup);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> emailList = getEmailListFromIntent();
                if (emailList != null && !emailList.isEmpty()) {
                    String[] stockArr = emailList.toArray(new String[emailList.size()]);
                    //String[] stringArray = input.Split(',');
                    Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    it.putExtra(Intent.EXTRA_EMAIL, stockArr);
                    it.putExtra(Intent.EXTRA_SUBJECT, "Hello");
                    it.putExtra(Intent.EXTRA_TEXT, "Check the update");
                    it.setType("message/rfc822");
                    startActivity(it);
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_group_member);

        mTvList = (TextView) findViewById(R.id.tvEmailList);
        mRecyclerPGM = (RecyclerView) findViewById(R.id.recyclerParticularGroup);
        btnPGMA = (Button) findViewById(R.id.btnParticularGroup);
        editTextPGM = (EditText) findViewById(R.id.etParticularGrMemAct);

        ArrayList<String> emailList = getEmailListFromIntent();
        if (emailList != null && !emailList.isEmpty()) {
            setGroupAdapter(emailList);
        }


       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingBtnPG);
       fab.setOnClickListener(this);

    }

    //  creating folder into drive

    final ResultCallback<DriveFolder.DriveFolderResult> callback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
        }
    };



    //writing the content into the google drive
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("Hello World!");
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };

    //choose images from gallery
     void chooseOption(){
         Intent intent = new Intent();
         intent.setType("image/*");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
     }

     public void onActivityResult(int requestCode, int resultCode, Intent data){
         if(resultCode == RESULT_OK) {
             if(requestCode == SELECT_PICTURE);
             // Get the url from data
             Uri selectedImageUri = data.getData();
             if(null != selectedImageUri) {
                 //Get the path from the uri
                 String path = getPathFromURI(selectedImageUri);
                 Log.i(TAG, "Image Path : " + path);
                 imgView.setImageURI(selectedImageUri);
             }
         }
     }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null,null,null);
        if (cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onClick(View v) {
        chooseOption();

    }


    private ArrayList<String> getEmailListFromIntent() {
        if (getIntent() != null && getIntent().hasExtra(EMAIL_LIST_KEY)) {
            return getIntent().getStringArrayListExtra(EMAIL_LIST_KEY);
        }
        return new ArrayList<>();
    }

    private void setGroupAdapter(ArrayList<String> emailList) {
        AdapterParticularGroupMember adapter = new AdapterParticularGroupMember(this, emailList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerPGM.setLayoutManager(mLayoutManager);
        mRecyclerPGM.setItemAnimator(new DefaultItemAnimator());
        mRecyclerPGM.setAdapter(adapter);
    }



}
