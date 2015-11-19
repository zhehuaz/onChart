package org.oo.onchart.ui;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.PersistableBundle;
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
import android.widget.Toast;

import com.google.gson.Gson;

import org.oo.onchart.R;
import org.oo.onchart.parser.StudentInfoParser;
import org.oo.onchart.parser.Utils;
import org.oo.onchart.session.BitJwcSession;
import org.oo.onchart.session.Session;
import org.oo.onchart.student.Lesson;
import org.oo.onchart.ui.adapter.LessonPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements Session.SessionListener, LoginTestFragment.LoginListener {

    private String TAG = "MainActivity";
    //private TextView contentText;
    private Toolbar mainToolbar;
    private ViewPager mainListPager;
    private TabLayout weekdayTabs;
    private LessonPagerAdapter mainListAdapter;
    private List<LessonListFragment> fragments;
    private ImageView stuffImage;
    private BitJwcSession session;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new BitJwcSession(this);
        //contentText = (TextView) findViewById(R.id.tv_content);
        mainToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mainToolbar);

        mainListPager = (ViewPager) findViewById(R.id.vp_lessons);
        weekdayTabs = (TabLayout) findViewById(R.id.tl_weekday);
        stuffImage = (ImageView) findViewById(R.id.iv_stuff);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_drawer);

        ViewGroup.LayoutParams params = stuffImage.getLayoutParams();
        params.height = getStatusBarHeight();
        stuffImage.setLayoutParams(params);
//        ViewGroup.LayoutParams layoutParams = mainListPager.getLayoutParams();
//        TypedValue value = new TypedValue();
//        getTheme().resolveAttribute(R.attr.actionBarSize, value, true);
//        layoutParams.height -= value.data;
//        mainListPager.setLayoutParams(layoutParams);

        fragments = new ArrayList<>();
        for(int i = 0;i < 5;i ++) {
            fragments.add(new LessonListFragment());
        }
        mainListAdapter = new LessonPagerAdapter(this, getSupportFragmentManager(), fragments);
        mainListPager.setAdapter(mainListAdapter);

        weekdayTabs.setupWithViewPager(mainListPager);
        weekdayTabs.setTabsFromPagerAdapter(mainListAdapter);

        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupDrawer() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_fetch) {
            LoginTestFragment dialog = new LoginTestFragment();
            dialog.setListener(this);
            dialog.show(getSupportFragmentManager(), TAG);
        }

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
                return StudentInfoParser.parseChart(session.fetchLessionChart());
            }

            @Override
            protected void onPostExecute(List<Lesson> lessons) {
              //  LessonListAdapter adapter = new LessonListAdapter(MainActivity.this, lessons);
                String storage = new Gson().toJson(lessons);
                Log.d(TAG, storage);

                for(Lesson l : lessons) {
                   int index = Utils.parseIndexFromWeekday(l.getWeekDay());
                   if(index >= 0) {
                       fragments.get(index).addLesson(l);
                   }
                   for(LessonListFragment f : fragments) {
                       f.updateList();
                   }
                }
            }
        }.execute();

    }

    @Override
    public void onError(Session.ErrorCode ec) {
        Toast.makeText(this, "Fail to connect JWC", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFinish(String usrNum, String psw) {
        session.setUsrNum(usrNum);
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
}
