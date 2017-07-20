package bizbrolly.svarochiapp.database.enitities;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

import bizbrolly.svarochiapp.database.AppDatabase;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;
import bizbrolly.svarochiapp.model.devices.ScanDevice;

/**
 * Created by Ayush on 27/04/17.
 */
@Table(database = AppDatabase.class)
public class AssociatedDevice extends BaseModel {
    @PrimaryKey
    private int deviceId;

    @Column
    private Blob resetKey;

    @Column
    private int deviceHash;

    @Column
    private String uuid;

    @Column
    private int rssi;

    @Column
    private int uuidHash;

    @Column
    private long timeStamp;

    @Column
    private int ttl;

    @Column
    private Blob appearanceCode;

    @Column
    private String shortName;

    @Column
    private int type = -1;

    @Column
    private long authCode;

    @Column
    private int model;

    @Column
    private int placeID;

    @Column
    private boolean isFavourite;

    @Column
    private boolean isAssociated;

    @Column
    private String name;

    @Column
    private int appearance;

    @Column
    private long modelHigh;

    @Column
    private long modelLow;

    @Column
    private long uuidHigh;

    @Column
    private long uuidLow;

    @Column
    private int numGroups;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUuid() {
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

    public int getUuidHash() {
        return uuidHash;
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

    public Blob getAppearanceCode() {
        return appearanceCode;
    }

    public void setAppearanceCode(Blob mAppearanceCode) {
        this.appearanceCode = mAppearanceCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String mShortName) {
        this.shortName = mShortName;
    }
//
//    public int getmAppearance() {
//        return mAppearance;
//    }
//
//    public void setmAppearance(int mAppearance) {
//        this.mAppearance = mAppearance;
//    }

    public long getAuthCode() {
        return authCode;
    }

    public void setAuthCode(long authCode) {
        this.authCode = authCode;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isAssociated() {
        return isAssociated;
    }

    public void setAssociated(boolean associated) {
        isAssociated = associated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAppearance() {
        return appearance;
    }

    public void setAppearance(int appearance) {
        this.appearance = appearance;
    }

    public long getModelHigh() {
        return modelHigh;
    }

    public void setModelHigh(long modelHigh) {
        this.modelHigh = modelHigh;
    }

    public long getModelLow() {
        return modelLow;
    }

    public void setModelLow(long modelLow) {
        this.modelLow = modelLow;
    }

    public long getUuidHigh() {
        return uuidHigh;
    }

    public void setUuidHigh(long uuidHigh) {
        this.uuidHigh = uuidHigh;
    }

    public long getUuidLow() {
        return uuidLow;
    }

    public void setUuidLow(long uuidLow) {
        this.uuidLow = uuidLow;
    }

    public int getNumGroups() {
        return numGroups;
    }

    public void setNumGroups(int numGroups) {
        this.numGroups = numGroups;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public Blob getResetKey() {
        return resetKey;
    }

    public void setResetKey(Blob resetKey) {
        this.resetKey = resetKey;
    }

    public int getDeviceHash() {
        return deviceHash;
    }

    public void setDeviceHash(int deviceHash) {
        this.deviceHash = deviceHash;
    }

    public static AssociatedDevice getAssociatedDevice(ScanDevice device) {
        AssociatedDevice associatedDevice = new AssociatedDevice();
        associatedDevice.setDeviceHash(device.getDeviceHash());
        associatedDevice.setDeviceId(device.getDeviceID());
        associatedDevice.setResetKey(new Blob(device.getDmKey()));
        associatedDevice.setUuid(device.getUuidString());
        associatedDevice.setRssi(device.getRssi());
        associatedDevice.setUuidHash(device.getUuidHash());
        associatedDevice.setTimeStamp(device.getTimeStamp());
        associatedDevice.setTtl(device.getTtl());
        if (device.getAppearanceDevice() != null) {
            associatedDevice.setAppearanceCode(new Blob(device.getAppearanceDevice().getAppearanceCode()));
            associatedDevice.setShortName(device.getAppearanceDevice().getShortName());
            associatedDevice.setType(device.getAppearanceDevice().getType());
        }
//        associatedDevice.setmAppearance(device.getAppearanceDevice().getAppearanceType());
        associatedDevice.setAuthCode(device.getAuthCode());
        associatedDevice.setPlaceID(device.getPlaceID());
        associatedDevice.setFavourite(device.isFavourite());
        associatedDevice.setAssociated(device.isAssociated());
        associatedDevice.setAppearance(device.getAppearance());
        associatedDevice.setModelHigh(device.getModelHigh());
        associatedDevice.setModelLow(device.getModelLow());
        associatedDevice.setUuidHigh(device.getUuidHigh());
        associatedDevice.setUuidLow(device.getUuidLow());
        associatedDevice.setNumGroups(device.getNumGroups());
        return associatedDevice;
    }

    public static ScanDevice getScanDevice(AssociatedDevice device) {
        ScanDevice scanDevice = new ScanDevice(device.getUuid(), device.getRssi(), device.getUuidHash(), device.getTtl());
        scanDevice.setDeviceHash(device.getDeviceHash());
        scanDevice.setDeviceID(device.getDeviceId());
        scanDevice.setDmKey(device.getResetKey().getBlob());
        scanDevice.setUuid(device.getUuid());
        scanDevice.setRssi(device.getRssi());
        scanDevice.setUuidHash(device.getUuidHash());
        scanDevice.setTimeStamp(device.getTimeStamp());
        scanDevice.setTtl(device.getTtl());
        AppearanceDevice appearanceDevice = new AppearanceDevice(device.getAppearance(), device.getShortName());
        scanDevice.setAppearanceDevice(appearanceDevice);
        if (device.getAppearanceCode() != null)
            scanDevice.getAppearanceDevice().setAppearanceCode(device.getAppearanceCode().getBlob());
        scanDevice.getAppearanceDevice().setShortName(device.getShortName());
        scanDevice.setType(device.getType());
        scanDevice.setAuthCode(device.getAuthCode());
        scanDevice.setPlaceID(device.getPlaceID());
        scanDevice.setFavourite(device.isFavourite());
        scanDevice.setAssociated(device.isAssociated());
        scanDevice.setAppearance(device.getAppearance());
        scanDevice.setModelHigh(device.getModelHigh());
        scanDevice.setModelLow(device.getModelLow());
        scanDevice.setUuidHigh(device.getUuidHigh());
        scanDevice.setUuidLow(device.getUuidLow());
        scanDevice.setNumGroups(device.getNumGroups());
        return scanDevice;
    }

    @Override
    public boolean save() {
        return super.save();
    }
}
