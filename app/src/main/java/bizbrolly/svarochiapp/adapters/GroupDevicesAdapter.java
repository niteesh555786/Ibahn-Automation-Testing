package bizbrolly.svarochiapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.GroupMap;

/**
 * Created by Akash on 25/06/17.
 */

public class GroupDevicesAdapter extends ArrayAdapter<GroupMap> {
    private List<GroupMap> groupDevices;
    private Context context;
    private ClickCallback callback;


    public GroupDevicesAdapter(Context context, List<GroupMap> groupDevices, ClickCallback callback) {
        super(context, R.layout.group_device_row, groupDevices);
        this.groupDevices = groupDevices;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.group_device_row, parent, false);
        TextView groupDeviceName;
        ImageView groupDeviceDeleteButton;
        groupDeviceName = (TextView) rootView.findViewById(R.id.group_device_name);
        groupDeviceDeleteButton = (ImageView) rootView.findViewById(R.id.group_device_delete_button);
        groupDeviceName.setText(getDeviceName(groupDevices.get(position).getDeviceId()));
        groupDeviceDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(groupDevices.get(position), position);
            }
        });
        return rootView;
    }

    private String getDeviceName(int deviceId) {
        return AssociatedDevice.getScanDevice(
                SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES)
                        .from(AssociatedDevice.class)
                        .where(AssociatedDevice_Table.deviceId.eq(deviceId))
                        .querySingle()).getName();
    }


    public interface ClickCallback{
        void onItemClick(GroupMap groupMap, int position);
    }

}
