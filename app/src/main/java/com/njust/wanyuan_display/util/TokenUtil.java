package com.njust.wanyuan_display.util;
/**
 * 生成token令牌工具类
 */

public class TokenUtil {
    public String token(String machineId, String time, String type, String gran) {
        int result = 17;
        result = 31 * result + machineId.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + gran.hashCode();
        return String.valueOf(result);
    }
}
