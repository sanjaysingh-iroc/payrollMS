<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>


<script type="text/javascript">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});

$(function() {
    $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
});

$(document).ready( function () {
	jQuery("#frm_TravelReport").validationEngine();
});

function submitForm(){
 	document.frm_TravelReport.submit();
}
</script>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_wLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
});    
</script>

<%
UtilityFunctions uF = new UtilityFunctions();
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String strTitle = (String)request.getAttribute(IConstants.TITLE);
%>
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">
	<s:form name="frm_TravelReport" id="frm_TravelReport" action="TravelReport" theme="simple">
		<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
			<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
				<%=(String)request.getAttribute("selectedFilter") %>
			</p>
			<div class="content1" style="height: 170px;">
				<div style="width: 100%; float: left;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Organisation</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
							cssStyle="float:left;margin-right: 10px;" listValue="orgName"
							onchange="submitForm();" list="organisationList" key="" />
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_wLocation" id="f_wLocation" listKey="wLocationId"
							listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
							listValue="deptName" multiple="true"></s:select>
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Service</p>
						<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"
							listValue="serviceName" multiple="true"></s:select>
					</div>
				</div>
				<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-calendar"></i>
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:75px" readonly="true"></s:textfield>
	      				<s:textfield name="strEndDate"  id="strEndDate" cssStyle="width:75px" readonly="true"></s:textfield>
			      	</div>
		
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />      
					</div>
				</div>
			</div>
		</div>
	</s:form>
	
	<div style="float:left; width: 100%;">
		<display:table name="reportList" cellspacing="1" class="tb_style" export="true" pagesize="50" id="lt" requestURI="TravelReport.action" width="100%">
			<display:setProperty name="export.excel.filename" value="TravelReport.xls" />
			<display:setProperty name="export.xml.filename" value="TravelReport.xml" />
			<display:setProperty name="export.csv.filename" value="TravelReport.csv" />
			<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
			<display:column style="text-align:right;" valign="top" title="From Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>	
			<display:column style="text-align:right;" valign="top" title="To Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
			<display:column style="text-align:right;" valign="top" title="No Of Days"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Applied Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Employee Reason"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Is Concierge"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Travel Mode"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Is Booking"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Booking Info"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Is Accommodation"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Accommodation Info"><%=((java.util.List) pageContext.getAttribute("lt")).get(13)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Attachment"><%=((java.util.List) pageContext.getAttribute("lt")).get(14)%></display:column>	

		</display:table>
	</div>
</div>
