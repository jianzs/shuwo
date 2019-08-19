package top.zhengsj.shuwo.timertask;

import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.utils.ShuwoUtil;


public class DateStatusTask extends BaseTimerTask{
    @Override
    protected Runnable getThread(UserEntity user) {
        return () -> {
            if (!user.getEnabled()) return;
            ShuwoUtil shuwoUtil = new ShuwoUtil();
            try {
                shuwoUtil.login(user);
                shuwoUtil.getUserAppointmentInfo(user);

                System.out.println("Name -> ".concat(user.getName()));
                if (user.getOldSeatAppointmentId() != null)
                    System.out.println("Today -> ".concat(user.getOldSeatAppointmentId()));
                if (user.getNewSeatAppointmentId() != null)
                    System.out.println("Tomorrow -> ".concat(user.getNewSeatAppointmentId()));
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
