package com.monclick.test.controller;


import javax.servlet.http.HttpServletRequest;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.monclick.test.model.ProductCompleteDTO;
import com.monclick.test.model.filter.request.ProductFilter;
import com.monclick.test.service.BusinessLogicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/products")
public class ProductController {

	 
	 @Autowired
	 BusinessLogicService bls;

	
	 @Operation(responses = @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCompleteDTO.class)))))
	 @GetMapping
	 public  Flux<ProductCompleteDTO> findAllProductsSingleAccess(@ParameterObject Pageable pageable,@ParameterObject @ModelAttribute("productFilter") ProductFilter productFilter) {
		 return bls.filterProductComplete(productFilter, pageable);
	 }
	 
	 @Operation(responses = @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCompleteDTO.class)))))
	 @RequestMapping(value= "{id}/**" , method = RequestMethod.GET)
	 public  Flux<ProductCompleteDTO> findSingleProduct(@PathVariable("id") String sku,HttpServletRequest request) {
		    String urlTail = new AntPathMatcher()
		            .extractPathWithinPattern( "/{id}/**", request.getRequestURI() );
		 return bls.findSingleProduct(urlTail);
	 }	 
		 
	 @Operation(responses = @ApiResponse(description = "A Mono with number of affected rows", content = @Content(schema = @Schema(implementation = Integer.class))))
	 @RequestMapping(value= "/create_default_list" , method = RequestMethod.PUT)
	 public  Mono<Integer> findSingleProduct() {
		 return bls.createSalesList();
	 }
	 
	 
}
