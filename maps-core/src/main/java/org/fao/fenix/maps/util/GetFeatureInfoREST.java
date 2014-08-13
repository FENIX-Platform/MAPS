package org.fao.fenix.maps.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;


public class GetFeatureInfoREST {
	
	private static final Logger LOGGER = Logger.getLogger(GetFeatureInfoREST.class);

	// TODO: pass the csspath and css name
	public static String getJoinFeatureInfo(String wmsUrl, String layername, String columncode, String columnlabel, Map<String, Double> values, String measurementunit, String lang, String width, String height, String x, String y, String bbox, String srs) {
		StringBuilder sb = new StringBuilder();
		StringBuilder response = new StringBuilder();
		try {
			String request = wmsUrl + "?SERVICE=WMS&VERSION=1.1.1" +
								"&REQUEST=GetFeatureInfo&" +
					            "LAYERS="+ layername +"&QUERY_LAYERS="+ layername +"" +
					            "&BBOX="+ bbox +
					            "&HEIGHT="+ height +
					            "&WIDTH="+ width +
					            "&INFO_FORMAT=text/html" +
								"&SRS="+ srs +
					            "&X="+ x +
					            "&Y="+ y;			
			
			LOGGER.info(request);
			URL u = new URL(request);
			URLConnection uc = u.openConnection();
			HttpURLConnection connection = (HttpURLConnection) uc;
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOGGER.info(response.toString());
        LOGGER.info(values);
//		sb.append(response.toString());
		
		// TODO: should parse the response
		sb.append(createHtml(response.toString(), columncode, columnlabel, values, measurementunit, lang));
		
//		String tagContent = XMLTools.readTag(sb.toString(), "DB", "DRIVER", PAYLOAD.PAYLOAD.name());
		return sb.toString();
	}
	
	private static String createHtml(String response, String columncode, String columnlabel, Map<String, Double> values, String measurementUnit, String lang) {
		StringBuilder html = new StringBuilder();
		
		// add headers, styles...
		
		List<String> features = getFeatures(response);
		if ( !features.isEmpty() ) {
			for(String feature : features ){
				
				// TODO:  this a quick fix for the labels in faostat
				// make it explocit the columnlabel and lang used 
				// TODO: create a separete popup for the other ones that are not faostat
				String label = getLabel(feature, lang);
				
				html.append("<div style=\"font-size:12px; font-weight:bold; color:#338cb8; " +
						    "border-bottom:1px color:#338cb8; " +
						    "font-family:'Trebuchet MS', Arial, Helvetica, sans-serif;\">" 
						     //+ getName(feature) + 
						     + label +
						    "</div> ");
				html.append(" <hr style=\"border:none;  margin-top:1px;  border-top: 1px dashed #338cb8;    -moz-border-bottom-colors: none;    -moz-border-left-colors: none;    -moz-border-right-colors: none;    -moz-border-top-colors: none;    border-image: none;    text-align: left;    width: 100%;\" />");
				String code = getCode(feature, columncode);
				LOGGER.info("code: " + code);
				LOGGER.info("label: " + label);
				try {
					Double v = values.get(code);
					if ( v != null ) {
						html.append("<div style=\"font-size:11px; " +
								    "color:#333333; " +
								    "font-family:'Trebuchet MS', Arial, Helvetica, sans-serif;\">");
						String formattedValue = getValue(v, null, null);
						html.append(formattedValue);
						if ( measurementUnit != null && measurementUnit != "" )
							html.append(" <i>" + measurementUnit+ "</i>");
						
						html.append("</div>");
					}
				}catch (Exception e) {
					LOGGER.warn("no code available");
				}
			}
		}
		else {
			html.append("<div style=\"font-size:12px;\"><i>No data available for this point</i></div>");	
		}
		
		LOGGER.info(html.toString());
		return html.toString();
	}
	
	private static String getValue(Double value, String thousandSeparator, String decimalSeparator) {
		
		DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));	
		if ( thousandSeparator != null ) 
			symbols.setGroupingSeparator(thousandSeparator.charAt(0));
		
		if ( decimalSeparator != null ) 
			symbols.setDecimalSeparator(decimalSeparator.charAt(0));
		
		formatter.setDecimalFormatSymbols(symbols);

		return formatter.format(value);
	} 
	
	private static List<String> getFeatures(String response) {
		List<String> features = Parser.extractValues(response, "<feature>", "</feature>"); 
		return features;
	}
	
	private static String getName(String response) {
		String name = Parser.extractValue(response, "<name>", "</name>"); 
		return name;
	}
	
	private static String getCode(String content, String columncode) {		
		List<String> values = Parser.extractValues(content, "<value>", "</value>"); 
		String cv = "";
		//LOGGER.info("values: " + values);

		for(String value : values) {
			String cm = Parser.extractValue(value, "<columnname>", "</columnname>");
			//LOGGER.info("columncode: " + columncode);
			if ( columncode.equalsIgnoreCase(cm)) {	
				cv = Parser.extractValue(value, "<columnvalue>", "</columnvalue>");
				// TODO: to be removed the codes should be without the decimal points
                if (cv.contains("."))
				    cv = Parser.extractValue(String.valueOf(cv), ".");
				LOGGER.info("cv1: -" +cv +"-");
				return cv;
			}
		}

		return cv;
	}
	
	private static String getLabel(String content, String lang) {		
		List<String> values = Parser.extractValues(content, "<name>", "</name>"); 
		String cm = "";
		//LOGGER.info("name: " + values);
		//LOGGER.info("lang: " + lang);
		for(String value : values) {
			cm = Parser.extractValue(value, "<"+lang+">", "</"+lang+">");
			//LOGGER.info("VALUE:" +  value);
			//LOGGER.info("cm:" +  cm);
			if ( cm != null ) {
				//LOGGER.info("BREAK");
				break;
			}
		}

		return cm;
	}

}
