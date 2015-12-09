package me.zchang.onchart.ui;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;

/*
 *    Copyright 2015 Zhehua Chang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

public class SettingsActivity extends PreferenceActivity {

    private final static String TAG = "SettingsActivity";
    //public final static String KEY_

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(me.zchang.onchart.R.style.SettingTheme);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();

    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesName(getResources().getString(me.zchang.onchart.R.string.pref_file_name));
            //View view = getView();
            //Log.i(TAG, view.getClass());
            addPreferencesFromResource(me.zchang.onchart.R.xml.preferences);

            Preference preference = findPreference(getString(me.zchang.onchart.R.string.key_num_of_weekday));
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    getActivity().setResult(RESULT_OK, new Intent().putExtra(getString(me.zchang.onchart.R.string.key_num_of_weekday), Integer.parseInt((String) newValue)));
                    return true;
                }
            });
        }


    }

}
