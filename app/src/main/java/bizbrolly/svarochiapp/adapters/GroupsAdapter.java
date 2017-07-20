package bizbrolly.svarochiapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akkipedia.skeleton.utils.GeneralUtils;
import com.csr.csrmesh2.GroupModelApi;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.activities.ControllerActivity;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.GroupMap;
import bizbrolly.svarochiapp.database.enitities.GroupMap_Table;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.ibahn_logic.Data;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;
import bizbrolly.svarochiapp.model.devices.Device;
import bizbrolly.svarochiapp.model.devices.ScanDevice;

/**
 * Created by Ayush on 03/05/17.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {
    private List<Group> list;
    private Callback callback;

    public void setCallback() {
    }

    public GroupsAdapter() {

    }

    public GroupsAdapter(List<Group> list, Callback callback) {
        this.list = list;
        this.callback = callback;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        holder.setData();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void showGroupMenu(final Context context, final int groupId) {
        final String[] options = groupId == 1 ? new String[]{"Identify"} : new String[]{
                "Identify",
                "Show devices",
                "Rename",
                "Delete"
        };

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                options
        );

        final AlertDialog menuDialog = new AlertDialog.Builder(context)
                .setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (options[which]) {
                            case "Identify":
                                identifyGroup(groupId == 1 ? 0 : groupId);
                                break;
                            case "Delete":
                                deleteGroup(groupId);
                                break;
                            case "Rename":
                                GeneralUtils.showEditTextDialog(
                                        context,
                                        "Rename",
                                        "Enter new name",
                                        "Save",
                                        "Cancel",
                                        new GeneralUtils.EditTextDialogClickListener() {
                                            @Override
                                            public void onPositiveClick(String text) {
                                                if (text.isEmpty()) {
                                                    Toast.makeText(context, "Cannot save empty name", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                renameGroup(groupId, text);
                                            }

                                            @Override
                                            public void onNegativeClick() {

                                            }
                                        }
                                );
                                break;
                            case "Show devices":
                                GroupDevicesAdapter groupDevicesAdapter = new GroupDevicesAdapter(
                                        context,
                                        getGroupMap(SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES)
                                                .from(Group.class)
                                                .where(Group_Table.groupId.eq(groupId))
                                                .querySingle().getGroupName()),
                                        new GroupDevicesAdapter.ClickCallback() {
                                            @Override
                                            public void onItemClick(GroupMap groupMap, int position) {
                                                removeDeviceFromGroup(groupMap);
                                                if (deviceListDialog != null)
                                                    deviceListDialog.dismiss();
                                            }
                                        }
                                );


                                (deviceListDialog = new AlertDialog.Builder(context)
                                        .setAdapter(groupDevicesAdapter, null)
                                        .setTitle("Devices")
                                        .setNegativeButton("Ok", null).create())
                                        .show();
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setTitle("Group Options")
                .create();

        menuDialog.show();
    }

    private AlertDialog deviceListDialog = null;

    private String[] getDeviceNames(String groupName) {
        List<String> devices = new ArrayList<>();
        for (GroupMap groupMap : SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES)
                .from(GroupMap.class)
                .where(GroupMap_Table.groupName.eq(groupName))
                .queryList()) {
            devices.add(
                    AssociatedDevice.getScanDevice(
                            SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                                    .from(AssociatedDevice.class)
                                    .where(AssociatedDevice_Table.deviceId.eq(groupMap.getDeviceId()))
                                    .querySingle()).getName()
            );
        }
        return devices.toArray(new String[]{});
    }

    private List<GroupMap> getGroupMap(String groupName) {
        return SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES)
                .from(GroupMap.class)
                .where(GroupMap_Table.groupName.eq(groupName))
                .queryList();
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private RelativeLayout backgroundLayout;

        public void setData() {
            groupName.setText(list.get(getAdapterPosition()).getGroupName());
        }

        private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {


            public boolean onDoubleTap(MotionEvent e) {
                showGroupMenu(itemView.getContext(), list.get(getAdapterPosition()).getGroupId());
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (getAdapterPosition() != 0 && SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES)
                        .from(GroupMap.class).where(GroupMap_Table.groupName.eq(list.get(getAdapterPosition()).getGroupName()))
                        .queryList().size() == 0) {
                    Toast.makeText(itemView.getContext(), "No Device Associated with this group", Toast.LENGTH_SHORT).show();
                    return true;
                }
                int groupId = getAdapterPosition() == 0 ? 0 : list.get(getAdapterPosition()).getGroupId();
                Intent intent = new Intent(itemView.getContext(), ControllerActivity.class);
                intent.putExtra("DeviceId", groupId);
                int groupType = 2;
                String groupTypeString = list.get(getAdapterPosition()).getGroupType();
                if (groupTypeString != null
                        && groupTypeString.length() >= 0) {
                    groupType = Integer.parseInt(groupTypeString);
                } else if (groupTypeString == null) {
                    groupType = getLcm(getAdapterPosition() == 0 ? null : list.get(getAdapterPosition()).getGroupName());
                }
                intent.putExtra("Type", groupType);
                itemView.getContext().startActivity(intent);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        };

        private int getLcm(String groupName) {
            int lcm = AppearanceDevice.TYPE_RGB;
            if (groupName != null) {
                for (GroupMap groupMap : SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES).from(GroupMap.class).where(GroupMap_Table.groupName.eq(groupName)).queryList()) {
                    AssociatedDevice device = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).where(AssociatedDevice_Table.deviceId.eq(groupMap.getDeviceId())).querySingle();
                    if (lcm > device.getType())
                        lcm = device.getType();
                }
            } else {
                for (AssociatedDevice device : SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList()) {
                    if (lcm > device.getType())
                        lcm = device.getType();
                }
            }
            return lcm;
        }

        private GestureDetector gestureDetector;

        public GroupsViewHolder(final View itemView) {
            super(itemView);
            initView(itemView);
            gestureDetector = new GestureDetector(itemView.getContext(), gestureListener);
            gestureDetector.setIsLongpressEnabled(true);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
            itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_ENTERED:
                            backgroundLayout.setBackgroundResource(R.drawable.selected_group_background);
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            backgroundLayout.setBackgroundResource(R.drawable.associated_device_background);
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            backgroundLayout.setBackgroundResource(R.drawable.associated_device_background);
                            break;
                        case DragEvent.ACTION_DROP:
                            backgroundLayout.setBackgroundResource(R.drawable.associated_device_background);
                            addDeviceToGroup(itemView.getContext(), new Gson().fromJson(event.getClipData().getItemAt(0).getText().toString(), ScanDevice.class), getAdapterPosition());
                            break;
                    }
                    return true;
                }
            });
        }

        private void initView(View itemView) {
            backgroundLayout = (RelativeLayout) itemView.findViewById(R.id.background_layout);
            groupName = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    private void removeDeviceFromGroup(GroupMap groupMap) {
        int deviceIndex = -1;
        List<GroupMap> groupMaps = SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES)
                .from(GroupMap.class)
                .where(GroupMap_Table.deviceId.eq(groupMap.getDeviceId()))
                .queryList();
        for (int i = 0; i < groupMaps.size(); i++) {

            if (groupMaps.get(i).getGroupName() != null && groupMaps.get(i).getGroupName().equals(groupMap.getGroupName())) {
                deviceIndex = i;
            }
        }
        if (deviceIndex != -1) {
            GroupModelApi.setModelGroupId(groupMap.getDeviceId(), 0xFF, deviceIndex, 0, 0);
            groupMap.delete();
        }
    }

    private void addDeviceToGroup(Context context, ScanDevice scanDevice, int position) {
        if (list.get(position).getGroupId() == 1) {
            Toast.makeText(context, "Cannot add device to this group", Toast.LENGTH_SHORT).show();
            return;
        }
        int deviceId = scanDevice.getDeviceID();

        if (SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES)
                .from(GroupMap.class).where(GroupMap_Table.deviceId.eq(deviceId))
                .and(GroupMap_Table.groupName.eq(list.get(position).getGroupName()))
                .queryList().size() > 0) {
            Toast.makeText(context, "Device is already associated with this group", Toast.LENGTH_SHORT).show();
            return;
        }
        int groupIndex = SQLite.select(GroupMap_Table.ALL_COLUMN_PROPERTIES).from(GroupMap.class).where(GroupMap_Table.deviceId.eq(deviceId)).queryList().size();
        if (groupIndex > 5)
            Toast.makeText(context, "Cannot add device to this group", Toast.LENGTH_SHORT).show();
        else
            GroupModelApi.setModelGroupId(scanDevice.getDeviceID(), 0xFF, groupIndex, 0, list.get(position).getGroupId());
        callback.onGroupAdded(groupIndex > 5);
    }

    private void identifyGroup(final int groupId) {
        Runnable identifyRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    DataSender.sendData(groupId, Data.POWER_OFF.getDataValue());
                    Thread.sleep(2000);
                    DataSender.sendData(groupId, Data.POWER_ON.getDataValue() + 255);
                    Thread.sleep(1000);
                    DataSender.sendData(groupId, Data.POWER_OFF.getDataValue());
                    Thread.sleep(1000);
                    DataSender.sendData(groupId, Data.POWER_ON.getDataValue() + 255);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Handler().post(identifyRunnable);
    }

    private void deleteGroup(int groupId) {
        String groupName = SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).where(Group_Table.groupId.eq(groupId)).querySingle().getGroupName();
        SQLite.delete().from(Group.class).where(Group_Table.groupId.eq(groupId)).execute();
        SQLite.delete().from(GroupMap.class).where(GroupMap_Table.groupName.eq(groupName)).execute();
        Group temp = new Group();
        temp.setGroupId(groupId);
        notifyItemRemoved(list.indexOf(temp));
        list.remove(temp);

    }

    private void renameGroup(int groupId, String newName) {
        Group group = null;
        String oldGroupName = null;
        for (Group tempGroup : list) {
            if (tempGroup.getGroupId() == groupId)
                group = tempGroup;
        }
        if (group != null) {
            SQLite.update(GroupMap.class)
                    .set(GroupMap_Table.groupName.eq(newName))
                    .where(GroupMap_Table.groupName.eq(group.getGroupName()))
                    .execute();
            group.setGroupName(newName);
            group.save();
            notifyDataSetChanged();
        }
    }

    public interface Callback {
        void onGroupAdded(boolean success);
    }
}