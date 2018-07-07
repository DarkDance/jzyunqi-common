package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.ali.response.SendSmsRsp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.TreeMap;

/**
 * @author wiiyaya
 * @date 2018/5/21.
 */
@Slf4j
public class AliSmsHelper extends AliBaseHelper {

    private static final String SMS_ENDPOINT = "http://dysmsapi.aliyuncs.com/";

    public AliSmsHelper(String smsAccessKeyId, String smsAccessKeySecret, RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(smsAccessKeyId, smsAccessKeySecret, restTemplate, objectMapper);
    }

    /**
     * 发送短信
     *
     * @param smsSign       短信签名
     * @param phone         短信接收号码,支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
     * @param templateCode  短信模板ID
     * @param templateParam 短信模板变量替换JSON串
     * @throws BusinessException 异常
     */
    public void sendSms(String smsSign, String phone, String templateCode, Object templateParam) throws BusinessException {
        SendSmsRsp body;
        try {
            //1. 构建系统参数
            TreeMap<String, String> params = super.getPublicParamMap();

            //2. 填充业务参数
            params.put("Action", "SendSms"); //API的命名
            params.put("Version", "2017-05-25"); //API的版本
            params.put("RegionId", "cn-hangzhou"); //API支持的RegionID
            params.put("SignName", smsSign); //短信签名
            params.put("PhoneNumbers", phone); //短信接收号码,支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
            params.put("TemplateCode", templateCode); //短信模板ID
            params.put("TemplateParam", super.getObjectMapper().writeValueAsString(templateParam)); //短信模板变量替换JSON串

            //3. 构造请求url
            String url = SMS_ENDPOINT + super.generateParamPopSign(params);
            RequestEntity<Object> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(url));
            ResponseEntity<SendSmsRsp> aliBaseRsp = getRestTemplate().exchange(requestEntity, SendSmsRsp.class);
            body = aliBaseRsp.getBody();
        } catch (Exception e) {
            log.error("AliSmsHelper.sendSms error", e);
            throw new BusinessException("common_error_ali_sms_send_error");
        }

        if (body == null || !"OK".equals(body.getCode())) {
            if (body == null) {
                body = new SendSmsRsp();
            }
            log.error("AliSmsHelper.sendSms error[{}][{}]", body.getCode(), body.getMessage());
            throw new BusinessException("common_error_ali_sms_send_failed");
        }
    }
}
