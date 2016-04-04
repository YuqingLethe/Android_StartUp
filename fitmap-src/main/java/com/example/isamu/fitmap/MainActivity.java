package com.example.isamu.fitmap;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    public GoogleMap mMap;
    public Location mLastLocation;

    private class DataUpdateReceiver extends BroadcastReceiver implements GoogleMap.OnMyLocationButtonClickListener {
        /**
         * Represents a geographical location.
         */

        MediaPlayer mediaPlayer;
        MainActivity mMainAct;
        public DataUpdateReceiver(MainActivity inputAct) {
            mMainAct = inputAct;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(getString(R.string.nameBrod))) {
                img = (ImageView) findViewById(R.id.imageAct);
                txtout = (TextView) findViewById(R.id.statusTV);
                mMainAct.updateMap();
                int resultCode = intent.getIntExtra("type", 3);

                    switch (resultCode) {
                        case DetectedActivity.IN_VEHICLE: {
                            img.setImageResource(R.drawable.in_vehicle);
                            txtout.setText(R.string.car);
                            break;
                        }
                        case DetectedActivity.ON_FOOT: {
                            img.setImageResource(R.drawable.walking);
                            txtout.setText(R.string.walk);
                            break;
                        }
                        case DetectedActivity.RUNNING: {
                            img.setImageResource(R.drawable.running);
                            txtout.setText(R.string.run);
                            Context currentCTX = getApplicationContext();

                            mediaPlayer = MediaPlayer.create(currentCTX, R.raw.beat_02);
                            mediaPlayer.start();
                            break;
                        }
                        case DetectedActivity.STILL: {
                            img.setImageResource(R.drawable.still);
                            txtout.setText(R.string.still);
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }

                            break;
                        }
                        case DetectedActivity.WALKING: {
                            img.setImageResource(R.drawable.walking);
                            txtout.setText(R.string.walk);
                            Context currentCTX = getApplicationContext();
                            mediaPlayer = MediaPlayer.create(currentCTX, R.raw.beat_02);
                            mediaPlayer.start();

                            break;
                        }
                    }
                }
            }

        public void stopMusic()
        {
            if(mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null ;
            }
        }
        @Override
        public boolean onMyLocationButtonClick() {
            Context currentCTX = getApplicationContext();
            Toast.makeText(currentCTX, "MyLocation button clicked inner class", Toast.LENGTH_SHORT).show();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            return true;
        }

    }

    public GoogleApiClient mApiClient;
    public GoogleApiClient mLocationApi;
    private ImageView img;
    private TextView txtout;
    private DataUpdateReceiver dataUpdateReceiver;
    public SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
// Create an instance of GoogleAPIClient.


        mApiClient.connect();

        img = (ImageView) findViewById(R.id.imageAct);
        txtout = (TextView) findViewById(R.id.statusTV);
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver(this);
        IntentFilter intentFilter = new IntentFilter(getString(R.string.nameBrod));
        registerReceiver(dataUpdateReceiver, intentFilter);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAct);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver(this);
        IntentFilter intentFilter = new IntentFilter(getString(R.string.nameBrod));
        registerReceiver(dataUpdateReceiver, intentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if (dataUpdateReceiver != null) {
            unregisterReceiver(dataUpdateReceiver);
            dataUpdateReceiver.stopMusic();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );

        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 10000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return true;
    }
    public void updateMap(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (mLastLocation != null && mMap != null) {
            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


            Marker marker;
            marker = mMap.addMarker(new MarkerOptions().position(loc));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
        }
    }


}
