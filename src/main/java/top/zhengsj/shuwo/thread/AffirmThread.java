package top.zhengsj.shuwo.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.zhengsj.shuwo.exception.*;
import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.utils.ShuwoUtil;

import java.io.IOException;
import java.util.Date;

public class AffirmThread implements Runnable {
    private Logger logger = LogManager.getLogger();
    private UserEntity user;

    public AffirmThread(UserEntity user) {
        this.user = user;
    }

    @Override
    public void run() {
        if (user == null || !user.getEnabled() || !user.getAffirmEnabled()) return;
        System.out.println(new Date().toString());


        ShuwoUtil shuwoUtil = new ShuwoUtil();

        int failedCnt = 0;
        boolean succeed = false;
        boolean failed = false;
        boolean needRelogin = false;
        while (failedCnt < 3 && !succeed && !failed) {
            try {
                if (needRelogin) shuwoUtil.login(user);
                needRelogin = false;

                shuwoUtil.affirm(user);
                succeed = true;

                logger.error(user.getName() + " affirm successfully.");
            } catch (IncompleteInfoException e) {
                failed = true;
                logger.error(e);
            } catch (NotLoginException e) {
                logger.warn(e);
                failedCnt++;
                needRelogin = true;
            } catch (IOException e) {
                logger.error(e);
                failedCnt++;
            } catch (LoginFailedException | AffirmFailedException e) {
                logger.error(e);
                failedCnt++;
                needRelogin = true;
            }
        }
    }
}
