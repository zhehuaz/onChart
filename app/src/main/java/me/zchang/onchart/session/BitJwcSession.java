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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zchang.onchart.http.HttpError;
import me.zchang.onchart.http.HttpRequest;
import me.zchang.onchart.http.HttpResponse;
import me.zchang.onchart.http.RequestMethod;
import me.zchang.onchart.parser.StudentInfoParser;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.Exam;

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
 * Session to <a href="http://jwc.bit.edu.cn"/>
 */
public class BitJwcSession extends Session{
    private String TAG = "BitJwcSession";
    private URL loginUrl;

    private String startResponse = null;

    public BitJwcSession(HttpRequest request) {
        super(request);
    }

    public BitJwcSession(SessionStartListener listener) {
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
                            return "__VIEWSTATE=dDwtMjEzNzcwMzMxNTs7Pj9pP88cTsuxYpAH69XV04GPpkse&TextBox1="+ stuNum +"&TextBox2="+ pswToUnicode(psw) +"&RadioButtonList1=%D1%A7%C9%FA&Button1=+%B5%C7+%C2%BC+\n" +
                                    "Name\t\n";
                        }
                    };
                    HttpResponse response = loginRequest.send();

                    if (loginUrl.toString().length() < 42)
                        throw new IOException("Intranet connection error");
                    sessionId = loginUrl.toString().substring(11,42);
                    return response.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                    isStarted = false;
                    listener.onSessionStartError(ErrorCode.SESSION_EC_FAIL_TO_CONNECT);
                    Log.e(TAG, "Can't connect to JWC");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result != null) {
                    startResponse = result;// TODO account validation here.
                    isStarted = true;
                    listener.onSessionStartOver();

                }
            }
        }.execute();
        return null;
    }

    /**
     * Fetch schedule from <a href="http://jwc.bit.edu.cn"/> synchronously,
     * having been authorized required.
     * @return a list of courses.
     */
    @Override
    public List<Course> fetchSchedule() throws IOException {
        String path = "/xskbcx.aspx?xh=" + stuNum + "&xm=%D5%C5%D5%DC%BB%AA&gnmkdm=N121603";
        HttpRequest chartRequest = null;
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
            Log.i(TAG, "Response : " + chartResponse.getContent());// TODO account validation here.
            return StudentInfoParser.parseSchedule(htmlRes);
        }
        return new ArrayList<>();
    }

    /**
     * Fetch current week number from <a href="http://jwc.bit.edu.cn"/> homepage.
     * @return current week number.
     * @throws IOException Unable to access to the page.
     */
    @Override
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

    /**
     * Fetch student name, having been authorized required.
     * @return the student name.
     */
    @Override
    public String fetchName() {
        if(startResponse != null) {
            return StudentInfoParser.parseName(startResponse);
        }
        return null;
    }

    @Override
    public List<Exam> fetchExams() throws IOException {
        String path = "/xskscx.aspx?xh=" + stuNum + "&xm=%D5%C5%D5%DC%BB%AA&gnmkdm=N121604";
        HttpRequest examRequest = null;
        if(loginUrl != null) {
            examRequest = new HttpRequest(loginUrl.toString().substring(0, 43) + path) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> param = new HashMap<>();
                    param.put("Referer", loginUrl.toString());
                    return param;
                }
            };
            HttpResponse examResponse = examRequest.send();
            String htmlRes = examResponse.getContent();
            Log.i(TAG, "Response : " + examResponse.getContent());
            // TODO account validation here in case that the password has been changed.
            return StudentInfoParser.parseExams(htmlRes);
        }
        return new ArrayList<>();
    }

    /**
     * Utility to covert the symbols in the password into unicode.
     * @param psw the input password.
     * @return coverted password
     */
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
    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public void onError(HttpError error) {
    }

    @Override
    public void onResponse(HttpResponse response) {
        this.sessionId = parseSessionId(response.getRequestUrl());
    }

    /**
     * Get session id from the redirected URL.
     * @param url the redirected URL.
     * @return session id.
     */
    private String parseSessionId(URL url) {
        return url.getPath().substring(2,2 + 12);
    }



    public String getStartResponse() {
        return startResponse;
    }


}
