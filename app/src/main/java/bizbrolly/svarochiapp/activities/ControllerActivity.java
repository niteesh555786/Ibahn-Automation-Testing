package bizbrolly.svarochiapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akkipedia.skeleton.utils.DraggableSwitch;
import com.aviadmini.quickimagepick.PickCallback;
import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.PickTriggerResult;
import com.aviadmini.quickimagepick.QiPick;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import bizbrolly.svarochiapp.ColorPickerDialog;
//import bizbrolly.svarochiapp.IdlingResource.SimpleIdlingResource;
import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice;
import bizbrolly.svarochiapp.database.enitities.AssociatedDevice_Table;
import bizbrolly.svarochiapp.database.enitities.Group;
import bizbrolly.svarochiapp.database.enitities.Group_Table;
import bizbrolly.svarochiapp.ibahn_logic.Data;
import bizbrolly.svarochiapp.ibahn_logic.DataSender;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;
import bizbrolly.svarochiapp.ibahn_logic.RecoveryModule;
import bizbrolly.svarochiapp.image_color_picker.ImageColorPickerDialog;
import bizbrolly.svarochiapp.model.devices.AppearanceDevice;

public class ControllerActivity extends BaseCsrActivity{

    private Toolbar toolbar;
    private DraggableSwitch powerSwitch;
    private ImageView lightIntensityLow;
    private ImageView lightIntensityHigh;
    private ImageView pickedImageView;
    private SeekBar lightIntensitySlider;
    private SeekBar tunnableSlider;
    private TextView textView, chooseImageButton;
    private TextView sceneButton;
    private ImageView colorPickerButton;
    RelativeLayout tunnableLayout;
    LinearLayout colorLayout;
    ColorPickerDialog colorPickerDialog;
    private int deviceId;
    long previousTimeStamp = -1;
    int type;
    ImageColorPickerDialog imageColorPickerDialog;
    private PickCallback imagePickerCallback;
    private boolean isGroup = true;
    private TextView musicButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            finish();
        deviceId = bundle.getInt("DeviceId");
        type = bundle.getInt("Type");
        findViews();
        setActions();
        setUpUi();
        if (Preferences.getInstance(this).doesFetchPreviousState() && !isGroup)
            fetchState();
    }

    private List<ProgressDialog> dialogs = new ArrayList<>();


    private static RecoveryModule.Callback callback;

    private void fetchState() {
        final ProgressDialog progressDialog = new ProgressDialog(ControllerActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Fetching state");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        dialogs.add(progressDialog);
        Log.e("Controller", "Fetch Started");
        Log.e("Controller", progressDialog.hashCode() + "");
        callback = new RecoveryModule.Callback() {
            @Override
            public void onResponse(RecoveryModule.State deviceState) {
                Log.e("Controller", "Fetch Ended");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                progressDialog.cancel();
                        for (ProgressDialog dialog : dialogs) {
                            dialog.cancel();
                            Log.e("Controller", dialog.hashCode() + "");
                        }
                    }
                });
                if (deviceState != null)
                    setState(deviceState);
            }
        };
        new RecoveryModule().fetchState(
                type,
                deviceId,
                callback
        );
    }


    private void setState(RecoveryModule.State state) {
        powerSwitch.setChecked(state.isPower());
        lightIntensitySlider.setProgress(state.getIntensityLevel() & 0xFF);
        tunnableSlider.setProgress(state.getTunnableLevel() & 0xFF);
    }

    private void setUpUi() {
        String deviceName = "All LED Lamps";
        if (deviceId >= 0x8001) {
            isGroup = false;
            deviceName = AssociatedDevice.getScanDevice(SQLite.select(AssociatedDevice_Table.ALL_COLUMN_PROPERTIES).from(AssociatedDevice.class).where(AssociatedDevice_Table.deviceId.eq(deviceId)).querySingle()).getName();
        } else if (deviceId != 0) {
            deviceName = SQLite.select(Group_Table.ALL_COLUMN_PROPERTIES).from(Group.class).where(Group_Table.groupId.eq(deviceId)).querySingle().getGroupName();
        }
        toolbar.setTitle(deviceName);
        switch (type) {
            case AppearanceDevice.TYPE_OOD:
                tunnableLayout.setVisibility(View.GONE);
                colorLayout.setVisibility(View.GONE);
                break;
            case AppearanceDevice.TYPE_CCT:
                colorLayout.setVisibility(View.GONE);
                break;
            case AppearanceDevice.TYPE_RGB:
                break;
        }
    }


    private boolean intensityUserSlider = false;

    private void setActions() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        powerSwitch.setOnCheckedChangeListener(new DraggableSwitch.OnCheckChangeListnener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, boolean isFromUser) {
                /*if (!isFromUser)
                    return;*/
                if (powerSwitch.isChecked())
                    DataSender.sendData(deviceId, Data.POWER_ON.getDataValue() + lightIntensitySlider.getProgress());
                else
                    DataSender.sendData(deviceId, Data.POWER_OFF.getDataValue());
            }
        });


//        lightIntensitySlider.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN)
//                    intensityUserSlider = true;
//                else if (event.getAction() == MotionEvent.ACTION_UP)
//                    intensityUserSlider = false;
//                return false;
//            }
//        });
        lightIntensitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*if (!fromUser)
                    return;*/
//                progress -= 20;
//                progress = (int) ((progress / 80f) * 100);
                if (!powerSwitch.isChecked())
                    powerSwitch.setChecked(true);
//                if (previousTimeStamp == -1 || (System.currentTimeMillis() - previousTimeStamp) > 100) {
//                    previousTimeStamp = System.currentTimeMillis();
                DataSender.sendData(deviceId, Data.INTENSITY.getDataValue() + progress);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        lightIntensitySlider.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
//            @Override
//            public void onProgressChanged(int progress, float progressFloat) {
//                if (!intensityUserSlider)
//                    return;
//                progress -= 20;
//                progress = (int) ((progress / 80f) * 100);
//                if (!powerSwitch.isChecked())
//                    powerSwitch.setChecked(true);
//                if (previousTimeStamp == -1 || (System.currentTimeMillis() - previousTimeStamp) > 100) {
//                    previousTimeStamp = System.currentTimeMillis();
//                    DataSender.sendData(deviceId, Data.INTENSITY.getDataValue() + progress);
//                }
//            }
//
//            @Override
//            public void getProgressOnActionUp(int progress, float progressFloat) {
//
//            }
//
//            @Override
//            public void getProgressOnFinally(int progress, float progressFloat) {
//
//            }
//        });

        tunnableSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*if (!fromUser)
                    return;*/
                if (previousTimeStamp == -1 || (System.currentTimeMillis() - previousTimeStamp) > 100) {
                    previousTimeStamp = System.currentTimeMillis();
                    DataSender.sendData(deviceId, Data.TUNING.getDataValue() + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(ControllerActivity.this);
        colorPickerDialog.hideColorComponentsInfo();
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.hideHexaDecimalValue();
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                Log.e("Color", "r : " + r + " g : " + g + " b : " + b);
                Log.e("Color", hexVal);
                DataSender.sendData(deviceId, Data.COLOR.getDataValue(lightIntensitySlider.getProgress(), r, g, b));
            }
        });

        imageColorPickerDialog = new ImageColorPickerDialog(this, 0);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerDialog.show();
            }
        });
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @PickTriggerResult final int triggerResult;
                triggerResult = QiPick.in(ControllerActivity.this)
                        .fromMultipleSources("All sources", PickSource.CAMERA, PickSource.GALLERY);
            }
        });
        imagePickerCallback = new PickCallback() {
            @Override
            public void onImagePicked(@NonNull PickSource pickSource, int i, @NonNull Uri uri) {
                imageColorPickerDialog.setColorPickerImage(uri.toString());
                imageColorPickerDialog.show();
            }

            @Override
            public void onMultipleImagesPicked(int i, @NonNull List<Uri> list) {

            }

            @Override
            public void onError(@NonNull PickSource pickSource, int i, @NonNull String s) {

            }

            @Override
            public void onCancel(@NonNull PickSource pickSource, int i) {

            }
        };
        sceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSceneActivity();
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMusicActivity();
            }
        });
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        powerSwitch = (DraggableSwitch) findViewById(R.id.power_switch);
        powerSwitch.setChecked(false);
        lightIntensityLow = (ImageView) findViewById(R.id.light_intensity_low);
        lightIntensityHigh = (ImageView) findViewById(R.id.light_intensity_high);
        pickedImageView = (ImageView) findViewById(R.id.picked_image_view);
        lightIntensitySlider = (SeekBar) findViewById(R.id.light_intensity_slider);
        tunnableSlider = (SeekBar) findViewById(R.id.tunnable_slider);
        textView = (TextView) findViewById(R.id.textView);
        chooseImageButton = (TextView) findViewById(R.id.choose_image_button);
        colorPickerButton = (ImageView) findViewById(R.id.color_picker_button);
        tunnableLayout = (RelativeLayout) findViewById(R.id.tunnable_layout);
        colorLayout = (LinearLayout) findViewById(R.id.color_layout);
        sceneButton = (TextView) findViewById(R.id.scenes_button);
        musicButton = (TextView) findViewById(R.id.music_button);

    }

    @Override
    public void onConnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QiPick.handleActivityResult(getApplicationContext(), requestCode, resultCode, data, imagePickerCallback);
    }

    private void startSceneActivity() {
        Intent intent = new Intent(this, ScenesActivity.class);
        intent.putExtra("DeviceId", deviceId);
        startActivity(intent);
    }

    private void startMusicActivity() {
        Intent intent = new Intent(this, MusicSyncActivity.class);
        intent.putExtra("DeviceId", deviceId);
        startActivity(intent);
    }

    /*@VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }*/

    private ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        finish();
    }

}
