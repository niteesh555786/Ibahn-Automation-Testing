package bizbrolly.svarochiapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;

public class ScenesActivity extends BaseCsrActivity implements View.OnClickListener {

    private ImageView closeScenesButton;
    private TextView coolDaylight;
    private TextView moonlight;
    private TextView sunrise;
    private TextView warmWhite;
    private TextView pinkTinge;
    private TextView blueEnergise;
    private TextView blueStudy;
    private CheckBox party;
    private int deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenes);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            finish();
        deviceId = bundle.getInt("DeviceId");
        initView();
        initActions();
    }

    private void initView() {
        closeScenesButton = (ImageView) findViewById(R.id.close_scenes_button);
        coolDaylight = (TextView) findViewById(R.id.cool_daylight);
        moonlight = (TextView) findViewById(R.id.moonlight);
        sunrise = (TextView) findViewById(R.id.sunrise);
        warmWhite = (TextView) findViewById(R.id.warm_white);
        pinkTinge = (TextView) findViewById(R.id.pink_tinge);
        blueEnergise = (TextView) findViewById(R.id.blue_energise);
        blueStudy = (TextView) findViewById(R.id.blue_study);
        party = (CheckBox) findViewById(R.id.party);
    }

    private void initActions() {
        closeScenesButton.setOnClickListener(this);
        coolDaylight.setOnClickListener(this);
        moonlight.setOnClickListener(this);
        sunrise.setOnClickListener(this);
        warmWhite.setOnClickListener(this);
        pinkTinge.setOnClickListener(this);
        blueEnergise.setOnClickListener(this);
        blueStudy.setOnClickListener(this);
        party.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        byte[] data = null;
        int r,
                g,
                b,
                w,
                l;
        if (v == closeScenesButton) {
            finish();
        } else if (v == coolDaylight) {
            r = 0;
            g = 0;
            b = 204;
            w = 255;
            l = 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == moonlight) {
            r = 0;
            g = 0;
            b = (byte) 22;
            w = (byte) 255;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == sunrise) {
            r = 38;
            g = 38;
            b = (byte) 0;
            w = (byte) 255;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == warmWhite) {
            r = 76;
            g = 76;
            b = (byte) 0;
            w = (byte) 255;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == pinkTinge) {
            r = 228;
            g = 76;
            b = (byte) 152;
            w = (byte) 255;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == blueEnergise) {
            r = 0;
            g = 0;
            b = (byte) 255;
            w = (byte) 255;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == blueStudy) {
            r = 0;
            g = 0;
            b = (byte) 255;
            w = (byte) 204;
            l = (byte) 255;
            data = new byte[]{
                    (byte) ((r/* * 255f) / (r + g + b + w*/)),
                    (byte) ((g/* * 255f) / (r + g + b + w*/)),
                    (byte) ((b/* * 255f) / (r + g + b + w*/)),
                    (byte) ((w/* * 255f) / (r + g + b + w*/)),
                    (byte) ((l/* * 255f) / (r + g + b + w*/))
            };
        } else if (v == party) {
            data = party.isChecked()
                    ? new byte[]{'I', 'B', 'D', 0x01}
                    : new byte[]{'I', 'B', 'D', 0x00};
        }
        if (data != null)
            sendData(data);
    }

    private void sendData(byte[] data) {
        byte[] dataToSend;
        if (data.length == 5) {
            byte[] ibrByteArray = "IBR".getBytes();
            dataToSend = new byte[ibrByteArray.length + 5];
            System.arraycopy(ibrByteArray, 0, dataToSend, 0, ibrByteArray.length);
            System.arraycopy(data, 0, dataToSend, ibrByteArray.length, data.length);
        } else {
            dataToSend = data;
        }
        DataSender.sendData(deviceId, dataToSend);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
