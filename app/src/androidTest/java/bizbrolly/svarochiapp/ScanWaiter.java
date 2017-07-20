package bizbrolly.svarochiapp;

import android.support.test.espresso.IdlingResource;

import bizbrolly.svarochiapp.activities.MainActivity;

/**
 * Created by Jaadugar on 7/5/2017.
 */

public class ScanWaiter implements IdlingResource {
    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public boolean isIdleNow() {
        return !MainActivity.isScanning.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        ScanChecker tempScanChecker = new ScanChecker();
        tempScanChecker.setCallback(callback);
        tempScanChecker.start();
    }

    private class ScanChecker extends Thread{
        private ResourceCallback callback;

        public void setCallback(ResourceCallback callback){
            this.callback = callback;
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()){
                if (!MainActivity.isScanning.get())
                    callback.onTransitionToIdle();
            }
        }
    }
}
