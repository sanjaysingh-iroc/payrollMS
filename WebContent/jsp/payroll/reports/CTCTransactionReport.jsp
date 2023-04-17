<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<script type="text/javascript">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});


function submitForm(){
	document.frm_CTCTransactionReport.exportType.value='';
	document.frm_CTCTransactionReport.submit();
}
</script>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
});    
</script>

<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="CTC Variable Transaction Report" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">


<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 170px;">
		<s:form name="frm_CTCTransactionReport" action="CTCTransactionReport" theme="simple" method="post">
		<s:hidden name="exportType"></s:hidden>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-top: 10px;">
					<i class="fa fa-filter"></i>
				</div>
				
				<div
					style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Organization</p>
					<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
						cssStyle="float:left;margin-right: 10px;" listValue="orgName"
						onchange="submitForm();"
						list="orgList" key="" />
				</div>
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Location</p>
					<s:select theme="simple" name="f_strWLocation" id="f_strWLocation"
						listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
						listValue="wLocationName" multiple="true" list="wLocationList"
						key="" />
				</div>
	
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Department</p>
					<s:select name="f_department" id="f_department"
						list="departmentList" listKey="deptId"
						cssStyle="float:left;margin-right: 10px;" listValue="deptName"
						multiple="true"></s:select>
				</div>
	
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Service</p>
					<s:select name="f_service" id="f_service" list="serviceList"
						listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
						listValue="serviceName" multiple="true"></s:select>
				</div>
	
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Level</p>
					<s:select theme="simple" name="f_level" id="f_level"
						listKey="levelId"
						cssStyle="float:left;margin-right: 10px;width:200px;"
						listValue="levelCodeName" multiple="true" list="levelList" key="" />
				</div>
			</div>
			<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-calendar"></i>
					</div>
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Financial Year</p>
						<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId"
						listValue="financialYearName" headerKey="0" 
						onchange="submitForm();"
						list="financialYearList" key="" cssStyle="width:200px;"/>
					</div>
					<div id="monthDIV" style="float: left;margin-top: 10px; margin-left: 10px; width: 325px;">
						<p style="padding-left: 5px;">Month</p>
						<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId"
							listValue="monthName" headerKey="1" 
							onchange="submitForm();"
							list="monthList" key="" cssStyle="width:200px;"/>	
		      		</div>
					<div style="float: left; margin-top: 10px; margin-left: -75px; width: auto;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<%-- <s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" /> --%>
						<input type="button" name="Submit" value="Submit" class="input_button" style="margin:0px" onclick="submitForm();"/>
					</div>
			</div>
	</s:form>
	</div>
</div>
		
		<br/>
		
		<display:table name="reportList" cellspacing="1" class="tb_style" export="true" pagesize="200" id="lt" requestURI="CTCTransactionReport.action" width="100%">
			<display:setProperty name="export.excel.filename" value="CTCTransactionReport.xls" />
			<display:setProperty name="export.xml.filename" value="CTCTransactionReport.xml" />
			<display:setProperty name="export.csv.filename" value="CTCTransactionReport.csv" />
			
			<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Work Location"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="SBU"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Level"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="CTC component"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="CTC Variable Amount"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
		</display:table>
		
		
</div>
   

