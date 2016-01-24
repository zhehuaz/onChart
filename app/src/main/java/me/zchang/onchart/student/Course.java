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

import java.sql.Time;

import me.zchang.onchart.parser.Utils;

/**
 * Entity class representative of a block in the schedule.
 */
public class Course implements Comparable{

    /**
     * The ID of each course to identify the blocks in the schedule.
     * That's to say, the ID identifies a lesson at certain time in a week.
     */
    protected int id;

    /**
     * The name of the course.
     * Attention that this field can't identify a lesson because lessons with one name
     * at different times are considered not equal.
     */
    protected String name;


    /**
     * The department hosting the course.
     */
    protected String department;

    /**
     * Credit of the course.
     */
    protected float credit;

    /**
     * Teacher of the course.
     */
    protected String teacher;

    /**
     * Classroom of the course.
     */
    protected String classroom;

    /**
     * The day of the course index.
     * The index is from 0 to 6 for Monday to Sunday.
     */
    protected int weekDay;

    /**
     * The time when the class begins.
     * The time should be on the day of Jan. 1st, 1970.
     */
    protected Time startTime;

    /**
     * The time when the class ends.
     * The time should be on the day of Jan. 1st, 1970.
     */
    protected Time endTime;

    /**
     * The first week of the course.
     */
    protected int startWeek;

    /**
     * The last week of the course.
     */
    protected int endWeek;

    /**
     * Whether the course is only in odd or even weeks or not.
     * -1 the course is in every week
     * 0 only in odd weeks
     * 1 only in even weeks
     */
    protected byte weekParity;

    /**
     * The label image for the course, which can be shown on screen.
     * This integer refers to a image,and you can define the map yourself.
     * Thus, {@link #setToNextLabelImg()} is supposed to be implemented.If not,
     * this class is considered no label.
     */
    protected int labelImgIndex;


    /**
     * Constructor of Course.
     */
    public Course() {
        this.name = "";
        this.department = "";
        credit = 0f;
        teacher = "";
        classroom = "";
        weekDay = 0;
        startTime = new Time(0);
        endTime = new Time(0);
        startWeek = 0;
        endWeek = 0;
        weekParity = -1;
        labelImgIndex = 0;
    }

    /**
     * Copy another course, and refer to different objects.
     * @param course
     */
    public Course(Course course) {
        this.name = new String(course.name);
        this.department = new String(course.department);
        this.teacher = new String(course.teacher);
        this.classroom = new String(course.classroom);
        this.credit = course.credit;
        this.weekDay = course.weekDay;
        this.startTime = course.startTime;
        this.endTime = course.endTime;
        this.startWeek = course.startWeek;
        this.endWeek = course.endWeek;
        this.weekParity = course.weekParity;
        this.labelImgIndex = course.labelImgIndex;
        this.id = course.id;
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

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
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

    public void setToNextLabelImg() { }

    public void setLabelImgIndex(int labelImgIndex) {
        this.labelImgIndex = labelImgIndex;
    }

    public int getId() {
        return id;
    }

    public Course setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public int compareTo(Object o) {
        Course l = (Course)o;
        return this.getStartTime().after(l.getStartTime()) ? 1 : -1;
    }


}
