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
public class OrderRefundResult implements Serializable {
    private static final long serialVersionUID = 8170407149370567980L;

    /**
     * 微信退单号
     */
    public String refundId;

    /**
     * 微信退款金额
     */
    public BigDecimal refundFee;

    /**
     * 查询返回字符串
     */
    private String responseStr;

}
