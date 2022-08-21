package com.monclick.test.service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monclick.test.common.Commons;
import com.monclick.test.model.ProductCompleteDTO;
import com.monclick.test.model.filter.request.ProductFilter;
import com.monclick.test.model.internal.SQLParameterBinder;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class BusinessLogicService {

	
	private static final String UPSERTDEFAULTLIST = 
			"MERGE INTO PRODUCT_SALES_PRICE_LIST AS T USING  ( "
			 +"SELECT bb.SKU SKU "
			 +", CASE WHEN bb.FINAL_PRICE > bb.PURCHASE_PRICE THEN  bb.FINAL_PRICE "
			 +"ELSE CASE WHEN  bb.PURCHASE_PRICE * 1.03  > bb.STREET_PRICE THEN  bb.STREET_PRICE ELSE bb.PURCHASE_PRICE * 1.03 END   END FINAL_PRICE "
			 +",bb.FINAL_PRICE  INTERMEDIATE_PRICE_NO_VAT, bb.PURCHASE_PRICE,bb.STREET_PRICE_VAT / (1+NVL(VAT_VALUE,0)) STREET_PRICE_COMPUTED , bb.STREET_PRICE_VAT,bb.VAT_VALUE "
			 +"FROM  "
			 +"( "
			 +"SELECT a.SKU, a.MPN "
			 +", CAST( NVL(a.STREET_PRICE_VAT,0) AS NUMERIC(20,15) ) * ( "
			 +"CASE a.CATEGORY_CODE  "
			 +"WHEN 'FIT' THEN 0.93   "
			 +"WHEN 'FOT' THEN 0.95  "
			 +"WHEN 'GIO' THEN 0.97  "
			 +"WHEN 'HDD' THEN 0.96  "
			 +"WHEN 'HOM' THEN 0.82  "
			 +"WHEN 'LET' THEN 0.68  "
			 +"WHEN 'MON' THEN 0.60  "
			 +"WHEN 'NET' THEN 0.50 "
			 +"WHEN 'PCN' THEN 0.99 "
			 +"WHEN 'PIC' THEN 0.88 "
			 +"ELSE 0.9 END)/(1+NVL(a.VAT_VALUE,0)) AS FINAL_PRICE,CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15))/(1+NVL(a.VAT_VALUE,0)) STREET_PRICE ,CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,a.VAT_VALUE,a.CATEGORY_CODE CODE , MAX( CAST(c.PRICE AS NUMERIC(20,15))) PURCHASE_PRICE "
			 +" FROM PRODUCT a "
			 +" JOIN PRODUCT_PURCHASE_PRICE_LIST c ON a.sku = c.product_sku  "
			 +"GROUP BY a.SKU, a.MPN, a.STREET_PRICE_VAT,a.VAT_VALUE, a.CATEGORY_CODE ) bb )  S ON  T.PRODUCT_SKU = S.SKU "
			 +"WHEN MATCHED THEN "
			 +"UPDATE SET T.STREET_PRICE_VAT = S.STREET_PRICE_VAT , T.FINAL_PRICE = S.FINAL_PRICE "
			 +"WHEN NOT MATCHED THEN "
			 +"INSERT (PRODUCT_SKU,STREET_PRICE_VAT,FINAL_PRICE,IS_ACTIVE ) VALUES (S.SKU,S.STREET_PRICE_VAT, S.FINAL_PRICE,TRUE); ";
	
	
	private static final String EXTERNALQUERY =  "SELECT aa.SKU, aa.MPN, CAST(aa.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,aa.VAT_VALUE,ba.CODE, ba.DESCRIPTION"
			+ ", CAST(ca.PRICE AS NUMERIC(20,15)) PRICE, ca.SUPPLIER_WAREHOUSE_CODE , CAST(da.QUANTITY AS BIGINT) QUANTITY, aa.SKU || NVL2(ca.id,'_'||ca.id,'') || NVL2(da.id,'_'||da.id,'') || NVL2(ga.id,'_'||ga.id,'') COMPLETE_PRODUCT_ID "
			+ ", CAST(ga.STREET_PRICE_VAT AS NUMERIC(20,15)) SALES_STREET_PRICE_VAT , CAST(ga.FINAL_PRICE AS NUMERIC(20,15)) FINAL_PRICE, ga.PROMOTION, ga.DATE_FROM, ga.DATE_TO, ga.IS_ACTIVE "
			+ "FROM PRODUCT aa JOIN CATEGORY ba ON aa.category_code = ba.code "
			+ "LEFT JOIN PRODUCT_PURCHASE_PRICE_LIST ca ON aa.sku = ca.product_sku "
			+ "LEFT JOIN PRODUCT_SALES_PRICE_LIST ga ON aa.SKU = ga.PRODUCT_SKU "
			+ "LEFT JOIN PRODUCT_STOCK da  ON  (aa.SKU = da.PRODUCT_SKU and (ca.SUPPLIER_WAREHOUSE_CODE = da.SUPPLIER_WAREHOUSE_CODE OR ca.SUPPLIER_WAREHOUSE_CODE is null)) WHERE aa.SKU IN (%s) "
			+ "%s ";
	
	private static final String ALLPRODUCTS = "SELECT DISTINCT a.SKU "
			+ "FROM PRODUCT a JOIN CATEGORY b ON a.category_code = b.code "
			+ "LEFT JOIN PRODUCT_PURCHASE_PRICE_LIST c ON a.sku = c.product_sku "
			+ "LEFT JOIN PRODUCT_STOCK d  ON  (a.SKU = d.PRODUCT_SKU and (c.SUPPLIER_WAREHOUSE_CODE = d.SUPPLIER_WAREHOUSE_CODE OR c.SUPPLIER_WAREHOUSE_CODE is null)) %s %s ";
	
	private static final String SINGLEPRODUCT = "SELECT a.SKU, a.MPN, CAST(a.STREET_PRICE_VAT AS NUMERIC(20,15)) STREET_PRICE_VAT,a.VAT_VALUE,b.CODE, b.DESCRIPTION "
			+ ", CAST(c.PRICE AS NUMERIC(20,15)) PRICE, c.SUPPLIER_WAREHOUSE_CODE , CAST(d.QUANTITY AS BIGINT) QUANTITY, a.SKU || NVL2(c.id,'_'||c.id,'') || NVL2(d.id,'_'||d.id,'') COMPLETE_PRODUCT_ID "
			+ ", CAST(g.STREET_PRICE_VAT AS NUMERIC(20,15)) SALES_STREET_PRICE_VAT , CAST(g.FINAL_PRICE AS NUMERIC(20,15)) FINAL_PRICE, g.PROMOTION, g.DATE_FROM, g.DATE_TO, g.IS_ACTIVE "
			+ "FROM PRODUCT a JOIN CATEGORY b ON a.category_code = b.code "
			+ "LEFT JOIN PRODUCT_PURCHASE_PRICE_LIST c ON a.sku = c.product_sku "
			+ "LEFT JOIN PRODUCT_SALES_PRICE_LIST g ON a.SKU = g.PRODUCT_SKU "	
			+ "LEFT JOIN PRODUCT_STOCK d  ON  (a.SKU = d.PRODUCT_SKU and (c.SUPPLIER_WAREHOUSE_CODE = d.SUPPLIER_WAREHOUSE_CODE OR c.SUPPLIER_WAREHOUSE_CODE is null)) WHERE UPPER(a.SKU) = UPPER(:sku) ";
	
	private static final String ALLPRODUCTS_QUANTITY_WHERE_CLAUSE = "( d.QUANTITY IS NOT NULL AND d.QUANTITY <> 0 )";
	private static final String ALLPRODUCT_CATEGORY_WHERE_CLAUSE_INNER_CONDITION = "UPPER(b.CODE) = UPPER(:%s)";
	private static final String ALLPRODUCT_SUPPLYER_WAREHOUSE_INNER_CONDITION  = "UPPER(c.SUPPLIER_WAREHOUSE_CODE) = UPPER(:%s)";
	
	private DatabaseClient dbClient;
	
	@Autowired
	public BusinessLogicService(ConnectionFactory connectionFactory) {
		this.dbClient = DatabaseClient.create(connectionFactory);
	}
	
	
	public Flux<ProductCompleteDTO> filterProductComplete(ProductFilter filter,Pageable page ){
		List<String> toJoin = new ArrayList<>();
		List<SQLParameterBinder> sqlParameterBinderList = new ArrayList<>(); 
		AtomicInteger ai = new AtomicInteger();
		String orderByClause = "";
		
		if(page!= null && page.isPaged()) {
			orderByClause = "ORDER BY a.SKU ASC LIMIT :limit OFFSET :offset ";
			sqlParameterBinderList.add(SQLParameterBinder.builder().parameterName("offset").parameterValue(Long.valueOf(page.getOffset())).build());
			sqlParameterBinderList.add(SQLParameterBinder.builder().parameterName("limit").parameterValue(Integer.valueOf(page.getPageSize())).build());
		}
		if(filter!=null && Boolean.TRUE.equals(filter.getOnlyWithStock())) {
			toJoin.add(ALLPRODUCTS_QUANTITY_WHERE_CLAUSE);
		}
		if(filter!=null && filter.getCategoryCode()!=null && !filter.getCategoryCode().isEmpty()) {
			
			
			List<SQLParameterBinder> toAdd =  filter.getCategoryCode().stream().map(elem-> {
				String namedParam = String.format("code_%d",ai.getAndIncrement());
				return SQLParameterBinder.builder()
						.sqlString(String.format(ALLPRODUCT_CATEGORY_WHERE_CLAUSE_INNER_CONDITION, namedParam))
						.parameterName(namedParam).parameterValue(elem).build();
			}).collect(Collectors.toList());
			sqlParameterBinderList.addAll(toAdd) ;
			toJoin.add("( " + toAdd.stream().map(elem->elem.getSqlString()).collect(Collectors.joining(" OR "))+ ")") ;
		}
		if(filter!=null && filter.getSupplierWarhouse()!=null) {
			String paramName = "whcode";
			SQLParameterBinder whCode =  SQLParameterBinder.builder()
			.sqlString(String.format(ALLPRODUCT_SUPPLYER_WAREHOUSE_INNER_CONDITION, paramName))
			.parameterName(paramName).parameterValue(filter.getSupplierWarhouse()).build();
			toJoin.add(whCode.getSqlString());
			sqlParameterBinderList.add(whCode);
		}
		String whereClause = ""; 
		if(!toJoin.isEmpty()) {
			whereClause = " WHERE "+ String.join(" AND ", toJoin);
		}
		//FIXME can only order pagination for SKU
		String sql = String.format(EXTERNALQUERY, String.format(ALLPRODUCTS,whereClause,orderByClause), "ORDER BY aa.SKU asc")   ;
		GenericExecuteSpec query  = dbClient.sql(sql);
		for(SQLParameterBinder element:sqlParameterBinderList) {
			query = query.bind(element.getParameterName(),element.getParameterValue());
		}
		return query.map(Commons.READ_PRODUCT_COMPLETE_ROW).all();
	}
	
	public Flux<ProductCompleteDTO> findSingleProduct(@NotNull String sku){
		return dbClient.sql(SINGLEPRODUCT).bind("sku",sku).map(Commons.READ_PRODUCT_COMPLETE_ROW).all();
	}
	
	@Transactional
	public Mono<Integer> createSalesList() {
		return dbClient.sql(UPSERTDEFAULTLIST).fetch().rowsUpdated();
		
	}
	
}
