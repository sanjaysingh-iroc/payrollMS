<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
UtilityFunctions uF = new UtilityFunctions();




%>


<!-- Custom form for adding new records -->
   


<div id="printDiv" class="leftbox reportWidth">


			
		<div  style="width:100%;">
	<%
	Map<String,String> empDetailsList =(Map<String,String>)request.getAttribute("empDetailsList");
	System.out.println("empDetailsList===>"+empDetailsList);
	%>
		<table  class="tb_style"
			width="100%">


			<tr>
				<th>EMP CODE</th>
				<td><%=uF.showData(empDetailsList.get("EMPCODE"),"-")%></td>
				<td rowspan="4" colspan="2"><img height="100" width="100" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/<%=empDetailsList.get("IMAGE")%>" /></td>
			</tr>
			<tr>
				<th>Company CODE</th>
				<td><%=uF.showData(empDetailsList.get("COMANYCODE"),"-")%></td>
			</tr>
			<tr>
				<th>Name</th>
				<td><%=uF.showData(empDetailsList.get("EMP_NAME"),"-")%></td>
			</tr>
			<tr>
				<th>Address</th>
				<td><%=uF.showData(empDetailsList.get("EMP_ADDRESS"),"-")%></td>
			</tr>
			<tr>
				<th>Designation</th>
				<td><%=uF.showData(empDetailsList.get("DESIG"),"-")%></td>
				<th>Grade</th>
				<td><%=uF.showData(empDetailsList.get("GRADE"),"-")%></td>
			</tr>
			<tr>
				<th>Department</th>
				<td><%=uF.showData(empDetailsList.get("DEPT"),"-")%></td>
				<th>City</th>
				<td><%=uF.showData(empDetailsList.get("CITY"),"-")%></td>
			</tr>
			<tr>
				<th>Email</th>
				<td><%=uF.showData(empDetailsList.get("EMAIL"),"-")%></td>
				<th>Contact</th>
				<td><%=uF.showData(empDetailsList.get("CONTACT"),"-")%></td>

			</tr>
			



		</table>
	</div>
    </div>
 
