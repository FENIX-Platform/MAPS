package org.fao.fenix.maps.layer;

import org.fao.fenix.maps.bean.map.Layer;

public class LayerTile {
	
	// This is currently used for OSM, MapQuest
		public static String createLayerLeaflet(Layer layer) {
			StringBuilder sb = new StringBuilder();
			String layerVariable = "var " + layer.getId();
			sb.append(layerVariable + " = new L.TileLayer(");
			sb.append("'" + layer.getGetMapUrl() + "'");	
			sb.append(");");
			return sb.toString();
		}
}
