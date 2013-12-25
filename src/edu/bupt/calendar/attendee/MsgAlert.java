package edu.bupt.calendar.attendee;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 短信提醒类
 * 
 * (功能9)
 * 
 * */

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