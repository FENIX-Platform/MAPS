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
package org.fao.fenix.maps.constants.join;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public enum BOUNDARIES {
	
	// column name in the database TODO: change the layername (with a fixed one)
//	GAUL0("ADM0_CODE", "geo_conversion_table", "gaul0_centroids", "gaul0_labels"), GAUL1("ADM1_CODE", "geo_conversion_table_gaul1", "g2008_1", "gual1_labels"), GAUL2("ADM2_CODE", "geo_conversion_table_gaul2", "g2008_2", "gual2_labels"), 
//	
//	FAOSTAT("FAOSTAT_CODE", "geo_conversion_table", "gaul0_centroids", "gual0_labels"), 
//	
//	ISO2("ISO2_CODE", "geo_conversion_table", "gaul0_centroids", "gual0_labels"), ISO3("ISO3_CODE", "geo_conversion_table", "gaul0_centroids",  "gual0_labels");
	
	
	GAUL0("adm0_code", "geo_conversion_table", "gaul0_centroids", "gaul0_labels"), GAUL1("adm1_code", "geo_conversion_table_gaul1", "g2008_1", "gual1_labels"), GAUL2("adm2_code", "geo_conversion_table_gaul2", "g2008_2", "gual2_labels"), 
	
	FAOSTAT("faost_code", "geo_conversion_table", "gaul0_centroids", "gual0_labels"), 
	
	// TODO: change it with either iso2 or iso2_code (zoomto works with iso2_code, the layer with iso2)
	// change one or the other
	ISO2("iso2_code", "geo_conversion_table", "gaul0_centroids", "gual0_labels"), ISO3("iso3_code", "geo_conversion_table", "gaul0_centroids",  "gual0_labels");
	
	private String columnName;
	
	private String conversionTable;
	
	private String layerName;
	
	private String labelsSLD;
	
	private BOUNDARIES(String columnName, String conversionTable, String layerName, String labelsSLD){
		this.setColumnName(columnName);
		this.setConversionTable(conversionTable);
		this.setLayerName(layerName);
		this.setLabelsSLD(labelsSLD);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getConversionTable() {
		return conversionTable;
	}

	public void setConversionTable(String conversionTable) {
		this.conversionTable = conversionTable;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getLabelsSLD() {
		return labelsSLD;
	}

	public void setLabelsSLD(String labelsSLD) {
		this.labelsSLD = labelsSLD;
	}
	
}
