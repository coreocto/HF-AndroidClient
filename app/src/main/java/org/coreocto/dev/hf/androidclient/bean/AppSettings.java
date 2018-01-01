package org.coreocto.dev.hf.androidclient.bean;

import android.content.SharedPreferences;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.gson.Gson;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.util.Registry;

public class AppSettings {

    private static AppSettings instance;
    private SuiseClient suiseClient;
    private SharedPreferences appPref;
    private Gson gson;
    private Registry registry;
    private VasstClient vasstClient;
    private DatabaseHelper databaseHelper;

    public DriveResourceClient getDriveResourceClient() {
        return driveResourceClient;
    }

    public void setDriveResourceClient(DriveResourceClient driveResourceClient) {
        this.driveResourceClient = driveResourceClient;
    }

    private DriveResourceClient driveResourceClient;

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public VasstClient getVasstClient() {
        return vasstClient;
    }

    public void setVasstClient(VasstClient vasstClient) {
        this.vasstClient = vasstClient;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public SharedPreferences getAppPref() {
        return appPref;
    }

    public void setAppPref(SharedPreferences appPref) {
        this.appPref = appPref;
    }

    public SuiseClient getSuiseClient() {
        return suiseClient;
    }

    public void setSuiseClient(SuiseClient suiseClient) {
        this.suiseClient = suiseClient;
    }
}
