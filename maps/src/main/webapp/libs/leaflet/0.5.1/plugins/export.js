L.Control.Export = L.Control.extend({
	options: {
		position: "topleft",
		useAnchor: true,
		useMarker: true,
		markerOptions: {}
	},

	onAdd: function(map) {
		this._container = L.DomUtil.create('div', '');
		L.DomEvent.disableClickPropagation(this._container);
		this._map = map;
		this._href = L.DomUtil.create('a', null, this._container);
		this._href.innerHTML = "<div class='export'  onclick='exportMap()'></div>";
		return this._container;
	}
});



function exportMap() {
	var href = window.location.href.split('#')[0], idx = href.indexOf('?');

	// getting the right bounding box
//	var BBOX = map.getBounds()._southWest.lng+","+map.getBounds()._southWest.lat+","+map.getBounds()._northEast.lng+","+map.getBounds()._northEast.lat;

	var bounds = map.getBounds(),
	sw = map.options.crs.project(bounds.getSouthWest()),
	ne = map.options.crs.project(bounds.getNorthEast());
	//alert(sw);
	//alert(ne);

	var BBOX = sw.x + "," + sw.y +"," + ne.x + "," + ne.y;
	
	//alert(BBOX);
	
	// this is to get the image size of the current div
	//var WIDTH = map.getSize().x;
	//var HEIGHT = map.getSize().y;
	var WIDTH = 1754;
	var HEIGHT = 1240;
	//var bgcolor = "B5D0D0";
	
	var lat = map.getCenter().lat;
	var lon = map.getCenter().lng;
	var zoom = map.getZoom();
	
	
	
	// replacing the bbox and the other variables
	href = addParameter(href, 'bbox', BBOX);
	href = addParameter(href, 'width', WIDTH);
	href = addParameter(href, 'height', HEIGHT);
	href = addParameter(href, 'export', true);
	//href = addParameter(href, 'bgcolor', bgcolor);
	href = addParameter(href, 'lat', lat);
	href = addParameter(href, 'lon', lon);
	href = addParameter(href, 'zoom', zoom);


	// change the layer with their visibility
	//alert(href);
	window.open(href);
}


/**
* Add a URL parameter (or changing it if it already exists)
* @param {search} string  this is typically document.location.search
* @param {key}    string  the key to set
* @param {val}    string  value 
*/
function addParameter(search, key, val){
  var newParam = key + '=' + val,
      params = '?' + newParam;

  // If the "search" string exists, then build params from it
  if (search) {
    // Try to replace an existance instance
    params = search.replace(new RegExp('[\?&]' + key + '[^&]*'), '&' + newParam);

    // If nothing was replaced, then add the new param to the end
    if (params === search) {
      params += '&' + newParam;
    }
  }

  return params;
};

