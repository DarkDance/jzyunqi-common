package cn.jzyunqi.common.persistence.dao.tools;

import com.querydsl.jpa.JPQLQuery;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public interface JF {

    <T> void prepareQry(JPQLQuery<T> schQry, boolean notCountQry);
}
