<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#lt1').dataTable();
	}); 
</script> 

<%-- <s:select name="cemp" list="empList" listKey="employeeId"
							listValue="employeeCode" headerKey="0" id="employee"
							headerValue="All Employee" multiple="true" size="4"></s:select> --%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	String planId = (String) request.getParameter("planId");
	Map<String,String> hmCheckEmpList=(Map<String,String>)request.getAttribute("hmCheckEmpList");
	CommonFunctions CF=(CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	if(hmCheckEmpList==null)hmCheckEmpList=new HashMap<String,String>();
	
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
	
	List<String> indiGoalEmpIds = (List<String>)request.getAttribute("indiGoalEmpIds");
	
%>
<%
		if (empList != null && !empList.equals("") && !empList.isEmpty()) {
	%>
<table id="lt1" class="table table-bordered">
	
	<thead>
	<tr>
		<th width="10%"><input onclick="checkUncheckValue('frmKRA');" type="checkbox" name="allEmp" id="allEmp"></th>
		<th align="center">Employee</th>
		<th align="center">Designation</th>
		<th align="center">Location</th>
		<!-- <th align="center">Factsheet</th> -->
	</tr>
</thead>
<tbody>
	<%
		for (int i = 0; i < empList.size(); i++) {

				String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
				String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();
				String emplocationID=(empID==null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
				String location=(emplocationID==null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID),"");
				
				String desig=(empID==null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID),"");
				
				String isInIndiGoal = "N";
				if(indiGoalEmpIds.contains(empID)) {
					isInIndiGoal = "Y";
				}
	%>
	<tr> <%-- onclick="getContent('idEmployeeInfo', 'GetSelectedEmployeeAjax.action?type=one&chboxStatus='+this.checked+'&selectedEmp='+this.value+'&planId=<%=planId%>')" --%>
		<td align="center" class="alignLeft"><input type="checkbox" name="strGoalEmpId" id="strGoalEmpId<%=empID%>" onclick="getGoalSelectedEmp(this.checked, this.value, 'frmKRA', '<%=isInIndiGoal %>');"
			value="<%=empID%>" <%if(hmCheckEmpList.get(empID)!=null){%>checked="checked"<%} %>></td>
		<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
		<td><%=desig%></td>
		<td><%=location%></td>
		<%-- <td><a class="factsheet" href="MyProfile.action?empId=<%=empID%>"></a> 
		</td>--%>

	</tr>
	<%
		}
	%>
	</tbody>
</table>
<%} else { %>
<div class="nodata msg" style="width: 88%">
	<span>No Employee Found</span>
</div>
<%}	%>
<div id="PanelEmpProfilePopup"></div>