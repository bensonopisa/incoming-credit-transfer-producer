package ke.co.pesalink.papss.incoming.credittransferproducerservice;

import aj.org.objectweb.asm.TypeReference;
import ke.co.pesalink.papss.incoming.credittransferproducerservice.service.PollingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class CreditTransferProducerServiceApplication{
	public static void main(String[] args) {
		SpringApplication.run(CreditTransferProducerServiceApplication.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(RestTemplate restTemplate) {
		return args -> {

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

			ResponseEntity<Object> responseEntity = restTemplate.exchange(URI.create("https://jsonplaceholder.typicode.com"), HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<>() {
			});

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				log.debug("Received a response successfully");
				log.info("Response received {}", responseEntity.getBody());
			}

			log.error("Failed to make the http call");
		};
	}
}