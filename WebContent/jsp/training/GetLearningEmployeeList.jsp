<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	UtilityFunctions uF = new UtilityFunctions();
	Map<String,String> hmCheckEmpList=(Map<String,String>)request.getAttribute("hmCheckEmpList");
	
	Map<String,String> hmEmpLocation = (Map<String,String>)request.getAttribute("hmEmpLocation");
	Map<String, String> hmWLocation = (Map<String,String>)request.getAttribute("hmWLocation"); 
	Map<String, String> hmEmpCodeDesig = (Map<String,String>)request.getAttribute("hmEmpCodeDesig");
	String boolPublished = (String) request.getAttribute("boolPublished");
	//System.out.println("boolPublished -----> "+ boolPublished);
%>

<table class="tb_style" width="100%">
	<%
		if (empList != null && !empList.equals("") && !empList.isEmpty()) {
	%>
	<tr>
		<th width="10%"><input onclick="checkUncheckValue('<%=boolPublished %>');" type="checkbox" name="allEmp" id="allEmp"></th>
		<th align="center">Employee</th>
		<th align="center">Designation</th>
		<th align="center">Location</th>
		<!-- <th align="center">Factsheet</th> -->
	</tr>

	<%
	//System.out.println("hmCheckEmpList -----> " + hmCheckEmpList);
		for (int i = 0; i < empList.size(); i++) {

				String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
				String empName = ((FillEmployee) empList.get(i)).getEmployeeCode();

				String emplocationID=(empID==null || empID.equals("")) ? "" : uF.showData(hmEmpLocation.get(empID), "");
				String location=(emplocationID==null || emplocationID.equals("")) ? "" : uF.showData(hmWLocation.get(emplocationID), "");
				String desig=(empID==null || empID.equals("")) ? "" : uF.showData(hmEmpCodeDesig.get(empID), "");
	%>
	<tr>
		<td align="center">
		<input type="checkbox" name="strTrainerId" id="strTrainerId<%=empID %>" onclick="getSelectedLearner(this.checked,this.value,'<%=boolPublished %>');"
								value="<%=empID%>" <%if (hmCheckEmpList.get(empID) != null) {
									if (uF.parseToBoolean(boolPublished) == true) {
								%> disabled="disabled" <% } %>  checked="checked" <%}%>>
		<%-- <input onclick="getContent('idEmployeeInfo', 'GetSelectedEmployeeAjax.action?type=one&chboxStatus='+this.checked+'&selectedEmp='+this.value+'&planId')"
			type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>" value="<%=empID%>" <%if(hmCheckEmpList.get(empID)!=null){%>checked="checked"<%} %>> --%>
		</td>
		<td><a href="javascript: void(0);" onclick="openPanelEmpProfilePopup('<%=empID%>')"><%=empName%></a></td>
		<td><%=desig%></td>
		<td><%=location%></td>
		<%-- <td><a class="factsheet" href="MyProfile.action?empId=<%=empID%>"></a></td> --%>
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
</table>
