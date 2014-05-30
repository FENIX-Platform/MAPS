package org.fao.fenix.maps.core.rest;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.sld.SLDCreator;
import org.fao.fenix.maps.sld.legend.LegendCreator;
import org.fao.fenix.maps.util.LeafletMap;
import org.fao.fenix.maps.util.Parser;

public class MapTest extends TestCase {

	public void _testMap() {
		
		MapView map = new MapView();
		
		 Layer layerJoin = new Layer();
		 layerJoin.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
		 layerJoin.setLayerName("g2008_0");
		 layerJoin.setStyleName("gaul0_test");
		 layerJoin.setTransparent(false);
		 map.addLayer(layerJoin);
		 
		 
		 Layer layer = new Layer();
		 layer.setGetMapUrl("http://127.0.0.1:8080/geoserver/wms");
		 layer.setLayerName("g2008_0");
		 layer.setStyleName("zzlayer_gaul0");
		 layer.setTransparent(true);
		 map.addLayer(layer);
		 

		 
//		 System.out.println(LeafletMap.createLeaftLetMap(map, "map", "map", "map"));
	}
	
	
//	public void _testMapGAUL0Join() {
//		try {
//			String sldStyleName = "sld_" + String.valueOf(Math.floor(Math.random() * 100000)).replace(".", "");
//
//			String propertyColumn = "ADM0_CODE";
//			GeoServerRESTPublisher publisher = new GeoServerRESTPublisher("http://127.0.0.1:9090/geoserver", "fenix", "nodefaultpw");
//
//			Integer intervals = 5;
//			HashMap<String, Double> map = new HashMap<String, Double>();
//			Random r = new Random();
//			for (int i = 0; i < 500; i++) {
//				double randomValue = r.nextInt(10000);
//				map.put(String.valueOf(i), randomValue);
//			}
//
//			System.out.println("creating SLD");
//			String sld = SLDCreator.createSLDQuantile(sldStyleName,
//					sldStyleName, propertyColumn, map, intervals, "PuRd");
//			// System.out.println(sld);
//
//			boolean created = publisher.publishStyle(sld);
//			System.out.println(created);
//
//			ClientMapView mapClient = new ClientMapView();
//			ClientGeoView base = new ClientGeoView();
//			base.setGetMapUrl("http://fenix.fao.org:8050/geoserver/wms");
//			base.setLayerName("bluemarble");
//			base.setTransparent(false);
//			mapClient.addLayer(base);
//
//			ClientGeoView layerJoin = new ClientGeoView();
//			layerJoin.setGetMapUrl("http://127.0.0.1:9090/geoserver/wms");
//			layerJoin.setLayerName("zlayer_26134");
//			layerJoin.setStyleName(sldStyleName);
//			layerJoin.setTransparent(true);
//			mapClient.addLayer(layerJoin);
//
////			ClientGeoView layer = new ClientGeoView();
////			layer.setGetMapUrl("http://127.0.0.1:9090/geoserver/wms");
////			layer.setLayerName("zlayer_26134");
////			layer.setStylename("zzlayer_gaul0");
////			layer.setTransparent(true);
////			mapClient.addLayer(layer);
//
//			System.out.println(LeafletMap.createLeaftLetMap(mapClient,"map",
//					"map", "map"));
//
//		} catch (Exception e) {
//		}
//	}
	
	
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
	
//	public void testBigInteger() {
//		List<Double> l = new ArrayList<Double>();
//		l.add(123.23333);
//		l.add(1223.123333);
//		System.out.println(Parser.roundValues(l, 0));
//	}
	
}
