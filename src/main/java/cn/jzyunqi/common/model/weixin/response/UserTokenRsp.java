package cn.jzyunqi.common.model.weixin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Getter
@Setter
public class UserTokenRsp extends WeixinOpenRsp {
    private static final long serialVersionUID = -7705834619669304828L;

    /**
     * 微信unionId
     */
    @JsonProperty("unionid")
    private String unionId;

    /**
     * 微信openId
     */
    @JsonProperty("openid")
    private String openId;

    /**
     * 授权token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 刷新token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 授权token过期时间
     */
    @JsonProperty("expires_in")
    private long expiresIn;

    /**
     * 用户授权的作用域
     */
    private String scope;

    //兼容小程序用
    public String getSession_key() {
        return accessToken;
    }

    public void setSession_key(String accessToken) {
        this.accessToken = accessToken;
    }
}
