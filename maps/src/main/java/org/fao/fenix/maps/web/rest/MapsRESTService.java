/**
 *
 * FENIX (Food security and Early warning Network and Information Exchange)
 *
 * Copyright (c) 2011, by FAO of UN under the EC-FAO Food Security
Information for Action Programme
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.fao.fenix.maps.web.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.fao.fenix.maps.configurations.GeoserverConf;
import org.fao.fenix.wds.core.exception.WDSException;
import org.fao.fenix.wds.core.utils.Wrapper;
import org.fao.fenix.maps.bean.map.BaseLayer;
import org.fao.fenix.maps.bean.map.BBox;
import org.fao.fenix.maps.bean.map.GUIOptions;
import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.bean.map.MAPSBean;
import org.fao.fenix.maps.bean.map.join.JoinLayer;
import org.fao.fenix.maps.configurations.BaseLayerConf;
import org.fao.fenix.maps.configurations.LeafletConf;
import org.fao.fenix.maps.configurations.MapsConf;
import org.fao.fenix.maps.constants.BASELAYER;
import org.fao.fenix.maps.constants.CLASSIFICATION;
import org.fao.fenix.maps.constants.ENGINE;
import org.fao.fenix.maps.constants.GUIOPTIONS;
import org.fao.fenix.maps.constants.JOINLAYER;
import org.fao.fenix.maps.constants.KEYWORDS;
import org.fao.fenix.maps.constants.OUTPUT;
import org.fao.fenix.maps.constants.WMS;
import org.fao.fenix.maps.constants.join.BOUNDARIES;
import org.fao.fenix.maps.constants.join.JOINTYPE;
import org.fao.fenix.maps.constants.join.queries.CROWDPRICES;
import org.fao.fenix.maps.export.WMSMapRetriever;
import org.fao.fenix.maps.join.JoinLayerUtils;
import org.fao.fenix.maps.util.DataServiceUtils;
import org.fao.fenix.maps.util.GetFeatureInfoREST;
import org.fao.fenix.maps.util.LeafletMap;
import org.fao.fenix.maps.util.Parser;
import org.fao.fenix.maps.util.TinyClient;
import org.fao.fenix.maps.web.utils.GetFeatureInfoUtils;
import org.fao.fenix.maps.web.utils.MapUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
@SuppressWarnings("serial")
public class MapsRESTService extends HttpServlet implements Servlet {
	
	
	private static final Logger LOGGER = Logger.getLogger(MapsRESTService.class);
 
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
//		LOGGER.info("start request");
		try {
			// timing
			long t0 = System.currentTimeMillis();
			
			// prepare output
			String output = parse(request, request.getParameterMap());	
			Map<String,String[]> parameters =request.getParameterMap();
			OUTPUT outputtype = OUTPUT.valueOf("HTML");
			for(String key: parameters.keySet()) {
				try {
					KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());					
					String value =  parameters.get(key)[0];
					switch (k) {
						case out: outputtype = OUTPUT.valueOf(value.toUpperCase()); break;
					}
				} catch (IllegalArgumentException e) {}
			}
			
			// JSONP callback
			String jsonCallbackParam = request.getParameter("jsoncallback");
			if (jsonCallbackParam != null) {
				output = jsonCallbackParam + output;
			}

            LOGGER.info(outputtype);
		
			switch (outputtype) {
				case HTML: response.setContentType("text/html"); break;
				case JS: response.setContentType("text/js"); break;
                case JOIN: response.setContentType("text/js"); break;
				default: response.setContentType("text/html"); break;
			}
//			response.setContentType("application/json");
			response.setContentLength(output.length());
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(output);
			
			// close stream
			out.close();
			out.flush();
			
//			LOGGER.info(output);

			// timing
			long t1 = System.currentTimeMillis();
			LOGGER.info("Map request completed in " + (t1 - t0) + " milliseconds.");
			
		} catch (WDSException e) {
			handleException(response, e.getMessage());
		} catch (IllegalAccessException e) {
			handleException(response, e.getMessage());
		} catch (InstantiationException e) {
			handleException(response, e.getMessage());
		} catch (SQLException e) {
			handleException(response, e.getMessage());
		} catch (ClassNotFoundException e) {
			handleException(response, e.getMessage());
		} catch (InvocationTargetException e) {
			handleException(response, e.getMessage());
		} catch (NoSuchMethodException e) {
			handleException(response, e.getMessage());
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, WDSException {
		doPost(request, response);
	}

	private String parse(HttpServletRequest request, Map<String,String[]> parameters) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, WDSException, IOException {
		String s = "";
		
//		LOGGER.info(parameters);
		
		MAPSBean mapBean = new MAPSBean();
		MapView mapView = mapBean.getMapView();
		List<Layer> layers = mapView.getLayerList();
		List<BaseLayer> baselayers = mapView.getBaseLayerList();
		
		// set height, width, bbox
		setMapOptions(parameters, mapBean);
		
		// set GUI options
		setMapGUIOptions(parameters, mapBean.getOptions());
		
		// base layers
		setBaseLayers(parameters, baselayers, mapBean.getLang());
		
		// layers
		boolean hasBaseLayers = false;
		if ( !baselayers.isEmpty())
			hasBaseLayers = true;
		setLayers(parameters, layers, hasBaseLayers, mapBean.getOptions());
		
		// joinlayer (the set of the jointype if used to reduce the time parsing)
		mapView.setJointype(setJoinLayer(parameters, layers, mapBean.getLang(), mapBean.getFeatureInfo()));
	
		// TODO: How to handle a layer if it's not in our geoserver?
		for(Layer layer : layers) {
			setWMSUrl(layer);
		}

		// Switching to create map, featureinfo, export...
		if ( mapBean.getFeatureInfo() )
			s = getFeatureInfo(mapBean);
		else if ( mapBean.isExport() )
			s = exportMap(mapBean);
		else if ( mapBean.isJsoncallback() )
			s = jsoncallback(mapBean, parameters);
		else
			s = createMap(request, parameters, mapBean);
		return s;
	}
	
	private void setMapOptions(Map<String,String[]> parameters, MAPSBean mapBean) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException{
	
		// set random variable prefix
		mapBean.setVariableSuffix(String.valueOf(Math.floor(Math.random() * 100000)).replace(".", ""));
		
		for(String key: parameters.keySet()) {
			try {
				KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());
//				LOGGER.info(key.toLowerCase() + " - " + parameters.get(key)[0]);
				String value = parameters.get(key)[0];
				switch (k) {
					case out: mapBean.setOutputType(OUTPUT.valueOf(value.toUpperCase())); break;
					case renderto: mapBean.setRenderTo(value.toLowerCase()); break;
					case height: mapBean.setHeight(value); break;
					case width: mapBean.setWidth(value); break;
					case bbox: parseBBox(value, mapBean.getMapView());   break;
					case srs: setSRS(value, mapBean.getMapView());   break;
					case zoom: mapBean.getMapView().setZoom(Integer.valueOf(value)); break;
					case minzoom: mapBean.getMapView().setMinzoom(Integer.valueOf(value)); break;
					case maxzoom: mapBean.getMapView().setMaxzoom(Integer.valueOf(value)); break;
					case lat: mapBean.getMapView().setLat(Double.valueOf(value)); break;
					case lon: mapBean.getMapView().setLon(Double.valueOf(value)); break;
					case lang: mapBean.setLang(value.toLowerCase()); break;
					case shared: mapBean.setShared(Boolean.valueOf(value)); break;
					case getfeatureinfo: mapBean.setGetFeatureInfo(Boolean.valueOf(value)); break;
					case enablejoingfi: mapBean.setEnableJoinGFI(Boolean.valueOf(value)); break;
					case x: mapBean.setX(value); break;
					case y: mapBean.setY(value); break;
					case export: mapBean.setExport(Boolean.valueOf(value)); break;
					case zoomto: //administrative unit, bbox;
								mapBean.getMapView().setBbox(zoomToAdministrativeUnit(value)); break;
//					case jsoncallback: // handle jsoncallback
//								mapBean.setJsoncallback(true); break;
				}
			} catch (IllegalArgumentException e) {}
		}
	}
	
	private void setMapGUIOptions(Map<String,String[]> parameters, GUIOptions guiOptions) {
		for(String key: parameters.keySet()) {
			try {
				GUIOPTIONS k = GUIOPTIONS.valueOf(key.toLowerCase());
				String value = parameters.get(k.name())[0];
				switch (k) {
					case addexport: guiOptions.setAddExport(Boolean.valueOf(value)); break;
					case addlayerscontrol: guiOptions.setAddLayersControl(Boolean.valueOf(value)); break;
					case showcontroloverlays: guiOptions.setShowControlOverlays(Boolean.valueOf(value)); break;
					case addlayerlabelscontrol: guiOptions.setAddLayerLabelscontrol(Boolean.valueOf(value)); break;
					
				}
			} catch (IllegalArgumentException e) {}
		}
	}
	
	private void setLayers(Map<String,String[]> parameters, List<Layer> layers, boolean hasBaseLayers, GUIOptions guiOptions) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException{
		for(String key: parameters.keySet()) {
			try {
				KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());
				String value = parameters.get(key)[0];
				switch (k) {
					case layers: parseLayers(value, layers, hasBaseLayers); break;
//					case styles: parseStyles(value, layers); break;
//					case cql_filter: parseCql_filter(value, layers); break;
//					case bgcolor: layers.get(0).setBgcolor(value); break;
				}
			} catch (IllegalArgumentException e) {}
		}
		
		for(String key: parameters.keySet()) {
			try {
				KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());
				String value = parameters.get(key)[0];
				switch (k) {
					case layertitle: parseLayerTitle(value, layers); break;
					case hidden: parseHidden(value, layers); break;
					case styles: parseStyles(value, layers); break;
					case cql_filter: parseCqlFilters(value, layers); break;
					case bgcolor: layers.get(0).setBgcolor(value); break;
				}
			} catch (IllegalArgumentException e) {}
		}
		// addlayers labels TODO: do it in a cleared way
		// it's set afterwards because the label layers should be on top
		for(String key: parameters.keySet()) {
			try {
				KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());
				String value = parameters.get(key)[0];
				switch (k) {
					case addlayerlabels: layers.add(addLayerLabels(value));
										 guiOptions.setAddLayerLabelscontrol(true);
					break;
					
				}
			} catch (IllegalArgumentException e) {}
		}
	}
	
	private void setBaseLayers(Map<String,String[]> parameters, List<BaseLayer> layers, String lang){
		// layers
		try {
			String value = parameters.get(KEYWORDS.baselayers.name())[0];
			parseBaseLayers(value, layers, lang);
		}catch (Exception e) {}
	}
	
	private JOINTYPE setJoinLayer(Map<String,String[]> parameters, List<Layer> layers, String language, boolean isGetFeatureInfo) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException{
		JOINTYPE jointype = null;
		// TODO: probably should not be here, but at the creating of the html map
		boolean isJoin = false;
		Layer joinLayer = null;
		for(Layer layer : layers) {
			if ( layer.isJoin() ) {
				joinLayer = layer;
				isJoin = true;
				break;
			}
		}
		// if the call contains a join, set the parameters
		if ( isJoin) {
			jointype = setJoinLayerOptions(parameters, joinLayer, language, isGetFeatureInfo);
		}
		return jointype;
	}
	
	private JOINTYPE setJoinLayerOptions(Map<String,String[]> parameters, Layer joinLayer, String language, Boolean isGetFeatureInfo) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
		JOINTYPE jointype = JOINTYPE.shaded;
		for(String key: parameters.keySet()) {
			try {
				JOINLAYER k = JOINLAYER.valueOf(key.toLowerCase());
				String value = parameters.get(key)[0];
//				LOGGER.info(k + " | " + value);
				switch (k) {
					case joincolumn: parseJoinColumn(value, joinLayer); break;
					case columnlabel: joinLayer.getJoinLayer().setColumnlabel(value); break;
					case joindata:  joinLayer.getJoinLayer().setJoindata(parseJoinData(value));
									joinLayer.getJoinLayer().setJoindataString(value);
									break;
					case intervals: joinLayer.getJoinLayer().setIntervals(Integer.valueOf(value)); break;
					case colorramp: joinLayer.getJoinLayer().setColorramp(value); break;
					case colorreverse: joinLayer.getJoinLayer().setColorreverse(Boolean.valueOf(value)); break;
					case colors: joinLayer.getJoinLayer().setColors(parseColors(value)); break;
					case ranges: joinLayer.getJoinLayer().setRanges(Parser.extractSortedDoules(value, ",")); break;
					case classification: joinLayer.getJoinLayer().setClassification(value); break;
					case joinboundary:  joinLayer.getJoinLayer().setBoundary(value.toUpperCase()); 
										// setting the joincolumn
										BOUNDARIES boundary = BOUNDARIES.valueOf(joinLayer.getJoinLayer().getBoundary());
										joinLayer.getJoinLayer().setJoincolumn(boundary.getColumnName());
					break;
					case pointdata: parsePointData(value); break;
					case mu: joinLayer.getJoinLayer().setMeasurementUnit(value.replace("+", " ")); break;
					case date: joinLayer.getJoinLayer().setDate(value); break;
					case jointype: joinLayer.getJoinLayer().setJointype(value);
								   jointype = JOINTYPE.valueOf(value);
					break;
					case addborders: joinLayer.getJoinLayer().setAddBorders(Boolean.valueOf(value)); break;					
					case borderscolor: joinLayer.getJoinLayer().setBordersColor(value); break;
					case bordersstroke: joinLayer.getJoinLayer().setBordersStroke(value); break;
					case bordersopacity: joinLayer.getJoinLayer().setBordersOpacity(value); break;
					case thousandseparator: joinLayer.getJoinLayer().setThousandSeparator(value); break;
					case decimalseparator: joinLayer.getJoinLayer().setDecimalSeparator(value); break;
					case decimalnumbers: joinLayer.getJoinLayer().setDecimalNumbers(Integer.valueOf(value)); break;
				}
			} catch (IllegalArgumentException e) {}
		}
		
		// layers
		try {
			String value = parameters.get(KEYWORDS.legendtitle.name())[0];
			// TODO: parse it better, not just replace
			joinLayer.getJoinLayer().getLegend().setTitle(value.replace("+", " "));
		}catch (Exception e) {}
		// layers
		try {
			String value = parameters.get(KEYWORDS.legendposition.name())[0];
			joinLayer.getJoinLayer().getLegend().setPosition(value);
		}catch (Exception e) {}
		
		try {
			// this performs the rounding of the decimal numbers before to be passed to the algorithm 
			// to generate the SLD
			if (joinLayer.getJoinLayer().getDecimalNumbers() != null ) {
				if ( joinLayer.getJoinLayer().getDecimalNumbers() >= 0 ) {
					joinLayer.getJoinLayer().setJoindata(MapUtils.roundValues(joinLayer.getJoinLayer().getJoindata(), joinLayer.getJoinLayer().getDecimalNumbers()));
					joinLayer.getJoinLayer().setJoindataString(MapUtils.valuesString(joinLayer.getJoinLayer().getJoindata()));
				}				
			}
		}catch (Exception e) {}
		
		
		// Creating the joinLayer
		if ( !isGetFeatureInfo )
			createJoinLayer(joinLayer, language);
		
		return jointype;
	}
	
	private void createJoinLayer(Layer joinLayer, String language) {
		// TODO: here to get the areas? (in theory should be performed on the layer table)
		CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getJoinLayer().getClassification());
		switch (c) {
			case equalarea: joinLayer.getJoinLayer().setAreas(getAreas(joinLayer.getJoinLayer().getBoundary(), joinLayer.getJoinLayer().getJoindata())); break;
		}
		
		JOINTYPE j = JOINTYPE.valueOf(joinLayer.getJoinLayer().getJointype());
		switch (j) {
			case shaded: createShadedJoinLayer(joinLayer); break;
			case point: createPointJoinLayer(joinLayer.getJoinLayer(), language); break;
		}
	}
	
	private Map<String, Double> getAreas(String boundary, Map<String, Double> values) {
//		LOGGER.info("getAreas");
		Map<String, Double> result = new HashMap<String, Double>();
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		DataServiceUtils dataServiceUtils = (DataServiceUtils) wac.getBean("dataServiceUtils");
		BOUNDARIES b = BOUNDARIES.valueOf(boundary);
		try {
			result = dataServiceUtils.getAreas(b.getConversionTable(), b.getColumnName(), "area_dd", values);
		} catch (AxisFault e) {e.printStackTrace();}
		
//		LOGGER.info("result: " + result);
		return result;
	}
	
	private void createShadedJoinLayer(Layer joinLayer) {
		// create join
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		JoinLayerUtils joinLayerUtils = (JoinLayerUtils) wac.getBean("joinLayerUtils");	
		ServletContext servletContextMap = this.getServletConfig().getServletContext();
		WebApplicationContext wacMap = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextMap);
		MapsConf mapsConf = (MapsConf) wacMap.getBean("mapsConf");

		/** TODO: issue with the namespace: fenix, set on the configuration file?**/
		String layername = joinLayer.getLayerName().replace("fenix:", "");
		layername = "fenix:" + layername;
		joinLayer.setLayerName(layername);
		String sldFilename = joinLayerUtils.createJoinSLD(layername, mapsConf.getStylesPath(), joinLayer.getJoinLayer(), null);
		joinLayer.setStyleURL(getSldURL(mapsConf.getUrl(), sldFilename));
	}
	
	private String getSldURL(String mapURL, String sldFilename) {
		String sldURL = "http://" + mapURL + "/styles/" + sldFilename;
		return sldURL;
	}
	

	
	private void createPointJoinLayer(JoinLayer joinLayer, String language) {
//		LOGGER.info("createPointJoinLayer");
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		DataServiceUtils dataServiceUtils = (DataServiceUtils) wac.getBean("dataServiceUtils");
		BOUNDARIES boundary = BOUNDARIES.valueOf(joinLayer.getBoundary());
		try {
			joinLayer.setJoininfo(dataServiceUtils.getPointData(boundary.getConversionTable(),boundary.getColumnName(), joinLayer.getJoindata(), language));
		} catch (IOException e) {e.printStackTrace();}
		
		JoinLayerUtils joinLayerUtils = (JoinLayerUtils) wac.getBean("joinLayerUtils");	
		joinLayerUtils.createPoints(joinLayer);
	}
	
	private BBox zoomToAdministrativeUnit(String value) {
		// parse value
		String boundaryname = Parser.extractValue(value, "(");
		BOUNDARIES boundary = BOUNDARIES.valueOf(boundaryname.toUpperCase());
		
		// for now get's the single value;
		String code = Parser.extractValue(value, "(", ")");
		
//		LOGGER.info(boundary.getConversionTable() + " | " + boundary.getColumnName());
//		LOGGER.info(code);
		// TODO: not hardcode the SRS
		return zoomToAdministrativeUnit(boundary.getConversionTable(), boundary.getColumnName(), code, "EPSG:3857");
	}

	private BBox zoomToAdministrativeUnit(String tablename, String columnname, String code, String srs) {
		BBox bbox = null;
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		DataServiceUtils dataServiceUtils = (DataServiceUtils) wac.getBean("dataServiceUtils");
		try {
			bbox = dataServiceUtils.getBBox(tablename, columnname, code, srs);
		} catch (IOException e) {e.printStackTrace();}
		return bbox;
	}   
	
	private String createMap(HttpServletRequest request, Map<String,String[]> parameters, MAPSBean mapBean) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
		String s = null;
		try {
			String value = parameters.get(KEYWORDS.engine.name())[0];
			switch (ENGINE.valueOf(value)) {
				case leaflet: s = createLeafletMap(request, mapBean); break;
				default: s = createLeafletMap(request, mapBean); break;
			}
		}catch (Exception e) {
			// LeafLet is used as default engine 
			s = createLeafletMap(request, mapBean);
		}
		return s;
	}
	
	private void parseBaseLayers(String value, List<BaseLayer> layers, String lang) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ",");
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		BaseLayerConf baseLayerConf = (BaseLayerConf) wac.getBean("baseLayerConf");
		for (int i = 0; i < lv.size(); i++) {
			String v = lv.get(i);
			BaseLayer layer = new BaseLayer();
			layer.setLayerName(v);
			try {
				BASELAYER c = BASELAYER.valueOf(v.toUpperCase());
				layer.setTile(true);
				switch (c) {
					case OSM: layer.setGetMapUrl(baseLayerConf.getOsmURL());break;
					case MAPQUEST: layer.setGetMapUrl(baseLayerConf.getMapquestURL()); break;
					case MAPQUEST_NASA: layer.setGetMapUrl(baseLayerConf.getMapquestNASAURL()); break;
					default:break;
				}
				layer.setLayerTitle(c.getName(lang));
				layers.add(layer);
			} catch (Exception e) {}
		}
	}
	
	private void parseLayers(String value, List<Layer> layers, boolean hasBaseLayers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ",");	
		for ( int i=0; i < lv.size(); i++) {
			String v = lv.get(i);
			Layer layer = new Layer();
			layer.setLayerName(v);
			layers.add(layer);
			// TODO: if BaseLayers are missing set to true
			if (i == 0 && !hasBaseLayers ) {
				layers.get(i).setTransparent(false);
			}
		}
	}
	
	private Layer addLayerLabels(String value) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		Layer layer = new Layer();	
		BOUNDARIES boundary = BOUNDARIES.valueOf(value.toUpperCase());
		layer.setLayerName(boundary.getLayerName());
		layer.setStyleName(boundary.getLabelsSLD());
		layer.setHidden(true);
		layer.setLabels(true);
		/* TODO: internationalize **/
		layer.setLayerTitle("Labels");
		return layer;
	}
	
	private void parseLayerTitle(String value, List<Layer> layers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ",");
		for (int i=0; i < lv.size(); i++) {
			String v = lv.get(i);
			if ( !v.equals("") ) {
				layers.get(i).setLayerTitle(v);
			}
		}
	}

    private void parseLayerName(String value, List<Layer> layers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
        List<String> lv = Parser.extractValues(value, ",");
        for (int i=0; i < lv.size(); i++) {
            String v = lv.get(i);
            if ( !v.equals("") ) {
                layers.get(i).setLayerName(v);
            }
        }
    }
	
	private void parseHidden(String value, List<Layer> layers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ",");
		for (int i=0; i < lv.size(); i++) {
			String v = lv.get(i);
			if ( !v.equals("") ) {
				LOGGER.info("set: " + i+ " " +Boolean.valueOf(v) + " | " + v);
				layers.get(i).setHidden(Boolean.valueOf(v));
			}
		}
	}
	
	private void parseStyles(String value, List<Layer> layers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ",");
		for (int i=0; i < lv.size(); i++) {
			String v = lv.get(i);
			if ( !v.equals("") ) {
				layers.get(i).setStyleName(v);
				if (v.toLowerCase().equals("join")) {
					// this is to set a join layer
					layers.get(i).setJoin(true);
				}
			}
		}
	}
	
	private void parseCqlFilters(String value, List<Layer> layers) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		List<String> lv = Parser.extractValues(value, ";");
		for (int i=0; i < lv.size(); i++) {
			String v = lv.get(i);
			if ( !v.equals("") ) {
				layers.get(i).setCql_filter(v.replace(" ", "+"));
			}
		}
	}

	private void parseBBox(String value, MapView mapView) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		//bbox=-179.25,-50.25,180.25,72.25
		List<String> lv = Parser.extractValues(value, ",");
		try {

			Double minlon = Double.valueOf(lv.get(0));
			Double minlat = Double.valueOf(lv.get(1));
			Double maxlon = Double.valueOf(lv.get(2));
			Double maxlat = Double.valueOf(lv.get(3));

			BBox bbox = new BBox();
			if ( mapView.getBbox() != null )
				bbox = mapView.getBbox();			
			bbox.setBBox(minlon, minlat, maxlon, maxlat);
			mapView.setBbox(bbox);
		}catch (Exception e) {
			LOGGER.info("error on the Bounding Box definition: " );
		}
	}
	
	private void setSRS(String value, MapView mapView) {
		BBox bbox = new BBox();
		if ( mapView.getBbox() != null )
			bbox = mapView.getBbox();			
		bbox.setSrs(value);
		
		mapView.setBbox(bbox);
//		LOGGER.info("SRS:" + mapView.getBbox().getSrs());
	}

	private void setWMSUrl(Layer layer) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		GeoserverConf geoserverConf = (GeoserverConf) wac.getBean("geoserverConf");
		WMS c = WMS.valueOf(layer.getWms());
		switch (c) {
		case DEFAULT:
			layer.setGetMapUrl(geoserverConf.wmsurl);
			break;
		}
	}
	
	// JOIN 
	private void parseJoinColumn(String value, Layer layer) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		layer.getJoinLayer().setJoincolumn(value);
	}
	
	private Map<String, Double> parseJoinData(String value) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		// the join boolean it's already been setted from the styles		
		Map<String, Double> joindata = new HashMap<String, Double>();
		String data = Parser.extractValue(value, "[", "]");
		List<String> singleValues = Parser.extractValues(data, "(", ")");
		for(String singleValue : singleValues) {
			 List<String> d = Parser.extractValues(singleValue, ",");
			 // the first value is the featurecode
			 // the second is the right value
			 try {
				 joindata.put(d.get(0), Double.valueOf(d.get(1)));
			 } catch (Exception e) {}
		}
//		LOGGER.info("data:" +joindata);
		return joindata;
	}
	
	// POINTDATA 
	private void parsePointData(String value) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
	}
	
	private List<String> parseColors(String value){
		List<String> l = new ArrayList<String>();		
		List<String> cs = Parser.extractValues(value, ",");
		for(String c : cs) {
			l.add("#" + c);
		}
		return l;
	}
	
	private String getFeatureInfo(MAPSBean mapBean) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException {
		String s = null;
//		LOGGER.info("getFeatureInfo");
		// call the server WMS (check if specified)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		GeoserverConf geoserverConf = (GeoserverConf) wac.getBean("geoserverConf");
		
		try {
			Layer layer = mapBean.getMapView().getLayerList().get(0);
			String layername = layer.getLayerName();
			/** TODO: get the joinboudary **/
			String joincolumn = layer.getJoinLayer().getJoincolumn();
			String columnlabel = layer.getJoinLayer().getColumnlabel();
			String lang = mapBean.getLang();
			String measurementunit = layer.getJoinLayer().getMeasurementUnit();
		
//			measurementunit= StringEscapeUtils.escapeHtml(measurementunit);

			Map<String, Double> values = layer.getJoinLayer().getJoindata();
			String width = mapBean.getWidth();
			String height = mapBean.getHeight();
			String x = mapBean.getX();
			String y = mapBean.getY();
			String bbox = mapBean.getMapView().getBbox().toBBOX();
			String srs = mapBean.getMapView().getBbox().getSrs();
			
			s = GetFeatureInfoREST.getJoinFeatureInfo(geoserverConf.wmsurl, layername, joincolumn, columnlabel, values, measurementunit, lang, width, height, x, y, bbox, srs);
		}catch (Exception e) {}
		return s;
	}
	
	private String exportMap(MAPSBean mapBean) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {
		String s = "";
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wacMap = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		MapsConf mapsConf = (MapsConf) wacMap.getBean("mapsConf");
		
//		LOGGER.info("bbox: " + mapBean.getMapView().getBbox().getSrs());
//		LOGGER.info("bbox: " + mapBean.getMapView().getBbox().toBBOX());
		String filename = WMSMapRetriever.getImage(mapsConf.getImagesPath(), mapBean);
		String path = getImageURL(mapsConf.getUrl(), filename);
		s = path;
		return s;
	}
	
	private String getImageURL(String mapURL, String filename) {
		String sldURL = "http://" + mapURL + "/export/" + filename;
		String html = "<META HTTP-EQUIV=\"Refresh\"  CONTENT=\"1; URL="+ sldURL+"\">";
		return html;
	}

	private String jsoncallback(MAPSBean mapBean, Map<String,String[]> parameters) throws WDSException, IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException, IOException {	
		String s = "";
		List<String> crowdprices = new ArrayList<String>();
		try {
			String values = parameters.get(CROWDPRICES.crowdprices.name())[0];
			crowdprices = Parser.extractValues(values, ",");
		}catch (Exception e) {}
		
		if ( crowdprices.isEmpty()) {
			if ( mapBean.getMapView().getBbox() != null ) {
				ServletContext servletContext = this.getServletConfig().getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
				DataServiceUtils dataServiceUtils = (DataServiceUtils) wac.getBean("dataServiceUtils");
				try {
					
					//italy
					//6.61,35.49,18.51,47.09
					// lon lat = 12.50,41.89
					
					BBox bbox = mapBean.getMapView().getBbox();
					System.out.println("bbox: " + bbox.toBBOX());
					String ymin = String.valueOf(bbox.getYmin());
					String xmin = String.valueOf(bbox.getXmin());
					String ymax = String.valueOf(bbox.getYmax());
					String xmax = String.valueOf(bbox.getXmax());
					
		//			dataServiceUtils.getPointFromBBox(tablename, ymin, xmin, ymax, xmax);
					s = dataServiceUtils.getPointFromBBox("geo_conversion_table_2", xmin, ymin, xmax, ymax);
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		else{
				ServletContext servletContext = this.getServletConfig().getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
				DataServiceUtils dataServiceUtils = (DataServiceUtils) wac.getBean("dataServiceUtils");
				
				try {
					s = dataServiceUtils.getCrowdPricesData("crowdprices_data", crowdprices.get(0), crowdprices.get(1));
				} catch (IOException e) {e.printStackTrace();}
		}
//		LOGGER.info(s);
		return s;
	}
	
	// Leaflet Map TODO: slipt the class with a switch
	private String createLeafletMap(HttpServletRequest request, MAPSBean mapBean) throws IllegalAccessException, InstantiationException, SQLException, WDSException, ClassNotFoundException {
		StringBuilder s = new StringBuilder();

        LOGGER.info( mapBean.getOutputType());
        if ( mapBean.getOutputType().equals(OUTPUT.HTML ) ) {
			Boolean jquery = isJquery(mapBean.getMapView().getJointype());
        	s.append(htmlOpenLeaflet(mapBean.getRenderTo(), mapBean.getWidth(), mapBean.getHeight(), jquery, mapBean.getOptions().isShowControlOverlays()));
        	s.append(addOptions(mapBean));
        	// TODO: change the renderTO
        	s.append(LeafletMap.createLeaftLetMap(mapBean, mapBean.getRenderTo(), mapBean.getRenderTo(),mapBean.getRenderTo()));
        	s.append(htmlCloseLeaflet(request, mapBean.getShared(), mapBean.getWidth()));
		}
        else if ( mapBean.getOutputType().equals(OUTPUT.JS ) ) {
            s.append(LeafletMap.createJsMap(mapBean, mapBean.getRenderTo(),mapBean.getRenderTo()));
        }
		else if ( mapBean.getOutputType().equals(OUTPUT.JOIN ) ) {
            s.append(LeafletMap.addLayersJoin(mapBean.getMapView(),mapBean.getRenderTo()));
        }


		return s.toString();
	}
	
	private Boolean isJquery(JOINTYPE jointype) {
		if ( jointype != null ) {
			if ( jointype.equals(JOINTYPE.point)) {
				return true;
			}
		}	
		return false;
	}

	private String htmlOpenLeaflet(String renderTo, String width, String height, Boolean jquery, Boolean showOverlayerController) {
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		LeafletConf leafletConf = (LeafletConf) wac.getBean("leafletConf");
		String s = 	 "<html><head>" +
					 "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
					 leafletConf.getLeafletCssPath(renderTo, width, height, showOverlayerController) +
					 "<script src=\"" + leafletConf.getLeaflet() + "\" type=\"text/javascript\"></script>";
	
//		if ( addExport )
		s += "<script src=\"" + leafletConf.getExport() + "\" type=\"text/javascript\"></script>";
		if ( jquery ) {
			s += "<script src=\"" + leafletConf.getJQuery() + "\" type=\"text/javascript\"></script>";
			s += "<script src=\"" + leafletConf.getJQueryHover() + "\" type=\"text/javascript\"></script>";
		}
		return s;
	}
	
	private String addOptions(MAPSBean mapBean) {
		String s = addGetFeatureInfo(mapBean);
		return s;
	}
	
	private String addGetFeatureInfo(MAPSBean mapBean) {
		ServletContext servletContext = this.getServletConfig().getServletContext();
		WebApplicationContext wacMap = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		MapsConf mapsConf = (MapsConf) wacMap.getBean("mapsConf");
		return GetFeatureInfoUtils.addGetFeatureinfo(mapBean, mapsConf.getUrl());
	}
	
	private String htmlCloseLeaflet(HttpServletRequest request, Boolean tiny, String width) {
		StringBuilder sb = new StringBuilder();
		if ( tiny ) {
			String fullURL = request.getRequestURL().toString() + "?" + request.getQueryString().toString();
			fullURL += "&shared=false";
			String tinyURL = null;
			try {
				ServletContext servletContext = this.getServletConfig().getServletContext();
				WebApplicationContext wac = WebApplicationContextUtils	.getRequiredWebApplicationContext(servletContext);
				TinyClient tinyClient = (TinyClient) wac.getBean("tinyClient");
				tinyURL = tinyClient.register(fullURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			if (tiny && tinyURL != null) {
				sb.append("<table width='").append(width).append("'><tr>");
				sb.append("<td align='right' style='color: #3E576F; font-family: verdana; font-size: 16px;'>");
				sb.append("Share This Map: <INPUT SIZE='27' TYPE='TEXT' VALUE='" + tinyURL + "'></td>");
				sb.append("</tr></table>");
			}
		}
		sb.append("</body></html>");
		return sb.toString();
	}
	
	private void handleException(HttpServletResponse response, String message) throws IOException {
		String output = Wrapper.wrapAsHTML(message).toString();
		response.setContentLength(output.length());
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(output);
		out.close();
		out.flush();
	}
	
}