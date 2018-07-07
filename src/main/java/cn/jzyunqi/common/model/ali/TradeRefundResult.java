package cn.jzyunqi.common.model.ali;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Getter
@Setter
public class TradeRefundResult implements Serializable {
    private static final long serialVersionUID = -11080430037729379L;

    /**
     * 支付宝退款单号
     */
    private String tradeNo;

    /**
     * 支付宝退款金额
     */
    private BigDecimal refundFee;

    /**
     * 查询返回字符串
     */
    private String responseStr;
}
