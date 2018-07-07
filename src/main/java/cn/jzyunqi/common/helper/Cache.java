package cn.jzyunqi.common.helper;

/**
 * @author wiiyaya
 * @date 2018/5/21.
 */
public interface Cache {

    CacheType getType();

    String getPrefix();

    long getExpiration();

    enum CacheType {
        V, L, S, Z, H;
    }
}
