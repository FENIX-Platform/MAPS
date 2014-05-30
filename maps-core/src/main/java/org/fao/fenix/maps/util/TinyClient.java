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
package org.fao.fenix.maps.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/** 
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a> 
 * */
public class TinyClient {
	
	private String tinyBaseURL;

	public String register(String fullURL) throws MalformedURLException, IOException {
		fullURL = fullURL.replaceAll("&", "!");
		URL u = new URL(tinyBaseURL + fullURL);
		URLConnection uc = u.openConnection();
		HttpURLConnection connection = (HttpURLConnection) uc;
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		StringBuilder sb = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			sb.append(inputLine);
		in.close();
		return sb.toString();
	}

	public void setTinyBaseURL(String tinyBaseURL) {
		this.tinyBaseURL = tinyBaseURL;
	}
	
}