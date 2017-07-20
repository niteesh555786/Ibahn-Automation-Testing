package bizbrolly.svarochiapp.ibahn_logic;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import bizbrolly.svarochiapp.activities.MusicSyncActivity;

/**
 * Created by Akash on 27/06/17.
 */

public class MusicLevelMessageHandler extends Handler {

    private WeakReference<MusicSyncActivity> parentWeakReference;

    public MusicLevelMessageHandler(MusicSyncActivity parent) {
        this.parentWeakReference = new WeakReference<>(parent);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case AudioProcessor.DATA_MSG :
                parentWeakReference.get().onAudio((Double) msg.obj);
                break;
            case AudioProcessor.MAXOVER_MSG :
                parentWeakReference.get().onMaxOverMsg();
                break;
            case AudioProcessor.ERROR_MSG:
                parentWeakReference.get().onErrorMsg((String) msg.obj);
                parentWeakReference.get().getEngine().stop_engine();
                break;
            default :
                super.handleMessage(msg);
                break;
        }
    }

}
