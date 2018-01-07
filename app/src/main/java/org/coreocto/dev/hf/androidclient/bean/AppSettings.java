package org.coreocto.dev.hf.androidclient.bean;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.wrapper.SuiseClientW;
import org.coreocto.dev.hf.androidclient.wrapper.VasstClientW;
import org.coreocto.dev.hf.commonlib.util.Registry;

public class AppSettings {

    private static AppSettings instance;
    private SuiseClientW suiseClient;
    private SharedPreferences appPref;
    private Gson gson;
    private Registry registry;
    private VasstClientW vasstClient;
    private DatabaseHelper databaseHelper;

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public VasstClientW getVasstClient() {
        return vasstClient;
    }

    public void setVasstClient(VasstClientW vasstClient) {
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

    public SuiseClientW getSuiseClient() {
        return suiseClient;
    }

    public void setSuiseClient(SuiseClientW suiseClient) {
        this.suiseClient = suiseClient;
    }
}
