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

import java.util.List;
import java.util.Map;

import org.fao.fenix.maps.bean.map.legend.Legend;
import org.fao.fenix.maps.constants.CLASSIFICATION;
import org.fao.fenix.maps.constants.join.JOINTYPE;
import org.geotools.brewer.color.ColorBrewer;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class JoinLayer {
	
	private Map<String, Double> joindata;
	
	private Map<String, JoinInfo> joininfo;
	
	// used for equalarea algorithm
	private Map<String, Double> areas;
	
	// Used for the get feature info values
	private String joindataString;
	
	// Used to have a custom list of ranges
	private List<Double> ranges;
	
	// Used to have a custom list of colors
	private List<String> colors;
	
	private String measurementUnit = "";
	
	private String date;
	
	private String joincolumn;
	
	// TODO: quick fix for faostat to show the right title 
	private String columnlabel;
	
	private Integer intervals = 5;
	
	private String classification =  CLASSIFICATION.quantile.toString();
	
	private String jointype = JOINTYPE.shaded.toString();
			
	private String colorramp = "Reds";
	
	private boolean colorreverse = false;
	
	private Legend legend = new Legend();
	
	// color TODO: add opacity
	private boolean addBorders = false;
	
	private String bordersColor = "000";
	
	private String bordersStroke = "0.8";
	
	private String bordersOpacity = "0.7";

	private String boundary; // JOINBOUNDARY (i.e. GAUL, FAOSTAT, if it's a known boundary)
	
	// Rounding Numbers (for now it's just at the level of the legend when the legend is rendered)
	private String thousandSeparator = null;
	
	private String decimalSeparator = ".";
	
//	private Integer decimalNumbers = 2;
	private Integer decimalNumbers;

    private String pointsJSON;

	public String getMeasurementUnit() {
		return measurementUnit;
	}

	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	public String getJoincolumn() {
		return joincolumn;
	}

	public void setJoincolumn(String joincolumn) {
		this.joincolumn = joincolumn;
	}

	public Integer getIntervals() {
		return intervals;
	}

	public void setIntervals(Integer intervals) {
		this.intervals = intervals;
	}

	public String getColorramp() {
		return colorramp;
	}

	public void setColorramp(String colorramp) {
		this.colorramp = colorramp;
	}

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public List<Double> getRanges() {
		return ranges;
	}

	public void setRanges(List<Double> ranges) {
		this.ranges = ranges;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}

	public Map<String, Double> getJoindata() {
		return joindata;
	}

	public void setJoindata(Map<String, Double> joindata) {
		this.joindata = joindata;
	}

	public Map<String, JoinInfo> getJoininfo() {
		return joininfo;
	}

	public void setJoininfo(Map<String, JoinInfo> joininfo) {
		this.joininfo = joininfo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBoundary() {
		return boundary;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public String getJointype() {
		return jointype;
	}

	public void setJointype(String jointype) {
		this.jointype = jointype;
	}

	public String getBordersColor() {
		return bordersColor;
	}

	public void setBordersColor(String bordersColor) {
		this.bordersColor = bordersColor;
	}

	public String getBordersStroke() {
		return bordersStroke;
	}

	public void setBordersStroke(String bordersStroke) {
		this.bordersStroke = bordersStroke;
	}

	public String getBordersOpacity() {
		return bordersOpacity;
	}

	public void setBordersOpacity(String bordersOpacity) {
		this.bordersOpacity = bordersOpacity;
	}

	public boolean isAddBorders() {
		return addBorders;
	}

	public void setAddBorders(boolean addBorders) {
		this.addBorders = addBorders;
	}

	public String getThousandSeparator() {
		return thousandSeparator;
	}

	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public Integer getDecimalNumbers() {
		return decimalNumbers;
	}

	public void setDecimalNumbers(Integer decimalNumbers) {
		this.decimalNumbers = decimalNumbers;
	}

	public String getJoindataString() {
		return joindataString;
	}

	public void setJoindataString(String joindataString) {
		this.joindataString = joindataString;
	}

	public Map<String, Double> getAreas() {
		return areas;
	}

	public void setAreas(Map<String, Double> areas) {
		this.areas = areas;
	}

	public boolean isColorreverse() {
		return colorreverse;
	}

	public void setColorreverse(boolean colorreverse) {
		this.colorreverse = colorreverse;
	}

	public String getColumnlabel() {
		return columnlabel;
	}

	public void setColumnlabel(String columnlabel) {
		this.columnlabel = columnlabel;
	}

    public String getPointsJSON() {
        return pointsJSON;
    }

    public void setPointsJSON(String pointsJSON) {
        this.pointsJSON = pointsJSON;
    }
}
