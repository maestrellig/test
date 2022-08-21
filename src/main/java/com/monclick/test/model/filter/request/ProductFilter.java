package com.monclick.test.model.filter.request;

import java.io.Serializable;
import java.util.Collection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFilter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean onlyWithStock;
	private Collection<String> categoryCode;
	private String supplierWarhouse;
}
