package cn.jzyunqi.common.model.express.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class KdniaoETResult implements Serializable {
    private static final long serialVersionUID = -4278394818146763818L;

    /**
     * 成功与否
     */
    @JsonProperty("Success")
    private Boolean success;

    /**
     * 失败原因
     */
    @JsonProperty("Reason")
    private String reason;

    /**
     * APP ID
     */
    @JsonProperty("EBusinessID")
    private String eBusinessID;

    /**
     * 快递公司编码
     */
    @JsonProperty("ShipperCode")
    private String shipperCode;

    /**
     * 物流运单号
     */
    @JsonProperty("LogisticCode")
    private String logisticCode;

    /**
     * 物流状态: 0-无轨迹 2-在途中，3-签收,4-问题件
     */
    @JsonProperty("State")
    private String state;

    /**
     * 物流轨迹详情
     */
    @JsonProperty("Traces")
    private List<ExpressTraceModel> traces;

    @Getter
    @Setter
    public static class ExpressTraceModel implements Serializable {
        private static final long serialVersionUID = 5294679264998583687L;

        /**
         * 时间
         */
        @JsonProperty("AcceptTime")
        @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime acceptTime;

        /**
         * 描述
         */
        @JsonProperty("AcceptStation")
        private String acceptStation;

        /**
         * 备注
         */
        @JsonProperty("Remark")
        private String remark;
    }
}
