package cn.jzyunqi.common.helper;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/21.
 */
public interface Cache extends Serializable {

    CacheType getType();

    String getPrefix();

    long getExpiration();

    enum CacheType {
        V, L, S, Z, H;
    }
}
