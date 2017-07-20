package bizbrolly.svarochiapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.akkipedia.skeleton.activities.BaseSkeletonActivity;
import com.akkipedia.skeleton.utils.GeneralUtils;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.database.enitities.DeviceIdGenerator;
import bizbrolly.svarochiapp.database.enitities.DeviceIdGenerator_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.ibahn_logic.DbScriptHelper;

public class SplashActivity extends BaseSkeletonActivity {


    private static final int BLE_NOT_SUPPORTED = 869;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).where(Group_Table.groupId.eq(0)).queryList().size() == 0) {
                Group allGroup = new Group();
                allGroup.setGroupName("All LED Lamps");
                allGroup.save();
                SQLite.update(Group.class).set(Group_Table.groupId.eq(0)).where(Group_Table.groupName.eq(allGroup.getGroupName())).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SQLite.select(DeviceIdGenerator_Table.ALL_COLUMN_PROPERTIES).from(DeviceIdGenerator.class).queryList().size() == 0) {
            DeviceIdGenerator deviceIdGenerator = new DeviceIdGenerator();
            deviceIdGenerator.setDeviceId(0x8001);
            deviceIdGenerator.save();
        }
        if (!GeneralUtils.isBleSupported(this)) {
            showAlertDialog(getString(R.string.error), getString(R.string.ble_not_supported), getString(R.string.ok), BLE_NOT_SUPPORTED);
            return;
        }
        Stetho.initializeWithDefaults(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    public void onNegativeButtonClick(DialogInterface dialog, int dialogId) {
        if (dialogId == BLE_NOT_SUPPORTED)
            finishAffinity();
    }
}
