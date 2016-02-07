package me.zchang.onchart.session.events;

import java.util.ArrayList;
import java.util.List;

import me.zchang.onchart.student.Course;

/**
 * Created by langley on 2/7/16.
 */
public class ScheduleFetchOverEvent {
	List<Course> courses = new ArrayList<>();

	public ScheduleFetchOverEvent(List<Course> courses) {
		if (courses != null)
			this.courses = courses;
	}

	public List<Course> getCourses() {
		return courses;
	}
}
