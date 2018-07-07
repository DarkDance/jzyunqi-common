package cn.jzyunqi.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/29.
 */
@Getter
@Setter
public class ShareDto implements Serializable {
    private static final long serialVersionUID = -7966289343176247028L;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片
     */
    private String images;

    /**
     * 描述
     */
    private String desc;

    /**
     * 超链接
     */
    private String url;
}
