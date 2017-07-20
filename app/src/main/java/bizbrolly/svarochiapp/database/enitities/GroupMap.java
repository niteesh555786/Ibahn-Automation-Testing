package bizbrolly.svarochiapp.database.enitities;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import bizbrolly.svarochiapp.database.AppDatabase;

/**
 * Created by Ayush on 03/05/17.
 */
@Table(database = AppDatabase.class)
public class GroupMap extends BaseModel {
    @PrimaryKey
    private String groupName;

    @PrimaryKey
    private int deviceId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
