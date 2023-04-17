<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
	<%
    String formName = (String)request.getAttribute("formName");
    System.out.println("formName ===>> " + formName);
    %>

<script type="text/javascript">
   
	$("#frmAlignResumeShortlistWorkflowMember").submit(function(e){
		e.preventDefault();
		//alert("for check");
		var formName = document.getElementById("formName").value;
		var recruitmentID = document.getElementById("recruitmentID").value;
		/* var form_data = $("form[name='frmAlignResumeShortlistWorkflowMember']").serialize(); */
		var form_data = $("#frmAlignResumeShortlistWorkflowMember").serialize();
		//alert("form_data ===>> " + form_data);
    	$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type: 'POST',
			url : "AlignResumeShortlistWorkflowMember.action",
			data: form_data + '&strInsert=Submit',
			cache : true,
			success : function(result) {
				if(formName != "" && (formName == "WF" || formName == 'A')) {
					$.ajax({
						url: 'Applications.action?recruitId='+recruitmentID,
						cache: true,
						success: function(result){
							$("#subSubDivResult").html(result);
				   		}
					});
				} else {
					$.ajax({
						url: 'ReportJobProfilePopUp.action?recruitId='+recruitmentID+'&fromPage='+formName+'&view=jobreport',
						cache: true,
						success: function(result){
							$("#subSubDivResult").html(result);
				   		}
					});
				}
				$("#modalInfo").hide();
			},
			error : function(error) {
				if(formName != "" && (formName == "WF" || formName == 'A')) {
					$.ajax({
						url: 'Applications.action?recruitId='+recruitmentID,
						cache: true,
						success: function(result){
							$("#subSubDivResult").html(result);
				   		}
					});
				} else {
					$.ajax({
						url: 'ReportJobProfilePopUp.action?recruitId='+recruitmentID+'&fromPage='+formName+'&view=jobreport',
						cache: true,
						success: function(result){
							$("#subSubDivResult").html(result);
				   		}
					});
				}
				$("#modalInfo").hide();
			}
    		
		});
			
	});


    function GetXmlHttpObject() {
          if (window.XMLHttpRequest) {
                  // code for IE7+, Firefox, Chrome, Opera, Safari
                  return new XMLHttpRequest();
          }
          if (window.ActiveXObject) {
                  // code for IE6, IE5
                  return new ActiveXObject("Microsoft.XMLHTTP");
          }
          return null;
    }
    
</script>

<s:form id="frmAlignResumeShortlistWorkflowMember" name="frmAlignResumeShortlistWorkflowMember" theme="simple" action="AlignResumeShortlistWorkflowMember" method="POST" cssClass="formcss">
	<div class="col-lg-12 col-md-12 col-sm-12">
		<s:hidden name="formName" id="formName"></s:hidden>
		<s:hidden name="recruitmentID" id="recruitmentID"></s:hidden>
	    <table class="table table_no_border">  <!-- table-striped -->
	        <%
				Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
				String policy_id = (String) request.getAttribute("policy_id");
				String recruitmentID = (String) request.getAttribute("recruitmentID");
				
				CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);   
				UtilityFunctions uF = new UtilityFunctions();
				
				/* String strValue = "Send Job Requirement Request"; 
				if(uF.parseToInt(recruitmentID) > 0){
					strValue = "Update";
				} */
				
				if (uF.parseToBoolean(CF.getIsWorkFlow())) {
					if (hmMemberOption != null && !hmMemberOption.isEmpty()) {%>
							<%
								Iterator<String> it = hmMemberOption.keySet().iterator();
								while (it.hasNext()) {
									String memPosition = it.next();
									String optiontr = hmMemberOption.get(memPosition);
							%>
								<%=optiontr%>
							<%}%>
						<tr>
							<td></td>
							<td>
								<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
								<input class="btn btn-primary" name="strInsert" type="submit" value="<%="Submit" %>">
							</td>
						</tr>
						
					<%} else {%>
						<tr>
							<td></td>
							<td>
								Your work flow is not defined.Please, speak to your HR for your work flow.
							</td>
						</tr>
			<%
					}
				} else {
			%>
				<tr>
					<td></td>
					<td>
						<input type="hidden" name="policy_id" id="policy_id" value="<%=policy_id%>" />
						<input class="btn btn-primary" name="strInsert" type="submit" value="<%="Submit" %>">
					</td>
				</tr>
			<%
				}
			%>
	    </table>
	</div>
	
</s:form>

