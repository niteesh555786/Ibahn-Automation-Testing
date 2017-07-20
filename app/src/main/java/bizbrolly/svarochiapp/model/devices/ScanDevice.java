/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package bizbrolly.svarochiapp.model.devices;

/**
 *
 */
public class ScanDevice extends Device implements Comparable<ScanDevice> {

    private static final long TIME_SCANINFO_VALID = 5 * 1000; // 5 secs

    public String uuid;
    public int rssi;
    public int uuidHash;
    public long timeStamp;
    public int ttl;
    private int type;
    public AppearanceDevice appearanceDevice;

    // Constructor
    public ScanDevice(String uuid, int rssi, int uuidHash, int ttl) {
        this.uuid = uuid;
        this.rssi = rssi;
        this.uuidHash = uuidHash;
        this.ttl = ttl;
        updated();
    }

    public void updated() {
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        if (appearanceDevice != null) {
            return appearanceDevice.getShortName();
        }
        return null;
    }


    public String getUuidString() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setUuidHash(int uuidHash) {
        this.uuidHash = uuidHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean isFavourite() {
        return false;
    }

    @Override
    public void setFavourite(boolean favourite) { // Empty
    }

    public int getUuidHash() {
        return this.uuidHash;
    }

    /**
     * This method check if the timeStamp of the last update is still valid or not (time<TIME_SCANINFO_VALID).
     *
     * @return true if the info is still valid
     */
    public boolean isInfoValid() {
        return ((System.currentTimeMillis() - this.timeStamp) < TIME_SCANINFO_VALID);
    }

    @Override
    public int compareTo(ScanDevice info) {
        final int LESS_THAN = -1;
        final int GREATER_THAN = 1;
        final int EQUAL = 0;

        // Compare to is used for sorting the list in ascending order.
        // Smaller number of hops (highest TTL) should be at the top of the list.
        // For items with the same TTL, largest signal strength (highest RSSI) should be at the top of the list.
        if (this.ttl > info.ttl) {
            return LESS_THAN;
        }
        else if (this.ttl < info.ttl) {
            return GREATER_THAN;
        }
        else if (this.rssi > info.rssi) {
            return LESS_THAN;
        }
        else if (this.rssi < info.rssi) {
            return GREATER_THAN;
        }
        else {
            return EQUAL;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getUuidHash()== ((ScanDevice)obj).getUuidHash()){
            return true;
        }
        return false;
    }

    public void setAppearanceDevice(AppearanceDevice scanAppearance) {
        appearanceDevice = scanAppearance;
        setType(scanAppearance.getType());
    }

    public boolean hasAppearance() {
        return appearanceDevice != null;
    }

    public AppearanceDevice getAppearanceDevice() {
        return appearanceDevice;
    }

}
