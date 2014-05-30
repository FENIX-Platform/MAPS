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
package org.fao.fenix.maps.bean.map.legend;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class Legend {
	
	private String legendUrl;
	
	private String html;
	
	private String title;
	
	private List<LegendInfo> legendInfo = new ArrayList<LegendInfo>();
	
	private String position = LegendPosition.bottomright.toString();	
	
	public List<LegendInfo> getLegendInfo() {
		return legendInfo;
	}

	public void setLegendInfo(List<LegendInfo> legendInfo) {
		this.legendInfo = legendInfo;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getLegendUrl() {
		return legendUrl;
	}

	public void setLegendUrl(String legendUrl) {
		this.legendUrl = legendUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}	
}
