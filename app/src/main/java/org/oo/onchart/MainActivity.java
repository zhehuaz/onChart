package org.oo.onchart;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.oo.onchart.http.HttpRequest;
import org.oo.onchart.http.HttpResponse;
import org.oo.onchart.http.RequestMethod;
import org.oo.onchart.parser.StudentInfoParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private TextView contentText;
    private EditText usrInput;
    private EditText pswInput;
    private Button fetchButton;
    private LinearLayout inputLayout;
    private String usrNum;
    private String psw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contentText = (TextView) findViewById(R.id.tv_content);
        usrInput = (EditText) findViewById(R.id.et_num);
        pswInput = (EditText) findViewById(R.id.et_pwd);
        fetchButton = (Button) findViewById(R.id.bt_fetch);
        inputLayout = (LinearLayout) findViewById(R.id.ll_input);

        fetchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                usrNum = usrInput.getText().toString();
                psw = pswInput.getText().toString();
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            HttpRequest homeRequest = new HttpRequest("http://10.5.2.80");

                            final URL loginUrl = homeRequest.send().getRequestUrl();

                            HttpRequest loginRequest = new HttpRequest(loginUrl.toString(), RequestMethod.POST) {
                                @Override
                                protected String getSentData() {
                                    return "__VIEWSTATE=dDwtMjEzNzcwMzMxNTs7Pj9pP88cTsuxYpAH69XV04GPpkse&TextBox1=" + usrNum + "&TextBox2=" + psw + "&RadioButtonList1=%D1%A7%C9%FA&Button1=+%B5%C7+%C2%BC+\n" +
                                            "Name\t\n";
                                }
                            };
                            loginRequest.send();

                            String path = "/xskbcx.aspx?xh=" + usrNum + "&xm=%D5%C5%D5%DC%BB%AA&gnmkdm=N121603";
                            HttpRequest chartRequest = new HttpRequest(loginUrl.toString().substring(0, 43) + path) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> param = new HashMap<>();
                                    param.put("Referer", loginUrl.toString());
                                    return param;
                                }
                            };
                            HttpResponse chartResponse = chartRequest.send();
                            String htmlRes = chartResponse.getResponseContent();
                            Log.i(TAG, "Response : " + chartResponse.getResponseContent());
                            return htmlRes;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        contentText.setText(response);
                        StudentInfoParser.parseChart(response);
                    }
                }.execute();
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
}
