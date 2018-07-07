package cn.jzyunqi.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class NetworkUtilPlus {
    private NetworkUtilPlus() {

    }

    /**
     * 获取客户端IP地址
     *
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) { // "***.***.***.***".length()
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获取客户端类型
     *
     * @param request HttpServletRequest
     * @return 客户端类型
     */
    public static String getUserClientType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        String clientType = "H5";
        if (userAgent.contains("iPhone")) {
            clientType = "IOS";
        } else if (userAgent.contains("Android")) {
            clientType = "ANDROID";
        }
        return clientType;
    }
}
