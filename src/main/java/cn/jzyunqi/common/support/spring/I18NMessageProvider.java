package cn.jzyunqi.common.support.spring;

import java.util.Locale;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public interface I18NMessageProvider {

    /**
     * 获取国际化消息
     *
     * @param code   消息码
     * @param locale 语言
     * @return 消息
     */
    String findMessage(String code, Locale locale);
}
