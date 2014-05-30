package org.fao.fenix.maps.constants;

public enum BASELAYER {
	
	DEFAULT("default","default","default"), 
	OSM("Open Street Map","Open Street Map","Open Street Map"), 
	MAPQUEST("Map Quest","Map Quest","Map Quest"),  
	ESRI_WSM("ESRI - World Street Map","ESRI - World Street Map", "ESRI - World Street Map"),
	MAPQUEST_NASA("Blue Marble","Blue Marble","Blue Marble");

	
	private String nameE;
	
	private String nameS;
	
	private String nameF;
	
	private BASELAYER(String nameE, String nameS, String nameF){
		this.setNameE(nameE);
		this.setNameS(nameS);
		this.setNameF(nameF);
	}
	
	public String getName(String language) {
		try {
			if ( language.toUpperCase().equals("E")) 
				return getNameE();
			else if ( language.toUpperCase().equals("S")) 
				return getNameS();
			else if  ( language.toUpperCase().equals("F")) 
				return getNameF();
		} catch (Exception e) {}
		return getNameE();
	}

	public String getNameE() {
		return nameE;
	}

	public void setNameE(String nameE) {
		this.nameE = nameE;
	}

	public String getNameS() {
		return nameS;
	}

	public void setNameS(String nameS) {
		this.nameS = nameS;
	}

	public String getNameF() {
		return nameF;
	}

	public void setNameF(String nameF) {
		this.nameF = nameF;
	}
}
