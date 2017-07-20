package bizbrolly.svarochiapp.ibahn_logic;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.akkipedia.skeleton.utils.Logger;

import bizbrolly.svarochiapp.activities.MainActivity;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;

/**
 * Created by Akash on 10/05/17.
 * Helper class to fetch previous state
 * Logic specific to iBahn firmware
 */

public class RecoveryModuleTemp {
    private static final int TIMEOUT = 3333;
    private static String currentData;
    private static boolean waitingForData;

    public static void fetchState(final int type, final int deviceId, final Callback callback) {
        final State state = new State();
        MainActivity.meshApiMessageHandler.setRecoveryModule(new MeshApiMessageHandler.BlockDataCallback() {
            @Override
            public void onBlockData(Bundle dataBlock) {
                waitingForData = false;
                beginTimeout(callback);
                switch (currentData) {
                    case Data.POWER: {
                        Logger.log("POWER");
                        state.setPower(dataBlock.getByteArray("DatagramOctets")[0]);
                        DataSender.sendData(deviceId, currentData = Data.INTENSITY_LEVEL);
                        waitingForData = true;
                    }
                    break;
                    case Data.INTENSITY_LEVEL: {
                        state.setIntensityLevel(dataBlock.getByteArray("DatagramOctets")[0]);
                        if (type == AppearanceDevice.TYPE_OOD) {
                            callback.onResponse(state);
                        } else {
                            DataSender.sendData(deviceId, currentData = Data.TUNABLE_LEVEL);
                            waitingForData = true;
                        }
                    }
                    break;
                    case Data.TUNABLE_LEVEL:
                        state.setIntensityLevel(dataBlock.getByteArray("DatagramOctets")[0]);
                        callback.onResponse(state);
                        cancleTimeOut();
                        break;

                }
            }
        });
        waitingForData = true;
        beginTimeout(callback);
        basicTimeOut(callback);
        DataSender.sendData(deviceId, currentData = Data.POWER);
    }


    private static Handler handler;
    private static Runnable runnable;

    private static void beginTimeout(final Callback callback) {
//        if (runnable == null) {
//            handler = new Handler();
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    if (waitingForData)
//                        callback.onResponse(null);
//                }
//            };
//        }
//        handler.removeCallbacks(runnable);
//        handler.postDelayed(runnable, TIMEOUT);
    }

    private static void cancleTimeOut(){
        handler.removeCallbacks(runnable);
    }

    private static void basicTimeOut(final Callback callback) {
        if (runnable == null) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(null);
                }
            };
        }
        handler.postDelayed(runnable, TIMEOUT);
    }

    public interface Callback {
        void onResponse(State deviceState);
    }

    public static class State {
        private boolean power;
        private byte intensityLevel;
        private byte tunnableLevel;

        public boolean isPower() {
            return power;
        }

        public void setPower(boolean power) {
            this.power = power;
        }

        public void setPower(byte powerState) {
            this.power = powerState == 1;
        }

        public byte getIntensityLevel() {
            return intensityLevel;
        }

        public void setIntensityLevel(byte intensityLevel) {
            this.intensityLevel = intensityLevel;
        }

        public byte getTunnableLevel() {
            return tunnableLevel;
        }

        public void setTunnableLevel(byte tunnableLevel) {
            this.tunnableLevel = tunnableLevel;
        }
    }

    private interface Data {
        static final String POWER = "L0";
        static final String INTENSITY_LEVEL = "L1";
        static final String TUNABLE_LEVEL = "L2";
    }
}
