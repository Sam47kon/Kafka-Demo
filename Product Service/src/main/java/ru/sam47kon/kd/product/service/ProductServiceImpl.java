package ru.sam47kon.kd.product.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.sam47kon.kd.core.event.ProductCreateEvent;
import ru.sam47kon.kd.product.service.dto.CreateProductDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final String topic;

	// KafkaTemplate для продюсера
	private final KafkaTemplate<String, ProductCreateEvent> kafkaTemplate;

	public ProductServiceImpl(KafkaTemplate<String, ProductCreateEvent> kafkaTemplate, @Value("${product-created-events-topic}") String topicName) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topicName;
	}

	@Override
	public String createProduct(@NotNull CreateProductDto createProductDto) throws ExecutionException, InterruptedException {
		// Сохраняем в БД ...
		// Возвращаем идентификатор из БД (эмуляция)
		String productId = UUID.randomUUID().toString();

		ProductCreateEvent productCreateEvent = new ProductCreateEvent(productId, createProductDto.getTittle(), createProductDto.getPrice(), createProductDto.getQuantity());

		//asynchronouslySend(productId, productCreateEvent);

		// Синхронно отправляем событие
		SendResult<String, ProductCreateEvent> result = kafkaTemplate.send(topic, productId, productCreateEvent).get();

		log.info("productCreateEvent: topic {}, partition {}, offset {}",
				result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());


		return productId;
	}

	private void asynchronouslySend(String productId, ProductCreateEvent productCreateEvent) {
		// Асинхронно отправляем событие
		CompletableFuture<SendResult<String, ProductCreateEvent>> future = kafkaTemplate.send(topic, productId, productCreateEvent);

		// Обработка результата
		future.whenComplete((sendResult, throwable) -> {
			if (throwable != null) {
				// Обработка ошибки
				log.error("Ошибка отправки productCreateEvent: {}", throwable.getMessage());
			} else {
				// Обработка успешного результата
				log.info("productCreateEvent отправлен успешно: {}, {}", sendResult.getRecordMetadata(), sendResult.getProducerRecord().value());
			}
		});

		// Ожидаем завершения отправки события, но это не гуд практика
		// future.join();
	}
}
