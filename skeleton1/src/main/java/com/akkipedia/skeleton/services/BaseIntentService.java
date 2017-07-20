package com.akkipedia.skeleton.services;

import android.app.IntentService;
import android.os.Process;

/**
 * Created by Ayush on 28/03/17.
 */

public abstract class BaseIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
    }

    public static final void kill() {
        Process.killProcess(Process.myPid());
    }
}
