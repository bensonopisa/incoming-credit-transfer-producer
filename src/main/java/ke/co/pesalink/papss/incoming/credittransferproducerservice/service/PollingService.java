package ke.co.pesalink.papss.incoming.credittransferproducerservice.service;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.configs.AppConfig;
import ke.co.pesalink.papss.incoming.credittransferproducerservice.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Service
public class PollingService implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(PollingService.class);
    private final AppConfig appConfig;

    private final HttpUtil httpUtil;

    public PollingService(AppConfig appConfig, RestTemplate restTemplate) {
        this.appConfig = appConfig;
        this.httpUtil = new HttpUtil(restTemplate, appConfig);
    }

    @Override
    public void run() {
        logger.info("About to make a poll request for message");
        try {
            ResponseEntity<Object> response = httpUtil.makeHttpCall(null, appConfig.getFetchMessageUrl(), Collections.emptyMap(), HttpMethod.GET);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Request was successful");
                logger.info("Response: {}", response.getBody());
            }
        }catch(RestClientException restClientException) {
            logger.error("Exception occurred", restClientException.getCause());
        }
    }
}
