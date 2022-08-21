package com.monclick.test.common;

import java.math.BigDecimal;
import java.util.function.Function;

import com.monclick.test.model.Category;
import com.monclick.test.model.ProductDAO;
import com.monclick.test.model.ProductCompleteDTO;

import io.r2dbc.spi.Row;

public class Commons {

	
	public final static Function<Row,ProductCompleteDTO> READ_PRODUCT_COMPLETE_ROW = source-> {
		BigDecimal streetPrice = source.get("STREET_PRICE_VAT",BigDecimal.class);
		Category b = null; 
		if(source.getMetadata().contains("DESCRIPTION") && source.getMetadata().contains("CODE")) {
			b = Category.builder().description(source.get("DESCRIPTION", String.class)).code(source.get("CODE",String.class)).build();
		}
		BigDecimal price = null; 
		if(source.getMetadata().contains("PRICE")) {
			price = source.get("PRICE", BigDecimal.class);
		}
		String warehouseCode = null;
		if(source.getMetadata().contains("SUPPLIER_WAREHOUSE_CODE")) {
			warehouseCode = source.get("SUPPLIER_WAREHOUSE_CODE", String.class);	
		}
		Long quantity = null;
		if(source.getMetadata().contains("QUANTITY")) {
			quantity = source.get("QUANTITY", Long.class);
		}
		String completeProductId = null;
		if(source.getMetadata().contains("COMPLETE_PRODUCT_ID")) {
			completeProductId = source.get("COMPLETE_PRODUCT_ID",String.class);
		}
		
		BigDecimal finalPrice = null;
		if(source.getMetadata().contains("FINAL_PRICE")) {
			finalPrice = source.get("FINAL_PRICE",BigDecimal.class);
		}
		
		BigDecimal salesStreetPriceVat = null;
		if(source.getMetadata().contains("SALES_STREET_PRICE_VAT")) {
			salesStreetPriceVat = source.get("SALES_STREET_PRICE_VAT",BigDecimal.class);
		}
		String promotion = null;
		if(source.getMetadata().contains("PROMOTION")) {
			promotion = source.get("PROMOTION", String.class);
		}
		java.time.LocalDateTime dateFrom = null;
		if(source.getMetadata().contains("DATE_FROM")) {
			dateFrom= source.get("DATE_FROM", java.time.LocalDateTime.class);
		}
		java.time.LocalDateTime dateTo = null;
		if(source.getMetadata().contains("DATE_TO")) {
			dateTo= source.get("DATE_TO", java.time.LocalDateTime.class);
		}
		Boolean isActive = null;
		if(source.getMetadata().contains("IS_ACTIVE")) {
			isActive = source.get("IS_ACTIVE",Boolean.class);
		}
		
		return ProductCompleteDTO.productCompleteBuilder()
		.sku(source.get("SKU",String.class))
		.mpn(source.get("MPN",String.class))
		.streetPriceVat(streetPrice)
		.vatValue(source.get("VAT_VALUE",Double.class))
		.category(b)
		.price(price)
		.warehouseCode(warehouseCode)
		.quantity(quantity)
		.completeProductId(completeProductId)
		.finalPrice(finalPrice)
		.salesStreetPriceVat(salesStreetPriceVat)
		.promotion(promotion)
		.dateFrom(dateFrom)
		.dateTo(dateTo)
		.isActive(isActive)
		.build();
		
	} ;

	public final static Function<Row,ProductDAO> READ_PRODUCT_ROW = source-> {
		Category b = null; 
		BigDecimal streetPrice = null;
		
		if(source.getMetadata().contains("DESCRIPTION") && source.getMetadata().contains("CODE")) {
			b = Category.builder().description(source.get("DESCRIPTION", String.class)).code(source.get("CODE",String.class)).build();
			streetPrice = source.get("STREET_PRICE_VAT",BigDecimal.class);
		}else if(source.getMetadata().contains("STREET_PRICE_VAT")  ) {
			Double toInsert =source.get("STREET_PRICE_VAT",Double.class);
			streetPrice = toInsert !=null ? BigDecimal.valueOf(toInsert): null;
		}
		
		return ProductDAO.productBuilder()
				.sku(source.get("SKU",String.class))
				.mpn(source.get("MPN",String.class))
				.streetPriceVat(streetPrice)
				.vatValue(source.get("VAT_VALUE",Double.class))
				.category(b)
				.build();
	};
}
