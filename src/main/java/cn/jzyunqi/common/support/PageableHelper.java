package cn.jzyunqi.common.support;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class PageableHelper {

    /**
     * 增加排序
     *
     * @param pageable 分页参数
     * @param orders   排序参数
     * @return 新的分页排序参数
     */
    public static Pageable addOrders(Pageable pageable, Order... orders) {
        List<Order> orderList = new ArrayList<>();
        CollectionUtils.addAll(orderList, orders);
        Sort sort = pageable.getSort().and(Sort.by(orderList));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    /**
     * 删除所有排序
     *
     * @param pageable 分页参数
     * @return 无排序分页
     */
    public static Pageable deleteAllOrder(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

}
