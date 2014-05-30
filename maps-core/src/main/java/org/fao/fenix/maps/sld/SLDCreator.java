package org.fao.fenix.maps.sld;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.geotools.feature.visitor.QuantileListVisitor;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.function.QuantileFunction;

public class SLDCreator {
	
	private static Logger LOGGER = Logger.getLogger(SLDCreator.class);
	
	public static String createSLD(String layername, String sldname, String title, String propertyColumnName, Map<String, Double> map, List<Double> values, List<String> colors, Boolean addBorders, String bordersColor, String bordersStroke, String bordersOpacity, String thousandSeparator, String decimalSeparator) {
		
		StringBuilder sld = new StringBuilder();
        Map<String, Double> mapClone = new HashMap<String, Double>(map);
        LOGGER.info("mapClone: " + mapClone);

        sld.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sld.append("<sld:StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.0.0\">");
		
		if ( layername != null ) sld.append(addLayerNameTag(layername));
		
		sld.append("<sld:UserStyle>");
		sld.append(addNameTag(sldname));
		sld.append(addTitleTag(title));			
		sld.append("<sld:FeatureTypeStyle>");
		

		LOGGER.info("VALUEs: " + values);
        LinkedHashMap<Double, List<String>> classifiedIDs = new LinkedHashMap<Double, List<String>>();


        Double baseValue = -9223372036854775808.0;// TODO: Double.MIN_VALUE doesn't work, set the minimum value
        // If for the first value is <= the others is < and the last one =>
        for (int i = 0; i < values.size(); i++) {
            List<String> classificationIDs = new ArrayList<String>();
            Double classificationValue = values.get(i);
            for (Iterator it = mapClone.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                Double fValue = mapClone.get(key);
                if ( i == 0 ) {
                    if ( fValue <= classificationValue ) {
                        classificationIDs.add(key);
                        it.remove();
                    }
                }
                else {
                        if ( fValue >= baseValue && fValue < classificationValue ) {
                            classificationIDs.add(key);
                            it.remove();
                        }
                 }

            }
            classifiedIDs.put(classificationValue, classificationIDs);
            baseValue = values.get(i);
        }

        // classified values
        Integer i = 0;
        Double lastValue =null;
        for(Double v: classifiedIDs.keySet() ) {
            lastValue = v;
            /** TODO: work around on decimal number, do it better and handle thousand separator and decimal separator**/
            String valueFormatted = getValueFormatted(lastValue, thousandSeparator, decimalSeparator);
            valueFormatted = ( valueFormatted.endsWith(decimalSeparator + "0"))? valueFormatted.substring(0,valueFormatted.length()-2) : valueFormatted;

            String classificationTitle = (i ==0 )? "&#60;&#61; " + valueFormatted: "&#60; " + valueFormatted;
            sld.append(createRule(classificationTitle, propertyColumnName, classifiedIDs.get(v), colors.get(i), addBorders, bordersColor, bordersStroke, bordersOpacity));
            i++;
        }
        // TODO: the remaining codes on the latest class

        List<String> lastClass = new ArrayList<String>();
        for (String key : mapClone.keySet()) {
            lastClass.add(key);
        }
        if ( mapClone.size() > 0 ) {
            String valueFormatted = getValueFormatted(lastValue, thousandSeparator, decimalSeparator);
            valueFormatted = ( valueFormatted.endsWith(decimalSeparator + "0"))? valueFormatted.substring(0,valueFormatted.length()-2) : valueFormatted;

            String classificationTitle = (classifiedIDs.size()>1)? "&#62;&#61; " + valueFormatted:  "&#62; " + valueFormatted;
            sld.append(createRule(classificationTitle, propertyColumnName, lastClass, colors.get(i), addBorders, bordersColor, bordersStroke, bordersOpacity));
        }

        sld.append("</sld:FeatureTypeStyle>");
        sld.append("</sld:UserStyle>");

        if ( layername != null )
            sld.append("</sld:NamedLayer>");
        sld.append("</sld:StyledLayerDescriptor>");

		return sld.toString();
	}

    private void roundClassificationValue() {

    }

    private static String createRule(String classificationTitle, String propertyColumnName, List<String> codes, String color, Boolean addBorders, String bordersColor, String bordersStroke, String bordersOpacity) {
        StringBuilder rule = new StringBuilder();
        rule.append("<sld:Rule>");
            // CLASSIFICATION TITLE
            rule.append(addTitleTag(classificationTitle));

            rule.append("<ogc:Filter>");

            if ( codes.size() > 1) { rule.append("<ogc:Or>"); }
            for( String code : codes ){
                rule.append("<ogc:PropertyIsEqualTo>");
                    rule.append("<ogc:PropertyName>"+ propertyColumnName + "</ogc:PropertyName>");
                    rule.append("<ogc:Literal>"+ code +"</ogc:Literal>");
                rule.append("</ogc:PropertyIsEqualTo> ");
            }
            if ( codes.size() > 1) { rule.append("</ogc:Or>"); }
            rule.append("</ogc:Filter>");
            rule.append("<sld:PolygonSymbolizer>");
                rule.append("<sld:Fill>");
                    rule.append("<sld:CssParameter name=\"fill\">"+ color +"</sld:CssParameter>");
                rule.append(" </sld:Fill>");

            // add borders rule
            if ( addBorders )
                rule.append(addDefaultBorderRule("#"+ bordersColor, bordersStroke, bordersOpacity));
             rule.append("</sld:PolygonSymbolizer>");

                rule.append("</sld:Rule>");
       return rule.toString();
    }

	
	/** TODO: quick fix for the FSD **/
	public static String createSLDCustomEqual(String layername, String sldname, String title, String propertyColumnName, Map<String, Double> map, List<Double> values, List<String> colors, Boolean addBorders, String bordersColor, String bordersStroke, String bordersOpacity) {	
		
		StringBuilder sld = new StringBuilder();		
		sld.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sld.append("<sld:StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" version=\"1.0.0\">");
		
		if ( layername != null ) {
			sld.append(addLayerNameTag(layername));
		}
		
		sld.append("<sld:UserStyle>");
		sld.append(addNameTag(sldname));
		sld.append(addTitleTag(title));			
		sld.append("<sld:FeatureTypeStyle>");
		
		// add borders rule
		if ( addBorders )
			sld.append(addDefaultBorderRule("#"+ bordersColor, bordersStroke, bordersOpacity));
//		LOGGER.info("VALUEs: " + values);
		// algorith for each value of the map it's associate
        for(String fID : map.keySet()) {
			sld.append("<sld:Rule>");
			sld.append(addTitleTag(fID));
			sld.append("<ogc:Filter>");
			sld.append("<ogc:PropertyIsEqualTo>");
			sld.append(addPropertyName(propertyColumnName));
			sld.append(addLiteral(fID));
			sld.append("</ogc:PropertyIsEqualTo>");
			sld.append("</ogc:Filter>");
			Double value = map.get(fID);

//			Integer classRange = values.size();
			Integer classRange = null;
//			LOGGER.info("values.size(): " + values.size());
//			LOGGER.info("colors.size(): " + colors.size());
			
			classRange = values.size();
			for (int i = 0; i < values.size(); i++) {
				// because are all the classes <
				// (TODO: slipt it in a different method) ?
				// Changed to <= instead of <
//				LOGGER.info("VALUE: " + value + " | " + values.get(i) + " | " + i);

				if (value <= values.get(i)) {
					classRange = i;
					break;
				}
			}

			sld.append(addPolygonSymbolizerFillTag(classRange, colors, 0.7));
			sld.append("</sld:Rule>");
        }
		sld.append("</sld:FeatureTypeStyle>");
		sld.append("</sld:UserStyle>");
		
		if ( layername != null )
			sld.append("</sld:NamedLayer>");
		sld.append("</sld:StyledLayerDescriptor>");
		
		return sld.toString();
	}
	
	public static List<Double> getEqualIntervals(Map<String, Double> map, Integer intervals, Integer decimalNumbers) {
//		LOGGER.info(map);
		double[] values = new double[map.size()];
		int i = 0;
		for(String key : map.keySet()) {
			values[i] = map.get(key);
//			LOGGER.info(values[i]);
			i++;
		}
		return getEqualIntervals(values, intervals, decimalNumbers);
	}
	
	/** change v to values **/
	public static List<Double> getEqualIntervals(double[] values, Integer intervals, Integer decimalNumbers) {
		List<Double> ranges = new ArrayList<Double>();
		Arrays.sort(values);
		if ( values.length > intervals ) {
			// Interval
			Double v = values[values.length -1 ] - values[0]; 	
			Double i = v / (intervals);
			
			Double value = values[0];
			for(int j = 0; j < intervals -1; j++){
				value = value + i;
				addValue(ranges, value, decimalNumbers);
			}	
		}
		else {
			ranges.addAll(defaultRanges(values));
		}
//		LOGGER.info("RANGES: " + ranges.size() + " | " + ranges);
		
		
//		List<Double> ranges = new ArrayList<Double>();
//		Arrays.sort(values);
//		if ( values.length > intervals ) {
//			// Interval
//			Double v = values[values.length -1 ] - values[0]; 	
//			Double i = v / (intervals);
//			
//			Double value = values[0];
//			for(int j = 0; j < intervals -1; j++){
//				value = value + i;
//				addValue(ranges, value, decimalNumbers);
//			}	
//		}
//		else {
//			ranges.addAll(defaultRanges(values));
//
//		}
//		LOGGER.info(ranges);
		return ranges;
	}
	
	public static List<Double> getEqualAreas(Map<String, Double> values, Map<String, Double> areas, Integer intervals, Integer decimalNumbers) {
//		LOGGER.info(values);
		List<Double> ranges = new ArrayList<Double>();
		if ( values.size() > intervals ) {
//			System.out.println("values: " + values);
//			System.out.println("areas: " + areas);
			Double totalAreas = getTotalAreas(areas);
			Double classes = totalAreas / intervals; 
//			System.out.println("TOTAL: " + totalAreas + " | CLASSES: " + classes);
			Double midsum = new Double(0);
	        Map<String,Double> sortedValues = sortByComparator(values);
//	        System.out.println("---> sortedValues: " + sortedValues);
	        for (Map.Entry entry : sortedValues.entrySet()) {
	        	try {
	//	            System.out.println("---> entry.getKey(): " + entry.getKey());
		        	midsum = midsum + areas.get(entry.getKey());
		        	if ( (midsum - classes) > 0) {	
//		        		System.out.println("---> adding: " + midsum + " | " + entry.getKey());
		        		// break of the class
		        		try {
		    				Double value = Double.valueOf(entry.getValue().toString());
		    				addValue(ranges, value, decimalNumbers);
		        			midsum = new Double(0);
		        		}catch (Exception e) {}
		        	}
	        	}catch (Exception e) {}
	        }		
		}
		else {
			// TODO: put it outside
			double[] v = new double[values.size()];
			int i = 0;
			for(String key : values.keySet()) {
				v[i] = values.get(key);
//				LOGGER.info("VALUES: " + v[i]);
				i++;
			}
			Arrays.sort(v);
			ranges.addAll(defaultRanges(v));
		}
//		LOGGER.info("RANGES: " + ranges.size() + " | " + ranges);
		
		
		
//		LOGGER.info(values);
//		List<Double> ranges = new ArrayList<Double>();
//		System.out.println("values: " + values);
//		System.out.println("areas: " + areas);
//		Double totalAreas = getTotalAreas(areas);
//		Double classes = totalAreas / intervals; 
//		System.out.println("TOTAL: " + totalAreas + " | CLASSES: " + classes);
//		Double midsum = new Double(0);
//        Map<String,Double> sortedValues = sortByComparator(values);
//        System.out.println("---> sortedValues: " + sortedValues);
//        for (Map.Entry entry : sortedValues.entrySet()) {
//        	try {
//	            System.out.println("---> entry.getKey(): " + entry.getKey());
//	        	midsum = midsum + areas.get(entry.getKey());
//	        	if ( (midsum - classes) > 0) {	
//	        		System.out.println("---> adding: " + midsum + " | " + entry.getKey());
//	        		// break of the class
//	        		try {
//	    				Double value = Double.valueOf(entry.getValue().toString());
//	    				addValue(ranges, value, decimalNumbers);
//	        			midsum = new Double(0);
//	        		}catch (Exception e) {}
//	        	}
//        	}catch (Exception e) {}
//        }		
//        System.out.println("ranges size: " + ranges.size());
		return ranges;
	}
	
	
	
	private static Map sortByComparator(Map unsortMap) {

		List list = new LinkedList(unsortMap.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
   }	
	
	private static Double getTotalAreas(Map<String, Double> map) {
		Double result = new Double(0);
		for(String key : map.keySet()) {
//			System.out.println("result: " + result + " | " + map.get(key));
			result = result + map.get(key);
		}
		return result;
	}
	

	
	public static List<Double> getQuantiles(Map<String, Double> map, Integer intervals, Integer decimalNumbers ) {
		double[] values = new double[map.size()];
		int i = 0;
		for(String key : map.keySet()) {
			values[i] = map.get(key);
			i++;
		}
		return getQuantiles(values, intervals, decimalNumbers);
	}
	
	public static List<Double> getQuantiles(double[] values, Integer intervals, Integer decimalNumbers ) {
		List<Double> ranges = new ArrayList<Double>();
		Arrays.sort(values);
		if ( values.length > intervals ) {
			// Interval
			Integer i = Integer.valueOf(values.length / (intervals));
			Double value = values[0];
			for(int j = 0; j < values.length -1; j++){
				// fix on algorithm (forcing the algorithm to stop)
				if ( ranges.size() < intervals - 1 ) {
					if ( j != 0)
						if ( (j % i) == 0 ) {
							value = values[j];
							addValue(ranges, value, decimalNumbers);
						}
				}
				else 
					break;
			}	
		}
		else {
			ranges.addAll(defaultRanges(values));
		}
//		LOGGER.info(ranges);
		return ranges;
	}
	
	/** TODO: this has to be skipped **/
	private static void addValue(List<Double> ranges, Double value, Integer decimalNumbers) {
		if ( ranges.isEmpty() )
			ranges.add(value);
		else if (ranges.get(ranges.size()-1) != value) {
			ranges.add(value);
		}
	}
	
	private static List<Double> defaultRanges(double[] values) {
		LinkedHashMap<Double, Double> v = new LinkedHashMap<Double, Double>();
		List<Double> ranges = new ArrayList<Double>();
		if ( values.length == 1) {
			ranges.add(values[0] + 0.1);
			return ranges;
		}
		else {
			for(int j = 0; j < values.length - 1; j++){
				v.put(values[j] + 0.1, values[j] + 0.1);
			}
			for(Double d : v.keySet()) {
				ranges.add(d);
			}
			
//			LOGGER.info("RANGES: " + ranges);
			return ranges;
		}
	}

	private static String addPolygonSymbolizerFillTag(Integer currentClass, List<String> colors, Double opacity) {
		StringBuilder sb = new StringBuilder();

		//LOGGER.info("addPolygonSymbolizerFillTag COLORS: " + colors);
//		LOGGER.info("-----------");
//		LOGGER.info("-> " + currentClass);
//		LOGGER.info("-> " + colors.get(currentClass));
		sb.append("<sld:PolygonSymbolizer>");
		sb.append("<sld:Fill>");
		try {
			
		sb.append("<sld:CssParameter name=\"fill\">" + colors.get(currentClass)+ "</sld:CssParameter>");
		sb.append("<CssParameter name=\"fill-opacity\">"+ opacity +"</CssParameter>");
		}catch (Exception e) {
			LOGGER.info("ERROR:" + currentClass + " | " + colors);			
		}
		
		sb.append("</sld:Fill>");
		sb.append("</sld:PolygonSymbolizer>");
		return sb.toString();
	}
	
	private static String addDefaultBorderRule(String color, String strokeWidth, String opacity ) {
		StringBuilder sb = new StringBuilder();
		
		//sb.append("<sld:Rule>");
		//sb.append("<sld:PolygonSymbolizer>");
		sb.append("<sld:Stroke>");
		sb.append("<sld:CssParameter name=\"stroke\">"+ color+ "</sld:CssParameter>");
		sb.append("<sld:CssParameter name=\"stroke-width\">" + strokeWidth + "</sld:CssParameter>");
		sb.append("<sld:CssParameter name=\"stroke-opacity\">" + opacity + "</sld:CssParameter>");
		sb.append("</sld:Stroke>");
		//sb.append("</sld:PolygonSymbolizer>");
		//sb.append("</sld:Rule>");

		return sb.toString();
	}
	
	private static String addLayerNameTag(String name){
		StringBuilder sb = new StringBuilder();
		sb.append("<sld:NamedLayer>");
		sb.append("<sld:Name>");
		sb.append(name);
		sb.append("</sld:Name>");
		return sb.toString();
	}
	
	private static String addNameTag(String name){
		StringBuilder sb = new StringBuilder();
		sb.append("<sld:Name>");
		sb.append(name);
		sb.append("</sld:Name>");
		return sb.toString();
	}
	
	private static String addTitleTag(String title){
		StringBuilder sb = new StringBuilder();
		sb.append("<sld:Title>");
		sb.append(title);
		sb.append("</sld:Title>");
		return sb.toString();
	}
	
	private static String addPropertyName(String propertyName){
		StringBuilder sb = new StringBuilder();
		sb.append("<ogc:PropertyName>");
		sb.append(propertyName);
		sb.append("</ogc:PropertyName>");
		return sb.toString();
	}
	
	private static String addLiteral(String value){
		StringBuilder sb = new StringBuilder();
		sb.append("<ogc:Literal>");
		sb.append(value);
		sb.append("</ogc:Literal>");
		return sb.toString();
	}

    private static String getValueFormatted(Double value, String thousandSeparator, String decimalSeparator) {

        DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("en_US"));
        if ( thousandSeparator != null )
            symbols.setGroupingSeparator(thousandSeparator.charAt(0));

        if ( decimalSeparator != null )
            symbols.setDecimalSeparator(decimalSeparator.charAt(0));

        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

}
