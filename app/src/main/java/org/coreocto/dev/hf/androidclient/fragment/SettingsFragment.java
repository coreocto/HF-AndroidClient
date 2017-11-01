package org.coreocto.dev.hf.androidclient.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.clientlib.suise.SuiseClient;

/**
 * Created by John on 9/9/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    private EditTextPreference prefClientKey1 = null;
    private EditTextPreference prefClientKey2 = null;
    private EditTextPreference prefServerHostname = null;

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
            pref.setSummary("<empty>");
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        prefClientKey1 = (EditTextPreference) findPreference(AppSettings.CLIENT_KEY1);
        prefClientKey1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });
        prefClientKey2 = (EditTextPreference) findPreference(AppSettings.CLIENT_KEY2);
        prefClientKey2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });
        prefServerHostname = (EditTextPreference) findPreference(AppSettings.SERVER_HOSTNAME);
        prefServerHostname.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                updateSummary((EditTextPreference) preference, (String) newValue);
                return true;
            }
        });

        updateSummary(prefClientKey1, "<empty>");
        updateSummary(prefClientKey2, "<empty>");
        updateSummary(prefServerHostname, "<empty>");

        Preference prefClientGenKeysBtn = findPreference("prefClientGenKeysBtn");
        prefClientGenKeysBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SuiseClient suiseClient = AppSettings.getInstance().getSuiseClient();
                suiseClient.Gen(16);
                prefClientKey1.setText(suiseClient.getKey1());
                prefClientKey2.setText(suiseClient.getKey2());

                updateSummary(prefClientKey1, prefClientKey1.getText());
                updateSummary(prefClientKey2, prefClientKey2.getText());

                //code for what you want it to do
                return true;
            }
        });

    }
}

