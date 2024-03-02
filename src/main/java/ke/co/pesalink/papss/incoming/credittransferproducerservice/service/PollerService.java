package ke.co.pesalink.papss.incoming.credittransferproducerservice.service;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.configs.AppConfig;
import ke.co.pesalink.papss.incoming.credittransferproducerservice.exceptions.XmlParseException;
import ke.co.pesalink.papss.incoming.credittransferproducerservice.util.KeyValueSelector;
import ke.co.pesalink.papss.incoming.credittransferproducerservice.util.XmlUtils;
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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
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
                logger.info("Message Type {}", messageType);

                String responseBody = response.getBody();

                logger.info("Response body {}", responseBody);

                boolean valid = false;
                XmlUtils xmlUtils = new XmlUtils();
                try {
                    valid = xmlUtils.validateXMLSignature(responseBody);
                }catch(Exception ex) {
                    logger.error("Error encountered while validating the message signature");
                    // decide what to do here...
                }

                if (!valid) {
                    // what do I do with this status
                }

                logger.info("Request signature validation is successful");
                routeToQueue(responseBody, messageType);
            }
        } catch (ResourceAccessException resourceAccessException) {
            if (resourceAccessException.getCause() instanceof SocketException socketTimeoutException) {
                logger.warn("Socket timeout.{}", socketTimeoutException.getLocalizedMessage());
            }
            if (resourceAccessException.getCause() instanceof ConnectException connectException) {
                logger.warn("Connection timeout: {}", connectException.getLocalizedMessage());
            }
        }
    }


    private void routeToQueue(String body, String messageType) {

    }
}

