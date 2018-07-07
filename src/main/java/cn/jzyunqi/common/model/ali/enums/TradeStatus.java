package cn.jzyunqi.common.model.ali.enums;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
public enum TradeStatus {
    /**
     * 交易创建: 等待买家付款
     */
    WAIT_BUYER_PAY,

    /**
     * 交易关闭: 在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。
     */
    TRADE_CLOSED,

    /**
     * 支付成功: 通知触发条件是商户签约的产品支持退款功能的前提下，买家付款成功；
     */
    TRADE_SUCCESS,

    /**
     * 交易完成: 通知触发条件是商户签约的产品不支持退款功能的前提下，买家付款成功；或者，商户签约的产品支持退款功能的前提下，交易已经成功并且已经超过可退款期限。
     */
    TRADE_FINISHED
}
