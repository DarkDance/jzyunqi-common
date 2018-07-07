package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.model.SnowflakeId;
import cn.jzyunqi.common.support.SnowflakeIdWorker;
import cn.jzyunqi.common.utils.StringUtilPlus;

/**
 * @author wiiyaya
 * @date 2018/5/24.
 */
public class NoGenHelper {

    private String specialPrefix;

    private SnowflakeIdWorker snowflakeIdWorker;

    public NoGenHelper(String specialPrefix, SnowflakeIdWorker snowflakeIdWorker) {
        this.specialPrefix = specialPrefix;
        this.snowflakeIdWorker = snowflakeIdWorker;
    }

    /**
     * 解码id
     * @param snowflakeId id
     * @return id
     */
    public SnowflakeId snowflakeIdDecrypt(String snowflakeId) {
        try {
            Long id = Long.valueOf(snowflakeId);
            return snowflakeIdWorker.parserId(id);
        } catch (Exception e) {
            return snowflakeIdWorker.parserId(snowflakeId);
        }
    }

    /**
     * 生成uid
     * @return uid
     */
    public String generateUid() {
        return snowflakeIdWorker.nextHexId();
    }

    /**
     * 生成现金账户流水号
     *
     * @return 现金账户流水号
     */
    public String generateCashTxn() {
        return incubateTxn("ACCS");//account - cash
    }

    /**
     * 生成保证金账户流水号
     *
     * @return 保证金账户流水号
     */
    public String generateBidBondTxn() {
        return incubateTxn("ACBB");//account - bid bond
    }

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    public String generateOrderNo() {
        return incubateTxn("ODNO");//order number
    }

    /**
     * 生成订单支付流水号
     *
     * @return 订单支付流水号
     */
    public String generateOrderPayTxn() {
        return incubateTxn("ODPY");//order pay
    }

    /**
     * 生成退单号
     *
     * @return 退单号
     */
    public String generateOrderReturnNo() {
        return incubateTxn("ODRT");//order return
    }

    /**
     * 生成退单支付流水号
     *
     * @return 退单支付流水号
     */
    public String generateOrderReturnPayTxn() {
        return incubateTxn("ORPY");//order return pay
    }

    /**
     * 生成公司账户流水号
     *
     * @return 公司账户流水号
     */
    public String generateCompAccTxn() {
        return incubateTxn("SCCP");//system comp cash process
    }

    /**
     * 生成保证金退回流水号
     *
     * @return 保证金退回流水号
     */
    public String generateBidBondRefundTxn() {
        return incubateTxn("BBRF");//bid bond refund
    }

    /**
     * 生成异常情况退回流水号
     *
     * @return 流水号
     */
    public String generateExceptionRefundNo() {
        return incubateTxn("EXRF");//exception refund
    }

    /**
     * 生成流水号
     *
     * @param prefix 前缀
     * @return 流水号
     */
    private String incubateTxn(String prefix) {
        return specialPrefix + prefix + StringUtilPlus.leftPad(snowflakeIdWorker.nextId(), 19, '0');
    }
}
