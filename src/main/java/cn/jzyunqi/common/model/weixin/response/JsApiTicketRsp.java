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
public class JsApiTicketRsp extends WeixinOpenRsp {
    private static final long serialVersionUID = -3902104748545703809L;

    /**
     * ticket
     */
    private String ticket;

    /**
     * ticket过期时间
     */
    @JsonProperty("expires_in")
    private long expiresIn;
}
