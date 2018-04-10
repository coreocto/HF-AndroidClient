package org.coreocto.dev.hf.androidclient.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import org.coreocto.dev.hf.clientlib.sse.chlh.Chlh2Client;
import org.coreocto.dev.hf.clientlib.sse.suise.SuiseClient;
import org.coreocto.dev.hf.clientlib.sse.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.perfmon.aspect.TraceAspect;

import java.io.File;

/**
 * Created by John on 9/9/2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";
    private EditTextPreference prefClientKey1 = null;
    private EditTextPreference prefClientKey2 = null;
    private EditTextPreference prefServerHostname = null;
    private EditTextPreference prefClientDatadir = null;
    private ListPreference prefClientSsetype = null;
    private CheckBoxPreference prefServerReportStat = null;
    private CheckBoxPreference prefClientDataProtect = null;

//    private EditTextPreference prefClientChlhNumOfHash = null;
//    private EditTextPreference prefClientChlhBitSize = null;

    private EditTextPreference prefClientChlhFPR = null;
    private EditTextPreference prefClientChlhExpectDocCnt = null;

//    private EditTextPreference prefClientKey3 = null;
//    private EditTextPreference prefClientKey4 = null;
//    private EditTextPreference prefClientKeyD = null;
//    private EditTextPreference prefClientKeyC = null;
//    private EditTextPreference prefClientKeyL = null;

//    private EditTextPreference prefClientM = null;
//    private EditTextPreference prefClientK = null;

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

    private static void updateSummary(EditTextPreference pref, Integer newValue) {
        if (newValue == null) {
            pref.setSummary(-1);
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

        updateSummary(prefClientChlhFPR, prefClientChlhFPR.getText());
        updateSummary(prefClientChlhExpectDocCnt, prefClientChlhExpectDocCnt.getText());

//        updateSummary(prefClientKey3, prefClientKey3.getText());
//        updateSummary(prefClientKey4, prefClientKey4.getText());
//        updateSummary(prefClientKeyD, prefClientKeyD.getText());
//        updateSummary(prefClientKeyC, prefClientKeyC.getText());
//        updateSummary(prefClientKeyL, prefClientKeyL.getText());
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

        final Activity ctx = getActivity();

        final AppSettings appSettings = AppSettings.getInstance();

        final SuiseClient suiseClient = appSettings.getSuiseClient();
        final VasstClient vasstClient = appSettings.getVasstClient();
//        final McesClient mcesClient = appSettings.getMcesClient();
        final Chlh2Client chlh2Client = appSettings.getChlh2Client();

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
                    chlh2Client.setSecretKey(newKey1);
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

//        prefClientM = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_M);
//        prefClientM.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue instanceof Integer) {
//                    int newValI = (int) newValue;
//                    updateSummary((EditTextPreference) preference, newValI);
//                    vasstClient.setM(newValI);
//                }
//                return true;
//            }
//        });
//
//        prefClientK = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_K);
//        prefClientK.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue instanceof Integer) {
//                    int newValI = (int) newValue;
//                    updateSummary((EditTextPreference) preference, newValI);
//                    vasstClient.setK(newValI);
//                }
//                return true;
//            }
//        });

//        prefClientChlhNumOfHash = (EditTextPreference) findPreference(getString(R.string.pref_client_chlh_numofhash));
//        prefClientChlhNumOfHash.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                updateSummary((EditTextPreference) preference, (String) newValue);
//                return true;
//            }
//        });
//        prefClientChlhBitSize = (EditTextPreference) findPreference(getString(R.string.pref_client_chlh_bitsize));
//        prefClientChlhBitSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                updateSummary((EditTextPreference) preference, (String) newValue);
//                return true;
//            }
//        });
        prefClientChlhFPR = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_CHLH_FPR);
        prefClientChlhFPR.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);

                double fpr = 0;

                try {
                    fpr = Double.parseDouble((String) newValue);
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "failed to parse fpr", ex);
                }

                int expectDocCnt = 0;

                try {
                    expectDocCnt = Integer.parseInt(prefClientChlhExpectDocCnt.getText());
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "failed to parse expectDocCnt", ex);
                }

                chlh2Client.setMode3(fpr, expectDocCnt);

                return true;
            }
        });

        prefClientChlhExpectDocCnt = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_CHLH_EXPECT_DOC_CNT);
        prefClientChlhExpectDocCnt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);

                double fpr = 0;

                try {
                    fpr = Double.parseDouble(prefClientChlhFPR.getText());
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "failed to parse fpr", ex);
                }

                int expectDocCnt = 0;

                try {
                    expectDocCnt = Integer.parseInt((String) newValue);
                } catch (NumberFormatException ex) {
                    Log.d(TAG, "failed to parse expectDocCnt", ex);
                }

                chlh2Client.setMode3(fpr, expectDocCnt);

                return true;
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
        updateSummary(prefClientChlhFPR, AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);
        updateSummary(prefClientChlhExpectDocCnt, AppConstants.PREF_EMPTY_VAL_PLACEHOLDER);

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

                chlh2Client.setSecretKey(suiseClient.getKey1());

//                mcesClient.setK1(suiseClient.getKey1());
//                mcesClient.setK2(suiseClient.getKey2());
//
//                Random random = new SecureRandom();
//
//                byte[] newKey = new byte[16];
//                random.nextBytes(newKey);
//                mcesClient.setK3(newKey);
//
//                newKey = new byte[16];
//                random.nextBytes(newKey);
//                mcesClient.setK4(newKey);
//
//                newKey = new byte[16];
//                random.nextBytes(newKey);
//                mcesClient.setKd(newKey);
//
//                newKey = new byte[16];
//                random.nextBytes(newKey);
//                mcesClient.setKc(newKey);
//
//                newKey = new byte[16];
//                random.nextBytes(newKey);
//                mcesClient.setKl(newKey);

//                prefClientKey3.setText(base64.encodeToString(mcesClient.getK3()));
//                prefClientKey4.setText(base64.encodeToString(mcesClient.getK4()));
//                prefClientKeyD.setText(base64.encodeToString(mcesClient.getKd()));
//                prefClientKeyC.setText(base64.encodeToString(mcesClient.getKc()));
//                prefClientKeyL.setText(base64.encodeToString(mcesClient.getKl()));
//
//                updateSummary(prefClientKey3, prefClientKey3.getText());
//                updateSummary(prefClientKey4, prefClientKey4.getText());
//                updateSummary(prefClientKeyD, prefClientKeyD.getText());
//                updateSummary(prefClientKeyC, prefClientKeyC.getText());
//                updateSummary(prefClientKeyL, prefClientKeyL.getText());

                //code for what you want it to do
                return true;
            }
        });

        prefServerReportStat = (CheckBoxPreference) findPreference(AppConstants.PREF_SERVER_RPT_STAT);
        prefServerReportStat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newValB = (Boolean) newValue;
                TraceAspect.setEnabled(newValB.booleanValue());
                return true;
            }
        });

        prefClientDataProtect = (CheckBoxPreference) findPreference(AppConstants.PREF_CLIENT_DATA_PROTECT);
        prefClientDataProtect.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newValB = (Boolean) newValue;
                suiseClient.setDataProtected(newValB);
                vasstClient.setDataProtected(newValB);
                return true;
            }
        });

//        prefClientKey3 = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEY3);
//        prefClientKey4 = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEY4);
//        prefClientKeyD = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEYD);
//        prefClientKeyC = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEYC);
//        prefClientKeyL = (EditTextPreference) findPreference(AppConstants.PREF_CLIENT_KEYL);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, key + " changed");
    }
}

