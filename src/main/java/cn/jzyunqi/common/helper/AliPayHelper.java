package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.model.ali.TradeAppPayResult;
import cn.jzyunqi.common.model.ali.TradeQueryResult;
import cn.jzyunqi.common.model.ali.TradeRefundResult;
import cn.jzyunqi.common.model.ali.enums.TradeStatus;
import cn.jzyunqi.common.model.ali.response.AliPayRsp;
import cn.jzyunqi.common.model.ali.response.TradeQueryRsp;
import cn.jzyunqi.common.model.ali.response.TradeRefundRsp;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Slf4j
public class AliPayHelper {

    private static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do?%s";
    private static final String API_TRADE_APP_PAY = "alipay.trade.app.pay";
    private static final String API_TRADE_QUERY = "alipay.trade.query";
    private static final String API_TRADE_REFUND = "alipay.trade.refund";

    private String appId;

    private String privateKey;

    private String publicKey;

    private String callbackUrl;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public AliPayHelper(String appId, String privateKey, String publicKey, String callbackUrl, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.appId = appId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.callbackUrl = callbackUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 支付签名
     *
     * @param outTradeNo       申请单号
     * @param subject          订单标题
     * @param detail           对交易或商品的描述
     * @param amount           支付金额
     * @param expiresInMinutes 过期时间
     * @param creditSupport    是否支持信用卡
     * @return 签名
     */
    public TradeAppPayResult signForPay(String outTradeNo, String subject, String detail, BigDecimal amount, int expiresInMinutes, boolean creditSupport) throws BusinessException {
        try {
            //设置业务参数
            Map<String, String> businessParamMap = new TreeMap<>();
            businessParamMap.put("out_trade_no", outTradeNo); //商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
            businessParamMap.put("subject", StringUtilPlus.substring(subject, 0, 256));//订单标题
            businessParamMap.put("body", StringUtilPlus.substring(detail, 0, 128));//对交易或商品的描述
            businessParamMap.put("timeout_express", expiresInMinutes + "m");//该笔订单允许的最晚付款时间，逾期将关闭交易
            businessParamMap.put("total_amount", amount.toPlainString()); //订单总金额，单位为元，精确到小数点后两位
            businessParamMap.put("product_code", "QUICK_MSECURITY_PAY");//销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
            if(!creditSupport){
                businessParamMap.put("disable_pay_channels", "credit_group");//禁用渠道，用户不可用指定渠道支付,当有多个渠道时用“,”分隔
            }

            String queryString = getQueryString(businessParamMap, API_TRADE_APP_PAY, true);

            TradeAppPayResult signModel = new TradeAppPayResult();
            signModel.setApplyPayNo(outTradeNo);
            signModel.setSign(queryString);
            return signModel;
        } catch (Exception e) {
            log.error("======AliPayHelper signForPay error [{}][{}][{}][{}]", outTradeNo);
            throw new BusinessException("common_error_ali_pay_sign_failed");
        }
    }

    /**
     * 校验返回数据
     * @param paramMap 返回数据
     * @return 校验结果 true 成功
     */
    public boolean payCallBackCheck(Map<String, String> paramMap) {
        try {
            String sign = paramMap.get("sign");
            String needSignContent = paramMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals("sign") && !entry.getKey().equals("sign_type"))
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> StringUtilPlus.join(entry.getKey(), "=", entry.getValue()))
                    .collect(Collectors.joining("&"));

            return DigestUtilPlus.RSA256.decrypt(needSignContent, sign, publicKey);
        } catch (Exception e) {
            log.error("======AliPayHelper payCallBackCheck error :", e);
            return false;
        }
    }

    /**
     * 根据单号查询支付结果
     *
     * @param tradeNo    支付宝单号
     * @param outTradeNo 本地单号
     * @return 查询结果
     */
    public TradeQueryResult queryPay(String tradeNo, String outTradeNo) {
        TradeQueryRsp tradeQueryRsp;
        try {
            //设置业务参数
            Map<String, String> businessParamMap = new TreeMap<>();
            businessParamMap.put("trade_no", tradeNo);
            businessParamMap.put("out_trade_no", outTradeNo);

            String queryString = getQueryString(businessParamMap, API_TRADE_QUERY, false);
            URI uri = new URIBuilder(String.format(GATEWAY_URL, queryString)).build();

            RequestEntity requestEntity = new RequestEntity(HttpMethod.POST, uri);
            ResponseEntity<AliPayRsp> sendRsp = restTemplate.exchange(requestEntity, AliPayRsp.class);

            AliPayRsp aliPayRsp = Objects.requireNonNull(sendRsp.getBody());
            //这里可以对sign做校验
            tradeQueryRsp = aliPayRsp.getTradeQueryRsp();
        } catch (Exception e) {
            log.error("======AliPayHelper queryPay other error :", e);
            return null;
        }

        //就是比如订单的总金额是10元total_amount，那用户支付的时候，如果您给商家发了6元的优惠券，那么用户支付完成后，您实际是收到了4元的receipt_amount，因为这个优惠券是您自己的，如果是用的支付宝红包6元红包，那这6元就是支付宝发的，您就实际收到了10元receipt_amount
        if (StringUtilPlus.isEmpty(tradeQueryRsp.getSubCode())) {
            TradeStatus tradeStatus = tradeQueryRsp.getTradeStatus();
            if (tradeStatus == TradeStatus.TRADE_FINISHED || tradeStatus == TradeStatus.TRADE_SUCCESS) {
                TradeQueryResult dto = new TradeQueryResult();
                dto.setTradeNo(tradeQueryRsp.getTradeNo()); //支付宝交易号
                dto.setTotalAmount(new BigDecimal(tradeQueryRsp.getTotalAmount())); //交易的订单金额，单位为元，两位小数
                dto.setResponseStr("主动：" + ToStringBuilder.reflectionToString(tradeQueryRsp));
                return dto;
            }
        }else{
            log.error("======AliPayHelper queryPay 200 error [{}][{}][{}][{}]", tradeQueryRsp.getCode(), tradeQueryRsp.getSubCode(), tradeQueryRsp.getMsg(), tradeQueryRsp.getSubMsg());
        }
        return null;
    }

    /**
     * 支付宝退款
     *
     * @param tradeNo      支付宝单号
     * @param outRefundNo  本地退单号
     * @param refundFee    需退款金额
     * @param refundReason 退款原因
     * @return 退款结果
     */
    public TradeRefundResult payRefund(String tradeNo, String outRefundNo, BigDecimal refundFee, String refundReason) throws BusinessException {
        TradeRefundRsp tradeRefundRsp;
        try {
            //设置业务参数
            Map<String, String> businessParamMap = new TreeMap<>();
            businessParamMap.put("trade_no", tradeNo);
            businessParamMap.put("out_request_no", outRefundNo);
            businessParamMap.put("refund_amount", refundFee.toPlainString());
            businessParamMap.put("refund_reason", refundReason);

            String queryString = getQueryString(businessParamMap, API_TRADE_REFUND, false);
            URI uri = new URIBuilder(String.format(GATEWAY_URL, queryString)).build();

            RequestEntity requestEntity = new RequestEntity(HttpMethod.POST, uri);
            ResponseEntity<AliPayRsp> sendRsp = restTemplate.exchange(requestEntity, AliPayRsp.class);

            AliPayRsp aliPayRsp = Objects.requireNonNull(sendRsp.getBody());
            //这里可以对sign做校验
            tradeRefundRsp = aliPayRsp.getTradeRefundRsp();
        } catch (Exception e) {
            log.error("======AliPayHelper payRefund other error :", e);
            throw new BusinessException("common_error_ali_pay_refund_error");
        }

        if (StringUtilPlus.isEmpty(tradeRefundRsp.getSubCode())) {
            TradeRefundResult refundCallbackDto = new TradeRefundResult();
            refundCallbackDto.setTradeNo(tradeRefundRsp.getTradeNo());
            refundCallbackDto.setRefundFee(new BigDecimal(tradeRefundRsp.getRefundFee()));
            refundCallbackDto.setResponseStr(ToStringBuilder.reflectionToString(tradeRefundRsp));
            return refundCallbackDto;
        }else{
            log.error("======AliPayHelper payRefund 200 error [{}][{}][{}][{}]", tradeRefundRsp.getCode(), tradeRefundRsp.getSubCode(), tradeRefundRsp.getMsg(), tradeRefundRsp.getSubMsg());
            throw new BusinessException("common_error_ali_pay_refund_failed");
        }
    }

    /**
     * 获取查询字符串
     *
     * @param businessParamMap 业务参数
     * @return 查询字符串
     */
    private String getQueryString(Map<String, String> businessParamMap, String method, boolean withCallback) throws Exception {
        Map<String, String> apiParamMap = new TreeMap<>();
        apiParamMap.put("app_id", this.appId);
        apiParamMap.put("method", method);//接口名称
        apiParamMap.put("charset", "utf-8");
        apiParamMap.put("sign_type", "RSA2"); //商户生成签名字符串所使用的签名算法类型
        apiParamMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        apiParamMap.put("version", "1.0");//调用的接口版本，固定为：1.0
        apiParamMap.put("biz_content", objectMapper.writeValueAsString(businessParamMap));
        if(withCallback){
            apiParamMap.put("notify_url", callbackUrl);
        }

        String needSignContent = apiParamMap.entrySet().stream()
                .map(entry -> StringUtilPlus.join(entry.getKey(), "=", entry.getValue()))
                .collect(Collectors.joining("&"));

        apiParamMap.put("sign", DigestUtilPlus.RSA256.encrypt(needSignContent, privateKey));

        return apiParamMap.entrySet().stream()
                .map(entry -> {
                    try {
                        return StringUtilPlus.join(entry.getKey(), "=", URLEncoder.encode(entry.getValue(),"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        return StringUtilPlus.join(entry.getKey(), "=", entry.getValue());
                    }
                })
                .collect(Collectors.joining("&"));
    }
}
