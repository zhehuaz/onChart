package me.zchang.onchart.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import me.zchang.onchart.BuildConfig;
import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.parser.Utils;
import me.zchang.onchart.session.BitJwcSession;
import me.zchang.onchart.session.Session;
import me.zchang.onchart.student.Lesson;
import me.zchang.onchart.ui.adapter.LessonPagerAdapter;

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
        implements Session.SessionListener, LoginTestFragment.LoginListener
        , SharedPreferences.OnSharedPreferenceChangeListener {

    public final static int REQ_POSITION = 0;
    public final static int REQ_SETTING_WEEKNUM = 1;

    public final static String TAG = "MainActivity";
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
    private int curImgIndex;

    IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String APP_ID = MainApp.APP_ID;
        api = WXAPIFactory.createWXAPI(MainActivity.this, APP_ID, true);
        api.registerApp(APP_ID);

        preferenceManager = ((MainApp) getApplication()).getPreferenceManager();

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
        setupFragments();
        setupList();// ATTENTION, order of refresh and setup
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupFragments() {
        fragments = new ArrayList<>();
        for (int i = 0;i < Math.abs(numOfWeekdays);i ++) {
            fragments.add(new LessonListFragment());
        }
        mainListAdapter = new LessonPagerAdapter(this, getSupportFragmentManager(), fragments, numOfWeekdays);
        mainListPager.setAdapter(mainListAdapter);

        weekdayTabs.setupWithViewPager(mainListPager);
        weekdayTabs.setTabsFromPagerAdapter(mainListAdapter);
    }

    private void setupList() {

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

                    startActivityForResult(intent, REQ_SETTING_WEEKNUM);
                } else if (id == R.id.item_share) {
                    WXWebpageObject webpageObject = new WXWebpageObject();
                    webpageObject.webpageUrl = "https://github.com/LangleyChang/onChart";

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = webpageObject;
                    msg.description = "求star～";
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;
                    //req.scene = SendMessageToWX.Req.WXSceneTimeline;

                    //Log.d(TAG, api.sendReq(req) + "");
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
        if(refreshProgress != null)
            refreshProgress.setVisibility(View.VISIBLE);
        new AsyncTask<String, String, Integer>() {

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer == 0) {
                    Toast.makeText(MainActivity.this, "Unable to fetch week", Toast.LENGTH_SHORT).show();
                    return ;
                }
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
    protected void onDestroy() {
        super.onDestroy();
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
                if (lessons != null) {
                    try {
                        if(lessons != null) {
                            preferenceManager.saveChart(lessons);
                            setupList();
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid account", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Save chart error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                String stuName = session.fetchName();
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

        switch (requestCode) {
            case REQ_SETTING_WEEKNUM:
                if (resultCode == RESULT_OK) {
                    //Log.d(TAG, "Result ok");
                    numOfWeekdays = data.getIntExtra(getString(R.string.key_num_of_weekday), 5);
                    setupFragments();
                    setupList();
                }
                break;
            case REQ_POSITION:
                if (resultCode == RESULT_OK) {

                    LessonListFragment curFragment = fragments.get(mainListPager.getCurrentItem());

                    Lesson lesson = curFragment.findLessonById(data.getIntExtra(getString(R.string.intent_lesson_id), -1));
                    if (lesson != null) {
                        lesson.setLabelImgIndex(curImgIndex);
                    }

                    curFragment.adapter
                        .notifyItemChanged(data.getIntExtra(getString(R.string.intent_position), 0));
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
        Log.d(TAG, "Get shared preference change message :" + key);
        if (key.matches("^[0-9]+$")) {
            //LessonListFragment fragment = fragments.get(mainListPager.getCurrentItem());
            //int id = Integer.parseInt(key);
            //fragment.updateLessonImg(id);
            //fragment.adapter.notifyItemChanged(fragment.adapter.findLessonById(id));
            curImgIndex = sharedPreferences.getInt(key, 0);
        } else if (key.equals(getString(R.string.pref_week_num))) {
            setupList();
            weekdayText.setText(curWeek + "");// TODO update changed items
        }
    }
}
