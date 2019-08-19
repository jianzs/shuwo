package top.zhengsj.shuwo.timertask;

import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.thread.AddesThread;

public class AddesTimerTask extends BaseTimerTask {
    protected Runnable getThread(UserEntity user) {
        return new AddesThread(user);
    };
}
