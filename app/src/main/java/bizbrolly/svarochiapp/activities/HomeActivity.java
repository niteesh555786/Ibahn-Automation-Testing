package bizbrolly.svarochiapp.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.akkipedia.skeleton.utils.Logger;
import com.bizbrolly.WebServiceRequests;
import com.bizbrolly.entities.AddDbDetailsResponse;
import com.csr.csrmesh2.MeshConstants;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.adapters.GroupsAdapter;
import bizbrolly.svarochiapp.adapters.HomeDevicesAdapter;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.GroupMap;
import bizbrolly.svarochiapp.database.enitities.GroupMap_Table;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.ibahn_logic.DbScriptHelper;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;
import bizbrolly.svarochiapp.model.devices.ScanDevice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseCsrActivity implements GroupsAdapter.Callback, HomeDevicesAdapter.Callback {
    public HomeDevicesAdapter homeDevicesAdapter;
    private RecyclerView deviceRecyclerView;
    Toolbar toolbar;
    private RecyclerView groupListRecyclerView;
    private ImageView expandGroupSheetButton;
    private ImageView addGroupButton;
    private boolean isExpanded = false;
    private RecyclerView.LayoutManager gridLayoutManager;
    private RecyclerView.LayoutManager horizontalLayoutManager;
    private GroupsAdapter groupAdapter;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setAction();
    }

    private void setAction() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        deviceRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        gridLayoutManager = new GridLayoutManager(this, 3);
        horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupListRecyclerView.setLayoutManager(horizontalLayoutManager);
        List<ScanDevice> scanDeviceList = new ArrayList<>();
        for (AssociatedDevice associatedDevice : SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList()) {
            scanDeviceList.add(AssociatedDevice.getScanDevice(associatedDevice));
        }
        homeDevicesAdapter = new HomeDevicesAdapter(this, scanDeviceList);
        deviceRecyclerView.setAdapter(homeDevicesAdapter);
        groupAdapter = new GroupsAdapter(SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).queryList(), this);
        groupListRecyclerView.setAdapter(groupAdapter);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGroupDialog();
            }
        });
        expandGroupSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    groupListRecyclerView.setLayoutManager(horizontalLayoutManager);
                    groupListRecyclerView.setAdapter(groupAdapter);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    groupListRecyclerView.setLayoutManager(gridLayoutManager);
                    groupListRecyclerView.setAdapter(groupAdapter);
                }
                isExpanded = !isExpanded;
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    groupListRecyclerView.setLayoutManager(gridLayoutManager);
                    groupListRecyclerView.setAdapter(groupAdapter);
                    isExpanded = true;
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    groupListRecyclerView.setLayoutManager(horizontalLayoutManager);
                    groupListRecyclerView.setAdapter(groupAdapter);
                    isExpanded = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    public void updateDevice(ScanDevice device) {
        if (device.getUuidHash() == homeDevicesAdapter.dessociateUUID) {
            SQLite.delete().from(GroupMap.class).where(GroupMap_Table.deviceId.eq(homeDevicesAdapter.associateDevice.getDeviceID())).execute();
            SQLite.delete().from(AssociatedDevice.class).where(AssociatedDevice_Table.uuidHash.eq(homeDevicesAdapter.dessociateUUID)).execute();
            List<ScanDevice> scanDeviceList = new ArrayList<>();
            for (AssociatedDevice associatedDevice : SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList()) {
                scanDeviceList.add(AssociatedDevice.getScanDevice(associatedDevice));
            }
            homeDevicesAdapter = new HomeDevicesAdapter(this, scanDeviceList);
            deviceRecyclerView.getRecycledViewPool().clear();
            deviceRecyclerView.setAdapter(homeDevicesAdapter);
            hideProgressDialog();
        } else {
            homeDevicesAdapter.updateDevice(device);
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }


    public void testDissociate(View v) {
        AssociatedDevice device = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList().get(0);
//        MainActivity.mService.resetDevice(0x8001, new byte[]{94,-107,-33,-62,22,-94,57,-44,-124,37,-98,-87,-51,-103,52,41,-54,-58,-77,-107,-35,62,41,58});
//        MainActivity.mService.resetDevice(device.getDeviceId(), device.getResetKey().getBlob());
    }

    public void onDeviceAssociated(ScanDevice device) {
        int count = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                .from(AssociatedDevice.class)
//                .where(AssociatedDevice_Table.shortName.like("%" + device.getAppearanceDevice().getShortName().replaceAll("[^a-zA-Z0-9]", "") + "%"))
                .queryList()
                .size();
        AssociatedDevice tempAssociatedDevice = AssociatedDevice.getAssociatedDevice(device);
        tempAssociatedDevice.setShortName(tempAssociatedDevice.getShortName().replaceAll("[0-9]", "") + " " + (count + 1));
        tempAssociatedDevice.setType(tempAssociatedDevice.getType());
        tempAssociatedDevice.save();
        List<ScanDevice> scanDeviceList = new ArrayList<>();
        for (AssociatedDevice associatedDevice : SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).queryList()) {
            scanDeviceList.add(AssociatedDevice.getScanDevice(associatedDevice));
        }
        homeDevicesAdapter = new HomeDevicesAdapter(this, scanDeviceList);
        deviceRecyclerView.setAdapter(homeDevicesAdapter);
        hideProgressDialog();
    }

    private void initView() {
        deviceRecyclerView = (RecyclerView) findViewById(R.id.device_recycler_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupListRecyclerView = (RecyclerView) findViewById(R.id.group_list_recycler_view);
        expandGroupSheetButton = (ImageView) findViewById(R.id.expand_group_sheet_button);
        addGroupButton = (ImageView) findViewById(R.id.add_group_button);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
    }

    AlertDialog addGroupDialog;

    private void showAddGroupDialog() {
        if (SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).queryList().size() <= 6) {
            if (addGroupDialog == null) {
                final EditText groupNameEditText = new EditText(this);
                groupNameEditText.setHint("Enter Group Name");
                addGroupDialog = new AlertDialog.Builder(this)
                        .setView(groupNameEditText).setTitle("Add Group")
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                addGroupDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (groupNameEditText.getText().toString().trim().length() > 0) {
                                    try {
                                        Group newGroup = new Group();
                                        newGroup.setGroupName(groupNameEditText.getText().toString());
                                        newGroup.save();
                                        groupAdapter = new GroupsAdapter(SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).queryList(), HomeActivity.this);
                                        groupListRecyclerView.setAdapter(groupAdapter);
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        Toast.makeText(HomeActivity.this, "Two groups cannot have same name", Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    groupNameEditText.setError("Enter Name");
                            }
                        });
                    }
                });
            }
            addGroupDialog.show();
        } else {
            Toast.makeText(this, "No more groups are allowed", Toast.LENGTH_SHORT).show();
        }
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        this.menu = menu;
        if (Preferences.getInstance(this).doesFetchPreviousState())
            menu.findItem(R.id.toggle_fetch_state).setTitle("Disable Fetch State");
        else
            menu.findItem(R.id.toggle_fetch_state).setTitle("Enable Fetch State");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_wiggle) {
            homeDevicesAdapter.stopWiggling();
            menu.findItem(R.id.cancel_wiggle).setVisible(false);
        }
        if (item.getItemId() == R.id.toggle_fetch_state) {
            Preferences.getInstance(this).fetchPreviousState(!Preferences.getInstance(this).doesFetchPreviousState());
            if (Preferences.getInstance(this).doesFetchPreviousState())
                menu.findItem(R.id.toggle_fetch_state).setTitle("Disable Fetch State");
            else
                menu.findItem(R.id.toggle_fetch_state).setTitle("Enable Fetch State");
        } else if (item.getItemId() == R.id.reset_whole_network) {
            showPasswordDialog();
        } else if (item.getItemId() == R.id.update_db_on_cloud){
            try {
                showProgressDialog("Updating database on cloud", "Please wait...");
                WebServiceRequests.getInstance().addDbDetails(
                        Preferences.getInstance(this).getEmail(),
                        new DbScriptHelper().generateDbScript(this),
                        new Callback<AddDbDetailsResponse>() {
                            @Override
                            public void onResponse(Call<AddDbDetailsResponse> call, Response<AddDbDetailsResponse> response) {
                                hideProgressDialog();
                            }

                            @Override
                            public void onFailure(Call<AddDbDetailsResponse> call, Throwable t) {
                                hideProgressDialog();
                            }
                        }
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onGroupAdded(boolean success) {
        showProgressDialog();
    }

    public void onGroupConfirmed(Bundle data) {
        GroupMap groupMap = new GroupMap();
        groupMap.setDeviceId(data.getInt("DeviceIdSrc"));
        int groupId = data.getInt("GroupID");
        AssociatedDevice associatedDevice = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                .from(AssociatedDevice.class)
                .where(AssociatedDevice_Table.deviceId.eq(data.getInt("DeviceIdSrc")))
                .querySingle();

        if (groupId == 0) {
            Toast.makeText(this, "Device removed successfully!", Toast.LENGTH_SHORT).show();
        } else {

            int deviceType = associatedDevice.getType();
            if (deviceType == -1)
                deviceType = AssociatedDevice.getScanDevice(associatedDevice).getAppearanceDevice().getType();


            List<Group> groupList = SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).where(Group_Table.groupId.eq(groupId)).queryList();
            if (groupList.size() > 0) {
                groupMap.setGroupName(groupList.get(0).getGroupName());
                String groupType = groupList.get(0).getGroupType();
                if (groupType == null
                        || groupType.length() == 0
                        || Integer.parseInt(groupType) >= deviceType) {
                    groupList.get(0).setGroupType("" + deviceType);
                    groupList.get(0).save();
                }
            }
            groupMap.save();
            hideProgressDialog();

            String deviceName = SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).where(AssociatedDevice_Table.deviceId.eq(data.getInt("DeviceIdSrc"))).querySingle().getShortName();
//        groupAdapter = new GroupsAdapter(SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).queryList(), HomeActivity.this);
//        groupListRecyclerView.setAdapter(groupAdapter);
            Toast.makeText(this, deviceName + " has been added to " + groupMap.getGroupName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWigglingStarted() {
        menu.findItem(R.id.cancel_wiggle).setVisible(true);
    }

    private void showPasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText passwordEditText = new EditText(this);
        passwordEditText.setHint("Enter Network Password");
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        layout.addView(passwordEditText);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Reset Whole Network")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (passwordEditText.getText().length() > 0
                                && Preferences.getInstance(HomeActivity.this).getNetworkPassword().equals(passwordEditText.getText().toString())) {
                            resetWholeNetwork();
                        } else {
                            if (passwordEditText.getText().toString().length() > 0)
                                passwordEditText.setError("Password Mismatch");
                            else
                                passwordEditText.setError("Enter Network Password");
                        }
                    }
                });
            }
        });

        alertDialog.show();

    }

    private void resetWholeNetwork() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog("Disassociating whole network", "Please wait...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                for (final AssociatedDevice device : SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                        .from(AssociatedDevice.class)
                        .queryList()) {
                    Logger.log("Disassociating device: " + device.getShortName());
                    MainActivity.mService.resetDevice(device.getDeviceId(), device.getResetKey().getBlob());
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Logger.log("Done");
                    device.delete();
                }
                homeDevicesAdapter.getDevices().clear();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                homeDevicesAdapter.notifyDataSetChanged();
                hideProgressDialog();
                Toast.makeText(HomeActivity.this, "Done", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
