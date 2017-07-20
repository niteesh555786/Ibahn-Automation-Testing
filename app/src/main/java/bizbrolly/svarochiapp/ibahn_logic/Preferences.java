package bizbrolly.svarochiapp.ibahn_logic;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bizbrolly on 9/9/16.
 */
public class Preferences {
    private static Preferences preferences;
    private SharedPreferences sharedPreferences;

    private enum KEYS {
        Password,
        NetworkPassword,
        FetchPreviousState,
        Email,
    }

    public static Preferences getInstance(Context context) {
        if (preferences == null) {
            preferences = new Preferences(context);
        }
        return preferences;
    }

    private Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
    }

    public void setPasswordSaved(boolean loggedIn) {
        sharedPreferences.edit().putBoolean(KEYS.Password.name(), loggedIn).apply();
    }

    public void setNetworkPassword(String password) {
        sharedPreferences.edit().putString(KEYS.NetworkPassword.name(), password).apply();
    }

    public String getNetworkPassword() {
        return sharedPreferences.getString(KEYS.NetworkPassword.name(), "");
    }

    public void setUserMail(String email){
        sharedPreferences.edit().putString(KEYS.Email.name(), email).apply();
    }

    public String getEmail(){
        return sharedPreferences.getString(KEYS.Email.name(), "");
    }

    public boolean isPasswordSaved() {
        return sharedPreferences.getBoolean(KEYS.Password.name(), false);
    }

    public void fetchPreviousState(boolean isFetch) {
        sharedPreferences.edit().putBoolean(KEYS.FetchPreviousState.name(), isFetch).apply();
    }

    public boolean doesFetchPreviousState() {
        return sharedPreferences.getBoolean(KEYS.FetchPreviousState.name(), true);
    }
}
