package com.monclick.test.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monclick.test.converter.ProductReadConverter;

import io.r2dbc.spi.ConnectionFactory;
@Configuration
public class DbConnectionConfiguration  extends AbstractR2dbcConfiguration {

	@Bean
	public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
	    var dialect = DialectResolver.getDialect(connectionFactory);
	    var converters = List.of(new ProductReadConverter());
	    return R2dbcCustomConversions.of(dialect, converters);
	}

	@Override
	public ConnectionFactory connectionFactory() {
		// TODO Auto-generated method stub
		return databaseClient().getConnectionFactory();
	}

	
}

