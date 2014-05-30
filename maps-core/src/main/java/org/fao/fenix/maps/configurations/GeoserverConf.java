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
package org.fao.fenix.maps.configurations;

/** 
 * @author <a href="mailto:simone.murzilli@fao.org">Simone Murzilli</a>
 * @author <a href="mailto:simone.murzilli@gmail.com">Simone Murzilli</a> 
 * */
public class GeoserverConf {

    public String wmsurl;

    public String resturl;

    public String datadir;

    public String username;

    public String password;

    public void setResturl(String resturl) {
        this.resturl = resturl;
    }

    public void setUsername(String username) { this.username = username;}

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWmsurl(String wmsurl) { this.wmsurl = wmsurl; }

    public void setDatadir(String datadir) { this.datadir = datadir; }


}
