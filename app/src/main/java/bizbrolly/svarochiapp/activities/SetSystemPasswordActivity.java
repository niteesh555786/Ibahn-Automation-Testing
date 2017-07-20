package bizbrolly.svarochiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akkipedia.skeleton.fontViews.FontEditText;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;


public class SetSystemPasswordActivity extends BaseCsrActivity {

    private RelativeLayout activitySetSystemPassword;
    private Toolbar setSystemToolbarId;
    private FontEditText emailId;
    private FontEditText passwordId;
    private FontEditText confirmPasswordId;
    private TextView passwordMustId;
    private TextView atLeastTextId;
    private TextView atLeastOneNumberId;
    private TextView showPasswordTextId;
    private Switch showPasswordSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setSystem_toolbar_id);
        setSupportActionBar(toolbar);
        initView();
        showPasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordId.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordId.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordId.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordId.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.password_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done_text_id) {
            if (passwordId.getText().toString().length() >= 6) {
                if (passwordId.getText().toString().equals(confirmPasswordId.getText().toString())) {
                    if ((passwordId.getText().toString().trim().length() >= 6)
                            && (passwordId.getText().toString().matches(".*\\d+.*"))) {
                        Preferences.getInstance(SetSystemPasswordActivity.this).setPasswordSaved(true);
                        Preferences.getInstance(SetSystemPasswordActivity.this).setNetworkPassword(passwordId.getText().toString());
                        startActivity(new Intent(SetSystemPasswordActivity.this,HomeActivity.class));
                        finish();
                    } else {
                        passwordId.setError("Invalid Password");
                    }
                }else {
                    confirmPasswordId.setError("Must same as password");
                }
            } else {
                passwordId.setError("Password must contains 6 characters");
            }
            // startActivity(new Intent(ToolbarActivity.this, CalenderActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        activitySetSystemPassword = (RelativeLayout) findViewById(R.id.activity_set_system_password);
        setSystemToolbarId = (Toolbar) findViewById(R.id.setSystem_toolbar_id);
        emailId = (FontEditText) findViewById(R.id.email_id);
        passwordId = (FontEditText) findViewById(R.id.password_id);
        confirmPasswordId = (FontEditText) findViewById(R.id.confirm_password_id);
        passwordMustId = (TextView) findViewById(R.id.password_must_id);
        atLeastTextId = (TextView) findViewById(R.id.at_least_text_id);
        atLeastOneNumberId = (TextView) findViewById(R.id.at_least_one_number_id);
        showPasswordTextId = (TextView) findViewById(R.id.show_password_text_id);
        showPasswordSwitch = (Switch) findViewById(R.id.show_password_switch);
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
}
