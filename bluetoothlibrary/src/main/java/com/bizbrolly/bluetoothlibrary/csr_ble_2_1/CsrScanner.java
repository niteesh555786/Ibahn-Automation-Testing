package com.bizbrolly.bluetoothlibrary.csr_ble_2_1;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.bizbrolly.bluetoothlibrary.AbstractClasses.Scanner;
import com.bizbrolly.bluetoothlibrary.Callbacks.BleScannerCallback;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ayush on 25/04/17.
 */

public class CsrScanner extends Scanner {


    public static final int INDEX_UUID_1 = 5;
    public static final int INDEX_UUID_2 = 6;
    public static final byte UUID_1 = (byte) 0xF1;
    public static final byte UUID_2 = (byte) 0xFE;
    public static final int NO_TIMEOUT = -1;

    //Default: 10s
    private BleScannerCallback scannerCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler timeOutHandler;
    private AtomicBoolean isScanning;
    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };


    public CsrScanner(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        timeOutHandler = new Handler();
        isScanning = new AtomicBoolean(false);
    }

    public CsrScanner(BluetoothAdapter mBluetoothAdapter, int scanTimeOutPeriod) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        SCAN_TIMEOUT_PERIOD = scanTimeOutPeriod;
        timeOutHandler = new Handler();
    }

    @Override
    public void setScanTimeout(int scanTimeout) {
        SCAN_TIMEOUT_PERIOD = scanTimeout;
    }

    private BluetoothAdapter.LeScanCallback leScanCallback;
    private ScanCallback newLeScanCallback;

    @Override
    public void startScan(BleScannerCallback scannerCallback) {
        if (!isScanning.get()) {
            isScanning.set(true);
            this.scannerCallback = scannerCallback;
            if (SCAN_TIMEOUT_PERIOD > 0)
                timeOutHandler.postDelayed(timeOutRunnable, SCAN_TIMEOUT_PERIOD);
            startScanActual();
        }
    }

    @Override
    public void startScan(BleScannerCallback scannerCallback, IBinder service) {

    }

    @Override
    public void startScan(BleScannerCallback scannerCallback, int scanTimeout) {
        if (!isScanning.get()) {
            SCAN_TIMEOUT_PERIOD = scanTimeout;
            startScan(scannerCallback);
        }
    }

    @Override
    public void stopScan() {
        if (isScanning.get()) {
            isScanning.set(false);
            timeOutHandler.removeCallbacks(timeOutRunnable);
            scannerCallback.onScanFinished(false);
            stopScanActual();
        }
    }

    private void startScanActual() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (newLeScanCallback == null)
                newLeScanCallback = new ScanCallback() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        scannerCallback.processScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                    }

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        for (ScanResult result : results) {
                            scannerCallback.processScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        scannerCallback.onScanFinished(true);
                    }
                };
            mBluetoothAdapter.getBluetoothLeScanner().startScan(newLeScanCallback);
        } else {
            if (leScanCallback == null)
                leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        scannerCallback.processScanResult(device, rssi, scanRecord);
                    }
                };
            mBluetoothAdapter.startLeScan(leScanCallback);
        }
    }


    private void stopScanActual() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(newLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

}
