<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%
String currUserType = (String) request.getAttribute("currUserType");  
//System.out.println("Current Type===>"+currUserType);
String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>

<script>
	
$("#frmAddComment").submit(function(e){
	e.preventDefault();
	
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var currUserType = document.getElementById("currUserType").value;
	if(strBaseUserType == '<%=IConstants.CEO %>' || strBaseUserType == '<%=IConstants.HOD %>') {
		divResult = 'subDivResult';
	}
	
	var form_data = $("form[name='frmAddComment']").serialize();
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
			url : "AddLeaveComment.action",
			data: form_data+'&commentSubmit=submit',
			cache : false,
			success : function(res) {
				$("#"+divResult).html(res);
			},
			error : function(res) {
				$.ajax({
					url: 'ManagerLeaveApprovalReport.action?leaveStatus='+leaveStatus+'&strStartDate='+startDate+'&strEndDate='+endDate+'&currUserType='+currUserType,
					cache: true,
					success: function(result){
						$("#"+divResult).html(result);
			   		}
				});
			}
		});
    	
});

</script>


<s:form action="AddLeaveComment" name="frmAddComment" id="frmAddComment" theme="simple" method="POST">

	<s:hidden name="leaveId"></s:hidden>
	<s:hidden name="leaveStatus"></s:hidden>
	<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
	<input type="hidden" name="currUserType" id="currUserType" value="<%=currUserType %>" />
	
	<div style="text-align:center">
		<s:textarea cols="35" rows="2" name="strComment" id="strComment"></s:textarea>
	</div>
	<br/>
	<div style="text-align:center">
		<s:submit cssClass="btn btn-primary" name="commentSubmit" id="commentSubmit" value="Enter Comment"></s:submit>
	</div>
</s:form>