package cn.jzyunqi.common.model.weixin.response;

import cn.jzyunqi.common.model.weixin.enums.TradeState;
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
public class OrderQueryRsp extends WeixinPayRsp {
    private static final long serialVersionUID = 3956653063757041459L;

    //private String device_info;

    /**
     * 用户在商户appId下的唯一标识
     */
    @XmlElement(name = "openid")
    private String openId;

    /**
     * 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
     */
    @XmlElement(name = "is_subscribe")
    private String isSubscribe;

    /**
     * 交易类型
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 交易状态
     */
    @XmlElement(name = "trade_state")
    private TradeState tradeState;

    //private String bank_type;

    /**
     * 总金额
     */
    @XmlElement(name = "total_fee")
    private BigDecimal totalFee;

    //private String fee_type;
    //private BigDecimal cash_fee;
    //private String cash_fee_type;
    //private BigDecimal settlement_total_fee;
    //private BigDecimal coupon_fee;
    //private Integer coupon_count;
    //private Integer coupon_id_$n;
    //private Integer coupon_type_$n;
    //private Integer coupon_fee_$n;

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

    //private String attach;
    //private String time_end;
    //private String trade_state_desc;
}
