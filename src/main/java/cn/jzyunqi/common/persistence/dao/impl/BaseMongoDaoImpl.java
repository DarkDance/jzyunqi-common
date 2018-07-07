package cn.jzyunqi.common.persistence.dao.impl;

import cn.jzyunqi.common.persistence.dao.BaseMongoDao;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class BaseMongoDaoImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements BaseMongoDao<T, ID> {

    private final PathBuilder<T> builder;
    private final EntityInformation<T, ID> entityInformation;
    private final MongoOperations mongoOperations;

    public BaseMongoDaoImpl(MongoEntityInformation<T, ID> entityInformation, MongoOperations mongoOperations) {
        this(entityInformation, mongoOperations, SimpleEntityPathResolver.INSTANCE);
    }

    public BaseMongoDaoImpl(MongoEntityInformation<T, ID> entityInformation, MongoOperations mongoOperations, EntityPathResolver resolver) {
        super(entityInformation, mongoOperations);

        EntityPath<T> path = resolver.createPath(entityInformation.getJavaType());
        this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
        this.entityInformation = entityInformation;
        this.mongoOperations = mongoOperations;
    }
}
