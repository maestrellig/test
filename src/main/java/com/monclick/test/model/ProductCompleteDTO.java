package com.monclick.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "PRODUCT")
@JsonInclude(JsonInclude.Include.NON_NULL )
public class ProductCompleteDTO  extends Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal price;
	private String warehouseCode;
	private Long quantity;
	private String completeProductId;
	private BigDecimal finalPrice;
	private BigDecimal salesStreetPriceVat;
	private String promotion;
	private java.time.LocalDateTime dateFrom;
	private java.time.LocalDateTime dateTo;
	private Boolean isActive;
	
	
	@Builder(builderMethodName = "productCompleteBuilder")
	public ProductCompleteDTO(
			String sku, String mpn, BigDecimal streetPriceVat
			, Double vatValue, Category category,BigDecimal price
			,String warehouseCode,Long quantity,String completeProductId
			,BigDecimal finalPrice,BigDecimal salesStreetPriceVat
			,String promotion, java.time.LocalDateTime dateFrom
			,java.time.LocalDateTime dateTo, Boolean isActive
			) {
		super(sku, mpn, streetPriceVat, vatValue, category);
		this.price = price;
		this.warehouseCode = warehouseCode;
		this.quantity = quantity;
		this.completeProductId=completeProductId;
		this.finalPrice = finalPrice;
		this.salesStreetPriceVat = salesStreetPriceVat;
		this.promotion = promotion;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.isActive = isActive;
	}


	
}
