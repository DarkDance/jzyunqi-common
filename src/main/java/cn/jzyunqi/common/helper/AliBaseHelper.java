package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomStringUtilPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Slf4j
public abstract class AliBaseHelper {

    private String accessKeyId;

    private String accessKeySecret;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    protected AliBaseHelper(String accessKeyId, String accessKeySecret, RestTemplate restTemplate) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.restTemplate = restTemplate;
    }

    protected AliBaseHelper(String accessKeyId, String accessKeySecret, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 构建rest请求系统参数
     *
     * @return TreeMap
     */
    TreeMap<String, String> getPublicParamMap() {
        TreeMap<String, String> params = new TreeMap<>();
        // 1. 填充系统参数
        params.put("SignatureMethod", "HMAC-SHA1"); //签名算法
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", RandomStringUtilPlus.random(32, true, true)); //用于请求的防重放攻击
        params.put("AccessKeyId", accessKeyId); //子用户accessKey
        params.put("Timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(LocalDateTime.now(ZoneId.of("GMT+0"))));// 这里一定要设置GMT时区
        params.put("Format", "JSON"); //没传默认为JSON，可选填值：XML

        return params;
    }

    /**
     * 使用POP协议签名请求参数
     *
     * @param params 请求参数
     * @return 签名后请求参数
     * @throws Exception 异常
     */
    String generateParamPopSign(TreeMap<String, String> params) throws Exception {
        StringBuilder sortQueryStringTmp = new StringBuilder();
        params.forEach((key, value) -> {
            try {
                sortQueryStringTmp.append("&").append(this.specialUrlEncode(key)).append("=").append(this.specialUrlEncode(value));
            } catch (Exception e) {
                log.error("generateParamPopSign URLEncoder error", e);
            }
        });
        String sign = this.popSign(this.specialUrlEncode(sortQueryStringTmp.substring(1))); //签名字符串
        return "?Signature=" + this.specialUrlEncode(sign) + sortQueryStringTmp;
    }

    /**
     * 签名
     *
     * @param stringToSign 需要签名的字符串
     * @return 签名后的字符串
     * @throws Exception 异常
     */
    String ossSign(String stringToSign) throws Exception {
        return DigestUtilPlus.HmacSHA1.sign(stringToSign, accessKeySecret);
    }

    /**
     * 使用POP协议签名
     *
     * @param stringToSign 需要签名的字符串
     * @return 签名后的字符串
     * @throws Exception 异常
     */
    private String popSign(String stringToSign) throws Exception {
        return DigestUtilPlus.HmacSHA1.sign("GET&%2F&" + stringToSign, accessKeySecret + "&");
    }

    /**
     * 替换特殊URL编码，加号（+）替换成 %20、星号（*）替换成 %2A、%7E 替换回波浪号（~）
     *
     * @param value 原始字符串
     * @return 替换后的字符串
     * @throws Exception 异常
     */
    private String specialUrlEncode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}
