package com.bizbrolly.bluetoothlibrary.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bizbrolly.bluetoothlibrary.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Created by Akash on 17/04/17.
 */

public class BluetoothLeService extends Service {
    private IBinder mBinder = new BluetoothLeBinder();
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private final UUID DEV_TECH_SERVICE_UUID = UUID.fromString("0003cdd0-0000-1000-8000-00805f9b0131");
    private final UUID DEV_TECH_READ_CHARECTERISTICS_UUID = UUID.fromString("0003cdd1-0000-1000-8000-00805f9b0131");
    private final UUID DEV_TECH_WRITE_CHARECTERISTICS_UUID = UUID.fromString("0003cdd2-0000-1000-8000-00805f9b0131");


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);


        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, stringBuilder.toString());
        }

        sendBroadcast(intent);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction = "";
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                Logger.log("Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Logger.log("Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Logger.log("Disconnected from GATT server.");
            }
            broadcastUpdate(intentAction);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.log(ACTION_GATT_SERVICES_DISCOVERED);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                registerNotificationCharacteristic();
            } else {
                Logger.log("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.log("New data available");
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Logger.log("New data changed");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            boolean test = false;
            Logger.log("onCharacteristicWrite status: "+status);
            if(test){
                readCustomCharacteristic();
            }
        }
    };

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Logger.log("Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.log("Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    private String mBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Logger.log("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Logger.log("Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.log("Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Logger.log("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null && intent.getExtras().keySet().contains("WRITE_DATA")) {
            writeCustomCharacteristic(intent.getByteArrayExtra("WRITE_DATA"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerNotificationCharacteristic(){
        BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(DEV_TECH_SERVICE_UUID).getCharacteristic(DEV_TECH_READ_CHARECTERISTICS_UUID);
        if(!mBluetoothGatt.setCharacteristicNotification(characteristic, true))
            Logger.log("Register for notification failed");
    }

    private void writeCustomCharacteristic(byte[] value) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.log("BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService service = mBluetoothGatt.getService(DEV_TECH_SERVICE_UUID);
        if (service == null) {
            Logger.log("Custom BLE Service not found");
            return;
        }
        Logger.log(service.getUuid().toString());
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(DEV_TECH_WRITE_CHARECTERISTICS_UUID);
        if (characteristic == null) {
            Logger.log("BluetoothGattCharacteristic not found");
            return;
        }

        Logger.log(characteristic.getUuid().toString());
        characteristic.setValue(value);
        Logger.log("Writing value: " + value);
        if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
            Logger.log("Failed to write characteristic");
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.log("BluetoothAdapter not initialized");
            return;
        }
        if(!mBluetoothGatt.readCharacteristic(characteristic)){
            Logger.log("Failed to read characteristic");
        }
    }

//    public void readCustomCharacteristic() {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Logger.log("BluetoothAdapter not initialized");
//            return;
//        }
//        /*check if the service is available on the device*/
//        List<BluetoothGattService> services = mBluetoothGatt.getServices();
//        if(services == null || services.size()==0){
//            Logger.log( "Custom BLE Service not found");
//            return;
//        }
//
//        for(BluetoothGattService service: services) {
//        /*get the read characteristic from the service*/
//            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
//            for(BluetoothGattCharacteristic characteristic : characteristics) {
//                if(
//                        (characteristic.getUuid().toString().equals("00002a00-0000-1000-8000-00805f9b34fb")
//                        || characteristic.getUuid().toString().equals("00002a01-0000-1000-8000-00805f9b34fb")
//                        || characteristic.getUuid().toString().equals("00002a04-0000-1000-8000-00805f9b34fb")
//                        || characteristic.getUuid().toString().equals("00002a05-0000-1000-8000-00805f9b34fb")
//                        || characteristic.getUuid().toString().equals("00002a19-0000-1000-8000-00805f9b34fb")
//                        || characteristic.getUuid().toString().equals("0003cbb1-0000-1000-8000-00805f9b0131")
//                        || characteristic.getUuid().toString().equals("0003cbb2-0001-0008-0000-0805f9b01310")
//                        )
//                        && (service.getUuid().toString().equals("00001800-0000-1000-8000-00805f9b34fb")
//                        || service.getUuid().toString().equals("00001801-0000-1000-8000-00805f9b34fb")
//                        || service.getUuid().toString().equals("0000180f-0000-1000-8000-00805f9b34fb")
//                        || service.getUuid().toString().equals("0003cbbb-0000-1000-8000-00805f9b0131")
//                        )
//                        )
//                    continue;
//                Logger.log("Service: "+ service.getUuid().toString());
//                Logger.log("Characteristic: "+ characteristic.getUuid().toString());
//                if (!mBluetoothGatt.readCharacteristic(characteristic)) {
//                    Logger.log("Failed to read characteristic");
//                } else {
//                    Logger.log("Read characteristic success");
//                    return;
//                }
//            }
//        }
//    }

    public void readCustomCharacteristic() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.log("BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = mBluetoothGatt.getService(DEV_TECH_SERVICE_UUID);
        if(mCustomService == null){
            Logger.log( "Custom BLE Service not found");
            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(DEV_TECH_READ_CHARECTERISTICS_UUID);
        if(!mBluetoothGatt.readCharacteristic(mReadCharacteristic)){
            Logger.log("Failed to read characteristic");
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.log("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    public class BluetoothLeBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
