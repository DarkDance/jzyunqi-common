package cn.jzyunqi.common.model.ali.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Getter
@Setter
public class AssumeRoleRsp extends AliYunBaseRsp {
    private static final long serialVersionUID = -2625532204977924204L;

    /**
     * 访问凭证
     */
    @JsonProperty("Credentials")
    private Credentials credentials;

    /**
     * 角色扮演临时身份
     */
    @JsonProperty("AssumedRoleUser")
    private AssumedRoleUser assumedRoleUser;

    @Getter
    @Setter
    public static class Credentials {

        /**
         * 访问密钥标识
         */
        @JsonProperty("AccessKeyId")
        private String accessKeyId;

        /**
         * 访问密钥
         */
        @JsonProperty("AccessKeySecret")
        private String accessKeySecret;

        /**
         * 安全令牌
         */
        @JsonProperty("SecurityToken")
        private String securityToken;

        /**
         * 失效时间
         */
        @JsonProperty("Expiration")
        @JsonFormat(locale = "en", timezone = "GMT+0", pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")//阿里返回的是伦敦时间
        private LocalDateTime expiration;

    }

    @Getter
    @Setter
    public static class AssumedRoleUser {

        /**
         * 该角色临时身份的资源描述符
         */
        @JsonProperty("Arn")
        private String arn;

        /**
         * 该角色临时身份的用户ID
         */
        @JsonProperty("AssumedRoleId")
        private String assumedRoleId;
    }
}
