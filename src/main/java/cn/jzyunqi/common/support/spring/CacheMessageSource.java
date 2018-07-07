package cn.jzyunqi.common.support.spring;

import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class CacheMessageSource extends AbstractMessageSource {

    private I18NMessageProvider i18NMessageProvider;

    public void setI18NMessageProvider(I18NMessageProvider i18NMessageProvider) {
        this.i18NMessageProvider = i18NMessageProvider;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return i18NMessageProvider.findMessage(code, Locale.CHINA);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = i18NMessageProvider.findMessage(code, Locale.CHINA);
        if (msg == null) {
            return null;
        }
        return createMessageFormat(msg, Locale.CHINA);
    }
}
