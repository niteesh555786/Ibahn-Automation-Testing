package bizbrolly.svarochiapp.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.Data;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;
import bizbrolly.svarochiapp.ibahn_logic.MusicLevelMessageHandler;
import bizbrolly.svarochiapp.ibahn_logic.AudioProcessor;

public class MusicSyncActivity extends BaseCsrActivity {

    private TextView dbLevel;
    private AudioProcessor engine;
    private MusicLevelMessageHandler handler;
    private int deviceId;

    public AudioProcessor getEngine() {
        return engine;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getExtras().getInt("DeviceId");
        setContentView(R.layout.activity_music_sync);
        initView();
        initAudioMeter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMeter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMeter();
    }

    private void initAudioMeter() {
        handler = new MusicLevelMessageHandler(this);
        engine = new AudioProcessor(handler, this);
        startMeter();
    }

    private void startMeter() {
        if (!engine.mIsRunning)
            engine.start_engine();
    }

    private void stopMeter() {
        if (engine.mIsRunning)
            engine.stop_engine();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    private void initView() {
        dbLevel = (TextView) findViewById(R.id.db_level);
    }

    private double minValue = 95;
    private double maxValue = 110;

    public void onAudio(double splValue) {
//        if(splValue<minValue)
        dbLevel.setText(""+splValue);
        DataSender.sendData(deviceId, Data.INTENSITY.getDataValue() + normalizeSplValue(splValue));
    }

    private int normalizeSplValue(double splValue) {
        if (splValue >= maxValue)
            return 255;
        splValue = splValue - minValue;
        return (int) ((splValue / (maxValue - minValue)) * 255);
    }

    public void onMaxOverMsg() {
        DataSender.sendData(deviceId, Data.INTENSITY.getDataValue() + 255);
    }

    public void onErrorMsg(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        finish();
    }
}
