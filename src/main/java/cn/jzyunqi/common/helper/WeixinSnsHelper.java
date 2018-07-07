package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.weixin.enums.AppType;
import cn.jzyunqi.common.model.weixin.response.UserInfoRsp;
import cn.jzyunqi.common.model.weixin.response.UserTokenRsp;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Slf4j
public class WeixinSnsHelper {

    /**
     * 通过code获取access_token的接口
     */
    private static final String WX_APP_USER_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 通过code获取access_token的接口
     */
    private static final String WX_MP_USER_TOKEN_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 获取用户个人信息
     */
    private static final String WX_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

    /**
     * 应用唯一标识
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 应用类型
     */
    private AppType appType;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public WeixinSnsHelper(String appId, String appSecret, AppType appType, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.appType = appType;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 通过code获取access_token
     *
     * @param authCode 用户换取access_token的code
     * @return UserTokenRsp
     */
    public UserTokenRsp getUserAccessToken(String authCode) throws BusinessException {
        UserTokenRsp userTokenRsp;
        try {
            URI accessTokenUri;
            switch (appType){
                case APP:
                    accessTokenUri = new URIBuilder(String.format(WX_APP_USER_TOKEN_URL, appId, appSecret, authCode)).build();
                    break;
                case MP:
                    accessTokenUri = new URIBuilder(String.format(WX_MP_USER_TOKEN_URL, appId, appSecret, authCode)).build();
                    break;
                default:
                    log.error("WeixinPublicService getUserAccessToken appType error[{}]:", appType);
                    throw new BusinessException("common_error_wx_get_auth_token_error");
            }

            RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(HttpMethod.GET, accessTokenUri);
            ResponseEntity<UserTokenRsp> weixinUserRsp = restTemplate.exchange(requestEntity, UserTokenRsp.class);
            userTokenRsp = weixinUserRsp.getBody();
        } catch (Exception e) {
            log.error("WeixinPublicService getUserAccessToken other error[{}]:", appType, e);
            throw new BusinessException("common_error_wx_get_auth_token_error");
        }
        //微信不管成功还是失败，返回的都是200，需要通过额外的字段来判断是否真的成功
        if (userTokenRsp != null && StringUtilPlus.isEmpty(userTokenRsp.getErrorCode())) {
            return userTokenRsp;
        } else {
            if (userTokenRsp == null) {
                userTokenRsp = new UserTokenRsp();
            }
            log.error("WeixinPublicService getUserAccessToken 200 error[{}][{}][{}]", appType, userTokenRsp.getErrorCode(), userTokenRsp.getErrorMsg());
            throw new BusinessException("common_error_wx_get_auth_token_failed");
        }
    }

    /**
     * 获取用户个人信息
     *
     * @param openid          用户openid
     * @param userAccessToken access_token
     * @return 用户信息
     */
    public UserInfoRsp getUserInfo(String openid, String userAccessToken) throws BusinessException {
        if(appType != AppType.APP){
            log.error("WeixinPublicService getUserInfo appType error:", appType);
            throw new BusinessException("common_error_wx_get_user_info_error");
        }
        UserInfoRsp userInfoRsp;
        try {
            URI weixinUserInfoUri = new URIBuilder(String.format(WX_USER_INFO_URL, userAccessToken, openid)).build();

            RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(HttpMethod.GET, weixinUserInfoUri);
            ResponseEntity<UserInfoRsp> weixinUserRsp = restTemplate.exchange(requestEntity, UserInfoRsp.class);
            userInfoRsp = weixinUserRsp.getBody();
        } catch (Exception e) {
            log.error("WeixinPublicService getUserInfo other error:", e);
            throw new BusinessException("common_error_wx_get_user_info_error");
        }
        //微信不管成功还是失败，返回的都是200，需要通过额外的字段来判断是否真的成功
        if (userInfoRsp != null && StringUtilPlus.isEmpty(userInfoRsp.getErrorCode())) {
            return userInfoRsp;
        } else {
            if (userInfoRsp == null) {
                userInfoRsp = new UserInfoRsp();
            }
            log.error("WeixinPublicService use code[{}] getUserInfo 200 error[{}][{}]", userAccessToken, userInfoRsp.getErrorCode(), userInfoRsp.getErrorMsg());
            throw new BusinessException("common_error_wx_get_user_info_failed");
        }
    }

    /**
     * 签名校验
     */
    public <T> T getEncryptedDataInfo(String sessionKey, String rawData, String signature, String iv, String encryptedData, Class<T> classType) throws Exception {
        if(appType != AppType.MP){
            log.error("WeixinPublicService getEncryptedDataInfo appType error:", appType);
            throw new BusinessException("common_error_wx_get_encrypted_data_info_failed");
        }
        if(signature.equals(DigestUtilPlus.SHA1.sign(rawData + sessionKey))){
            return objectMapper.readValue(DigestUtilPlus.AES.decrypt(encryptedData, sessionKey, iv), classType);
        }else{
            throw new BusinessException("common_error_wx_get_encrypted_data_info_failed");
        }
    }
}
