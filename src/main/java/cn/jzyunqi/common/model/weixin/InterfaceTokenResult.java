package cn.jzyunqi.common.model.weixin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2018/5/31.
 */
@Getter
@Setter
public class InterfaceTokenResult implements Serializable {
    private static final long serialVersionUID = -3361217292124952710L;

    /**
     * 授权token
     */
    private String token;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
