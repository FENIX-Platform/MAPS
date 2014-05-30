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
package org.fao.fenix.maps.bean.map;

import org.fao.fenix.maps.constants.OUTPUT;
import org.fao.fenix.maps.constants.PREDEFINEDVIEW;
import org.fao.fenix.wds.core.bean.FWDSBean;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class MAPSBean extends FWDSBean {
	
	MapView mapView = new MapView();
	
	OUTPUT outputType = OUTPUT.HTML;
	
	String height = "100%";
	
	String width = "100%";
	
	String renderTo = "map";
	
	String lang = "e";
	
	boolean shared = false;
	
	boolean getFeatureInfo = false;
	
	boolean jsoncallback = false;
	
	String variableSuffix;
	
	// for the feature info
	String x;
	
	String y;
	
	boolean export = false;
	
	// TODO: should be made a getFeatureInfo Bean
	// this should perfor a get feature info standard with a WMS call
	// (i.e. http://127.0.0.1:9090/geoserver/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetFeatureInfo&LAYERS=gaul0_faostat&QUERY_LAYERS=gaul0_faostat&srs=EPSG:3857&BBOX='+BBOX+'&HEIGHT='+HEIGHT+'&WIDTH='+WIDTH+'&INFO_FORMAT=text%2Fhtml&X='+X+'&Y='+Y;
	private boolean enableGetFeatureInfo = false;
	
	// TODO: Enables the getFeatureInfo on the joined layer (using maps webapps)
	// (i.e. http://127.0.0.1:8080/maps/api?layers='+querylayers+'&styles=join&joinboundary=GAUL0&bbox='+BBOX+'&height='+HEIGHT+'&width='+WIDTH+'&INFO_FORMAT=text%2Fhtml&srs=EPSG%3A3857&x='+X+'&y='+Y+'&joindata='+joindata+'&mu='+mu+'&getfeatureinfo=true';
	private boolean enableJoinGFI = false;
	
	PREDEFINEDVIEW predefinedView;
	
	private GUIOptions options = new GUIOptions(); 

	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
	}

	public PREDEFINEDVIEW getPredefinedView() {
		return predefinedView;
	}

	public void setPredefinedView(PREDEFINEDVIEW predefinedView) {
		this.predefinedView = predefinedView;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getRenderTo() {
		return renderTo;
	}

	public void setRenderTo(String renderTo) {
		this.renderTo = renderTo;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public Boolean getFeatureInfo() {
		return getFeatureInfo;
	}

	public void setGetFeatureInfo(Boolean getFeatureInfo) {
		this.getFeatureInfo = getFeatureInfo;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public boolean isExport() {
		return export;
	}

	public void setExport(boolean export) {
		this.export = export;
	}

	public GUIOptions getOptions() {
		return options;
	}

	public boolean isJsoncallback() {
		return jsoncallback;
	}

	public void setJsoncallback(boolean jsoncallback) {
		this.jsoncallback = jsoncallback;
	}

	public boolean isEnableGetFeatureInfo() {
		return enableGetFeatureInfo;
	} 
	
	public void setEnableGetFeatureInfo(boolean enableGetFeatureInfo) {
		this.enableGetFeatureInfo = enableGetFeatureInfo;
	}

	public boolean isEnableJoinGFI() {
		return enableJoinGFI;
	}

	public void setEnableJoinGFI(boolean enableJoinGFI) {
		this.enableJoinGFI = enableJoinGFI;
	}

	public OUTPUT getOutputType() {
		return outputType;
	}

	public void setOutputType(OUTPUT outputType) {
		this.outputType = outputType;
	}

	public String getVariableSuffix() {
		return variableSuffix;
	}

	public void setVariableSuffix(String variableSuffix) {
		this.variableSuffix = variableSuffix;
	}

}
