package org.fao.fenix.maps.core.rest;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.sld.SLDColors;
import org.fao.fenix.maps.sld.SLDCreator;
import org.fao.fenix.maps.util.LeafletMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SLDTest extends TestCase {
    
    static List<String> colors;
    
    String table = "<table class=\"featureInfo\">  <caption class=\"featureInfo\">zzlayer_gaul0</caption>  <tr>  <th>fid</th>    <th >AREA</th>    <th >PERIMETER</th>    <th >G2008_0_</th>    <th >G2008_0_ID</th>    <th >ADM0_CODE</th>    <th >ADM0_NAME</th>    <th >LAST_UPDAT</th>    <th >CONTINENT</th>    <th >REGION</th>    <th >STR_YEAR0</th>    <th >EXP_YEAR0</th>  </tr>    <tr>  <td>zzlayer_gaul0.67</td>         <td>6.0E-5</td>      <td>0.03078</td>      <td>25777</td>      <td>25776</td>      <td>68</td>      <td>Democratic Republic of the Congo</td>      <td>20081104</td>      <td>Africa</td>      <td>Middle Africa</td>      <td>0</td>      <td>0</td>  </tr><tr>  <td>zzlayer_gaul0.67</td>         <td>6.0E-5</td>      <td>0.03078</td>      <td>25777</td>      <td>25776</td>      <td>68</td>      <td>Democratic Republic of the Congo</td>      <td>20081104</td>      <td>Africa</td>      <td>Middle Africa</td>      <td>0</td>      <td>0</td>  </tr></table>";    
    
    
    static {
            colors = new ArrayList<String>();
            
            colors.add("#FFE9D9");
            colors.add("#F5CBB5");
            colors.add("#E6AA91");
            colors.add("#D18262");
            colors.add("#C25E3A");
            colors.add("#9E3A19");
    }
    
  public void testEqualArea() {
		Integer intervals = 5;
		Map<String, Double> map = new HashMap<String, Double>();
		Map<String, Double> areas = new HashMap<String, Double>();
		Random r=new Random();
		for(int i=0; i < 100; i++) {
			double randomValue = r.nextInt(100000);
			double randomValue2 = r.nextInt(100000);
			map.put(String.valueOf(i), randomValue);
			areas.put(String.valueOf(i), randomValue2);
		}
		
		System.out.println("testEqualArea");
		List<Double> values = SLDCreator.getEqualAreas(map, areas, intervals, 3);
		System.out.println(values);
	}


    
//    private String rgbToHex(String r, String g, String b) {
//
//
//    	      int i = Integer.parseInt(r);
//    	      int j = Integer.parseInt(g);
//    	      int k = Integer.parseInt(b);
//    	    
//    	      Color c = new Color(i,j,k);
//    	      System.out.println
//    	        ( "hex: " + Integer.toHexString( c.getRGB() & 0x00ffffff ) ); 
//
//    }
    
//    public void _testQuantiles() {
//    	Integer intervals = 5;
//    	HashMap<String, Double> map = new HashMap<String, Double>();
//    	Random r=new Random();
//    	for(int i=2; i < 302; i++) {
//    		double randomValue = r.nextInt(1000);
//    		map.put(String.valueOf(i), randomValue);
//    	}
//    	
//    	String sld = SLDCreator.createSLDQuantile("test", "test", "ADM0_CODE", map, intervals, null);
//    	System.out.println(sld);
//
//    	// publish the style
//    	
//    	// return success
//  
//    }
    
    public void _testCreateSLD() {
            System.out.println("createSLDQuantitle");
            
            StringBuilder sld = new StringBuilder();                
            sld.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sld.append("<sld:UserStyle xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");

            sld.append(addNameTag(""));
            
            sld.append(addTitleTag(""));
                            
            sld.append("<sld:FeatureTypeStyle>");

                    
            for(int i=0; i < 300; i++) {
                    sld.append("<sld:Rule>");
                    sld.append(addTitleTag(String.valueOf(i)));
                    sld.append("<ogc:Filter>");
                    sld.append("<ogc:PropertyIsEqualTo>");
                    sld.append(addPropertyName("ADM0_CODE"));
                    sld.append(addLiteral(String.valueOf(i)));
                    sld.append("</ogc:PropertyIsEqualTo>");
                    
                    sld.append("</ogc:Filter>");
                    
                    sld.append(addPolygonSymbolizerFillTag((int)(Math.random()*4) + 1));
                    sld.append("</sld:Rule>");
            }


                    
            sld.append("</sld:FeatureTypeStyle>");
            sld.append("</sld:UserStyle>");
            System.out.println("SLD");
                    
            System.out.println(sld.toString());
    }
    
    public void _testHTMLTAble() {
            System.out.println(table);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            try {
                    db = dbf.newDocumentBuilder();
            
            
            InputStream stream = new ByteArrayInputStream(table.getBytes("UTF-8"));
            Document document = db.parse(stream);
            Element element = document.getDocumentElement();
            
            List<List<String>> tableList = new ArrayList<List<String>>();
            
            List<String> headers = new ArrayList<String>();
            // header
            NodeList headerName = element.getElementsByTagName("tr");
            for (int i = 0; i < headerName.getLength(); i++) {
                    Element headerNameElement = (Element) headerName.item(i);
                    NodeList headerListNode = headerNameElement.getElementsByTagName("th");
                    for(int j = 0; j < headerListNode.getLength(); j++) {
                            Element thElement = (Element) headerListNode.item(j);
                            String value = thElement.getTextContent();
                            headers.add(value);
                    }
                    
            }
            tableList.add(headers);
            
            
            NodeList contentNode = element.getElementsByTagName("tr");
            for (int i = 0; i < contentNode.getLength(); i++) {
                    List<String> content = new ArrayList<String>();
                    Element contentNodeElement = (Element) contentNode.item(i);
                    NodeList contentNodeListNode = contentNodeElement.getElementsByTagName("td");
                    for(int j = 0; j < contentNodeListNode.getLength(); j++) {
                            Element tdElement = (Element) contentNodeListNode.item(j);
                            String value = tdElement.getTextContent();
                            content.add(value);
                    }
            
                    if ( !content.isEmpty() ) {
                            System.out.println("CONTENT: " + content);
                            tableList.add(content);
                    }
                    
            }
            
            
            
            System.out.println(tableList);
            
            
            // content
            
            } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            
    }
    


   
    
    public void _testSLDQuantity(){
            
            List<Double> values = new ArrayList<Double>();
            
            values.add(new Double(18));
            values.add(new Double(30));
            values.add(new Double(36));
            values.add(new Double(226));
            values.add(new Double(325));

            values.add(new Double(400));
            values.add(new Double(600));
            values.add(new Double(700));
            values.add(new Double(970));
            
            values.add(new Double(1000));
            values.add(new Double(1000));
            values.add(new Double(1070));
            values.add(new Double(2386));
            
            values.add(new Double(2500));
            values.add(new Double(4000));
            values.add(new Double(4800));
            values.add(new Double(7835));
            
            values.add(new Double(7987));
            values.add(new Double(8100));
            values.add(new Double(8144));
            values.add(new Double(8500));
            
            values.add(new Double(8500));
            values.add(new Double(8740));
            values.add(new Double(8879));
            values.add(new Double(9500));
            
            values.add(new Double(9641));
            values.add(new Double(10359));
            values.add(new Double(11000));
            values.add(new Double(13000));
            
            values.add(new Double(13116));
            values.add(new Double(19000));
            values.add(new Double(20000));
            values.add(new Double(20702));
            values.add(new Double(31000));
            values.add(new Double(39191));
            values.add(new Double(53000));
            values.add(new Double(75734));
            values.add(new Double(80300));
            values.add(new Double(86400));
            values.add(new Double(97760));
            values.add(new Double(104000));
            values.add(new Double(113242));
            values.add(new Double(143700));
            values.add(new Double(158000));
            values.add(new Double(160297));
            
            values.add(new Double(170446));
            values.add(new Double(175000));
            values.add(new Double(199990));
            values.add(new Double(203300));
            values.add(new Double(206936));
            values.add(new Double(209830));
            values.add(new Double(236434));
            values.add(new Double(240533));
            
            values.add(new Double(288642));
            values.add(new Double(291719));
            values.add(new Double(335000));
            values.add(new Double(342500));
            values.add(new Double(343350));
            values.add(new Double(453300));
            values.add(new Double(548097));
            values.add(new Double(659096));
            values.add(new Double(746200));
            values.add(new Double(787500));
            values.add(new Double(799032));
            values.add(new Double(844000));
            values.add(new Double(858333));

            values.add(new Double(858333));
            values.add(new Double(919000));
            
            
            
            
            Integer intervals = 6;
            
            createSLDQuantitle("layername", "sldname", "TITLE", values, intervals);
    }
    

    
    private void createSLDQuantitle(String tablename, String sldname, String title, List<Double> values, Integer intervals) {
            System.out.println("createSLDQuantitle");
            
            StringBuilder sld = new StringBuilder();                
            sld.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sld.append("<sld:UserStyle xmlns=\"http://www.opengis.net/sld\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");

            sld.append(addNameTag(sldname));
            
            sld.append(addTitleTag(title));
                            
            sld.append("<sld:FeatureTypeStyle>");
            
            if ( values.size() < intervals ) {
                    sld.append(createStandardSLD());
            }
            else{
                    // number of values
                    Integer count = values.size();
                    
                    // number of steps
                    Integer steps = count / intervals;
                    
                    System.out.println("intervals: " + intervals);
                    System.out.println("count: " + count);
                    System.out.println("step: " + steps);
                    
                    // that's the number of currentClass
                    Integer currentClass = 0;
                    Double oldValue = new Double(0);
                    
                    
                    for(int i=0; i < count; i++) {
    //                      System.out.println("i: " + i + " |" + steps + " | ");
                            
                            if ( (i % steps) == 0) {
    //                              System.out.println("----->currentClass: " + currentClass +" i: " + i + " |" + steps + " | " +  (i % steps));
                                    String rule = createRule("quantity", "#000000", currentClass, intervals, oldValue, values.get(i));
                                    oldValue = values.get(i);
                                    System.out.println(rule);
                                    sld.append(rule);
                                    currentClass++;
                            }
                    }
            }
                    
            sld.append("</sld:FeatureTypeStyle>");
            sld.append("</sld:UserStyle>");
            System.out.println("SLD");
                    
            System.out.println(sld.toString());
    }
    

    
    private String createStandardSLD() {
            StringBuilder sb = new StringBuilder();
            sb.append("<sld:Rule>");
            sb.append(addPolygonSymbolizerFillTag(1));
            sb.append("</sld:Rule>");
            return sb.toString();
    }
    
    private String createRule(String propertyName, String color, Integer currentClass, Integer intervals, Double startingValue, Double endValue) {
            StringBuilder rule = new StringBuilder();
            
            if ( currentClass != 0 ) {
                    rule.append("<sld:Rule>");
                    
                    // name
//                  rule.append(addNameTag(name));
                    
                    //title
                    
                    
                    // create filter
                    
                    
                    System.out.println("create rule: " + currentClass + " | " + intervals);
                    /** FIST RULE == PropertyIsLessThan **/
                    if( currentClass == 1) {
                            System.out.println("PropertyIsLessThan: " + currentClass+ " | " + intervals);
                            rule.append(addTitleTag("&lt; " + endValue.toString()));
                            rule.append("<ogc:Filter>");
                                    rule.append("<ogc:PropertyIsLessThan>");
                                    rule.append(addPropertyName(propertyName));
                                    rule.append(addLiteral(endValue.toString()));
                                    rule.append("</ogc:PropertyIsLessThan>");
                            
                            rule.append("</ogc:Filter>");
                    }
                    
                    /** 1 to n-2 RULE == PropertyIsGreaterThanOrEqualTo **/
                    else if( currentClass <= (intervals -1) && currentClass != 0) {
                            System.out.println("PropertyIsGreaterThanOrEqualTo: " + currentClass+ " | " + intervals);
                            rule.append(addTitleTag("&gt;= " + startingValue.toString() + " AND &lt;" + endValue.toString()));
                            rule.append("<ogc:Filter>");
                                    rule.append("<ogc:And>");
                                            rule.append("<ogc:PropertyIsGreaterThanOrEqualTo>");
                                            rule.append(addPropertyName(propertyName));
                                            rule.append(addLiteral(startingValue.toString()));
                                            rule.append("</ogc:PropertyIsGreaterThanOrEqualTo>");
                                            rule.append("<ogc:PropertyIsLessThan>");
                                            rule.append(addPropertyName(propertyName));
                                            rule.append(addLiteral(endValue.toString()));
                                            rule.append("</ogc:PropertyIsLessThan>");
                                    rule.append("</ogc:And>");
                            rule.append("</ogc:Filter>");
                    }
                    
                    
                    /** n-1 == PropertyIsGreaterThan **/
                    else if( currentClass == (intervals) ) {
                            rule.append(addTitleTag("&gt;= " + startingValue.toString()));
                            rule.append("<ogc:Filter>");
                            System.out.println("PropertyIsGreaterThanOrEqualTo: " + currentClass+ " | " + intervals);
                                    rule.append("<ogc:PropertyIsGreaterThanOrEqualTo>");
                                    rule.append(addPropertyName(propertyName));
                                    rule.append(addLiteral(startingValue.toString()));
                                    rule.append("</ogc:PropertyIsGreaterThanOrEqualTo>");
                            rule.append("</ogc:Filter>");
                    }
            
                    rule.append(addPolygonSymbolizerFillTag(currentClass));
                    // symbolizer
                    
                    
                    
                    
                    rule.append("</sld:Rule>");
            }
            return rule.toString();
    }
    
    private String addPolygonSymbolizerFillTag(Integer currentClass) {
            StringBuilder sb = new StringBuilder();
            sb.append("<sld:PolygonSymbolizer>");
            sb.append("<sld:Fill>");
            sb.append("<sld:CssParameter name=\"fill\">" + colors.get(currentClass - 1)+ "</sld:CssParameter>");
            
            sb.append("</sld:Fill>");
            sb.append("</sld:PolygonSymbolizer>");
            return sb.toString();
    }
    
    private String addNameTag(String name){
            StringBuilder sb = new StringBuilder();
            sb.append("<sld:Name>");
            sb.append(name);
            sb.append("</sld:Name>");
            return sb.toString();
    }
    
    private String addTitleTag(String title){
            StringBuilder sb = new StringBuilder();
            sb.append("<sld:Title>");
            sb.append(title);
            sb.append("</sld:Title>");
            return sb.toString();
    }
    
    private String addPropertyName(String propertyName){
            StringBuilder sb = new StringBuilder();
            sb.append("<ogc:PropertyName>");
            sb.append(propertyName);
            sb.append("</ogc:PropertyName>");
            return sb.toString();
    }
    
    private String addLiteral(String value){
            StringBuilder sb = new StringBuilder();
            sb.append("<ogc:Literal>");
            sb.append(value);
            sb.append("</ogc:Literal>");
            return sb.toString();
    }
    
    
//    public void _testMap() {
//		
//		try {
//			
//			String sldStyleName = "sld_" + String.valueOf(Math.floor(Math.random() * 100000)).replace(".", "");
//
//			GeoServerRESTPublisher publisher = new GeoServerRESTPublisher("http://127.0.0.1:8080/geoserver", "fenix", "nodefaultpw");
//		
//			
//			Integer intervals = 5;
//	    	HashMap<String, Double> map = new HashMap<String, Double>();
//	    	Random r=new Random();
//	    	for(int i=2; i < 302; i++) {
//	    		double randomValue = r.nextInt(1000);
//	    		map.put(String.valueOf(i), randomValue);
//	    	}
//
//	    	System.out.println("creating SLD");
//	    	String sld = SLDCreator.createSLDQuantile(sldStyleName, sldStyleName, "ADM0_CODE", map, intervals, null);
//	    	System.out.println(sld);
//
//	    	boolean created = publisher.publishStyle(sld);
//	    	System.out.println(created);
//
//	    	
//	     ClientMapView mapClient = new ClientMapView();	
//	   	 ClientGeoView layerJoin = new ClientGeoView();
//		 layerJoin.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
//		 layerJoin.setLayerName("g2008_0");
//		 layerJoin.setStyleName(sldStyleName);
//		 layerJoin.setTransparent(false);
//		 mapClient.addLayer(layerJoin);
//		 
//		 
//		 ClientGeoView layer = new ClientGeoView();
//		 layer.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
//		 layer.setLayerName("g2008_0");
//		 layer.setStyleName("zzlayer_gaul0");
//		 layer.setTransparent(true);
//		 mapClient.addLayer(layer);
//
//		 
//		 System.out.println(LeafletMap.createLeaftLetMap(mapClient, "map","map", "map"));
//			
//		} catch (Exception e) {
//		}
//		
//	}
//    
//    
//    public void testMapGAUL1() {
//		
//  		try {
//  			
//  			String sldStyleName = "sld_" + String.valueOf(Math.floor(Math.random() * 100000)).replace(".", "");
//
//  			GeoServerRESTPublisher publisher = new GeoServerRESTPublisher("http://127.0.0.1:9090/geoserver", "fenix", "nodefaultpw");
//  		
//  			
//  			Integer intervals = 5;
//  	    	HashMap<String, Double> map = new HashMap<String, Double>();
//  	    	Random r=new Random();
//  	    	for(int i=1000; i < 5000; i++) {
//  	    		double randomValue = r.nextInt(100000);
//  	    		map.put(String.valueOf(i), randomValue);
//  	    	}
//
//  	    	System.out.println("creating SLD");
//  	    	String sld = SLDCreator.createSLDQuantile(sldStyleName, sldStyleName, "ADM1_CODE", map, intervals, null);
////  	    	System.out.println(sld);
//
//  	    	boolean created = publisher.publishStyle(sld);
//  	    	System.out.println(created);
//
//  	    	
//  	     ClientMapView mapClient = new ClientMapView();	
//  	   	 ClientGeoView layerJoin = new ClientGeoView();
//  		 layerJoin.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
//  		 layerJoin.setLayerName("g2008_1");
//  		 layerJoin.setStyleName(sldStyleName);
//  		 layerJoin.setTransparent(false);
//  		 mapClient.addLayer(layerJoin);
//  		 
//  		 
//  		 ClientGeoView layer = new ClientGeoView();
//  		 layer.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
//  		 layer.setLayerName("g2008_0");
//  		 layer.setStyleName("zzlayer_gaul0");
//  		 layer.setTransparent(true);
//  		 mapClient.addLayer(layer);
//
//  		 
//  		 System.out.println(LeafletMap.createLeaftLetMap(mapClient, "map","map", "map"));
//  			
//  		} catch (Exception e) {
//  		}
//  	}
    
}
