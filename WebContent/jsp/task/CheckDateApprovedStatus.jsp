
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% 	boolean flag = (Boolean)request.getAttribute("flag");
	String strDate = (String)request.getAttribute("strDate");
	//System.out.println("flag ===>> " + flag);
%>

<%if(flag) { %>
	<%-- <span style="color: red;">Timesheet for this date is already approved.</span><br/> --%>
	<input type="hidden" name="hideDateApprovedStatus" id="hideDateApprovedStatus" value="1">
	<!-- <input type="text" name="strt_date" id="idStartDate" class="validate[required]" style="width:93px"/> -->
<% } else { %>
	<input type="hidden" name="hideDateApprovedStatus" id="hideDateApprovedStatus" value="0">
	<%-- <input type="text" name="strt_date" id="idStartDate" class="validate[required]" style="width:93px" value="<%=strDate %>"/> --%>
<% } %>