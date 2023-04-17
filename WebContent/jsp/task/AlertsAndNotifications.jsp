<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF =  new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String type = (String) request.getAttribute("operation");
	String sbAllNoti = "";
	String notiCnt ="";
	
	if(type != null && type.equals("NA")){
		sbAllNoti = (String) request.getAttribute("newsAndalerts");
		notiCnt = (String) request.getAttribute("newsCount");
	}else{
		sbAllNoti = (String) request.getAttribute("sbAllNoti");
		notiCnt = (String) request.getAttribute("notiCnt");
	}
	
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">
	<div>
		<%-- <% if (uF.parseToInt(notiCnt) > 0) { %>
			<div style="float: left; width: 100%; font-weight: bold; border-bottom: 2px solid lightgray;">
				<span style="float: left; margin-left: 5px;">Notifications</span>
				<a href="#" style="font-weight: normal; float: right; margin: 0px 20px; width: auto; height: 0px;">Mark Read All </a>
			</div>
		<% } %> --%>
		<div id="idTask" style="float: left; width: 100%;"> <!-- max-height: 250px; overflow-y: auto; -->
		<% if (uF.parseToInt(notiCnt) > 0) { %>
			<%=sbAllNoti %>
		<% } else { %>
			<div class="nodata msg" style="width: 96%">
				<span>No unread notifications available.</span>
			</div>
			<!-- <div style="float: left; color: gray; margin-left: 10px;">No unread notifications available.</div> -->
		<% } %>
		</div>
	</div>
    
</div>

<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});  
</script>