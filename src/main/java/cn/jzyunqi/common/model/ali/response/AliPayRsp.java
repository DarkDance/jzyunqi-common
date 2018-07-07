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
public class AliPayRsp{

    private String sign;

    @JsonProperty("alipay_trade_query_response")
    private TradeQueryRsp tradeQueryRsp;

    @JsonProperty("alipay_trade_refund_response")
    private TradeRefundRsp tradeRefundRsp;
}
