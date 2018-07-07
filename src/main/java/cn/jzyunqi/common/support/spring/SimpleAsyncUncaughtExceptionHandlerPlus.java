package cn.jzyunqi.common.support.spring;

import cn.jzyunqi.common.utils.CurrentUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class SimpleAsyncUncaughtExceptionHandlerPlus extends SimpleAsyncUncaughtExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(SimpleAsyncUncaughtExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        Long currUserId = CurrentUserUtils.currentUserIdWithNull();
        LOGGER.error("Error catch Spring async event : method[{}], params[{}], currUserId[{}]", method, params, currUserId, ex);
    }
}
