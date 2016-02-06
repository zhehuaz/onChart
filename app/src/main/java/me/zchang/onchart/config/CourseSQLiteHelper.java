package me.zchang.onchart.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.zchang.onchart.R;
import me.zchang.onchart.student.Course;
import me.zchang.onchart.student.LabelCourse;

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
 * SQLite helper of courses table.
 */
public class CourseSQLiteHelper extends SQLiteOpenHelper {
	private final static String TAG = "CourseSQLiteHelper";
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
		COURSE_SEMESTER,
		COURSE_LABEL_IMG_INDEX
	}

	private final static String[] FIELD_NAMES = {
			"id",
			"name",
			"department",
			"credit",
			"teacher",
			"classroom",
			"weekDay",
			"startTime",
			"endTime",
			"startWeek",
			"endWeek",
			"weekParity",
			"semester",
			"labelImgIndex"
	};

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String createStatement =
				"CREATE TABLE " + context.getString(R.string.course_table_name) + " (" +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_ID.ordinal()] + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_NAME.ordinal()] + " VARCHAR(100) NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_DEPARTMENT.ordinal()] + " VARCHAR(100)," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_CREDIT.ordinal()] + " FLOAT NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_TEACHER.ordinal()] + " VARCHAR(30)," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_CLASSROOM.ordinal()] + " VARCHAR(30)," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_WEEKDAY.ordinal()] + " INT NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_START_TIME.ordinal()] + " INTEGER NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_END_TIME.ordinal()] + " INTEGER NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_START_WEEK.ordinal()] + " INTEGER NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_END_WEEK.ordinal()] + " INTEGER NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_WEEK_PARITY.ordinal()] + " INT," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_SEMESTER.ordinal()] + " CHAR(6) NOT NULL," +
						FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()] + " INT" +
						")";
		db.execSQL(createStatement);
	}

	/**
	 * Insert a new course in database.Besides, conflict is detected.
	 * This method is intent for inserting a course manually to schedule.
	 * Don't call this to insert plenty of courses, inefficiently.If you have to,
	 * use {@link #addCourses(List)} instead.
	 * @param course The course to be inserted.
	 * @return If the inserted course is valid,
	 */
	public boolean insertCourse(Course course) {
		SQLiteDatabase courseDatabase = getWritableDatabase();

		boolean success = false;
		if (checkConflict(courseDatabase, course) == null) {
			addCourse(courseDatabase, course);
			success = true;
		}
		courseDatabase.close();
		return success;
	}

	/**
	 * Delete a course specified by ID.
	 *
	 * @param id The ID of the course to be deleted.
	 * @return If it is deleted successfully.
	 */
	public boolean deleteCourse(long id) {
		SQLiteDatabase courseDatabase = getWritableDatabase();
		boolean result = true;
		int affectedId = courseDatabase.delete(context.getString(R.string.course_table_name),
				"id = ?", new String[]{id + ""});
		if (affectedId != id)
			result = false;
		courseDatabase.close();
		return result;
	}

	/**
	 * Replace the course when ID conflict occurs.
	 * If not, insert it as calling {@link #insertCourse(Course)}.
	 * This method will also check time conflict.If any, the transaction will be stopped.
	 * @param course The new course to be set, shares the ID field with the old one.
	 */
	public void replaceCourse(Course course) {
		SQLiteDatabase courseDatabase = getWritableDatabase();

		List<Long> conflicts = checkConflict(courseDatabase, course);
		if (conflicts == null || (conflicts.size() == 1 && conflicts.get(0) == course.getId()))
			courseDatabase.replace(context.getString(R.string.course_table_name),
					null,
					putCourseValues(course));
	}

	private long addCourse(SQLiteDatabase courseDatabase, Course course) {
		return courseDatabase.insert(
				context.getString(R.string.course_table_name),
				null,
				putCourseValues(course));
	}

	/**
	 * Convert a {@link Course} to {@link ContentValues}.
	 *
	 * @param course if ID of the course is -1, the id is AUTOINCREMENT.
	 */
	private ContentValues putCourseValues(@NonNull Course course) {
		ContentValues values = new ContentValues();
		if (course.getId() != -1)
			values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_ID.ordinal()], course.getId());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_NAME.ordinal()], course.getName());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_DEPARTMENT.ordinal()], course.getDepartment());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_CREDIT.ordinal()], course.getCredit());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_TEACHER.ordinal()], course.getTeacher());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_CLASSROOM.ordinal()], course.getClassroom());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_WEEKDAY.ordinal()], course.getWeekDay());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_START_WEEK.ordinal()], course.getStartWeek());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_END_WEEK.ordinal()], course.getEndWeek());

		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_START_TIME.ordinal()], course.getStartTime());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_END_TIME.ordinal()], course.getEndTime());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_WEEK_PARITY.ordinal()], course.getWeekParity());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_SEMESTER.ordinal()], course.getSemester());
		values.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()], course.getLabelImgIndex());

		return values;
	}

	/**
	 * Check if the course has conflicts in the database.
	 * If any, conflict courses' IDs are returned.
	 *
	 * @param courseDatabase The database to check.
	 * @param course         The course to check.
	 * @return The conflict courses' IDs.If no, returns null.
	 */
	private List<Long> checkConflict(SQLiteDatabase courseDatabase, @NonNull Course course) {
		Cursor cursor = courseDatabase.query(context.getString(R.string.course_table_name),
				new String[]{"id"},
				"semester = ? AND (startTime <= ? OR endTime >= ?) AND (startWeek <= ? OR endWeek >= ?) AND weekDay IS ?",
				new String[]{course.getSemester(),
						course.getEndTime() + "",
						course.getStartTime() + "",
						course.getEndWeek() + "",
						course.getStartWeek() + "",
						course.getWeekDay() + ""},
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			List<Long> retIds = new ArrayList<>();
			do {
				retIds.add(cursor.getLong(COURSE_TABLE_INDICES.COURSE_ID.ordinal()));
			} while (cursor.moveToNext());
			cursor.close();
			return retIds;
		} else {
			return null;
		}
	}

	/**
	 * Add a list of courses to the database.
	 * Conflict is not detected, so you should make sure the courses don't overlap one anther.
	 *
	 * This method is used for initializing the schedule, which means to add courses to an empty
	 * course table.
	 * If you have detected the conflict, use {@link #insertCourse(Course)} instead.
	 *
	 * @param courses The courses to be added.
	 */
	public void addCourses(List<Course> courses) {
		if (courses != null) {
			SQLiteDatabase courseDatabase = getWritableDatabase();
			for (Course course : courses) {
				addCourse(courseDatabase, course);
			}
			courseDatabase.close();
		}
	}

	/**
	 * Get all the courses from database.
	 * @return The list of courses.
	 */
	public List<Course> getCourses() {
		return getCourses(null);
	}

	/**
	 * Get courses from database.
	 *
	 * @param whereClause The where clause in query state, "WHERE" is included.
	 *                    All the courses will be returned if null.
	 * @return The list of courses.
	 */
	public List<Course> getCourses(String whereClause) {
		List<Course> courses = new ArrayList<>();
		SQLiteDatabase courseDatabase = getReadableDatabase();
		if (whereClause == null)
			whereClause = "";
		Cursor cursor = courseDatabase.rawQuery("SELECT * from " + context.getString(R.string.course_table_name) + " " + whereClause, null);

		while (cursor.moveToNext()) {
			Course newCourse = new LabelCourse();
			newCourse.setStartTime(cursor.getLong(COURSE_TABLE_INDICES.COURSE_START_TIME.ordinal()));
			newCourse.setEndTime(cursor.getLong(COURSE_TABLE_INDICES.COURSE_END_TIME.ordinal()));
			newCourse.setId(cursor.getLong(COURSE_TABLE_INDICES.COURSE_ID.ordinal()));
			newCourse.setName(cursor.getString(COURSE_TABLE_INDICES.COURSE_NAME.ordinal()));
			newCourse.setDepartment(cursor.getString(COURSE_TABLE_INDICES.COURSE_DEPARTMENT.ordinal()));
			newCourse.setCredit(cursor.getFloat(COURSE_TABLE_INDICES.COURSE_CREDIT.ordinal()));
			newCourse.setTeacher(cursor.getString(COURSE_TABLE_INDICES.COURSE_TEACHER.ordinal()));
			newCourse.setClassroom(cursor.getString(COURSE_TABLE_INDICES.COURSE_CLASSROOM.ordinal()));
			newCourse.setWeekDay(cursor.getInt(COURSE_TABLE_INDICES.COURSE_WEEKDAY.ordinal()));
			newCourse.setStartWeek(cursor.getInt(COURSE_TABLE_INDICES.COURSE_START_WEEK.ordinal()));
			newCourse.setEndWeek(cursor.getInt(COURSE_TABLE_INDICES.COURSE_END_WEEK.ordinal()));
			newCourse.setLabelImgIndex(cursor.getInt(COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()));
			newCourse.setWeekParity((byte) cursor.getInt(COURSE_TABLE_INDICES.COURSE_WEEK_PARITY.ordinal()));

			courses.add(newCourse);
		}
		cursor.close();
		return courses;
	}

	public void clearCourses() {
		SQLiteDatabase courseDatabase = getWritableDatabase();
		courseDatabase.execSQL("DELETE FROM " + context.getString(R.string.course_table_name));
		courseDatabase.close();
	}

	public void setImgPathIndex(long id, int resIndex) {
		SQLiteDatabase courseDatabase = getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put(FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()], resIndex);
		courseDatabase.update(context.getString(R.string.course_table_name), value, "id = " + id, null);
		courseDatabase.close();
	}

	public int getImgPathIndex(int id, int defaultResIndex) {
		SQLiteDatabase courseDatabase = getReadableDatabase();

		Cursor cursor =
				courseDatabase.rawQuery(
						"SELECT "
								+ FIELD_NAMES[COURSE_TABLE_INDICES.COURSE_LABEL_IMG_INDEX.ordinal()]
								+ " FROM "
								+ context.getString(R.string.course_table_name)
								+ " WHERE id = "
								+ id, null);
		int index = defaultResIndex;
		if (cursor.moveToNext()) {
			index = cursor.getInt(0);
		}
		cursor.close();
		courseDatabase.close();
		return index;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
