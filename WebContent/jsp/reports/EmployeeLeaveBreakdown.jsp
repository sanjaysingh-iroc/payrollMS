<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});

function submitForm(){
	document.frm_EmpLeaveBreakdown.exportType.value='';
	document.frm_EmpLeaveBreakdown.submit();
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

    <% 
    Map<String, Map<String, Map<String, String>>> hmInnerTotal = (Map)request.getAttribute("hmInnerTotal");
    Map<String, String> hmEmployeeNameMap = (Map<String, String>)request.getAttribute("hmEmployeeNameMap");
    Map<String, String> hmEmpCode = (Map<String, String>)request.getAttribute("hmEmpCode");

    Map<String, String> hmLeaveMap = (Map<String, String>)request.getAttribute("hmLeaveMap");
    Map<String, String> hmBalance = (Map<String, String>)request.getAttribute("hmBalance");
    List<String> alLeaveType = (List<String>)request.getAttribute("alLeaveType");
    if(alLeaveType == null) alLeaveType = new ArrayList<String>();
    
    Set<String> alEmpId = (Set<String>)request.getAttribute("alEmpId");
    UtilityFunctions uF = new UtilityFunctions();
    
    List<String> alLeaveCompType = (List<String>)request.getAttribute("alLeaveCompType");
    if(alLeaveCompType == null) alLeaveCompType = new ArrayList<String>();
	
    Map<String, String> hmMainBalance = (Map<String, String>)request.getAttribute("hmMainBalance");
    if(hmMainBalance == null) hmMainBalance=new HashMap<String, String>();
    Map<String, String> hmAccruedBalance = (Map<String, String>)request.getAttribute("hmAccruedBalance");
    if(hmAccruedBalance == null) hmAccruedBalance=new HashMap<String, String>();
    Map<String, String> hmPaidBalance = (Map<String, String>)request.getAttribute("hmPaidBalance");
    if(hmPaidBalance == null) hmPaidBalance=new HashMap<String, String>();
    Map<String, String> hmLeaveStatus = (Map<String, String>)request.getAttribute("hmLeaveStatus");
    if(hmLeaveStatus == null) hmLeaveStatus=new HashMap<String, String>();
    Map<String, String> hmComOffBalance = (Map<String, String>)request.getAttribute("hmComOffBalance");
    if(hmComOffBalance == null) hmComOffBalance=new HashMap<String, String>();
    Map<String, String> hmApprovedBalance = (Map<String, String>)request.getAttribute("hmApprovedBalance");
    if(hmApprovedBalance == null) hmApprovedBalance=new HashMap<String, String>();
    
%>
    
     
    <style>
    .approvedColor{
	    background:green; 
	    color:white; 
    }
    .remainingColor{
	    background:blue;
	    color:white; 
    }
    .pendingColor{
	    background:orange;
	    color:white; 
    }
    .deniedColor{
	    background:red;
	    color:white; 
    }
    
    </style>
     
    
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employeewise Leave Breakdown" name="title"/>
</jsp:include>
    
<div id="printDiv" class="leftbox reportWidth">

<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px;">
		<%
		String dataType = (String) request.getAttribute("dataType");
		String strLabel = "";
		if(dataType == null || dataType.equals("L")) { 
			strLabel ="Leaves";
		%>
			<a href="EmployeeLeaveBreakdown.action?dataType=L" class="all">Leaves</a>
			<a href="EmployeeLeaveBreakdown.action?dataType=E" class="live_dull" style="width: 100px;">Extra Working</a>
		<% } else if(dataType != null && dataType.equals("E")) { 
			strLabel ="Extra Working";
		%>
			<a href="EmployeeLeaveBreakdown.action?dataType=L" class="all_dull">Leaves</a>
			<a href="EmployeeLeaveBreakdown.action?dataType=E" class="live" style="width: 100px;">Extra Working</a>
		<% } %>	
</div>

	<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323; margin-top: 34px;"">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 170px;">
		
		<s:form name="frm_EmpLeaveBreakdown" action="EmployeeLeaveBreakdown" theme="simple">
			<s:hidden name="exportType"></s:hidden>
			<s:hidden name="dataType"></s:hidden>
			<div style="float: left; width: 100%;">
				<div style="float: left; margin-top: 10px;">
					<i class="fa fa-filter"></i>
				</div>
				<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">Organization</p>
					<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
		                         onchange="submitForm();" list="orgList" key="" cssStyle="width:200px;"/>
	            </div>
	            <div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">Location</p>
					<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName"
								list="wLocationList" key=""  cssStyle="width:200px;" multiple="true"/>
				</div>
				<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">Department</p>
					<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" 
								list="departmentList" key=""  cssStyle="width:200px;" multiple="true"/>
				</div>		
				
				<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
					<p style="padding-left: 5px;">SBU</p>
					<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"  
		    			listValue="serviceName" cssStyle="width:200px;" key="" multiple="true"/>
				</div>			
				<div
					style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
					<p style="padding-left: 5px;">Level</p>
					<s:select theme="simple" name="f_level" id="f_level"listKey="levelId"
						cssStyle="float:left;margin-right: 10px;width:200px;" listValue="levelCodeName" multiple="true" list="levelList" key="" />
				</div>			
			</div>
			
			<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-calendar"></i>
					</div>
					
		      		<div id="paycycleDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Paycycle</p>
						<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" 
						list="paycycleList" key="" onchange="submitForm();"/>
		      		</div>
		      		<div id="submitDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="button" name="Submit" value="Submit" class="input_button" style="margin:0px" onclick="submitForm();"/>
		      		</div>
			</div>
		</s:form>
</div>	
</div>	
<%
	if(dataType == null || dataType.equals("L")) { 
%>
	<display:table name="reportList" cellspacing="1" class="tb_style" export="true" pagesize="50" id="lt1" requestURI="EmployeeLeaveBreakdown.action" width="100%">
			<display:setProperty name="export.excel.filename" value="EmployeeLeaveBreakdown.xls" />
			<display:setProperty name="export.xml.filename" value="EmployeeLeaveBreakdown.xml" />
			<display:setProperty name="export.csv.filename" value="EmployeeLeaveBreakdown.csv" />
			<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<%-- <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><div style="padding:0 5px;background-color: <%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></div></display:column> --%>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
			<display:column style="text-align:right;" valign="top" title="Remaining"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Approved"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Pending"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>	
			<display:column style="text-align:right;" valign="top" title="Denied"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
			
	</display:table>
<%} else if(dataType == null || dataType.equals("E")) {  %>
	<display:table name="reportList" cellspacing="1" class="tb_style" export="true" pagesize="50" id="lt1" requestURI="EmployeeLeaveBreakdown.action" width="100%">
			<display:setProperty name="export.excel.filename" value="EmployeeLeaveBreakdown.xls" />
			<display:setProperty name="export.xml.filename" value="EmployeeLeaveBreakdown.xml" />
			<display:setProperty name="export.csv.filename" value="EmployeeLeaveBreakdown.csv" />
			<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<%-- <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><div style="padding:0 5px;background-color: <%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></div></display:column> --%>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
			<display:column style="text-align:right;" valign="top" title="Approved"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
			<display:column style="text-align:right;" valign="top" title="Pending"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>	
			<display:column style="text-align:right;" valign="top" title="Denied"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
			
	</display:table>
<%} %>

<%-- 

<table cellpadding="3" cellspacing="1" align="right">
	<tr>
		<td width="70px" class="remainingColor alignCenter">Remaining</td>
		<td width="70px" class="approvedColor alignCenter">Approved</td>
		<td width="70px" class="pendingColor alignCenter">Pending</td>
		<td width="70px" class="deniedColor alignCenter">Denied</td>
	</tr>
</table>
<br/><br/>


<table cellpadding="3" cellspacing="1">

    	<tr>
    	    <td class="reportHeading alignCenter">Employee Code</td>
    		<td class="reportHeading alignCenter">Staff</td>
    		<%
	    		for(int i=0; i<alLeaveType.size(); i++){
	    			%>
	    			<td class="reportHeading alignCenter" colspan="4"><%=uF.showData( hmLeaveMap.get(alLeaveType.get(i)), "")%></td>	
	    			<%
	    		}
    		%>
    		<td class="alignCenter" colspan="2" style="width: 10%;">&nbsp;</td>
    		<%
	    		for(int i=0; i<alLeaveCompType.size(); i++){
	    			%>
	    			<td class="reportHeading alignCenter" colspan="3"><%=uF.showData( hmLeaveMap.get(alLeaveCompType.get(i)), "")%></td>	
	    			<%
	    		}
    		%>
    		   
    	</tr>
    
    
    
    <%
    Iterator<String> it = alEmpId.iterator();
    while(it.hasNext()){ 
    	String strEmpId = it.next();
    	//Map<String, Map<String, String>> emphmInnerTotal=hmInnerTotal.get(strEmpId);
    	//if(emphmInnerTotal==null)emphmInnerTotal=new HashMap<String,Map<String, String>>();
    	%>
    	<tr>
    	    	<td><%=hmEmpCode.get(strEmpId) %></td>
    	
    	<td><%=hmEmployeeNameMap.get(strEmpId) %></td>
    	<%
    		for(int i=0; i<alLeaveType.size(); i++){
    			String strLeaveTypeId = alLeaveType.get(i);
    			double dblBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+strLeaveTypeId));
				dblBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+strLeaveTypeId));
				
				double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strEmpId+"_"+strLeaveTypeId));
				
				if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            dblBalance = dblBalance - dblPaidBalance; 
		        }
    			
				double dblPending = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_0"));
				double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_-1"));
				
				double dblApprovedBalance = uF.parseToDouble(hmApprovedBalance.get(strEmpId+"_"+strLeaveTypeId));
    	  
    	%>
    			<td class="reportLabel alignRight remainingColor"><%=uF.showData(""+dblBalance, "0")%></td>
    			<td class="reportLabel alignRight pendingColor"><%=uF.showData(""+dblPending, "0")%></td>
    			<td class="reportLabel alignRight approvedColor"><%=uF.showData(""+dblApprovedBalance, "0")%></td>
    			<td class="reportLabel alignRight deniedColor"><%=uF.showData(""+dblDenied, "0")%></td>	
    			<%
    		}
    		
    		%>
    		<td colspan="2">&nbsp;</td>
			<%
    		for(int i=0; i<alLeaveCompType.size(); i++){
    			String strLeaveTypeId = alLeaveCompType.get(i);
    			double dblBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+strLeaveTypeId));
				dblBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+strLeaveTypeId));
				
				double dblPaidBalance = uF.parseToDouble(hmComOffBalance.get(strEmpId+"_"+strLeaveTypeId));
				
				if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            dblBalance = dblBalance - dblPaidBalance; 
		        }
    			
				double dblPending = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_0"));
				double dblDenied = uF.parseToDouble(hmLeaveStatus.get(strEmpId+"_"+strLeaveTypeId+"_-1"));
				
				double dblApprovedBalance = uF.parseToDouble(hmApprovedBalance.get(strEmpId+"_"+strLeaveTypeId));
    	  
    	%>
    			<td class="reportLabel alignRight pendingColor"><%=uF.showData(""+dblPending, "0")%></td>
    			<td class="reportLabel alignRight approvedColor"><%=uF.showData(""+dblApprovedBalance, "0")%></td>
    			<td class="reportLabel alignRight deniedColor"><%=uF.showData(""+dblDenied, "0")%></td>	
    			<%
    		}
    		
    		%>
    		
		</tr>	
		<%
    }
    %>
    
    </table> --%>
    
    <%-- <display:table name="reportListPrint" cellspacing="1" class="itis" export="true" style="display:none"
	pagesize="0" id="lt1" requestURI="EmployeeLeaveBreakdown.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="AttendanceRegister.xls" />
	<display:setProperty name="export.xml.filename" value="AttendanceRegister.xml" />
	<display:setProperty name="export.csv.filename" value="AttendanceRegister.csv" />
	
	<display:column style="align:left;" nowrap="nowrap" title="Employee code" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
		<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 7+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<display:column title="<%=strDate %>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
				<%
			}  
			%>
			
			<%
    		for(int i=0; i<alLeaveType.size(); i++){
    			String strLeaveTypeId = alLeaveType.get(i);
    			Map<String, String> empLeaveTypeMp=emphmInnerTotal.get(strLeaveTypeId);
    			if(empLeaveTypeMp==null)empLeaveTypeMp=new HashMap<String, String>();
    	  
    	%>
    			<td class="reportLabel alignRight remainingColor"><%=uF.showData(hmBalance.get(strEmpId+"_"+strLeaveTypeId), "0")%></td>
    			<td class="reportLabel alignRight pendingColor"><%=uF.showData(empLeaveTypeMp.get("WAITING"), "0")%></td>
    			<td class="reportLabel alignRight approvedColor"><%=uF.showData(empLeaveTypeMp.get("APPROVED"), "0")%></td>
    			<td class="reportLabel alignRight deniedColor"><%=uF.showData(empLeaveTypeMp.get("DENIED"), "0")%></td>	
    			<%
    		}
    		
    		%>

</display:table> --%>
    
    
</div>
