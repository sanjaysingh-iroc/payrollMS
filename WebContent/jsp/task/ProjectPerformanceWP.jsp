<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script> --%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>
$(function() {
    $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    
    var value = document.getElementById("selectOne").value;
    checkSelectType(value);
});
    
    jQuery(document).ready(function() {

    	jQuery(".content1").hide();
    	//toggle the componenet with class msg_body
    	jQuery(".heading_dash").click(function() {
    		jQuery(this).next(".content1").slideToggle(500);
    		$(this).toggleClass("filter_close");
    	});
    });
    
    function checkSelectType(value) {
    	
    	//fromToDIV financialYearDIV monthDIV paycycleDIV
    	if(value == '1') {
    		document.getElementById("fromToDIV").style.display = 'block';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '2') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'block';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '3') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'block';
    		document.getElementById("paycycleDIV").style.display = 'none';
    	} else if(value == '4') {
    		document.getElementById("fromToDIV").style.display = 'none';
    		document.getElementById("financialYearDIV").style.display = 'none';
    		document.getElementById("monthDIV").style.display = 'none';
    		document.getElementById("paycycleDIV").style.display = 'block';
    	}
    }
</script>

<%
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strTitle = "Project Analytics (Realtime)";

	if(strUserType!=null && strUserType.equalsIgnoreCase(IConstants.MANAGER)) {
		strTitle = "My Project Analytics";
	}
%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_project_service").multiselect();
	$("#f_client").multiselect();
});    
</script>

	<%  
		String proType = (String)request.getAttribute("proType");  
	%>
	
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include>

 

<div class="leftbox reportWidth">
<%-- <%
String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
String strQuery = (String)request.getAttribute("javax.servlet.forward.query_string");

if(strAction!=null){
	strAction = strAction.replace(request.getContextPath()+"/","");
	if(strQuery!=null && strQuery.indexOf("NN")>=0){
		strAction = strAction+"?"+strQuery;
	}
}

%> --%>

	<div style="margin-bottom: 20px">
		<a class="<%=((proType == null || proType.equals("") || proType.equalsIgnoreCase("L")) ? "current" : "next") %>" href="ProjectPerformanceWP.action?proType=L">Working Projects</a> | 
		<a class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "current" : "next") %>" href="ProjectPerformanceWP.action?proType=C">Completed Projects</a>
	</div>
	
<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 170px;">
		
    <s:form name="frmProjectPerformanceWP" action="ProjectPerformanceWP" theme="simple">
    	<s:hidden name="proType" />
			<div style="float: left; width: 100%; margin-top: -5px;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Organisation</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
							onchange="document.frmProjectPerformanceWP.submit();" list="organisationList" />
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
							listValue="wLocationName" list="wLocationList" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" cssStyle="float:left; margin-right: 10px;" 
							listValue="deptName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">SBU</p>
						<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
							listValue="serviceName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;"
							listValue="levelCodeName" list="levelList" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 35px; width: 215px;">
						<p style="padding-left: 5px;">Service</p>
						<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
							listValue="serviceName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Client</p>
						<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" cssStyle="float:left;margin-right: 10px;" list="clientList" 
							key="" multiple="true" />
					</div>
			</div>
			
			<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-calendar"></i>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Select Period</p>
						<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
							headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/> <!--   -->
					</div>
					
					<div id="fromToDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:textfield name="strStartDate" id="strStartDate" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
			      		<s:textfield name="strEndDate"  id="strEndDate" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
		      		</div>
		      		
		      		<div id="financialYearDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Financial Year</p>
						<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
							headerValue="Select Financial Year" list="financialYearList" />
		      		</div>
		      		
		      		<div id="monthDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 325px;">
						<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
						<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
							headerValue="Select Financial Year" list="financialYearList" /> 
						<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px;" listKey="monthId" listValue="monthName" list="monthList" />	
		      		</div>
		      		
		      		<div id="paycycleDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Paycycle</p>
						<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName"
							headerValue="Select Paycycle" list="paycycleList" />
		      		</div>
		      		
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
					</div>
			</div>
		</s:form>
	
		</div>
	</div>
      

<display:table name="alOuter" cellspacing="1" class="tb_style" export="true"
	pagesize="50" id="lt1" requestURI="ProjectPerformanceWP.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ProjectPerformance.xls" />
	<display:setProperty name="export.xml.filename" value="ProjectPerformance.xml" />
	<display:setProperty name="export.csv.filename" value="ProjectPerformance.csv" />
	
	<display:column nowrap="nowrap" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column title="Budgeted Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
	<display:column nowrap="nowrap" title="Actual Cost" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	<display:column nowrap="nowrap" title="Billable Amount" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column nowrap="nowrap" title="Percentage" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%>%</display:column>
	<display:column nowrap="nowrap" title="Indicator" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
	
	
</display:table>


<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/denied.png" width="17">  --><i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/re_submit.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Budgeted and Actual &lt; Billable</div>
</div>


<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/approved.png" width="17">  --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &lt; Budgeted</div>
</div>

<div>
<jsp:include page="/jsp/task/ProjectPerformanceChart.jsp"></jsp:include>
</div>

</div>


