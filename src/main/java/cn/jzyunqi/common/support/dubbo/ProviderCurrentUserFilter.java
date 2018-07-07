package cn.jzyunqi.common.support.dubbo;

import cn.jzyunqi.common.model.spring.security.LoginUserDto;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class ProviderCurrentUserFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String currentUserId = RpcContext.getContext().getAttachment("currentUserId");
        if (StringUtilPlus.isEmpty(currentUserId)) {
            LoginUserDto currentUser = new LoginUserDto();
            currentUser.setId(Long.parseLong(currentUserId));
            Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, StringUtilPlus.EMPTY, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return invoker.invoke(invocation);
    }
}
