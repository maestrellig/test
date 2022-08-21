package com.monclick.test.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.monclick.test.common.Commons;
import com.monclick.test.model.ProductDAO;

import io.r2dbc.spi.Row;

@ReadingConverter
public class ProductReadConverter implements Converter<Row,ProductDAO> {

	@Override
	public ProductDAO convert(Row source) {
		return Commons.READ_PRODUCT_ROW.apply(source);		
	}

	

}
