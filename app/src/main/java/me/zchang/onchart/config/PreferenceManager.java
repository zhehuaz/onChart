package me.zchang.onchart.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.LabelCourse;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;


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

public class PreferenceManager {
    private static String SETTING_FILE;
    private static String PREF_KEY_NAME = "name";
    private static String PREF_KEY_WEEK = "week";

    final static String CHART_FILE_NAME = "chart.js";

    Context context;
    Gson gson;
    SharedPreferences sp;
    //SharedPreferences.OnSharedPreferenceChangeListener listener = null;

    public final static int labelImgs[] = {
            R.mipmap.little_label1,
            R.mipmap.autumn,
            R.mipmap.winter,
            R.mipmap.spring,
            R.mipmap.night
    };

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unRegisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }



    public PreferenceManager(Context context) {
        this.context = context;
        gson = new Gson();
        SETTING_FILE = context.getString(R.string.pref_file_name);

        sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        PREF_KEY_WEEK = context.getString(R.string.pref_week_num);
    }

    public void saveChart(List<Course> courses) throws IOException {
        String json = gson.toJson(courses);
        FileOutputStream fos = context.openFileOutput(CHART_FILE_NAME, Context.MODE_PRIVATE);
        fos.write(json.getBytes());
        fos.close();

        SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (Course course : courses) {
            //savePicPath(lesson.getId(), lesson.getLabelImgIndex());
            editor.putInt(course.getId() + "", course.getLabelImgIndex());
        }
        editor.apply();
    }

    public List<Course> getChart() throws FileNotFoundException {
        Reader reader = new InputStreamReader(context.openFileInput(CHART_FILE_NAME));
        List<Course> courses = gson.fromJson(reader, new TypeToken<List<LabelCourse>>(){ }.getType());

        for (Course course : courses) {
            course.setLabelImgIndex(getPicPathIndex(course.getId()));
        }

        return courses;
    }

    public void savePicPathIndex(int key, int resIndex) {
        //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        sp.edit().putInt(key + "", resIndex).commit();
    }

    public int getPicPathIndex(int key) {
        //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return sp.getInt(key + "", 0);
    }

    public void saveName(String name) {
        if(name != null) {
            //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
            sp.edit().putString(PREF_KEY_NAME, name).apply();
        }
    }

    public String getName() {
        return sp.getString(PREF_KEY_NAME, null);
    }

    public void saveWeek(int week) {
        //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        sp.edit().putInt(PREF_KEY_WEEK, week).apply();
    }

    public int getWeek() {
        //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return sp.getInt(PREF_KEY_WEEK, 1);
    }

    public int getNumOfWeekdays() {
        //SharedPreferences sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        return Integer.parseInt(sp.getString(context.getResources().getString(R.string.key_num_of_weekday), "5"));
    }
}
