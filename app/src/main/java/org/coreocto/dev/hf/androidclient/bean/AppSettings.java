package org.coreocto.dev.hf.androidclient.bean;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.commonlib.util.Registry;

public class AppSettings {

    private static AppSettings instance;
    private SuiseClient suiseClient;
    private SharedPreferences appPref;
    private Gson gson;
    private String idToken;
    private Registry registry;

    public static AppSettings getInstance() {
        if (instance == null) {
            instance = new AppSettings();
        }
        return instance;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Registry getRegistry() {
        return registry;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

//    public String getKey1() {
//        return appPref.getString("client.key1", null);
//    }
//
//    public String getKey2() {
//        return appPref.getString("client.key2", null);
//    }
//
//    public String getHostname() {
//        return appPref.getString("server.hostname", null);
//    }

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
