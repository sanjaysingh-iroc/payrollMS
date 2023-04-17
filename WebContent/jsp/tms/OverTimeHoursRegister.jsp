<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script>
function generateTimesheetExcel(){
		
		var month=document.frm_Attendance.strMonth.value;
		var year=document.frm_Attendance.strYear.value;
		var wLocation=document.frm_Attendance.strWLocation.value;
		var f_department=document.frm_Attendance.department.value;
		var f_service=document.frm_Attendance.service.value;
		var url='TimeSheetHoursExcel.action?year='+year+'&month='+month+'&wLocation='+wLocation+'&f_department='+f_department+'&f_service='+f_service;
		
		window.location = url;
		
	}
function generateReportPdf(){
	window.location = "ExportPdfReport.action";
}function generateReportExcel(){
		window.location = "ExportExcelReport.action";
}
</script>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
String  strYear = (String)request.getAttribute("strYear");
String  strMonth = (String)request.getAttribute("strMonth");
 

List alDates = (List)request.getAttribute("alDates"); 
List alEmployees = (List)request.getAttribute("alEmployees");
Map hmEmpAttendance = (Map)request.getAttribute("hmEmpAttendance");
Map hmEmpServiceWorkedFor = (Map)request.getAttribute("hmEmpServiceWorkedFor");
Map hmEmpWlocation = (Map)request.getAttribute("hmEmpWlocation");
Map hmWeekEnds = (Map)request.getAttribute("hmWeekEnds");
Map hmEmpName = (Map)request.getAttribute("hmEmpName");
Map hmServiceMap = (Map)request.getAttribute("hmServiceMap");
if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
	strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
}


List alLegends = (List)request.getAttribute("alLegends");
//out.println("<br/>"+hmEmpAttendance);
//out.println("<br/>"+hmEmpServiceWorkedFor);


%>





<!-- Custom form for adding new records -->

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Overtime Hours Register" name="title"/>
</jsp:include>
   


    <div class="leftbox reportWidth">
    
    
    
    <s:form name="frm_Attendance" action="OverTimeHoursRegister" theme="simple">
    
	<div class="filter_div">
    <div class="filter_caption">Filter</div>			
			<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         onchange="document.frm_Attendance.submit();"  	
                         list="organisationList" key=""  />
                         
			<s:select theme="simple" name="strWLocation" listKey="wLocationId"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frm_Attendance.submit();" 		
                         list="wLocationList" key=""  />
                    
             <s:select name="department" list="departmentList" listKey="deptId" 
             			listValue="deptName" headerKey="0" headerValue="All Departments" 
             			onchange="document.frm_Attendance.submit()"></s:select>
             			
			<s:select name="service" list="serviceList" listKey="serviceId" 
						listValue="serviceName" headerKey="0" headerValue="All Services"
						 onchange="document.frm_Attendance.submit()"></s:select>     
                      
            <s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:101px;"
                         listValue="monthName" headerKey="0"
                         onchange="document.frm_Attendance.submit();" 		
                         list="monthList" key=""  />
                            	
			<s:select theme="simple" name="strYear" listKey="yearsID" cssStyle="width:65px;"
                         listValue="yearsName" headerKey="0"
                         onchange="document.frm_Attendance.submit();" 		
                         list="yearList" key=""  />
				
				
		<%-- 		
			Select Paycycle &nbsp;	
			<s:select theme="simple" label="Select Pay Cycle" name="paycycle" listKey="paycycleId"
                         listValue="paycycleName" headerKey="0"
                         onchange="document.frm_Attendance.submit();" 		
                         list="payCycleList" key=""  /> --%>
	                      
			<!--   <a onclick="generateTimesheetExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>   -->             
			<!-- <a onclick="generateReportPdf();" href="javascript:void(0)" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->               
	      
		<a onclick="generateReportPdf();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
 		<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>               
	</div>	                      
	                            
	</s:form>
	
		
<display:table name="reportList" cellspacing="1" class="tb_style" export="false"
	pagesize="50" id="lt" requestURI="OverTimeHoursRegister.action" width="100%">
	
	<display:column style="align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<%-- <display:column style="align:left" nowrap="nowrap" title="SBU" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column> --%>	
		
		<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 1+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<display:column media="html" title="<%= strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<%
			}  
			%>
</display:table>
	
	
<display:table name="reportListPrint" cellspacing="1" class="itis" export="true" style="display:none"
	pagesize="0" id="lt1" requestURI="OverTimeHoursRegister.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="OverTimeHoursRegister.xls" />
	<display:setProperty name="export.xml.filename" value="OverTimeHoursRegister.xml" />
	<display:setProperty name="export.csv.filename" value="OverTimeHoursRegister.csv" />
	
	<display:column style="align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	<%-- <display:column style="align:left" nowrap="nowrap" title="Service" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column> --%>	
		
		<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 1+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<display:column title="<%=strDate %>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
				<%
			}  
			%>

</display:table>
	
	<div style="float:left;width:100%">
	<%for(int i=0; i<alLegends.size(); i++){ %>
	<div style="margin:0px 0px 5px 0px">
	<%=alLegends.get(i) %>
	</div>
	<%} %>
	</div>
	
	
    </div>
   