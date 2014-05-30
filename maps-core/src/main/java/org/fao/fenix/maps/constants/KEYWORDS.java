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
package org.fao.fenix.maps.constants;

public enum KEYWORDS {

	// keyword (i.e. countrystat)
	
	out, engine, height, width, keyword, renderto,
	
	layers, baselayers, styles, layertitle, hidden, cql_filter, bbox, lat, lon, zoom, minzoom, maxzoom, srs, format, bgcolor,

    lang,
	
	zoomto, // (JOINBOUNDARY (i.e. GAUL0, GAUL1,) // TODO: change name to BOUNDARY)
	
	// getFeatureInfo
	getfeatureinfo, query_layers, x, y, enablejoingfi, 
	
	jsoncallback,
	
	// export
	export,
	
	// legend TODO: move to join?
	legendtitle, legendposition,
	
	// legend TODO: move to join?
	addlayerlabels,

	shared;
}