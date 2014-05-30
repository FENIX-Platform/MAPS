/*
 */

package org.fao.fenix.maps.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.Header;

/**
 *
 * @author sgiaccio, etj
 */
public class HttpCache {
//      private File directory;
	private static Map uriMap = new HashMap();
	private static Map headersMap = new HashMap();
	
	private static Map<String, TempFilesHandler> tempDirs = new HashMap<String, TempFilesHandler>();
	private TempFilesHandler tf;
		
	// constructor
	public HttpCache(String cacheDir, int expireTime) {
		tf = tempDirs.get(cacheDir);
		if(tf == null) {
			tf = new TempFilesHandler(cacheDir, expireTime + 5);
			tempDirs.put(cacheDir, tf);
		}
	}

	public void put(String uri, byte[] response, Header[] headers) throws IOException {
		File f = tf.getFile();
		ByteArrayInputStream is = new ByteArrayInputStream(response);
		FileOutputStream os = new FileOutputStream(f);

		byte[] buf = new byte[1024];
		for(int nRead; (nRead = is.read(buf, 0, 1024)) > 0;) {
			os.write(buf, 0, nRead);
		}

		uriMap.put(uri, f.getAbsolutePath());
		headersMap.put(uri, headers);
	}

	public void put(String uri, InputStream is, Header[] headers) throws IOException {
		File f = tf.getFile();
		FileOutputStream os = new FileOutputStream(f);

		byte[] buf = new byte[1024];
		for(int nRead; (nRead = is.read(buf, 0, 1024)) > 0;) {
			os.write(buf, 0, nRead);
		}

		uriMap.put(uri, f.getAbsolutePath());
		headersMap.put(uri, headers);
	}

	public byte[] get(String uri) throws IOException {
		String name = (String) uriMap.get(uri);

		if(name == null) {
			return null;
		}

		File f = new File(name);

		if(!f.exists()) {
			return null;
		}

		FileInputStream is = new FileInputStream(f);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		for(int nRead; (nRead = is.read(buf, 0, 1024)) > 0;) {
			os.write(buf, 0, nRead);
		}

		return os.toByteArray();
	}

	public String getResponseFilePath(String uri) {
		return (String) uriMap.get(uri);
	}

	public String getHeaderValue(String uri, String header) {
		Header[] h = (Header[]) headersMap.get(uri);
		if(h == null) {
			return null;
		}

		for(Header loopheader : h) 
		{
//                      System.out.println("name: " + loopheader.getName() + " = " + loopheader.getValue());
			if(header.equalsIgnoreCase(loopheader.getName())) {
				return loopheader.getValue();
			}
		}
		return null;
	}

	public Calendar getCachedTime(String uri) {
		String name = (String) uriMap.get(uri);

		if(name == null) {
			return null;
		}

		File f = new File(name);
		if(!f.exists()) {
			return null;
		}

		Date t = new Date(f.lastModified());
		Calendar c = Calendar.getInstance();
		c.set(t.getYear(), t.getMonth(), t.getDate(), t.getHours(), t.getMinutes(), t.getSeconds());

		return c;
	}

	public void clear() {
		uriMap.clear();
	}
}

