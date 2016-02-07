package me.zchang.onchart.session.events;

import me.zchang.onchart.session.Session;

/**
 * Created by langley on 2/7/16.
 */
public class SessionErrorEvent {
	Session.ErrorCode ec;

	public SessionErrorEvent(Session.ErrorCode ec) {
		this.ec = ec;
	}

	public Session.ErrorCode getEc() {
		return ec;
	}
}
