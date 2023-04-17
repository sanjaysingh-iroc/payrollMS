<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
 
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script src='scripts/customAjax.js'></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
	String policy_id = (String) request.getAttribute("policy_id");
	String fromPage = (String) request.getAttribute("fromPage");
	//System.out.println(fromPage);
%>

<s:form id="formID" name="frmLearningRequestPopUp" theme="simple" action="LearningRequestPopUp" method="POST" cssClass="formcss">
	<s:hidden name="fromPage" id="fromPage"></s:hidden>
	<s:hidden name="operation"></s:hidden>
	<s:hidden name="planId"></s:hidden>
	
	<table class="table table_no_border" style="width: 100%">
		<%	
				if (hmMemberOption != null && !hmMemberOption.isEmpty()) { %>
				<%
					Iterator<String> it = hmMemberOption.keySet().iterator();
					while (it.hasNext()) {
						String memPosition = it.next(); 
						String optiontr = hmMemberOption.get(memPosition);
				%>
					<%=optiontr%>
					<% } %>
				<% } else { %>
					<tr>
		     			<td colspan="2">Your work flow is not defined.Please, speak to your HR for your work flow.</td>
		     		</tr>
		     	<% } %>	
			<tr>
	     		<td>&nbsp;</td>
	     		<td>
					<input type="hidden" name="policyId" id="policyId" value="<%=policy_id%>"/>
					<input id="submitButton" class="btn btn-primary" type="submit" value="Submit"/>
				</td>
			</tr>
	</table>
	
</s:form>

<script>


  $("#formID").submit(function(e) {
		
		var form_data = $(this).serialize();
	    $("#divMyHRData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    
		$.ajax({ 
			type : 'POST',
			url: "LearningRequestPopUp.action?operation=A",
			data: form_data+"&submit=Save",
			success: function(result){
				$("#divMyHRData").html(result); 
				$.ajax({
					url : "MyLearningPlan.action?dataType=LC&fromPage=MyHR", 
					//url : "MyLearningPlan.action?&dataType=LC",
					cache : false,
					success : function(data) {
						$("#divMyHRData").html(data);
					}
				}); 
	   		},
	   	//===start parvez date: 11-10-2021=== 
	   		error: function(res){
	     		$.ajax({
 					//url: 'MyLearningPlan.action?dataType=LC&fromPage='+strfromPage,
 					url : "MyLearningPlan.action?dataType=LC&fromPage=MyHR",
 					cache: true,
 					success: function(result){
 						
 						$("#divMyHRData").html(result);
 			   		}
 				});
    		}
	  //===end parvez date: 11-10-2021=== 
		});
    });

</script>
