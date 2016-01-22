package me.zchang.onchart.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/22.
 */
public class CourseSQLiteHelper extends SQLiteOpenHelper {
    public CourseSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE courses (" +
                "id INT," +
                "name VARCHAR(100)," +
                "department VARCHAR(100)," +
                "credit FLOAT," +
                "teacher VARCHAR(30)," +
                "classroom VARCHAR(30)," +
                "weekDay INT," +
                "startTime TIME," +
                "endTime TIME," +
                "startWeek INT," +
                "endWeek INT," +
                "weekParity INT," +
                "labelImageIndex INT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
