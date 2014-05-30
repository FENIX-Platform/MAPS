package org.fao.fenix.maps.geoserver.publish;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

import org.fao.fenix.maps.configurations.GeoserverConf;


public class GeoServerPublisher {

	// Methos to publish SLDs (inside the sld there is the sldName)
	public boolean publishStyle(String resturl, String username, String password, String sld) {
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(resturl, username, password);
		boolean published = publisher.publishStyle(sld);
		if ( !published ){
			// TODO check if the layer can be published with that name?
//			LOGGER.error("SLD already puslihed, find a solution");
		}
		return published;
	}

}
