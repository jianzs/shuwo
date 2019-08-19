package top.zhengsj.shuwo.timertask;

import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.thread.AffirmThread;

public class AffirmTimerTask extends BaseTimerTask {
    protected Runnable getThread(UserEntity user) {
        return new AffirmThread(user);
    };
}
