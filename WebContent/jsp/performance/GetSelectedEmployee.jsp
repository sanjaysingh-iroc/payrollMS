<%-- <%@page import="java.util.List"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
   
 
 <%
List<String> selectEmpList=(List<String>)request.getAttribute("empNameList");
String setSelectedEmp=(String)request.getAttribute("selectedID");
if(selectEmpList!=null) {
%>
<div style="border: 2px solid #ccc;">
<%if(selectEmpList != null && !selectEmpList.isEmpty() && selectEmpList.size() > 0){ %>
		<div style="padding: 8px 20px; border: 2px solid lightgray;"><b>Employee</b></div>
		
		<table border="0" class="formcss" width="100%">
		<% 
			for(int i=0;i<selectEmpList.size();i++){
		%>
			<tr>
				<td nowrap="nowrap" style="font-weight: bold;"><%=i+1 %></td>
				<td align="left"><%=selectEmpList.get(i) %></td>				
			</tr>
			<% } %>
		</table>
		<%} %>
		<%if(selectEmpList.isEmpty() || selectEmpList.size() == 0){ %>
			<div class="nodata msg" style="width: 85%"> <span>No Employee selected</span> </div>
		<%} %>
	</div> 
<% } else{ %>
<div class="nodata msg" style="width:85%"><span>No Employee selected</span></div>
<% } %>
<input type="hidden" name="empselected" id="empselected" value="<%=setSelectedEmp!=null && !setSelectedEmp.equals("") ? setSelectedEmp :"0" %>"/> --%>


<%-- <%System.out.println("allData == "+request.getAttribute("allData").toString()); %> --%>

<%=request.getAttribute("allData").toString()%>