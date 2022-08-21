package com.monclick.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Table(name = "PRODUCT")
@AllArgsConstructor
@Builder
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	private String sku;

	private String mpn;
	
	private BigDecimal streetPriceVat;
	
	private Double vatValue;
	
	@Transient
	@Builder.Default private Category category = null;
	
}
