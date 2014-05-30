<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

	<head>
	    <jsp:include page="include/httpbase.jsp"/>
	    <title>FENIX Maps Service</title>
	    <link href="axis2-web/css/axis-style.css" rel="stylesheet" type="text/css"/>
	    <link rel='icon' type='image/png' href='http://ldvapp07.fao.org:8030/downloads/fao.png'>
  	</head>

  	<body style="background-color: #365736;">
  	
  	<div style="height: 50px;" ></div>
  	
  	<table align="center" style="width: 800px; align: center; border: 3px solid #1D4589; background-color: #FFFFFF;">
  		
  		<tr>
  			<td align="left" width="80%" style="font-family: sans-serif; font-weight: bold; color: #365736; font-size: 35pt;">FENIX Maps Service</td>
  			<td width="200px" align="right"><img src="axis2-web/FAOLogo.png" width="50" height="50" alt="FAO"/></td>
  			
  			<td width="200px" align="right"><img src="axis2-web/Axis2Logo.jpg" width="87" height="50" alt="Apache Axis2"/></td>
  		</tr>
  		
  		<tr>
  			<td colspan="3" style="font-family: sans-serif; text-align: justify;">
  				Welcome! If you're reading these words your installation of maps-web was successful and you can now check 
  				the <a href="services/listServices">list of available web-services</a>. 
  				Please take a look at our <a href='axis2-web/TryIt.html'>Maps Gallery</a> where 
  				it is possible to find out more about the capabilities of this service and easily try through the editor.
  			</td>
  		</tr>
  		
  		<tr>
  			<td colspan="3" align="center">
  				<a target="_blank" href="http://www.foodsec.org/workstation/">FENIX Portal</a> |
  				<a target="_blank" href="http://axis.apache.org/axis2/java/core/">Apache Axis2</a>  
  			</td>
  		</tr>
  		
  	</table>
    
  	</body>
  	
</html>