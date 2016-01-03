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

import android.graphics.Path;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
    private String loginUrl;
    private final OkHttpClient httpClient = new OkHttpClient();
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
                Request request = new Request.Builder()
                        .url("http://10.5.2.80")
                        .build();
                Response response = null;
                try {
                    response = httpClient.newCall(request).execute();
                    loginUrl = response.request().urlString();
                    if (loginUrl == null
                            || loginUrl.length() == 0
                            ||!response.isSuccessful()
                            || loginUrl.length() < 42)
                        throw new IOException("Intranet connection error");
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("__VIEWSTATE", "dDwtMjEzNzcwMzMxNTs7Pj9pP88cTsuxYpAH69XV04GPpkse")
                            .add("TextBox1", stuNum)
                            .add("TextBox2", pswToUnicode(psw))
                            .add("RadioButtonList1", "%D1%A7%C9%FA")
                            .add("Button1", "+%B5%C7+%C2%BC+")
                            .build();
                    Request loginRequest = new Request.Builder()
                            .url(loginUrl)
                            .post(formBody)
                            .build();
                    Response loginResult = httpClient.newCall(loginRequest).execute();
                    sessionId = loginUrl.substring(11, 42);
                    Request homeRequest = loginResult.request();
                    homeRequest = homeRequest.newBuilder().addHeader("Referer", loginUrl).build();
                    Response homePage = httpClient.newCall(homeRequest).execute();
                    return homePage.body().string();
                } catch (IOException e) {
                    listener.onSessionStartError(ErrorCode.SESSION_EC_FAIL_TO_CONNECT);
                    e.printStackTrace();
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
        if (loginUrl != null) {
            Request scheduleRequest = new Request.Builder()
                    .addHeader("Referer", loginUrl)
                    .get()
                    .url(loginUrl.substring(0, 43) + path)
                    .build();
            Response scheduleResponse = httpClient.newCall(scheduleRequest).execute();
            if (scheduleResponse.isSuccessful()) {
                return StudentInfoParser.parseSchedule(scheduleResponse.body().string());
            }
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

        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();
        Response weekResponse = httpClient.newCall(request).execute();
        if (weekResponse.isSuccessful()) {
            return StudentInfoParser.parseWeek(weekResponse.body().string());
        }
        return 0;
//
//        HttpRequest weekRequest = null;
//
//        try {
//            weekRequest = new HttpRequest(path);
//            HttpResponse response = weekRequest.send();
//            return StudentInfoParser.parseWeek(response.getContent());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return 0;
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
        Request request = new Request.Builder()
                .url(loginUrl.substring(0, 43) + path)
                .addHeader("Referer", loginUrl)
                .build();
        Response examResponse = httpClient.newCall(request).execute();
        if (examResponse.isSuccessful()) {
            String htmlRes = examResponse.body().string();
            // TODO I guess account validation should be in StudentInfoParser.parseExams()
            return StudentInfoParser.parseExams(htmlRes);
        }
        return new ArrayList<>();
//
//        HttpRequest examRequest = null;
//        if(loginUrl != null) {
//            examRequest = new HttpRequest(loginUrl.substring(0, 43) + path) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> param = new HashMap<>();
//                    param.put("Referer", loginUrl);
//                    return param;
//                }
//            };
//            HttpResponse examResponse = examRequest.send();
//            String htmlRes = examResponse.getContent();
//            Log.i(TAG, "Response : " + examResponse.getContent());
//            // TODO account validation here in case that the password has been changed.
//            return StudentInfoParser.parseExams(htmlRes);
//        }
//        return new ArrayList<>();
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
