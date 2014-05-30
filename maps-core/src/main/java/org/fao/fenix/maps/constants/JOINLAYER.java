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

public enum JOINLAYER {
	
	// joinpropertyname (i.e. ADM_CODE0)
	// level (i.e. GAUL0)

	joincolumn, columnlabel, joindata,  intervals, colorramp, colorreverse, level, classification, ranges, colors,
	
	//measurementunit
	mu, date, 
	
	jointype,
	
	// GUAL0, FAOSTAT, ISO2, ISO3...
	joinboundary,
	
	// inserting directly a poindata (with icon etc)
	pointdata,
	
	// borders
	addborders, borderscolor, bordersstroke, bordersopacity,
	
	// roundings
	thousandseparator, decimalseparator, decimalnumbers;
	
}