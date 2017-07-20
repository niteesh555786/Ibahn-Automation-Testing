package com.bizbrolly.bluetoothlibrary.AbstractClasses;

import android.os.IBinder;

import com.bizbrolly.bluetoothlibrary.Callbacks.BleScannerCallback;

/**
 * Created by Ayush on 25/04/17.
 */

public abstract class Scanner {
    public static int SCAN_TIMEOUT_PERIOD = 40000;

    public abstract void startScan(BleScannerCallback scannerCallback);

    public abstract void startScan(BleScannerCallback scannerCallback, IBinder service);

    public abstract void startScan(BleScannerCallback scannerCallback, int scanTimeout);

    public abstract void stopScan();

    public abstract void setScanTimeout(int scanTimeout);
}
