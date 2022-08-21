package com.monclick.test.model;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Table("PRODUCT_STOCK")
public class ProductStock {

	@Id
	BigInteger id;
	
	Integer quantity;
	
	String productSku;
	
	String supplierWarehouseCode;
	
}
