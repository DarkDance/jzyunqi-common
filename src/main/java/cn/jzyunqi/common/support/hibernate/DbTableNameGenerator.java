package cn.jzyunqi.common.support.hibernate;

import cn.jzyunqi.common.utils.StringUtilPlus;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.source.spi.AttributePath;

import java.util.Locale;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class DbTableNameGenerator extends ImplicitNamingStrategyJpaCompliantImpl {

    public static final String TABLE_PERFIX = StringUtilPlus.EMPTY;

    private static final long serialVersionUID = -7764055383263168923L;

    @Override
    protected String transformEntityName(EntityNaming entityNaming) {
        return addUnderscores(super.transformEntityName(entityNaming));
    }

    @Override
    protected String transformAttributePath(AttributePath attributePath) {
        return addUnderscores(super.transformAttributePath(attributePath));
    }

    private static String addUnderscores(String name) {
        StringBuilder buf = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (
                    Character.isLowerCase(buf.charAt(i - 1)) &&
                            Character.isUpperCase(buf.charAt(i)) &&
                            Character.isLowerCase(buf.charAt(i + 1))
                    ) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase(Locale.ROOT);
    }
}
