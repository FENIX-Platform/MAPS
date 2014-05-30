package org.fao.fenix.maps.sld.legend;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.legend.LegendInfo;

import com.cenqua.clover.cfg.Interval;

public class LegendCreator {
	
	private static final Logger LOGGER = Logger.getLogger(LegendCreator.class);
	
	// The colors are n+1 range values
	// i.e. ranges[10,20]
	// means <10, 10-20, >20 (so 3 values, and 3 different colors)
//	public static String createVerticalLegend(String title, List<Double> values, List<String> colors, String position, String thousandSeparator, String decimalSeparator, Integer joinDataSize, LinkedHashMap<String, String> colorLegend) {
	public static String createVerticalLegend(String title, List<Double> values, List<String> colors, String position, String thousandSeparator, String decimalSeparator, Integer joinDataSize, List<LegendInfo> legendInfo) {
	//		LOGGER.info("COLORS: " + colors);
		
		String s;
		
		List<String> valuesFormatted = getValues(values, thousandSeparator, decimalSeparator);
		
		s = "<div id='"+ LegendConstants.id + "' class='"+ position + "'>";
		
		// title
		if ( title != null)
			s += "<div class='"+ LegendConstants.classTitle+ "'>" + title + "</div>"; 
		
		// values
		s += "<div><table>";

		
		// this is used just if there is one value
		// TODO: make it nicer
		if ( joinDataSize <= 1 ) {
			s += "<tr>";
			s += "<td class='" + LegendConstants.classValueBox+"'  bgcolor="+ colors.get(0) +"></td>";
			s += "<td class='"+ LegendConstants.classValue +"'>";
//			s += " < " + values.get(0);
			s += " < " + valuesFormatted.get(0);
			s += "</td>";
			s += "</tr>";
			
			legendInfo.add(new LegendInfo(valuesFormatted.get(0), colors.get(0)));
		}
		else {
			int intervals = colors.size();
			if ( colors.size() > joinDataSize ) {
				intervals = joinDataSize;
			}
			for(int i=0; i < intervals; i++) {	
				try {
					LegendInfo l = new LegendInfo();
					
					s += "<tr>";
					s += "<td class='" + LegendConstants.classValueBox+"'  bgcolor="+ colors.get(i) +"></td>";
					s += "<td class='"+ LegendConstants.classValue +"'>";
					
					l.setColor(colors.get(i));
					
					// TODO: check for values like 2 intervals etc..
//					if( values.size() == 2 ) {
//						if ( i == 0) {
//							//s += " < " + valuesFormatted.get(i);
//							s += " <= " + valuesFormatted.get(i);
//						}
//						if ( i == 1 ) {
//							s += " >= " + valuesFormatted.get(i);
//						}
//					}
					// if ( values > 2 )
//					else {
						
						String legendName;
						// TODO: check for values like 2 intervals etc..
						if ( i == 0) {
							//s += " < " + valuesFormatted.get(i);
//							s += " <= " + valuesFormatted.get(i);
							legendName = " <= " + valuesFormatted.get(i);
						}
	//					else if ( i == colors.size() - 1 || i == intervals -1) {
						else if ( i == intervals -1) {
							if ( valuesFormatted.size() == 1) {
	//							s += " >= " +valuesFormatted.get(i - 1); 
//								s += " > " +valuesFormatted.get(i - 1); 
								legendName = " > " +valuesFormatted.get(i - 1); 
							}
							else {
	//							s += " >= " +valuesFormatted.get(i - 1); 
//								s += " >= " +valuesFormatted.get(i - 1); 
								legendName = " >= " +valuesFormatted.get(i - 1); 
							}
						}
						else {
//							s += valuesFormatted.get(i);
							legendName = valuesFormatted.get(i);; 
						}
						
						// adding to the html string and to legendvalues
						s += legendName;
						l.setName(legendName);
						
						legendInfo.add(l);
//					}
					
				}catch (Exception e) {
				}
				s += "</td>";
				s += "</tr>";
			}
		}
		s += "</table></div>";
		s += "</div>";
		
//		LOGGER.info(s);		
		return s;
	}
	
	
	public static String createVerticalLegendEqual(String title, List<Double> values, List<String> colors, String position, String thousandSeparator, String decimalSeparator, Integer joinDataSize) {
//		LOGGER.info("COLORS: " + colors);
		
		String s;
		
		List<String> valuesFormatted = getValues(values, thousandSeparator, decimalSeparator);
		
		s = "<div id='"+ LegendConstants.id + "' class='"+ position + "'>";
		
		// title
		if ( title != null)
			s += "<div class='"+ LegendConstants.classTitle+ "'>" + title + "</div>"; 
		
		// values
		s += "<div><table>";

		
		// this is used just if there is one value
		// TODO: make it nicer
		if ( joinDataSize <= 1 ) {
			s += "<tr>";
			s += "<td class='" + LegendConstants.classValueBox+"'  bgcolor="+ colors.get(0) +"></td>";
			s += "<td class='"+ LegendConstants.classValue +"'>";
//			s += " < " + values.get(0);
			s += " < " + valuesFormatted.get(0);
			s += "</td>";
			s += "</tr>";
		}
		else {
			int intervals = colors.size();
			if ( colors.size() > joinDataSize ) {
				intervals = joinDataSize;
			}
			for(int i=0; i < intervals; i++) {	
				try {
					s += "<tr>";
					s += "<td class='" + LegendConstants.classValueBox+"'  bgcolor="+ colors.get(i) +"></td>";
					s += "<td class='"+ LegendConstants.classValue +"'>";
					s +=  valuesFormatted.get(i);
					
				}catch (Exception e) {
				}
				s += "</td>";
				s += "</tr>";
			}
		}
		s += "</table></div>";
		s += "</div>";
		
//		LOGGER.info(s);		
		return s;
	}
	
	// TODO: remove decimalPlaces?
	private static List<String> getValues(List<Double> values, String thousandSeparator, String decimalSeparator) {
		List<String> valuesFormatted = new ArrayList<String>(values.size());
//		LOGGER.info(thousandSeparator);
//		LOGGER.info(decimalSeparator);
		DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));	
		if ( thousandSeparator != null ) 
			symbols.setGroupingSeparator(thousandSeparator.charAt(0));
		
		if ( decimalSeparator != null ) 
			symbols.setDecimalSeparator(decimalSeparator.charAt(0));
		
		formatter.setDecimalFormatSymbols(symbols);
		for(Double value : values) {
			valuesFormatted.add(formatter.format(value));
		}
//		LOGGER.info(valuesFormatted);
		return valuesFormatted;
	} 
}
