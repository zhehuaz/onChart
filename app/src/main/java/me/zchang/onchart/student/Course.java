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

/**
 * Entity class representative of a block in the schedule.
 */
public class Course implements Comparable{

    /**
     * The ID of each course to identify the blocks in the schedule.
     * That's to say, the ID identifies a lesson at certain time in a week.
     */
    protected long id;

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
     * The time should be on the day of Jan. 1st, 1970 GMT.
     */
    protected long startTime;

    /**
     * The time when the class ends.
     * The time should be on the day of Jan. 1st, 1970 GMT.
     */
    protected long endTime;

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
	 * The semester this course is in.
	 * The format is "YYYY-N", which means the nth semester in the year of YYYY-YYYY+!.
	 * For example, "2015-1" means the 1st semester in 2015-2016.
	 */
	protected String semester;

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
        this.id = -1;
        this.name = "";
        this.department = "";
        this.credit = 0f;
        this.teacher = "";
        this.classroom = "";
        this.weekDay = 0;
        this.startTime = 0;
        this.endTime = 0;
        this.startWeek = 0;
        this.endWeek = 0;
        this.weekParity = -1;
	    this.semester = "";
	    this.labelImgIndex = 0;
    }

    /**
     * Copy another course, and refer to different objects.
     * @param course The course to be copied.
     */
    public Course(Course course) {
        this.name = course.name;
        this.department = course.department;
        this.teacher = course.teacher;
        this.classroom = course.classroom; // safe and efficient.
        this.credit = course.credit;
        this.weekDay = course.weekDay;
        this.startTime = course.startTime;
        this.endTime = course.endTime;
        this.startWeek = course.startWeek;
        this.endWeek = course.endWeek;
        this.weekParity = course.weekParity;
	    this.semester = course.semester;
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
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

    public long getId() {
        return id;
    }

    public Course setId(long id) {
        this.id = id;
        return this;
    }

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	@Override
	public int compareTo(Object o) {
        Course l = (Course)o;
        return(int)(this.getStartTime() - l.getStartTime());
    }


}
