package bizbrolly.svarochiapp.ibahn_logic;


import android.util.Log;

import com.csr.csrmesh2.DataModelApi;

/**
 * Created by Ayush on 27/04/17.
 */

public class DataSender {
    private static long lastSentTime = -1;


    private static boolean shouldSendData() {
        boolean shouldSend = lastSentTime < 0
                || (System.currentTimeMillis() - lastSentTime) > 200;

        if (shouldSend)
            lastSentTime = System.currentTimeMillis();

        return shouldSend;
    }

    public static void sendData(int deviceId, String data) {
        if (!shouldSendData())
            return;

        if (data.contains("IBI")) {
            byte[] stringBytes = "IBI".getBytes();
            int stringBytesLength = stringBytes.length;
            byte[] bytesToSend = new byte[stringBytesLength + 1];
            byte intensity = (byte) Integer.parseInt(data.substring(3));
            for (int i = 0; i < stringBytesLength; i++) {
                bytesToSend[i] = stringBytes[i];
            }
            bytesToSend[stringBytesLength] = intensity;
            DataModelApi.sendData(deviceId, bytesToSend, false);
            return;
        }
        if (data.contains("IBW")) {
            byte[] stringBytes = "IBW".getBytes();
            int stringBytesLength = stringBytes.length;
            byte[] bytesToSend = new byte[stringBytesLength + 1];
            byte intensity = (byte) Integer.parseInt(data.substring(3));
            for (int i = 0; i < stringBytesLength; i++) {
                bytesToSend[i] = stringBytes[i];
            }
            bytesToSend[stringBytesLength] = intensity;
            DataModelApi.sendData(deviceId, bytesToSend, false);
            return;
        }
        Log.e("DataSender", data);
        DataModelApi.sendData(deviceId, data.getBytes(), false);
    }

    public static void sendData(int deviceId, byte[] data) {
        if (!shouldSendData())
            return;
        Log.e("DataSender", "rgb_data");
        DataModelApi.sendData(deviceId, data, false);
    }
}
