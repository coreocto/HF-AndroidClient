package org.coreocto.dev.hf.androidclient.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.util.AndroidBase64Impl;
import org.coreocto.dev.hf.clientlib.sse.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.sse.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.perfmon.aspect.TraceAspect;

import java.io.File;

/**
 * Created by John on 9/9/2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private EditTextPreference prefClientKey1 = null;
    private EditTextPreference prefClientKey2 = null;
    private EditTextPreference prefServerHostname = null;
    private EditTextPreference prefClientDatadir = null;
    private ListPreference prefClientSsetype = null;
    private CheckBoxPreference perfServerReportStat = null;
    private static final String TAG = "SettingsFragment";

    private SharedPreferences sharedPreferences;

    public SettingsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UploadFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    private static void updateSummary(EditTextPreference pref, String newValue) {
        if (newValue == null || newValue.toString().isEmpty()) {
            pref.setSummary(AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);
        } else {
            pref.setSummary(newValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        updateSummary(prefClientKey1, prefClientKey1.getText());
        updateSummary(prefClientKey2, prefClientKey2.getText());
        updateSummary(prefServerHostname, prefServerHostname.getText());
        updateSummary(prefClientDatadir, prefClientDatadir.getText());
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final Context ctx = getActivity();

        final AppSettings appSettings = AppSettings.getInstance();

        final SuiseClient suiseClient = appSettings.getSuiseClient();
        final VasstClient vasstClient = appSettings.getVasstClient();

        final IBase64 base64 = new AndroidBase64Impl();

        sharedPreferences = getPreferenceManager().getSharedPreferences();

        prefClientSsetype = (ListPreference) findPreference(AppConstants.PREF_CLIENT_SSE_TYPE);

        prefClientKey1 = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEY1);
        prefClientKey1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean validBase64 = true;

                String newValS = (String) newValue;

                byte[] newKey1 = null;
                try {
                    newKey1 = base64.decodeToByteArray(newValS);
                } catch (Exception e) {
                    Log.e(TAG, "invalid base64 sequence", e);
                    validBase64 = false;
                }

                if (!validBase64) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Error")
                            .setMessage("Invalid base64 sequence!")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    //update the key of sse clients
                    updateSummary((EditTextPreference) preference, newValS);
                    suiseClient.setKey1(newKey1);
                    vasstClient.setSecretKey(newKey1);
                    //end
                }

                return validBase64;
            }
        });

        prefClientKey2 = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEY2);
        prefClientKey2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean validBase64 = true;

                String newValS = (String) newValue;

                byte[] newKey2 = null;
                try {
                    newKey2 = base64.decodeToByteArray(newValS);
                } catch (Exception e) {
                    Log.e(TAG, "invalid base64 sequence", e);
                    validBase64 = false;
                }

                if (!validBase64) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Error")
                            .setMessage("Invalid base64 sequence!")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    updateSummary((EditTextPreference) preference, newValS);

                    suiseClient.setKey2(newKey2);   //update the key of sse clients
                }

                return validBase64;
            }
        });

        prefServerHostname = (EditTextPreference) findPreference(AppConstants.PREF_SERVER_HOSTNAME);
        prefServerHostname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });

        prefClientDatadir = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_DATA_DIR);
        prefClientDatadir.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (newValue instanceof String && !((String) newValue).isEmpty()) {
                    File dir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + newValue);
                    if (!dir.exists()) { //check directory exists
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Error")
                                .setMessage(newValue + " does not exists!")
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();
                        return false;
                    }
                }

                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });

        updateSummary(prefClientKey1, AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);
        updateSummary(prefClientKey2, AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);
        updateSummary(prefServerHostname, AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);

        //added on 15/12/2017 for removing all records from app db
        Preference prefClientClrAppDbBtn = findPreference("prefClientClrAppDbBtn");
        prefClientClrAppDbBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                DatabaseHelper databaseHelper = appSettings.getDatabaseHelper();
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                long affectedRows = database.delete(AppConstants.TABLE_REMOTE_DOCS, null, null);

                Log.d(TAG, "deletedRows: " + affectedRows);

                database.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Info")
                        .setMessage("All document records have been deleted.")
                        .setCancelable(false)
                        .setPositiveButton("OK", null)
                        .show();

                //code for what you want it to do
                return true;
            }
        });
        //end

        Preference prefClientGenKeysBtn = findPreference("prefClientGenKeysBtn");
        prefClientGenKeysBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                suiseClient.Gen(16);

                vasstClient.setSecretKey(suiseClient.getKey1());    //VasstClient & SuiseClient shared the same secret key (key1)

                String key1Str = base64.encodeToString(suiseClient.getKey1());
                String key2Str = base64.encodeToString(suiseClient.getKey2());

                prefClientKey1.setText(key1Str);
                prefClientKey2.setText(key2Str);

                updateSummary(prefClientKey1, prefClientKey1.getText());
                updateSummary(prefClientKey2, prefClientKey2.getText());

                //code for what you want it to do
                return true;
            }
        });

        perfServerReportStat = (CheckBoxPreference) findPreference(AppConstants.PREF_SERVER_RPT_STAT);
        perfServerReportStat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newValB = (Boolean) newValue;
                TraceAspect.setEnabled(newValB.booleanValue());
                return true;
            }
        });

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, key + " changed");
    }
}

