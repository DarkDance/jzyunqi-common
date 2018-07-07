package cn.jzyunqi.common.model.weixin.response;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Getter
@Setter
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderRefundRsp extends WeixinPayRsp {
    private static final long serialVersionUID = 4586413743395590814L;

    /**
     * 微信支付订单号
     */
    @XmlElement(name = "transaction_id")
    private String transactionId;

    /**
     * 订单号
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    /**
     * 微信退款单号
     */
    @XmlElement(name = "refund_id")
    public String refundId;

    /**
     * 退款金额
     */
    @XmlElement(name = "refund_fee")
    public BigDecimal refundFee;

    //private Integer settlement_refund_fee

    /**
     * 总金额
     */
    @XmlElement(name = "total_fee")
    private BigDecimal totalFee;

    //private BigDecimal settlement_total_fee;
    //private String fee_type;
    //private BigDecimal cash_fee;
    //private String cash_fee_type;
    //private BigDecimal cash_refund_fee;
    //private String coupon_type_$n;
    //private BigDecimal coupon_refund_fee;
    //private String coupon_refund_fee_$n;
    //private Integer coupon_refund_count;
    //private Integer coupon_refund_id_$n;

}
