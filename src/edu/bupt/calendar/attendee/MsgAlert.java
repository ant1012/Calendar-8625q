package edu.bupt.calendar.attendee;

public class MsgAlert {
    public int _id;
    public long event_id;
    public long alert_time;

    public MsgAlert() {
    }

    public MsgAlert(long event_id, long alert_time) {
        this.event_id = event_id;
        this.alert_time = alert_time;
    }

}