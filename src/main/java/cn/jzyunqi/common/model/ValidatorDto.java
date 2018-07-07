package cn.jzyunqi.common.model;

/**
 * @author wiiyaya
 * @date 2018/5/4.
 */
public interface ValidatorDto {

    void checkAndThrowErrors(Class checkType);
}
