package ke.co.pesalink.papss.incoming.credittransferproducerservice.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Class used to logger request and responses from papss
 */
@Component
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.info("===========================request begin================================================");
        logger.info("URI         : {}", request.getURI());
        logger.info("Method      : {}", request.getMethod());
        logger.info("Headers     : {}", request.getHeaders());
        logger.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        logger.info("==========================request end================================================");

        ClientHttpResponse response = execution.execute(request, body);

        logger.debug("============================response begin==========================================");
        logger.debug("Status code  : {}", response.getStatusCode());
        logger.debug("Status text  : {}", response.getStatusText());
        logger.debug("Headers      : {}", response.getHeaders());
        logger.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        logger.debug("=======================response end=================================================");

        return response;
    }
}
