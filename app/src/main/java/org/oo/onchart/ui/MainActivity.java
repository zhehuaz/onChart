package org.oo.onchart.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.oo.onchart.BuildConfig;
import org.oo.onchart.R;
import org.oo.onchart.config.PreferenceManager;
import org.oo.onchart.parser.Utils;
import org.oo.onchart.session.BitJwcSession;
import org.oo.onchart.session.Session;
import org.oo.onchart.student.Lesson;
import org.oo.onchart.ui.adapter.LessonPagerAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

public class MainActivity extends AppCompatActivity
        implements Session.SessionListener, LoginTestFragment.LoginListener {

    private final static String TAG = "MainActivity";
    private Toolbar mainToolbar;
    private ViewPager mainListPager;
    private TabLayout weekdayTabs;
    private LessonPagerAdapter mainListAdapter;
    private List<LessonListFragment> fragments;
    private ImageView stuffImage;
    private BitJwcSession session;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private RelativeLayout loginArea;
    private TextView nameText;
    private ProgressBar refreshProgress;
    private TextView weekdayText;
    private TextView versionText;
    private NavigationView drawerView;

    private PreferenceManager preferenceManager;

    private int curWeek;
    private int numOfWeekdays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);

        session = new BitJwcSession(this);
        mainToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mainToolbar);

        curWeek = preferenceManager.getWeek();

        mainListPager = (ViewPager) findViewById(R.id.vp_lessons);
        weekdayTabs = (TabLayout) findViewById(R.id.tl_weekday);
        stuffImage = (ImageView) findViewById(R.id.iv_stuff);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_drawer);
        loginArea = (RelativeLayout) findViewById(R.id.rl_login_click);
        nameText = (TextView) findViewById(R.id.tv_stu_name);
        weekdayText = (TextView) findViewById(R.id.tv_weekday);
        versionText = (TextView) findViewById(R.id.tv_version);
        drawerView = (NavigationView) findViewById(R.id.nv_drawer);

        if (versionText != null)
            versionText.setText(BuildConfig.VERSION_NAME);
        if (weekdayText != null)
            weekdayText.setText("" + curWeek);// an int would be considered as a resource id

        loginArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTestFragment dialog = new LoginTestFragment();
                dialog.setListener(MainActivity.this);
                dialog.show(getSupportFragmentManager(), TAG);
            }
        });

        ViewGroup.LayoutParams params = stuffImage.getLayoutParams();
        params.height = getStatusBarHeight();
        stuffImage.setLayoutParams(params);

        numOfWeekdays = preferenceManager.getNumOfWeekdays();

        setupDrawer();
        refreshWeek();
        setupList();// ATTENTION, order of refresh and setup
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupList() {
        fragments = new ArrayList<>();
        for (int i = 0;i < Math.abs(numOfWeekdays);i ++) {
            fragments.add(new LessonListFragment());
        }
        mainListAdapter = new LessonPagerAdapter(this, getSupportFragmentManager(), fragments, numOfWeekdays);
        mainListPager.setAdapter(mainListAdapter);

        weekdayTabs.setupWithViewPager(mainListPager);
        weekdayTabs.setTabsFromPagerAdapter(mainListAdapter);

        List<Lesson> lessons;
        try {
            lessons = preferenceManager.getChart();
            for (LessonListFragment f : fragments)
                f.clearLesson();
            for (Lesson l : lessons) {
                int index = Utils.parseIndexFromWeekday(l.getWeekDay());
                if (index >= 0 && curWeek >= l.getStartWeek() && curWeek <= l.getEndWeek()) {
                    if (l.getWeekParity() < 0)
                        fragments.get(index).addLesson(l);
                    else if (curWeek % 2 == l.getWeekParity()) // odd or even week num
                        fragments.get(index).addLesson(l);
                }
            }
            for (LessonListFragment f : fragments) {
                f.updateList();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        int curWeekDay = cal.get(Calendar.DAY_OF_WEEK);
        curWeekDay = (curWeekDay - 2) % 7;
        if (curWeekDay < mainListAdapter.getCount()) {
            mainListPager.setCurrentItem(curWeekDay);
        }
    }

    private void setupDrawer() {

        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                if(id == R.id.item_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, 0);
                }
                return false;
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        updateDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //numOfWeekdays = preferenceManager.getNumOfWeekdays();
        //setupList();// slow..
        //preferenceManager.registerOnPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //preferenceManager.unregisterOnPreferenceChangeListener(this);
    }

    public void refreshWeek() {
        if(refreshProgress != null)
            refreshProgress.setVisibility(View.VISIBLE);
        new AsyncTask<String, String, Integer>() {

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer == 0) {
                    Toast.makeText(MainActivity.this, "Unable to fetch week", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(curWeek != integer) {
                    curWeek = integer;
                    preferenceManager.saveWeek(curWeek);
                    setupList();
                    weekdayText.setText(curWeek + "");
                }
                if(refreshProgress != null)
                    refreshProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    return session.fetchWeek();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        refreshProgress = (ProgressBar) menu.findItem(R.id.item_refresh).getActionView().findViewById(R.id.pb_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStartOver() {
        new AsyncTask<String, String, List<Lesson>>() {
            @Override
            protected List<Lesson> doInBackground(String... strings) {
                return session.fetchLessonChart();
            }

            @Override
            protected void onPostExecute(List<Lesson> lessons) {
              //  LessonListAdapter adapter = new LessonListAdapter(MainActivity.this, lessons);
                //int curWeek = preferenceManager.getWeek();
                if (lessons != null)
                    try {
                        preferenceManager.saveChart(lessons);
                        setupList();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Save chart error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                String stuName = session.fetchName();
                if (stuName != null) {
                    preferenceManager.saveName(stuName);
                    updateDrawer();
                }
                if(refreshProgress != null)
                    refreshProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();

    }

    @Override
    public void onError(Session.ErrorCode ec) {
        Toast.makeText(this, "Fail to connect JWC", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinish(String usrNum, String psw) {
        if (refreshProgress != null)
            refreshProgress.setVisibility(View.VISIBLE);
        session.setStuNum(usrNum);
        session.setPsw(psw);
        session.start();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        //fragments.get(mainListPager.getCurrentItem()).
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void updateDrawer() {
        String stuName = preferenceManager.getName();
        if (stuName != null) {
            nameText.setText(stuName);
            loginArea.setClickable(false);
            drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Log.d(TAG, "Result ok");
            numOfWeekdays = data.getIntExtra(getString(R.string.key_num_of_weekday), 5);
            setupList();
        }
    }
}
