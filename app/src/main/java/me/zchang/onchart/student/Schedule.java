/*
 *     Copyright 2016 Zhehua Chang
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package me.zchang.onchart.student;

import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class Schedule {
    String year;
    String semester;
    List<Course> courses;

    public Schedule(String year, String semester, List<Course> courses) {
        this.year = year;
        this.semester = semester;
        this.courses = courses;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
