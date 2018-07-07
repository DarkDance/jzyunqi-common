package cn.jzyunqi.common.persistence.dao.tools;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;

import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public interface JFTuple {

    List<Tuple> prepareQry(JPQLQuery schQry, boolean notCountQry);

}
