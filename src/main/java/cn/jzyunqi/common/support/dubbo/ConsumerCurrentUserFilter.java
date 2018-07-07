package cn.jzyunqi.common.support.dubbo;

import cn.jzyunqi.common.utils.CurrentUserUtils;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ConsumerCurrentUserFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Long currentUserId = CurrentUserUtils.currentUserIdWithNull();
        //Locale currentUserLocale = LocaleContextHolder.getLocale();
        if (currentUserId != null) {
            RpcContext.getContext().setAttachment("currentUserId", currentUserId.toString());
        } else {//需要设置为空，防止线程池问题
            RpcContext.getContext().setAttachment("currentUserId", null);
        }
        return invoker.invoke(invocation);
    }

}
