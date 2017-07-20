package bizbrolly.svarochiapp.ibahn_logic;

import android.content.Context;
import android.util.Base64;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;

import bizbrolly.svarochiapp.database.AppDatabase;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.GroupMap;
import bizbrolly.svarochiapp.database.enitities.GroupMap_Table;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;
import bizbrolly.svarochiapp.model.devices.ScanDevice;

/**
 * Created by Akash on 25/06/17.
 */

public class DbScriptHelper {


    public void parseScript(Context context, String scriptString) throws JSONException {
        JSONObject jsonObject = new JSONObject(scriptString);
        String networkKey = jsonObject.getString("NetworkKey");
        Preferences.getInstance(context).setNetworkPassword(networkKey);

        //Delete all devices from DB
        SQLite.delete().from(AssociatedDevice.class).execute();

        //Delete all groups from DB except all lights group
        Group allLampGroup = SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class)
                .where(Group_Table.groupName.eq("All LED Lamps"))
                .querySingle();
        SQLite.delete().from(Group.class).execute();
        if (allLampGroup != null) {
            allLampGroup.save();
            SQLite.update(Group.class).set(Group_Table.groupId.eq(1)).where(Group_Table.groupName.eq("All LED Lamps")).execute();
        }

        //Reset DB sequences
        String query = "DELETE FROM sqlite_sequence where name='Group'";
        FlowManager.getDatabase(AppDatabase.class)
                .getHelper()
                .getDatabase()
                .rawQuery(query, new String[]{});


        JSONArray devices = jsonObject.getJSONArray("Device");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject jsonDevice = devices.getJSONObject(i);
            ScanDevice scanDevice = new ScanDevice(
                    jsonDevice.getString("uuid"),
                    0,
                    jsonDevice.getString("uuid").hashCode(),
                    9
            );
            scanDevice.setAppearanceDevice(new AppearanceDevice(
                    jsonDevice.getInt("appearanceValue"),
                    jsonDevice.getString("name")
            ));
            scanDevice.setDeviceID(jsonDevice.getInt("id"));
            scanDevice.setDmKey(Base64.decode(jsonDevice.getString("dhmKey"), Base64.DEFAULT));
            scanDevice.setAssociated(true);
            AssociatedDevice.getAssociatedDevice(scanDevice).save();
        }

        JSONArray groups = jsonObject.getJSONArray("Group");
        for (int i = 0; i < groups.length(); i++) {
            JSONObject jsonGroup = groups.getJSONObject(i);
            Group group = new Group();
            group.setGroupId(jsonGroup.getInt("id"));
            group.setGroupName(jsonGroup.getString("name"));
            group.save();
            SQLite.update(Group.class).set(Group_Table.groupId.eq(jsonGroup.getInt("id"))).where(Group_Table.groupName.eq(group.getGroupName())).execute();
            JSONArray jsonDeviceArray = jsonGroup.getJSONArray("device");
            for (int j = 0; j < jsonDeviceArray.length(); j++) {
                GroupMap groupMap = new GroupMap();
                groupMap.setGroupName(group.getGroupName());
                groupMap.setDeviceId(jsonDeviceArray.getInt(j));
                groupMap.save();
            }
        }
    }

    public String generateDbScript(Context context) throws JSONException {
        JSONObject dbScript = new JSONObject();
        dbScript.put("NetworkKey", Preferences.getInstance(context).getNetworkPassword());

        List<AssociatedDevice> associatedDevices = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList();
        JSONArray devices = new JSONArray();
        for (AssociatedDevice associatedDevice : associatedDevices) {
            JSONObject jsonDevice = new JSONObject();

            jsonDevice.put("appearanceShortname", associatedDevice.getShortName());
            jsonDevice.put("appearanceValue", associatedDevice.getAppearance());
            jsonDevice.put("deviceHash", Base64.encodeToString(intToByteArray(associatedDevice.getDeviceHash()), Base64.DEFAULT));
            jsonDevice.put("dhmKey", Base64.encodeToString(associatedDevice.getResetKey().getBlob(), Base64.DEFAULT));
            jsonDevice.put("id", associatedDevice.getDeviceId());
            jsonDevice.put("name", associatedDevice.getShortName());
            jsonDevice.put("name", associatedDevice.getShortName());
            jsonDevice.put("type", "Other");
            jsonDevice.put("uuid", associatedDevice.getUuid());


            devices.put(jsonDevice);
        }

        dbScript.put("Device", devices);


        JSONArray groups = new JSONArray();
        List<Group> groupList = SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).queryList();
        for (Group group : groupList) {
            JSONObject jsonGroup = new JSONObject();

            if (group.getGroupName().equals("All LED Lamps"))
                continue;
            jsonGroup.put("id", (group.getGroupId()));
            jsonGroup.put("name", group.getGroupName());
            JSONArray jsonDeviceArray = new JSONArray();
            List<GroupMap> groupDevices = SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES).from(GroupMap.class).where(GroupMap_Table.groupName.eq(group.getGroupName())).queryList();
            for (GroupMap groupDevice : groupDevices) {
                jsonDeviceArray.put(groupDevice.getDeviceId());
            }
            jsonGroup.put("device", jsonDeviceArray);

            groups.put(jsonGroup);
        }

        dbScript.put("Group", groups);


        return dbScript.toString();
    }

    private byte[] intToByteArray(final int i) {
        BigInteger bigInt = BigInteger.valueOf(i);
        return bigInt.toByteArray();
    }

}
