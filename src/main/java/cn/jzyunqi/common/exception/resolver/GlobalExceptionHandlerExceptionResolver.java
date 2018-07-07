package cn.jzyunqi.common.exception.resolver;

import org.springframework.util.ClassUtils;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @date 2018/1/24.
 */
public class GlobalExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {

    private static final boolean JACKSON2_PRESENT =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", WebMvcConfigurationSupport.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", WebMvcConfigurationSupport.class.getClassLoader());

    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap<>(64);
    private final Set<Class<?>> ignoredExceptionHandler = new HashSet<>();

    public void addIgnoredExceptionHandlers(Class<?>... handlerMethod){
        ignoredExceptionHandler.addAll(Arrays.asList(handlerMethod));
    }

    public GlobalExceptionHandlerExceptionResolver(List<HandlerExceptionResolver> exceptionResolvers) {
        super();
        int i = 0;
        ExceptionHandlerExceptionResolver e = null;
        for (HandlerExceptionResolver handlerExceptionResolver : exceptionResolvers) {
            if(handlerExceptionResolver instanceof ExceptionHandlerExceptionResolver){
                e = (ExceptionHandlerExceptionResolver) handlerExceptionResolver;
                break;
            }
            i++;
        }
        if(e != null){
            this.setContentNegotiationManager(e.getContentNegotiationManager());
            this.setMessageConverters(e.getMessageConverters());
            this.setCustomArgumentResolvers(e.getCustomArgumentResolvers());
            this.setCustomReturnValueHandlers(e.getCustomReturnValueHandlers());
            if (JACKSON2_PRESENT) {
                this.setResponseBodyAdvice(Collections.<ResponseBodyAdvice<?>>singletonList(new JsonViewResponseBodyAdvice()));
            }
            this.setApplicationContext(e.getApplicationContext());
            exceptionResolvers.remove(i);
            exceptionResolvers.add(i, this);
        }
    }

    @Override
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
        Class<?> handlerType = (handlerMethod != null ? handlerMethod.getBeanType() : null);

        if (handlerMethod != null && !ignoredExceptionHandler.contains(handlerType)) {
            ExceptionHandlerMethodResolver resolver = this.exceptionHandlerCache.get(handlerType);
            if (resolver == null) {
                resolver = new ExceptionHandlerMethodResolver(handlerType);
                this.exceptionHandlerCache.put(handlerType, resolver);
            }
            Method method = resolver.resolveMethod(exception);
            if (method != null) {
                return new ServletInvocableHandlerMethod(handlerMethod.getBean(), method);
            }
        }

        for (Map.Entry<ControllerAdviceBean, ExceptionHandlerMethodResolver> entry : this.getExceptionHandlerAdviceCache().entrySet()) {
            if (entry.getKey().isApplicableToBeanType(handlerType)) {
                ExceptionHandlerMethodResolver resolver = entry.getValue();
                Method method = resolver.resolveMethod(exception);
                if (method != null) {
                    return new ServletInvocableHandlerMethod(entry.getKey().resolveBean(), method);
                }
            }
        }

        return null;
    }
}
