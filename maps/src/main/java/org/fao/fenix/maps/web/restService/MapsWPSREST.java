package org.fao.fenix.maps.web.restService;

import org.apache.log4j.Logger;
import org.fao.fenix.maps.configurations.GeoserverConf;
import org.fao.fenix.maps.configurations.ScriptsConf;
import org.fao.fenix.maps.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vortex
 * Date: 7/30/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Path("/wps")
public class MapsWPSREST {

    @Autowired
    private GeoserverConf geoserverConf;

    @Autowired
    private ScriptsConf scriptsConf;

    private static final Logger LOGGER = Logger.getLogger(MapsWPSREST.class);

    @GET
    @Path("/hist")
    // i.e. layers=fenix:raster[.geotiff always like that?] (workspace/rastername)
    /**
     */
    public Response hist(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        Map<String,String[]> parameters = parse(queryParams);
        String result = "";
        List<String> results = Parser.extractValues(parameters.get("layers")[0], ":");
        String workspace = results.get(0);
        String layer = results.get(1);
        String script  = scriptsConf.pythonScriptsPath + scriptsConf.pythonHistogram;
        String options = geoserverConf.datadir + "/data/" + workspace + "/" + layer + "/"+ layer +".geotiff";
        LOGGER.info(options);
        String[] cmd = new String[] {"python", script, options};
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmd);
            BufferedReader br = new BufferedReader (new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine())!=null) {
                result += line;
            }
        }catch (Exception e) { LOGGER.error(e.getMessage()); }

        LOGGER.info(result);
        // wrap result
        Response.ResponseBuilder builder = Response.ok(result);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With, Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Accept-Charset, Keep-Alive, Connection, Referer,Origin");

        // Stream result
        return builder.build();
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
}