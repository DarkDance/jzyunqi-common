package cn.jzyunqi.common.model.express.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @date 2018/6/5.
 */
@Getter
@AllArgsConstructor
public enum ExpressStatus {

    /**
     * nothing
     */
    N("无轨迹"),

    /**
     * on the way
     */
    O("在途中"),

    /**
     * sign
     */
    S("签收"),

    /**
     * problem
     */
    P("问题件"),;

    /**
     * 描述
     */
    private String desc;

}
