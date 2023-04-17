<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	String type = (String) request.getAttribute("type");
	String closeReason = (String) request.getAttribute("closeReason");
	String proPage = (String) request.getParameter("proPage");
	String minLimit = (String) request.getParameter("minLimit");
	String dataType = (String) request.getAttribute("dataType");
	String currUserType = (String) request.getAttribute("currUserType");
	String fromPage = (String) request.getAttribute("fromPage");
	String empId = (String) request.getAttribute("empId");
	String f_org = (String) request.getAttribute("f_org");
	//String view = (String) request.getAttribute("view");
	//System.out.println("type ===>> " + type);
%>
<div id="closeGoalTargetKRADiv">
<% if(closeReason != null && !closeReason.equals("") && (type == null || type.equals("null") || !type.equals("open"))) { %>
	<table border="0" class="" style="width: 97%; float: left">
			<tr>
				<td class="txtlabel alignRight" style="width: 50px;" valign="top">Reason:</td>
				<td><%=(closeReason==null ? "" : closeReason)%></td>
			</tr>
		</table>
	<% } else { %>
	<s:form id="formID" name="frmCloseGoalTargetKRA" theme="simple" action="CloseGoalTargetKRA" method="POST" cssClass="formcss" enctype="multipart/form-data">
		<s:hidden name="type"></s:hidden> 
		<s:hidden name="goalId"></s:hidden> 
		<s:hidden name="orgID"></s:hidden> 
       	<s:hidden name="wlocID"></s:hidden>
       	<s:hidden name="desigID"></s:hidden>
       	<s:hidden name="checkStatus"></s:hidden>
       	<s:hidden name="fdate"></s:hidden>
       	<s:hidden name="tdate"></s:hidden>
       	<s:hidden name="fromPage"></s:hidden>
       	<s:hidden name="operation" value="update"></s:hidden>
       	<s:hidden name="kratype"></s:hidden>
       	<s:hidden name="dataType"></s:hidden>
       	<s:hidden name="currUserType"></s:hidden>
       	<s:hidden name="empId"></s:hidden>
       	<input type="hidden" name="proPage"  value = "<%= proPage%>" />
       	<input type="hidden" name="minLimit" value = "<%= minLimit %>" />
       
		<table border="0" class="table" style="width: 97%; float: left">
			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:<sup>*</sup></td>
				<td><textarea name="closeReason" id="closeReason" cols="26" rows="4" required ><%=(closeReason==null ? "" : closeReason)%></textarea></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					 <s:submit cssClass="btn btn-primary" value="Submit" align="center" />
				</td>
			</tr>
		</table>
	</s:form>
	<% } %>
</div>
<script>

$("input[type='submit']").click(function(){
	$("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
	$("#formID").find('.validateRequired').filter(':visible').prop('required',true);
});

$("#formID").submit(function(event){
	event.preventDefault();
	var from = '<%=fromPage%>';
	if(from != null) {
		var form_data = $("#formID").serialize();
		$("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type :'POST',
			url  :'CloseGoalTargetKRA.action',
			data :form_data,
			cache:true,
			success : function(result) {
				if(from != "" && from == 'GKT') {
					getGoalKRADetails('GoalKRATarget','<%=empId%>','<%=dataType%>','<%=currUserType%>',from);
					getGoalsData('GoalKRATargetDashboard','','','','');/* Created by dattatray */
				}else if(from != "" && from == 'GS'){
					console.log('GS')
					getGoalSummary('GoalSummary','<%=dataType%>','<%=currUserType%>', '<%=f_org %>');
					getGoalsData('GoalSummaryDashboard','','','','');/* Created by dattatray */
				}else {
					$("#divMyHRData").html(result);
				}
			},
			error : function(result) {
				if(from != "" && from == 'GKT') {
					getGoalKRADetails('GoalKRATarget','<%=empId%>','<%=dataType%>','<%=currUserType%>',from);
					getGoalsData('GoalKRATargetDashboard','','','','');/* Created by dattatray */
				} else if(from != "" && from == 'GS') {
					getGoalSummary('GoalSummary','<%=dataType%>','<%=currUserType%>', '<%=f_org %>');
					getGoalsData('GoalSummaryDashboard','','','','');/* Created by dattatray */
				} else {
					//$("#divMyHRData").html(result);
					getMyHRData('KRATarget','L','','');
				}
			}
		});
		
	}
});
</script>