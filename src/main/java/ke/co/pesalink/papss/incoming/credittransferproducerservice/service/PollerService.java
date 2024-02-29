package ke.co.pesalink.papss.incoming.credittransferproducerservice.service;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.configs.AppConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class PollerService implements Runnable {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(PollerService.class);

    @Override
    public void run() {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();

            httpHeaders.set("X-PAPSSRTRP-Channel", "LR2020");
            httpHeaders.set("X-PAPSS-RTP-Version", "1");

            HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

            URI uri = UriComponentsBuilder.fromHttpUrl(appConfig.getBaseUrl())
                    .path(appConfig.getMessagePath())
                    .build().toUri();

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);


            if (response.getStatusCode().is2xxSuccessful()) {
                HttpHeaders responseHeaders = response.getHeaders();

                final String messageSequenceId = responseHeaders.getFirst("X-PAPSSRTP-MessageSeq");
                logger.info("Message Sequence number {}", messageSequenceId);

                final String messageType = responseHeaders.getFirst("X-PAPSSRTP-MessageType");
                logger.info("Message Type            {}", messageType);

                String responseBody;

                if (response.hasBody()) {
                    responseBody = response.getBody();
                    logger.info("Received response body {}", responseBody);
                }
                // validate request signature

                // switch  message type, build a document and sent to appropriate queue
            }
        }catch(ResourceAccessException resourceAccessException) {
            if (resourceAccessException.getCause() instanceof SocketException socketTimeoutException) {
                logger.warn("Socket timeout.{}", socketTimeoutException.getLocalizedMessage());
            }
            if (resourceAccessException.getCause() instanceof ConnectException connectException) {
                logger.warn("Connection timeout: {}", connectException.getLocalizedMessage());
            }
        }
    }
}
