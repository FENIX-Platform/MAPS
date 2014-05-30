/*! Hover map methos */

// TODO: add the mapid (to append it to the bottom)
function changeDivHTML(content) {
	/** var previousInnerHTML = new String(""); 

	 previousInnerHTML = document.getElementById("test").innerHTML; 
	 previousInnerHTML =  previousInnerHTML.concat("<p align=\"center\">"  +
	                   content + "</p>"); 
	
	document.getElementById("test").innerHTML = previousInnerHTML; **/

	// Create a popup with a unique ID linked to this record
	var popup = $("<div></div>", {
		id : "popup",
		css : {
			position : "absolute",
			bottom : "15px",
			left : "10px",
			zIndex : 1002,
			backgroundColor : "black",
			padding : "5px",
			border : "1px solid #ccc"
		}
	});
	// Insert a headline into that popup
	var hed = $(
			"<div style='font-variant:small-caps; color:#fff; font-size: 13px;'>"
					+ content + "</div>").appendTo(popup);
	// Add the popup to the map
	popup.appendTo("#map").slideDown('slow');
}


function removeDivHTML(id) {
	$(id).remove();
}