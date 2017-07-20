package bizbrolly.svarochiapp.adapters;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.akkipedia.skeleton.utils.GeneralUtils;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.activities.ControllerActivity;
import bizbrolly.svarochiapp.activities.HomeActivity;
import bizbrolly.svarochiapp.activities.MainActivity;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.DeviceIdGenerator;
import bizbrolly.svarochiapp.database.enitities.DeviceIdGenerator_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.ibahn_logic.Data;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;
import bizbrolly.svarochiapp.ibahn_logic.RecoveryModule;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;
import bizbrolly.svarochiapp.model.devices.Device;
import bizbrolly.svarochiapp.model.devices.ScanDevice;
import bizbrolly.svarochiapp.ibahn_logic.Association;

/**
 * Created by Ayush on 26/04/17.
 */

public class HomeDevicesAdapter extends RecyclerView.Adapter<HomeDevicesAdapter.HomeDevicesViewHolder> {
    private List<ScanDevice> devices;
    private HomeActivity context;
    public ScanDevice associateDevice;
    public int dessociateUUID;
    private boolean isWiggling;
    private Callback callback;

    public List<ScanDevice> getDevices() {
        return devices;
    }

    public HomeDevicesAdapter(HomeActivity context, List<ScanDevice> devices) {
        this.context = context;
        this.devices = new ArrayList<>();
        if (devices.size() > 0)
            this.devices.addAll(devices);
        this.callback = context;
    }

    public void updateDevice(ScanDevice newDevice) {
        if (!newDevice.isAssociated()) {
            if (!devices.contains(newDevice)) {
                devices.add(newDevice);
                notifyItemInserted(devices.size() - 1);
            } else {
                int index = devices.indexOf(newDevice);
                if (devices.get(index).getName() != null && devices.get(index).getName().length() > 0) {

                } else {
                    devices.set(index, newDevice);
                    notifyItemChanged(index);
                }
            }
        }
    }


    @Override
    public HomeDevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeDevicesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeDevicesViewHolder holder, int position) {
        holder.setData();
        if (isWiggling) {
            holder.startWiggling();
        } else {
            holder.stopWiggling();
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void onDeviceAssociated(ScanDevice device) {
        AssociatedDevice.getAssociatedDevice(device).save();
    }

    public class HomeDevicesViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private View backgroundLayout;
        private View deviceTypeLayout;
        private boolean isHolderWiggling = false;

        private Animation wiggleAnimation;

        private void startWiggling() {
            if (isHolderWiggling || !devices.get(getAdapterPosition()).isAssociated())
                return;
            if (wiggleAnimation == null) {
                wiggleAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
            }
            itemView.startAnimation(wiggleAnimation);
            isHolderWiggling = true;
        }


        private void stopWiggling() {
            if (!isHolderWiggling || !devices.get(getAdapterPosition()).isAssociated() || wiggleAnimation == null)
                return;
            itemView.clearAnimation();
            isHolderWiggling = false;
        }

        private void startDragging(String data) {
            ClipData clipData = ClipData.newPlainText("Device", data);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(itemView);
            itemView.startDrag(clipData, shadowBuilder, itemView, 0);
        }

        private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (devices.get(getAdapterPosition()).isAssociated()) {
                    String data = new Gson().toJson(devices.get(getAdapterPosition()));
                    HomeDevicesAdapter.this.startWiggling();
                    startDragging(data);
                }
            }


            public boolean onDoubleTap(MotionEvent e) {
                associateDevice = devices.get(getAdapterPosition());
                if (associateDevice.isAssociated()) {
                    showDeviceMenu(context, devices.get(getAdapterPosition()));
                }
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                associateDevice = devices.get(getAdapterPosition());
                if (associateDevice.isAssociated()) {
                    gotoControllerActivity(context, associateDevice);
                } else {
                    if (associateDevice.getName() == null || associateDevice.getName().length() < 1) {
                        Toast.makeText(context, "Waiting for device info", Toast.LENGTH_SHORT).show();
                    } else {
                        context.showProgressDialog();
                        int a = Association.associateDevice(associateDevice.getUuidHash(), associateDevice.getAuthCode(), associateDevice.getAuthCode() != -1, generateNewDeviceId(), MainActivity.mService);
                        Log.e("", "");
                    }
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (isWiggling && devices.get(getAdapterPosition()).isAssociated()) {
                    startDragging(new Gson().toJson(devices.get(getAdapterPosition())));
                }
                return true;
            }
        };

        private GestureDetector gestureDetector;

        public HomeDevicesViewHolder(final View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            backgroundLayout = itemView.findViewById(R.id.background_layout);
            deviceTypeLayout = itemView.findViewById(R.id.device_type_background);
            gestureDetector = new GestureDetector(itemView.getContext(), gestureListener);
            gestureDetector.setIsLongpressEnabled(true);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

        public void setData() {
            if (devices.get(getAdapterPosition()).isAssociated()) {
                backgroundLayout.setBackgroundResource(R.drawable.associated_device_background);
            } else
                backgroundLayout.setBackgroundResource(R.drawable.discovered_device_item_background);

            if (devices.get(getAdapterPosition()).getAppearanceDevice() != null) {
                int type = devices.get(getAdapterPosition()).getType();
                if (type == -1)
                    type = devices.get(getAdapterPosition()).getAppearanceDevice().getType();
                switch (type) {
                    case AppearanceDevice.TYPE_CCT:
                        deviceTypeLayout.setBackgroundResource(R.drawable.tunnable_item_background);
                        break;
                    case AppearanceDevice.TYPE_RGB:
                        deviceTypeLayout.setBackgroundResource(R.drawable.rgb_item_background);
                        break;
                    case AppearanceDevice.TYPE_OOD:
                        deviceTypeLayout.setBackgroundColor(Color.WHITE);
                        break;
                }
            }
            deviceName.setText(devices.get(getAdapterPosition()).getName() != null ? devices.get(getAdapterPosition()).getName() : "?");
        }

    }

    private void renameDevice(int deviceId, String newName) {
        SQLite.update(AssociatedDevice.class)
                .set(AssociatedDevice_Table.shortName.eq(newName))
                .where(AssociatedDevice_Table.deviceId.eq(deviceId))
                .execute();
        for (int i = 0; i < devices.size(); i++) {
            if (deviceId == devices.get(i).getDeviceID()) {
                devices.get(i).getAppearanceDevice().setShortName(newName);
                notifyItemChanged(i);
            }
        }
    }

    private void showDeviceMenu(final HomeActivity context, final ScanDevice associateDevice) {
        final String[] options = {
                "Identify",
                "Rename",
                "Disassociate"
        };
        ArrayAdapter<String> menuAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                options
        );

        AlertDialog menuDialog = new AlertDialog.Builder(context)
                .setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (options[which]) {
                            case "Identify":
                                identifyDevice(associateDevice.getDeviceID());
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
                                                renameDevice(associateDevice.getDeviceID(), text);
                                            }

                                            @Override
                                            public void onNegativeClick() {

                                            }
                                        }
                                );
                                break;
                            case "Disassociate":
                                showDisassociationDialog(context, associateDevice);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setTitle("Group Options")
                .create();

        menuDialog.show();
    }

    public void showDisassociationDialog(final HomeActivity context, final ScanDevice associateDevice) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Alert")
                .setMessage("Are you sure you want to dissociate this device")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.showProgressDialog();
                        final AssociatedDevice device = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                                .from(AssociatedDevice.class)
                                .where(AssociatedDevice_Table.deviceId.eq(associateDevice.getDeviceID()))
                                .queryList().get(0);
                        MainActivity.mService.resetDevice(device.getDeviceId(), device.getResetKey().getBlob());
//                        dessociateUUID = associateDevice.getUuidHash();
                        SQLite.delete().from(AssociatedDevice.class).where(AssociatedDevice_Table.uuidHash.eq(associateDevice.getUuidHash())).execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.hideProgressDialog();
                                device.delete();
                                ScanDevice deviceToDelete = null;
                                for (ScanDevice tempDevice : devices) {
                                    if (tempDevice.getDeviceID() == associateDevice.getDeviceID())
                                        deviceToDelete = tempDevice;
                                }
                                devices.remove(deviceToDelete);
                                notifyDataSetChanged();
                            }
                        }, 400);
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void gotoControllerActivity(final HomeActivity context, final ScanDevice associateDevice) {
        Intent intent = new Intent(context, ControllerActivity.class);
        intent.putExtra("DeviceId", associateDevice.getDeviceID());
        int type = associateDevice.getType();
        intent.putExtra("Type", type == -1 ? associateDevice.getAppearanceDevice().getType() : type);
        context.startActivity(intent);
    }

    private void startWiggling() {
        isWiggling = true;
        callback.onWigglingStarted();
        notifyDataSetChanged();
    }

    public void stopWiggling() {
        isWiggling = false;
        notifyDataSetChanged();
    }

    public interface Callback {
        void onWigglingStarted();
    }

    private void identifyDevice(final int groupId) {
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

    private int generateNewDeviceId() {
        int newDeviceId;
        List<DeviceIdGenerator> list = SQLite.select(DeviceIdGenerator_Table.ALL_COLUMN_PROPERTIES).from(DeviceIdGenerator.class).orderBy(DeviceIdGenerator_Table.deviceId, true).queryList();
        newDeviceId = list.get(list.size() - 1).getDeviceId() + 1;
        DeviceIdGenerator deviceIdGenerator = new DeviceIdGenerator();
        deviceIdGenerator.setDeviceId(newDeviceId);
        deviceIdGenerator.save();
        return newDeviceId;
    }

}
