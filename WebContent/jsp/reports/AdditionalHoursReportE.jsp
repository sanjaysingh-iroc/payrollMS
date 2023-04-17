<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

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

<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>


<script type="text/javascript">
function generateReportPdf(){
    alert("pdf generation");

 }

 function generateReportExcel(){
 	 alert("Excel  generation");
 	
 	
 }
var chartRoster;
var chartActual;
var chartPie; 

$(document).ready(function() {
	
	
	chartActual = new Highcharts.Chart({ 
   		 
      chart: {
         renderTo: 'container_Actual',
        	defaultSeriesType: 'column'
      },
      title: {
         text: 'Additional Hours'
      },
      xAxis: {
         categories: [<%=(String) request.getAttribute("sbActualPC")%>],
         labels: {
             rotation: -45,
             align: 'right',
             style: {
                 font: 'normal 10px Verdana, sans-serif'
             }
          },
         title: {
	            text: 'Date'
	         }
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Hours'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [<%=request.getAttribute("sbActualHours")%>]
   });
});
	
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	List _allDates = (List) request.getAttribute("_allDates");
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	List alId = (List) request.getAttribute("alId");

	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmAdditionalHours = (Map) request.getAttribute("hmAdditionalHours");
	Map hmTotal = (Map) request.getAttribute("hmTotal");

	String strFrom = (String) request.getAttribute("FROM");
	String strTo = (String) request.getAttribute("TO");

	if (_allDates == null) {
		_allDates = new ArrayList();
	}
	if (_alHolidays == null) {
		_alHolidays = new ArrayList();
	}

	if (_hmHolidaysColour == null) {
		_hmHolidaysColour = new HashMap();
	}
	String strP = (String) request.getAttribute("strP");
	String strSubTitle = "";
	String strColumnTitle = null;
	if (strP != null && strP.equalsIgnoreCase("AHE")) {
		strSubTitle = "By Employee (per employee)- by and between [" + strFrom + " to " + strTo + "]";
		strColumnTitle = "Employees";
	} else if (strP != null && strP.equalsIgnoreCase("AHWL")) {
		strSubTitle = "By Worklocation (per employee)- by and between [" + strFrom + " to " + strTo + "]";
		strColumnTitle = "Locations";
	} else if (strP != null && strP.equalsIgnoreCase("AHS")) {
		strSubTitle = "By service - by and between [" + strFrom + " to " + strTo + "]";
		strColumnTitle = "Services";
	} else if (strP != null && strP.equalsIgnoreCase("AHD")) {
		strSubTitle = "By Department - by and between [" + strFrom + " to " + strTo + "]";
		strColumnTitle = "Departments";
	} else if (strP != null && strP.equalsIgnoreCase("AHUT")) {
		strSubTitle = "By Usertype - by and between [" + strFrom + " to " + strTo + "]";
		strColumnTitle = "UserTypes";
	}
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Additional Hours Worked" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">
	<s:form name="frmAdditionalHours" action="AdditionalEmpHours"
		theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			<div
				style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
					cssStyle="float:left;margin-right: 10px;" listValue="orgName"
					headerKey="" headerValue="All Organisations"
					onchange="document.frmLiveEmployee.submit();"
					list="organisationList" key="" />
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
					cssStyle="float:left;margin-right: 10px;width:100px;"
					listValue="levelCodeName" multiple="true" list="levelList" key="" />
			</div>


			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<s:submit value="Submit" cssClass="input_button"
					cssStyle="margin:0px" />
			</div>

			<div style="padding-top: 10px; float: left;">
				<s:radio name="param"
					list="#{'AHWL':'By Location','AHS':'By Service','AHD':'By Department','AHUT':'By UserType','AHE':'By Employee'}" />
			</div>

			<%-- <div style="padding-top:10px;float: right;">
	    <s:radio name="duration" list="#{'1M':'1 Month','3M':'3 Months','6M':'6 Months','1Y':'1 Year','5Y':'5 Years','UTD':'Upto date'}" />
    </div> --%>

			<!-- <a onclick="generateReportPdf();" href="javascript:void(0)"
				style="background-image: url('images1/file-pdf.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
			 -->

<a onclick="generateReportPdf();" href="javascript:void(0)"><i class="fa fa-file-pdf-o" aria-hidden="true" style="float:right;"></i></a>







			<!-- <a onclick="generateReportExcel();" href="javascript:void(0)"
				style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				

		</div>
	</s:form>


	<span><%=strSubTitle%></span>

<s:form cssStyle="margin-left:735px; margin-bottom:10px" theme="simple" method="post" name="frm_roster_actual">
<s:hidden name="strP"></s:hidden>
<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" cssStyle="float:right"
		listValue="paycycleName" headerKey="0" headerValue="Select Paycycle"
		onchange="document.frm_roster_actual.submit();"
		list="paycycleList" key="" />
</s:form>


<div class="scroll" style="width:100%;float:left">

<display:table name="alReport" cellspacing="1" class="itis" export="true" 
	pagesize="50" id="lt1" requestURI="AdditionalEmpHours.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ResourceEfforts.xls" />
	<display:setProperty name="export.xml.filename" value="ResourceEfforts.xml" />
	<display:setProperty name="export.csv.filename" value="ResourceEfforts.csv" />
	
	<display:column style="text-align:left" nowrap="nowrap" title="<%=strColumnTitle%>" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
		<%
			for (int ii = _allDates.size() - 1; ii >= 0; ii--) {
					int count = 1 + ii;
					String strDate = uF.getDateFormat((String) _allDates.get(ii), CF.getStrReportDateFormat(), "dd-MMM");
		%>
				<display:column  style="width:120px;text-align:center" nowrap="nowrap" title="<%=strDate %>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
				<%
					}
				%>

	<display:footer>
			<tr>
			<th colspan="1">Total</th>
			<%
				for (int ii = _allDates.size() - 1; ii >= 0; ii--) {
							int count = ii - 1;
			%>
				<th colspan="1"><%=hmTotal.get(ii + "")%></th>
				<%
					}
				%>
			</tr>
	</display:footer>
	
</display:table>



</div>

<%
	if (strP != null && !strP.equalsIgnoreCase("AHE")) {
%>
<div class="chartholder">
	<!-- <div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div> -->
	<div id="container_Actual" style="height: 300px; width:95%; float:left; margin-top:20px"></div>
</div>
<%
	}
%>

</div>











<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div>