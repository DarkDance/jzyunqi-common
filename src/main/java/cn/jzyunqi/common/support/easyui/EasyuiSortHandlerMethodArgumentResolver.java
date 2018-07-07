package cn.jzyunqi.common.support.easyui;

import cn.jzyunqi.common.utils.StringUtilPlus;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class EasyuiSortHandlerMethodArgumentResolver extends SortHandlerMethodArgumentResolver {

    private static final String EASYUI_SORT_PARAMETER = "sort";
    private static final String EASYUI_ORDER_PARAMETER = "order";

    @Override
    public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String[] sortParameter = webRequest.getParameterValues(EASYUI_SORT_PARAMETER);
        String[] orderParameter = webRequest.getParameterValues(EASYUI_ORDER_PARAMETER);

        if (sortParameter != null && sortParameter.length != 0) {
            return prepareSort(sortParameter[0], orderParameter);
        } else {
            return Sort.unsorted();
        }
    }

    private Direction[] prepareDirection(int length, String[] orderParameter) {
        Direction[] directions = new Direction[length];
        if (orderParameter != null && orderParameter.length != 0) {
            String[] orders = StringUtilPlus.split(orderParameter[0], StringUtilPlus.COMMA);
            for (int i = 0; i < length; i = i + 1) {
                if (i < orders.length) {
                    directions[i] = Direction.fromString(orders[i]);
                } else {
                    directions[i] = Direction.ASC;
                }
            }
        }
        return directions;
    }

    private Sort prepareSort(String sortParameter, String[] orderParameter) {
        if (sortParameter == null) {
            return Sort.unsorted();
        }
        List<Order> allOrders = new ArrayList<>();

        String[] elements = StringUtilPlus.split(sortParameter, StringUtilPlus.COMMA);
        Direction[] directions = prepareDirection(elements.length, orderParameter);
        for (int i = 0; i < elements.length; i = i + 1) {
            String property = elements[i];
            if (!org.springframework.util.StringUtils.hasText(property)) {
                continue;
            }

            allOrders.add(new Order(directions[i], property));
        }
        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }
}
