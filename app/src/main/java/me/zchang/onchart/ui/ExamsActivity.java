package me.zchang.onchart.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.List;

import me.zchang.onchart.R;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.config.PreferenceManager;
import me.zchang.onchart.session.BitJwcSession;
import me.zchang.onchart.session.Session;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.ui.adapter.ExamListAdapter;

public class ExamsActivity extends AppCompatActivity implements Session.SessionStartListener{

    public static final String TAG = "ExamsActivity";

    private Toolbar toolbar;
    private ImageView stuffImage;
    private RecyclerView recyclerView;
    private ProgressBar loadingProgress;
    ExamListAdapter adapter;

    private PreferenceManager preferenceManager;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        toolbar = (Toolbar) findViewById(R.id.tb_exams);
        stuffImage = (ImageView) findViewById(R.id.iv_stuff);
        recyclerView = (RecyclerView) findViewById(R.id.rv_exams);
        loadingProgress = (ProgressBar) findViewById(R.id.pb_fetch_exams);

        adapter = new ExamListAdapter(this, null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup.LayoutParams params = stuffImage.getLayoutParams();
        params.height = MainActivity.getStatusBarHeight(this);
        stuffImage.setLayoutParams(params);


        preferenceManager = ((MainApp) getApplication()).getPreferenceManager();
        session = new BitJwcSession(this);

        String stuNo = preferenceManager.getStuNo();
        String psw = preferenceManager.getPassword();
        if (stuNo.length() > 0 && psw.length() > 0) {
            session.setStuNum(stuNo);
            session.setPsw(psw);
            session.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exams, menu);
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
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSessionStartOver() {

        new AsyncTask<String, String, List<Exam>>() {
            @Override
            protected List<Exam> doInBackground(String... params) {
                try {
                    return session.fetchExams();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Exam> recExams) {
                if (recExams != null) {
                    Log.d(TAG, "exams fetch over");
                    adapter.setExams(recExams);
                    adapter.notifyDataSetChanged();

                    loadingProgress.setVisibility(View.GONE);
                }
            }
        }.execute();

    }

    @Override
    public void onSessionStartError(Session.ErrorCode ec) {

    }
}
