/******************************************************************************
 * Copyright 2015 Qualcomm Technologies International, Ltd.
 ******************************************************************************/

package bizbrolly.svarochiapp.model;

/**
 *
 */
public class Place {

    private int id = -1;
    private String name = "";
    private String passphrase = "";
    private byte[] networkKey;
    private String cloudSiteID = "";
    private int iconID;
    private int color;
    private int hostControllerID;
    private int settingsID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public byte[] getNetworkKey() {
        return networkKey;
    }

    public void setNetworkKey(byte[] networkKey) {
        this.networkKey = networkKey;
    }

    public String getCloudSiteID() {
        return cloudSiteID;
    }

    public void setCloudSiteID(String cloudSiteID) {
        this.cloudSiteID = cloudSiteID;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getHostControllerID() {
        return hostControllerID;
    }

    public void setHostControllerID(int hostControllerID) {
        this.hostControllerID = hostControllerID;
    }

    public int getSettingsID() {
        return settingsID;
    }

    public void setSettingsID(int settingsID) {
        this.settingsID = settingsID;
    }
}
