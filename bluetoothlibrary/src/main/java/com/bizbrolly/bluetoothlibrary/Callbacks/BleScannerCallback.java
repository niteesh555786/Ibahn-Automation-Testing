package com.bizbrolly.bluetoothlibrary.Callbacks;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import com.bizbrolly.bluetoothlibrary.Logger;
import com.bizbrolly.bluetoothlibrary.csr_ble_2_1.CsrScanner;

public abstract class BleScannerCallback {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final void processScanResult(ScanResult result){
        BluetoothDevice device = result.getDevice();
        byte[] scanRecord = result.getScanRecord().getBytes();

        if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE
                && device.getName() != null
                && device.getName().toLowerCase().contains("ibahn")
//                && scanRecord[CsrScanner.INDEX_UUID_1] == CsrScanner.UUID_1
//                && scanRecord[CsrScanner.INDEX_UUID_2] == CsrScanner.UUID_2
                ) {
            onScan(device, result.getRssi(), scanRecord);
        }
    }

    public final void processScanResult(BluetoothDevice device, int rssi, byte[] scanRecord){
        Logger.log("Found: " + device.getAddress());
        if (
//                device.getType() == BluetoothDevice.DEVICE_TYPE_LE
//                && device.getName() != null
//                && device.getName().toLowerCase().contains("ibahn")
                scanRecord[CsrScanner.INDEX_UUID_1] == CsrScanner.UUID_1
                && scanRecord[CsrScanner.INDEX_UUID_2] == CsrScanner.UUID_2
                ) {
            onScan(device, rssi, scanRecord);
        }
    }

    public abstract void onScan(BluetoothDevice device, int rssi, byte[] scanRecord);

    public abstract void onScanFinished(boolean failed);
}