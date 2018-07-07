package cn.jzyunqi.common.persistence.dao.impl;

import cn.jzyunqi.common.utils.StringUtilPlus;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BaseNativeDaoImpl<T, ID extends Serializable> {

    private static final String SQL_COUNT = "select count(1) ";
    private static final String SQL_ORDER = " order by ";

    @Resource
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    protected PageImpl<T> getResultPage(String listStr, String qryStr, Pageable pageable, Map<String, Object> paraMap, Class<T> target) {
        String countQryS = SQL_COUNT + qryStr;

        Query countQry = entityManager.createNativeQuery(countQryS);
        prepareQueryParam(countQry, paraMap);
        Long total = ((BigInteger) countQry.getSingleResult()).longValue();

        List rst;
        if (total > pageable.getOffset()) {
            StringBuilder qryS = new StringBuilder();
            qryS.append(listStr);
            qryS.append(qryStr);
            if (pageable.getSort().isSorted()) {
                qryS.append(SQL_ORDER);
                for (Sort.Order order : pageable.getSort()) {
                    qryS.append(order.getProperty());
                    qryS.append(StringUtilPlus.SPACE);
                    qryS.append(order.getDirection().toString());
                }
            }
            Query qry = entityManager.createNativeQuery(qryS.toString());
            prepareQueryParam(qry, paraMap);
            qry.setFirstResult((int) pageable.getOffset());
            qry.setMaxResults(pageable.getPageSize());

            setResultTransformer(qry, target);
            rst = qry.getResultList();

        } else {
            rst = Collections.emptyList();
        }
        return new PageImpl<>(rst, pageable, total);
    }

    protected Object getSingleResult(String sql, Map<String, Object> paraMap, Class<T> target) {
        Query query = entityManager.createNativeQuery(sql);
        prepareQueryParam(query, paraMap);

        setResultTransformer(query, target);
        return query.getSingleResult();
    }

    protected List<T> getResultList(String qryStr, Map<String, Object> paraMap, Class<T> target) {
        Query qry = entityManager.createNativeQuery(qryStr);
        prepareQueryParam(qry, paraMap);

        setResultTransformer(qry, target);
        return qry.getResultList();
    }

    private void prepareQueryParam(Query query, Map<String, ?> paraMap) {
        if (null != paraMap) {
            for (Map.Entry<String, ?> entry : paraMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setResultTransformer(Query query, Class target) {
        org.hibernate.query.Query<?> hQuery = query.unwrap(org.hibernate.query.Query.class);
        hQuery.setResultTransformer(Transformers.aliasToBean(target));
    }
}
