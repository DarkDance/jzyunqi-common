package cn.jzyunqi.common.model.netease.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @date 2018/6/2.
 */
@Getter
@AllArgsConstructor
public enum RoleOpt {

    /**
     * 管理员
     */
    ADMIN(1),

    /**
     * 普通用户
     */
    NORMAL(2),

    /**
     * 黑名单
     */
    BLACK(-1),

    /**
     * 禁言
     */
    MUTE(-2),;

    /**
     * 操作编号
     */
    private Integer opt;
}
