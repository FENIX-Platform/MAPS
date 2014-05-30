package org.fao.fenix.maps.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ExportExternalLayer {
	
	// http://tile.openstreetmap.org/cgi-bin/export?bbox=7.3229,51.4641,7.6127,51.6127&scale=110000&format=png
	// http://ojw.dev.openstreetmap.org/StaticMap/?lat=45&lon=23&z=0&show=1&w=1200&h=600&layer=osm
	public static String getExternalLayer(String layer, Double lat, Double lon, Integer zoomLevel, String width, String height) throws MalformedURLException, IOException {
		System.out.println("getExternalLayer");
		
		String url = "http://ojw.dev.openstreetmap.org/StaticMap/?";
		
		url += "show=1";
//		url += "&layer=" + layer;
		url += "&layer=osm";
		url += "&lat=" + lat;
		url += "&lon=" + lon;
		url += "&z=" + zoomLevel;
		url += "&w=" + width;
		url += "&h=" + height;
		
		System.out.println("URL:" + url );
		
//		URL u = new URL(url);
//		URLConnection uc = u.openConnection();
//		HttpURLConnection connection = (HttpURLConnection) uc;
//		connection.setDoOutput(true);
//		connection.setRequestMethod("GET");
		StringBuilder sb = new StringBuilder();
//		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//		String inputLine;
//		while ((inputLine = in.readLine()) != null)
//			sb.append(inputLine);
//		in.close();
		return sb.toString();
	}

}
