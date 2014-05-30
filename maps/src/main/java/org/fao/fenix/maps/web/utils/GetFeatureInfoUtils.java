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
package org.fao.fenix.maps.web.utils;

import java.util.List;

import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MAPSBean;

/**
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 */
public class GetFeatureInfoUtils {
	
	public static String addGetFeatureinfo(MAPSBean mapBean, String mapsURL) {
		String s = "";

		if ( mapBean.isEnableGetFeatureInfo() ) {
			// TODO: enable generic GetFeatureInfo
		}
		else if (mapBean.isEnableJoinGFI()) {
			s += addJoinGFIScript(mapBean, mapsURL);
		} 
//		System.out.println("addGetFeatureinfo: " + s);
		return s;
	}

	private static String addJoinGFIScript(MAPSBean mapBean, String mapsURL){
		String s = open();
		s += addJoinGFIVariables(mapBean.getMapView().getLayerList(), mapBean.getLang(), mapBean.getVariableSuffix());
		
		s += addJoinGFIFunction(mapBean.getRenderTo(), mapsURL,mapBean.getVariableSuffix());
		// get maps url
		// get join and add parameters
		s += close();
		return s;
	}
	
	private static String addJoinGFIVariables(List<Layer> layers, String lang, String variableSuffix) {
		String s = "";
		for(Layer layer : layers ) {
			if ( layer.isJoin() ) {
				s +="var querylayers"+ variableSuffix +" = '"+layer.getLayerName()+"';";
				s +="var joindata"+ variableSuffix +" = '"+layer.getJoinLayer().getJoindataString()+"';";
				s +="var mu"+ variableSuffix +" = '"+layer.getJoinLayer().getMeasurementUnit()+"';";
				s +="mu"+ variableSuffix +" = escape(mu"+ variableSuffix +");";
				s +="var querysrs"+ variableSuffix +" = '"+layer.getBbox().getSrs()+"';";
				s +="var queryjoincolumn"+ variableSuffix +" = '"+layer.getJoinLayer().getJoincolumn()+"';";
				s +="var columnlabel"+ variableSuffix +" = '"+layer.getJoinLayer().getColumnlabel()+"';";
				s +="var lang"+ variableSuffix +" = '"+lang+"';";
				break;
			}
		}
		//String s ="var querylayers = '"+ +"';";
		//s +="var joindata = '"+ +"';";
		//s +="var mu = '"+ +"';";
		return s;
	}
	
	
	private static String addJoinGFIFunction(String mapID, String mapsURL, String variableSuffix) {
		String s = " function getFeatureInfoJoin(e) {";
		
		System.out.println("mapID: " + mapID);
		
		s += "var latlngStr"+ variableSuffix +" = '(' + e.latlng.lat.toFixed(3) + ', ' + e.latlng.lng.toFixed(3) + ')';";
		s += "var bounds"+ variableSuffix +" = " +mapID + ".getBounds(),";
		s += "sw"+ variableSuffix +" = " +mapID + ".options.crs.project(bounds"+ variableSuffix +".getSouthWest()),";
		s += "ne"+ variableSuffix +" = " +mapID + ".options.crs.project(bounds"+ variableSuffix +".getNorthEast());";
		s += "var BBOX"+ variableSuffix +" = sw"+ variableSuffix +".x + ',' + sw"+ variableSuffix +".y +',' + ne"+ variableSuffix +".x + ',' + ne"+ variableSuffix +".y;";
		s += "var WIDTH"+ variableSuffix +" = " +mapID + ".getSize().x;";
		s += "var HEIGHT"+ variableSuffix +" = " +mapID + ".getSize().y;";
		s += "var X"+ variableSuffix +" = " +mapID + ".layerPointToContainerPoint(e.layerPoint).x;";
		s += "var Y"+ variableSuffix +" = " +mapID + ".layerPointToContainerPoint(e.layerPoint).y;";
		
		// TODO: pass dynamicall the request URL
		s += "var queryURL"+ variableSuffix +" = 'http://"+ mapsURL+"/api?" +
//		s += "var queryURL = 'http://"+ mapsURL+"/api?" +
			  "layers='+ querylayers" + variableSuffix + "+'" +
			  "&styles=join&" +
			  "joincolumn='+queryjoincolumn" + variableSuffix + "+'" +
			  "&bbox='+BBOX" + variableSuffix + "+'" +
			  "&height='+HEIGHT" + variableSuffix + "+'" +
			  "&width='+WIDTH" + variableSuffix + "+'" +
			  "&INFO_FORMAT=text%2Fhtml" +
			  "&srs='+querysrs" + variableSuffix + "+'" +
			  "&x='+X" + variableSuffix + "+'" +
			  "&y='+Y" + variableSuffix + "+'" +
			  "&joindata='+joindata" + variableSuffix + "+'" +
			  "&mu='+mu" + variableSuffix + "+'" +
			  "&getfeatureinfo=true" +
			  "&lang='+lang" + variableSuffix + "+'" +
			  "&columnlabel='+columnlabel" + variableSuffix + ";";
			
		s += "var querypopup"+ variableSuffix +" = new L.Popup();";
		s += "querypopup"+ variableSuffix +".setLatLng(e.latlng);";
		s += "querypopup"+ variableSuffix +".setContent(\"<iframe src='\"+queryURL" + variableSuffix + "+\"' " +
				"width='240' height='63'" +
				" frameborder='0'><p>Your browser does not support iframes.</p>" +
				"</iframe>\");";
		
//		s += "querypopup"+ variableSuffix +".setContent(\"<iframe src='\"+queryURL+\"' width='240' height='63' frameborder='0'><p>Your browser does not support iframes.</p></iframe>\");";

		
		// TODO: the map variable should be passed dynamically
		s += " " + mapID +".openPopup(querypopup"+ variableSuffix +");";
		
		s += "} ";

		return s;
	}
	
	private static String open() {
		String s ="<script type='text/javascript'>";
		return s;
	}
	
	private static String close() {
		String s ="</script>";
		return s;
	}
}
