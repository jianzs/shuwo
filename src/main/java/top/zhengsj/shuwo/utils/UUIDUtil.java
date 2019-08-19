package top.zhengsj.shuwo.utils;

import java.util.UUID;

public class UUIDUtil {
	public static String getLowerRandomId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

	public static String getRandomId() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

	public static String getRandomCode() {
		return getRandomId();
	}

	public static String getRandomCodeLength(Integer len) {
		return getRandomCode().substring(0, len);
	}
}
