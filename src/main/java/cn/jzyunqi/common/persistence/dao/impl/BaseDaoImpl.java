package cn.jzyunqi.common.persistence.dao.impl;

import cn.jzyunqi.common.persistence.dao.BaseDao;
import cn.jzyunqi.common.persistence.dao.tools.JF;
import cn.jzyunqi.common.persistence.dao.tools.JFTuple;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BaseDaoImpl<T, ID extends Serializable> extends QuerydslJpaRepository<T, ID> implements BaseDao<T, ID> {

    private final EntityPath<T> path;
    private final PathBuilder<T> builder;
    private final Querydsl querydsl;

    public BaseDaoImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        this(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE);
    }

    public BaseDaoImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager, EntityPathResolver resolver) {
        super(entityInformation, entityManager);
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.builder = new PathBuilder<>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);
    }

    @Override
    public Page<T> findAllJF(JF joinFetch, Pageable pageable) {
        JPQLQuery<?> countQuery = createQuery();
        joinFetch.prepareQry(countQuery, false);
        long total = countQuery.fetchCount();

        List<T> content;
        if (total > pageable.getOffset()) {
            JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery().select(path));
            joinFetch.prepareQry(query, true);
            content = query.fetch();
        } else {
            content = Collections.emptyList();
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<T> findAllJF(JF joinFetch) {
        JPQLQuery<T> query = createQuery().select(path);
        joinFetch.prepareQry(query, true);
        return query.fetch();
    }

    @Override
    public Page<Tuple> findAllJFTuple(JFTuple joinFetch, Pageable pageable) {
        JPQLQuery countQuery = createQuery();
        joinFetch.prepareQry(countQuery, false);
        Long total = countQuery.fetchCount();

        List<Tuple> content = null;
        if (total > pageable.getOffset()) {
            JPQLQuery query = querydsl.applyPagination(pageable, createQuery());
            content = joinFetch.prepareQry(query, true);
        } else {
            content = Collections.emptyList();
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Tuple> findAllJFTuple(JFTuple joinFetch) {
        return joinFetch.prepareQry(createQuery(), true);
    }

}
