package org.oo.onchart.ui;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.oo.onchart.R;
import org.oo.onchart.parser.StudentInfoParser;
import org.oo.onchart.parser.Utils;
import org.oo.onchart.session.BitJwcSession;
import org.oo.onchart.session.Session;
import org.oo.onchart.student.Lesson;
import org.oo.onchart.ui.adapter.LessonListAdapter;
import org.oo.onchart.ui.adapter.LessonPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements Session.SessionListener, LoginTestFragment.LoginListener {

    private String TAG = "MainActivity";
    //private TextView contentText;
    private Toolbar mainToolbar;
    private ViewPager mainListPager;
    private LessonPagerAdapter mainListAdapter;
    private List<LessonListFragment> fragments;

    private BitJwcSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new BitJwcSession(this);
        //contentText = (TextView) findViewById(R.id.tv_content);
        mainToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mainToolbar);
        mainListPager = (ViewPager) findViewById(R.id.vp_lessons);

//        ViewGroup.LayoutParams layoutParams = mainListPager.getLayoutParams();
//        TypedValue value = new TypedValue();
//        getTheme().resolveAttribute(R.attr.actionBarSize, value, true);
//        layoutParams.height -= value.data;
//        mainListPager.setLayoutParams(layoutParams);

        fragments = new ArrayList<>();
        for(int i = 0;i < 5;i ++) {
            fragments.add(new LessonListFragment());
        }
        mainListAdapter = new LessonPagerAdapter(getSupportFragmentManager(), fragments);
        mainListPager.setAdapter(mainListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().  inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_fetch) {
            LoginTestFragment dialog = new LoginTestFragment();
            dialog.setListener(this);
            dialog.show(getSupportFragmentManager(), TAG);
        }

        return super.onOptionsItemSelected(item);
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
}
