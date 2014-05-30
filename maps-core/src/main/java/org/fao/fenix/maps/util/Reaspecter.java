/*
 */

package org.fao.fenix.maps.util;

import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.BBox;

/**
 *
 * @author ETj
 */
public class Reaspecter {
	private final static Logger LOGGER = Logger.getLogger(Reaspecter.class);

	public static BBox reaspect(BBox bb, int w, int h) {
		// Get boundaries
//		double north = bb.getMaxLat();
//		double south = bb.getMinLat();
//		double east = bb.getMaxLon();
//		double west = bb.getMinLon();
		double north = bb.getYmax();
		double south = bb.getYmin();
		double east = bb.getXmax();
		double west = bb.getXmin();

		double dx = Math.abs(east - west);
		double dy = Math.abs(north - south);

		// Reaspect
		if ((w / dx) > (h / dy))
		{
			double d = dy * w / h - dx;
			west -= d / 2;
			east += d / 2;
			LOGGER.info("REASPECTING - changing ratio WE += " + d);
		}
		else if ((h / dy) > (w / dx))
		{
			double d = dx * h / w - dy;
			south -= d / 2;
			north += d / 2;
			LOGGER.info("REASPECTING - changing ratio NS += " + d);
		}

		// Check for pan overflows: shift the map up or down if it can
		// N-S: limit navigation
		if(north > 90 && south>-90)
		{
			double off = north - 90;
			north = 90;
			south -= off;
			LOGGER.info("REASPECTING - shifting NS -= " + off);
		}

		if(south < -90 && north < 90)
		{
			double off = - 90 - south;
			south = -90;
			north += off;
			LOGGER.info("REASPECTING - shifting NS += " + off);
		}

		// If the map has scrolled enough sideways, then roll the view
		// W-E: wrap navigation
		if(west > 180)
		{
			east -= 180;
			west -= 180;
			LOGGER.info("REASPECTING - wrapping WE -= 180");
		}

		if(east < -180)
		{
			east += 180;
			west += 180;
			LOGGER.info("REASPECTING - wrapping WE += 180");
		}

		// If the map is too much reduced, zoom it to fit the view
		if ((Math.abs(east - west)) > 360 && (Math.abs(north - south)) > 180)
		{
			// Which side can be fully extended?
			if(w/360f > h/180f)
			{
				north = 90;
				south = -90;

				float we = 180.0f/h*w;
				west = -we/2;
				east = we/2;
				LOGGER.info("REASPECTING - NS fit, WE = " + we);
			}
			else
			{
				west = -180;
				east = 180;

				float ns = 360.0f/w*h;
				north = ns/2;
				south = -ns/2;
				LOGGER.info("REASPECTING - WE fit, NS = " + ns);
			}
		}
//		System.out.println("north = " + north + "; south = " + south + "; east = " + east + "; west = " + west);

		return new BBox(bb.getSrs(), west, south, east, north);
	}

}
