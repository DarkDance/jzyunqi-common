package cn.jzyunqi.common.model.weixin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Getter
@Setter
abstract class WeixinOpenRsp implements Serializable {
    private static final long serialVersionUID = -6792109548151994746L;

    /**
     * 错误代码
     */
    @JsonProperty("errcode")
    private String errorCode;

    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errorMsg;
}
