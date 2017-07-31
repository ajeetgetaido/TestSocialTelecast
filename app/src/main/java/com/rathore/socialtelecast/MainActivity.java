package com.rathore.socialtelecast;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int CREATE_GROUP_REQUEST_CODE = 101;
    GoogleApiClient mGoogleApiClient;
    private DbHelper db;
    private Button btnNext;
    private RecyclerView recyclerViewDetail;
    // public static final String EXISTING_FOLDER_ID = "0B2EEtIjPUdX6MERsWlYxN3J6RU0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbHelper(MainActivity.this);

        Log.i("oncreate", "called");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                //.setAccountName("vinitashekhawat7@gmail.com")
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)


                .build();

        //mGoogleApiClient.connect();
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }

        setGroupAdapter();

        btnNext = (Button) findViewById(R.id.btnNextMainACT);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iBTN = new Intent(MainActivity.this, createGroup.class);
                startActivityForResult(iBTN, CREATE_GROUP_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("connection", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("connection", "suspanded");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("connection", connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
        }
        try {
            connectionResult.startResolutionForResult(this, 1);
        } catch (IntentSender.SendIntentException e) {
            // Unable to resolve, message user appropriately
        }
    }
//            else {
//            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
//        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_group) {
            Toast.makeText(this, "Edit Group", Toast.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGroupAdapter() {
        if (db != null) {
            HashMap<String, ArrayList<String>> detailList = db.getGroupDetails();
            Log.d("detailList", "" + detailList.size());
            recyclerViewDetail = (RecyclerView) findViewById(R.id.recyclerMain);
            createGroupAdapter adapater = new createGroupAdapter(this, detailList, getSupportFragmentManager());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerViewDetail.setLayoutManager(mLayoutManager);
            recyclerViewDetail.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDetail.setAdapter(adapater);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.i("inActivityresult", "called");
                mGoogleApiClient.connect();
            } else if (requestCode == CREATE_GROUP_REQUEST_CODE) {
                setGroupAdapter();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}
