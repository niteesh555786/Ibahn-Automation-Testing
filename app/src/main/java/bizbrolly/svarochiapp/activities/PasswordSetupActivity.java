package bizbrolly.svarochiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akkipedia.skeleton.activities.BaseSkeletonActivity;
import com.bizbrolly.WebServiceRequests;
import com.bizbrolly.entities.AddDetailsResponse;
import com.bizbrolly.entities.GetDbDetailsResponse;

import org.json.JSONException;

import bizbrolly.svarochiapp.R;
import bizbrolly.svarochiapp.ibahn_logic.DbScriptHelper;
import bizbrolly.svarochiapp.ibahn_logic.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordSetupActivity extends BaseSkeletonActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Switch showPasswordSwitch;
    private Button restoreNetworkButton;
    private Toolbar toolbar;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setup);
        initView();
        initActions();
    }

    private void validateAndSetPassword(){

//        startActivity(new Intent(this,HomeActivity.class));
//        finish();

        if(!isValidEmail(emailEditText.getText())){
            emailEditText.setError("Invalid Email");
            return;
        }

        if (passwordEditText.getText().toString().length() >= 6) {
            if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                if ((passwordEditText.getText().toString().trim().length() >= 6)
                        && (passwordEditText.getText().toString().matches(".*\\d+.*"))) {
                    showProgressDialog();
                    WebServiceRequests.getInstance().createUser(
                            emailEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            new Callback<AddDetailsResponse>() {
                                @Override
                                public void onResponse(Call<AddDetailsResponse> call, Response<AddDetailsResponse> response) {
                                    if(response != null && response.body()!= null) {
                                        if (response.body().getAddDetailsResult().isData()) {
                                            Preferences.getInstance(PasswordSetupActivity.this).setUserMail(emailEditText.getText().toString());
                                            Preferences.getInstance(PasswordSetupActivity.this).setNetworkPassword(passwordEditText.getText().toString());
                                            Preferences.getInstance(PasswordSetupActivity.this).setPasswordSaved(true);
                                            startActivity(new Intent(PasswordSetupActivity.this, HomeActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(PasswordSetupActivity.this, response.body().getAddDetailsResult().getErrorDetail().getErrorMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(PasswordSetupActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<AddDetailsResponse> call, Throwable t) {

                                }
                            }
                    );

                } else {
                    passwordEditText.setError("Invalid Password");
                }
            }else {
                confirmPasswordEditText.setError("Must be same as password");
            }
        } else {
            passwordEditText.setError("Password must contain 6 characters");
        }
    }

    private   boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void initActions() {
        showPasswordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPasswordSwitch.isChecked()){
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSetPassword();
            }
        });
        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 101){
                    validateAndSetPassword();
                }
                return false;
            }
        });
        restoreNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("Restoring database", "Please wait...");
                WebServiceRequests.getInstance().getDb(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        new Callback<GetDbDetailsResponse>() {
                            @Override
                            public void onResponse(Call<GetDbDetailsResponse> call, Response<GetDbDetailsResponse> response) {
                                hideProgressDialog();
                                if(response != null && response.body() != null){
                                    if (response.body().getGetDBDetailsResult().getResult()){
                                        Preferences.getInstance(PasswordSetupActivity.this).setUserMail(emailEditText.getText().toString());
                                        Preferences.getInstance(PasswordSetupActivity.this).setNetworkPassword(passwordEditText.getText().toString());
                                        Preferences.getInstance(PasswordSetupActivity.this).setPasswordSaved(true);
                                        String dbString = response.body().getGetDBDetailsResult().getData().getDBScript();
                                        try {
                                            new DbScriptHelper().parseScript(PasswordSetupActivity.this, dbString);
                                            Toast.makeText(PasswordSetupActivity.this, "Database restored!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PasswordSetupActivity.this, HomeActivity.class));
                                            finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(PasswordSetupActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(PasswordSetupActivity.this, response.body().getGetDBDetailsResult().getErrorDetail().getErrorMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(PasswordSetupActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetDbDetailsResponse> call, Throwable t) {
                                hideProgressDialog();
                                Toast.makeText(PasswordSetupActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

//                try {
//                    new DbScriptHelper().parseScript(PasswordSetupActivity.this, "{\"NetworkKey\":\"\",\"Device\":[{\"appearanceShortname\":\"RGBW 1\",\"appearanceValue\":-1,\"deviceHash\":\"e7W80g==\\n\",\"dhmKey\":\"+cl5Q89TUwrLgXWXG4ZNc\\/Kp3C39qek7\\n\",\"id\":32770,\"name\":\"RGBW 1\",\"type\":\"Other\",\"uuid\":\"\"},{\"appearanceShortname\":\"Warm & Cool 2\",\"appearanceValue\":-1,\"deviceHash\":\"fJTogQ==\\n\",\"dhmKey\":\"8s1t9UeKp\\/1x8Zh9NPyBfpDSdagU96+p\\n\",\"id\":32771,\"name\":\"Warm & Cool 2\",\"type\":\"Other\",\"uuid\":\"\"}],\"Group\":[{\"id\":2,\"name\":\"tem\",\"device\":[32770]},{\"id\":3,\"name\":\"tem1\",\"device\":[32770,32771]}]}");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(PasswordSetupActivity.this, "Work in progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password_edit_text);
        showPasswordSwitch = (Switch) findViewById(R.id.show_password_switch);
        restoreNetworkButton = (Button) findViewById(R.id.restore_network_button);
        toolbar = (Toolbar) findViewById(R.id.setup_password_toolbar);
        doneButton = (Button) findViewById(R.id.setup_password_done_button);
    }
}
