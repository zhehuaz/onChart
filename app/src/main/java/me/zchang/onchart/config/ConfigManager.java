package me.zchang.onchart.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.student.Course;


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

public class ConfigManager {
    private static String SETTING_FILE;

    final static String CHART_FILE_NAME = "chart.js";

    Context context;
    SharedPreferences sp;
    CourseSQLiteHelper courseSQLiteHelper;

	OnConfigChangeListner configChangeListner;

    private boolean firstLaunch = false;

    public final static int labelImgIndices[] = {
            R.mipmap.little_label1,
            R.mipmap.autumn,
            R.mipmap.winter,
            R.mipmap.spring,
            R.mipmap.night
    };

	public void registerListener(OnConfigChangeListner listener) {
		configChangeListner = listener;
		sp.registerOnSharedPreferenceChangeListener(listener);
    }

	public void unRegisterListener(OnConfigChangeListner listener) {
		configChangeListner = null;
		sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

	public ConfigManager(Context context) {
		this.context = context;
        SETTING_FILE = context.getString(R.string.pref_file_name);

        sp = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        courseSQLiteHelper = new CourseSQLiteHelper(context, context.getString(R.string.course_database_name), null, 1);

        if (getLastVersionCode() != BuildConfig.VERSION_CODE) {
            firstLaunch = true;
            saveLastVersionCode(BuildConfig.VERSION_CODE);
            // create the database at the first launch.
            SQLiteDatabase courseDatabase = courseSQLiteHelper.getWritableDatabase();
            courseDatabase.close();
        }
    }

	public ConfigManager saveSchedule(List<Course> courses) {
		courseSQLiteHelper.addCourses(courses);
        return this;
    }

    public List<Course> getSchedule() {
        return courseSQLiteHelper.getCourses();
    }

	public ConfigManager deleteSchedule() {
		courseSQLiteHelper.clearCourses();
        return this;
    }

    public boolean insertCourse(Course course) {
	    boolean result = courseSQLiteHelper.insertCourse(course);
        if (configChangeListner != null && result)
            configChangeListner.onInsertCourse(course);
	    return result;
    }

	public ConfigManager saveImgPathIndex(long key, int resIndex) {
		courseSQLiteHelper.setImgPathIndex(key, resIndex);
        return this;
    }

    public int getImgPathIndex(int key) {
        return courseSQLiteHelper.getImgPathIndex(key, 0);
    }

	public ConfigManager saveName(String name) {
		if(name != null) {
            sp.edit().putString(context.getString(R.string.key_name), name).apply();
        }
        return this;
    }

    public String getName() {
        return sp.getString(context.getString(R.string.key_name), null);
    }

	public ConfigManager saveWeek(int week) {
		sp.edit().putInt(context.getString(R.string.key_week), week).apply();
        return this;
    }

    public int getWeek() {
        return sp.getInt(context.getString(R.string.key_week), 1);
    }

    public int getNumOfWeekdays() {
        return Integer.parseInt(sp.getString(context.getResources().getString(R.string.key_num_of_weekday), "5"));
    }

    public long getLastFetchWeekTime() {
        return sp.getLong(context.getString(R.string.key_last_fetch_week_time), 0);
    }

	public ConfigManager saveLastFetchWeekTime(long value) {
		sp.edit().putLong(context.getString(R.string.key_last_fetch_week_time), value).apply();
        return this;
    }

    public String getStuNo() {
        return sp.getString(context.getString(R.string.key_stu_no), "");
    }

	public ConfigManager saveStuNo(String stuNo) {
		sp.edit().putString(context.getString(R.string.key_stu_no), stuNo).apply();
        return this;
    }

    public String getPassword() {
        return sp.getString(context.getString(R.string.key_psw), "");
    }

	public ConfigManager savePassword(String psw) {
		sp.edit().putString(context.getString(R.string.key_psw), psw).apply();
        return this;
    }

    public boolean isFirstLaunch() {
        return firstLaunch;
    }

    public int getLastVersionCode() {
        return sp.getInt(context.getString(R.string.key_last_version_code), 0);
    }

	public ConfigManager saveLastVersionCode(int code) {
		sp.edit().putInt(context.getString(R.string.key_last_version_code), code).apply();
        return this;
    }

	public interface OnConfigChangeListner extends SharedPreferences.OnSharedPreferenceChangeListener {
		void onInsertCourse(Course course);
	}
}
