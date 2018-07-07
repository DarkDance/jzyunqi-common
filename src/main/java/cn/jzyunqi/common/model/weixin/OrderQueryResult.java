package cn.jzyunqi.common.model.weixin;

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
public class OrderQueryResult implements Serializable {
    private static final long serialVersionUID = -1522830759190152692L;

    /**
     * 本地单号
     */
    private String outTradeNo;

    /**
     * 微信单号
     */
    private String transactionId;

    /**
     * 微信支付金额
     */
    private BigDecimal totalFee;

    /**
     * 查询返回字符串
     */
    private String responseStr;
}
