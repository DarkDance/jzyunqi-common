package cn.jzyunqi.common.model.ali.response;

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
abstract class AliYunBaseRsp implements Serializable {
    private static final long serialVersionUID = -3393793664243718777L;

    /**
     * 请求ID
     */
    @JsonProperty("RequestId")
    private String requestId;

    /**
     * 请求访问的站点ID
     */
    @JsonProperty("HostId")
    private String hostId;

    /**
     * 状态码-返回OK代表请求成功
     */
    @JsonProperty("Code")
    private String code;

    /**
     * 状态码的描述
     */
    @JsonProperty("Message")
    private String message;
}
