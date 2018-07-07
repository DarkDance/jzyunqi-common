package cn.jzyunqi.common.support.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class DefaultResponseErrorHandlerPlus extends DefaultResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResponseErrorHandlerPlus.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            super.handleError(response);
        } catch (RestClientResponseException e) {
            LOGGER.error("==RestClientResponseException header[{}]==", e.getResponseHeaders());
            LOGGER.error("==RestClientResponseException body[{}]==", e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            LOGGER.error("==other exception==:", e);
            throw e;
        }
    }
}
