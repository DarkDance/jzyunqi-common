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
public class UserInfoRsp extends WeixinOpenRsp {
    private static final long serialVersionUID = -1078218095463872809L;

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
     * 昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    @JsonProperty("headimgurl")
    private String headImgUrl;

    /**
     * 性别，1为男性，2为女性
     */
    private String sex;

    /**
     * 省份
     */
    private String province;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 语言
     */
    private String language;

    /**
     * 是否关注
     */
    private Boolean subscribe;
}
