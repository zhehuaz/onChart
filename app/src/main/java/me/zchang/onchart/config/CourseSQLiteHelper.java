package me.zchang.onchart.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Course;

/**
 * Created by Administrator on 2016/1/22.
 */
public class CourseSQLiteHelper extends SQLiteOpenHelper {

    Context context;
    public CourseSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ context.getString(R.string.course_table_name) +" (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "department VARCHAR(100)," +
                "credit FLOAT NOT NULL," +
                "teacher VARCHAR(30)," +
                "classroom VARCHAR(30) NOT NULL," +
                "weekDay INT NOT NULL," +
                "startTime TIME NOT NULL," +
                "endTime TIME NOT NULL," +
                "startWeek INT NOT NULL," +
                "endWeek INT NOT NULL," +
                "weekParity INT," +
                "labelImgIndex INT" +
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
        DateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        values.put("startTime", format.format(course.getStartTime()));
        values.put("endTime", format.format(course.getEndTime()));
        values.put("weekParity", course.getWeekParity());
        values.put("labelImgIndex", course.getLabelImgIndex());

        courseDatabase.insert(
                context.getString(R.string.course_table_name),
                null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
