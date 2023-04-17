<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
    $(function(){
    	$("#jobCode").multiselect({
    		noneSelectedText: 'None Selected (required)',
    	}).multiselectfilter();
    	$("#frmAddSelectedCandidateInJob_submit").click(function(){
    		$(".validateRequired").prop('required',true);
    	});
    });    
</script>
	<% 
	UtilityFunctions uF = new UtilityFunctions();
	%>
<div>  
		<s:form id="frmAddSelectedCandidateInJob" name="frmAddSelectedCandidateInJob" method="POST" theme="simple" target="_parent" action="AddSelectedCandidateInJob">
			<s:hidden name="candidateId" id="candidateId"/>
			<s:hidden name="type" id="type"/>
			
			<div style="width: 100%; margin-top: 18px;"><b>Candidates:</b> <%=request.getAttribute("sbCandiNames") %></div>
			<div style="width: 100%; margin-bottom: 18px; margin-top: 18px;">
			<span>Add to Job Code:<sup>*</sup> &nbsp;&nbsp;</span>
				<span><select name="jobCode" id="jobCode" class="validateRequired" multiple="multiple" size="4">
				<%=request.getAttribute("option") %>
				</select> &nbsp;&nbsp;</span>
				<span><s:submit name="submit" value="Add" cssClass="btn btn-primary"></s:submit></span>
			</div>
		</s:form>
		
</div>
