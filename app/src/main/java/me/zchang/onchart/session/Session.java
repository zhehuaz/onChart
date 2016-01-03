package me.zchang.onchart.session;


import java.io.IOException;
import java.util.List;

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
 * A session to login and fetch interested information.
 */
public abstract class Session{
    protected SessionStartListener listener;

    protected String stuNum;
    protected String psw;


    protected boolean isStarted = false;

    public boolean isStarted() {
        return isStarted;
    }

    public void setListener(SessionStartListener listener) {
        this.listener = listener;
    }

    public Session() {}
    public Session(SessionStartListener listener) {
        this.listener = listener;
    }

    /**
     * Session starts asynchronously, the start over and start error response
     * via callback interface {@link SessionStartListener]}.
     * @return the HTTP request sent to start.
     */
    public abstract String start();

    public abstract List<Course> fetchSchedule() throws IOException;
    public abstract int fetchWeek() throws IOException;
    public abstract String fetchName() throws IOException;
    public abstract List<Exam> fetchExams() throws IOException;

    public interface SessionStartListener {
        void onSessionStartOver();
        void onSessionStartError(ErrorCode ec);
    }

    public enum ErrorCode {
        SESSION_EC_FAIL_TO_CONNECT
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
        this.psw = psw;
    }

}
