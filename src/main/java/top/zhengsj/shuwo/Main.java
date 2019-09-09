package top.zhengsj.shuwo;

import top.zhengsj.shuwo.timertask.AddesTimerTask;
import top.zhengsj.shuwo.timertask.AffirmTimerTask;
import top.zhengsj.shuwo.timertask.DateStatusTask;
import top.zhengsj.shuwo.timertask.ReleaseTimerTask;

import java.util.*;

public class Main {
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public static void main(String[] args) {
        System.out.println("Start Successfully!");

        Timer addTimer = new Timer();
        addTimer.schedule(new AddesTimerTask(), getTime(11, 52, 35), PERIOD_DAY);

        Timer affirmTimer = new Timer();
        affirmTimer.schedule(new AffirmTimerTask(), getTime(8, 10, 0), PERIOD_DAY);

        Timer releaseTimer = new Timer();
        releaseTimer.schedule(new ReleaseTimerTask(), getTime(21, 0, 0), PERIOD_DAY);

        Timer dateStatusTimer = new Timer();
        dateStatusTimer.schedule(new DateStatusTask(), getTime(20, 1, 0), PERIOD_DAY);
    }

    private static Date getTime(Integer hour, Integer minute, Integer second){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTime();
    }
}
