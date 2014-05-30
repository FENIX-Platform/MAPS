package org.fao.fenix.maps.core.rest;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.fao.fenix.maps.bean.map.BBox;
import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.bean.map.MAPSBean;
import org.fao.fenix.maps.sld.SLDColors;
import org.fao.fenix.maps.sld.SLDCreator;
import org.fao.fenix.maps.util.LeafletMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class QuatileTest extends TestCase {
  
	
	public void testPost() {
	    // Send data
		try {
			String data = "engine=leaflet&baselayers=osm&layers=gaul_faostat&styles=join&joincolumn=FAOST_CODE&joindata=[(270,1100),(124,900),(68,850),(238,1110),(202,800),(12,407),(100,450),(194,700),(185,1690),(41,1790),(39,1300),(33,215),(10,1850),(231.,1850),(21,2600),(4,400),(124,500),(250,1640),(215,920),(233,123),(129,1400),(133,1200),(12,1900),(11,1900),(1,1900),(52,200),(23,1339),(2,200),(3,300),(5,600),(6,550),(7,850),(8,950),(9,100),(20,950),(55,100),(65,1950),(85,780)]&colorramp=Reds&intervals=6&addborders=true";
		    URL url = new URL("http://ldvapp07.fao.org:8030/maps/api");
		    URLConnection conn;
			conn = url.openConnection();	
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();
	
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		     		System.out.println(line);
		    }
		    wr.close();
	    rd.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	
	
	
	
//	public void _testEqualInterval() {
//		double[] values = new double[100];
//		for(int i=0; i < 100; i++) {
//			values[i] = i+1;
//		}
//		
//		for(int a = 0 ; a < values.length; a++) {
//			System.out.println(values[a]);
//		}
//		List<Double> v =SLDCreator.getEqualInterval(values, 10);
//		System.out.println(v);
//		
////		HashMap<String, Double> hm = new HashMap<String, Double>();
////		hm.put("11", new Double(10));
////		hm.put("112", new Double(20));
////		hm.put("113", new Double(30));
////		hm.put("114", new Double(40));
////		
////		List<Double> v1 =SLDCreator.getQuantiles(hm, 3);
////		System.out.println(v1);
//	}
    
}
