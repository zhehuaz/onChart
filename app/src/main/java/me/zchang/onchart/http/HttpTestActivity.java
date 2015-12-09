package me.zchang.onchart.http;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

public class HttpTestActivity extends AppCompatActivity{
    public final static String TAG = "HttpTestActivity";
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
        setContentView(me.zchang.onchart.R.layout.activity_http_test);

        contentText = (TextView) findViewById(me.zchang.onchart.R.id.tv_content);
        usrInput = (EditText) findViewById(me.zchang.onchart.R.id.et_num);
        pswInput = (EditText) findViewById(me.zchang.onchart.R.id.et_pwd);
        fetchButton = (Button) findViewById(me.zchang.onchart.R.id.bt_fetch);
        inputLayout = (LinearLayout) findViewById(me.zchang.onchart.R.id.ll_input);

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
                                    return "__VIEWSTATE=dDwtMjEzNzcwMzMxNTs7Pj9pP88cTsuxYpAH69XV04GPpkse&TextBox1="+ usrNum +"&TextBox2="+ psw +"&RadioButtonList1=%D1%A7%C9%FA&Button1=+%B5%C7+%C2%BC+\n" +
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
                            String htmlRes = chartResponse.getContent();
                            Log.i(TAG, "Response : " + chartResponse.getContent());
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
                    }
                }.execute();
                inputLayout.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(me.zchang.onchart.R.menu.menu_http_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == me.zchang.onchart.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
