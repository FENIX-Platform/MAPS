package org.fao.fenix.maps.layer;

import org.fao.fenix.maps.bean.map.Layer;

public class LayerWMS {
	
	public static String createLayerWMSLeaflet(Layer layer, Boolean addLayer) {
		StringBuilder sb = new StringBuilder();
		
		// TODO: add a layerID
        if ( addLayer ) {
		    String layerVariable = "var " + layer.getId();
            sb.append(layerVariable + " =");
        }
		
		sb.append(" new L.TileLayer.WMS(");

		sb.append("'" + layer.getGetMapUrl() + "'");
		sb.append(",");
		sb.append(createLayerOption(layer));
				 
		sb.append(");");	
				
		return sb.toString();
	}
	
    private static String createLayerOption(Layer layer) {
        String szOL = "{"
					+ "layers:\"" + layer.getLayerName() + "\"";
	
		if(layer.getLayerType() == Layer.LayerType.EXTERNAL)
			; // DO NOTHING format = ""; // default one
		else if(layer.getLayerType() == Layer.LayerType.RASTER)
			; // DO NOTHING format = ""; // TODO: force jpg?
		else // all the other ones: internal vect
			szOL += ",format:\"image/png\"";
		
		// background color
		if ( layer.getBgcolor() != null ) {
			if ( !layer.getBgcolor().equals(""))
				szOL += ",bgcolor: \"0x" + layer.getBgcolor() + "\"";
		}
		
		if(layer.getStyleName() != null)
			szOL += ",styles:\"" + layer.getStyleName() + "\"";
		if(layer.getStyleURL() != null)
			szOL += ",sld:\"" + layer.getStyleURL() + "\"";
		if(layer.getCql_filter() != null)
			szOL += ",cql_filter:\"" + layer.getCql_filter() + "\"";
		
		szOL += "," + "visibility:" + !layer.isHidden() + "," + "transparent:"
				+ layer.isTransparent() + "}";
		return szOL;
    }
}
