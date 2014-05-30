package org.fao.fenix.maps.util;

import java.util.List;

import org.fao.fenix.maps.bean.map.BaseLayer;
import org.fao.fenix.maps.bean.map.Layer;
import org.fao.fenix.maps.bean.map.MAPSBean;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.layer.LayerTile;
import org.fao.fenix.maps.layer.LayerVector;
import org.fao.fenix.maps.layer.LayerWMS;

public class LeafletMap {

	private static String ONE = "<script type=\"text/javascript\">";
	private static String THREE = "</script>";
	
	public static String createLeaftLetMap(MAPSBean mapBean, String renderToID, String mapID, String divID) {
		StringBuilder sb = new StringBuilder();

		
//		sb.append("<div class='container'>");
		sb.append("<div class=''>");
		sb.append("<div id=\"" + renderToID + "\"></div>");
		// legend
		String legend = getJoinLegend(mapBean.getMapView().getLayerList());
		if ( legend != null )
			sb.append(legend);
		sb.append("</div>");

		// script
		sb.append(ONE);
		sb.append(createJsMap(mapBean, mapID, divID));
		sb.append(THREE);

		return sb.toString();
	}
	
	public static String createJsMap(MAPSBean mapBean, String mapID, String divID) {
		StringBuilder sb = new StringBuilder();
		sb.append(createMap(mapBean, mapID, divID));
		return sb.toString();
	}
	
	private static String createMap(MAPSBean mapBean, String mapID, String divID) {
		MapView map = mapBean.getMapView();
		StringBuilder sb = new StringBuilder();
		
		// map options
		sb.append(addMapOptions(mapBean, mapID, divID, mapBean.getVariableSuffix()));
		
		// add layers
		sb.append(addLayers(map, mapID));
		
		// add points
		sb.append(addPoints(map, mapID));
			
		// adding the control in case
		sb.append(addLayersControl(mapBean.getMapView().getBaseLayerList(), mapBean.getMapView().getLayerList(), mapID, mapBean.getOptions().isAddLayersControl(), mapBean.getOptions().isAddLayerLabelscontrol()));
		
		return sb.toString();
	}
	
	public static String addLayers(MapView map, String mapID) {
		// adding layers
		StringBuilder sb = new StringBuilder();
		
		// baselayers
		int i=0;
		for(BaseLayer layer : map.getBaseLayerList()) {
			layer.setId("layer" + String.valueOf(Math.floor(Math.random() * 100000)).replace(".", ""));
			if ( layer.isTile() )
				sb.append(LayerTile.createLayerLeaflet(layer));
			else {
				sb.append(LayerWMS.createLayerWMSLeaflet(layer, true));
			}
			
			// just the first base layer is added, the others are automatically added in the control  
			if ( i == 0)
				sb.append(addLayer(mapID, layer.getId()));
			
			i++;
		}
		
		// overlays
		for(Layer layer : map.getLayerList()) {
				layer.setId("layer" + String.valueOf(Math.floor(Math.random() * 100000)).replace(".", ""));
				sb.append(LayerWMS.createLayerWMSLeaflet(layer, true));
				// checking is the layer is hidden
				if ( !layer.isHidden()) {
					sb.append(addLayer(mapID, layer.getId()));
				}
		}
		

//		 base layer control
//		if ( !map.getBaseLayerList().isEmpty() && map.getBaseLayerList().size() > 1) {
//			sb.append(addBaseLayersControl(map.getBaseLayerList(), mapID));
//		}

		return sb.toString();
	}

    // TODO REFACTOR. for now that is used just to handle a join layer stand alone request
    public static String addLayersJoin(MapView map, String mapID) {
        // adding layers
        StringBuilder sb = new StringBuilder();

        // overlays
        for(Layer layer : map.getLayerList()) {
            // TODO: make it dynamic the transparency, pass it as parameter
            layer.setTransparent(true);
            sb.append(LayerWMS.createLayerWMSLeaflet(layer, false));
        }

        return sb.toString();
    }
	
	private static String addBaseLayersControl(List<BaseLayer> baselayers, String mapID, String variableSuffix) {
		StringBuilder sb = new StringBuilder();
		sb.append("var baseMaps"+ variableSuffix +" = {");
		int i=0;
		for(BaseLayer l : baselayers) {
			String layerTitle = l.getLayerTitle() != null ? l.getLayerTitle() : l.getLayerName();
			sb.append("\""+ layerTitle + "\": " + l.getId()+"");
			if ( i < baselayers.size()-1 )
				sb.append(", ");
			i++;
		}
	    sb.append("};");
	    
	    sb.append("var layersControl"+ variableSuffix +" = new L.Control.Layers(baseMaps);");
	    sb.append(mapID +".addControl(layersControl"+ variableSuffix +"); ");
		return sb.toString();
	}

	

	
	private static String addMapOptions(MAPSBean mapBean, String mapID, String divID, String variableSuffix) {
		MapView map = mapBean.getMapView();
		StringBuilder sb = new StringBuilder();
		
		// TODO: fix it with a list of options to add dynamically to the map		
		if ( map.getMinzoom() != null ) {
			sb.append("var "+ mapID +" = new L.Map('"+ mapID +"', {minZoom: "+ map.getMinzoom() +"});");
		}
		else {
			sb.append("var "+ mapID +" = new L.Map('" + divID +"');");
		}
		
		// if it's set a BBOX it's
		if ( map.getBbox() != null ) {
			 sb.append("var southWest"+ variableSuffix +" = new L.LatLng("+map.getBbox().getYmin()+","+map.getBbox().getXmin()+");");
			 sb.append("var northEast"+ variableSuffix +" = new L.LatLng("+map.getBbox().getYmax()+","+map.getBbox().getXmax()+");");
			 sb.append("var bounds"+ variableSuffix +" = new L.LatLngBounds(southWest"+ variableSuffix +", northEast"+ variableSuffix +"); ");
			 sb.append(mapID +".fitBounds(bounds"+ variableSuffix +"); ");
		}
		else {
			sb.append(mapID +".setView(new L.LatLng("+ map.getLat()+", "+map.getLon()+"), "+map.getZoom() +"); ");
		}
		
		//adding scale control
		sb.append(mapID +".addControl(new L.Control.Scale()); ");
		
		//adding scale control
		if ( mapBean.isEnableJoinGFI() )
			sb.append(mapID +".addEventListener('click', getFeatureInfoJoin); ");
		
		// GUI Options
		if (mapBean.getOptions().isAddExport()) {
			sb.append(addExportControl(mapID));
		}
		
		return sb.toString();
	}
	
	private static String addLayer(String mapID, String layerID) {
		StringBuilder sb = new StringBuilder();
		sb.append(mapID +".addLayer(");
		sb.append(layerID);
		sb.append(");");
		return sb.toString();
	}
	
	private static String addPoints(MapView map, String mapID) {
		// adding layers
		StringBuilder sb = new StringBuilder();
		
		// looking for points (TODO: a more efficient way?)
		for(Layer layer : map.getLayerList()) {
			if(layer.isJoin()) {
				try {
					if (!layer.getJoinLayer().getJoininfo().isEmpty()) {
						sb.append(LayerVector.createCircleLeaflet(mapID, layer.getJoinLayer().getJoininfo(), layer.getJoinLayer().getJoindata(), layer.getJoinLayer().getMeasurementUnit(), layer.getJoinLayer().getDate(), layer.getJoinLayer().getThousandSeparator(), layer.getJoinLayer().getDecimalSeparator()));
					}
				}catch (Exception e) {}
			}
		}

		return sb.toString();
	}
	
	private static String addLayersControl(List<BaseLayer> baselayers, List<Layer> layers, String mapID, Boolean addLayersControl, Boolean AddLayersLabelsControl) {		
		
		StringBuilder sb = new StringBuilder();
		Boolean added = false;
		if ( addLayersControl ) {
			// TODO: this is a quick fix for the layers
			addLayersControl = true;
			added = true;
		}
		sb.append("var baseMaps" + mapID +" = {");
		int i=0;
		for(Layer l : baselayers) {
			String layerTitle = l.getLayerTitle() != null ? l.getLayerTitle() : l.getLayerName();
			sb.append("\""+ layerTitle + "\": " + l.getId()+"");
			if ( i < baselayers.size()-1 )
				sb.append(", ");
			i++;
		}
	    sb.append("};");
	    
		sb.append("var overlayMaps" + mapID +" = {");
		i=0;
		// this is to add everything
		if ( addLayersControl ) {
			added = true;
			for(Layer l : layers) {
				String layerTitle = l.getLayerTitle() != null ? l.getLayerTitle() : l.getLayerName();
				sb.append("\""+ layerTitle + "\": " + l.getId()+"");
				if ( i < layers.size()-1 )
					sb.append(", ");
				i++;
			}
		}
		// this just to add the label layer
		else if ( AddLayersLabelsControl ) {
			added = true;
			for(Layer l : layers) {
				if ( l.isLabels() ) {
					String layerTitle = l.getLayerTitle() != null ? l.getLayerTitle() : l.getLayerName();
					sb.append("\""+ layerTitle + "\": " + l.getId()+"");
				}
			}
		}
		sb.append("};");

		
	    sb.append("var control_" + mapID +" = L.control.layers(baseMaps"+ mapID +", overlayMaps"+ mapID +").addTo("+ mapID +");");

	    return sb.toString();
	}
	
	private static String addExportControl(String mapID) {
		StringBuilder sb = new StringBuilder();
		sb.append(mapID +".addControl(new L.Control.Export()); ");
		return sb.toString();
	}
	
	private static String getJoinLegend(List<Layer> layers) {
		for(Layer layer : layers) {
			if (layer.isJoin()) {
				try {
					if ( layer.getJoinLayer().getLegend().getHtml() != null )
						return layer.getJoinLayer().getLegend().getHtml();
				}catch (Exception e) {
				}
			}
		}
		return null;
	}

}
