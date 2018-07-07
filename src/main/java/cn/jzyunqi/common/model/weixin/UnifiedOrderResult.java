package cn.jzyunqi.common.model.weixin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/29.
 */
@Getter
@Setter
public class UnifiedOrderResult implements Serializable {
    private static final long serialVersionUID = -2409444518131619094L;

    /**
     * 申请流水号
     */
    private String applyPayNo;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 预支付交易会话ID
     */
    private String prepayId;

    /**
     * 扩展字段
     */
    private String weixinPackage;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 签名
     */
    private String sign;
}
