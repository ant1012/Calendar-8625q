package edu.bupt.calendar.alerts;

import edu.bupt.calendar.alerts.AlertService.NotificationWrapper;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，未作修改
 * 
 * */

public interface NotificationMgr {
    public void cancel(int id);
    public void cancel(String tag, int id);
    public void cancelAll();
    public void notify(int id, NotificationWrapper notification);
    public void notify(String tag, int id, NotificationWrapper notification);
}
