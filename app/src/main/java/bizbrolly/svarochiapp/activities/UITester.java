package bizbrolly.svarochiapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.akkipedia.skeleton.utils.DraggableSwitch;

import bizbrolly.svarochiapp.R;

public class UITester extends AppCompatActivity {

    private DraggableSwitch draggable;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitester);
        initView();
        setActions();
    }

    private void setActions() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draggable.setChecked(!draggable.isChecked());
            }
        });
        draggable.setOnCheckedChangeListener(new DraggableSwitch.OnCheckChangeListnener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, boolean isFromUser) {

            }
        });
    }

    private void initView() {
        draggable = (DraggableSwitch) findViewById(R.id.draggable);
        button = (Button) findViewById(R.id.button);
    }
}
