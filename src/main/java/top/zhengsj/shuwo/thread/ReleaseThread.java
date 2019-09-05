package top.zhengsj.shuwo.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.zhengsj.shuwo.exception.*;
import top.zhengsj.shuwo.pojo.UserEntity;
import top.zhengsj.shuwo.utils.ShuwoUtil;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class ReleaseThread implements Runnable {
    private Logger logger = LogManager.getLogger();
    private UserEntity user;

    public ReleaseThread(UserEntity user) {
        this.user = user;
    }

    @Override
    public void run() {
        if (user == null || !user.getEnabled() || !user.getReleaseEnabled()) return;
        System.out.println(new Date().toString());

        ShuwoUtil shuwoUtil = new ShuwoUtil();

        int failedCnt = 0;
        boolean succeed = false;
        boolean failed = false;
        boolean needRelogin = false;
        boolean needAppointmentInfo = false;
        while (failedCnt < 3 && !succeed && !failed) {
            try {
                if (needRelogin) shuwoUtil.login(user);
//                if (needAppointmentInfo)
                shuwoUtil.getUserAppointmentInfo(user);
//                needAppointmentInfo = false;
                needRelogin = false;

                shuwoUtil.release(user);
                succeed = true;

                logger.error(user.getName() + " release successfully.");
            } catch (IncompleteInfoException e) {
                failed = true;
                logger.error(e);
            } catch (NotLoginException e) {
                logger.warn(e);
                failedCnt++;
                needRelogin = true;
            } catch (IOException | GetAppointmentInfoFailedException e) {
                logger.error(e);
                failedCnt++;
                needRelogin = true;
            } catch (LoginFailedException | ReleaseFailedException e) {
                logger.error(e);
                failedCnt++;
                needRelogin = true;
                needAppointmentInfo = true;
            } catch (DeviceIdLostException | ParseException e) {
                logger.error(e);
                failed = true;
            } catch (NoAppointmentInfoException e) {
                logger.error(e);
                failedCnt++;
                needAppointmentInfo = true;
            }
        }
    }
}
