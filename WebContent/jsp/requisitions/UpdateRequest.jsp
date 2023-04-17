<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script>
	
	<% 
	String type = (String)request.getAttribute("type");
	String strMode = (String)request.getAttribute("strMode");
	String strType = (String)request.getAttribute("strType");
	if(strType != null && strType.equalsIgnoreCase("REG") && type!= null && type.equalsIgnoreCase("TR")) { %>
	     
		$("#frmUpdateRequest").submit(function(event){
			event.preventDefault();
			var currUserType = document.getElementById("currUserType").value;
			var divResult = document.getElementById("strDivResult").value;
			var form_data = $("form[name='frmUpdateRequest']").serialize();
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$.ajax({
	 			url : "UpdateRequest.action",
	 			data: form_data,
	 			cache : false/* ,
	 			success : function(res) {
	 				$("#"+divResult).html(res);
	 			} */
	 		});
	    	
	    	$.ajax({
				url: 'ResignationReport.action?currUserType='+currUserType,
				cache: true,
				success: function(result){
					$("#"+divResult).html(result);
		   		}
			});
	    	
		});
	<% } %>
</script>

<div>

	<% 
	//System.out.println("strMode ===>> " + strMode);
	if(strMode != null && strMode.equals("VIEW")) { 
		List<String> alData = (List<String>) request.getAttribute("alData");
	%>
		<table border="0" class="table table_no_border">
				<tr>
					<th class="txtlabel alignRight" valign="top">Canceled By & Date:</th>
					<td><%=alData.get(0) %> on <%=alData.get(1) %></td>
				</tr>
				<tr>
					<th class="txtlabel alignRight" valign="top">Reason:</th>
					<td><%=alData.get(2) %></td>
				</tr>
		</table>
	<% } else { %>
		<s:form id="frmUpdateRequest" name="frmUpdateRequest" theme="simple" action="UpdateRequest" method="POST" cssClass="formcss">
	
			<table border="0" class="table table_no_border">
				<tr>
					<td colspan=2><s:fielderror /></td>
				</tr>
				<tr>
					<td>
						<input type="hidden" name="operation" value="U"/>
						<s:hidden name="from"></s:hidden>
						<input type="hidden" name="S" value="<%=(String)request.getAttribute("strStatus") %>"/>
						<input type="hidden" name="M" value="<%=(String)request.getAttribute("strMode") %>"/>
						<input type="hidden" name="RID" value="<%=(String)request.getAttribute("strId") %>"/>
						<input type="hidden" name="T" value="<%=(String)request.getAttribute("strType") %>"/>
						<s:hidden name="type"></s:hidden>
						<s:hidden name="strDivResult" id="strDivResult" />
						<s:hidden name="currUserType" id="currUserType" />
						<s:hidden name="userType" id="userType" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight" valign="top">Reason:</td> 
					<td><textarea name="approveDenyReason" id="approveDenyReason" cols="26" rows="4"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><s:submit cssClass="btn btn-primary" value="Submit" name="btnAddNewLevelOk" id="btnAddNewLevelOk"/></td>
				</tr>
			</table>
		</s:form>
	<% } %>
	
</div>



