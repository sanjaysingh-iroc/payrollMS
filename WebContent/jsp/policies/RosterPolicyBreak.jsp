<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page import="com.konnect.jpms.select.FillApproval"%>
<%@ page import="com.konnect.jpms.select.FillInOut"%>
<%@ page import="com.konnect.jpms.select.FillTimeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<link rel="stylesheet" type="text/css" href="css/select/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<link rel="stylesheet" href="js_bootstrap/datepicker/bootstrap-datepicker3.css">
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="js_bootstrap/datepicker/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselectfilter.js"></script>
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script> 

jQuery(document).ready(function(){
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	$( "#strEffectiveDate" ).datepicker({format: 'dd/mm/yyyy'});
	$("#formAddNewRow_in_out").multiselect().multiselectfilter();
});
	
function getLeaveTpe(val){
	if(val=='2'){
		document.getElementById("chooseLeaveTrId").style.display="table-row";
	}else{
		document.getElementById("chooseLeaveTrId").style.display="none";
	}
}	
	
</script>
 
<div class="box-body">

	<s:form theme="simple" action="RosterPolicyBreak" method="POST" cssClass="formcss" id="formAddNewRow">
		<s:hidden name="orgId"></s:hidden>
		<s:hidden name="strWLocation"></s:hidden>
		<s:hidden name="rosterpolicyHDId" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<table border="0" class="table">
			<tr><td colspan=2><s:fielderror/></td></tr>
			
			<tr>
				<td class="txtlabel alignRight">Effective Date<sup>*</sup>:</td>
				<td><s:textfield name="strEffectiveDate" id="strEffectiveDate" cssClass="validateRequired"/><span class="hint">Effective Date.<span class="hint-pointer">&nbsp;</span></span> dd/MM/yyyy</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">IN/OUT:</td>
				<td>
					<s:select size="2" multiple="true" list="in_out_List" name="in_out" listKey="in_out_Id" listValue="in_out_Name" ></s:select>			
					<span class="hint">Is this policy applicable for coming in or going out?<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Time (in mins)<sup>*</sup>:</td>
				<td><s:textfield name="timeValue" cssClass="validateRequired"/><span class="hint">Specify time in minutes.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Break Type:</td>
				<td>
					<s:select list="breakList" name="breakType" listKey="breakTypeId" listValue="breakTypeName" ></s:select>			
					<span class="hint">Is this policy applicable for coming in or going out?<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Days<sup>*</sup>:</td>
				<td><s:textfield name="dayValue" cssClass="validateRequired"/><span class="hint">Specify time in minutes.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Month<sup>*</sup>:</td>
				<td><s:textfield name="monthValue" cssClass="validateRequired"/><span class="hint">Specify time in minutes.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr id="alignLeaveTrId"> 
				<td class="txtlabel alignRight">Align with leave? :</td>  
				<td><s:radio name="isAlignLeave" id="isAlignLeave"  list="#{'1':'No','2':'Yes'}" onclick="getLeaveTpe(this.value);"/></td>
			</tr>
			
			<%
				UtilityFunctions uF=new UtilityFunctions();
				int isAlign=uF.parseToInt((String)request.getAttribute("isAlignLeave"));
				String isdisplay="none";
				if(isAlign==2){
					isdisplay="table-row";
				} %> 
			
			<tr id="chooseLeaveTrId" style="display: <%=isdisplay %>;">
				<td class="txtlabel alignRight" valign="top">Choose Leave Type :</td>
				<td><s:select theme="simple" name="strLeaveType" id="strLeaveType" listKey="leaveTypeId" headerValue="Select Leave"
						listValue="leavetypeName" headerKey="0" list="leaveTypeList" key="" required="true" />
				</td>
			</tr>
				
			<tr>
				<td></td>
				<td><s:submit cssClass="btn btn-primary" value="Ok" id="btnAddNewRowOk"/></td>
			</tr>
			
		</table>
	</s:form>

</div>

