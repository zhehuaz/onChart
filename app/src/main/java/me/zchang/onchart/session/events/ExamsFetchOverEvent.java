package me.zchang.onchart.session.events;

import java.util.List;

import me.zchang.onchart.student.Exam;

/**
 * Created by langley on 2/7/16.
 */
public class ExamsFetchOverEvent {
	List<Exam> exams;

	public ExamsFetchOverEvent(List<Exam> exams) {
		this.exams = exams;
	}

	public List<Exam> getExams() {
		return exams;
	}
}
