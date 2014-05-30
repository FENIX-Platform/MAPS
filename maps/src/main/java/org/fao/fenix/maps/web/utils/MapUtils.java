package org.fao.fenix.maps.web.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	
	public static Map<String, Double>  roundValues(Map<String, Double> values, Integer decimalNumbers) {
		if ( decimalNumbers != null) {
			Map<String, Double> result = new HashMap<String, Double>();
			for(String key : values.keySet() ) {
				BigDecimal bd = new BigDecimal(Double.toString(values.get(key)));	
				bd = bd.setScale(decimalNumbers, BigDecimal.ROUND_DOWN);
				result.put(key, bd.doubleValue());
			}
			return result;
		}
		return values;
	}
	
	public static String valuesString(Map<String, Double> values) {
		String s = "[";
		int i =0;
		for (String key : values.keySet() ) {
			s += "(" + key + "," + values.get(key) + ")";
			if ( i < values.size() -1 )
				s += ",";
		}
		s += "]";
		return s;
	}

}
