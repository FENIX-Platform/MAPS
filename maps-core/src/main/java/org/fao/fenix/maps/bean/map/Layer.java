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

import org.fao.fenix.maps.bean.map.join.JoinLayer;

public class Layer {
	
	// this should be initialized
	private String id;
	
	private long clientId;
	
	private String layerName;
	
	private String layerTitle;
	
	private String styleName;

    // this is used to pass a stylename to the join
    private String styleNameJoin;
	
	private String styleURL;
	
	private String cql_filter;
	
	private String title;

	private String getMapUrl;
	
	private String wms = "DEFAULT";
	
	private BBox bbox = new BBox();
	
	/** range 0..100*/
	private Integer opacity = 100;
	
	private String legendUrl;
	
	private boolean hidden = false;
	
	private String source;
	
	private boolean transparent = true;
	
	private String bgcolor;

	private long geoViewId = -1;
	
	private boolean isJoin = false;
	
	private boolean isLabels = false;

    public static enum LayerType {
        UNDEF, VECTOR, RASTER, EXTERNAL
    }

    public static enum VectorType {
        UNDEF, POINT, LINE, POLY
    }
		    
	private LayerType layerType = LayerType.UNDEF;
    
    /** meaningful only if layerType==VECTOR */
	private VectorType vectorType = VectorType.UNDEF;

	private JoinLayer joinLayer = new JoinLayer();

	public Layer copy() {
		Layer cgv = new Layer();
//		cgv.setTitle(getTitle());
		cgv.setStyleName(getStyleName());
		cgv.setHidden(isHidden());
		cgv.getMapUrl = getMapUrl;
		cgv.bbox = bbox;
		cgv.opacity = opacity;
		cgv.setLayerName(getLayerName());
		return cgv;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	

	public String getGetMapUrl() {
		return getMapUrl;
	}

	/**
	 * This id is assigned for every CGV, even for the non stored ones.
	 * Use it to reference this CGV inside the client.
	 */
	public long getClientId() {
		return clientId;
	}
	
	public void setGetMapUrl(String getMapUrl) {
		this.getMapUrl = getMapUrl;
	}

	public long getGeoViewId() {
		return geoViewId;
	}

	public void setGeoViewId(long id) {
		this.geoViewId = id;
	}

	/**
	 * @return true if the GeoView is stored on DB and has a valid id.
	 */
	public boolean isStored() {
		return geoViewId != -1;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public BBox getBBox() {
		return bbox;
	}

	public void setBBox(BBox bbox) {
		this.bbox = bbox;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public void setLayerType(LayerType layerType) {
		this.layerType = layerType;
	}

	/**
	 * Will return one from the values TYPE_VECTOR_*
	 */
    public VectorType getVectorType() {
        return vectorType;
    }

    public void setVectorType(VectorType vectorType) {
        this.vectorType = vectorType;
    }

	public Integer getOpacity() {
		return opacity;
	}

	public void setOpacity(int opacity) {
		opacity = Math.max(opacity, 0);
		opacity = Math.min(opacity, 100);
		this.opacity = opacity;
	}

	public boolean incOpacity() {
		if(opacity < 100) {
			opacity += 10;
			opacity = Math.min(opacity, 100);
			return true;
		}
		return false;
	}

	public boolean decOpacity() {
		if(opacity > 0) {
			opacity -= 10;
			opacity = Math.max(opacity, 0);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ClientGeoView["
				+ "cid:" + clientId
				+ " type:" + layerType
				+"]";
	}

	public String getLegendUrl() {
		return legendUrl;
	}

	public void setLegendUrl(String legendUrl) {
		this.legendUrl = legendUrl;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}


	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BBox getBbox() {
		return bbox;
	}

	public void setBbox(BBox bbox) {
		this.bbox = bbox;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public String getStyleName() {
		return styleName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JoinLayer getJoinLayer() {
		return joinLayer;
	}

	public void setJoinLayer(JoinLayer joinLayer) {
		this.joinLayer = joinLayer;
	}

	public boolean isJoin() {
		return isJoin;
	}

	public void setJoin(boolean isJoin) {
		this.isJoin = isJoin;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public String getWms() {
		return wms;
	}

	public void setWms(String wms) {
		this.wms = wms;
	}

	public void setOpacity(Integer opacity) {
		this.opacity = opacity;
	}

	public String getStyleURL() {
		return styleURL;
	}

	public void setStyleURL(String styleURL) {
		this.styleURL = styleURL;
	}

	public boolean isLabels() {
		return isLabels;
	}

	public void setLabels(boolean isLabels) {
		this.isLabels = isLabels;
	}

	public String getLayerTitle() {
		return layerTitle;
	}

	public void setLayerTitle(String layerTitle) {
		this.layerTitle = layerTitle;
	}

	public String getCql_filter() {
		return cql_filter;
	}

	public void setCql_filter(String cql_filter) {
		this.cql_filter = cql_filter;
	}
	
	

}
