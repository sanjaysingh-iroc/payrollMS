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
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	String planId = (String) request.getParameter("planId");
	Map<String,String> hmCheckEmpList=(Map<String,String>)request.getAttribute("hmCheckEmpList");
	CommonFunctions CF=(CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	if(hmCheckEmpList==null)hmCheckEmpList=new HashMap<String,String>();
	
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
%>
<table id="lt1" class="tb_style" width="100%">
	<%
		if (empList != null && !empList.equals("") && !empList.isEmpty()) {
	%>
	<thead>
	<tr>
		<th width="10%"><input onclick="checkUncheckValue();" type="checkbox" name="allEmp" id="allEmp"></th>
		<th align="center">Employee</th>
		<th align="center">Designation</th>
		<th align="center">Location</th>
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
	%>
	<tr><td align="center"><input type="checkbox" name="strHiringEmpId" id="strHiringEmpId<%=i%>" onclick="getHiringSelectedEmp(this.checked,this.value,'frmKRA');"
			value="<%=empID%>" <%if(hmCheckEmpList.get(empID)!=null) { %>checked="checked"<% } %>></td>
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