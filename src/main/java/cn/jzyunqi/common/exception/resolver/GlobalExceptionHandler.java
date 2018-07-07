package cn.jzyunqi.common.exception.resolver;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.exception.InterfaceDeprecatedException;
import cn.jzyunqi.common.exception.MaxSessionException;
import cn.jzyunqi.common.exception.PageNotFoundException;
import cn.jzyunqi.common.exception.RequestLimitException;
import cn.jzyunqi.common.exception.SessionTimeoutException;
import cn.jzyunqi.common.exception.ValidateException;
import cn.jzyunqi.common.model.RestResultDto;
import cn.jzyunqi.common.utils.CurrentUserUtils;
import cn.jzyunqi.common.utils.NetworkUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@Slf4j
@RestControllerAdvice
@Controller
@RequestMapping("${server.error.path:/error}")
public class GlobalExceptionHandler {// extends ResponseEntityExceptionHandler里面还有一些异常未处理

    private static final String MAX_SESSION_LIMIT = "max_session_limit";
    private static final String TIME_OUT = "time_out";
    private static final String ACCESS_DENIED = "access_denied";
    private static final String INVALID_PARAM = "invalid_param";
    private static final String PAGE_NOT_FOUND = "page_not_found";
    private static final String METHOD_NOT_ALLOWED = "method_not_allowed";
    private static final String INTERFACE_DEPRECATED = "interface_deprecated";
    private static final String TOO_MANY_REQUESTS = "too_many_requests";
    private static final String INTERNAL_SERVER_ERROR = "internal_server_error";
    private static final String REST_TEMPLATE_ERROR = "rest_template_error";
    private static final String DB_NOT_FOUND = "db_not_found";
    private static final String INVALID_CLIENT = "invalid_client";

    @Resource
    protected MessageSource messageSource;

    private boolean showRealError = false;

    public void setShowRealError(boolean showRealError) {
        this.showRealError = showRealError;
    }

    /**
     * tomcat异常
     *
     * @param request HttpServletRequest
     * @return 异常页面
     */
    @RequestMapping
    @ResponseBody
    public ResponseEntity<Object> error(HttpServletRequest request, WebRequest webRequest) throws Throwable {
        Integer code = (Integer) request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
        Throwable exception = (Throwable) request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
        String simpleMsg = (String) request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE);
        String path = (String) request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE);
        Long currUserId = CurrentUserUtils.currentUserIdWithNull();

        log.error("======Spring can't catch this error real path[{}] code[{}], message[{}], currUserId[{}]======", path, code, simpleMsg, currUserId, exception);
        if (exception != null) {
            throw getRootCause(exception);
        } else {
            switch (code) {
                case 404:
                    return handlePageNotFound(new PageNotFoundException(), new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
                case 403:
                    return handleAccessDenied(new AccessDeniedException(simpleMsg), new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
                default:
                    return handleOtherSpringMVCException(new Exception(simpleMsg), webRequest);
            }
        }
    }

    /**
     * spring其他异常 500
     */
    @ExceptionHandler
    public final ResponseEntity<Object> handleOtherSpringMVCException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String simpleMsg = (String) request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE, WebRequest.SCOPE_REQUEST);
        String realErrorMessage = ex == null ? simpleMsg : ex.getMessage();
        String errorMessage = messageSource.getMessage(INTERNAL_SERVER_ERROR, null, LocaleContextHolder.getLocale());

        RestResultDto restResponseDto = RestResultDto.failed(status.value(), INTERNAL_SERVER_ERROR, errorMessage, realErrorMessage);
        return handleExceptionInternal(ex, restResponseDto, new HttpHeaders(), status, request);
    }

    @ExceptionHandler({
            InterfaceDeprecatedException.class
            , MaxSessionException.class
            , SessionTimeoutException.class
            , ValidateException.class
            , ConstraintViolationException.class
            , ServletRequestBindingException.class
            , TypeMismatchException.class
            , BindException.class
            , HttpMessageNotReadableException.class
            , PageNotFoundException.class
            , RequestLimitException.class
            , BusinessException.class
            , RestClientResponseException.class
            , RestClientException.class
            , NoSuchElementException.class
            , HttpRequestMethodNotSupportedException.class
            , AccessDeniedException.class
            , OAuth2Exception.class
            , ClientRegistrationException.class
    })
    public final ResponseEntity<Object> handleSelfException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if(ex instanceof InterfaceDeprecatedException){
            HttpStatus status = HttpStatus.FOUND;
            return handleInterfaceDeprecated(ex, headers, status, request);
        }else if(ex instanceof MaxSessionException){
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleMaxSession(ex, headers, status, request);
        }else if(ex instanceof AccessDeniedException){
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleAccessDenied(ex, headers, status, request);
        }else if(ex instanceof SessionTimeoutException){
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleSessionTimeout(ex, headers, status, request);
        }else if(ex instanceof ValidateException
                || ex instanceof ConstraintViolationException
                || ex instanceof ServletRequestBindingException
                || ex instanceof TypeMismatchException
                || ex instanceof BindException
                || ex instanceof HttpMessageNotReadableException
                ){
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleValidate(ex, headers, status, request);
        }else if(ex instanceof PageNotFoundException){
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handlePageNotFound(ex, headers, status, request);
        }else if(ex instanceof RequestLimitException){
            HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
            return handleRequestLimit(ex, headers, status, request);
        }else if(ex instanceof BusinessException){
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleBusiness(ex, headers, status, request);
        }else if(ex instanceof RestClientResponseException){
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleRestClientResponseException(ex, headers, status, request);
        }else if(ex instanceof RestClientException){
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleRestClientException(ex, headers, status, request);
        }else if(ex instanceof NoSuchElementException){
            HttpStatus status = HttpStatus.FORBIDDEN;
            return handleNoSuchElementException(ex, headers, status, request);
        }else if(ex instanceof HttpRequestMethodNotSupportedException){
            HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
            return handleHttpRequestMethodNotSupported(ex, headers, status, request);
        }else if(ex instanceof OAuth2Exception){
            return handleOAuth2Exception(ex, headers, request);
        }else if(ex instanceof ClientRegistrationException){
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            return handleClientRegistrationException(ex, headers, status, request);
        }else{
            log.warn("Unknown exception type: " + ex.getClass().getName());
            return handleOtherSpringMVCException(ex, request);
        }
    }

    /**
     * 接口过时异常 302
     */
    private ResponseEntity<Object> handleInterfaceDeprecated(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(INTERFACE_DEPRECATED, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), INTERFACE_DEPRECATED, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 同时登陆数量超过最大异常 401
     */
    private ResponseEntity<Object> handleMaxSession(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(MAX_SESSION_LIMIT, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), MAX_SESSION_LIMIT, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 会话超时异常 401
     */
    private ResponseEntity<Object> handleSessionTimeout(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(TIME_OUT, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), TIME_OUT, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 无权访问指定页面异常 401
     */
    private ResponseEntity<Object> handleAccessDenied(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(ACCESS_DENIED, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), ACCESS_DENIED, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 系统实体校验，丢失参数，参数类型转换异常 400
     */
    private ResponseEntity<Object> handleValidate(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(INVALID_PARAM, null, LocaleContextHolder.getLocale());
        String realErrorMessage;
        if (ex instanceof ConstraintViolationException) {
            StringBuilder sb = new StringBuilder();
            Set<ConstraintViolation<?>> constraintViolationSet = ((ConstraintViolationException) ex).getConstraintViolations();
            for (ConstraintViolation<?> constraintViolation : constraintViolationSet) {
                sb.append("[");
                sb.append(constraintViolation.getPropertyPath());
                sb.append(constraintViolation.getMessage());
                sb.append("]");
            }
            realErrorMessage = sb.toString();
        } else if (ex instanceof ValidateException) {
            StringBuilder sb = new StringBuilder();
            List<String> codeList = ((ValidateException) ex).getCodeList();
            for (String code : codeList) {
                sb.append("[");
                sb.append(code);
                sb.append("]");
            }
            realErrorMessage = sb.toString();
        } else {
            realErrorMessage = ex.getMessage();
        }
        RestResultDto restResultDto = RestResultDto.failed(status.value(), INVALID_PARAM, errorMessage, realErrorMessage);
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 系统业务异常 403
     */
    private ResponseEntity<Object> handleBusiness(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BusinessException bex = (BusinessException)ex;
        String errorMessage = messageSource.getMessage(bex.getCode(), bex.getArguments(), LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), bex.getCode(), errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 找不到指定页面异常 404
     */
    private ResponseEntity<Object> handlePageNotFound(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(PAGE_NOT_FOUND, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), PAGE_NOT_FOUND, errorMessage, errorMessage);
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 请求方法错误 405
     */
    private ResponseEntity<Object> handleHttpRequestMethodNotSupported(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Set<HttpMethod> supportedMethods = ((HttpRequestMethodNotSupportedException)ex).getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        String requestMethod = ((ServletWebRequest)request).getRequest().getMethod();
        String errorMessage = messageSource.getMessage(METHOD_NOT_ALLOWED, new String[]{requestMethod}, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), METHOD_NOT_ALLOWED, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * 太多请求 429
     */
    private ResponseEntity<Object> handleRequestLimit(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), TOO_MANY_REQUESTS, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    /**
     * restTemplate 异常 403
     */
    private ResponseEntity<Object> handleRestClientResponseException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RestClientResponseException rex = (RestClientResponseException)ex;
        log.error("======RestClientResponseException header[{}]======", rex.getResponseHeaders());
        log.error("======RestClientResponseException body[{}]======", rex.getResponseBodyAsString());
        String errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS, null, LocaleContextHolder.getLocale());
        RestResultDto restResponseDto = RestResultDto.failed(status.value(), REST_TEMPLATE_ERROR, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResponseDto, headers, status, request);
    }

    /**
     * restTemplate 其他异常 403
     */
    private ResponseEntity<Object> handleRestClientException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("======RestClientException======:", ex);
        String errorMessage = messageSource.getMessage(TOO_MANY_REQUESTS, null, LocaleContextHolder.getLocale());
        RestResultDto restResponseDto = RestResultDto.failed(status.value(), REST_TEMPLATE_ERROR, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResponseDto, headers, status, request);
    }

    /**
     * 数据库异常 403
     */
    private ResponseEntity<Object> handleNoSuchElementException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(DB_NOT_FOUND, null, LocaleContextHolder.getLocale());
        RestResultDto restResponseDto = RestResultDto.failed(status.value(), DB_NOT_FOUND, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResponseDto, headers, status, request);
    }

    /**
     * oauth2 用户权限异常 401/403
     */
    private ResponseEntity<Object> handleOAuth2Exception(Exception ex, HttpHeaders headers, WebRequest request) {
        OAuth2Exception oAuth2Exception = (OAuth2Exception) ex;
        int realHttpStatus = oAuth2Exception.getHttpErrorCode();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        if (realHttpStatus == HttpStatus.UNAUTHORIZED.value() || (oAuth2Exception instanceof InsufficientScopeException)) {
            headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, oAuth2Exception.getSummary()));
        }

        String errorMessage = messageSource.getMessage(oAuth2Exception.getOAuth2ErrorCode(), null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(realHttpStatus, oAuth2Exception.getOAuth2ErrorCode(), errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, HttpStatus.valueOf(realHttpStatus), request);
    }

    /**
     * oauth2 客户端权限异常 401
     */
    private ResponseEntity<Object> handleClientRegistrationException(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = messageSource.getMessage(INVALID_CLIENT, null, LocaleContextHolder.getLocale());
        RestResultDto restResultDto = RestResultDto.failed(status.value(), INVALID_CLIENT, errorMessage, ex.getMessage());
        return handleExceptionInternal(ex, restResultDto, headers, status, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @NonNull RestResultDto restResultDto, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String[]> requestHeader = new LinkedHashMap<>();
        Iterator<String> headerNames = request.getHeaderNames();
        while (headerNames.hasNext()) {
            String headerName = headerNames.next();
            String[] headerValues = request.getHeaderValues(headerName);
            requestHeader.put(headerName, headerValues);
        }
        HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();

        log.error("======client ip: [{}]======", NetworkUtilPlus.getIpAddress(httpServletRequest));
        log.error("======request : {} with {}======", request, ex.getClass().getSimpleName());
        log.error("======request header : {}======", requestHeader);
        log.error("======request param : {}======", request.getParameterMap());
        log.error("======Global error catch: statusCode[{}], errorCode[{}], errorMessage[{}], errorUrl[{}], currUserId[{}]======"
                , HttpStatus.INTERNAL_SERVER_ERROR.value()
                , restResultDto.getErrorCode()
                , restResultDto.getErrorMessage()
                , httpServletRequest.getRequestURI()
                , CurrentUserUtils.currentUserIdWithNull()
                , ex);

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        restResultDto.setErrorPath(httpServletRequest.getRequestURI());
        if(showRealError){
            restResultDto.setErrorMessage(restResultDto.getRealErrorMessage());
        }
        return new ResponseEntity<>(restResultDto, headers, status);
    }

    private Throwable getRootCause(Throwable exception) {
        if (exception.getCause() != null) {
            return getRootCause(exception.getCause());
        }
        return exception;
    }
}
