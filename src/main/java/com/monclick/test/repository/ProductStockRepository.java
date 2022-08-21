package com.monclick.test.repository;

import java.math.BigInteger;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.monclick.test.model.ProductStock;

import reactor.core.publisher.Flux;

public interface ProductStockRepository extends ReactiveCrudRepository<ProductStock,BigInteger> {

	public Flux<ProductStock> getByProductSku(String productSku);
	
}
