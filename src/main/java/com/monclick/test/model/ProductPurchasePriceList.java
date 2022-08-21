package com.monclick.test.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Table("PRODUCT_PURCHASE_PRICE_LIST")
public class ProductPurchasePriceList {

	@Id
	BigInteger id;
	
	BigDecimal price;
	
	String productSku;
	
	String supplierWarehouseCode;
	
}
