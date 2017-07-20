package bizbrolly.svarochiapp.database.enitities;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import bizbrolly.svarochiapp.database.AppDatabase;

/**
 * Created by Ayush on 07/05/17.
 */
@Table(database = AppDatabase.class)
public class DeviceIdGenerator extends BaseModel {
    @PrimaryKey
    private int deviceId;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
