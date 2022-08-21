package com.monclick.test.repository;


import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

import com.monclick.test.model.ProductDAO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProductRepository extends ReactiveSortingRepository<ProductDAO, String> {

	@Query("SELECT a.SKU, a.MPN, CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,a.VAT_VALUE,b.CODE, b.DESCRIPTION FROM PRODUCT a JOIN CATEGORY b ON a.category_code = b.code LIMIT :limit OFFSET :offset ")
	public Flux<ProductDAO> findAllBy(long offset,int limit);
	
	@Query("SELECT DISTINCT a.SKU, a.MPN, CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,a.VAT_VALUE,b.CODE, b.DESCRIPTION FROM PRODUCT a JOIN CATEGORY b ON a.category_code = b.code WHERE UPPER(a.category_code) IN (:test) LIMIT :limit OFFSET :offset ")
	public Flux<ProductDAO> findAllByCategoryCodeList(long offset,int limit,@Param("test") Collection<String> test);
	
	
	public Flux<ProductDAO> findAllBy(Pageable pageable);
	
	@Query("SELECT a.SKU, a.MPN, CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,a.VAT_VALUE,b.CODE, b.DESCRIPTION FROM PRODUCT a JOIN CATEGORY b ON a.category_code = b.code where a.SKU = :sku ")
	public Mono<ProductDAO> findById(String sku);
	
	
	
}
