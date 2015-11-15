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

package org.oo.onchart.student;

/**
 * Entity class representative of a block in lesson chart.
 */
public class Lesson{
    private String name;
    private String department;
    private float credit;
    private String teacher;
    private String classroom;
    private String weekDay;
    private int startTime;
    private int endTime;
    private int startWeek;
    private int endWeek;

    public Lesson() {

    }

    public Lesson(Lesson lesson) {
        if(lesson.name != null)
            this.name = new String(lesson.name);
        if(lesson.department != null)
            this.department = new String(lesson.department);
        if(lesson.teacher != null)
            this.teacher = new String(lesson.teacher);
        if(lesson.classroom != null)
            this.classroom = new String(lesson.classroom);

        this.credit = lesson.credit;
        if(lesson.weekDay != null)
            this.weekDay = lesson.weekDay;
        this.startTime = lesson.startTime;
        this.endTime = lesson.endTime;
        this.startWeek = lesson.startTime;
        this.endWeek = lesson.endWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }
}
