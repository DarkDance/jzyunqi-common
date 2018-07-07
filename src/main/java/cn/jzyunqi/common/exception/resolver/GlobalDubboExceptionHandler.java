package cn.jzyunqi.common.exception.resolver;

import cn.jzyunqi.common.model.RestResultDto;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.rpc.RpcException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author wiiyaya
 * @date 2018/7/2.
 */
public class GlobalDubboExceptionHandler extends GlobalExceptionHandler {

    private static final String GATEWAY_TIMEOUT = "gateway_timeout";

    /**
     * dubbo异常
     */
    @ExceptionHandler({
            RpcException.class
            , RemotingException.class
    })
    public final ResponseEntity<Object> handleSecurityException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.GATEWAY_TIMEOUT;
        return handleDubboRpc(ex, new HttpHeaders(), status, request);
    }

    /**
     * dubbo超时 504
     */
    private ResponseEntity<Object> handleDubboRpc(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(GATEWAY_TIMEOUT, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), GATEWAY_TIMEOUT, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }
}
