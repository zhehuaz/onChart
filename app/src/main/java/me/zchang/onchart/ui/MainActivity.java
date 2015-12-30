package me.zchang.onchart.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.parser.Utils;
import me.zchang.onchart.session.BitJwcSession;
import me.zchang.onchart.session.Session;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.ui.adapter.CoursePagerAdapter;
import me.zchang.onchart.ui.adapter.DiffTransformer;

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
        implements Session.SessionStartListener, LoginFragment.LoginListener
        , SharedPreferences.OnSharedPreferenceChangeListener {

    public final static int REQ_POSITION = 0;
    public final static int REQ_SETTING = 1;

    public final static long MILLISECONDS_IN_A_DAY = 24 * 3600 * 1000;

    public final static String TAG = "MainActivity";
    private Toolbar mainToolbar;
    private ViewPager mainListPager;
    private TabLayout weekdayTabs;
    private CoursePagerAdapter mainListAdapter;
    private List<LessonListFragment> fragments;
    private ImageView stuffImage;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private RelativeLayout loginArea;
    private TextView nameText;
    private ProgressBar refreshProgress;
    private TextView weekdayText;
    private TextView versionText;
    private NavigationView drawerView;
    private AppBarLayout toolbarContainer;

    private Session session;
    private PreferenceManager preferenceManager;

    private int curWeek;
    private int numOfWeekdays;
    private Calendar today;
    private boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            firstLaunch = false;
        setContentView(R.layout.activity_main);

        preferenceManager = ((MainApp) getApplication()).getPreferenceManager();
        today = Calendar.getInstance();
        today.setTimeZone(TimeZone.getDefault());

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
        toolbarContainer = (AppBarLayout) findViewById(R.id.appb_container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && firstLaunch)
            toolbarContainer.setTranslationY(- toolbarContainer.getLayoutParams().height);

        if (versionText != null)
            versionText.setText(BuildConfig.VERSION_NAME);
        if (weekdayText != null)
            weekdayText.setText("" + curWeek);// an int would be considered as a resource id

        loginArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment dialog = new LoginFragment();
                dialog.setListener(MainActivity.this);
                dialog.show(getSupportFragmentManager(), TAG);
            }
        });

        ViewGroup.LayoutParams params = stuffImage.getLayoutParams();
        params.height = getStatusBarHeight(this);
        stuffImage.setLayoutParams(params);

        numOfWeekdays = preferenceManager.getNumOfWeekdays();

        setupDrawer();

        // if haven't refreshed week for a week.
        if (Math.abs(preferenceManager.getLastFetchWeekTime() - today.getTimeInMillis()) >  7 * MILLISECONDS_IN_A_DAY) {
            refreshWeek();
        }

        setupFragments();
        setupList();// ATTENTION, order of refresh and setup
        fragments.get(mainListPager.getCurrentItem()).setSlideAnimFlag(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFragments() {
        fragments = new ArrayList<>();
        for (int i = 0;i < Math.abs(numOfWeekdays);i ++) {
            fragments.add(new LessonListFragment());
        }
        mainListAdapter = new CoursePagerAdapter(
                this, getSupportFragmentManager(), fragments, numOfWeekdays);
        mainListPager.setPageTransformer(false, new DiffTransformer());
        mainListPager.setAdapter(mainListAdapter);
        mainListPager.setClipChildren(false);
        mainListPager.setClipToPadding(false);
        mainListPager.setOffscreenPageLimit(2);

        weekdayTabs.setupWithViewPager(mainListPager);
        weekdayTabs.setTabsFromPagerAdapter(mainListAdapter);
    }

    private void setupList() {

        List<Course> courses = null;
        try {
            courses = preferenceManager.getSchedule();
        } catch (FileNotFoundException e) {
            courses = null;
        } finally {
            for (LessonListFragment f : fragments)
                f.clearLesson();
            if (courses != null) {
                for (Course course : courses) {
                    int index = Utils.parseIndexFromWeekday(course.getWeekDay());
                    if (index >= 0 && curWeek >= course.getStartWeek() && curWeek <= course.getEndWeek()) {
                        if (course.getWeekParity() < 0)
                            fragments.get(index).addLesson(course);
                        else if (curWeek % 2 == course.getWeekParity()) // odd or even week num
                            fragments.get(index).addLesson(course);
                    }
                }
            }
            for (LessonListFragment f : fragments) {
                f.updateList();
            }
        }

        int curWeekDay = today.get(Calendar.DAY_OF_WEEK);
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

                    startActivityForResult(intent, REQ_SETTING);
                } else if (id == R.id.item_exams) {
                    Intent intent = new Intent(MainActivity.this, ExamsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.item_share) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_share)
                            + "\n\r"
                            + getString(R.string.url_download));
                    startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
                } else if (id == R.id.item_donate) {
                    DonateFragment donateFragment = new DonateFragment();
                    donateFragment.show(getSupportFragmentManager(), TAG);
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

    public void refreshWeek() {
        preferenceManager.saveLastFetchWeekTime(today.get(Calendar.MILLISECOND));
        if(refreshProgress != null)
            refreshProgress.setVisibility(View.VISIBLE);
        new AsyncTask<String, String, Integer>() {

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer == 0) {
                    Toast.makeText(MainActivity.this, "Unable to fetch week", Toast.LENGTH_SHORT).show();
                    return ;
                }
                // save the nearest past Monday
                preferenceManager.saveLastFetchWeekTime(
                        today.getTimeInMillis()
                                - ((today.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) % 7) * MILLISECONDS_IN_A_DAY);


                if(curWeek != integer && integer > 0) {
                    curWeek = integer;
                    preferenceManager.saveWeek(curWeek);
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
    protected void onResume() {
        super.onResume();
        preferenceManager.registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferenceManager.unRegisterListener(this);
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
    public void onSessionStartOver() {
        new AsyncTask<String, String, List<Course>>() {
            @Override
            protected List<Course> doInBackground(String... strings) {
                try {
                    return session.fetchSchedule();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Course> courses) {
                try {
                    if(courses != null) {
                        preferenceManager.saveSchedule(courses);
                        preferenceManager.saveStuNo(session.getStuNum());
                        preferenceManager.savePassword(session.getPsw());
                        setupList();
                    } else {
                        // TODO bad logic.Account validation should be in Session.start()
                        Toast.makeText(MainActivity.this, getString(R.string.alert_invalid_account), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Save chart error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


                String stuName = null;
                try {
                    stuName = session.fetchName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (stuName != null) {
                    preferenceManager.saveName(stuName);
                    updateDrawer();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid account", Toast.LENGTH_SHORT).show();
                }
                if(refreshProgress != null)
                    refreshProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();

    }

    @Override
    public void onSessionStartError(Session.ErrorCode ec) {
        //Looper.prepare();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.alert_network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoginInputFinish(String usrNum, String psw) {
        if (refreshProgress != null)
            refreshProgress.setVisibility(View.VISIBLE);

        if (session.isStarted())
            session = new BitJwcSession(this);

        session.setStuNum(usrNum);
        session.setPsw(psw);
        session.start();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void updateDrawer() {
        String stuName = preferenceManager.getName();
        if (stuName != null && !stuName.equals(getString(R.string.null_stu_name))) {
            nameText.setText(stuName);
            loginArea.setClickable(false);
            drawerLayout.closeDrawers();
        } else {
            nameText.setText(getString(R.string.title_login));
            loginArea.setClickable(true);
        }
    }

    @TargetApi(21)
    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        toolbarContainer.animate()
                .translationY(0)
                .setStartDelay(50)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        RecyclerView recyclerView = fragments.get(mainListPager.getCurrentItem()).getCourseRecyclerView();
        if (recyclerView != null && firstLaunch) {
           recyclerView.scheduleLayoutAnimation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_SETTING:
                if (resultCode == RESULT_OK) {
                    int returnWeekday = data.getIntExtra(getString(R.string.key_num_of_weekday), -1);
                    int isLogout = data.getIntExtra(getString(R.string.key_logout), SettingsActivity.FLAG_NO_LOGOUT);
                    /**
                     * 0 assign num of weekdays and setupFragments
                     * 1 updateDrawer
                     * 2 setupList
                     */
                    boolean flags[] = new boolean[4];
                    if(returnWeekday != -1) {
                        flags[0] = true;
                        flags[2] = true;
                    }
                    if (isLogout != SettingsActivity.FLAG_NO_LOGOUT) {
                        flags[1] = true;
                        flags[2] = true;
                    }
                    for (int i = 0;i < flags.length; i ++) {
                        if (flags[i]) {
                            switch (i) {
                                case 0:
                                    numOfWeekdays = returnWeekday;
                                    setupFragments();
                                    break;
                                case 1:
                                    updateDrawer();
                                    break;
                                case 2:
                                    setupList();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                break;
            case REQ_POSITION:
                LessonListFragment curFragment = fragments.get(mainListPager.getCurrentItem());
                int pos = data.getIntExtra(getString(R.string.intent_position), 0);

                if (resultCode == RESULT_OK) {
                    Course course = curFragment.findCourseById(data.getIntExtra(getString(R.string.intent_course_id), -1));
                    if (course != null) {
                        course.setLabelImgIndex(data.getIntExtra(getString(R.string.intent_label_image_index), 0));
                    }

                    if (curFragment == null)
                        Log.e(TAG, "curFragment is null");
                    if (curFragment.adapter == null)
                        Log.e(TAG, "adapater is null");
                    curFragment.adapter
                        .notifyItemChanged(pos);
                }
                break;
        }
    }

    PreferenceManager getPreferenceManager()
    {
        return preferenceManager;
    }

    LessonListFragment getListFragment() {
        return fragments.get(mainListPager.getCurrentItem());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_week_num))) {
            setupList();
            weekdayText.setText(curWeek + "");// TODO update changed items
        }
    }

}
