package me.zchang.onchart.session.events;

/**
 * Created by langley on 2/7/16.
 */
public class SessionStartOverEvent {
    private String target;

    public SessionStartOverEvent(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
}
