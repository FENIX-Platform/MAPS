package org.fao.fenix.maps.web.restService;

import com.google.gson.Gson;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.*;
import org.fao.fenix.maps.bean.map.join.JoinLayer;
import org.fao.fenix.maps.configurations.GeoserverConf;
import org.fao.fenix.maps.configurations.MapsConf;
import org.fao.fenix.maps.constants.CLASSIFICATION;
import org.fao.fenix.maps.constants.JOINLAYER;
import org.fao.fenix.maps.constants.KEYWORDS;
import org.fao.fenix.maps.constants.join.BOUNDARIES;
import org.fao.fenix.maps.constants.join.JOINTYPE;
import org.fao.fenix.maps.join.JoinLayerUtils;
import org.fao.fenix.maps.layer.LayerVector;
import org.fao.fenix.maps.util.DataServiceUtils;
import org.fao.fenix.maps.util.GetFeatureInfoREST;
import org.fao.fenix.maps.util.Parser;
import org.fao.fenix.maps.web.utils.MapUtils;
import org.fao.fenix.wds.core.bean.DBBean;
import org.fao.fenix.wds.core.bean.SQLBean;
import org.fao.fenix.wds.core.constant.DATASOURCE;
import org.fao.fenix.wds.core.exception.WDSException;
import org.fao.fenix.wds.core.jdbc.JDBCConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.fao.fenix.maps.bean.map.BBox;


import javax.ws.rs.*;

import javax.ws.rs.core.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created with IntelliJ IDEA.
 * User: vortex
 * Date: 7/30/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Path("/service")
public class MapsREST {

    @Autowired
    private MapsConf mapsConf;

    @Autowired
    private GeoserverConf geoserverConf;

    @Autowired
    private DataServiceUtils dataServiceUtils;

    private String defaultLanguage = "E";

    private static final Logger LOGGER = Logger.getLogger(MapsREST.class);

    @GET
    @Path("/sld")
    /**
     * Returns the SLD URI path
     */
    public Response createSLD(@Context UriInfo uriInfo) {
        String query = uriInfo.getRequestUri().getQuery();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        Map<String,String[]> parameters = parse(queryParams);

        Layer layer = new Layer();
        setLayerName(parameters, layer);

        setJoinLayerOptions(parameters, layer, null, false);

        Gson g = new Gson();
        HashMap<String, String> obj = new HashMap<String, String>();
        obj.put("sldurl",layer.getStyleURL() );
        obj.put("geoserverwms", geoserverConf.wmsurl );

        String result = g.toJson(obj);

        // wrap result
        Response.ResponseBuilder builder = Response.ok();
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");
        builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=utf-8");
        return Response.status(200).entity(result).build();
    }


    @POST
    @Path("/sld2")
    /**
     * Returns the SLD URI path
     */
    public Response createSLD2(MultivaluedMap<String, String> queryParams) {

        Map<String,String[]> parameters = parse(queryParams);

        Layer layer = new Layer();
        setLayerName(parameters, layer);

        String language = defaultLanguage;
        if ( parameters.get("language") != null ) {
            language = parameters.get("language")[0];
        }

        setJoinLayerOptions(parameters, layer, language, false);

        // tODO: legenedtitle in the new version is handled in js
        /*String legend = LegendCreator.createVerticalLegend(null, layer.getJoinLayer().getRanges(), layer.getJoinLayer().getColors(),
                layer.getJoinLayer().getLegend().getPosition(),
                layer.getJoinLayer().getThousandSeparator(),
                layer.getJoinLayer().getDecimalSeparator(),
                layer.getJoinLayer().getJoindata().size(),
                layer.getJoinLayer().getLegend().getLegendInfo()); */

        Gson g = new Gson();
        HashMap<String, String> obj = new HashMap<String, String>();
        obj.put("sldurl",layer.getStyleURL() );
        obj.put("geoserverwms", geoserverConf.wmsurl );
        //obj.put("legendHTML", "legend" );
        obj.put("pointsJSON", layer.getJoinLayer().getPointsJSON() );

        String result = g.toJson(obj);

        // wrap result
        Response.ResponseBuilder builder = Response.ok(result);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "POST");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");

        // Stream result
        return builder.build();
    }


    @GET
    @Path("/request")
    /**
     */
    public Response geoserverRequest(@Context UriInfo uriInfo) {
        String query = uriInfo.getRequestUri().getQuery();

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        Map<String,String[]> parameters = parse(queryParams);

        String stringURL = "";
        // getGeoserverWMS  /** TODO: check if there is a get without case sensitive
        stringURL += parameters.get("urlWMS")[0];
        stringURL += (!stringURL.contains("?"))? "?": "";


        String values = "";
        for(String key : parameters.keySet()) {
            if ( !key.equalsIgnoreCase("urlWMS")) {
                values += '&' + key + '=';
                for(int i=0; i < parameters.get(key).length; i++){
                    values += parameters.get(key)[i];
                }
            }
        }
        String result = "";
        stringURL += values;

        URL u = null;
        try {
            u = new URL(stringURL);
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();
            result = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        //LOGGER.info(result);
        // wrap result
        Response.ResponseBuilder builder = Response.ok(result);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");

        // Stream result
        return builder.build();
    }

    @POST
    @Path("/joingfi")
    /**
     */
    public Response geoserverJoinGetFeatureInfo(MultivaluedMap<String, String> queryParams) {

        Map<String,String[]> parameters = parse(queryParams);
        Layer layer = new Layer();
        setLayerName(parameters, layer);

        setJoinLayerOptions(parameters, layer, null, true);


      String result = getFeatureInfo( parameters.get("urlWMS")[0],
                layer,
                parameters.get("lang")[0],
                parameters.get("width")[0],
                parameters.get("height")[0],
                parameters.get("x")[0],
                parameters.get("y")[0],
                parameters.get("bbox")[0],
                parameters.get("srs")[0]);

        //LOGGER.info(result);
        // wrap result
        Response.ResponseBuilder builder = Response.ok(result);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");

        // Stream result
        return builder.build();
    }





    @GET
    @Path("/bbox/{boundary}/{code}/{srs}")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     *
     * type (I.E. FAOSTAT, ISO2, ISO3, GAUL0)
     *
     * layer
     * columnname
     * code
     *
     *
     */
    public Response getBBOX(
            @PathParam("boundary") String boundary,
            @PathParam("code") String code,
            @PathParam("srs") String srs) {

        String epsg = "EPSG:3857";

        if ( srs != null )
            epsg = srs;
        BOUNDARIES b = BOUNDARIES.valueOf(boundary.toUpperCase());
        //LOGGER.info(b.getConversionTable());
        //LOGGER.info(b.getColumnName());
        BBox bbox = zoomToAdministrativeUnit(b.getConversionTable(), b.getColumnName(), code, epsg);

        String result = "{";
        result += "\"xmin\" : \""+  bbox.getXmin() +"\",";
        result += "\"xmax\" : \""+  bbox.getXmax() +"\",";
        result += "\"ymin\" : \""+  bbox.getYmin() +"\",";
        result += "\"ymax\" : \""+  bbox.getYmax() +"\"";
        result +="}";

        // wrap result
        // wrap result
        Response.ResponseBuilder builder = Response.ok(result);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");

        // Stream result
        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spatial")
    public Response get(
            @FormParam("datasource") final String datasource,
            @FormParam("select") final String select,
            @FormParam("layername") final String layername,
            @FormParam("srs") final String srs,
            @FormParam("geocolumn") final String geocolumn,
            @FormParam("polygon") final String polygon
    ) {

        /**
         * TODO: How to get
         * **/

        //System.out.println("[spatial]");

        try {

            // compute result

            DATASOURCE ds = DATASOURCE.valueOf(datasource.toUpperCase());

            //String s = "SELECT adm0_code, faost_n ";
            String s = "SELECT " + select.toLowerCase();

            // FROM
            //s += " FROM gaul0_faostat_3857 ";
            s += " FROM "+ layername.toLowerCase() +" ";


            // WHERES
            s += "WHERE ";
            //s += "ST_Contains( ST_Polygon(ST_GeomFromText('LINESTRING(156543.03392804097 6887893.4928338, 6809621.975869781 7083572.285243855, 6809621.975869781 1448023.0638343797, -958826.0828092508 1663269.7354854352, 156543.03392804097 6887893.4928338)'),3857),  geom) ";

            s += "ST_Contains( ST_Polygon(ST_GeomFromText('LINESTRING(" + polygon + ")')";

            if ( srs == null )
                s += ",3857), ";
            else
                s += "," + srs + "), ";
            if ( geocolumn == null )
                s += " geom) ";
            else
                s += geocolumn +") ";



            SQLBean sql = new SQLBean();
            sql.setQuery(s);

            // compute result
            DBBean db = new DBBean(ds);

            List<List<String>> table = JDBCConnector.query(db, sql, true);
            Gson g = new Gson();
            String json = g.toJson(table);

            // wrap result
            Response.ResponseBuilder builder = Response.ok(json);
            builder.header("Access-Control-Allow-Origin", "*");
            builder.header("Access-Control-Max-Age", "3600");
            builder.header("Access-Control-Allow-Methods", "POST");
            builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");
            builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=utf-8");


            // return response
            return builder.build();


        } catch (WDSException e) {
            LOGGER.error(e.getMessage());
            return Response.status(500).entity("Error in 'getPoints' service: " + e.getMessage()).build();
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            return Response.status(500).entity("Error in 'getPoints' service: " + e.getMessage()).build();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.status(500).entity("Error in 'getPoints' service: " + e.getMessage()).build();
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage());
            return Response.status(500).entity("Error in 'getPoints' service: " + e.getMessage()).build();
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
            return Response.status(500).entity("Error in 'getPoints' service: " + e.getMessage()).build();
        }

    }

    private BBox zoomToAdministrativeUnit(String value) {
        // parse value
        String boundaryname = Parser.extractValue(value, "(");
        BOUNDARIES boundary = BOUNDARIES.valueOf(boundaryname.toUpperCase());

        // for now get's the single value;
        String code = Parser.extractValue(value, "(", ")");

        // TODO: not hardcode the SRS
        return zoomToAdministrativeUnit(boundary.getConversionTable(), boundary.getColumnName(), code, "EPSG:3857");
    }

    private BBox zoomToAdministrativeUnit(String tablename, String columnname, String code, String srs) {
        BBox bbox = null;
        try {
            bbox = dataServiceUtils.getBBox(tablename, columnname, code, srs);
        } catch (IOException e) {e.printStackTrace();}
        return bbox;
    }




    private Map<String, String[]> parse(MultivaluedMap<String, String> queryParams) {
        Map<String,String[]> parameters = new HashMap<String, String[]>();

        for (String s : queryParams.keySet() ) {
            int i=0;
            String a[] = null;
            for(String sub : queryParams.get(s)) {
                a = new String[queryParams.get(s).size()];
                a[i] = sub;
                i++;
            }
            parameters.put(s, a);
        }

        return parameters;
    }

    private void setLayerName(Map<String,String[]> parameters, Layer layer ) {
        for(String key: parameters.keySet()) {
            try {
                KEYWORDS k = KEYWORDS.valueOf(key.toLowerCase());
                String value = parameters.get(key)[0];
                switch (k) {
                    case layers:
                        layer.setLayerName(value);
                        layer.setTransparent(false);
                     break;
                    case cql_filter:
                        parseCqlFilters(value, layer);
                    break;
                }
            } catch (IllegalArgumentException e) {}
        }
    }

    private JOINTYPE setJoinLayerOptions(Map<String,String[]> parameters, Layer joinLayer, String language, Boolean isGFI) {
        JOINTYPE jointype = JOINTYPE.shaded;
        for(String key: parameters.keySet()) {
            try {
                JOINLAYER k = JOINLAYER.valueOf(key.toLowerCase());
                String value = parameters.get(key)[0];
//				LOGGER.info(k + " | " + value);
                switch (k) {
                    case joincolumn: parseJoinColumn(value, joinLayer); break;
                    case columnlabel: joinLayer.getJoinLayer().setColumnlabel(value); break;
                    case joindata:  joinLayer.getJoinLayer().setJoindata(parseJoinData(value));
                        joinLayer.getJoinLayer().setJoindataString(value);
                        break;
                    case intervals: joinLayer.getJoinLayer().setIntervals(Integer.valueOf(value)); break;
                    case colorramp: joinLayer.getJoinLayer().setColorramp(value); break;
                    case colorreverse: joinLayer.getJoinLayer().setColorreverse(Boolean.valueOf(value)); break;
                    case colors: joinLayer.getJoinLayer().setColors(parseColors(value)); break;
                    case ranges: joinLayer.getJoinLayer().setRanges(Parser.extractSortedDoules(value, ",")); break;
                    case classification: joinLayer.getJoinLayer().setClassification(value); break;
                    case joinboundary:  joinLayer.getJoinLayer().setBoundary(value.toUpperCase());
                        // setting the joincolumn
                        BOUNDARIES boundary = BOUNDARIES.valueOf(joinLayer.getJoinLayer().getBoundary());
                        joinLayer.getJoinLayer().setJoincolumn(boundary.getColumnName());
                        break;
//                    case pointdata: parsePointData(value); break;
                    case mu: joinLayer.getJoinLayer().setMeasurementUnit(value.replace("+", " ")); break;
                    case date: joinLayer.getJoinLayer().setDate(value); break;
                    case jointype: joinLayer.getJoinLayer().setJointype(value);
                        jointype = JOINTYPE.valueOf(value);
                        break;
                    case addborders: joinLayer.getJoinLayer().setAddBorders(Boolean.valueOf(value)); break;
                    case borderscolor: joinLayer.getJoinLayer().setBordersColor(value); break;
                    case bordersstroke: joinLayer.getJoinLayer().setBordersStroke(value); break;
                    case bordersopacity: joinLayer.getJoinLayer().setBordersOpacity(value); break;
                    case thousandseparator: joinLayer.getJoinLayer().setThousandSeparator(value); break;
                    case decimalseparator: joinLayer.getJoinLayer().setDecimalSeparator(value); break;
                    case decimalnumbers: joinLayer.getJoinLayer().setDecimalNumbers(Integer.valueOf(value)); break;
                }
            } catch (IllegalArgumentException e) {}
        }

        // layers
        try {
            String value = parameters.get(KEYWORDS.legendtitle.name())[0];
            // TODO: parse it better, not just replace
            joinLayer.getJoinLayer().getLegend().setTitle(value.replace("+", " "));
        }catch (Exception e) {}
        // layers
        try {
            String value = parameters.get(KEYWORDS.legendposition.name())[0];
            joinLayer.getJoinLayer().getLegend().setPosition(value);
        }catch (Exception e) {}

        try {
            // this performs the rounding of the decimal numbers before to be passed to the algorithm
            // to generate the SLD
            if (joinLayer.getJoinLayer().getDecimalNumbers() != null ) {
                if ( joinLayer.getJoinLayer().getDecimalNumbers() >= 0 ) {
                    joinLayer.getJoinLayer().setJoindata(MapUtils.roundValues(joinLayer.getJoinLayer().getJoindata(), joinLayer.getJoinLayer().getDecimalNumbers()));
                    joinLayer.getJoinLayer().setJoindataString(MapUtils.valuesString(joinLayer.getJoinLayer().getJoindata()));
                }
            }
        }catch (Exception e) {}


        if (!isGFI ) createJoinLayer(joinLayer, language);

        return jointype;
    }

    private void parseJoinColumn(String value, Layer layer) {
        layer.getJoinLayer().setJoincolumn(value);
    }

    private Map<String, Double> parseJoinData(String value) {
        String parserOpen = "{";
        String parserClose = "}";
        String parserSingleValue = ":";
        String parseValue = "\"";
        // TODO: quick patch, do it nicer with json object
        if ( !value.contains("{")) {
            parserOpen = "(";
            parserClose = ")";
            parserSingleValue = ",";
            parseValue = null;
        }

        // the join boolean it's already been setted from the styles
        Map<String, Double> joindata = new HashMap<String, Double>();
        String data = Parser.extractValue(value, "[", "]");
        List<String> singleValues = Parser.extractValues(data, parserOpen, parserClose);
        for(String singleValue : singleValues) {
            List<String> d = Parser.extractValues(singleValue, parserSingleValue);
            // the first value is the featurecode
            // the second is the right value
            try {
                if ( parseValue == null)
                    joindata.put(d.get(0), Double.valueOf(d.get(1)));
                else {
                    joindata.put(d.get(0).replaceAll(parseValue, ""), Double.valueOf(d.get(1).replaceAll(parseValue, "")));
                }
            } catch (Exception e) {}
        }
        return joindata;
    }

    private List<String> parseColors(String value){
        List<String> l = new ArrayList<String>();
        List<String> cs = Parser.extractValues(value, ",");
        for(String c : cs) {
            l.add("#" + c);
        }
        return l;
    }

    private void createJoinLayer(Layer joinLayer, String language) {
        // TODO: here to get the areas? (in theory should be performed on the layer table)
        CLASSIFICATION c = CLASSIFICATION.valueOf(joinLayer.getJoinLayer().getClassification());
        switch (c) {
            case equalarea: joinLayer.getJoinLayer().setAreas(getAreas(joinLayer.getJoinLayer().getBoundary(), joinLayer.getJoinLayer().getJoindata())); break;
        }

        JOINTYPE j = JOINTYPE.valueOf(joinLayer.getJoinLayer().getJointype());
        switch (j) {
            case shaded: createShadedJoinLayer(joinLayer); break;
            case point: createPointJoinLayer(joinLayer.getJoinLayer(), language); break;
        }
    }

    private void createShadedJoinLayer(Layer joinLayer) {
        // create join
        JoinLayerUtils joinLayerUtils = new JoinLayerUtils();

        /** TODO: issue with the namespace: fenix, set on the configuration file?**/
        String layername = joinLayer.getLayerName().replace("fenix:", "");
        layername = "fenix:" + layername;
        joinLayer.setLayerName(layername);
        String sldFilename = joinLayerUtils.createJoinSLDREST(layername, mapsConf.getStylesPath(), joinLayer.getJoinLayer(), null);
        joinLayer.setStyleURL(getSldURL(mapsConf.getUrl(), sldFilename));
    }

    private String getSldURL(String mapURL, String sldFilename) {
        String sldURL = "http://" + mapURL + "/styles/" + sldFilename;
        return sldURL;
    }



    private void createPointJoinLayer(JoinLayer joinLayer, String language) {
//		LOGGER.info("createPointJoinLayer");
        BOUNDARIES boundary = BOUNDARIES.valueOf(joinLayer.getBoundary());
        try {
            joinLayer.setJoininfo(dataServiceUtils.getPointData(boundary.getConversionTable(), boundary.getColumnName(), joinLayer.getJoindata(), language));
        } catch (IOException e) {e.printStackTrace();}

        JoinLayerUtils joinLayerUtils = new JoinLayerUtils();
        joinLayerUtils.createPoints(joinLayer);


        joinLayer.setPointsJSON(LayerVector.createCircleLeafletJson(joinLayer.getJoininfo(), joinLayer.getJoindata(), joinLayer.getThousandSeparator(), joinLayer.getDecimalSeparator()));
    }

    private Map<String, Double> getAreas(String boundary, Map<String, Double> values) {
//		LOGGER.info("getAreas");
        Map<String, Double> result = new HashMap<String, Double>();
        BOUNDARIES b = BOUNDARIES.valueOf(boundary);
        try {
            result = dataServiceUtils.getAreas(b.getConversionTable(), b.getColumnName(), "area_dd", values);
        } catch (AxisFault e) {e.printStackTrace();}

        return result;
    }


    private void parseCqlFilters(String value, Layer layer)  {
        List<String> lv = Parser.extractValues(value, ";");
        for (int i=0; i < lv.size(); i++) {
            String v = lv.get(i);
            if ( !v.equals("") ) {
                layer.setCql_filter(v.replace(" ", "+"));
            }
        }
    }

    private String getFeatureInfo(String urlWMS,
                                  Layer layer,
                                  String lang,
                                  String width,
                                  String height,
                                  String x,
                                  String y,
                                  String bbox,
                                  String srs) {
        String s = null;

        try {
            String layername = layer.getLayerName();
            /** TODO: get the joinboudary **/
            String joincolumn = layer.getJoinLayer().getJoincolumn();
            String columnlabel = layer.getJoinLayer().getColumnlabel();
            String measurementunit = layer.getJoinLayer().getMeasurementUnit();

			measurementunit = StringEscapeUtils.escapeHtml(measurementunit);

            Map<String, Double> values = layer.getJoinLayer().getJoindata();
            //LOGGER.info(values);

            s = GetFeatureInfoREST.getJoinFeatureInfo(urlWMS, layername, joincolumn, columnlabel, values, measurementunit, lang, width, height, x, y, bbox, srs);
        }catch (Exception e) {}
        return s;
    }


}