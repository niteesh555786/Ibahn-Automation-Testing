package com.bizbrolly.bluetoothlibrary;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.bizbrolly.bluetoothlibrary.AbstractClasses.Scanner;
import com.bizbrolly.bluetoothlibrary.ble.BleScanner;
import com.bizbrolly.bluetoothlibrary.csr_ble_2_1.CsrScanner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Akash on 29/03/17.
 */

public class BluetoothHelper {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private static BluetoothHelper sharedInstance;
    private static final int ENABLE_BLUETOOTH_CODE = 454;
    private BluetoothEnableListener bluetoothEnableListener;
    private GpsEnableListener gpsEnableListener;
    private boolean isCsrMode;
    private Context context;

    public static void init(Context context, boolean CSR) {
        sharedInstance = new BluetoothHelper();
        Logger.setTag(sharedInstance.getClass().getSimpleName());
        sharedInstance.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        sharedInstance.bluetoothAdapter = sharedInstance.bluetoothManager.getAdapter();
        sharedInstance.isCsrMode = CSR;
    }

    public static BluetoothHelper getSharedInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new BluetoothHelper();
            Logger.setTag(sharedInstance.getClass().getSimpleName());
            sharedInstance.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            sharedInstance.bluetoothAdapter = sharedInstance.bluetoothManager.getAdapter();
        }
        sharedInstance.context = context;
        return sharedInstance;
    }

    public void setCsrMode(boolean isCsrMode) {
        this.isCsrMode = isCsrMode;
    }

    private void refreshBluetoothAdapter() {
        sharedInstance.bluetoothAdapter = sharedInstance.bluetoothManager.getAdapter();
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            return false;
        }
    }

    public void enableBluetooth(Activity context, BluetoothEnableListener bluetoothEnableListener) {
        this.bluetoothEnableListener = bluetoothEnableListener;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_CODE);
    }

    public void requestBluetoothEnable(final Activity activity, final BluetoothEnableListener bluetoothEnableListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle("Bluetooth")
                .setMessage("Application requires you to enable bluetooth")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothEnableListener.onBluetoothResult(false);
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableBluetooth(activity, bluetoothEnableListener);
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != ENABLE_BLUETOOTH_CODE && requestCode != REQUEST_GPS_ENABLE)
            return;
        if (requestCode == ENABLE_BLUETOOTH_CODE) {
            refreshBluetoothAdapter();
            bluetoothEnableListener.onBluetoothResult(isBluetoothEnabled());
        } else {
            gpsEnableListener.onBluetoothResult(resultCode == Activity.RESULT_OK);
        }
    }


    private static final int REQUEST_GPS_ENABLE = 186;

    public void requestGpsEnable(final Activity activity, final GpsEnableListener gpsEnableListener) {
        this.gpsEnableListener = gpsEnableListener;
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        gpsEnableListener.onBluetoothResult(true);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, REQUEST_GPS_ENABLE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        gpsEnableListener.onBluetoothResult(false);
                        break;
                }
            }
        });
    }


    private Scanner scanner;

    public Scanner getBleScanner() {
        if (scanner == null)
            if (isCsrMode) {
                scanner = new CsrScanner(bluetoothAdapter);
            } else
                scanner = new BleScanner(bluetoothAdapter);
        return scanner;
    }

    public interface BluetoothEnableListener {
        void onBluetoothResult(boolean enabled);
    }

    public interface GpsEnableListener {
        void onBluetoothResult(boolean enabled);
    }
}
