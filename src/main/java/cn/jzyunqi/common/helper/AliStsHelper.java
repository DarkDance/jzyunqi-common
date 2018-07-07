package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.ali.response.AssumeRoleRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.TreeMap;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Slf4j
public class AliStsHelper extends AliBaseHelper {

    private static final String OSS_ENDPOINT = "https://sts.aliyuncs.com/";

    private String durationSeconds;

    public AliStsHelper(String accessKeyId, String accessKeySecret, String durationSeconds, RestTemplate restTemplate) {
        super(accessKeyId, accessKeySecret, restTemplate);
        this.durationSeconds = durationSeconds;
    }

    public AssumeRoleRsp generateAssumeRole(String roleArn, String roleSessionName) throws BusinessException {
        AssumeRoleRsp body;
        try {
            //1. 构建系统参数
            TreeMap<String, String> params = getPublicParamMap();

            //2. 填充业务参数
            params.put("Action", "AssumeRole"); //API的命名
            params.put("Version", "2015-04-01"); //API的版本
            params.put("RoleArn", roleArn); //角色的全局资源描述符
            params.put("RoleSessionName", roleSessionName); //用户自定义参数。此参数用来区分不同的Token，可用于用户级别的访问审计。
            params.put("DurationSeconds", durationSeconds); //指定的过期时间，单位为秒。过期时间范围：900 ~ 3600，默认值为3600。
            //params.put("Policy", "XXX");

            //3. 构造请求url
            String url = OSS_ENDPOINT + generateParamPopSign(params);
            RequestEntity<Object> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(url));
            ResponseEntity<AssumeRoleRsp> aliBaseRsp = getRestTemplate().exchange(requestEntity, AssumeRoleRsp.class);
            body = aliBaseRsp.getBody();
        } catch (Exception e) {
            log.error("AliStsHelper.generateAssumeRole error", e);
            throw new BusinessException("common_error_ali_sts_gen_assume_role_error");
        }

        if (body == null || body.getCode() != null) {
            if (body == null) {
                body = new AssumeRoleRsp();
            }
            log.error("AliStsHelper.generateAssumeRole error[{}][{}]", body.getCode(), body.getMessage());
            throw new BusinessException("common_error_ali_sts_gen_assume_role_failed");
        } else {
            return body;
        }
    }
}
