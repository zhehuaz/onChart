package me.zchang.onchart.session.events;

/**
 * Created by langley on 2/7/16.
 */
public class HomepageFetchOverEvent {
	int week;

	public HomepageFetchOverEvent(int week) {
		this.week = week;
	}

	public int getWeek() {
		return week;
	}
}
