package cn.jzyunqi.common.model.netease;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/6/1.
 */
@Getter
@Setter
public class NeteaseBaseRsp implements Serializable {
    private static final long serialVersionUID = -2556797967452488772L;

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private Object desc;
}
