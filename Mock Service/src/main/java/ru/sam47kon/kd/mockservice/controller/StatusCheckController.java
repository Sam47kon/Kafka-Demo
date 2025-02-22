package ru.sam47kon.kd.mockservice.controller;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sam47kon.kd.core.event.ProductCreateEvent;

@Slf4j
@RestController
@RequestMapping("/response")
public class StatusCheckController {

	@PostMapping("/")
	ResponseEntity<String> response(@Nullable RequestEntity<ProductCreateEvent> request) {
		if (null == request) {
			log.error("Возвращаем 500. RequestEntity пустой");
			return ResponseEntity.internalServerError().build();
		}

		ProductCreateEvent productCreateEvent = request.getBody();
		if (productCreateEvent == null) {
			log.error("Возвращаем 400. Body пустой");
			return ResponseEntity.badRequest().build();
		}

		if (productCreateEvent.checkPrice()) {
			log.error("Возвращаем 400. Проверка цены не прошла");
			return ResponseEntity.badRequest().build();
		}

		log.info("Возвращаем 200");
		return ResponseEntity.ok().body("Всё окей");
	}
}
