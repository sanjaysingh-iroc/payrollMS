<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!-- timeline -->
<link rel="stylesheet" href="scripts/timeline/css/reset.css"> <!-- CSS reset -->
<link rel="stylesheet" href="scripts/timeline/css/style.css"> <!-- Resource style -->
<script src="scripts/timeline/js/modernizr.js"></script> <!-- Modernizr -->
<!-- timeline end -->


<%
	List<String> visitList = (List<String>)request.getAttribute("alInner");
	if(visitList == null) visitList = new ArrayList<String>();
%>

<%if(visitList!=null && visitList.size()>0){ %>
<div id="printDiv" class="leftbox reportWidth">

	<div class="leftholder" style="width: 100%; ">
		<div style="float: left;  width: 100%;">
								
			</div>
			<table class="table table_no_border ">
				<tr> 
				   <td><span  style="font-size: 16px;width:100%;color:#a14f76;font-style: italic;">Visit Details</span></td>
				</tr>
				
				<tr> 
				   <td><span style = "font-size:14px;color:green;">Description : </span>
				   	<span style = "font-size:12px;color:gray;"><%= visitList.get(1)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">HR Name: </span>
				   	<span style = "font-size:12px;color:gray;"><%= visitList.get(5)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;">Client Name : </span>
				   	<span style = "font-size:12px;color:gray;"><%= visitList.get(4)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;"> Date : </span>
				   	<span style = "font-size:12px;color:gray;"><%= visitList.get(2)%></span>
				   </td>
				</tr>
				<tr> 
				   <td><span style = "font-size:12px;"> Time : </span>
				   	<span style = "font-size:12px;color:gray;"><%= visitList.get(3)%></span>
				   </td>
				</tr>
				
				
			
				
			</table>
			
   </div>
</div>	
<%} %>	
<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
}); 
</script>