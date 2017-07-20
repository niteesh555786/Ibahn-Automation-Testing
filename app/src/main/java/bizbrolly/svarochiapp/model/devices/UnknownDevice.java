/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package bizbrolly.svarochiapp.model.devices;

/**
 *
 */
public class UnknownDevice extends Device {

    // Constructor
    public UnknownDevice() {
    }

    @Override
    public int getType() {
        return TYPE_UNKNOWN;
    }

}
