package cn.jzyunqi.common.model.weixin.response;

import cn.jzyunqi.common.model.weixin.enums.ResultCode;
import cn.jzyunqi.common.model.weixin.enums.ReturnCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
abstract class WeixinPayRsp implements Serializable {
    private static final long serialVersionUID = 1549312919516040308L;

    /**
     * 返回状态码,SUCCESS/FAIL
     */
    @XmlElement(name = "return_code")
    private ReturnCode returnCode;

    /**
     * 返回信息
     */
    @XmlElement(name = "return_msg")
    private String returnMsg;

    /**
     * 应用APPID
     */
    @XmlElement(name = "appid")
    private String appId;

    /**
     * 商户号
     */
    @XmlElement(name = "mch_id")
    private String mchId;

    /**
     * 随机字符串
     */
    @XmlElement(name = "nonce_str")
    private String nonceStr;

    /**
     * 签名
     */
    @XmlElement(name = "sign")
    private String sign;

    /**
     * 业务结果
     */
    @XmlElement(name = "result_code")
    private ResultCode resultCode;

    /**
     * 错误代码
     */
    @XmlElement(name = "err_code")
    private String errCode;

    /**
     * 错误代码描述
     */
    @XmlElement(name = "err_code_des")
    private String errCodeDes;
}
