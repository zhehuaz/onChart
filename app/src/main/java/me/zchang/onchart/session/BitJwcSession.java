/*
 *
 *  *    Copyright 2015 Zhehua Chang
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package me.zchang.onchart.session;

import android.os.AsyncTask;
import android.util.Log;

import me.zchang.onchart.http.HttpError;
import me.zchang.onchart.http.HttpRequest;
import me.zchang.onchart.http.HttpResponse;
import me.zchang.onchart.http.RequestMethod;
import me.zchang.onchart.parser.StudentInfoParser;
import me.zchang.onchart.student.Course;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

/**
 * Session to http://jwc.bit.edu.cn
 */
public class BitJwcSession extends Session{
    private String TAG = "BitJwcSession";
    private String stuNum;
    private String psw;
    private URL loginUrl;

    private String startResponse = null;

    public BitJwcSession(HttpRequest request) {
        super(request);
    }

    public BitJwcSession(SessionListener listener) {
        super(listener);
    }

    @Override
    public HttpRequest start() {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    HttpRequest homeRequest = new HttpRequest("http://10.5.2.80");

                    loginUrl = homeRequest.send().getRequestUrl();

                    HttpRequest loginRequest = new HttpRequest(loginUrl.toString(), RequestMethod.POST) {
                        @Override
                        protected String getSentData() {
                            return "__VIEWSTATE=dDwtMjEzNzcwMzMxNTs7Pj9pP88cTsuxYpAH69XV04GPpkse&TextBox1="+ stuNum +"&TextBox2="+ psw +"&RadioButtonList1=%D1%A7%C9%FA&Button1=+%B5%C7+%C2%BC+\n" +
                                    "Name\t\n";
                        }
                    };
                    HttpResponse response = loginRequest.send();

                    sessioId = loginUrl.toString().substring(11,42);
                    return response.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(ErrorCode.SESSION_EC_FAIL_TO_CONNECT);
                    Log.e(TAG, "Can't connect to JWC");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result != null) {
                    startResponse = result;
                    listener.onStartOver();
                }
            }
        }.execute();
        return null;
    }

    public List<Course> fetchLessonChart() {
        String path = "/xskbcx.aspx?xh=" + stuNum + "&xm=%D5%C5%D5%DC%BB%AA&gnmkdm=N121603";
        HttpRequest chartRequest = null;
        try {
            if(loginUrl != null) {
                chartRequest = new HttpRequest(loginUrl.toString().substring(0, 43) + path) {
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
                return StudentInfoParser.parseChart(htmlRes);
            }
            return new ArrayList<>();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int fetchWeek() throws IOException {
        String path = "http://10.0.6.51";
        HttpRequest weekRequest = null;

        try {
            weekRequest = new HttpRequest(path);
            HttpResponse response = weekRequest.send();
            return StudentInfoParser.parseWeek(response.getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String fetchName() {
        if(startResponse != null) {
            return StudentInfoParser.parseName(startResponse);
        }
        return null;
    }

    public String pswToUnicode(String psw) {
        StringBuffer sb = new StringBuffer();
        int length = psw.length();
        char c;
        int temp;
        for (int i = 0;i < length;i ++) {
            c = psw.charAt(i);
            if((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c <= 'a' && c >= 'z')) {
                sb.append(c);
            } else {
                temp = c;
                sb.append("%" + String.format("%x", temp));
            }
        }

        return sb.toString();
    }

    @Override
    public void onError(HttpError error) {

    }

    @Override
    public void onResponse(HttpResponse response) {
        this.sessioId = parseSessionId(response.getRequestUrl());
    }

    private String parseSessionId(URL url) {
        return url.getPath().substring(2,2 + 12);
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = pswToUnicode(psw);
    }

    public String getStartResponse() {
        return startResponse;
    }
}
