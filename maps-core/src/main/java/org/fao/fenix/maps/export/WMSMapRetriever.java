package org.fao.fenix.maps.export;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.fao.fenix.maps.bean.map.BBox;
import org.fao.fenix.maps.bean.map.BaseLayer;
import org.fao.fenix.maps.bean.map.MAPSBean;
import org.fao.fenix.maps.bean.map.MapView;
import org.fao.fenix.maps.bean.map.legend.Legend;
import org.fao.fenix.maps.bean.map.legend.LegendInfo;
import org.fao.fenix.maps.util.MapUtils;


/**
 * Retrieves layers and builds an image out of them.
 * 
 * @author etj
 */

public class WMSMapRetriever {

	private final static Logger LOGGER = Logger.getLogger(WMSMapRetriever.class);

	private int height = 0;
	private int width = 0;
	private BBox bbox;
	private Legend legend;
	
	private boolean reaspectEnabled = true;

	private String tempDir = null;

	private List<Layer> layers = new ArrayList<Layer>();
	
	private String backgroundColor = null;

	static class Layer {
		String wmsUrl;
		String layerName;
		String styleName = null;
		String styleSld = null;
		String cql = null;
		Float opacity = null;

		public Layer(String wmsUrl, String layerName, String styleName, String styleSld, String cql) {
			this(wmsUrl, layerName);
			if (styleName != null )
				this.styleName = styleName;
			if (styleSld != null )
				this.styleSld = styleSld;
			if (cql != null )
				this.cql = cql;
		}

		public Layer(String wmsUrl, String layerName) {
			this.wmsUrl = wmsUrl;
			this.layerName = layerName;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName()
					+ "["
					+ layerName
					+ "@" + wmsUrl
					+ " %"+opacity
					+ "]";
		}
	}

	static class AggregatedLayer {
		String url;
		Float opacity;

		@Override
		public String toString() {
			return getClass().getSimpleName()
					+ "[" + url + " "+opacity + "]";
		}

	}

	public WMSMapRetriever(String tempDir) {
		this.tempDir = tempDir;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void addLayer(String wmsUrl, String layername, String stylename, String stylesld, String cql) {
		layers.add(new Layer(wmsUrl, layername, stylename, stylesld, cql));
	}



	/**
	 * <UL>
	 *    <LI>Fetch images from remote servers</LI>
	 *    <LI>Merge them together</LI>
	 * </UL>
	 *
	 * @return the resulting merged map image
	 */
	public BufferedImage getMapImage()
	{
		if(width == 0)
			setWidth(480);
		if(height == 0)
			setHeight(240);

		List<AggregatedLayer> aggregatedList = aggregateLayers(width, height);

		List<String> files = new ArrayList<String>();
		List<Float> opacity = new ArrayList<Float>();
		
		ConcurrentHTTPTransactionHandler c = new ConcurrentHTTPTransactionHandler();// DEBUG
		HttpCache hcache = new HttpCache(tempDir, 5);
		c.setCache(hcache); // DEBUG
		//c.checkIfModified(false); //DEBUG

		for (AggregatedLayer agg: aggregatedList) {
			c.addUrl(agg.url);
		}
		c.doTransactions();

		
		for (AggregatedLayer agg : aggregatedList) {
			String url = agg.url;
			String path = c.getResponseFilePath(url);
//			System.out.println("vImageUrls.get(i) = " + (String)vImageUrls.get(i));
//			System.out.println("path = " + path);
			if (path != null) {
				String contentType = c.getHeaderValue(url, "content-type");
//				System.out.println("contentType = " + contentType); // DEBUG
				if (contentType.startsWith("image")) {
//					System.out.println(agg);
					files.add(path);
					opacity.add(agg.opacity);
				} else {
					System.out.println("*** Bad response from " + url + " into " + path);
				}
			}
		}

		// Merge the images
		//Collections.reverse(files);
		if ( legend != null ) {
//			LinkedHashMap<String, String> legend = new LinkedHashMap<String, String>();
//			legend.put("ff0000", "> 16");
//			legend.put("ffffff", "20");
//			legend.put("faaaaa", "< 36");
			files.add(createLegend(legend, width, height));
			opacity.add(null);
		}
		
		BufferedImage bi = ImageMerger.merge(files, opacity, width, height);
		
		return bi;			
	}

	/**
	 * group close layers that are provided by the same server
	 */
	protected List<AggregatedLayer> aggregateLayers(int width, int height)
	{
		List<AggregatedLayer> urlList = new ArrayList<AggregatedLayer>();
		
		// TODO: this one is valid just for EPSG:4326
		//if( isReaspectEnabled() )
			//bbox = Reaspecter.reaspect(bbox, width, height);

		int seqstart = 0;
		int idx = 0;

		for (int i = 1; i < layers.size(); i++) {
			Layer layerThis = layers.get(i);
			Layer layerPrev = layers.get(i-1);
			System.out.println("Aggregating " + layerPrev);
//			if(! layer.wmsUrl.equals(layers.get(i-1).wmsUrl)) {
			if( ! areAggregable(layerPrev, layerThis)) {
				AggregatedLayer agg = new AggregatedLayer();
				agg.url = buildImageUrl(layers.subList(seqstart, i), bbox, width, height, (idx++==0));
				agg.opacity = layerPrev.opacity;
				urlList.add(agg);
				seqstart = i;
				System.out.println("breaking aggr group");
			}
		}

		// add last group
		if(seqstart < layers.size()) {
			AggregatedLayer agg = new AggregatedLayer();
			agg.url = buildImageUrl(layers.subList(seqstart, layers.size()), bbox, width, height, (idx++==0));
			agg.opacity = layers.get(seqstart).opacity;
			urlList.add(agg);
		}

//		for (String url : urlList)
//			LOGGER.info("URLList: " + url);
		
		return urlList;
	}

	protected boolean areAggregable(Layer l1, Layer l2) {
		/** TODO: this should check the respose, if it bad, should send again a sepataret request to the WMS server 
		 *  	  the nasa wms server doesn't support the multiple request 
		 */
		if (l1.wmsUrl.contains("nasa") || ! l2.wmsUrl.contains("nasa"))
			return false;
		// check wms url
		if( ! l1.wmsUrl.equals(l2.wmsUrl))
			return false;

		// check opacity
		if( l1.opacity != null ) {
			if( l2.opacity == null )
				return false;
			else {
				if( ! l1.opacity.equals(l2.opacity))
					return false;
			}
		} else {
			if( l2.opacity != null )
				return false;
		}

		return true;
	}
	
    /** 
	 * Build the getMap request.
	 * 
	 * TODO: 
	 *	1) refactor 
	 *	2) wms version and format are now fixed -> change url according to the server wms version
	 *	3) if requests with near indices are toward the same server, make a single request
	 * 
	 * @return the image request URL with  many image names (utility method) 
	 */
	protected String buildImageUrl(List<Layer> layers, BBox bb, int width, int height, boolean base)
	{
		String getMapHref = layers.get(0).wmsUrl;
			//_wmscapa.getCapability().getRequest().getGetMap().getDCPType(0).getHttpGetHref();
		StringBuilder cslayers  = new StringBuilder();
		StringBuilder csstyles  = new StringBuilder();
		String csssld  = null;
		StringBuilder cqlFilter = new StringBuilder();
		boolean useCQL = false;

		for (Layer layer : layers) {
			if(cslayers.length()!=0) {
				cslayers.append(",");
				csstyles.append(",");
				cqlFilter.append(";");
			}
			cslayers.append(layer.layerName);
			csstyles.append(layer.styleName==null?"":layer.styleName);
			
			System.out.println(" layer.styleSld: [" +  layer.styleSld + "]");
			if ( layer.styleSld != null ) {
				csssld = layer.styleSld;
			}
			
			String cql=layer.cql;
			if(cql!=null) {
//				try {
//					cql = URLEncoder.encode(cql, "UTF-8");
					cqlFilter.append(cql);
					useCQL = true;
////				} catch (UnsupportedEncodingException ex) {
////					LOGGER.warn(ex.getMessage(), ex);
//				}
			} else {
				cqlFilter.append("INCLUDE");
			}
		}

		if (getMapHref.indexOf("?") == -1)  
			getMapHref += "?";
		else if (!getMapHref.endsWith("?")) 
			getMapHref += "&";


//		String request = getMapHref 
//			+ "SERVICE=WMS" 
//			+ "&VERSION=" + WMSCapabilities.WMSVer.V111 
//			+ "&REQUEST=GetMap"
//			+ "&LAYERS=" + cslayers.toString()
//			+ "&STYLES=" + csstyles.toString()
//			+ "&SRS=" + bb.getSrs() 
//			+ "&BBOX=" + bb.getMinX() + "," + bb.getMinY() + "," + bb.getMaxX() + "," + bb.getMaxY() 
//			+ "&WIDTH=" + width + "&HEIGHT=" + height 
//            + (base && backgroundColor!=null? ("&BGCOLOR=0x"+backgroundColor) : "&TRANSPARENT=TRUE")
//            + "&FORMAT=image/png";
		
		String base_url = getMapHref;
		if (base_url.endsWith("?"))
			base_url = base_url.substring(0, base_url.indexOf("?"));
		
		String request = base_url + 
						 "/reflect?layers=" + cslayers.toString();

		if ( csssld == null || csssld.equals(""))
			request += "&STYLES=" + csstyles.toString();

		else
			 request +=	"&SLD=" + csssld.toString();
		
		request += "&SRS=" + bb.getSrs() + 
				   "&BBOX=" + bb.getXmin() + "," + bb.getYmin() + "," + bb.getXmax() + "," + bb.getYmax() + 
				   "&WIDTH=" + width;
		
		
         request += (base && backgroundColor!=null? ("&BGCOLOR=0x"+backgroundColor) : "&TRANSPARENT=TRUE")
        		 + "&FORMAT=image/png";
		// TODO: set the background color
//		request += "&TRANSPARENT=TRUE" 
//				     + "&FORMAT=image/png";

		if(useCQL) {
			request += "&CQL_FILTER=" + cqlFilter;
		}

		System.out.println(">>> REQUEST >>>\n" + request);

		return request;
	}



	public void setReaspectEnabled(boolean reaspectEnabled) {
		this.reaspectEnabled = reaspectEnabled;
	}

	public boolean isReaspectEnabled() {
		return reaspectEnabled;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public void setBoundingBox(BBox bbox) {
		this.bbox = bbox;
	}

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	/**
	 * Set the bg color in hex format RRGGBB (eg: 10ff35).
	 * If null, the background will be transparent.
	 * @param backgroundColor
	 */
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	
	public static String getImage(String path, MAPSBean mapBean) throws IOException {
		
		String filename = MapUtils.getRandomName() + ".png";

		// TODO: temp dir
		 // This is the property name for accessing OS temporary directory or
        // folder.
        String property = "java.io.tmpdir";
   
        // Get the temporary directory and print it.
        String tempDir = System.getProperty(property);
//		WMSMapRetriever mm = new WMSMapRetriever("/tmp");
        WMSMapRetriever mm = new WMSMapRetriever(tempDir);
		mm.setHeight(Integer.valueOf(mapBean.getHeight()));
		mm.setWidth(Integer.valueOf(mapBean.getWidth()));
		mm.setBoundingBox(mapBean.getMapView().getBbox());
		
		// background
		System.out.println("getBaseLayerList: " + mapBean.getMapView().getBaseLayerList());
		if ( !mapBean.getMapView().getBaseLayerList().isEmpty() ) {
			System.out.println(mapBean.getMapView().getBaseLayerList());
			// export baselayer
			MapView mapView = mapBean.getMapView();
//			String externalMapFile = ExportExternalLayer.getExternalLayer(mapView.getBaseLayerList().get(0).getLayerName(), mapView.getLat(), mapView.getLon(), mapView.getZoom(), mapBean.getWidth(), mapBean.getHeight());
		}
		

		for (org.fao.fenix.maps.bean.map.Layer l : mapBean.getMapView().getLayerList()) {
			
			
			if ( l.getBgcolor() != null && mm.backgroundColor == null ) {
				mm.setBackgroundColor(l.getBgcolor());
			}
			
			// quick fix 
			// TODO: pass it dinamically the URL
			if ( l.isJoin() )
				mm.setLegend(l.getJoinLayer().getLegend()); 
			
//			mm.addLayer("http://hqlprfenixapp1.hq.un.fao.org:9082/geo/wms?", l.getLayerName(), l.getStyleName(), l.getStyleURL());
			mm.addLayer(l.getGetMapUrl() + "?", l.getLayerName(), l.getStyleName(), l.getStyleURL(), l.getCql_filter());
		}
	
		// TODO: remove it...
//		if ( mm.backgroundColor == null )
//			mm.setBackgroundColor("ffffff");
		
		BufferedImage image = mm.getMapImage();
		ImageIO.write(image, "png", new File(path + File.separator + filename));
		
		System.out.println("WORK DONE");
		
		return filename;
	}
	
	private static String createLegend(Legend legend, int width, int height) {
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);	
		String filename = MapUtils.getRandomName() + ".png";
		
		String filepath = tempDir + File.separator + filename;
		  try {

		      // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
		      // into integer pixels
		      BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		      
		      Graphics2D ig2 = b.createGraphics();
		      
		      // this is for the background (if the width and height are dynamically calculated)
//		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
//		      ig2.setColor(new Color(0, 0, 0));
//		      ig2.fillRect(5, 5, width - 15, height - 15);
		      
		      // this is used to add height between objects
		      int padding = 0;
		      
		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		      Font font = new Font("Arial", Font.BOLD, 13);
		      ig2.setFont(font);
		      FontMetrics fontMetrics = ig2.getFontMetrics();
		      ig2.setColor(new Color(0, 0, 0));

		      if ( legend.getTitle() != null ) {
			      int stringWidth = fontMetrics.stringWidth(legend.getTitle());
			      int stringHeight = fontMetrics.getAscent();
	//		      ig2.drawString(title, (width - stringWidth) / 2, height / 2 + stringHeight / 4);
			      
			      if ( legend.getTitle() != null ) {
			    	  ig2.drawString(legend.getTitle(), 15, 15);
			    	  padding = padding + 25;
			      }
		      }
		      
//		      ig2.drawString(message, 10, 20);
//		      ig2.drawString(message, 10, 30);
		      
		      // set the rect
		      // Hex to color
//		      int intValue = Integer.parseInt("ff0000",16);
//		      ig2.setColor(new Color( intValue ));
//		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//		      ig2.fillRect(15, 25, 10, 10);
//		      
//		      // set the label
//		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//		      ig2.setColor(new Color(255, 255, 255));
//		      ig2.drawString(arg, 35, 33);
		      
		      font = new Font("Arial", Font.PLAIN, 12);
		      ig2.setFont(font);
		      for(LegendInfo l : legend.getLegendInfo() ) {
		    	  System.out.println("color: " + l.getColor() );
		    	  int intValue = Integer.parseInt(l.getColor().replace("#", ""),16);
			      ig2.setColor(new Color( intValue ));
			      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			      ig2.fillRect(15, padding, 10, 10);
			      
			      // set the label
			      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			      ig2.setColor(new Color(0, 0, 0));
			      ig2.drawString(l.getName(), 35, 10 + padding);
			      
			      // this is the padding to be added on each entry
			      padding = padding + 15;
		      }

		      // set the rect
//		      ig2.setColor(new Color(125, 167, 116));
//		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
//		      ig2.fillRect(10, 40, 10, 10);
//		      
//		      // set the label
//		      ig2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//		      ig2.setColor(new Color(255, 255, 255));
//		      ig2.drawString(arg, 25, 48);

		      ImageIO.write(b, "PNG", new File(filepath));
//		      ImageIO.write(bi, "JPEG", new File("c:\\yourImageName.JPG"));
//		      ImageIO.write(bi, "gif", new File("c:\\yourImageName.GIF"));
//		      ImageIO.write(bi, "BMP", new File("c:\\yourImageName.BMP"));
		      
		    } catch (IOException ie) {
		      ie.printStackTrace();
		    }
		  return filepath;
	}
	
	
	public static void main(String[] args) {
		try {

			WMSMapRetriever mm = new WMSMapRetriever("/tmp");
			
			int height = 450;
			int width = 800;
			mm.setHeight(450);
			mm.setWidth(800);
			//http://lprapp08:8080/geoserver/wms?
			// bbox=-18.746560000000002,-36.4765,51.84788,37.61512999999999
			// &styles=&Format=application/openlayers&request=GetMap&version=1.1.1
			// &layers=fenix:di09011&width=574&height=550&srs=EPSG:4326
//			mm.addLayer("http://lprapp08:8080/geoserver/wms?", "fenix:di09011", null, null);


//			CQLFilter f1 = GaulCQLFilterFactory.createGaul1Is("51330");
//			CQLFilter f2 = GaulCQLFilterFactory.createGaul1Is("51331");
//			CQLFilter f = CQLFilterOp.or(f1,f2);

//			mm.addLayer("http://127.0.0.1:9090/geoserver/wms?", "fenix:bluemarble", null);
//			mm.addLayer("http://127.0.0.1:9090/geoserver/wms?", "fenix:gaul_faostat", "join", "http://127.0.0.1:8080/maps/styles/sldjoin_31-5-2012_925780.sld");
			mm.addLayer("http://fenix.fao.org/geo/reflect?", "fenix:gaul0_faostat_3857", "polygon", null, null);
			
//			mm.addLayer("http://fenixapps.fao.org:80/geo/wms", "fenix:gaul0_line", "gaul0_line", null);
//			mm.addLayer("http://127.0.0.1:9090/geoserver/wms?", "fenix:g2008_0", "zzlayer_gaul0", null);			
//			mm.addLayer("http://127.0.0.1:9090/geoserver/wms?", "fenix:g2008_0", "zzlayer_gaul0", null);

			
			//mm.addLayer("http://lprapp08.fao.org:8080/geoserver/wms?", "fenix:zzlayer_gaul1",  "zzlayer_gaul1_black", f,.8f);
//			mm.setBoundingBox(new BBox("EPSG:4326", -180.0, -89.9999999999971, 179.9999999999942, 90.0));
			mm.setBoundingBox(new BBox("EPSG:4326", -80.0, -39.9999999999971, 79.9999999999942, 20.0));

			//mm.setTempDir("/tmp");

//			WMSMapProvider g0 = null;
//			GeoView geoViewG0 = new GeoView(g0);
//			geoViewG0.setStyleName("polygon_filled");
//			geoViewG0.setCqlFilter(GaulCQLFilterFactory.createGaul0IsNot(countryCode));
//			mm.addLayer(geoViewG0);
//
//			WMSMapProvider g1 = null;
//			GeoView geoViewG1 = new GeoView(g1);
//			geoViewG1.setStyleName("zzlayer_gaul1_black");
//			geoViewG1.setCqlFilter(GaulCQLFilterFactory.createGaul1Is(gaul1code);
//			mm.addLayer(geoViewG1);
//
//			mm.setBackgroundColor("2020ff");
			
			mm.setBackgroundColor("ffffff");


			BufferedImage image = mm.getMapImage();
//			format=image%2Fsvg%2Bxml
			String legendFile = mm.tempDir + File.separator + MapUtils.getRandomName();
			ImageIO.write(image, "png", new File(legendFile));
			System.out.println("WORK DONE");
			
			
			String s = getData("http://search.twitter.com/search.json?q=rome&result_type=mixed&rpp=100"); 
			System.out.println(s);
			
			System.exit(0);
			

			
		} catch(IOException ex) {
//			Logger.getLogger(MapRetriever.class).error(null, ex);
			System.out.println(ex);
		}
		
	}
	
	public static String getData(String fullURL) throws MalformedURLException, IOException {
		URL u = new URL(fullURL);
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
		return sb.toString();
	}
	
}
