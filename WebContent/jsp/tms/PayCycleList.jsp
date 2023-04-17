<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<% 
String strUserType= (String)session.getAttribute(IConstants.USERTYPE);
List reportList = (List)request.getAttribute("reportList");
if(reportList==null){
	reportList = new ArrayList();
}
String strType= (String)request.getParameter("T");
String strTypeText= "";
String strText= "";

if(strType!=null && strType.equalsIgnoreCase("T")){
	
	if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER))){
		strTypeText = "Timesheet";
	}else{
		strTypeText = "My Attendence";
		strText="Please select any of the Pay Cycles to view your timesheet";
	}
}else if(strType!=null && strType.equalsIgnoreCase("O")){
	strTypeText = "Exceptions";
}else if(strType!=null && strType.equalsIgnoreCase("C")){
	
	if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER))){
		strTypeText = "Clock Entries";
		strText="Please select any of the Pay Cycles to view Employee list";
	}else{
		strTypeText = "My Clock Entries";
		strText="Please select any of the Pay Cycles to view your clock entries";
	}
}else if(strType!=null && strType.equalsIgnoreCase("RRA")){
	strTypeText = "Roster Hours vs Actual Hours Report";
	strText="Please select any of the Pay Cycles to view Employee list";
}else if(strType!=null && strType.equalsIgnoreCase("A")){
	strTypeText = "Approve Payroll";
	strText="Please select any of the current pay cycle to approve the payroll";
}else if(strType!=null && strType.equalsIgnoreCase("A")){
	strTypeText = "Exceptions";
	strText="Please select any of the current pay cycle to view employee exceptions";
}else if(strType!=null && strType.equalsIgnoreCase("EA")){
	strTypeText = "My Attandance";
	strText="Please select among any of the Pay Cycles to visit the their corresponding Timesheet";
}

%>



                        
    <div class="pagetitle">
      <span><%= strTypeText %></span>
    </div>


    <div class="leftbox reportWidth">
    <h5><%= strText %></h5>
<table>

<%
//for(int i=0; i < reportList.size(); i++){
	for(int i= reportList.size() -1; i >0 ; i--){
	 %> 
	 
	 <tr>
	 <td class="reportLabel"><%= (String)reportList.get(i)%></td>
	 </tr>
	 <%
}
%>
</table>


	</div>
