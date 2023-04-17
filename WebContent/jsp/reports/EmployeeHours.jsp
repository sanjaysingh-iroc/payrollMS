<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

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



<%!String showData(String strData, String strVal) {
		if (strData == null)
			return strVal;
		else
			return strData;
	}%>  
 
<%
   	Map hmActual = (Map) request.getAttribute("hmActual");
   	Map hmRoster = (Map) request.getAttribute("hmRoster");
   	List alId = (List) request.getAttribute("alId");
   	List alPayCycles = (List) request.getAttribute("alPayCycles");
   	List alSubTitle = (List) request.getAttribute("alSubTitle");

   	Map hmTotal = (Map) request.getAttribute("hmTotal");
   %>

<script type="text/javascript">

var chartRoster;
var chartActual;

$(document).ready(function() {
	
	chartActual = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'container_Actual',
        	defaultSeriesType: 'column'
      },
      title: {
         text: 'Actual Hours'
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
	            text: 'Pay Cycles'
	         }
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Resource Efforts'
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
	
	
	chartRoster = new Highcharts.Chart({
   		
	      chart: {
	         renderTo: 'container_Roster',
	        	defaultSeriesType: 'column'
	      },
	      title: {
	         text: 'Roster Hours'
	      },
	      credits: {
	         	enabled: false
	      },
	      xAxis: {
	         categories: [<%=(String) request.getAttribute("sbActualPC")%>],
	         title: {
		            text: 'Pay Cycles'
		         }
	      },
	      yAxis: {
	         min: 0,
	         title: {
	            text: 'Resource Efforts'
	         }
	      },
	      plotOptions: {
	      	column: {
	            pointPadding: 0.2,
	            borderWidth: 0
	         }
	      },
	     series: [<%=request.getAttribute("sbRosterHours")%>]
	   });
	
	
});

function generateReportPdf(){
    alert("pdf generation");

 }

 function generateReportExcel(){
 	 alert("Excel  generation");
 	
 	
 }
</script>




<%
	String strSubTitle = null;
	String strColumnTitle = null;
	String strP = (String) request.getParameter("param");
	if (strP != null && strP.equalsIgnoreCase("WLH")) {
		strSubTitle = "By Location";
		strColumnTitle = "Location";
	} else if (strP != null && strP.equalsIgnoreCase("SH")) {
		strSubTitle = "By Service";
		strColumnTitle = "Service";
	} else if (strP != null && strP.equalsIgnoreCase("UTH")) {
		strSubTitle = "By Usertypes";
		strColumnTitle = "UserTypes";
	} else if (strP != null && strP.equalsIgnoreCase("DH")) {
		strSubTitle = "By Departments";
		strColumnTitle = "Departments";
	} else {
		strSubTitle = "By Employee";
		strColumnTitle = "Employees";
	}
%>


 <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Actual Effort" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">

	<s:form name="frmEmployeeHours" action="EmployeeHours" theme="simple">

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
					list="#{'WLH':'By Location','SH':'By Service','DH':'By Department','UTH':'By UserType','EH':'By Employee'}" />
			</div>

			<%-- <div style="padding-top:10px;float: right;">
	    <s:radio name="duration" list="#{'1M':'1 Month','3M':'3 Months','6M':'6 Months','1Y':'1 Year','5Y':'5 Years','UTD':'Upto date'}" />
    </div> --%>
			<!-- <a onclick="generateReportPdf();" href="javascript:void(0)"
				style="background-image: url('images1/file-pdf.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				
				
				<a onclick="generateReportPdf();" href="javascript:void(0)"><i class="fa fa-file-pdf-o" aria-hidden="true" style="float:right;"></i></a>
				
				
				
				

				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)"
					style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
					
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
					
		</div>
	</s:form>

	<span style="font-weight:bold">[<%=strSubTitle%>]</span>

<div class="scroll">

<!-- decorator="org.displaytag.decorator.TotalTableDecorator" -->
	
<display:table name="alReport" cellspacing="1" class="itis" export="true" 
	pagesize="50" id="lt1" requestURI="EmployeeHours.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ResourceEfforts.xls" />
	<display:setProperty name="export.xml.filename" value="ResourceEfforts.xml" />
	<display:setProperty name="export.csv.filename" value="ResourceEfforts.csv" />
	<display:setProperty name="export.pdf" value="true" />
	
	
	<display:column style="text-align:left" nowrap="nowrap" title="<%=strColumnTitle%>" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	
		<%
				for (int ii = alPayCycles.size() - 1; ii >= 0; ii--) {
						int count = 1 + (ii * 3);
			%>
				<display:column style="width:120px;text-align:center" nowrap="nowrap" title="<%=(String)alPayCycles.get(ii)%>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(0 + count)%></display:column>
<%-- 				<display:column style="width:120px;text-align:center" nowrap="nowrap" title="Roster" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(1+count)%></display:column>
				<display:column style="width:120px;text-align:center" nowrap="nowrap" title="Var" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(2+count)%></display:column> --%>
				<%
					}
				%>

	<display:footer>
			<tr>
			<th colspan="1">Total</th>
			<%
				for (int ii = alPayCycles.size() - 1; ii >= 0; ii--) {
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
		if (strP != null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH") || strP.equalsIgnoreCase("DH"))) {
	%>
	
		<div class="chartholder">
		<div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div> 
		<div id="container_Actual" style="height: 300px; width:45%; float:left; "></div>
		<div id="container_Roster" style="height: 300px; width:45%; float:left;  "></div>
		</div>
	<%
		}
	%>
	
	
		

</div>


<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div>