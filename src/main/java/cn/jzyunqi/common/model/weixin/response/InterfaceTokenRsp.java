package cn.jzyunqi.common.model.weixin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/5/29.
 */
@Getter
@Setter
public class InterfaceTokenRsp extends WeixinOpenRsp {
    private static final long serialVersionUID = 1783807609363500904L;

    /**
     * 授权token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 授权token过期时间(秒)
     */
    @JsonProperty("expires_in")
    private long expiresIn;

}
