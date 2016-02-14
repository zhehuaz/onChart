
package me.zchang.onchart.session;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.zchang.onchart.parser.StudentInfoParser;
import me.zchang.onchart.session.events.ScheduleFetchOverEvent;
import me.zchang.onchart.session.events.SessionErrorEvent;
import me.zchang.onchart.session.events.SessionStartOverEvent;
import me.zchang.onchart.session.events.HomepageFetchOverEvent;
import me.zchang.onchart.student.Exam;
import me.zchang.onchart.ui.MainActivity;


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
	private String stuName;

    @Override
    public void start() {
	    new Thread(new Runnable() {
		    @Override
		    public void run() {
			    Request request = new Request.Builder()
					    .url("http://10.5.2.80")
					    .build();
			    Response response = null;
			    try {
				    response = httpClient.newCall(request).execute();
				    loginUrl = response.request().urlString();
				    if (loginUrl == null
						    || loginUrl.length() == 0
						    || !response.isSuccessful()
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
				    Request homeRequest = loginResult.request();
				    homeRequest = homeRequest.newBuilder().addHeader("Referer", loginUrl).build();
				    Response homePage = httpClient.newCall(homeRequest).execute();
				    startResponse = homePage.body().string();
				    stuName = fetchName();
				    if (stuName != null) {// name detected, as good as account validation.
					    isStarted = true;
					    EventBus.getDefault().post(new SessionStartOverEvent());
				    } else {
					    EventBus.getDefault().post(new SessionErrorEvent(ErrorCode.SESSION_EC_INVALID_ACCOUNT));
				    }
			    } catch (IOException e) {
				    SessionErrorEvent ee = new SessionErrorEvent(ErrorCode.SESSION_EC_FAIL_TO_CONNECT);
				    EventBus.getDefault().post(ee);
				    e.printStackTrace();
			    }
		    }
	    }).start();
    }

    /**
     * Fetch schedule from <a href="http://jwc.bit.edu.cn"/> synchronously,
     * having been authorized required.
     */
    @Override
    public void fetchSchedule() {
	    new Thread(new Runnable() {
		    @Override
		    public void run() {
			    String path = "/xskbcx.aspx?xh=" + stuNum + "&xm=%D5%C5%D5%DC%BB%AA&gnmkdm=N121603";
			    if (loginUrl != null) {
				    Request scheduleRequest = new Request.Builder()
						    .addHeader("Referer", loginUrl)
						    .get()
						    .url(loginUrl.substring(0, 43) + path)
						    .build();
				    Response scheduleResponse = null;
				    try {
					    scheduleResponse = httpClient.newCall(scheduleRequest).execute();
					    if (scheduleResponse.isSuccessful()) {
						    EventBus.getDefault().post(
								    new ScheduleFetchOverEvent(StudentInfoParser.parseCourses(scheduleResponse.body().string()))
						    );
						    Log.i(MainActivity.TAG, "post schedule fetch over");
					    }
				    } catch (IOException e) {
					    EventBus.getDefault().post(new SessionErrorEvent(ErrorCode.SESSION_EC_FETCH_SCHEDULE));
					    e.printStackTrace();
				    }
			    }
		    }
	    }).start();
    }

    /**
     * Fetch <a href="http://jwc.bit.edu.cn"/> homepage
     * for week number and current date.
     */
    @Override
    public void fetchHomePage() {
	    new Thread(new Runnable() {
		    @Override
		    public void run() {
			    String path = "http://10.0.6.51";

			    Request request = new Request.Builder()
					    .url(path)
					    .get()
					    .build();
			    Response weekResponse = null;
			    try {
				    weekResponse = httpClient.newCall(request).execute();
				    if (weekResponse.isSuccessful()) {
					    EventBus.getDefault().post(new HomepageFetchOverEvent(StudentInfoParser.parseWeek(weekResponse.body().string())));
				    }
			    } catch (IOException e) {
				    EventBus.getDefault().post(new SessionErrorEvent(ErrorCode.SESSION_EC_FETCH_WEEK));
				    e.printStackTrace();
			    }

		    }
	    }).start();
    }

    /**
     * Fetch student name, having been authorized required.
     * @return the student name.
     */
    @Override
    public String fetchName() {
        if(startResponse != null) {
	        String name = StudentInfoParser.parseName(startResponse);
	        if (name != null)
		        return name;
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
