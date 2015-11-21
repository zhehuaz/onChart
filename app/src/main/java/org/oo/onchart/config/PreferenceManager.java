package org.oo.onchart.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.oo.onchart.student.Lesson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Created by Administrator on 2015/11/21.
 */
public class PreferenceManager {
    private static final String SETTING_FILE = "onchar_setting";
    private static final String PREF_KEY_NAME = "name";
    private static final String PREF_KEY_WEEK = "week";

    final static String CHART_FILE_NAME = "chart.js";

    Context context;
    Gson gson;

    public PreferenceManager(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public void saveChart(List<Lesson> lessons) throws IOException {
        String json = gson.toJson(lessons);
        FileOutputStream fos = context.openFileOutput(CHART_FILE_NAME, Context.MODE_PRIVATE);
        fos.write(json.getBytes());
        fos.close();
    }

    public List<Lesson> getChart() throws FileNotFoundException {
        Reader reader = new InputStreamReader(context.openFileInput(CHART_FILE_NAME));
        return gson.fromJson(reader, new TypeToken<List<Lesson>>(){ }.getType());
    }

    public void saveName(String name) {
        if(name != null) {
            SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
            sp.edit().putString(PREF_KEY_NAME, name).apply();
        }
    }

    public String getName() {
        SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return sp.getString(PREF_KEY_NAME, null);
    }

    public void saveWeek(int week) {
        SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        sp.edit().putInt(PREF_KEY_WEEK, week).apply();
    }

    public int getWeek() {
        SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return sp.getInt(PREF_KEY_WEEK, 1);
    }

}
