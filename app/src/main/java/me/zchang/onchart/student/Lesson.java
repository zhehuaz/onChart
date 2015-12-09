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

package me.zchang.onchart.student;

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

import me.zchang.onchart.config.PreferenceManager;

/**
 * Entity class representative of a block in lesson chart.
 */
public class Lesson implements Comparable {


    private int id;
    private String name;
    private String department;
    private float credit;
    private String teacher;
    private String classroom;
    private char weekDay;
    private int startTime;
    private int endTime;
    private int startWeek;
    private int endWeek;
    private byte weekParity;
    private int labelImgIndex;


    public Lesson() {
        this.name = "";
        this.department = "";
        credit = 0f;
        teacher = "";
        classroom = "";
        weekDay = 0;
        startTime = 0;
        endTime = 0;
        startWeek = 0;
        endWeek = 0;
        weekParity = -1;
        labelImgIndex = 0;
    }

    public Lesson(Lesson lesson) {
        this.name = new String(lesson.name);
        this.department = new String(lesson.department);
        this.teacher = new String(lesson.teacher);
        this.classroom = new String(lesson.classroom);
        this.credit = lesson.credit;
        this.weekDay = lesson.weekDay;
        this.startTime = lesson.startTime;
        this.endTime = lesson.endTime;
        this.startWeek = lesson.startTime;
        this.endWeek = lesson.endWeek;
        this.weekParity = lesson.weekParity;
        this.labelImgIndex = lesson.labelImgIndex;
        this.id = lesson.id;
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

    public char getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(char weekDay) {
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

    public byte getWeekParity() {
        return weekParity;
    }

    public void setWeekParity(byte weekParity) {
        this.weekParity = weekParity;
    }

    public int getLabelImgIndex() {
        return labelImgIndex;
    }

    public void setToNextLabelImg() {
        this.labelImgIndex = (this.labelImgIndex + 1) % PreferenceManager.labelImgs.length;
    }

    public void setLabelImgIndex(int labelImgIndex) {
        this.labelImgIndex = labelImgIndex;
    }

    public int getId() {
        return id;
    }

    public Lesson setId(int id) {
        this.id = id;
        return this;
    }
    @Override
    public int compareTo(Object o) {
        Lesson l = (Lesson)o;
        return this.getStartTime() - l.getStartTime();
    }
}
