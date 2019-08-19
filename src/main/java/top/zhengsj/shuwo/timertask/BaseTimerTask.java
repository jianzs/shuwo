package top.zhengsj.shuwo.timertask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.pojo.UserList;
import top.zhengsj.shuwo.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public abstract class BaseTimerTask extends TimerTask {
    private Logger logger = LogManager.getLogger();

    private static final String USER_XML_PATH = "src/main/resources/users.xml";

    @Override
    public void run() {
        UserList userList = (UserList) XMLUtil.convertXmlFileToObject(UserList.class, USER_XML_PATH);
        List<UserEntity> users = userList.getUsers();

        List<Thread> threads = new ArrayList<>();
        for (UserEntity user: users) {
            Thread thread = new Thread(getThread(user));
            thread.start();
            threads.add(thread);
        }

        while (true) {
            boolean ended = true;
            for (Thread thread : threads) {
                if (thread.isAlive()) ended = false;
            }
            if (ended) break;
            else {
                Thread.yield();
            }
        }

        userList = new UserList(users);
        XMLUtil.convertToXml(userList, USER_XML_PATH);
    }

    protected abstract Runnable getThread(UserEntity user);
}
