<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%UtilityFunctions uF = new UtilityFunctions(); %>	   
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Import Attendance" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">

<%--
<s:form action="ImportAttendance" method="POST" theme="simple" enctype="multipart/form-data">

 <table style="padding:50px">
	<tr>
		<td class="txtlabel alignRight">Select File to Import</td>
		<td><s:file name="fileUpload" label="Select a File to upload" size="20" /></td>
	</tr> 
	 
	<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %>
	<tr>
		<td colspan="2" align="center"><s:submit value="Upload" align="center" cssClass="input_button" /></td>
	</tr>
	<%} %>
	<tr>
		<td colspan="2" align="right"><a href="import/Timesheet1.xlsx" title="Download Sample File">Download Sample File</a></td>
	</tr>
</table> --%>






<div style="width: 45%; float: left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees Salary (Format 1)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportEmployeeSalary" onsubmit="return true;" name="ImportEmployeeSalary" id="ImportEmployeeSalary" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/importSalary.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div>
	






<%--
<div style="width: 45%; float: left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees attendance (Format 2 Solar)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance1" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload1" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/Timesheet2.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div>
	
 <div style="width: 45%; float: left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees attendance (Format 3)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload1" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/Timesheet1.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div> 


 <div style="width: 45%; float: right; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees attendance (Format 4)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload2" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/attendanceReportLifynShift.csv" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div> 


 <div style="width: 45%; float: left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees attendance (Format 5)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload3" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/DailyAttendanceReport.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div> 




<div style="width: 45%; float: right; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees attendance (Format 6 (Deogiri))</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportAttendance" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
		<table style="width: 100%;">
			<tbody><tr>
				<td class="txtlabel alignRight">Select File to Import</td>
				<td><s:file name="fileUpload4" label="Select a File to upload" size="20" /></td>
			</tr>  
			<tr>
				<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportEmployees_0">
</td>
			</tr>
			<tr>
				<td align="right" colspan="2"><a title="Download Sample File" href="import/attendanceReportLifynShift.csv" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>

	</div>
	</div> 
	
	
	 <div
		style="width: 45%; float: Left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
		<p class="past">Quick bulk upload your employees attendance
			(Format 7 Lift and Shift)</p>
		<div style="text-align: center; padding: 10px;">

			<s:form enctype="multipart/form-data" method="POST"
				action="ImportAttendance1" onsubmit="return true;"
				name="ImportEmployees" id="ImportEmployees" theme="simple">
				<table style="width: 100%;">
					<tbody>
						<tr>
							<td class="txtlabel alignRight">Select File to Import</td>
							<td><s:file name="fileUpload2"
									label="Select a File to upload" size="20" />
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2"><input type="submit"
								class="input_button" value="Import File" id="ImportEmployees_0">
							</td>
						</tr>
						<tr>
							<td align="right" colspan="2"><a
								title="Download Sample File" href="import/Timesheet2.xlsx"
								target="_blank">Download Sample File</a>
							</td>
						</tr>
					</tbody>
				</table>
			</s:form>
		</div>
	</div> --%>


	<%
java.util.List couterlist = (java.util.List)request.getAttribute("alReport");
if(couterlist!=null){
%>




<table style='width:100%;'>
	<tbody style='background-color:#EFEFEF;'>
				     
	   <% for (int i=0; i<couterlist.size(); i++) {%>
	    <tr>
	 	<td align="left"><%= couterlist.get(i) %>  Attendance Inserted Successfully !!</td>
	    </tr>
	   
	   <%} %>
	</tbody>
    </table>
   <%} %>

 </div>