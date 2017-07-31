package com.rathore.socialtelecast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.google.android.gms.drive.DriveId.decodeFromString;

/**
 * Created by rathore on 27/07/17.
 */

public class RetrieveContentsActivity extends MainActivity {
    private static final String TAG = "RetrieveContentsActivity";
    DriveId id;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Intent  intent=getIntent();
        if(intent!=null){
             id=decodeFromString(intent.getStringExtra("driveid"));
            Log.i("params",id.toString());


        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), "0ByfSjdPVs9MZTHBmMVdSeWxaNTg")
                .setResultCallback(idCallback);
        //Drive.DriveApi.fetchDriveId(getGoogleApiClient(),null)
    }

    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
//            result.getDriveId();
//            Log.i("params",result.getDriveId().toString());

            new RetrieveDriveFileContentsAsyncTask(
                    RetrieveContentsActivity.this).execute(id);
        }
    };

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
//            String param=params[0].toString();
            Log.i("params",id.toString());
            //params[0]=
            //String id="CAESABggIKb3iO6FVygA";

            //DriveId driveId=decodeFromString("CAESABggIKb3iO6FVygA");


            DriveFile file = id.asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents = builder.toString();
            } catch (IOException e) {
              //  Log.e(TAG, "IOException while reading from the stream", e);
                Log.i("exception",e.toString());
            }

            driveContents.discard(getGoogleApiClient());
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            Log.i("result",result);
            showMessage("File contents: " + result);
        }

    }


}
