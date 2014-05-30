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
package org.fao.fenix.maps.configurations;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class LeafletConf {

//	private String ip;
//	
//	private String port;
	
	private String url;
	
	private String leafletPath;
	
	private String leafletVersion;
	
	private String leafletCss;
	
	private String jqueryPath;
	
	private String jqueryVersion;

	public String getLeaflet() {
		return "http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/leaflet.js";
	}
	
	public String getExport() {
		return "http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/plugins/export.js";
	}
	
	public String getJQuery() {
		return "http://" + this.getUrl() + this.getJqueryPath() + "/" + this.getJqueryVersion() + "/jquery.min.js";
	}	
	public String getJQueryHover() {
		return "http://" + this.getUrl() + this.getJqueryPath() + "/" + this.getJqueryVersion() + "/hover.js";
	}	
	
	
	public String getLeafletCssPath(String renderTo, String width, String height, Boolean showOverlayerController) {
		// basic CSS
		String s = getLeafletCss(renderTo, width, height, showOverlayerController);
		// adding the CSS for the (join) legend
		// TODO: that one should be dynamic
		s += getLegendCss();
		s += getExportCss();
		return s;
	}

	
	private String getLeafletCss(String renderTo, String width, String height, Boolean showControlOverlays) {
		String s = "<link rel='stylesheet' href='http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/leaflet.css' />" +
				   "<!--[if lte IE 8]>" +
//				   "<!--[if IE 8]>" +
				   "<link rel='stylesheet' href='http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/leaflet.ie.css' />" +
				   "<![endif]--></head>" +
				   "  <body>	<style>body {padding: 0;margin: 0;} 	" +
				   " html, body, #"+ renderTo +" {width: "+ width+"; height: "+ height +"; " +
				"}";
		
		  
		if ( !showControlOverlays ) {
			s += " .leaflet-control-layers-overlays { display:none !important; } ";
			s += " .leaflet-control-layers-separator { display:none !important; } ";
		}
		s += "</style>";
		return s;
	}
	
	private String getLegendCss(){
		String s = "<link rel='stylesheet' href='http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/legend.css' /><!--[if lte IE 8]><link rel='stylesheet' href='http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/legend.ie.css' /><![endif]-->";
		s += "</style>";
		return s;
	}
	
	private String getExportCss(){
		String s = "<link rel='stylesheet' href='http://" + this.getUrl() + this.getLeafletPath() + "/" + this.getLeafletVersion() + "/plugins/export.css' />";
		s += "</style>";
		return s;
	}

	
	
	
//	public String getIp() {
//		return ip;
//	}
//
//	public void setIp(String ip) {
//		this.ip = ip;
//	}
//
//	public String getPort() {
//		return port;
//	}
//
//	public void setPort(String port) {
//		this.port = port;
//	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLeafletPath() {
		return leafletPath;
	}

	public void setLeafletPath(String leafletPath) {
		this.leafletPath = leafletPath;
	}

	public String getLeafletVersion() {
		return leafletVersion;
	}

	public void setLeafletVersion(String leafletVersion) {
		this.leafletVersion = leafletVersion;
	}

	public String getLeafletCss() {
		return leafletCss;
	}

	public void setLeafletCss(String leafletCss) {
		this.leafletCss = leafletCss;
	}

	public String getJqueryVersion() {
		return jqueryVersion;
	}

	public void setJqueryVersion(String jqueryVersion) {
		this.jqueryVersion = jqueryVersion;
	}

	public String getJqueryPath() {
		return jqueryPath;
	}

	public void setJqueryPath(String jqueryPath) {
		this.jqueryPath = jqueryPath;
	}	
}