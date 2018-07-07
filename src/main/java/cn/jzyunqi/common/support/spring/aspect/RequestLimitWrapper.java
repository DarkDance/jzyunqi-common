package cn.jzyunqi.common.support.spring.aspect;

import cn.jzyunqi.common.exception.RequestLimitException;
import cn.jzyunqi.common.support.spring.annotation.RequestLimit;
import cn.jzyunqi.common.utils.CurrentUserUtils;
import cn.jzyunqi.common.utils.StringUtilPlus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@Aspect
public class RequestLimitWrapper implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLimitWrapper.class);

    /**
     * 所有标记了@RestController的类下所有的方法
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    /**
     * 所有标记了@Controller的类下所有的方法
     */
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controller() {
    }

    /**
     * 标记了@RequestLimit的方法
     */
    @Pointcut(value = "@annotation(limit)", argNames = "limit")
    public void requestLimit(RequestLimit limit) {
    }

    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redisTemplate, "A redisTemplate must be supplied.");
    }

    @Before(value = "restController() && requestLimit(limit) || controller() && requestLimit(limit)", argNames = "joinPoint,limit")
    public void requestLimit(JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        Method method = ReflectionUtils.findMethod(authentication.getDetails().getClass(), "getRemoteAddress");
        String ip = "unknown_ip";
        if (method != null) {
            ip = (String) ReflectionUtils.invokeMethod(method, authentication.getDetails());
        }
        String methodName = joinPoint.getSignature().getName();
        String key = StringUtilPlus.join("REQUEST_LIMIT:", methodName, ":", ip);
        Long count = redisTemplate.opsForValue().increment(key, 1);

        if (count != null && count == 1L) {
            redisTemplate.expire(key, limit.seconds() * 1000, TimeUnit.MILLISECONDS);
        }
        if (count != null && count > limit.count()) {
            Long currentMemberId = CurrentUserUtils.currentUserIdWithNull();
            LOGGER.info("用户[{}][{}]在限定时间[{}]秒内访问方法[{}][{}]次，超过了设定的[{}]次限制", currentMemberId, ip, limit.seconds(), methodName, count, limit.count());
            throw new RequestLimitException();
        }
    }

}
