package ru.sam47kon.kd.emailnotification.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.sam47kon.kd.core.event.ProductCreateEvent;
import ru.sam47kon.kd.emailnotification.exception.NonRetryableException;
import ru.sam47kon.kd.emailnotification.exception.RetryableException;

@Slf4j
@Component
@KafkaListener(topics = "#{'${product-created-events-topic}'.split(',')}", groupId = "${spring.kafka.consumer.group-id}")
public class ProductCreateEventHandler {

	private static final String URL = "http://localhost:8090/response/";

	private final RestTemplate restTemplate;

	public ProductCreateEventHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@KafkaHandler
	public void handle(@NotNull ProductCreateEvent productCreateEvent) {
		log.info("productCreateEvent: productId: {}, tittle: {}", productCreateEvent.getProductId(), productCreateEvent.getTittle());

		try {
			ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(productCreateEvent), String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("Вернулся response: {}", response.getBody());
			} else {
				throw new NonRetryableException(String.format("Вернулся status code: %s, body: %s", response.getStatusCode(), response.getBody()));
			}
		} catch (ResourceAccessException e) {
			log.error("ResourceAccessException", e);
			throw new RetryableException(e);
		} catch (HttpServerErrorException e) {
			log.error("HttpServerErrorException", e);
			throw new NonRetryableException(e);
		} catch (Exception e) {
			log.error("Exception", e);
			throw new NonRetryableException(e);
		}
	}
}
