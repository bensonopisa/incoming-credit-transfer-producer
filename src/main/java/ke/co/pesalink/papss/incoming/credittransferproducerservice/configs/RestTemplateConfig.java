package ke.co.pesalink.papss.incoming.credittransferproducerservice.configs;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.interceptor.RequestResponseLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * Rest client configurations
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);
    private final RequestResponseLoggingInterceptor requestResponseLoggingInterceptor;

    @Bean
    RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.setInterceptors(List.of(requestResponseLoggingInterceptor));
        return restTemplate;
    }

    @Bean
    ClientHttpRequestFactory clientHttpRequestFactory(HttpClient closeableHttpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
        requestFactory.setConnectionRequestTimeout(appConfig.getPapssConnectTimeout());

        return requestFactory;
    }

    @Bean
    HttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager) {
        return HttpClientBuilder
                .create()
                .setDefaultRequestConfig(requestConfig())
                .setConnectionManager(httpClientConnectionManager)
                .build();
    }
    @Bean
    HttpClientConnectionManager httpClientConnectionManager(SSLConnectionSocketFactory socketFactory) {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(100)
                .setSSLSocketFactory(socketFactory)
                .build();
    }

    @Bean
    SSLConnectionSocketFactory sslConnectionSocketFactory() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext())
                .setTlsVersions(TLS.V_1_2)
                .build();
    }

    RequestConfig requestConfig() {
            return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(appConfig.getPapssConnectTimeout()))
                .setResponseTimeout(Timeout.of(appConfig.getPapssReadTimeout()))
                .build();
    }
    SSLContext sslContext() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final char[] keyStorePassword = appConfig.getPapssKeyStorePassword().toCharArray();

        return SSLContextBuilder.create()
                .loadKeyMaterial(keyStore(keyStorePassword), keyStorePassword)
               .build();
    }

    KeyStore keyStore(char[] keyStorePassword) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream is = getClass().getClassLoader().getResourceAsStream("client.p12");
            keyStore.load(is, keyStorePassword);
        }catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            logger.warn("Failed to read keystore file");
        }
        return keyStore;
    }
}
