package ke.co.pesalink.papss.incoming.credittransferproducerservice.util;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.configs.AppConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class HttpUtil {
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    public ResponseEntity<Object> makeHttpCall(final String body, final String url, Map<String, String> additionalHeaders, HttpMethod method) throws RestClientException {
        HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
//        httpHeaders.set(appConfig.getRtpChannelHeaderKey(), appConfig.getRtpChannelHeaderValue());
//        httpHeaders.set(appConfig.getRtpVersionHeaderKey(), String.valueOf(appConfig.getRtpVersionHeaderValue()));

        if (!additionalHeaders.isEmpty()){
            for(Map.Entry<String,String> entry : additionalHeaders.entrySet()) {
                if(httpHeaders.containsKey(entry.getKey())) {
                    logger.debug("Found header {} configured, updating its value to {}", entry.getKey(), entry.getValue());
                    httpHeaders.set(entry.getKey(), entry.getValue());
                }else{
                    logger.debug("Appending header {}:{} to configured headers", entry.getKey(), entry.getValue());
                    httpHeaders.add(entry.getKey(), entry.getValue());
                }
            }
        }

        HttpEntity<String> httpEntity;

        // optionally create the httpEntity based on whether the body exist or not
        if (body == null) {
            httpEntity = new HttpEntity<>(httpHeaders);
        }else {
            httpEntity = new HttpEntity<>(body, httpHeaders);
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .build().toUri();

        return restTemplate.exchange(uri, method, httpEntity, Object.class);
    }
}
