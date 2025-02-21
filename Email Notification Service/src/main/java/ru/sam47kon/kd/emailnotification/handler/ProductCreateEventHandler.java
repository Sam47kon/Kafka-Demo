package ru.sam47kon.kd.emailnotification.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.sam47kon.kd.core.event.ProductCreateEvent;

@Slf4j
@Component
@KafkaListener(topics = "#{'${product-created-events-topic}'.split(',')}")
public class ProductCreateEventHandler {

	@KafkaHandler
	public void handle(@NotNull ProductCreateEvent productCreateEvent) {
		log.info("productCreateEvent: {}", productCreateEvent.getTittle());
	}
}
