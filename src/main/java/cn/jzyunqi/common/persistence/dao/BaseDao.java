package cn.jzyunqi.common.persistence.dao;

import cn.jzyunqi.common.persistence.dao.tools.JF;
import cn.jzyunqi.common.persistence.dao.tools.JFTuple;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Page<T> findAllJF(JF joinFetch, Pageable pageable);

    List<T> findAllJF(JF joinFetch);

    Page<Tuple> findAllJFTuple(JFTuple joinFetch, Pageable pageable);

    List<Tuple> findAllJFTuple(JFTuple joinFetch);

}
