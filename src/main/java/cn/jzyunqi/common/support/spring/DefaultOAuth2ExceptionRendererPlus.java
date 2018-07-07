package cn.jzyunqi.common.support.spring;

import cn.jzyunqi.common.model.RestResultDto;
import cn.jzyunqi.common.utils.NetworkUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.http.converter.jaxb.JaxbOAuth2ExceptionMessageConverter;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
@Slf4j
public class DefaultOAuth2ExceptionRendererPlus implements OAuth2ExceptionRenderer {

    private List<HttpMessageConverter<?>> messageConverters = geDefaultMessageConverters();

    private MessageSource messageSource;

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
        if (responseEntity == null) {
            return;
        }
        HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
        HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
        if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
            ((ServerHttpResponse) outputMessage).setStatusCode(((ResponseEntity<?>) responseEntity).getStatusCode());
        }
        HttpHeaders entityHeaders = responseEntity.getHeaders();
        if (!entityHeaders.isEmpty()) {
            outputMessage.getHeaders().putAll(entityHeaders);
        }
        OAuth2Exception body = (OAuth2Exception) responseEntity.getBody();
        if (body != null) {
            Map<String, String[]> requestHeader = new LinkedHashMap<>();
            Iterator<String> headerNames = webRequest.getHeaderNames();
            while (headerNames.hasNext()) {
                String headerName = headerNames.next();
                String[] headerValues = webRequest.getHeaderValues(headerName);
                requestHeader.put(headerName, headerValues);
            }
            HttpServletRequest httpServletRequest = webRequest.getRequest();
            log.error("======client ip: [{}]======", NetworkUtilPlus.getIpAddress(httpServletRequest));
            log.error("======request header : {}======", requestHeader);
            log.error("======request param : {}======", webRequest.getParameterMap());
            log.error("======OAuth2 error catch: statusCode[{}], errorCode[{}], errorMessage[{}], errorUrl[{}]======"
                    , body.getHttpErrorCode()
                    , body.getOAuth2ErrorCode()
                    , body.getLocalizedMessage()
                    , webRequest.getRequest().getRequestURI());
            String errorMessage;
            if(messageSource == null){
                errorMessage = body.getLocalizedMessage();
            }else{
                errorMessage = messageSource.getMessage(body.getOAuth2ErrorCode(), null, body.getLocalizedMessage(), LocaleContextHolder.getLocale());
            }
            RestResultDto restResponseDto = RestResultDto.failed(body.getHttpErrorCode(), webRequest.getRequest().getRequestURI(), body.getOAuth2ErrorCode(), errorMessage, body.getLocalizedMessage());
            writeWithMessageConverters(restResponseDto, inputMessage, outputMessage);
        } else {
            // flush headers
            outputMessage.getBody();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage,
                                            HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
        List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
        if (acceptedMediaTypes.isEmpty()) {
            acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
        }
        MediaType.sortByQualityValue(acceptedMediaTypes);
        Class<?> returnValueType = returnValue.getClass();
        List<MediaType> allSupportedMediaTypes = new ArrayList<>();
        for (MediaType acceptedMediaType : acceptedMediaTypes) {
            for (HttpMessageConverter messageConverter : messageConverters) {
                if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
                    messageConverter.write(returnValue, acceptedMediaType, outputMessage);
                    if (log.isDebugEnabled()) {
                        MediaType contentType = outputMessage.getHeaders().getContentType();
                        if (contentType == null) {
                            contentType = acceptedMediaType;
                        }
                        log.debug("Written [" + returnValue + "] as \"" + contentType + "\" using ["
                                + messageConverter + "]");
                    }
                    return;
                }
            }
        }
        for (HttpMessageConverter messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
    }

    private List<HttpMessageConverter<?>> geDefaultMessageConverters() {
        List<HttpMessageConverter<?>> result = new ArrayList<>();
        result.addAll(new RestTemplate().getMessageConverters());
        result.add(new JaxbOAuth2ExceptionMessageConverter());
        return result;
    }

    private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
        return new ServletServerHttpResponse(servletResponse);
    }
}
