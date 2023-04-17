<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<div style="margin-top: 25px; border: 1px solid #CCCCCC;">
		<p class="past">Bulk Timesheet Import & Download Sample File</p> 
		<div style="text-align: center; padding: 10px;">
						
		<s:form enctype="multipart/form-data" method="POST" action="ImportTimesheet"  name="ImportTimesheet" id="ImportTimesheet" theme="simple">
			<table style="width: 100%;">
				<tbody>
					<tr>
						<td class="txtlabel alignRight">Select File to Import:</td>
						<td><s:file name="fileUpload1" label="Select a File to upload" size="20" /></td>
					</tr>  
					<tr>
						<td align="center" colspan="2"><input type="submit" class="input_button" value="Import File" id="ImportTimesheet_0"></td>
					</tr>
					<tr>
						<td align="right" colspan="2"><a title="Download Sample File" href="import/ImportTimeSheet.xlsx" target="_blank">Download Sample File</a></td>
					</tr>
				</tbody>
			</table>
			</s:form>
		</div>
	</div>	
	
	<%
	java.util.List couterlist = (java.util.List)request.getAttribute("alReport");
	if(couterlist!=null) {
	%>
	<div style="width: 95%; float: left; margin-top: 40px;">
		<table style='width:100%;'>
			<tbody style='background-color:#EFEFEF;'>
			   <% for (int i=0; i<couterlist.size(); i++) {%>
			    <tr>
			 		<td align="left"><%= couterlist.get(i) %></td>
			    </tr>
			   <%} %>
			</tbody>
		</table>
	</div>
	<%} %>
