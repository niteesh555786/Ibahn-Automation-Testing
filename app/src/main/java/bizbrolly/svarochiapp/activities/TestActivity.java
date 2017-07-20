package bizbrolly.svarochiapp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;

import com.akkipedia.skeleton.permissions.PermissionManager;
import com.akkipedia.skeleton.utils.Logger;
import com.bizbrolly.bluetoothlibrary.BluetoothHelper;
import com.bizbrolly.bluetoothlibrary.Callbacks.BleScannerCallback;
import com.bizbrolly.bluetoothlibrary.csr_ble_2_1.CsrScanner;
import com.csr.csrmesh2.MeshService;

import java.lang.ref.WeakReference;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.Association;
import bizbrolly.svarochiapp.ibahn_logic.MeshApiMessageHandler;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;

public class TestActivity extends BaseCsrActivity {

    private BluetoothHelper bluetoothHelper;
    private PermissionManager permissionManager;
    public static MeshService mService;
    public static MeshApiMessageHandler meshApiMessageHandler;
    private BluetoothDevice device;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initPermissions();
    }

    private void discoverBleDevices() {
        Logger.log("Starting scan");
        bluetoothHelper.getBleScanner().startScan(new BleScannerCallback() {

            @Override
            public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Logger.log(device.getAddress());
                bluetoothHelper.getBleScanner().stopScan();
                TestActivity.this.device = device;
                stopScan();
                initBleBearer();
            }

            @Override
            public void onScanFinished(boolean failed) {

            }
        }, CsrScanner.NO_TIMEOUT);
    }

    private void bindMeshService() {
        Logger.log("Binding service");
        if (mService == null) {
            Intent bindIntent = new Intent(this, MeshService.class);
            bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {



        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.log("Service bound");
            mService = ((MeshService.LocalBinder) service).getService();
            mService.setHandler(meshApiMessageHandler = new MeshApiMessageHandler(TestActivity.this));
            mService.setLeScanCallback(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    mService.processMeshAdvert(device, scanRecord, rssi);
                }
            });
            mService.setBluetoothBearerEnabled(ScanSettings.SCAN_MODE_LOW_LATENCY);
            mService.setContinuousLeScanEnabled(true);
//            initBleBearer();
            mService.setMeshListeningMode(true, false);
            onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initBleBearer() {
        Logger.log("Init Bearer");
        onBearerReady();
//        if (Build.VERSION.SDK_INT >= 21) {
//            mService.setBluetoothBearerEnabled(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        } else {
//            mService.setBluetoothBearerEnabled();
//        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onBearerReady() {
        Logger.log("Connecting device: " + device.getName());
//        mService.connectBridge(device);
//        mService.startAutoConnect(1);
        device.connectGatt(
                this,
                true,
                new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        Logger.log("onConnectionStateChange: " + status);
                        bindMeshService();


                    }
                },
                BluetoothDevice.TRANSPORT_LE
        );
    }

    //endregion

    //region BLE Callbacks

    @Override
    protected void onResume() {
        super.onResume();
        if (mConnected)
            Association.discoverDevices(true, mService);
    }


    private void stopScan() {
        if (bluetoothHelper != null)
            bluetoothHelper.getBleScanner().stopScan();
    }

    private boolean mConnected;
    public static final String MY_PREFS_NAME = "PASSWORD_SAVED_STATUS";

    @Override
    public void onConnected() {
        hideProgressDialog();
//        stopScan();
        mConnected = true;
        /*SharedPreferences sp = this.getSharedPreferences(MY_PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (Preferences.getInstance(this).isPasswordSaved()) {
            editor.putInt("pStatus", 1);
            editor.commit();
        }
        else{
            editor.putInt("pStatus", 0);
            editor.commit();
        }*/
        Association.discoverDevices(true, mService);
        hideProgressDialog();
        if (Preferences.getInstance(this).isPasswordSaved())
            startActivity(new Intent(this, HomeActivity.class));
        else
            startActivity(new Intent(this, SetSystemPasswordActivity.class));
    }

    @Override
    public void onDisconnected() {
        mConnected = false;
        Association.discoverDevices(false, mService);
    }

    //endregion


    //region Permission code
    private void initPermissions() {
        PermissionManager.PermissionListener permissionListener = new PermissionManager.PermissionListener() {
            @Override
            public void onPermissionsResult(boolean allGranted, String[] grantedPermissions, String[] rejectedPermissions) {
                if (!allGranted) {
                    showToast("Please provide all the permissions in order to use the application.");
                    finish();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initGps();
                else {
                    bluetoothHelper = BluetoothHelper.getSharedInstance(TestActivity.this);
                    bluetoothHelper.setCsrMode(true);
                    initBluetooth();
                }
            }
        };
        permissionManager = new PermissionManager.Builder(permissionListener)
                .with(this)
                .addLocationPermissions()
                .addPermission(Manifest.permission.BLUETOOTH)
                .addPermission(Manifest.permission.BLUETOOTH_ADMIN)
                .addCameraPermissions()
                .build();
        permissionManager.checkAndAskPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bluetoothHelper.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    private void initGps() {
        bluetoothHelper = BluetoothHelper.getSharedInstance(this);
        bluetoothHelper.setCsrMode(true);
        if (bluetoothHelper.isGpsEnabled(this)) {
            initBluetooth();
        } else {
            bluetoothHelper.requestGpsEnable(this, new BluetoothHelper.GpsEnableListener() {
                @Override
                public void onBluetoothResult(boolean enabled) {
                    if (!enabled) {
                        showToast("GPS is required!");
                        finish();
                        return;
                    }
                    initBluetooth();
                }
            });
        }
    }

    private void initBluetooth() {
        if (!bluetoothHelper.isBluetoothEnabled()) {
            bluetoothHelper.requestBluetoothEnable(this, new BluetoothHelper.BluetoothEnableListener() {
                @Override
                public void onBluetoothResult(boolean enabled) {
                    if (!enabled) {
                        showToast("Bluetooth is required!");
                        finish();
                        return;
                    }
                    discoverBleDevices();
                }
            });
        } else {
            discoverBleDevices();
        }
    }
    //endregion
}
