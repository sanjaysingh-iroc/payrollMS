<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
	String strUserType = (String) session
			.getAttribute(IConstants.USERTYPE);
%>



<script type="text/javascript" charset="utf-8">
					<%-- $(document).ready(function () {
						
							$('#lt').dataTable({ bJQueryUI: true, 
								  								
								"sPaginationType": "full_numbers",
								"aaSorting": [],
								"sDom": '<"H"lf>rt<"F"ip>',
								oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
												aButtons : [
														"csv",
														"xls",
														{
															sExtends : "pdf",
															sPdfOrientation : "landscape"
														//sPdfMessage: "Your custom message would go here."
														}, "print" ]
											}
										});
					}); --%>
					
					$(document).ready(function() {
						$('#lt').dataTable({
							bJQueryUI : true,
							"sPaginationType" : "full_numbers",
							"aaSorting" : []
						})
					});
</script>
<style>
table.display1 {
    border: 1px solid #CCCCCC;
    border-collapse: collapse;
    clear: both;
    margin: 0 auto;
   
}
table.display1 tfoot th {
    font-weight: normal;
    padding: 3px 0 3px 10px;
}
table.display1 tr.heading2 td {
    border-bottom: 1px solid #AAAAAA;
}
table.display1 td {
    font-size: 11px;
    padding: 3px 10px;
}
table.display1 td.center {
    text-align: center;
}


</style>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TAttendanceMuster %>" name="title" />
</jsp:include>
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

List<List<String>> reportList =(List<List<String>>)request.getAttribute("reportList");


List alLegends = (List)request.getAttribute("alLegends");
//out.println("<br/>"+hmEmpAttendance);
//out.println("<br/>"+hmEmpServiceWorkedFor);


%>


<div id="printDiv" class="leftbox reportWidth">

    
    <s:form name="frm_Attendance" action="AttendanceMuster" theme="simple">
    
	<div class="filter_div">
    <div class="filter_caption">Filter</div>			
				
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
		                  
	     <!--   <a onclick="generateTimesheetExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>   -->             
	                      
	  <!--  <a style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;padding-right:20px;height:25px;" href="AttendanceMuster.action?pdfGeneration=true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
	  
	   <a href="AttendanceMuster.action?pdfGeneration=true"><i class="fa fa-file-pdf-o" aria-hidden="true" style="right;padding-right:20px;height:25px;"></i></a>
      
 

	    <%--  <a href="<%=request.getContextPath() %>/jsp/reports/factory/factorydocs/form22.pdf" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;padding-right:20px;height:25px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
	     --%>                  
	</div>	                      
	                            
	</s:form>
	
		
<%-- <display:table name="reportList" cellspacing="1" class="tb_style" export="false"
	pagesize="50" id="lt" requestURI="AttendanceMuster.action" width="100%">
	
	<display:column style="align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column style="align:left" nowrap="nowrap" title="Gender" sort="true"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>	
		
		<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 2+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<display:column media="html" title="<%= strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<%
			}  
			%>
</display:table> --%>

<div>
    	<table class="display1" id="lt" width="100%">
    	
				<thead>
					<tr> 
						<th>Employee Name</th>
						<th>Gender</th>
						<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 2+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<th><%=strDate%></th>
				<%}%>						
					</tr>		
					
					
				</thead>
				
				 <tbody>
				 <%
				 for(int i=0;reportList!=null && i<reportList.size();i++){
					 List<String> innerList=reportList.get(i);
					 String gender="";
					 if(innerList.get(1)!=null){
						 if(innerList.get(1).equals("M")){
							 gender="Male";
						 }else if(innerList.get(1).equals("F")){
							 gender="FeMale";
						 }
					 }
				 %>
				 <tr>
				 <td><%=innerList.get(0) %></td>
				 <td><%=gender %></td>
				 	<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 2+ii;
				%>
				<td><%=innerList.get(count)%></td>
				<%}%>	
				 </tr>
				<%} %>
				</tbody>
				
				
			</table> 
   
   </div>
		

<div style="float:left;width:100%">

<p>
<strong>Adult:</strong>Men(<b><%=(String)request.getAttribute("adultCountMale") %></b>) and Women(<b><%=(String)request.getAttribute("adultCountFeMale") %></b>)
</p>
<p>
<strong>Adolescent:</strong>Male(<b><%=(String)request.getAttribute("adolescentCountMale") %></b>) and Female(<b><%=(String)request.getAttribute("adolescentCountFeMale") %></b>)
</p>
<p>
<strong>Child:</strong>Boys(<b><%=(String)request.getAttribute("childCountMale") %></b>) and Girls(<b><%=(String)request.getAttribute("childCountFeMale") %></b>)
</p>
</div>

<%-- <div style="float:left;width:35%">
<table border="0" class="formcss" style="float:left">  
	
	<tr>
		<td height="10px" rowspan="2">a) Adult</td>
		<td>Men (<%=(String)request.getAttribute("adultCountMale") %>)</td>
		<td>Women(<%=(String)request.getAttribute("adultCountFeMale") %>)</td>
	</tr>
	<tr>
		<td height="10px" rowspan="2">b) Adolescent</td>
		<td>Male (<%=(String)request.getAttribute("adolescentCountMale") %>)</td>
		<td>Female (<%=(String)request.getAttribute("adolescentCountFeMale") %>)</td>
	</tr>
	<tr>
		<td height="10px" rowspan="2">Child</td>
		<td>Boys (<%=(String)request.getAttribute("childCountMale") %>)</td>
		<td>Girls (<%=(String)request.getAttribute("childCountFeMale") %>)</td>
	</tr>
		
	</table>
</div> --%>

	
	<div style="float:left;width:100%; margin-top:25px;"">
	<%for(int i=0; i<alLegends.size(); i++){ %>
	<div style="margin:0px 0px 5px 0px">
	<%=alLegends.get(i) %>
	</div>
	<%} %>
	</div>
	
	
    </div>
