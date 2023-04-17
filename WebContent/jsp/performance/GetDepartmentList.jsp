
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@page import="java.util.List"%>
<table>
	<%
		List<FillDepartment> departmentList = (List<FillDepartment>) request.getAttribute("departList");
		for (int i = 0; departmentList != null && !departmentList.isEmpty() && i < departmentList.size(); i++) {
	%>
	<tr>
		<td class="textblue"><input type="checkbox" value="<%=(String) ((FillDepartment) departmentList.get(i)).getDeptId()%>" name="checkDepart" checked="checked" onclick="getParameterByDepartment();"/>
		</td>
		<td><%=(String) ((FillDepartment) departmentList.get(i)).getDeptName()%></td>
	</tr>
	<%
		}
	%>
</table>