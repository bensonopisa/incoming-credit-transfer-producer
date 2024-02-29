package ke.co.pesalink.papss.incoming.credittransferproducerservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.asm.TypeReference;
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
}