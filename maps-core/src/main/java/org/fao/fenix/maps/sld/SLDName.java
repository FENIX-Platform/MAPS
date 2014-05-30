package org.fao.fenix.maps.sld;

import java.util.Date;

public class SLDName {
	
	public static String getRandomName() {
		Date date = new Date();
		String d = date.getDate() + "-"+ (date.getMonth()+1) + "-"+ (date.getYear() + 1900);
		String sldName = d +"_" +String.valueOf(Math.floor(Math.random() * 100000)).replace(".", "");
		return sldName;
	}
}
