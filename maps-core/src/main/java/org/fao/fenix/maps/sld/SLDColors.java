package org.fao.fenix.maps.sld;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.geotools.brewer.color.BrewerPalette;
import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.PaletteType;

public class SLDColors {
	
	static String paletteDefault = "Reds";
//  Palettes:
//	YlOrRd
//	Grays
//	PuBuGn
//	RdPu
//	BuPu
//	YlOrBr
//	Greens
//	BuGn
//	GnBu
//	PuRd
//	Purples
//	Blues
//	Oranges
//	PuBu
//	OrRd
//	Reds
//	YlGn
//	YlGnBu
	
	static PaletteType paletteTypeDefault = ColorBrewer.ALL;

	public static List<String> getColorsPalette(Integer interval, boolean colorreverse) {
		return  getColorsPalette(null, interval, colorreverse);
	}


	public static List<String> getColorsPalette(String palette, Integer interval, boolean colorreverse){
		List<String> colors = new ArrayList<String>();	
		String paletteName;
		if ( palette != null )
			paletteName = palette;
		else 
			paletteName = paletteDefault;
		 
		ColorBrewer brewer = null;

		try {
			if ( brewer == null )
				brewer = ColorBrewer.instance(paletteTypeDefault);
		} catch (Exception e1) {}
		
		// Instantiate the color palette
		BrewerPalette brewerPalette = brewer.getPalette(paletteName);
		Color[] cls = brewerPalette.getColors(interval);
		if ( !colorreverse ) {
			for (int i = 0; i < cls.length; i++) {
				Color color = cls[i];
//				System.out.println("RGB color: " + color.getRGB());
				String rgb = Integer.toHexString(color.getRGB());
				String c = "#" + rgb.substring(2, rgb.length());
//				String c = "#"+Integer.toHexString( color.getRGB() & 0x00ffffff );
				colors.add(c);
			}
		}
		else {
			for (int i = cls.length-1; i >= 0; i--) {
				Color color = cls[i];
				String rgb = Integer.toHexString(color.getRGB());
				String c = "#" + rgb.substring(2, rgb.length());
//				String c = "#"+Integer.toHexString( color.getRGB() & 0x00ffffff );
				colors.add(c);
			}
		}
		return colors;
	}
	

}
