package com.monclick.test.model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;





@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "PRODUCT")
public class ProductDAO extends Product implements Serializable {
	
	private static final long serialVersionUID = 1L;

	
	@Transient
	List<ProductPurchasePriceList> productPurchasePriceList = null;
	
	
	@Transient
	private List<ProductStock> productStock= null;
	

	@Builder(builderMethodName = "productBuilder")
	public ProductDAO(
			String sku, String mpn, BigDecimal streetPriceVat
			, Double vatValue, Category category,List<ProductPurchasePriceList> productPurchasePriceList,List<ProductStock> productStock) {
		super(sku, mpn, streetPriceVat, vatValue, category);
		this.productStock = productStock;
		this.productPurchasePriceList = productPurchasePriceList;
	}
	
	public ProductDAO withProductPurchasePriceList(List<ProductPurchasePriceList> list) {
		return ProductDAO.productBuilder()
				.sku(getSku())
				.mpn(getMpn())
				.category(getCategory())
				.productPurchasePriceList(list)
				.productStock(getProductStock()).build();		
	}
	
	public ProductDAO withProductStock(List<ProductStock> list) {
		return ProductDAO.productBuilder()
				.sku(getSku())
				.mpn(getMpn())
				.category(getCategory())
				.productPurchasePriceList(getProductPurchasePriceList())
				.productStock(list).build();
			
	}

}
