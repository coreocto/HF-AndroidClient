package org.coreocto.dev.hf.androidclient.service;

import android.util.Log;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

import java.util.concurrent.ExecutorService;

public class GDriveEventService extends DriveEventService {
    private static final String TAG = "GDriveEventService";
    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onChange(ChangeEvent changeEvent) {
        Log.d(TAG, "Received event: " + changeEvent);
    }

    @Override
    public void onCompletion(CompletionEvent event) {

        DriveId driveId = event.getDriveId();

        Log.d(TAG, "onComplete: " + driveId.getResourceId());

        boolean eventHandled = false;
        switch (event.getStatus()) {
            case CompletionEvent.STATUS_SUCCESS:
                // Commit completed successfully.
                // Can now access the remote resource Id
                break;
            case CompletionEvent.STATUS_FAILURE:
                // Handle failure....
                // Modified contents and metadata failed to be applied to the server.
                // They can be retrieved from the CompletionEvent to try to be applied later.
                break;
            case CompletionEvent.STATUS_CONFLICT:
                // Handle completion conflict.
                break;
        }

        if (eventHandled) {
            event.dismiss();
        }
    }
}
