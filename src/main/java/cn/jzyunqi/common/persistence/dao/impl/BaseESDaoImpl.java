package cn.jzyunqi.common.persistence.dao.impl;

import cn.jzyunqi.common.persistence.dao.BaseESDao;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BaseESDaoImpl<T, ID extends Serializable> extends AbstractElasticsearchRepository<T, ID> implements BaseESDao<T, ID> {

    public BaseESDaoImpl() {
        super();
    }

    public BaseESDaoImpl(ElasticsearchEntityInformation<T, ID> metadata, ElasticsearchOperations elasticsearchOperations) {
        super(metadata, elasticsearchOperations);
    }

    public BaseESDaoImpl(ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    protected String stringIdRepresentation(ID id) {
        return id.toString();
    }
}
