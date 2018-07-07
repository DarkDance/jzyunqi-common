package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.weixin.InterfaceTokenResult;
import cn.jzyunqi.common.model.weixin.JsApiTicketResult;
import cn.jzyunqi.common.model.weixin.response.InterfaceTokenRsp;
import cn.jzyunqi.common.model.weixin.response.JsApiTicketRsp;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomStringUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/29.
 */
@Slf4j
public class WeixinCgiHelper {

    /**
     * 获取access_token
     */
    private static final String WX_INTERFACE_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获得jsapi_ticket
     */
    private static final String WX_JS_API_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    /**
     * 需要签名的字符串
     */
    private static final String WX_JS_API_TICKET_SIGN = "jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s";

    /**
     * access_token缓存key
     */
    private static final String WX_INTERFACE_TOKEN_KEY = "INTERFACE_TOKEN";

    private static final String WX_JS_API_TICKET_KEY = "JS_API_TICKET";

    /**
     * 第三方用户唯一凭证
     */
    private String appId;

    /**
     * 第三方用户唯一凭证密钥
     */
    private String appSecret;

    private RestTemplate restTemplate;

    private RedisHelper redisHelper;

    public WeixinCgiHelper(String appId, String appSecret, RestTemplate restTemplate, RedisHelper redisHelper) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.restTemplate = restTemplate;
        this.redisHelper = redisHelper;
    }

    /**
     * 获取access_token
     *
     * @param cache 缓存
     * @return 获取access_token
     * @throws BusinessException
     */
    public String getInterfaceToken(Cache cache) throws BusinessException {
        InterfaceTokenResult tokenResult = (InterfaceTokenResult) redisHelper.vGet(cache, WX_INTERFACE_TOKEN_KEY);
        if (tokenResult != null && LocalDateTime.now().isBefore(tokenResult.getExpireTime())) {
            return tokenResult.getToken();
        }

        InterfaceTokenRsp interfaceTokenRsp;
        try {
            URI accessTokenUri = new URIBuilder(String.format(WX_INTERFACE_TOKEN_URL, appId, appSecret)).build();

            RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(HttpMethod.GET, accessTokenUri);
            ResponseEntity<InterfaceTokenRsp> weixinUserRsp = restTemplate.exchange(requestEntity, InterfaceTokenRsp.class);
            interfaceTokenRsp = weixinUserRsp.getBody();
        } catch (Exception e) {
            log.error("WeixinCgiHelper getInterfaceToken other error:", e);
            throw new BusinessException("common_error_wx_get_interface_token_error");
        }

        //微信不管成功还是失败，返回的都是200，需要通过额外的字段来判断是否真的成功
        if (interfaceTokenRsp != null && StringUtilPlus.isEmpty(interfaceTokenRsp.getErrorCode())) {
            //把token放入缓存
            tokenResult = new InterfaceTokenResult();
            tokenResult.setToken(interfaceTokenRsp.getAccessToken()); //获取到的凭证
            tokenResult.setExpireTime(LocalDateTime.now().plusSeconds(interfaceTokenRsp.getExpiresIn()).minusSeconds(120)); //凭证有效时间，单位：秒
            redisHelper.vPut(cache, WX_INTERFACE_TOKEN_KEY, tokenResult);

            return interfaceTokenRsp.getAccessToken();
        } else {
            if (interfaceTokenRsp == null) {
                interfaceTokenRsp = new InterfaceTokenRsp();
            }
            log.error("WeixinCgiHelper getInterfaceToken 200 error[{}][{}]", interfaceTokenRsp.getErrorCode(), interfaceTokenRsp.getErrorMsg());
            throw new BusinessException("common_error_wx_get_interface_token_failed");
        }
    }

    /**
     * 获得jsapi_ticket权限签名
     *
     * @param cache 缓存
     * @param url   需要签名的url
     * @return 签名结果
     * @throws BusinessException
     */
    public Map<String, Object> getJsApiTicket(Cache cache, String url) throws BusinessException {

        JsApiTicketResult ticketResult = (JsApiTicketResult) redisHelper.vGet(cache, WX_JS_API_TICKET_KEY);
        if (ticketResult != null && LocalDateTime.now().isBefore(ticketResult.getExpireTime())) {
            return this.ticketSign(ticketResult.getTicket(), url);
        }

        JsApiTicketRsp jsApiTicketRsp;
        try {
            URI accessTokenUri = new URIBuilder(String.format(WX_JS_API_TICKET_URL, this.getInterfaceToken(cache))).build();

            RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(HttpMethod.GET, accessTokenUri);
            ResponseEntity<JsApiTicketRsp> weixinUserRsp = restTemplate.exchange(requestEntity, JsApiTicketRsp.class);
            jsApiTicketRsp = weixinUserRsp.getBody();
        } catch (Exception e) {
            log.error("WeixinCgiHelper getJsApiTicket other error:", e);
            throw new BusinessException("common_error_wx_get_jsapi_ticket_error");
        }
        //微信不管成功还是失败，返回的都是200，需要通过额外的字段来判断是否真的成功
        if (jsApiTicketRsp != null && "0".equals(jsApiTicketRsp.getErrorCode())) {
            //把token放入缓存
            ticketResult = new JsApiTicketResult();
            ticketResult.setTicket(jsApiTicketRsp.getTicket()); //授权ticket
            ticketResult.setExpireTime(LocalDateTime.now().plusSeconds(jsApiTicketRsp.getExpiresIn()).minusSeconds(120)); //过期时间
            redisHelper.vPut(cache, WX_JS_API_TICKET_KEY, ticketResult);

            return this.ticketSign(jsApiTicketRsp.getTicket(), url);
        } else {
            if (jsApiTicketRsp == null) {
                jsApiTicketRsp = new JsApiTicketRsp();
            }
            log.error("WeixinCgiHelper getJsApiTicket 200 error[{}][{}]", jsApiTicketRsp.getErrorCode(), jsApiTicketRsp.getErrorMsg());
            throw new BusinessException("common_error_wx_get_jsapi_ticket_failed");
        }
    }

    /**
     * 签名url
     *
     * @param ticket 授权ticket
     * @param url    需要签名的url
     * @return 签名结果
     */
    private Map<String, Object> ticketSign(String ticket, String url) {

        long timestamp = System.currentTimeMillis() / 1000;//从1970年1月1日00:00:00至今的秒数
        String nonceStr = RandomStringUtilPlus.random(32, true, true);
        //注意这里参数名必须全部小写，且必须有序
        String needSign = String.format(WX_JS_API_TICKET_SIGN, ticket, nonceStr, timestamp, url);
        String signature = DigestUtilPlus.SHA1.sign(needSign);

        Map<String, Object> params = new HashMap<>();
        params.put("appId", appId);
        params.put("timestamp", timestamp);
        params.put("nonceStr", nonceStr);
        params.put("signature", signature);
        return params;
    }
}
