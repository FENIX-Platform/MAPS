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
package org.fao.fenix.maps.bean.map.join;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class JoinInfo {
	
	private String code;
	
	private String label;
	
	private String info;
	
	private String html;
	
	// the inteval 
	private String interval;
	
	// join type: point
	private Double radius;
	
	private Double lat;
	
	private Double lon;

	public JoinInfo(){
	}
	
	public JoinInfo(String code, String label, Double lat, Double lon) {
		this.code = code;
		this.label = label;
		this.lat = lat;
		this.lon = lon;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
