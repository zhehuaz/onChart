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

package me.zchang.onchart.ui;

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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import me.zchang.onchart.R;
import me.zchang.onchart.config.ConfigManager;
import me.zchang.onchart.config.MainApp;
import me.zchang.onchart.session.BitJwcSession;
import me.zchang.onchart.session.Session;
import me.zchang.onchart.session.events.ExamsFetchOverEvent;
import me.zchang.onchart.session.events.SessionErrorEvent;
import me.zchang.onchart.session.events.SessionStartOverEvent;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.ui.adapter.ExamListAdapter;



public class ExamsActivity extends AppCompatActivity {

    public static final String TAG = "ExamsActivity";

    private Toolbar toolbar;
    private ImageView stuffImage;
    private RecyclerView recyclerView;
    private ProgressBar loadingProgress;
    ExamListAdapter adapter;

    private ConfigManager configManager;
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


        configManager = ((MainApp) getApplication()).getConfigManager();
	    session = new BitJwcSession();

        String stuNo = configManager.getStuNo();
        String psw = configManager.getPassword();
        if (stuNo.length() > 0 && psw.length() > 0) {
            loadingProgress.setVisibility(View.VISIBLE);
            session.setStuNum(stuNo);
            session.setPsw(psw);
            session.start();
        }
    }

	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onExamsFetchOverEvent(ExamsFetchOverEvent event) {
		List<Exam> recExams = event.getExams();
		if (recExams != null) {
			Log.d(TAG, "exams fetch over");
			adapter.setExams(recExams);
			adapter.notifyDataSetChanged();

			loadingProgress.setVisibility(View.GONE);
		} else {// invalid account.
			Toast.makeText(ExamsActivity.this, getString(R.string.alert_invalid_account), Toast.LENGTH_SHORT).show();
		}
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onSessionStartOverEvent(SessionStartOverEvent event) {
		try {
			EventBus.getDefault().post(new ExamsFetchOverEvent(session.fetchExams()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSessionErrorEvent(SessionErrorEvent event) {

	}
}
