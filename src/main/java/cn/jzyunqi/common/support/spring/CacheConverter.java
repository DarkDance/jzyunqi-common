package cn.jzyunqi.common.support.spring;

import cn.jzyunqi.common.helper.Cache;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wiiyaya
 * @date 2018/7/9.
 */
public class CacheConverter implements Converter<String, Cache> {

    private Set<Class<? extends Cache>> cacheClassList = new HashSet<>();

    public void addCacheClass(Class<? extends Cache> cacheClass){
        cacheClassList.add(cacheClass);
    }

    @Override
    public Cache convert(@NonNull String source) {
        for (Class<? extends Cache> aClass : cacheClassList) {
            try {
                Field field = aClass.getField(source);
                try {
                    return (Cache)field.get(null);
                } catch (IllegalAccessException e) {
                    return null;
                }
            } catch (NoSuchFieldException e) {
                //continue;
            }
        }
        return null;
    }
}
