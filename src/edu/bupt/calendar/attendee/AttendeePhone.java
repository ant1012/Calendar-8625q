package edu.bupt.calendar.attendee;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 日程参与者类
 * 
 * (功能8)
 * 
 * */

public class AttendeePhone {
    public int _id;
    public long event_id;
    public String name;
    public String phoneNumber;

    public AttendeePhone() {
    }

    public AttendeePhone(long event_id, String name, String phoneNumber) {
        this.event_id = event_id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
