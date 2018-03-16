package org.coreocto.dev.hf.androidclient.bean;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.wrapper.Chlh2ClientW;
import org.coreocto.dev.hf.androidclient.wrapper.SuiseClientW;
import org.coreocto.dev.hf.androidclient.wrapper.VasstClientW;

public class AppSettings {

    private static AppSettings instance;
    private SuiseClientW suiseClient;
    private SharedPreferences appPref;
    private Gson gson;
    private VasstClientW vasstClient;
    private DatabaseHelper databaseHelper;
    private Chlh2ClientW chlh2Client;

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public Chlh2ClientW getChlh2Client() {
        return chlh2Client;
    }

    public void setChlh2Client(Chlh2ClientW chlh2Client) {
        this.chlh2Client = chlh2Client;
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
