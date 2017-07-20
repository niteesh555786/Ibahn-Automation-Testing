/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/
package bizbrolly.svarochiapp.model.devices;

import com.csr.csrmesh2.MeshConstants;

import bizbrolly.svarochiapp.ibahn_logic.ApplicationUtils;

public class AppearanceDevice {
    private byte[] mAppearanceCode;
    private String mShortName;
    private int mAppearance;
    private int type = -1;

    public static final int TYPE_OOD = 0;
    public static final int TYPE_CCT = 1;
    public static final int TYPE_RGB = 2;

    public static int CONTROLLER_APPEARANCE = MeshConstants.CONTROLLER_APPEARANCE;
    public static int LIGHT_APPEARANCE = MeshConstants.LIGHT_APPEARANCE;
    public static int HEATER_APPEARANCE = MeshConstants.HEATER_APPEARANCE;
    public static int SENSOR_APPEARANCE = MeshConstants.SENSOR_APPEARANCE;
    public static int GATEWAY_APPEARANCE = MeshConstants.GATEWAY_APPEARANCE;


    public int getType() {
        return type;
    }

    public AppearanceDevice(byte[] appearanceCode, String shortName) {
        setAppearanceCode(appearanceCode);
        setShortName(shortName);

        mAppearance = ApplicationUtils.convertBytesToInteger(appearanceCode, false);
    }

    public AppearanceDevice(int appearance, String mShortName) {
        mAppearance = appearance;
        setShortName(mShortName == null ? getNameByAppearance() : mShortName);
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String mShortName) {
        this.mShortName = mShortName;
        String[] split = mShortName.split(" ");

        int index = -1;
        try {
            index = Integer.parseInt(split[split.length - 1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (mShortName.contains("CCT") || mShortName.toLowerCase().contains("warm")) {
            type = TYPE_CCT;
            this.mShortName = "Warm & Cool";
        } else if (mShortName.contains("OOD") || mShortName.toLowerCase().contains("bright")) {
            type = TYPE_OOD;
            this.mShortName = "Bright & Dim";
        } else if (mShortName.contains("RGB")) {
            this.mShortName = "RGBW";
            type = TYPE_RGB;
        }
        if (index != -1)
            this.mShortName += " " + index;
    }

    public byte[] getAppearanceCode() {
        return mAppearanceCode;
    }

    public void setAppearanceCode(byte[] mAppearanceCode) {
        this.mAppearanceCode = mAppearanceCode;
    }

    public int getAppearanceType() {
        return mAppearance;
    }

    private String getNameByAppearance() {
        if (mAppearance == LIGHT_APPEARANCE) {
            return "Light";
        } else if (mAppearance == HEATER_APPEARANCE) {
            return "Heater";
        } else if (mAppearance == SENSOR_APPEARANCE) {
            return "Sensor";
        } else if (mAppearance == CONTROLLER_APPEARANCE) {
            return "Controller";
        } else {
            return "Unknown";
        }
    }

}
