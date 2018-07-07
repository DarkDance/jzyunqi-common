package cn.jzyunqi.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class MapUtilPlus {
    private MapUtilPlus() {

    }

    /**
     * 判断指定键-值映射是否为空
     *
     * @param map 键-值映射
     * @return true 为空， false 不为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return org.apache.commons.collections4.MapUtils.isEmpty(map);
    }

    /**
     * 判断指定键-值映射是否不为空
     *
     * @param map 键-值映射
     * @return true 为空， false 不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return org.apache.commons.collections4.MapUtils.isNotEmpty(map);
    }

    /**
     * 把map转换成url请求参数
     *
     * @param params 待组装参数
     * @return 组装后结果
     */
    public static String getUrlParamForSign(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        String urlParamStr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                urlParamStr = urlParamStr + key + "=" + value;
            } else {
                urlParamStr = urlParamStr + key + "=" + value + "&";
            }
        }
        return urlParamStr;
    }

    /**
     * 把map转换成url请求参数
     *
     * @param params 待组装参数
     * @return 组装后结果
     */
    public static String getUrlParam(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder urlParamStr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            try {
                value = StringUtilPlus.isEmpty(value) ? "" : URLEncoder.encode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                value = "";
            }

            urlParamStr.append(key);
            urlParamStr.append("=");
            urlParamStr.append(value);
            if (i != keys.size() - 1) {// 拼接时，不包括最后一个&字符
                urlParamStr.append("&");
            }
        }
        return urlParamStr.toString();
    }
}
