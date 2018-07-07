package cn.jzyunqi.common.support.hibernate;

import org.hibernate.dialect.MySQL55Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class MySQL5InnoDBDialectPlus extends MySQL55Dialect {

    public MySQL5InnoDBDialectPlus() {
        super();
        registerFunction(
                "regexp_like", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN,
                        "?1 REGEXP ?2")
        );
        registerFunction(
                "add_minutes", new SQLFunctionTemplate(StandardBasicTypes.TIMESTAMP,
                        "date_add(?1, INTERVAL ?2 MINUTE)"));
        registerFunction(
                "bit_and", new SQLFunctionTemplate(StandardBasicTypes.INTEGER,
                        "?1 & ?2"));

        registerHibernateType(Types.BIGINT, StandardBasicTypes.LONG.getName());
    }
}
