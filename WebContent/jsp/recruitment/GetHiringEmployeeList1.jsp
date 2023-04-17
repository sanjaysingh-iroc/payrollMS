<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List<FillEmployee> empList1 = (List<FillEmployee>) request.getAttribute("empList1");
	Map<String,String> hmCheckEmpList1 = (Map<String,String>)request.getAttribute("hmCheckEmpList1");
	CommonFunctions CF=(CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
		
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
%>
<table id="lt1" class="table table-bordered">
	<%
		if (empList1 != null && !empList1.equals("") && !empList1.isEmpty()) {
	%>
	<thead>
	<tr>
		<th><input onclick="checkUncheckValue1();" type="checkbox" name="allEmp1" id="allEmp1"></th>
		<th align="center">Employee</th>
		<th align="center">Designation</th>
		<th align="center">Location</th>
	</tr>
</thead>
<tbody>
	<%
		for (int i = 0; i < empList1.size(); i++) {

				String empID = ((FillEmployee) empList1.get(i)).getEmployeeId();
				String empName = ((FillEmployee) empList1.get(i)).getEmployeeCode();
				String emplocationID=(empID==null || empID.equals("")) ? "" : hmEmpLocation.get(empID);
				String location=(emplocationID==null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID),"");
				
				String desig=(empID==null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID),"");
	%>
	<tr><td align="center"><input type="checkbox" name="strHiringEmpId1" id="strHiringEmpId1<%=i%>" onclick="getHiringSelectedEmp1(this.checked,this.value,'frmKRA');"
			value="<%=empID%>" <%if(hmCheckEmpList1 != null && hmCheckEmpList1.get(empID)!=null) { %>checked="checked"<% } %>></td>
		<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
		<td><%=desig%></td>
		<td><%=location%></td>

	</tr>
	<%
		}
		} else {
	%>
	<tr>
		<td colspan="3"><div class="nodata msg" style="width: 88%">
				<span>No Employee Found</span>
			</div>
		</td>
	</tr>
	<%
		}
	%>
	</tbody>
</table>

<div id="PanelEmpProfilePopup"></div>