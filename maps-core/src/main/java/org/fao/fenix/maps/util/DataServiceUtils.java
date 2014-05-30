package org.fao.fenix.maps.util;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.BBox;
import org.fao.fenix.maps.bean.map.join.JoinInfo;
import org.fao.fenix.wds.core.bean.DBBean;
import org.fao.fenix.wds.core.bean.FWDSBean;
import org.fao.fenix.wds.core.bean.OrderByBean;
import org.fao.fenix.wds.core.bean.SQLBean;
import org.fao.fenix.wds.core.constant.DATASOURCE;
import org.fao.fenix.wds.core.constant.SQL;
import org.fao.fenix.wds.core.exception.WDSException;
import org.fao.fenix.wds.core.sql.Bean2SQL;
import org.fao.fenix.wds.tools.client.WDSClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class DataServiceUtils {
	
	private String wdsURL;
	
	private String wdsIP;

	private String wdsPORT;

	private final static Logger LOGGER = Logger.getLogger(DataServiceUtils.class);
	
	public Map<String, JoinInfo> getPointData(String tablename, String codeColumn, Map<String, Double> map, String language) throws AxisFault {
		Map<String, JoinInfo> joinInfo = new HashMap<String, JoinInfo>();
		List<JoinInfo> values = getPointDataList(tablename, codeColumn, map, language);
		for(JoinInfo value : values) 
			joinInfo.put(value.getCode(), value);
		return joinInfo;
	}
	
	public BBox getBBox(String tablename, String codeColumn, String code, String srs) throws AxisFault {
		
		BBox bbox = null;
		
		String urlParameters = "out=json&db="+ DATASOURCE.FENIX +"&select=minx,miny,maxx,maxy&from=geo_conversion_table&where="+ codeColumn +"('"+ code +"')";

		URL url;
		try {
			url = new URL(wdsURL + "/api");
			URLConnection conn;		
			conn = url.openConnection();
			conn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			
			writer.write(urlParameters);
			writer.flush();
	
			String line;
			String result = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
			    result = line;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result);
			JSONArray inputArray = (JSONArray) obj;			
			for(int i=1; i < inputArray.size(); i++) {
				JSONArray row = (JSONArray) inputArray.get(i);
				bbox =  new BBox(srs, Double.valueOf(row.get(0).toString()), Double.valueOf(row.get(1).toString()), Double.valueOf(row.get(2).toString()),Double.valueOf(row.get(3).toString()));
			}
			writer.close();
			reader.close();  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return bbox;
	}
	
	public Map<String, Double> getAreas(String tablename, String codeColumn, String areaColumn, Map<String, Double> map) throws AxisFault {
//		http://fenix.fao.org/wds/api?out=json&db=fenix&select=faost_code,area_dd&from=geo_conversion_table&where=faost_code('35':'130')
		
		Map<String, Double> values = new HashMap<String, Double>();
	
		String codes = getCodesString(map);
		String urlParameters = "out=json&db="+ DATASOURCE.FENIX +"&select=" + codeColumn + ",area_dd&from=geo_conversion_table&where="+ codeColumn +"("+ codes +")";
		URL url;
		try {
			url = new URL(wdsURL + "/api");
			URLConnection conn;		
			conn = url.openConnection();
			conn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			
			writer.write(urlParameters);
			writer.flush();
	
			String line;
			String result = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
			    result = line;
			}
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result);
			JSONArray inputArray = (JSONArray) obj;			
			for(int i=1; i < inputArray.size(); i++) {
				JSONArray v = (JSONArray) inputArray.get(i);
				values.put(v.get(0).toString(), Double.valueOf(v.get(1).toString()));
			}
			writer.close();
			reader.close();  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return values;
	}
	
	public String getPointFromBBox(String tablename, String xmin, String ymin, String xmax, String ymax) throws AxisFault {
		WDSClient c = new WDSClient(wdsIP, wdsPORT);
		DBBean db = new DBBean(DATASOURCE.FENIX);
		SQLBean sql = getPointsFromBBox(tablename, xmin, ymin, xmax, ymax);
		FWDSBean b = new FWDSBean(sql, db);
		LOGGER.info(Bean2SQL.convert(sql));
		List<List<String>> table = new ArrayList<List<String>>();
		try {
			table = c.querySynch(b);
		} catch (WDSException e) {
			e.printStackTrace();
		}
		System.out.println(table);
		String s = "({ \"type\":\"FeatureCollection\",\"features\":[";
		int i = 0;
		for (List<String> row : table) {
			try {
				s += "{\"type\":\"Feature\",\"properties\":{\"name\":\"Countrys\",\"iconurl\":\"http://localhost:8080/maps/libs/images/black_marker.png\",\"popupContent\":\"and the country is?<br> <b>"+ row.get(0) +"</b> ("+ row.get(1) +")\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":["
						+ row.get(2) + "," + row.get(3) + "]}}";
				if (i < table.size() - 1) {
					s += ",";
				}
			}catch (Exception e) {
			}
			i++;
		}
		s += "]})";
		return s;
	}
	

	
	
	public String getCrowdPricesData(String tablename, String commodityCode, String date) throws AxisFault {
		WDSClient c = new WDSClient(wdsIP, wdsPORT);
		DBBean db = new DBBean(DATASOURCE.STAGINGAREA);
		SQLBean sql = getCrowdPricesPoints(tablename, commodityCode, date);
		FWDSBean b = new FWDSBean(sql, db);
//		LOGGER.info(Bean2SQL.convert(sql));
		List<List<String>> table = new ArrayList<List<String>>();
		try {
			table = c.querySynch(b);
		} catch (WDSException e) {
			e.printStackTrace();
		}
		System.out.println(table);
		
		String s = "({ \"type\":\"FeatureCollection\",\"features\":[";
		int i = 0;
		LinkedHashMap<String, List<List<String>>> markets = getCrowdPricesPoints(table);
		for(String marketname : markets.keySet()) {
			String popupcontent = "<b>" + marketname + "</b><br>";
			String lat ="";
			String lon = "";
			for(List<String> row : markets.get(marketname)) {
				popupcontent += row.get(1).replace("_", " ") +" - "+ row.get(2) +" ("+ row.get(3).replace("_", " ") +") <br>";
				lon = row.get(4);
				lat = row.get(5);
			}
			System.out.println("popup: " + popupcontent);
			s += "{\"type\":\"Feature\",\"properties\":{\"name\":\"Countrys\",\"iconurl\":\"http://localhost:8080/maps/libs/images/black_down_circular.png\"," +
				 "\"popupContent\":\""+ popupcontent+" \"},\"geometry\":{\"type\":\"Point\",\"coordinates\":["
				+ lon + "," + lat + "]}}";
			if (i < table.size() - 1) {
				s += ",";
			}
			i++;
		}
		s += "]})";
		
		
		return s;
	}
	
	private LinkedHashMap<String, List<List<String>>> getCrowdPricesPoints(List<List<String>> table) {

		LinkedHashMap<String, List<List<String>>> markets = new LinkedHashMap<String, List<List<String>>>();
		
		for (List<String> row : table) {
			String marketname = row.get(0);
			List<List<String>> rows = new ArrayList<List<String>>();
			if ( markets.containsKey(marketname)) {
				rows = markets.get(marketname);
			}
			rows.add(row);
			markets.put(marketname, rows);
		}
		
		System.out.println(markets);
		return markets;
	}
	
	
		
	private List<JoinInfo> getPointDataList(String tablename, String codeColumn, Map<String, Double> map,  String language) throws AxisFault {
		List<JoinInfo> values = new ArrayList<JoinInfo>();
		
//		http://fenix.fao.org/wds/api?out=json&db=fenix&select=faost_code,area_dd&from=geo_conversion_table&where=faost_code('35':'130')
		String codes = getCodesString(map);
		String urlParameters =
                "out=json" +
                "&db="+ DATASOURCE.FENIX +"" +
                "&select="+ codeColumn +"," +
                "label"+ language+",lat,lon" +
                "&from=geo_conversion_table" +
                "&where="+ codeColumn +"("+ codes +")";
		URL url;
		try {

            LOGGER.info("wdsURL: " + wdsURL);
            LOGGER.info("urlParameters: " + urlParameters);
			url = new URL(wdsURL + "/api");
			URLConnection conn;		
			conn = url.openConnection();
			conn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			
			writer.write(urlParameters);
			writer.flush();
			
			String line;
			String result = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
			    result = line;
			}
            LOGGER.info("getPointDataList: " + result);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result);
			JSONArray inputArray = (JSONArray) obj;			
			for(int i=1; i < inputArray.size(); i++) {
			JSONArray row = (JSONArray) inputArray.get(i);
				String label = row.get(1).toString().replace("'", " ");
				values.add(new JoinInfo(row.get(0).toString(), label, Double.valueOf(row.get(2).toString()), Double.valueOf(row.get(3).toString())));
			}
				
			writer.close();
			reader.close();  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return values;
	}
	
	private SQLBean getPointData(String tablename, String codeColumn, String[] codes, String language) {
		SQLBean sql = new SQLBean();
		sql.select(null, "D."+codeColumn , "code");
		sql.select(null, "D.label"+ language, "label");
		sql.select(null, "D.lat", "label");
		sql.select(null, "D.lon", "label");
		sql.from(tablename, "D");
		sql.where(SQL.TEXT.name(), codeColumn, "IN", null, codes);
		return sql;
	}
	
	private SQLBean getAreasData(String tablename, String codeColumn, String areaColumn, String[] codes) {
		SQLBean sql = new SQLBean();
		sql.select(null, "D."+codeColumn , "code");
		sql.select(null, "D."+ areaColumn, "area");
		sql.from(tablename, "D");
		sql.where(SQL.TEXT.name(), codeColumn, "IN", null, codes);
		return sql;
	}
	
	private SQLBean getPointsFromBBox(String tablename, String xmin, String ymin, String xmax, String ymax) {
		SQLBean sql = new SQLBean();
		sql.select(null, "D.labele", "label");
		sql.select(null, "D.capitale", "label");
		sql.select(null, "D.lon", "lon");
		sql.select(null, "D.lat", "lat");
		sql.from(tablename, "D");
		sql.where(SQL.DATE.name(), "D.lon", ">", xmin, null);
		sql.where(SQL.DATE.name(), "D.lon", "<", xmax, null);
		sql.where(SQL.DATE.name(), "D.lat", ">", ymin, null);
		sql.where(SQL.DATE.name(), "D.lat", "<", ymax, null);
		return sql;
	}
	
	private SQLBean getCrowdPricesPoints(String tablename, String commodityCode, String date) {
		SQLBean sql = new SQLBean();
		sql.select(null, "M.marketnamee", "b");
		sql.select(null, "D.commoditycode", "b");
		sql.select(null, "D.value", "a");
		sql.select(null, "D.unitcode", "b");
		// TODO: lat e lon is wrong on the DB
		sql.select(null, "M.lon", "lon");
		sql.select(null, "M.lat", "lat");

		sql.from(tablename, "D");
		sql.from("crowdprices_market","M");
		sql.where(SQL.DATE.name(), "D.marketcode", "=", "M.marketcode", null);
		sql.where(SQL.DATE.name(), "D.commoditycode", "=", "'" +commodityCode +"'", null);
		sql.where(SQL.DATE.name(), "D.date", "=", "'" +date + "'", null);
		sql.orderBy(new OrderByBean("M.marketcode", "ASC"));
		return sql;
	}
	
	private SQLBean getBBox(String tablename, String codeColumn, String[] codes) {
		SQLBean sql = new SQLBean();
		sql.select(null, "D.minx", "minlon");
		sql.select(null, "D.miny", "minlat");
		sql.select(null, "D.maxx", "maxlon");
		sql.select(null, "D.maxy", "maxlat");
		sql.from(tablename, "D");
		sql.where(SQL.TEXT.name(), codeColumn, "IN", null, codes);
		return sql;
	}
	
	private String[] getCodes(Map<String, Double> map) {
		String[] values = new String[map.size()];
		int i = 0;
		for(String key : map.keySet()) {
			values[i] = "'" + key + "'";
			i++;
		}	
		return values;
	}
	
	private String getCodesString(Map<String, Double> map) {
		String values = "";
		for(String key : map.keySet()) {
			values += "'" + key + "':";
		}	
		if ( !values.equals(""))
			values.substring(0, map.size() -1);
		return values;
	}
	
	public void setWdsIP(String wdsIP) {
		this.wdsIP = wdsIP;
	}

	public void setWdsPORT(String wdsPORT) {
		this.wdsPORT = wdsPORT;
	}

	public void setWdsURL(String wdsURL) {
		this.wdsURL = wdsURL;
	}
	
	
}
