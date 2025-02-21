package ru.sam47kon.kd.product.service;

import ru.sam47kon.kd.product.service.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {

	String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;
}
