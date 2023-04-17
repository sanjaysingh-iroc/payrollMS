<%@page import="com.konnect.jpms.select.FillColour"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
jQuery(document).ready(function(){
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
}); 

function checkUncheckValue() { 
	var isLeaveEncashment=document.getElementById("isLeaveEncashment");		
	if(isLeaveEncashment.checked==true) {
		document.getElementById("minLeaveTrId").style.display='table-row';
		document.getElementById("leaveAppliTrId").style.display='table-row';
		document.getElementById("leaveNoTimesTrId").style.display='table-row';
	} else {
		document.getElementById("minLeaveTrId").style.display='none';
		document.getElementById("leaveAppliTrId").style.display='none';
		document.getElementById("leaveNoTimesTrId").style.display='none'; 		 
	}	 
}

function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

</script>

<div class="aboveform">

<s:form theme="simple" action="AddLeaveType" method="POST" id="formAddNewRow" cssClass="formcss">

<s:token></s:token> 

	<s:hidden name="leaveTypeId" />
	<s:hidden name="orgId" />
	<s:hidden name="strLocation"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
	
	<table class="table"> 
		<s:fielderror/>
		<tr>
			<td class="txtlabel alignRight">Leave Code:<sup>*</sup></td>
			<td>
				<s:textfield name="leaveCode" id="leaveCode" cssClass="validateRequired"/>
				<span class="hint">Add new leave Code here.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		<tr>
			<td class="txtlabel alignRight">Leave Type:<sup>*</sup></td>
			<td>
				<s:textfield name="leaveType" id="leaveType" cssClass="validateRequired"/>
				<span class="hint">Add new leave type here.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td class="txtlabel alignRight">Choose Colour:<sup>*</sup></td>
			<td>
			<s:select list="colourList" name="strColour" value="%{strColour}" listKey="colourValue" listValue="colourName" cssClass="validateRequired"></s:select> 			
			<span class="hint ml_25">Choose a colour for this roster. This colour will be marked in timesheets and clock entries.
				<span class="hint-pointer">&nbsp;</span>
			</span> </td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Leave Category:<sup>*</sup></td>
			<td><s:radio name="leaveCategory" id="leaveCategory" cssClass="validateRequired" list="leaveCategoryList"
					listKey="leaveCategoryId" listValue="leaveCategoryName" value="defaultLeaveCategory"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Compensatory Leave:</td>
			<td><s:checkbox cssStyle="width:10px" name="isCompensatory" /></td>
		</tr>
		 
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Documents Required:</td>
			<%-- <td><s:checkbox cssStyle="width:10px" name="isDocumentRequired" /></td> --%>
			<td>
				<table>
					<tr>
						<td><s:checkbox cssStyle="width:10px" name="isDocumentRequired" /></td>
						<td class="txtlabel alignRight" nowrap="nowrap" style="padding-left: 10px;padding-right: 5px;">Is Mandatory:</td>
						<td><s:checkbox cssStyle="width:10px" name="isMandatoryDocument" /></td>
					</tr>
				</table>
			</td>
			
			
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Encashment:</td>
			<td><s:checkbox cssStyle="width:10px" name="isLeaveEncashment" id="isLeaveEncashment"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Is this leave for optional holiday?:</td>
			<td><s:checkbox cssStyle="width:10px" name="isLeaveOptHoliday" id="isLeaveOptHoliday"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Is work from home condition:</td>
			<td><s:checkbox cssStyle="width:10px" name="isWorkFromHome" id="isWorkFromHome"/></td>
		</tr>
		<!-- Created by Dattatray Date : 28-July-21 -->
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Is Short Leave:</td>
			<td><s:checkbox cssStyle="width:10px" name="isShortLeave" id="isShortLeave"/></td>
		</tr>
<!-- ===start parvez date: 27-09-2022=== -->		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Is Bereavement Leave:</td>
			<td><s:checkbox cssStyle="width:10px" name="isBereavementLeave" id="isBereavementLeave"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight" nowrap="nowrap">Align Leave with weekend / holiday:</td>
			<td><s:checkbox cssStyle="width:10px" name="alignWeekendHoliday" id="alignWeekendHoliday"/></td>
		</tr>
<!-- ===end parvez date: 27-09-2022=== -->		
		<tr>
			<td colspan="2" align="center"><s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/></td>
		</tr>
	
	</table>
</s:form>

</div>
