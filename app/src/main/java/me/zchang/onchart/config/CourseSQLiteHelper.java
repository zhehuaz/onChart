package me.zchang.onchart.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                "teacher VARCHAR(30) NOT NULL," +
                "classroom VARCHAR(30) NOT NULL," +
                "weekDay INT NOT NULL," +
                "startTime TIME NOT NULL," +
                "endTime TIME NOT NULL," +
                "startWeek INT NOT NULL," +
                "endWeek INT NOT NULL," +
                "weekParity INT," +
                "labelImageIndex INT" +
                ");");
    }

    public void addCourse(Course course) {
        SQLiteDatabase courseDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", course.getId());
        values.put("name", course.getName());
        values.put("department", course.getDepartment());
        values.put("credit", course.getCredit());
        values.put("classroom", course.getClassroom());
        values.put("weekDay", course.getWeekDay());
        //values.put();
//        courseDatabase.insert(
//                context.getString(R.string.course_table_name,
//                null,
//
//                        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
