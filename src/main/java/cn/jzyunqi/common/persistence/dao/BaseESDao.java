package cn.jzyunqi.common.persistence.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@NoRepositoryBean
public interface BaseESDao<T, ID extends Serializable> extends ElasticsearchRepository<T, ID> {
}
