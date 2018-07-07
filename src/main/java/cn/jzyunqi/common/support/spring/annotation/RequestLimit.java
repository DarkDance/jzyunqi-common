package cn.jzyunqi.common.support.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestLimit {
    /**
     * 允许访问的次数
     */
    int count() default 5;

    /**
     * 时间段，单位为秒
     */
    int seconds() default 60;
}
