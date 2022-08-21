package com.monclick.test.model.internal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SQLParameterBinder {

	private String sqlString;
	private String parameterName;
	private Object parameterValue;
	
	
}
