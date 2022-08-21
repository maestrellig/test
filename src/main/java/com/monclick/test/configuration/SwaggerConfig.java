package com.monclick.test.configuration;


import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Spring webflux", version = "1.0",description = "Test"))	
public class SwaggerConfig {

	

}
