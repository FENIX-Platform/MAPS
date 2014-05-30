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

import java.util.ArrayList;
import java.util.List;

import org.fao.fenix.maps.constants.join.JOINTYPE;
/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class MapView {

	private BBox bbox;
	private List<Layer> layerList = new ArrayList<Layer>();
	private List<BaseLayer> baseLayerList = new ArrayList<BaseLayer>();

	// used for the permalink (or to zoom to a point instead of using the bbox)
	private Integer zoom = 1;
	private Integer minzoom = 1;
	// define it manually
	private Integer maxzoom;
	private Double lat = 0.0;
	private Double lon = 0.0;
	
	private JOINTYPE jointype;

	public MapView() {
	}

	public void addLayer(Layer r) {
		layerList.add(r);
	}

	public void addBaseLayer(BaseLayer r) {
		baseLayerList.add(r);
	}

	public List<Layer> getLayerList() {
		return layerList;
	}

	public BBox getBbox() {
		return bbox;
	}

	public void setBbox(BBox bbox) {
		this.bbox = bbox;
	}

	public String toString() {
		if (!baseLayerList.isEmpty())
			return "ClientMapView[" + bbox + " baselayers: "
					+ baseLayerList.size() + " - layers: " + layerList.size()
					+ "]";
		return "ClientMapView[" + bbox + " layers: " + layerList.size() + "]";
	}

	public void setLayerList(List<Layer> layerList) {
		this.layerList = layerList;
	}

	public List<BaseLayer> getBaseLayerList() {
		return baseLayerList;
	}

	public void setBaseLayerList(List<BaseLayer> baseLayerList) {
		this.baseLayerList = baseLayerList;
	}

	public JOINTYPE getJointype() {
		return jointype;
	}

	public Integer getZoom() {
		return zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
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

	public void setJointype(JOINTYPE jointype) {
		this.jointype = jointype;
	}

	public Integer getMinzoom() {
		return minzoom;
	}

	public void setMinzoom(Integer minzoom) {
		this.minzoom = minzoom;
	}

	public Integer getMaxzoom() {
		return maxzoom;
	}

	public void setMaxzoom(Integer maxzoom) {
		this.maxzoom = maxzoom;
	}
	
	
}
