package top.zhengsj.shuwo.contanst;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class SeatListConst {
    private static Map<Integer, JSONArray> instance = new HashMap<>();

    public static JSONArray getSeatList(Integer roomId) {
        return instance.get(roomId);
    }

    public static void setSeatList(Integer roomId, JSONArray seatList) {
        instance.put(roomId, seatList);
    }
}
