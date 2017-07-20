package bizbrolly.svarochiapp.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.akkipedia.skeleton.permissions.PermissionManager;
import com.akkipedia.skeleton.utils.Logger;
import com.bizbrolly.bluetoothlibrary.BluetoothHelper;
import com.bizbrolly.bluetoothlibrary.Callbacks.BleScannerCallback;
import com.bizbrolly.bluetoothlibrary.csr_ble_2_1.CsrScanner;
import com.csr.csrmesh2.MeshService;

import java.util.concurrent.atomic.AtomicBoolean;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.Association;
import bizbrolly.svarochiapp.ibahn_logic.MeshApiMessageHandler;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;

/**
 * Created by Akash on 24/04/17.
 */

public class MainActivity extends BaseCsrActivity {

    private BluetoothHelper bluetoothHelper;
    private PermissionManager permissionManager;
    public static MeshService mService;
    public static MeshApiMessageHandler meshApiMessageHandler;
    private BluetoothDevice device;
    private boolean mConnected;


    public static  AtomicBoolean isScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScanning = new AtomicBoolean(true);
        setContentView(R.layout.activity_main);
        initPermissions();
    }


    //region CSR Code

    private void discoverBleDevices() {
        isScanning.set(true);
        Logger.log("Starting scan");
        showProgressDialog("Discovering","Please wait...");
        bluetoothHelper.getBleScanner().startScan(new BleScannerCallback() {

            @Override
            public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Logger.log(device.getAddress());
                bluetoothHelper.getBleScanner().stopScan();
                MainActivity.this.device = device;
                bindMeshService();
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
            mService.setHandler(meshApiMessageHandler = new MeshApiMessageHandler(MainActivity.this));
            initBleBearer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initBleBearer() {
        Logger.log("Init Bearer");
        mService.setLeScanCallback(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                mService.processMeshAdvert(device, scanRecord, rssi);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            mService.setBluetoothBearerEnabled(ScanSettings.SCAN_MODE_LOW_LATENCY);
        } else {
            mService.setBluetoothBearerEnabled();
        }
        mService.setContinuousLeScanEnabled(true);
        mService.setMeshListeningMode(true, true);
    }

    public void onBearerReady() {
        showProgressDialog("Connecting", "Please wait...");
        Logger.log("Connecting device: " + device.getName());
//        mService.connectBridge(device);
        mService.startAutoConnect(1);
    }


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

    //endregion


    //region BLE Callbacks

    @Override
    public void onConnected() {
        isScanning.set(false);
        hideProgressDialog();
        stopScan();
        mConnected = true;
        Association.discoverDevices(true, mService);
        hideProgressDialog();
        if (Preferences.getInstance(this).isPasswordSaved())
            startActivity(new Intent(this, HomeActivity.class));
        else
            startActivity(new Intent(this, PasswordSetupActivity.class));
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
                    bluetoothHelper = BluetoothHelper.getSharedInstance(MainActivity.this);
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


    //region Enable Hardware code


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
