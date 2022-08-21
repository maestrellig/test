package com.monclick.test.repository;

import java.math.BigInteger;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.monclick.test.model.ProductPurchasePriceList;

import reactor.core.publisher.Flux;

public interface ProductPurchaseRepository extends ReactiveCrudRepository<ProductPurchasePriceList,BigInteger> {

	public Flux<ProductPurchasePriceList> getByProductSku(String productSku);
	
}
