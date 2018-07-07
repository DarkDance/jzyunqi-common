package cn.jzyunqi.common.model.ali.response;

import cn.jzyunqi.common.model.ali.enums.TradeStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @date 2018/7/7.
 */
@Getter
@Setter
public class TradeQueryRsp extends AliPayBaseRsp{
    private static final long serialVersionUID = 7512226337272314643L;

    /**
     * 支付宝交易号
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    /**
     * 交易的订单金额，单位为元，两位小数。该参数的值为支付时传入的total_amount
     */
    @JsonProperty("total_amount")
    private String totalAmount;

    /**
     * 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
     */
    @JsonProperty("trade_status")
    private TradeStatus tradeStatus;
}
