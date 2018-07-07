package cn.jzyunqi.common.model.weixin.response;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author shenhui
 * @date 2016/5/20.
 */
@Getter
@Setter
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedOrderRsp extends WeixinPayRsp {
    private static final long serialVersionUID = -3826347105518365075L;

    //private String device_info;

    /**
     * 交易类型
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 预支付交易会话标识
     */
    @XmlElement(name = "prepay_id")
    private String prepayId;

}
