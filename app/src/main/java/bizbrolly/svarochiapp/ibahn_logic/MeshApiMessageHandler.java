package bizbrolly.svarochiapp.ibahn_logic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import com.akkipedia.skeleton.utils.Logger;
import com.csr.csrmesh2.MeshConstants;
import java.lang.ref.WeakReference;
import bizbrolly.svarochiapp.activities.BaseCsrActivity;
import bizbrolly.svarochiapp.activities.HomeActivity;
import bizbrolly.svarochiapp.activities.MainActivity;
import bizbrolly.svarochiapp.activities.TestActivity;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;
import bizbrolly.svarochiapp.model.devices.ScanDevice;

/**
 * Created by Akash on 10/05/17.
 */

public class MeshApiMessageHandler extends Handler {

    private WeakReference<BaseCsrActivity> mParent;

    private WeakReference<BlockDataCallback> blockDataCallback;

    public void setRecoveryModule(BlockDataCallback blockDataCallback) {
        this.blockDataCallback = new WeakReference<>(blockDataCallback);
    }

    public MeshApiMessageHandler(BaseCsrActivity machine) {
        this.mParent = new WeakReference<BaseCsrActivity>(machine);
    }

    public void setParent(BaseCsrActivity machine) {
        this.mParent = new WeakReference<BaseCsrActivity>(machine);
    }

    @Override
    public void handleMessage(Message msg) {
        if(mParent != null && mParent.get() != null && mParent.get().isFinishing() && mParent.get().isDestroyed())
            return;

        Bundle data = msg.getData();
        for(String key : data.keySet()){
            Logger.log("MESH Handler", key + " : "+ data.get(key));
        }

        switch (msg.what) {
            case MeshConstants.MESSAGE_LE_BEARER_READY: {
                Logger.log("MESSAGE_LE_BEARER_READY");
                if(mParent != null && mParent.get() instanceof MainActivity){
                    ((MainActivity) mParent.get()).onBearerReady();
                }
                break;
            }
            case MeshConstants.MESSAGE_REST_BEARER_READY: {
                break;
            }
            case MeshConstants.MESSAGE_LE_CONNECTED: {
                Logger.log("MESSAGE_LE_CONNECTED");
                mParent.get().onConnected();
                break;
            }
            case MeshConstants.MESSAGE_LE_DISCONNECTED: {
                mParent.get().onDisconnected();
                break;
            }
            case MeshConstants.MESSAGE_LE_DISCONNECT_COMPLETE: {
                break;
            }

            case MeshConstants.MESSAGE_GATEWAY_SERVICE_DISCOVERED: {
                break;
            }
            case MeshConstants.MESSAGE_DEVICE_APPEARANCE: {
                byte[] appearance = data.getByteArray(MeshConstants.EXTRA_APPEARANCE);
                String shortName = data.getString(MeshConstants.EXTRA_SHORTNAME);
                int uuidHash = data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                ScanDevice scanDevice = new ScanDevice("", 0, uuidHash, 0);
                scanDevice.setAppearanceDevice(new AppearanceDevice(appearance, shortName));
                //TODO: Send to adapter(check on the basis of UUID hash)
                if (mParent.get() instanceof HomeActivity) {
                    ((HomeActivity) mParent.get()).updateDevice(scanDevice);
                }
                break;
            }
            case MeshConstants.MESSAGE_RESET_DEVICE:
                    /* The application can handle a request to reset here.
                     * It should calculate the signature using ConfigModelApi.computeResetDeviceSignatureWithDeviceHash(long, byte[])
                     * to check the signature is valid.
                     */
                break;
            case MeshConstants.MESSAGE_BATTERY_STATE:
                break;
            case MeshConstants.MESSAGE_LIGHT_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_POWER_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_ACTION_SENT: {
                break;
            }
            case MeshConstants.MESSAGE_ACTION_DELETE: {
                break;
            }
            case MeshConstants.MESSAGE_ASSOCIATING_DEVICE: {
                Log.e("TEST", "MESSAGE_ASSOCIATING_DEVICE");
                break;
            }
            case MeshConstants.MESSAGE_LOCAL_DEVICE_ASSOCIATED: {
                break;
            }
            case MeshConstants.MESSAGE_LOCAL_ASSOCIATION_FAILED: {
                break;
            }
            case MeshConstants.MESSAGE_ASSOCIATION_PROGRESS: {
                Log.e("TEST", "MESSAGE_ASSOCIATION_PROGRESS");
                break;
            }
            case MeshConstants.MESSAGE_NETWORK_SECURITY_UPDATE: {
                break;
            }
            case MeshConstants.MESSAGE_REQUEST_BT: {
                break;
            }
            case MeshConstants.MESSAGE_DEVICE_ASSOCIATED:
                Log.e("TEST", "MESSAGE_DEVICE_ASSOCIATED");
                if (mParent.get() instanceof HomeActivity) {
                    int deviceId = data.getInt(MeshConstants.EXTRA_DEVICE_ID);
                    int uuidHash = data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                    byte[] dhmKey = data.getByteArray(MeshConstants.EXTRA_RESET_KEY);
                    ScanDevice mTempDevice = ((HomeActivity) mParent.get()).homeDevicesAdapter.associateDevice;
                    mTempDevice.setDeviceID(deviceId);
                    mTempDevice.setDeviceHash(uuidHash);
                    mTempDevice.setDmKey(dhmKey);
                    mTempDevice.setAssociated(true);
                    ((HomeActivity) mParent.get()).onDeviceAssociated(mTempDevice);
                }
                break;
            case MeshConstants.MESSAGE_TIMEOUT:
                break;

            case MeshConstants.MESSAGE_ATTENTION_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_ACTUATOR_VALUE_ACK:
                break;
            case MeshConstants.MESSAGE_ACTUATOR_TYPES:
                break;
            case MeshConstants.MESSAGE_BEARER_STATE:
                break;

            case MeshConstants.MESSAGE_SENSOR_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_SENSOR_VALUE: {
                break;
            }
            case MeshConstants.MESSAGE_SENSOR_TYPES: {
                break;
            }
            case MeshConstants.MESSAGE_PING_RESPONSE: {
                break;
            }
            case MeshConstants.MESSAGE_GROUP_NUM_GROUPIDS: {
                break;
            }
            case MeshConstants.MESSAGE_GROUP_MODEL_GROUPID: {
                if (mParent.get() instanceof HomeActivity) {
                    ((HomeActivity) mParent.get()).onGroupConfirmed(data);
                }
                break;
            }
            case MeshConstants.MESSAGE_FIRMWARE_VERSION: {
                break;
            }
            case MeshConstants.MESSAGE_DATA_SENT: {
                break;
            }
            case MeshConstants.MESSAGE_RECEIVE_BLOCK_DATA: {
                if(blockDataCallback != null && blockDataCallback.get() != null){
                    blockDataCallback.get().onBlockData(data);
                }
                break;
            }
            case MeshConstants.MESSAGE_RECEIVE_STREAM_DATA: {
                break;
            }
            case MeshConstants.MESSAGE_RECEIVE_STREAM_DATA_END: {
                break;
            }
            case MeshConstants.MESSAGE_DEVICE_ID: {
                break;
            }
            case MeshConstants.MESSAGE_CONFIG_DEVICE_INFO: {
                break;
            }
            case MeshConstants.MESSAGE_DEVICE_DISCOVERED: {
                ParcelUuid uuid = data.getParcelable(MeshConstants.EXTRA_UUID);
                int uuidHash = data.getInt(MeshConstants.EXTRA_UUIDHASH_31);
                int rssi = data.getInt(MeshConstants.EXTRA_RSSI);
                int ttl = data.getInt(MeshConstants.EXTRA_TTL);
                boolean existing = false;
                ScanDevice device = new ScanDevice(uuid.toString().toUpperCase(), rssi, uuidHash, ttl);
                //TODO: Send this device to Ayush
                if (mParent.get() instanceof HomeActivity) {
                    ((HomeActivity) mParent.get()).updateDevice(device);
                }
                break;
            }
            case MeshConstants.MESSAGE_PARAMETERS: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_PROFILE: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_REMOVE_NETWORK: {
                break;
            }
            case MeshConstants.MESSAGE_TENANT_RESULTS: {
                break;
            }
            case MeshConstants.MESSAGE_TENANT_CREATED: {
                break;
            }
            case MeshConstants.MESSAGE_TENANT_INFO: {
                break;
            }
            case MeshConstants.MESSAGE_TENANT_DELETED: {
                break;
            }
            case MeshConstants.MESSAGE_TENANT_UPDATED: {
                break;
            }
            case MeshConstants.MESSAGE_SITE_RESULTS: {
                break;
            }
            case MeshConstants.MESSAGE_SITE_CREATED: {
                break;
            }
            case MeshConstants.MESSAGE_SITE_INFO: {
                break;
            }
            case MeshConstants.MESSAGE_SITE_DELETED: {
                break;
            }
            case MeshConstants.MESSAGE_SITE_UPDATED: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_FILE_INFO: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_FILE: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_FILE_CREATED: {
                break;
            }
            case MeshConstants.MESSAGE_GATEWAY_FILE_DELETED: {
                break;
            }
            case MeshConstants.MESSAGE_FIRMWARE_UPDATE_ACKNOWLEDGED: {
                break;
            }
            case MeshConstants.MESSAGE_TRANSACTION_NOT_CANCELLED: {
                break;
            }
            case MeshConstants.MESSAGE_REST_ERROR: {
                break;
            }
            case MeshConstants.MESSAGE_TIME_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_LOT_ANNOUNCE: {
                break;
            }
            case MeshConstants.MESSAGE_LOT_INTEREST: {
                break;
            }
            case MeshConstants.MESSAGE_DIAGNOSTIC_STATE: {
                break;
            }
            case MeshConstants.MESSAGE_DIAGNOSTIC_STATS: {
                break;
            }
            case MeshConstants.MESSAGE_TRACKER_REPORT: {
                break;
            }
            case MeshConstants.MESSAGE_WATCHDOG_INTERVAL: {
                break;
            }
            case MeshConstants.MESSAGE_WATCHDOG_MESSAGE: {
                break;
            }
            case MeshConstants.MESSAGE_TRACKER_FOUND: {
                break;
            }

            default:
                break;
        }
    }

    public interface BlockDataCallback{
        void onBlockData(Bundle dataBlock);
    }
}
