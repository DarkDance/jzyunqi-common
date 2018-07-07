package cn.jzyunqi.common.model.netease;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/6/11.
 */
@Getter
@Setter
public class Msg implements Serializable {
    private static final long serialVersionUID = 6170663144184781479L;

    /**
     * 文本消息
     */
    private String msg;

}
