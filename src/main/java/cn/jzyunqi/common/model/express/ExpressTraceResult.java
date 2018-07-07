package cn.jzyunqi.common.model.express;

import cn.jzyunqi.common.model.express.enums.ExpressStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/6/5.
 */
@Getter
@Setter
public class ExpressTraceResult implements Serializable {
    private static final long serialVersionUID = 4015380449248362620L;

    /**
     * 快递公司名称
     */
    private String expressName;

    /**
     * 快递公司图片
     */
    private String expressLogo;

    /**
     * 快递公司电话
     */
    private String expressPhone;

    /**
     * 物流运单号
     */
    private String trackingNo;

    /**
     * 物流状态
     */
    private ExpressStatus status;

    /**
     * 物流轨迹详情
     */
    private List<TraceDto> traceList;

    /**
     * 物流轨迹详情URL
     */
    private String traceUrl;

    @Getter
    @Setter
    public static class TraceDto implements Serializable {
        private static final long serialVersionUID = -5602262112409757546L;

        /**
         * 时间
         */
        private LocalDateTime acceptTime;

        /**
         * 描述
         */
        private String acceptStation;

        /**
         * 备注
         */
        private String remark;
    }
}
