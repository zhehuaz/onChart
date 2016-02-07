package me.zchang.onchart.session.events;

/**
 * Created by langley on 2/7/16.
 */
public class WeekFetchOverEvent {
	int week;

	public WeekFetchOverEvent(int week) {
		this.week = week;
	}

	public int getWeek() {
		return week;
	}
}
