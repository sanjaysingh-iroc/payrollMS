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
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
String policy_id = (String) request.getAttribute("policy_id");

String operation = (String)request.getAttribute("operation"); %>    

 
<script type="text/javascript" charset="utf-8">
$(function () {
	$("input[value='Submit']").click(function(){
		$(".validateRequired").prop('required',true);
	});
});

function checkLTAAmount(){
	var amount = document.frm_MyLTA.strAmount.value; 
	amount = parseFloat(amount);
	var actualAmount = document.frm_MyLTA.strActualAmount.value; 
	actualAmount = parseFloat(actualAmount);
	
	if(amount > actualAmount){
		alert ("You can not apply for more than "+actualAmount);
		document.frm_MyLTA.strAmount.value = '';
		return false;
	} else if(amount == 0){
		alert ("You can not apply CTC variable amount as 0.");
		document.frm_MyLTA.strAmount.value = '';
		return false;
	}
	return true;
}

function readFileURL(input, targetDiv) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#'+targetDiv).attr('path', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

$("#frm_MyLTA").submit(function(e){
	e.preventDefault();
	if(checkLTAAmount()){
		if($("#file").attr('path') !== undefined){
	  		var form_data = new FormData($(this)[0]);
	  		form_data.append("strDocument", $("#file").attr('path'));
	  		  //alert("form_data ===>> " + form_data);
	  		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "ApplyLTA.action",
	 			type: 'POST',
	      		data: form_data,
	      		contentType: false,
	            cache: false,
	      		processData: false,
	 			success : function(res) {
	 				$("#divResult").html(res);
	 			},
				error : function(res) {
					$.ajax({
						url: 'CTCVariable.action',
						cache: true,
						success: function(result){
							$("#divResult").html(result);
				   		}
					});
				}
	 		});
		 }else{
			var form_data = $("form[name='frm_MyLTA']").serialize();
	     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "ApplyLTA.action",
	 			data: form_data,
	 			cache : false ,
	 			success : function(res) {
	 				$("#divResult").html(res);
	 			},
				error : function(res) {
					$.ajax({
						url: 'CTCVariable.action',
						cache: true,
						success: function(result){
							$("#divResult").html(result);
				   		}
					});
				}
	 		});
		}
	}
});


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function viewApplyLTA(strEmpId){
	var dialogEdit = '.modal-body';
	
	$.ajax({
		url : 'ApplyLTA.action?selectedEmpID='+strEmpId,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>


	<div class="box-body" style="padding: 5px; overflow-y: auto;">
		<s:form id="frm_MyLTA" theme="simple" name="frm_MyLTA" action="ApplyLTA" enctype="multipart/form-data">
			<s:hidden name="strId"/>
			<s:hidden name="operation"/>
			<input type="hidden" name="policy_id" id="policy_id" value="<%=(String)request.getAttribute("policy_id") %>"/>
			<input type="hidden" name="strEmpID" id="strEmpID" value="<%=(String)request.getAttribute("strEmpID") %>"/>
				<table class="table table_no_border form-table">
					<%if(strUserType != null && strUserType.equals(IConstants.ADMIN)){ %>
						<tr>
							<td>Employee:<sup>*</sup></td>
							<td colspan="3">
								<s:select theme="simple" name="selectedEmpID" id="selectedEmpID" listKey="employeeId" listValue="employeeCode" headerKey=""  headerValue="All Employees" 
								 	list="empNamesList" key="" required="true" onchange="viewApplyLTA(this.value);"/>
							</td>
						</tr>
						
						<tr>
							<td class="alignRight">PayCycle:<sup>*</sup></td>
							<td colspan="3">
								<s:select name="paycycle"  id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" 
								 cssClass="validateRequired"/>
								
							</td> 
						</tr>
					<%} %>
					
					<tr>
						<td>CTC Variable Head:<sup>*</sup></td>
						<td colspan="3">
							<s:select theme="simple" name="salaryHead" id="salaryHead" listKey="salaryHeadId" cssClass="validateRequired" listValue="salaryHeadName" headerKey="" headerValue="Select Variable Head"		
								list="salaryHeadList" key="" required="true" onchange="getContent('myDiv','GetLTAAmount.action?strTypeId='+this.value+'&strEmpId='+document.frm_MyLTA.strEmpID.value);" />
						</td>
					</tr>
										
					<tr>
						<td>Balance Variable Amount:</td>
						<td colspan="3">
							<div id="myDiv"><s:textfield name="strActualAmount" id="strActualAmount" readonly="true"/></div>
						</td>			
					</tr>
								
					<tr>
						<td>Amount:<sup>*</sup></td>
						<td colspan="3">
							<s:textfield name="strAmount" id="strAmount" cssClass="validateRequired validateNumber"  onkeyup="checkLTAAmount();" onkeypress="return isNumberKey(event)" />
						</td>			
					</tr>
								
					<tr>
						<td valign="top">Purpose:<sup>*</sup></td>
						<td colspan="3">
							<s:textarea rows="5" cols="40" name="strPurpose" cssClass="validateRequired"></s:textarea>
						</td>
					</tr>
								
					<%if(operation != null && operation.equalsIgnoreCase("U")){ %>
						<tr>
							<td>&nbsp;</td>
							<td colspan="3">
								<%=uF.showData((String)request.getAttribute("strViewDocument"),"") %>
							</td>
						</tr>
						<tr>
							<td>Attach Document:</td>
							<td colspan="3">
								<span id="file"></span>
								<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" onchange="readFileURL(this, 'file');"/>
							</td>
						</tr>
					<%}else{%>		
						<tr>
							<td>Attach Document:</td>
							<td colspan="3">
								<span id="file"></span>
								<s:file accept=".gif,.jpg,.png,.tif,.svg, .svgz,.xls,.pdf,.ppt,.doc,.docs" name="strDocument" onchange="readFileURL(this, 'file');"/>
							</td>
						</tr>
					<%}%>	
					<% if(uF.parseToBoolean(CF.getIsWorkFlow())) {		
							if(hmMemberOption!=null && !hmMemberOption.isEmpty() ){
								Iterator<String> it1=hmMemberOption.keySet().iterator();
								while(it1.hasNext()){
									String memPosition=it1.next();
									String optiontr=hmMemberOption.get(memPosition);					
									out.println(optiontr); 
								}
					%>
					<tr><td>&nbsp;</td>
						<td colspan="3"><input type="submit" name="submit1" id="submit1" value="Submit" class="btn btn-primary" /></td>
					</tr>
					<% } else { %>
					<tr><td colspan="4">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>
					<% } %>
					<% } else { %>
					<tr><td>&nbsp;</td>
						<td colspan="3"><input type="submit" name="submit1" id="submit1" value="Submit" class="btn btn-primary" /></td>
					</tr>
					<% } %>
				</table>
			</s:form>
		</div>
                <!-- /.box-body -->
  
