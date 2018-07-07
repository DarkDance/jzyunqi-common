package cn.jzyunqi.common.support.freemarker;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleDate;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * @author wiiyaya
 * @date 2018/6/30.
 */
public class Java8ObjectWrapper extends DefaultObjectWrapper {

    public Java8ObjectWrapper() {
        super(Configuration.VERSION_2_3_28);
    }

    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        if (obj instanceof TemporalAccessor) {
            ZoneId zoneId = ZoneId.systemDefault();
            if (obj instanceof LocalDate) {
                return new SimpleDate(Date.from(((LocalDate) obj).atStartOfDay(zoneId).toInstant()), 2);
            }
            if (obj instanceof LocalDateTime) {
                return new SimpleDate(Date.from(((LocalDateTime) obj).atZone(zoneId).toInstant()), 3);
            }
        }
        return super.handleUnknownType(obj);
    }
}
