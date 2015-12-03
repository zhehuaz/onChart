package org.oo.onchart.ui;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.oo.onchart.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private final static String TAG = "SettingsActivity";
    //public final static String KEY_

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();

    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
            getPreferenceManager().setSharedPreferencesName(getResources().getString(R.string.pref_file_name));
            //View view = getView();
            //Log.i(TAG, view.getClass());
            Preference preference = findPreference(getString(R.string.key_num_of_weekday));
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    getActivity().setResult(RESULT_OK, new Intent().putExtra(getString(R.string.key_num_of_weekday), Integer.parseInt((String)newValue)));
                    return true;
                }
            });
        }


    }

}
