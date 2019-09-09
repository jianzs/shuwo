package top.zhengsj.shuwo.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import top.zhengsj.shuwo.contanst.SeatListConst;
import top.zhengsj.shuwo.exception.*;
import top.zhengsj.shuwo.pojo.UserEntity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ShuwoUtil {
    private final String LOGIN_URL = "http://t1.beijingzhangtu.com/api/user/loginByPhone.html";
    private final String ADDES_URL = "http://t1.beijingzhangtu.com/api/YySeatAppointment/addes.html";
    private final String AFFIRM_URL = "http://t1.beijingzhangtu.com/api/YySeatAppointment/affirmSeat.html";
    private final String RELEASE_URL = "http://t1.beijingzhangtu.com/api/YySeatAppointment/releaseBySelfes.html";
    private final String APPOINTMENT_URL = "http://t1.beijingzhangtu.com/api/YySeatAppointment/getUserAppointmentInfoes.html";
    private final String SEAT_LIST_URL = "http://t1.beijingzhangtu.com/api/seat/seatView.html";

    public void login(UserEntity user)
            throws IOException, IncompleteInfoException, LoginFailedException {
        String phone = user.getPhone();
        String password = user.getPassword();
        String deviceId = user.getDeviceId();
        if (phone == null || password == null) {
            throw new IncompleteInfoException(user.toString());
        }
        if (deviceId == null) {
            deviceId = UUIDUtil.getLowerRandomId().substring(0, 16);
            user.setDeviceId(deviceId);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("phone", phone);
        body.put("pass", password);
        body.put("deviceId", deviceId);
        body.put("deviceName", "HUAWEI-MHA-AL00");
        body.put("deviceVersion", "26");
        body.put("deviceType", "android");

        HttpRequest httpRequest = new HttpRequest();
        JSONObject response = httpRequest.sendPost(LOGIN_URL, body);
        Integer code = response.getInt("code");
        if (!code.equals(1)) {
            throw new LoginFailedException(user.toString() + "\n" + response);
        }

        String token = response.getJSONObject("data").getString("token");
        Integer userId = response.getJSONObject("data").getInt("id");
        user.setToken(token);
        user.setUserId(String.valueOf(userId));
    }

    /**
     * {
     *         appointmentStartTime: "05:40",
     *         appointmentEndTime: "06:40",
     *         appointmentDay: (new Date(targetTime)).format("yyyy-MM-dd"),
     *         seatid: 3935 + user.seatId,
     *         libraryid: '10000',
     *         userid: user.userId,
     *         token: user.token
     *     }
     */
    public void addes(UserEntity user, Date targetDate)
            throws IncompleteInfoException, NotLoginException, IOException, AddesFailedException, GetSeatIdException {
        Integer seatId = getSeatId(user);
        String userId = user.getUserId();
        String token = user.getToken();

        if (seatId == null) {
            throw new IncompleteInfoException(user.toString());
        }
        if (userId == null || token == null) {
            throw new NotLoginException(user.toString());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("appointmentStartTime", "05:40");
        body.put("appointmentEndTime", "06:40");
        body.put("appointmentDay", getFormatDate(targetDate));
        body.put("seatid", seatId);
        body.put("libraryid", "10000");
        body.put("userid", userId);
        body.put("token", token);

        HttpRequest httpRequest = new HttpRequest();
        JSONObject response = httpRequest.sendPost(ADDES_URL, body);
        Integer code = response.getInt("code");
        if (!code.equals(1)) {
            throw new AddesFailedException(user.toString() + "\n" + response);
        }

        String seatAppointmentId = String.valueOf(response.getJSONObject("data").getInt("keyid"));
        user.setOldSeatAppointmentId(user.getNewSeatAppointmentId());
        user.setNewSeatAppointmentId(seatAppointmentId);
    }

    /**
     * {
     *         ibeaconBatteryJson: '[{"remainingBattery":100,"serialNumber":"1918FC03DBE9"},{"remainingBattery":100,"serialNumber":"1918FC03DB99"}]',
     *         seatid: 3935 + user.seatId,
     *         userid: user.userId,
     *         libraryid: 10000,
     *         token: user.token,
     *         deviceId: user.deviceId
     *
     */
    public void affirm(UserEntity user)
            throws IncompleteInfoException, NotLoginException, IOException, AffirmFailedException, GetSeatIdException {
        Integer seatId = getSeatId(user);
        String userId = user.getUserId();
        String deviceId = user.getDeviceId();
        String token = user.getToken();

        if (seatId == null) {
            throw new IncompleteInfoException(user.toString());
        }
        if (userId == null || token == null) {
            throw  new NotLoginException(user.toString());
        }
        if (deviceId == null) {
            deviceId = UUIDUtil.getLowerRandomId().substring(0, 16);
            user.setDeviceId(deviceId);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ibeaconBatteryJson", "[{\"remainingBattery\":100,\"serialNumber\":\"1918FC03DBE9\"},{\"remainingBattery\":100,\"serialNumber\":\"1918FC03DB99\"}]");
        body.put("seatid", seatId);
        body.put("userid", userId);
        body.put("libraryid", "10000");
        body.put("token", token);
        body.put("deviceId", deviceId);

        HttpRequest httpRequest = new HttpRequest();
        JSONObject response = httpRequest.sendPost(AFFIRM_URL, body);

        Integer code = response.getInt("code");
        if (!code.equals(1)) {
            throw new AffirmFailedException(user.toString() + "\n" + response);
        }
    }

    /**
     *  {
     *                              [{"remainingBattery":100,"serialNumber":"1918FC03DB99"},{"remainingBattery":100,"serialNumber":"1918FC055369"}]
     *         ibeaconBatteryJson: '[{"remainingBattery":100,"serialNumber":"1918FC03DB99"},{"remainingBattery":100,"serialNumber":"1918FC055369"}]',
     *         seatAppointmentId: user.oldSeatAppointmentId,
     *         userid: user.userId,
     *         libraryid: 10000,
     *         deviceId: user.deviceId,
     *         token: user.token
     *     }
     */
    public void release(UserEntity user)
            throws NotLoginException, DeviceIdLostException, IOException, ReleaseFailedException, NoAppointmentInfoException, ParseException, GetAppointmentInfoFailedException {
        String seatAppointmentId = user.getOldSeatAppointmentId();

        String userId = user.getUserId();
        String deviceId = user.getDeviceId();
        String token = user.getToken();

        if (seatAppointmentId == null) {
            throw new NoAppointmentInfoException(user.toString());
        }
        if (userId == null || token == null) {
            throw new NotLoginException(user.toString());
        }
        if (deviceId == null) {
            throw new DeviceIdLostException(user.toString());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ibeaconBatteryJson", "[{\"remainingBattery\":100,\"serialNumber\":\"1918FC03DB99\"},{\"remainingBattery\":100,\"serialNumber\":\"1918FC055369\"}]");
        body.put("userid", userId);
        body.put("libraryid", "10000");
        body.put("token", token);
        body.put("deviceId", deviceId);
        body.put("seatAppointmentId", seatAppointmentId);

        HttpRequest httpRequest = new HttpRequest();
        JSONObject response = httpRequest.sendPost(RELEASE_URL, body);

        Integer code = response.getInt("code");
        if (!code.equals(1)) {
            throw new ReleaseFailedException(user.toString() + "\n" + response);
        }
    }

    public void getUserAppointmentInfo(UserEntity user)
            throws NotLoginException, IOException, GetAppointmentInfoFailedException, ParseException {
        String userId = user.getUserId();
        String token = user.getToken();
        if (userId == null || token == null) {
            throw  new NotLoginException(user.toString());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("libraryid", "10000");
        body.put("userid", userId);
        body.put("token", token);

        HttpRequest httpRequest = new HttpRequest();
        JSONObject response = httpRequest.sendPost(APPOINTMENT_URL, body);

        Integer code = response.getInt("code");
        if (!code.equals(1)) {
            throw new GetAppointmentInfoFailedException(user.toString() + "\n" + response);
        }

        user.setOldSeatAppointmentId(null);
        user.setNewSeatAppointmentId(null);

        JSONArray data = response.getJSONArray("data");
        for (int i = 0, len = data.length(); i < len; i++) {

            if (!data.getJSONObject(i).isNull("signTime")) {
                user.setOldSeatAppointmentId(String.valueOf(data.getJSONObject(i).getInt("keyid")));
            } else {
                user.setNewSeatAppointmentId(String.valueOf(data.getJSONObject(i).getInt("keyid")));
            }

//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = dateFormat.parse(data.getJSONObject(i).getString("appointmentDay"));
//            if (date.before(new Date())) {
//                user.setOldSeatAppointmentId(String.valueOf(data.getJSONObject(i).getInt("keyid")));
//            } else {
//                user.setNewSeatAppointmentId(String.valueOf(data.getJSONObject(i).getInt("keyid")));
//            }
        }
    }


    private Integer getSeatId(UserEntity user)
            throws NotLoginException, IOException, GetSeatIdException {
        JSONArray seatList = SeatListConst.getSeatList(user.getRoomId());
        if (seatList == null) {
            String userId = user.getUserId();
            String token = user.getToken();
            if (userId == null || token == null) {
                throw  new NotLoginException(user.toString());
            }

            Map<String, Object> params = new HashMap<>();
            params.put("todayOrTomorrow", "tomorrow");
            params.put("libraryId", "10000");
            params.put("roomId", user.getRoomId());
            params.put("token", token);

            HttpRequest httpRequest = new HttpRequest();
            JSONObject response = httpRequest.sendGet(SEAT_LIST_URL, params);

            Integer code = response.getInt("code");
            if (!code.equals(1)) {
                throw new GetSeatIdException(user.toString() + "\n" + response);
            }

            JSONObject data = response.getJSONObject("data");
            seatList = data.getJSONArray("seatList");

            SeatListConst.setSeatList(user.getRoomId(), seatList);
        }

        for (int i = 0, len = seatList.length(); i < len; i++) {
            JSONObject seat = seatList.getJSONObject(i);
            if (Integer.valueOf(seat.getString("number")).equals(user.getSeatId())) {
                return seat.getInt("keyid");
            }
        }
        return -1;
    }

    private String getFormatDate(Date targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}
