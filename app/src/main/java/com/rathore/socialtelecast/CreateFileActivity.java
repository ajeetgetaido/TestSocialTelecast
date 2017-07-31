package com.rathore.socialtelecast;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.google.android.gms.drive.DriveId.decodeFromString;

/**
 * Created by rathore on 27/07/17.
 */

public class CreateFileActivity extends MainActivity {
    private static final String TAG = "CreateFileActivity";
    String encodeString;
    File finalFile;
    public static final int TAKE_PICTURE_REQUEST_CODE = 100;

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        editcontent();
//    }

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
                            MetadataChangeSet changeSet;
                            // write content to DriveContents
                            if(finalFile!=null)
                            {
                            InputStream input = null;
                            try {
                                input = new FileInputStream(finalFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            OutputStream outputStream = driveContents.getOutputStream();
                            byte[] buffer = new byte[16 * 1014];
                            int read = 0;
                            try {
                                while ((read = input.read(buffer)) > 0) {
                                    outputStream.write(buffer, 0, read);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                             changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("image file")
                                    .setMimeType("image/*")
                                    .setStarred(true).build();
                        }else{
                            //write Text
                              OutputStream outputStream = driveContents.getOutputStream();
 Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("Hello World!");
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                             changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();}

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
                    encodeString=result.getDriveFile().getDriveId().encodeToString();
                    Log.i("file created",encodeString);
                    //retrivecontent();
                    if(encodeString!=null){
                        Log.i("encodestring","not null");

                       //editcontent();
                        retrivecontent();
                    }
                    else {
                        Log.i("encodestring","null");
                    }



                }
            };


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
//        Drive.DriveApi.newDriveContents(getGoogleApiClient())
//                .setResultCallback(driveContentsCallback);
        takepicture();

    }

    public void retrivecontent(){
//
    final String[] contents = {null};

    final DriveId id=decodeFromString(encodeString);
    new AsyncTask<Void,Void,String>() {

        @Override
        protected String doInBackground(Void... params) {
            DriveFile file = id.asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                Log.i("success","null");
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
          //  BitmapFactory.decodeStream(driveContents.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents[0] = builder.toString();
            } catch (IOException e) {
                //  Log.e(TAG, "IOException while reading from the stream", e);
                Log.i("exception",e.toString());
            }
            driveContents.discard(getGoogleApiClient());
            return contents[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("result",s);
        }
    }.execute();



    //Log.i("contents", contents[0]) ;
//    Intent intent=new Intent(getApplicationContext(),RetrieveContentsActivity.class) ;
//    intent.putExtra("driveid",encodeString);
//    startActivity(intent);
}
public void editcontent(){
    final String[] contents = {null};

    final DriveId id=decodeFromString(encodeString);
    new AsyncTask<Void,Void,Boolean>() {

        @Override
        protected Boolean doInBackground(Void... params) {
            DriveFile file = id.asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                Log.i("success","null");
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            try {
                outputStream.write("content edit".getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            com.google.android.gms.common.api.Status status =
                    driveContents.commit(getGoogleApiClient(), null).await();
            return status.getStatus().isSuccess();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            Log.i("result",String.valueOf(s));
        }
    }.execute();
}
public void takepicture(){
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(takePictureIntent,TAKE_PICTURE_REQUEST_CODE);
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode ==TAKE_PICTURE_REQUEST_CODE){
            Log.i("intentdata",data.getData().toString());
            File file=new File(data.getData().getPath());
            Log.i("file path",file.getAbsolutePath());
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
             finalFile = new File(path);
            Log.i("final path", finalFile.getAbsolutePath());
            Drive.DriveApi.newDriveContents(getGoogleApiClient())
                    .setResultCallback(driveContentsCallback);

//            InputStream input = null;
//            OutputStream output = null;
//            try {
//                input = new FileInputStream(finalFile);
//                output = target.getOutputStream();
//                byte[] buffer = new byte[16 * 1014];
//                int read = 0;
//                while ((read = input.read(buffer)) > 0) {
//                    output.write(buffer, 0, read);
//                }
//            } catch (IOException ex) {
//                throw ex;
//            } finally {
//                if (input != null) {
//                    try {
//                        input.close();
//                    } catch (IOException ex) {
//                        Log.e(TAG, "Cannot close input", ex);
//                    }
//                }
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (IOException ex) {
//                        Log.e(TAG, "Cannot close output", ex);
//                    }

        }
    }
}
