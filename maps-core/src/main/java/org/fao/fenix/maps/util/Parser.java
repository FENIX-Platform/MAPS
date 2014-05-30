package org.fao.fenix.maps.util;

import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map;

public class Parser {
	
	public static String extractValue(String s, String from, String to) {
		int fromIdx = s.indexOf(from) + from.length();
		int toIdx = s.indexOf(to);
		String result = null;
		try {
			result = s.substring(fromIdx, toIdx);
		} catch (Exception e) {}
		return result;
	}
	
	public static List<String> extractValues(String s, String from, String to) {
		List<String> l = new ArrayList<String>();		
		for (int i = 0 ; i < s.length() ; i++) {
			int fromIdx = s.indexOf(from, i);
			int toIdx = s.indexOf(to, i);
			if ( fromIdx != -1) {
				fromIdx = fromIdx + from.length(); 
				l.add(s.substring(fromIdx, toIdx));
				i = toIdx;
			}
		}	
		return l;
	}

	public static List<String> extractValues(String s, String separator) {
		List<String> l = new ArrayList<String>();		
		String[] tokens = s.split(separator);
		for (int i = 0 ; i < tokens.length; i++) {
			l.add(tokens[i]);
		}
		return l;
	}
	
	public static List<Double> extractSortedDoules(String s, String separator) {
		List<Double> l = new ArrayList<Double>();		
		String[] tokens = s.split(separator);
		Double[] values = new Double[tokens.length];
		for (int i = 0 ; i < tokens.length; i++) {
			try {
				values[i] = Double.valueOf(tokens[i]);
			}catch (Exception e) {
			}
		}
		Arrays.sort(values);
		for (int i = 0; i < values.length; i++) {
			l.add(values[i]);
		}
		return l;
	}
	
	
	public static List<String> extractTagValues(String s, String from, String to) {
		List<String> l = new ArrayList<String>();		
		for (int i = 0 ; i < s.length() ; i++) {
			int fromIdx = s.indexOf(from, i);
			int toIdx = s.indexOf(to, i);
			if ( fromIdx != -1) {
				fromIdx = fromIdx + from.length(); 
				l.add(s.substring(fromIdx, toIdx));
				i = toIdx;
			}
		}	
		return l;
	}
	
	public static String extractValue(String s, String to) {
		String l = null;
		int toIdx = s.indexOf(to, 0);
		try {
			l = s.substring(0, toIdx);
		} catch (Exception e) {
		}
		return l;
	}
	
//	private static List<Double> roundValues(List<Double> values, Integer decimalNumbers) {
//		List<Double> v = new ArrayList<Double>();
//		for(Double value : values) {
//			BigDecimal bd = new BigDecimal(Double.toString(value));	
//			bd = bd.setScale(decimalNumbers, BigDecimal.ROUND_DOWN);
//			v.add(bd.doubleValue());
//		}
//		return v;
//	}
	
	public static String getValue(Double value, String thousandSeparator, String decimalSeparator) {
		DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));	
		if ( thousandSeparator != null ) 
			symbols.setGroupingSeparator(thousandSeparator.charAt(0));
		
		if ( decimalSeparator != null ) 
			symbols.setDecimalSeparator(decimalSeparator.charAt(0));
		
		
		
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(value);
	}
	
	public static HashMap<String, Double>  roundValues(HashMap<String, Double> values, Integer decimalNumbers) {
		if ( decimalNumbers != null) {
			HashMap<String, Double> result = new HashMap<String, Double>();
			for(String key : values.keySet() ) {
				BigDecimal bd = new BigDecimal(Double.toString(values.get(key)));	
				bd = bd.setScale(decimalNumbers, BigDecimal.ROUND_DOWN);
				result.put(key, bd.doubleValue());
			}
			return result;
		}
		return values;
	}
}
