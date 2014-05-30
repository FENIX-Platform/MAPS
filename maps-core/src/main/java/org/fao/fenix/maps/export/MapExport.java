package org.fao.fenix.maps.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MapExport {
	
	/**
	 *
	 * TODO: returning a URL would be much better
	 */
//	public String exportAsImage(ClientMapView cmv) {
//		
////		MapView mapView = getMapViewUtils().build(cmv);
////		MapRetriever mapRetriever = new MapRetriever(mapView, System.getProperty("java.io.tmpdir"));
//		
//		mapRetriever.setWidth(1024);
//		mapRetriever.setHeight(768);
//		
//		BufferedImage bi = mapRetriever.getMapImage();
//
//		try {
//			File png = File.createTempFile("map", ".png", exportPath.getFile());
//			ImageIO.write(bi, "png", png);
//			png.deleteOnExit(); // todo: we need a process that deletes temp files after a while
//			return png.getName();
//		} catch (IOException ex) {
//			LOGGER.warn("Error in saving map image", ex);
//			return null;
//		}
//	}

}
