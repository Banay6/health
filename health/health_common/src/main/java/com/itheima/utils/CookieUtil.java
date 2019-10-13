package com.itheima.utils;

import javax.servlet.http.Cookie;

public class CookieUtil {
    /**
     * 创建并配置Cookie
     *
     * @param name  cookie的name
     * @param value cookie的value
     * @param time  有效期，秒
     * @param path  有效范围
     * @return
     */
    public static Cookie createCookie(String name, String value, int time, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(time);
        cookie.setPath(path);
        return cookie;
    }

    public static String getCookieValue(Cookie[] cookies, String cookieName) {
        String value = "";
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    value = cookie.getValue();
                }
            }
        }
        return value;
    }
}
