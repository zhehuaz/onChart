package me.zchang.onchart.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Course;

/**
 * Created by Administrator on 2016/1/22.
 */
public class CourseSQLiteHelper extends SQLiteOpenHelper {

    Context context;
    public final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    public CourseSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private enum COURSE_TABLE_INDICES {
        COURSE_ID,
        COURSE_NAME,
        COURSE_DEPARTMENT,
        COURSE_CREDIT,
        COURSE_TEACHER,
        COURSE_CLASSROOM,
        COURSE_WEEKDAY,
        COURSE_START_TIME,
        COURSE_END_TIME,
        COURSE_START_WEEK,
        COURSE_END_WEEK,
        COURSE_WEEK_PARITY,
        COURSE_LABEL_IMG_INDEX
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ context.getString(R.string.course_table_name) +" (" +
                "id INT PRIMARY KEY," + // 0
                "name VARCHAR(100) NOT NULL," + // 1
                "department VARCHAR(100)," + // 2
                "credit FLOAT NOT NULL," + // 3
                "teacher VARCHAR(30)," + // 4
                "classroom VARCHAR(30) NOT NULL," + // 5
                "weekDay INT NOT NULL," + // 6
                "startTime TIME NOT NULL," + // 7
                "endTime TIME NOT NULL," + // 8
                "startWeek INT NOT NULL," + // 9
                "endWeek INT NOT NULL," + // 10
                "weekParity INT," + // 11
                "labelImgIndex INT" + // 12
                ");");
    }

    public void addCourse(Course course) {// TODO validate course
        SQLiteDatabase courseDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", course.getId());
        values.put("name", course.getName());
        values.put("department", course.getDepartment());
        values.put("credit", course.getCredit());
        values.put("classroom", course.getClassroom());
        values.put("weekDay", course.getWeekDay());
        values.put("startWeek", course.getStartWeek());
        values.put("endWeek", course.getEndWeek());

        values.put("startTime", timeFormat.format(course.getStartTime()));
        values.put("endTime", timeFormat.format(course.getEndTime()));
        values.put("weekParity", course.getWeekParity());
        values.put("labelImgIndex", course.getLabelImgIndex());

        courseDatabase.insert(
                context.getString(R.string.course_table_name),
                null,values);
    }

    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase courseDatabase = getReadableDatabase();
        Cursor cursor = courseDatabase.rawQuery("SELECT * from " + context.getString(R.string.course_table_name), null);

        while (cursor.moveToNext()) {
            Course newCourse = new Course();
            String sStartTime = cursor.getString(COURSE_TABLE_INDICES.COURSE_START_TIME.ordinal());
            String sEndTime = cursor.getString(COURSE_TABLE_INDICES.COURSE_END_TIME.ordinal());
            try {
                newCourse.setStartTime(new Time(timeFormat.parse(sStartTime).getTime()));
                newCourse.setEndTime(new Time(timeFormat.parse(sEndTime).getTime()));
                newCourse.setId(cursor.getInt(COURSE_TABLE_INDICES.COURSE_ID.ordinal()));
                newCourse.setName(cursor.getString(COURSE_TABLE_INDICES.COURSE_NAME.ordinal()));
                newCourse.setDepartment(cursor.getString(COURSE_TABLE_INDICES.COURSE_DEPARTMENT.ordinal()));
                newCourse.setCredit(cursor.getFloat(COURSE_TABLE_INDICES.COURSE_CREDIT.ordinal()));
                newCourse.setTeacher(cursor.getString(COURSE_TABLE_INDICES.COURSE_TEACHER.ordinal()));
                newCourse.setClassroom(cursor.getString(COURSE_TABLE_INDICES.COURSE_CLASSROOM.ordinal()));
                newCourse.setWeekDay(cursor.getInt(COURSE_TABLE_INDICES.COURSE_WEEKDAY.ordinal()));
                newCourse.setStartWeek(cursor.getInt(COURSE_TABLE_INDICES.COURSE_START_WEEK.ordinal()));
                newCourse.setEndWeek(cursor.getInt(COURSE_TABLE_INDICES.COURSE_END_WEEK.ordinal()));
                newCourse.setLabelImgIndex(cursor.getInt(COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()));
                // TODO unchecked, how to get a byte from cursor?
                newCourse.setWeekParity((byte)cursor.getShort(COURSE_TABLE_INDICES.COURSE_WEEK_PARITY.ordinal()));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return courses;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
