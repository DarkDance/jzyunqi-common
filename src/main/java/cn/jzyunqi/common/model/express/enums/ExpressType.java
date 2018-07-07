package cn.jzyunqi.common.model.express.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @date 2018/6/5.
 */
@Getter
@AllArgsConstructor
public enum ExpressType {

    /**
     * 快递鸟
     */
    KDNIAO("http://www.kdniao.com/JSInvoke/MSearchResult.aspx?expCode=%s&expNo=%s"),

    /**
     * 顺丰
     */
    SF("http://www.sf-express.com/mobile/cn/sc/dynamic_function/waybill/waybill_query_info.html?billno=%s&mediaSource=MOBILE.OWF"),;

    /**
     * 轨迹查询URL
     */
    private String traceUrl;
}
