package cn.jzyunqi.common.model.netease.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @date 2018/6/11.
 */
@Getter
@AllArgsConstructor
public enum MsgType {

    /**
     * 文本
     */
    TEXT(0),

    /**
     * 图片
     */
    IMAGE(1),

    /**
     * 语音
     */
    VOICE(2),

    /**
     * 视频
     */
    VIDEO(3),

    /**
     * 地理位置
     */
    LOCATION(4),

    /**
     * 文件
     */
    FILE(6),

    /**
     * Tips
     */
    tips(10),

    /**
     * 自定义
     */
    CUSTOM(100),;

    /**
     * 消息类型
     */
    private Integer type;
}
