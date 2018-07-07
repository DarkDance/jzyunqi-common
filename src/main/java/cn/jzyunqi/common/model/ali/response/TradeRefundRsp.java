package cn.jzyunqi.common.model.ali.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/7/7.
 */
@Getter
@Setter
public class TradeRefundRsp extends AliPayBaseRsp {
    private static final long serialVersionUID = -5914905810647832716L;

    /**
     * 支付宝单号
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    /**
     * 退款总金额
     */
    @JsonProperty("refund_fee")
    private String refundFee;

}
