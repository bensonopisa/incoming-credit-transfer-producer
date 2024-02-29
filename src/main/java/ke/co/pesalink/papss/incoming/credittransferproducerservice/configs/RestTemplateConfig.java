package ke.co.pesalink.papss.incoming.credittransferproducerservice.configs;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.interceptors.RequestLoggingInterceptor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    private final String KEYSTORE_PASSWORD="secure";

    private final Resource certificatex  = new ClassPathResource("client.p12");


    @Bean
    public RestTemplate createRestTemplateSslWithPooling() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {

        if (!certificatex.exists()) throw new IOException("Certificate path cannot be resolved. Path={}"+ certificatex.getURL());

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(certificatex.getURL(), KEYSTORE_PASSWORD.toCharArray())
                .build();

        SSLConnectionSocketFactory socketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setTlsVersions(TLS.V_1_2)
                .build();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create()
                .register("http", socketFactory)
                .build();
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnManager.setMaxTotal(100);
        poolingConnManager.setDefaultMaxPerRoute(50);

        RequestConfig requestConfig  = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(30))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .build();


        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolingConnManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate =  new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(new RequestLoggingInterceptor()));
        return restTemplate;
    }
}
