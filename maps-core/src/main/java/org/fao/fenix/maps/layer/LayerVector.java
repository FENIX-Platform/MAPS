package org.fao.fenix.maps.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import org.fao.fenix.maps.bean.map.join.JoinInfo;
import org.fao.fenix.maps.util.Parser;

public class LayerVector {
	
	public static String createCircleLeaflet(String mapID, Map<String, JoinInfo> joinInfo, Map<String, Double> values, String measurementUnit, String date, String thousandSeparator, String decimalSeparator) {
		StringBuilder sb = new StringBuilder();
		int id=100;
		// TODO: do it somewhere else
		if ( measurementUnit != null)
			measurementUnit =  "(" + measurementUnit + ")";
		else
			measurementUnit = "";
		for(String key : joinInfo.keySet()) {
			JoinInfo p = joinInfo.get(key);
			String v = Parser.getValue(values.get(key), thousandSeparator, decimalSeparator) ;
//			System.out.println("VALUE: " + p.getLabel() + " - " + v);
			sb.append("var ll" + id + " = new L.LatLng("+ p.getLat() +", "+ p.getLon() +"); ");
			sb.append("var i" + id + " =  { color: 'red', fillColor: '#f03', fillOpacity: 0.4, " +
                    "html: '<b>"+p.getLabel()+"</b><br>"+v+" "+measurementUnit+"'" +
                    " }; ");
//			sb.append("var i" + id + " =  { color: 'red', fillColor: '#f03', fillOpacity: 0.4, html: '<b>"+p.getLabel()+"</b><br>"+values.get(key)+" "+measurementUnit+"' }; ");
			sb.append("var cm" + id + " = new L.CircleMarker(ll" + id +", i" + id + "); ");
			sb.append("cm" + id + ".setRadius("+p.getRadius()+"); ");
			
			// POPUP
			sb.append("cm" + id + ".bindPopup('"+p.getLabel()+" - "+v +" " + measurementUnit + "');");
//			sb.append("cm" + id + ".bindPopup('"+p.getLabel()+" - "+values.get(key) +" " + measurementUnit + "');");
			
			
			// HOVER function
			sb.append("cm" + id +".on('mouseover', function () { changeDivHTML(i"+ id +".html); });" +
					  "cm" + id +".on('mouseout', function () { removeDivHTML('#popup'); }); ");
//					  "cm" + id +".on('mouseout', function (e) {$('#popup').remove(); }); ");

			sb.append(mapID + ".addLayer(cm" + id + ");");
			id++;
		}
		return sb.toString();
	}
	
	// CIRCLE POPUP
	//	var ll1 = new L.LatLng(10, 10);
	//	var co1 = {
	//		color: 'red',
	//		fillColor: '#f03',
	//		fillOpacity: 0.5,
	//		html: '<b>Ghana</b><br>1000 ($USD)'
	//	};
	//	var cm1 = new L.CircleMarker(ll1, co1);
	//	cm1.setRadius(10);
	//	cm1.bindPopup("I am a circle.<br>iuashdiuh<br><b>uhhd</b><br><a href='https://www.google.it/' class='blank'>google</a>");



//    public static String createCircleLeafletJson( Map<String, JoinInfo> joinInfo, Map<String, Double> values, String thousandSeparator, String decimalSeparator, String color, String fillColor, String fillOpacity) {
    public static String createCircleLeafletJson( Map<String, JoinInfo> joinInfo, Map<String, Double> values, String thousandSeparator, String decimalSeparator) {

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        int i=0;
        for(String key : joinInfo.keySet()) {
            JoinInfo p = joinInfo.get(key);
            String v = Parser.getValue(values.get(key), thousandSeparator, decimalSeparator) ;
//			System.out.println("VALUE: " + p.getLat() + " - " + v);

            sb.append("{");
                // setting lat, lon
                sb.append("\"lat\":" + p.getLat() + ",");
                sb.append("\"lon\":" + p.getLon() + ",");

                // setting
                sb.append("\"radius\":" + p.getRadius() + ",");

                sb.append("\"properties\":{");
                     sb.append("\"color\":\"red\"," +
                             "\"fillColor\":\"#f03\"," +
                             "\"fillOpacity\":0.4,");
                     sb.append("\"title\":\""+ p.getLabel() +"\"," +
                               "\"value\":\""+ v +"\"");
                sb.append("}");

            sb.append("}");
            if ( i < joinInfo.size() -1)
                sb.append(",");
            i++;
        }
        sb.append("]");


        return sb.toString();
    }
}
