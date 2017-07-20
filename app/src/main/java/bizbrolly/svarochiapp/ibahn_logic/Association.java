/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package bizbrolly.svarochiapp.ibahn_logic;

import android.os.Bundle;

import com.csr.csrmesh2.MeshConstants;
import com.csr.csrmesh2.MeshService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class covers the APIs related to association such as associateDevice, resetDevice, Attention, etc.
 */
public class Association {
    private static AtomicInteger requestId = new AtomicInteger(0);


    public static void discoverDevices(boolean enabled, MeshService meshService) {
        Bundle data = new Bundle();
        data.putBoolean(MeshConstants.EXTRA_ENABLED, enabled);
        meshService.setDeviceDiscoveryFilterEnabled(enabled);
    }

    public static int associateDevice(int deviceHash, long authorizationCode, boolean authorizationCodeKnown, int deviceId, MeshService meshService) {
        Bundle data = new Bundle();
        int id = requestId.incrementAndGet();
        data.putInt(MeshConstants.EXTRA_UUIDHASH_31, deviceHash);
        data.putLong(MeshConstants.EXTRA_AUTH_CODE, authorizationCode);
        data.putBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN, authorizationCodeKnown);
        data.putInt(MeshConstants.EXTRA_DEVICE_ID, deviceId);
        meshService.associateDevice(
                data.getInt(MeshConstants.EXTRA_UUIDHASH_31),
                data.getLong(MeshConstants.EXTRA_AUTH_CODE),
                data.getBoolean(MeshConstants.EXTRA_AUTH_CODE_KNOWN),
                data.getInt(MeshConstants.EXTRA_DEVICE_ID));
        return id;
    }

}
