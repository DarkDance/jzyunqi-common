package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomStringUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.aliyun.oss.common.utils.CodingUtils.assertTrue;
import static com.aliyun.oss.internal.RequestParameters.PART_NUMBER;
import static com.aliyun.oss.internal.RequestParameters.POSITION;
import static com.aliyun.oss.internal.RequestParameters.SECURITY_TOKEN;
import static com.aliyun.oss.internal.RequestParameters.STYLE_NAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_APPEND;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_BUCKET_INFO;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_CNAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_COMP;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_CORS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_DELETE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_END_TIME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_IMG;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIFECYCLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIVE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_OBJECTMETA;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_PROCESS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_PROCESS_CONF;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_QOS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REFERER;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION_PROGRESS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_RESTORE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_START_TIME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STAT;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STATUS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STYLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_SYMLINK;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_TAGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF_APPLICATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF_IMAGE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF_IMAGE_DESC;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF_LOG;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UDF_NAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UPLOADS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_VOD;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_WEBSITE;
import static com.aliyun.oss.internal.RequestParameters.UPLOAD_ID;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES;

/**
 * @author wiiyaya
 * @date 2018/5/23.
 */
@Slf4j
public class AliOssHelper extends AliBaseHelper {

    private static final List<String> SIGNED_PARAMTERS = Arrays.asList(SUBRESOURCE_ACL,
            SUBRESOURCE_UPLOADS, SUBRESOURCE_LOCATION, SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE,
            SUBRESOURCE_REFERER, SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE, SUBRESOURCE_APPEND, SUBRESOURCE_TAGGING,
            SUBRESOURCE_OBJECTMETA, UPLOAD_ID, PART_NUMBER, SECURITY_TOKEN, POSITION, RESPONSE_HEADER_CACHE_CONTROL,
            RESPONSE_HEADER_CONTENT_DISPOSITION, RESPONSE_HEADER_CONTENT_ENCODING, RESPONSE_HEADER_CONTENT_LANGUAGE,
            RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_EXPIRES, SUBRESOURCE_IMG, SUBRESOURCE_STYLE, STYLE_NAME,
            SUBRESOURCE_REPLICATION, SUBRESOURCE_REPLICATION_PROGRESS, SUBRESOURCE_REPLICATION_LOCATION,
            SUBRESOURCE_CNAME, SUBRESOURCE_BUCKET_INFO, SUBRESOURCE_COMP, SUBRESOURCE_QOS, SUBRESOURCE_LIVE,
            SUBRESOURCE_STATUS, SUBRESOURCE_VOD, SUBRESOURCE_START_TIME, SUBRESOURCE_END_TIME, SUBRESOURCE_PROCESS,
            SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_SYMLINK, SUBRESOURCE_STAT, SUBRESOURCE_UDF, SUBRESOURCE_UDF_NAME,
            SUBRESOURCE_UDF_IMAGE, SUBRESOURCE_UDF_IMAGE_DESC, SUBRESOURCE_UDF_APPLICATION, SUBRESOURCE_UDF_LOG,
            SUBRESOURCE_RESTORE);

    private static final String OSS_UPLOAD_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";

    private String accessAccount;

    private String accessSecret;

    private String stsToken;

    private AliOssHelper(String accessAccount, String accessSecret, String stsToken, RestTemplate restTemplate) {
        super(accessAccount, accessSecret, restTemplate);
        this.accessAccount = accessAccount;
        this.accessSecret = accessSecret;
        this.stsToken = stsToken;
    }

    public static AliOssHelper of(String accessKeyId, String accessKeySecret, String stsToken) {
        return new AliOssHelper(accessKeyId, accessKeySecret, stsToken, null);
    }

    /**
     * 转存文件
     *
     * @param uid    当前用户uid
     * @param url    文件名称
     * @param bucket 空间
     * @return 转存后文件名
     */
    public String fetch(String uid, String url, String bucket) throws BusinessException {
        try {
            OSSClient ossClient = new OSSClient(OSS_UPLOAD_ENDPOINT, new DefaultCredentialProvider(accessAccount, accessSecret, stsToken), null);
            String key = DigestUtilPlus.MD5.sign(StringUtilPlus.join(uid, "-", System.currentTimeMillis(), "-", RandomStringUtilPlus.random(32, true, true))) + ".jpg";
            InputStream inputStream = new URL(url).openStream();
            ossClient.putObject(bucket, key, inputStream);
            ossClient.shutdown();
            return key;
        } catch (Exception e) {
            log.error("AliOssHelper.fetch error", e);
            throw new BusinessException("common_error_ali_oss_url_fetch_failed");
        }
    }

    public String buildCanonicalString(String method, String resourcePath, Map<String, String> headers, Map<String, String> parameters) throws Exception {

        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(method).append("\n");

        TreeMap<String, String> headersToSign = new TreeMap<>();

        headers.forEach((key, value) -> {
            String lowerKey = key.toLowerCase();
            if (lowerKey.equals("content-type")
                    || lowerKey.equals("content-md5")
                    || lowerKey.equals("date")
                    || lowerKey.startsWith("x-oss-")) {
                headersToSign.put(lowerKey, value.trim());
            }
        });

        if (!headersToSign.containsKey("content-type")) {
            headersToSign.put("content-type", "");
        }
        if (!headersToSign.containsKey("content-md5")) {
            headersToSign.put("content-md5", "");
        }

        headersToSign.forEach((key, value) -> {
            if (key.startsWith("x-oss-")) {
                canonicalString.append(key).append(':').append(value);
            } else {
                canonicalString.append(value);
            }
            canonicalString.append("\n");
        });

        canonicalString.append(buildCanonicalizedResource(resourcePath, parameters));

        String signS = canonicalString.toString();
        System.out.println(signS);
        return ossSign(signS);
    }

    private static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {

        assertTrue(resourcePath.startsWith("/"), "Resource path should start with slash character");

        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);

        String paramList = Optional.of(parameters).orElse(new HashMap<>()).entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .filter(entry -> SIGNED_PARAMTERS.contains(entry.getKey()) && entry.getValue() != null)
                    .map(entry -> StringUtilPlus.join(entry.getKey(), "=", entry.getValue()))
                    .collect(Collectors.joining("&"));
        if(StringUtilPlus.isNotBlank(paramList)){
            builder.append("?");
            builder.append(paramList);
        }
        return builder.toString();
    }
}
