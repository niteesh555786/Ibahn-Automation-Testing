package bizbrolly.svarochiapp.activities;

import com.akkipedia.skeleton.activities.BaseSkeletonActivity;

/**
 * Created by Akash on 26/04/17.
 */

public abstract class BaseCsrActivity extends BaseSkeletonActivity {

    public abstract void onConnected();

    public abstract void onDisconnected();


    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.meshApiMessageHandler != null)
            MainActivity.meshApiMessageHandler.setParent(this);
    }
}
