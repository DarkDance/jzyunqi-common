package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.IOUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @date 2018/5/23.
 */
@Slf4j
public class AliOssHelper extends AliBaseHelper {
    private static final List<String> SIGNED_PARAMS = Arrays.asList(
            "acl", "uploads" //子资源标识，还有很多，暂未添加
            , "response-content-type", "response-content-language" //指定返回Header字段，还有很多，暂未添加
            , "x-oss-process" //文件处理方式
    );

    private static final String OSS_UPLOAD_ENDPOINT = "http://%s.oss-cn-hangzhou.aliyuncs.com/%s";

    private String accessAccount;

    private String stsToken;

    public AliOssHelper(String accessAccount, String accessSecret, String stsToken, RestTemplate restTemplate) {
        super(accessAccount, accessSecret, restTemplate);
        this.accessAccount = accessAccount;
        this.stsToken = stsToken;
    }

    /**
     * 转存文件
     *
     * @param url    文件url
     * @param bucket 空间
     * @param fileName  文件名称
     */
    public void fetch(String url, String bucket, String fileName) throws BusinessException {
        try {
            URI sendMsgUri = new URIBuilder(String.format(OSS_UPLOAD_ENDPOINT, bucket, fileName)).build();

            String date = LocalDateTime.now(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH));
            String contentType = "image/jpeg";
            byte[] content = IOUtilPlus.toByteArray(new URL(url));
            String contentMd5 = DigestUtilPlus.Base64.encodeBase64String(DigestUtilPlus.MD5.sign(content));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", contentType);
            headers.set("Content-Length", String.valueOf(content.length));
            headers.set("Content-Md5", contentMd5);
            headers.set("Date", date);
            headers.set("X-OSS-Security-Token", stsToken);

            String sign = ossSign(prepareWaitSign(bucket, fileName, date, contentMd5, contentType, headers, null));
            headers.set("Authorization", "OSS " + accessAccount + ":" + sign);

            RequestEntity<byte[]> requestEntity = new RequestEntity<>(content, headers, HttpMethod.PUT, sendMsgUri);
            getRestTemplate().exchange(requestEntity, Object.class);
        } catch (Exception e) {
            log.error("AliOssHelper.fetch error", e);
            throw new BusinessException("common_error_ali_oss_url_fetch_failed");
        }
    }

    private String prepareWaitSign(String bucket, String fileName, String date, String contentMd5, String contentType, HttpHeaders headers, Map<String, String> params) {
        return String.valueOf(HttpMethod.PUT) + "\n" +
                contentMd5 + "\n" +
                contentType + "\n" +
                date + "\n" +
                prepareHeaders(headers, "x-oss-") +
                prepareResource(bucket, fileName, params);
    }


    private String prepareHeaders(HttpHeaders headers, String prefix) {
        return headers.entrySet().stream()
                .filter(entry -> StringUtilPlus.startsWithIgnoreCase(entry.getKey(), prefix))
                .map(entry -> StringUtilPlus.join(StringUtilPlus.lowerCase(entry.getKey()), ":", StringUtilPlus.join(entry.getValue().toArray()), "\n"))
                .sorted(String::compareTo)
                .collect(Collectors.joining(""));
    }

    private String prepareResource(String bucket, String fileName, Map<String,String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("/").append(bucket).append("/").append(fileName);

        String paramList = Optional.ofNullable(params).orElse(new HashMap<>()).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(entry -> SIGNED_PARAMS.contains(entry.getKey()) && entry.getValue() != null)
                .map(entry -> StringUtilPlus.join(entry.getKey(), "=", entry.getValue()))
                .collect(Collectors.joining("&"));
        if(StringUtilPlus.isNotBlank(paramList)){
            builder.append("?");
            builder.append(paramList);
        }
        return builder.toString();
    }
}
