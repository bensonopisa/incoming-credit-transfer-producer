package ke.co.pesalink.papss.incoming.credittransferproducerservice.interceptors;

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

@Component
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    public transient final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.debug("--------------- Request headers    {}", request.getHeaders());
        logger.debug("--------------- Request method     {}", request.getMethod());
        logger.debug("--------------- Request URL        {}", request.getURI());

        ClientHttpResponse clientHttpResponse = execution.execute(request, body);

        String response = StreamUtils.copyToString(clientHttpResponse.getBody(), Charset.defaultCharset());
        logger.debug("--------------- Response body      {}", response);
        logger.debug("--------------- Response headers   {}", clientHttpResponse.getHeaders());
        logger.debug("--------------- Response code      {}", clientHttpResponse.getStatusCode());
        logger.debug("--------------- Response message   {}", clientHttpResponse.getStatusText());

        return clientHttpResponse;
    }
}
