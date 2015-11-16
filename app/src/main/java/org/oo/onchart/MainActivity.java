package org.oo.onchart;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.oo.onchart.parser.StudentInfoParser;
import org.oo.onchart.session.BitJwcSession;
import org.oo.onchart.session.Session;
import org.oo.onchart.student.Lesson;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Session.SessionListener{

    private String TAG = "MainActivity";
    private TextView contentText;
    private EditText usrInput;
    private EditText pswInput;
    private Button fetchButton;
    private LinearLayout inputLayout;

    private BitJwcSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new BitJwcSession(this);
        contentText = (TextView) findViewById(R.id.tv_content);
        usrInput = (EditText) findViewById(R.id.et_num);
        pswInput = (EditText) findViewById(R.id.et_pwd);
        fetchButton = (Button) findViewById(R.id.bt_fetch);
        inputLayout = (LinearLayout) findViewById(R.id.ll_input);

        fetchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                session.setUsrNum(usrInput.getText().toString());
                session.setPsw(pswInput.getText().toString());
                session.start();
                inputLayout.setVisibility(View.GONE);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
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
                for(Lesson l : lessons) {
                    contentText.append(l.getName()
                        + "\n" + l.getTeacher()
                        + "\n" + l.getStartWeek()
                        + "\n" + l.getEndWeek()
                        + "\n" + l.getWeekDay()
                        + "\n" + l.getDepartment()
                        + "\n" + l.getStartTime()
                        + "\n" + l.getEndTime()
                        + "\n" + "==================\n");
                }
            }
        }.execute();

    }
}
