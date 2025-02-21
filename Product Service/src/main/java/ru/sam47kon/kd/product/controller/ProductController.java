package ru.sam47kon.kd.product.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sam47kon.kd.product.service.ProductService;
import ru.sam47kon.kd.product.service.dto.CreateProductDto;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<Object> createProduct(@RequestBody CreateProductDto createProductDto) {
		String productId;
		try {
			productId = productService.createProduct(createProductDto);
		} catch (Exception e) {
			log.error("Произошла ошибка при создании продукта: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(new Date(), e.getMessage()));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(productId);
	}
}
