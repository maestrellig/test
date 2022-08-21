package com.monclick.test.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.monclick.test.common.Commons;
import com.monclick.test.model.ProductCompleteDTO;

import io.r2dbc.spi.Row;

@ReadingConverter
public class ProductCompleteReadConverter implements Converter<Row,ProductCompleteDTO> {

	@Override
	public ProductCompleteDTO convert(Row source) {
		return Commons.READ_PRODUCT_COMPLETE_ROW.apply(source);		
	}

	

}
