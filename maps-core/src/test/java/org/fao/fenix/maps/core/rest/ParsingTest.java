package org.fao.fenix.maps.core.rest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.fao.fenix.maps.util.Parser;

public class ParsingTest extends TestCase {
	
	public void _testParser() {
		String s = ",b,c";
		
		List<String> l = new ArrayList<String>();
		for (int i = 0 ; i < s.length() ; i++) {
			int idx = s.indexOf(",", i);
			if ( idx != -1) {
				l.add(s.substring(i, idx));
				i = idx;
			}
			else 
				l.add(s.substring(i, s.length()));
		}	
	}
	
	public void _testDataParser() {
		String s = "[{1,2},{3,4}]";
		
		String from = "[";
		String to = "]";
		
		int fromIdx = s.indexOf(from) + 1;
		int toIdx = s.indexOf(to);
		
		String result = s.substring(fromIdx, toIdx);
		
		System.out.println(result);
 
	}
	
	
	public void _testDataParserMap() {
		
		Map<String, Double> joindata = new HashMap<String, Double>();
		String value = "[{1,2},{3,4}]";
		String data = Parser.extractValue(value, "[", "]");
		
		System.out.println("data:" + data);
		
		List<String> singleValues = new ArrayList<String>();
		for (int i = 0 ; i < data.length() ; i++) {
			int fromidx = data.indexOf("{", i);
			int toidx = data.indexOf("}", i);
			if ( fromidx != -1) {
				fromidx += 1;
				singleValues.add(data.substring(fromidx, toidx));
				i = toidx;
			}
		}	
		
		for (String v : singleValues) {
			System.out.println("single:" + v);
		}
		
		System.out.println(joindata);
	}
	
	public void _testGetTag() {
	String h2 = "<html>  <head>    <title>Geoserver GetFeatureInfo output</title>  </head>  <style type=\"text/css\">	table.featureInfo, table.featureInfo td, table.featureInfo th {		border:1px solid #ddd;		border-collapse:collapse;		margin:0;		padding:0;		font-size: 90%;		padding:.2em .1em;	}	table.featureInfo th {	    padding:.2em .2em;		font-weight:bold;		background:#eee;	}	table.featureInfo td{		background:#fff;	}	table.featureInfo tr.odd td{		background:#eee;	}	table.featureInfo caption{		text-align:left;		font-size:100%;		font-weight:bold;		text-transform:uppercase;		padding:.2em .2em;	}  </style>  <body>  	<feature>		<name>Pakistan</name> <br>		<value><columnname>FAOST_CODE</columnname> -  <columnvalue>165.0</columnvalue></value> <br>		<value><columnname>ADM0_CODE</columnname> -  <columnvalue>188.0</columnvalue></value> <br>		<value><columnname>ISO2</columnname> -  <columnvalue>PK</columnvalue></value> <br>		<value><columnname>ISO3</columnname> -  <columnvalue>PAK</columnvalue></value> <br>	</feature>  </body></html>";
		String h = "<html>  <head>    <title>Geoserver GetFeatureInfo output</title>  </head>  <style type=\"text/css\">	table.featureInfo, table.featureInfo td, table.featureInfo th {		border:1px solid #ddd;		border-collapse:collapse;		margin:0;		padding:0;		font-size: 90%;		padding:.2em .1em;	}	table.featureInfo th {	    padding:.2em .2em;		font-weight:bold;		background:#eee;	}	table.featureInfo td{		background:#fff;	}	table.featureInfo tr.odd td{		background:#eee;	}	table.featureInfo caption{		text-align:left;		font-size:100%;		font-weight:bold;		text-transform:uppercase;		padding:.2em .2em;	}  </style>  <body>  	<name>Argentina</name> <br>	<code>FAOST_CODE</code> -  <value>9.0</value> <br>	<code>ADM0_CODE</code> -  <value>12.0</value> <br>	<code>ISO2</code> -  <value>AR</value> <br>	<code>ISO3</code> -  <value>ARG</value> <br>  </body></html>";
		List<String> v = Parser.extractValues(h2, "<name>", "</name>");
		System.out.println("v: " + v);
	}
	
	public void _testParseHtml() {
			
		Map<String, Double> values = new HashMap<String, Double>();
		values.put("9.0", 23123.0);
		
		String bbox = "15.380859374999998,-9.318990192397905,71.630859375,30.259067203213018";
		String x = "30";
		String y = "60";
		
		String width = "1280";
		String height = "936";
		
//		String s = GetFeatureInfo.getFeatureInfo("http://127.0.0.1:9090/geoserver/wms", "gaul_faostat", "FAOST_CODE", values, "USD", width, height, x, y, bbox);
		System.out.println("----");
//		System.out.println(s);
	}
	
	public void testThousandSeparator() {
		System.out.println("ghhg");
		DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
		symbols.setGroupingSeparator('a');
		symbols.setDecimalSeparator(',');
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));
		formatter.setDecimalFormatSymbols(symbols);

//		symbols.setZeroDigit('z');
//		BigDecimal bd = new BigDecimal(Double.toString(1423213.30005));	

		System.out.println(formatter.format(14203213.00));

//		String strange = "#,###.*";
//		DecimalFormat df = new DecimalFormat(strange, formatSymbols);
//		df.setGroupingSize(3);
		
		

//		String out = df.format(new BigDecimal(3090009.003).doubleValue());
//		System.out.println(out);
//		 out = df.format(new BigDecimal(123090009.003).doubleValue());
//		System.out.println(out);
//		 out = df.format(new BigDecimal(30903009.03203).doubleValue());
//		System.out.println(out);
//		 out = df.format(new BigDecimal(3009000329.10103).doubleValue());
//		System.out.println(out);

		
	}

}
