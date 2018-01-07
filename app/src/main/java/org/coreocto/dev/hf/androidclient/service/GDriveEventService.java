package org.coreocto.dev.hf.androidclient.service;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GDriveEventService extends DriveEventService {
    private static final String TAG = "GDriveEventService";
    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        Log.d(TAG, this.getClass().getName() + ".onCreate()");
        super.onCreate();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
    }

    @Override
    public void onChange(ChangeEvent changeEvent) {
        Log.d(TAG, "Received event: " + changeEvent);

        final ChangeEvent evt = changeEvent;

        final DatabaseHelper databaseHelper = AppSettings.getInstance().getDatabaseHelper();

        DriveResourceClient drvResClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

        // [START retrieve_metadata]
        Task<Metadata> getMetadataTask = drvResClient.getMetadata(changeEvent.getDriveId().asDriveFile());
        getMetadataTask
                .addOnSuccessListener(
                        new OnSuccessListener<Metadata>() {
                            @Override
                            public void onSuccess(Metadata metadata) {
                                Log.d(TAG, metadata.getTitle());
                                String docId = metadata.getTitle();

                                //Log.d(TAG, "docId: " + docId);
                                //Log.d(TAG, "onChange(), " + evt.getDriveId() + ", resourceId: " + evt.getDriveId().getResourceId());

                                {
                                    ContentValues values = new ContentValues();
                                    values.put("cremoteid", evt.getDriveId().encodeToString());
                                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                    long affectedRows = db.update(AppConstants.TABLE_REMOTE_DOCS, values, "cremotename=?", new String[]{docId});
                                    db.close();
                                    Log.d(TAG, "affectedRows(update): " + affectedRows);
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to retrieve metadata", e);
                    }
                });
        // [END retrieve_metadata]
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
                String resourceId = event.getDriveId().getResourceId();
                Log.d(TAG, "Remote resource ID: " + resourceId);
                eventHandled = true;
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
