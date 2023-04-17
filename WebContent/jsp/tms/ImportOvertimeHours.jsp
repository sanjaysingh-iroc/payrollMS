<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%UtilityFunctions uF = new UtilityFunctions(); %>	   
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Import Overtime Hours" name="title"/>
</jsp:include>

<div class="leftbox reportWidth">


<div style="width: 45%; float: left; border: 1px solid rgb(204, 204, 204); margin-top: 40px;">
	<p class="past">Quick bulk upload your employees overtime hours (Format 1 Solar)</p> 
	<div style="text-align: center; padding: 10px;">
					
<s:form enctype="multipart/form-data" method="POST" action="ImportOvertimeHours" onsubmit="return true;" name="ImportEmployees" id="ImportEmployees" theme="simple">
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
				<td align="right" colspan="2"><a title="Download Sample File" href="import/ImportOvertimeHours.xlsx" target="_blank">Download Sample File</a></td>
			</tr>
		</tbody></table>
		</s:form>




	</div>
	</div>



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