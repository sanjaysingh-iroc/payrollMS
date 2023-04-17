<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <% if(((String)session.getAttribute(IConstants.MESSAGE)).equals("")) { %> --%>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
	<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
	<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
	<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<%-- <% } %> --%>

<script> 
$(document).ready( function () {
	
	$("#potentialAttribute").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	$("#performanceAttribute").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	$("#skills").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	$("#department").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	$("#sbu").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	$("#geography").multiselect({ nonSelectedText: 'Select Something (required)' }).multiselectfilter();
	
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
	var presentExpCheck = document.getElementById("presentOrgExpCheckbox").checked;
	//alert(presentExpCheck);
	showPresentExpYearsMonths(presentExpCheck);
	
	var totExpCheck = document.getElementById("totExpCheckbox").checked;
	//alert(totExpCheck);
	showTotalExpYearsMonths(totExpCheck);
	
	/* var value = document.getElementById("qualification").value;
	showWieghtageTd(value); */
});	

/* function validateForm(){
	if(document.frmAddSuccessionPlanCriteria.desigCode==''){
		alert ("Desig code is required, please enter the code");
		return false;
	}else {
		return true;
	}
}
 function viewAttributeData(id,check){
	 
	 if(check==true){
		 document.getElementById("attributeid"+id).style.display="table-row";
	 }else{
		 document.getElementById("attributeid"+id).style.display="none";
		 document.getElementById("totExpYear").selectedIndex="0";
		 document.getElementById("totExpMonth").selectedIndex="0";
	 }
	 
 } */

 function showPresentExpYearsMonths(check){
	 if(check==true){
	 	document.getElementById("presentExpDiv").style.display="block";
	 }else{
		 document.getElementById("presentExpDiv").style.display="none";
		 document.getElementById("presentOrgExpYear").selectedIndex="0";
		 document.getElementById("presentOrgExpMonth").selectedIndex="0";
		 
	 }
 }
 
 function showTotalExpYearsMonths(check){
	 if(check==true){
	 	document.getElementById("totExpDiv").style.display="block";
	 }else{
		 document.getElementById("totExpDiv").style.display="none";
	 }
 }
 
 /* function showWieghtageTd(value){
	 if(value==''){
	 	document.getElementById("qualiWeightlblTD").style.display="none";
	 	document.getElementById("qualiWeightvalTD").style.display="none";
	 }else{
		 document.getElementById("qualiWeightlblTD").style.display="table-cell";
		 document.getElementById("qualiWeightvalTD").style.display="table-cell";
	 }
 } */
 
</script>
<%
UtilityFunctions uF=new UtilityFunctions();
List<List<String>> outerList=(List<List<String>>)request.getAttribute("attributeList"); 
Map<String,String> hmDesigAttribute=(Map<String,String>)request.getAttribute("hmDesigAttribute");
if(hmDesigAttribute==null) hmDesigAttribute=new HashMap<String,String>();

%>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>
	
<s:form theme="simple" id="frmAddSuccessionPlanCriteria" name="frmAddSuccessionPlanCriteria" action="AddSuccessionPlanCriteria" method="POST" cssClass="formcss">
	<s:hidden name="desigId" id="desigId" />
	<s:hidden name="orgId" id="orgId"/>
	<s:hidden name="operation" id="operation"/>
	<s:hidden name="criteriaId" id="criteriaId"/>
	<div style="float:left;">
	<h4><%=uF.showData((String)request.getAttribute("desigName"), "")  %></h4>
		<table class="table">
			<tr>
				<td class="txtlabel alignRight" nowrap="nowrap">Minimum Qualification to Get into the Succession Plan:<sup>*</sup></td>
				<td colspan="3">
					<s:select name="qualification" id="qualification" cssClass="validateRequired" headerKey="" headerValue="Select Qualification" 
					list="educationList" listKey="eduId" listValue="eduName" /> <!-- onchange="showWieghtageTd(this.value);" --> 
				</td>
				<%-- <td id="qualiWeightlblTD" class="txtlabel alignRight" nowrap="nowrap" style="display: none;">Weightage:</td>
				<td id="qualiWeightvalTD" style="display: none;"><s:select name="eduWeightage" id="eduWeightage" cssClass="validateRequired" cssStyle="width: 80px;" headerKey="" headerValue="Select" 
				list="#{'1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10' }"/></td> --%>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr>
		
			<tr>
				<td class="txtlabel alignRight" nowrap="nowrap">Years of Experience for Readiness:</td>
				<td colspan="3">
				<td class="txtlabel" nowrap="nowrap"><i> Readiness</i></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight" nowrap="nowrap" style="font-weight: normal;">Total years of experience:</td>
				<td colspan="3">
					<div style="float: left;">
						<input type="checkbox" name="totExpCheckbox" id="totExpCheckbox" onclick="showTotalExpYearsMonths(this.checked);" <%=(String)request.getAttribute("totExpCheckboxStatus") %>>
					</div> 
				<div id="totExpDiv" style="display: none; float: left; margin-left: 5px;">
					<s:select name="totExpYear" id="totExpYear" cssClass="validateRequired" cssStyle="width: 100px !important;" headerKey="0" headerValue="Select Year" 
					list="totExpYearList" listKey="totExpID" listValue="totExpName"/>&nbsp;
					<s:select name="totExpMonth" id="totExpMonth" cssClass="validateRequired" cssStyle="width: 120px !important;" 
					list="totExpMonthList" listKey="totExpID" listValue="totExpName"/>
				</div>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" nowrap="nowrap" style="font-weight: normal;">Experience working with present organization:</td>
				<td colspan="3">
					<div style="float: left;">
						<input type="checkbox" name="presentOrgExpCheckbox" id="presentOrgExpCheckbox" onclick="showPresentExpYearsMonths(this.checked);" <%=(String)request.getAttribute("presentOrgExpCheckboxStatus") %>>
					</div> 
				<div id="presentExpDiv" style="display: none; float: left; margin-left: 5px;">
					<s:select name="presentOrgExpYear" id="presentOrgExpYear" cssClass="validateRequired" cssStyle="width: 100px !important;" 
					list="totExpYearList" listKey="totExpID" listValue="totExpName"/>&nbsp;
					<s:select name="presentOrgExpMonth" id="presentOrgExpMonth" cssClass="validateRequired" cssStyle="width: 120px !important;"
					list="totExpMonthList" listKey="totExpID" listValue="totExpName"/>
				</div>	
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Potential Attribute:<sup>*</sup></td>
				<td>
					<s:select name="potentialAttribute" id="potentialAttribute" cssClass="validateRequired" 
					list="potentialAttributeList" listKey="id" listValue="name" multiple="true" size="4" value="potentialAttribID"/>
				</td>
				<td class="txtlabel alignRight" nowrap="nowrap">Threshold:<sup>*</sup></td>
				<td>
				<div style="width: 130px;">
				<s:textfield name="potentialAttThreshhold" cssClass="validateRequired" id="potentialAttThreshhold" cssStyle="color: #f6931f; font-weight: bold; width:120px !important; margin-bottom: 8px;"/>
				<br/>
				<script>
							$("#sliderPotentialAtt").slider({
								value : <%=uF.parseToInt((String)request.getAttribute("potentialAttThreshhold")) %>,
								min : 0,
								max : 100,
								step : 1,
								slide : function(event, ui) {
									$("#potentialAttThreshhold").val(ui.value);
								}
							});
							$("#potentialAttThreshhold").val($("#sliderPotentialAtt").slider("value"));
						</script>		
					<div id="sliderPotentialAtt" style="width: width:120px;"></div>
				</div>	
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Performance Attributes:<sup>*</sup></td>
				<td>
					<s:select name="performanceAttribute" id="performanceAttribute" cssClass="validateRequired" 
					list="performanceAttributeList" listKey="id" listValue="name" multiple="true" size="4" value="performanceAttribID"/>
				</td>
				<td class="txtlabel alignRight" nowrap="nowrap">Threshold:<sup>*</sup></td>
				<td>
				<div style="width: 130px;">
				<s:textfield name="performanceAttThreshhold" cssClass="validateRequired" id="performanceAttThreshhold" cssStyle="color: #f6931f; font-weight: bold; width:120px !important; margin-bottom: 8px;"/>
				<script>
							$("#sliderPerformanceAtt").slider({
								value : <%=uF.parseToInt((String)request.getAttribute("performanceAttThreshhold")) %>,
								min : 0,
								max : 100,
								step : 1,
								slide : function(event, ui) {
									$("#performanceAttThreshhold").val(ui.value);
								}
							});
							$("#performanceAttThreshhold").val($("#sliderPerformanceAtt").slider("value"));
						</script>		
					<div id="sliderPerformanceAtt" style="width: width:120px;"></div>
				</div>	
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Skills:<sup>*</sup></td>
				<td>
					<s:select name="skills" id="skills" cssClass="validateRequired" list="skillslist" listKey="skillsName" listValue="skillsName" 
						multiple="true" value="skillsID"/>
				</td>
				<td class="txtlabel alignRight" nowrap="nowrap">Threshold:<sup>*</sup></td>
				<td>
				<div style="width: 130px;">
				<s:textfield name="skillThreshhold" cssClass="validateRequired" id="skillThreshhold" cssStyle="color: #f6931f; font-weight: bold; width:120px !important; margin-bottom: 8px;"/>
				<script>
							$("#sliderSkill").slider({
								value : <%=uF.parseToInt((String)request.getAttribute("skillThreshhold")) %>,
								min : 0,
								max : 100,
								step : 1,
								slide : function(event, ui) {
									$("#skillThreshhold").val(ui.value);
								}
							});
							$("#skillThreshhold").val($("#sliderSkill").slider("value"));
						</script>		
					<div id="sliderSkill" style="width: width:120px;"></div>
				</div>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Department:<sup>*</sup></td>
				<td colspan="3">
					<s:select name="department" id="department" cssClass="validateRequired" list="departmentList" listKey="deptId" 
						listValue="deptName" multiple="true" size="4" value="departID"/>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			
			<tr>
				<td class="txtlabel alignRight" valign="top">SBU:<sup>*</sup></td>
				<td colspan="3">
					<s:select name="sbu" id="sbu" cssClass="validateRequired" list="serviceList" listKey="serviceId" listValue="serviceName" 
						multiple="true" size="4" value="serviceID"/>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Geography:<sup>*</sup></td>
				<td colspan="3">
					<s:select name="geography" id="geography" cssClass="validateRequired" list="locationList" listKey="wLocationId" 
						listValue="wLocationName" multiple="true" size="4" value="locationID"/>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			 
			<tr>
				<td class="txtlabel alignRight" valign="top">Level's Below:<sup>*</sup></td>
				<td colspan="3">
				<s:select name="levelBelow" id="levelBelow" cssClass="validateRequired" cssStyle="width: 80px;" headerKey="" headerValue="Select" 
				list="#{'1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10' }"/>
					<%-- <s:select name="levelBelow" id="levelBelow" cssClass="validateRequired" headerKey="" headerValue="Select Levels" 
					list="levelList" listKey="levelId" listValue="levelCodeName" multiple="true" size="4" value="levelID"/> --%>
				</td>
				<td class="txtlabel" nowrap="nowrap"><i> Entry Criteria</i></td>
			</tr> 
			
			<tr>
				<td></td>
				<td>
					<s:submit cssClass="btn btn-primary" value="Save" name="submit" id="btnAddNewRowOk"/>
				</td>
				<td></td>
			</tr>
	
		</table>
	</div>

</s:form>


<script>

$("#frmAddSuccessionPlanCriteria").submit(function(event){
	event.preventDefault();
	var form_data = $("#frmAddSuccessionPlanCriteria").serialize();
	var divResult = 'successionPlanDetails';
	//alert("divResult ---------- " + divResult);
	var operation = document.getElementById("operation").value;
	var orgId = document.getElementById("orgId").value;
	var criteriaId = document.getElementById("criteriaId").value;
	var desigId = document.getElementById("desigId").value;
	//alert("---------- 1");
	$.ajax({
		type :'POST',
		url  :'AddSuccessionPlanCriteria.action',
		data :form_data+"&submit=Save",
		cache:true,
		success : function(result) {
			$("#"+divResult).html(result);
		},
		error : function(result) {
			$.ajax({
				url: 'AddSuccessionPlanCriteria.action?orgId='+orgId+'&desigId='+desigId+'&operation=E&criteriaId='+criteriaId,
				cache: true,
				success: function(result){
					$("#"+divResult).html(result);
		   		}
			});
		}
	});
	
	//alert("---------- 2");
	/* $.ajax({
		url: 'AddSuccessionPlanCriteria.action?orgId='+orgId+'&desigId='+desigId+'&operation=E&criteriaId='+criteriaId,
		cache: true,
		success: function(result){
			$("#"+divResult).html(result);
   		}
	}); */
});

</script>
