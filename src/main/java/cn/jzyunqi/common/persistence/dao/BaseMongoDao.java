package cn.jzyunqi.common.persistence.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@NoRepositoryBean
public interface BaseMongoDao<T, ID extends Serializable> extends MongoRepository<T, ID> {

}
