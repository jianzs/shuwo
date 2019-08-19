package top.zhengsj.shuwo.timertask;

import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.thread.ReleaseThread;

public class ReleaseTimerTask extends BaseTimerTask {
    protected Runnable getThread(UserEntity user) {
        return new ReleaseThread(user);
    };
}
