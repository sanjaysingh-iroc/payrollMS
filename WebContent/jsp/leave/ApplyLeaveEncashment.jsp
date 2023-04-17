<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

	<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		UtilityFunctions uF =  new UtilityFunctions();
		Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
		if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
		String policy_id = (String) request.getAttribute("policy_id");
		String strUserTYpe = (String)session.getAttribute(IConstants.USERTYPE); 
	%>

 
<style>#strMessageId{color: rgb(207, 9, 9);}</style>
<script type="text/javascript" charset="utf-8">
 	
	$(function () {
		$("#btnSubmit").click(function(){
			$(".validateRequired").prop('required',true);
		});	
	});
	
/* 	$(document).on('click', '#btnSubmit', function(e){
		e.preventDefault();
		refresh("#divResult");
	}); */ 
	
	
	function checkDays(val) {
		document.getElementById("strMessageId").innerHTML='';
		//document.getElementById("submitTrId").style.display='none';
		
		var availEncash= 0;
		var availEncash= 0;
		if(document.getElementById("strAvailableEncashment")) {
			availEncash=document.getElementById("strAvailableEncashment").value;
		}
		if(document.getElementById("strMaxLeavesForEncashment")) {
			MaxEncash=document.getElementById("strMaxLeavesForEncashment").value;
		}
		
		if(val =='' || parseFloat(val)==0) {
			document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
		} else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(MaxEncash) == 0) {
			
		} else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(val)<=parseFloat(MaxEncash)) {
			
		} else {
			document.getElementById("strMessageId").innerHTML='You have entered more than available encashment.';
		}
	} 
	
	function checkDays1(){
		document.getElementById("strMessageId").innerHTML='';
		//document.getElementById("submitTrId").style.display='none';
		var val = document.getElementById("strNoOfDays").value;
		var availEncash= 0;
		var availEncash= 0;
		if(document.getElementById("strAvailableEncashment")) {
			availEncash=document.getElementById("strAvailableEncashment").value;
		}
		if(document.getElementById("strMaxLeavesForEncashment")) {
			MaxEncash=document.getElementById("strMaxLeavesForEncashment").value;
		}
		
		
		if(val =='' || parseFloat(val)==0) {
			document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
			return false;
		} else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(MaxEncash) == 0) {
			return true;
		} else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(val)<=parseFloat(MaxEncash)) {
			return true;
		} else {
			document.getElementById("strMessageId").innerHTML='You have entered more than available encashment.';
			return false;
		}
	}
	
	$("#frm_ApplyLeaveEncashment").submit(function(e){
		e.preventDefault();
		if(checkDays1()){
			var form_data = $("form[name='frm_ApplyLeaveEncashment']").serialize();
	     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "ApplyLeaveEncashment.action",
	 			data: form_data,
	 			cache : false,
	 			success : function(res) {
	 				$("#divResult").html(res);
	 			},
	 			error : function(res) {
	 				$.ajax({ 
	 					url: 'LeaveEncashment.action',
	 					cache: true,
	 					success: function(result){
	 						$("#divResult").html(result);
	 			   		}
	 				});
	 			}
	 		});
		}
	});
	
	function getLeaveBalance(selected_option){
		$("#myDiv").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
 			url : "GetLeaveEncashmentInfo.action?LID="+selected_option,
 			cache : false,
 			success : function(res) {
 				$("#myDiv").html(res);
 			}
 		});
	}
</script>

		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<s:form id="frm_ApplyLeaveEncashment" theme="simple" name="frm_ApplyLeaveEncashment" action="ApplyLeaveEncashment">
			<s:hidden name="strId"/>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
			<s:hidden name="strPaycycle"/>
			<s:hidden name="pageType"/>
			
			<table class="table table_no_border">
				<tr>
					<th valign="top" style="border-top: none;">Type:<sup>*</sup></th>
					<td colspan="3" style="border-top: none;" valign="top">
						<s:select  cssClass="validateRequired" name="typeOfLeave" cssStyle="float:left;" headerKey="" headerValue="Select Leave Type" 
						listKey="leaveTypeId" listValue="leavetypeName" onchange="getLeaveBalance(this.options[this.selectedIndex].value);" 
						list="leaveTypeList" key="" required="true" />
						<div id="myDiv" style="float:left; text-align:left;margin-left: 10px;"><%=(request.getAttribute("STATUS_MSG")!=null?request.getAttribute("STATUS_MSG"):"") %></div>
					</td>
				</tr>
				
				<tr>
					<th valign="top">No of days:<sup>*</sup></th>
					<td colspan="3">
						<s:textfield name="strNoOfDays" id="strNoOfDays" cssClass="validateRequired" onkeyup="checkDays(this.value);" onkeypress="return isNumberKey(event)"/>
						<div id="strMessageId"></div>
					</td>
				</tr> 
				
				<tr>
					<th valign="top">Reason:<sup>*</sup></th><td colspan="3">
						<s:textarea rows="5" cols="40" name="strReason" cssClass="validateRequired"></s:textarea>
					</td>
				</tr>
				
				<%
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
					if(hmMemberOption!=null && !hmMemberOption.isEmpty() ) {
						Iterator<String> it1=hmMemberOption.keySet().iterator();
						while(it1.hasNext()) {
							String memPosition=it1.next();
							String optiontr=hmMemberOption.get(memPosition);					
							out.println(optiontr); 
					}
				%>
					<tr><td>&nbsp;</td>
						<td colspan="3"><input type="submit" name="btnSubmit" value="Submit" id="btnSubmit" class="btn btn-primary"/></td>
					</tr>
				<% } else { %>
					<tr><td colspan="4">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>
				<% } %>
				<% } else { %>
					<tr><td>&nbsp;</td>
						<td colspan="3"><input type="submit" name="btnSubmit" value="Submit" id="btnSubmit" class="btn btn-primary"/></td>
					</tr>
				<% } %>
			</table>					
		</s:form>
    </div>

