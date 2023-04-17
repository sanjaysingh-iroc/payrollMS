<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF =  new UtilityFunctions();
Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
String policy_id = (String) request.getAttribute("policy_id");

String strE = (String)request.getParameter("operation"); 

%> 
<script type="text/javascript">
	$( function () {
		$("#submitButton").click(function(){
			$("#frm_MyPerks").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#frm_MyPerks").find('.validateRequired').filter(':visible').prop('required',true);
	    });
	});

	function readFileURL(input, targetDiv) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#'+targetDiv)
                    .attr('path', e.target.result);
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
	
	function checkPerkLimit() {
		var limit = document.getElementById("limit").innerHTML;
		limit = parseFloat(limit);
		var actualAmount = document.frm_MyPerks.strAmount.value; 
		var strType = document.frm_MyPerks.strType.value;
		
		var dblActualAmount = parseFloat(actualAmount);
		if(dblActualAmount>limit){
			alert ("You can not apply for more than "+limit);
			document.frm_MyPerks.strAmount.value = '';
			return false;
		}
		return true;
	}
	
	
	function getPolicy(val){
		document.getElementById("myDiv").innerHTML = '';
		document.getElementById("strType").selectedIndex = 0;
		document.getElementById("strMonth").selectedIndex = 0;
		document.getElementById("strAmount").value='';
		
		var action = 'GetPerkType.action?financialYear='+val;
		getContent('perkTypeDiv',action);
	}
	
	
	function checkPerkPolicy(){
		var policyId = document.getElementById("strType").value;
		var strMonth = document.getElementById("strMonth").value;
		var strId = document.getElementById("strId").value;
		document.getElementById("strAmount").value='';
		
		var action = 'GetPerkLimit.action?typeId='+policyId+'&strMonth='+strMonth+'&strId='+strId;
		getContent('myDiv',action);
	}
	
	
	function checkPerkPolicyOnLoad(){
		var policyId = document.getElementById("strType").value;
		var strMonth = document.getElementById("strMonth").value;
		var strId = document.getElementById("strId").value;
		
		var action = 'GetPerkLimit.action?typeId='+policyId+'&strMonth='+strMonth+'&strId='+strId;
		getContent('myDiv',action);
	}

	$("#frm_MyPerks").submit(function(e){
		var financialYear = document.getElementById("financialYear").value;
		//alert("check ........");
		e.preventDefault();
		if(checkPerkLimit()){
			if($("#doc").attr('path') !== undefined){
	     		  var form_data = new FormData($(this)[0]);
	    		  form_data.append("strDocument", $("#doc").attr('path'));
	    		  $("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    		  $.ajax({
	  	      		url: "Perks.action",
	  	      		type: 'POST',
	  	      		data: form_data,
	  	      		contentType: false,
	  	            cache: false,
	  	      		processData: false,
	  	      		success: function(result){
	  	      			$("#subDivResult").html(result);
	  	      	    },
		 			error : function(res) {
		 				$.ajax({
		 					url: 'Perks.action?financialYear='+financialYear,
		 					cache: true,
		 					success: function(result) {
		 						$("#subDivResult").html(result);
		 					}
		 				});
		 			} 
	  	      	 });
	     	  }else{
				var form_data = $("form[name='frm_MyPerks']").serialize();
		     	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		     	$.ajax({
		 			url : "Perks.action",
		 			data: form_data,
		 			cache : false,
		 			success : function(res) {
		 				$("#subDivResult").html(res);
		 			},
		 			error : function(res) {
		 				$.ajax({
		 					url: 'Perks.action?financialYear='+financialYear,
		 					cache: true,
		 					success: function(result) {
		 						$("#subDivResult").html(result);
		 					}
		 				});
		 			}
		 		});
	     	}
		}
	});
	
</script>


	<div class="box-body">
		<s:form id="frm_MyPerks" theme="simple" name="frm_MyPerks" action="Perks" enctype="multipart/form-data" method="post">
			<s:hidden name="strId" id="strId"/>
			<s:hidden name="operation" id="operation"/>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
			<table class="table table_no_border form-table">
				<tr>
					<td>Financial Year:<sup>*</sup></td>
					<td colspan="3">
						<s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" cssClass="validateRequired"
							onchange="getPolicy(this.value);" list="financialYearList" key="" />
					</td>
				</tr>

				<tr>
					<td>Month:<sup>*</sup></td>
					<td colspan="3">
						<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" onchange="checkPerkPolicy();" key="" cssClass="validateRequired" />
					</td>
				</tr>

				<tr>
					<td>Perk Policy:<sup>*</sup></td>
					<td colspan="3"><div id="perkTypeDiv">
						<s:select theme="simple" name="strType" id="strType" listKey="perkTypeId" cssClass="validateRequired" listValue="perkTypeName" headerKey="" headerValue="Select Perk Policy"		
							list="typeList" key="" required="true" onchange="checkPerkPolicy();" />
						</div>
					</td>
				</tr>

				<tr>
					<td valign="top">Amount:<sup>*</sup></td>
					<td colspan="3">
						<s:textfield name="strAmount" id="strAmount" cssClass="validateRequired"  onkeyup="checkPerkLimit();" onkeypress="return isNumberKey(event)" />
						<div id="myDiv" style="color:red"></div>
					</td>
				</tr>

				<tr>
					<td valign="top">Purpose:<sup>*</sup></td>
					<td colspan="3">
						<s:textarea rows="5" cols="40" name="strPurpose" cssClass="validateRequired"></s:textarea>
					</td>
				</tr>

				<%if(strE!=null) { %>
				<tr>
					<td colspan="4" style="text-align: center;">
					<%=uF.showData((String)request.getAttribute("strViewDocument"), "") %>
					</td>
				</tr>
				<tr>
					<td>Attach Document:<sup>*</sup></td>
					<td colspan="3">
						<div id="doc"></div>
						<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" onchange="readFileURL(this, 'doc');"/>
					</td>
				</tr>
				<% } else { %>		
				<tr>
					<td>Attach Document:</td>
					<td colspan="3">
						<div id="doc"></div>
						<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" onchange="readFileURL(this, 'doc');"/>
					</td>
				</tr>
				<% } %>	

			<% if(uF.parseToBoolean(CF.getIsWorkFlow())) {
						
				if(hmMemberOption!=null && !hmMemberOption.isEmpty()) {
				Iterator<String> it1=hmMemberOption.keySet().iterator();
				while(it1.hasNext()) {
					String memPosition=it1.next();
					String optiontr=hmMemberOption.get(memPosition);
					out.println(optiontr);
				}
			%>
			
				<tr><td>&nbsp;</td>
					<td colspan="3"><input type="submit" name="submit" id="submitButton" value="Submit" class="btn btn-primary"/></td>
				</tr>
				<% } else { %>
				<tr><td colspan="4">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>
				<% } %>
			<% } else { %>
				<tr>
					<td>&nbsp;</td>
					<td colspan="3"><input type="submit" name="submit" id="submitButton" value="Submit" class="btn btn-primary"/></td>
				</tr>
			<% } %>
			</table>
		</s:form>
	</div>
           <!-- /.box-body -->

<script type="text/javascript">
	$(function () {
		checkPerkPolicyOnLoad();
	}); 
</script>
