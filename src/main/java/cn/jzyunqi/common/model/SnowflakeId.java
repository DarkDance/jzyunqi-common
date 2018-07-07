package cn.jzyunqi.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2018/6/8.
 */
@Getter
@Setter
public class SnowflakeId implements Serializable {
    private static final long serialVersionUID = 5256895414969908133L;

    /**
     * 生成时间
     */
    private LocalDateTime time;

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long dataCenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence;
}
