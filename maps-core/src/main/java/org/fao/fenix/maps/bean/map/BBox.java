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

public class BBox {
		
	// default srs
	//private String srs = "EPSG:4326";
	private String srs = "EPSG:3857";
	private double xmin; // xMin minlon
	private double ymin; // yMin minlat
	private double xmax; // xMax maxlon
	private double ymax; // yMax maxlat

	public BBox() {
	}
	
	public BBox(String srs, double xmin, double ymin, double xmax, double ymax) {
		this.srs = srs;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}
	
	public void setBBox( double xmin, double ymin, double xmax, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}
	
	public BBox(double xmin, double ymin, double xmax, double ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	public double getXmin() {
		return xmin;
	}

	public void setXmin(double xmin) {
		this.xmin = xmin;
	}

	public double getYmin() {
		return ymin;
	}

	public void setYmin(double ymin) {
		this.ymin = ymin;
	}

	public double getXmax() {
		return xmax;
	}

	public void setXmax(double xmax) {
		this.xmax = xmax;
	}

	public double getYmax() {
		return ymax;
	}

	public void setYmax(double ymax) {
		this.ymax = ymax;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}		
	
//    public BBox union(BBox other) {
//        BBox ret = new BBox();
//        ret.setYmin(Math.min(ymin, other.getY()));
//        ret.setYmax(Math.max(ymax, other.getMaxLat()));
//        ret.setMinLon(Math.min(minlon, other.getMinLon()));
//        ret.setMaxLon(Math.max(maxlon, other.getMaxLon()));
//        ret.setSrs(srs); // FIXME: what if SRS are different?
//        return ret;
//    }
    
	public String toString() {
		return "BBOX[" 
			+ "'"+srs+"', x0:"
			+ xmin + ", y0:"
			+ ymin + ", x1:"
			+ xmax + ", y1:"
			+ ymax 
			+ "]";
	}
	
	public String toBBOX() {
		return xmin + ","
			+ ymin + ","
			+ xmax + ","
			+ ymax;
	}
}
