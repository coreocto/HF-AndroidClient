package org.coreocto.dev.hf.androidclient.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;
import org.coreocto.dev.hf.commonlib.util.Registry;

import java.io.File;

/**
 * Created by John on 9/9/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private EditTextPreference prefClientKey1 = null;
    private EditTextPreference prefClientKey2 = null;
    private EditTextPreference prefServerHostname = null;
    private EditTextPreference prefClientDatadir = null;

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
            pref.setSummary(Constants.PREF_EMPTY_VAL_PLACEHOLDER);
        } else {
            pref.setSummary(newValue);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateSummary(prefClientKey1, prefClientKey1.getText());
        updateSummary(prefClientKey2, prefClientKey2.getText());
        updateSummary(prefServerHostname, prefServerHostname.getText());
        updateSummary(prefClientDatadir, prefClientDatadir.getText());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final Context ctx = getActivity();

        prefClientKey1 = (EditTextPreference) findPreference(Constants.PREF_CLIENT_KEY1);
        prefClientKey1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });
        prefClientKey2 = (EditTextPreference) findPreference(Constants.PREF_CLIENT_KEY2);
        prefClientKey2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });
        prefServerHostname = (EditTextPreference) findPreference(Constants.PREF_SERVER_HOSTNAME);
        prefServerHostname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });
        prefClientDatadir = (EditTextPreference) findPreference(Constants.PREF_CLIENT_DATA_DIR);
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

        updateSummary(prefClientKey1, Constants.PREF_EMPTY_VAL_PLACEHOLDER);
        updateSummary(prefClientKey2, Constants.PREF_EMPTY_VAL_PLACEHOLDER);
        updateSummary(prefServerHostname, Constants.PREF_EMPTY_VAL_PLACEHOLDER);

        Preference prefClientGenKeysBtn = findPreference("prefClientGenKeysBtn");
        prefClientGenKeysBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SuiseClient suiseClient = AppSettings.getInstance().getSuiseClient();
                Registry registry = AppSettings.getInstance().getRegistry();
                suiseClient.Gen(16);

                String key1Str = registry.getBase64().encodeToString(suiseClient.getKey1());
                String key2Str = registry.getBase64().encodeToString(suiseClient.getKey2());

                prefClientKey1.setText(key1Str);
                prefClientKey2.setText(key2Str);

                updateSummary(prefClientKey1, prefClientKey1.getText());
                updateSummary(prefClientKey2, prefClientKey2.getText());

                //code for what you want it to do
                return true;
            }
        });

    }
}

