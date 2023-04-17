<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillSalaryHeads"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript">
jQuery(document).ready(function(){
	$("#submitButton").click(function(){
		$("#formAddClaimReimbursement").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formAddClaimReimbursement").find('.validateRequired').filter(':visible').prop('required',true);
    });
}); 


/* function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
} */

function checkLimitType(){
	var travelLimitType = document.getElementById("travelLimitType").value;
	if(parseInt(travelLimitType) == 2){
		document.getElementById("trLimit").style.display = 'table-row';
	}else {
		document.getElementById("trLimit").style.display = 'none';
	}
}

function checkTransportType(){
	var transportType = document.getElementById("transportType").value;
	if(parseInt(transportType) == 1){
		document.getElementById("trTrain").style.display = 'table-row';
		document.getElementById("trBus").style.display = 'none';
		document.getElementById("trFlight").style.display = 'none';
		document.getElementById("trCar").style.display = 'none';
	} else if(parseInt(transportType) == 2){
		document.getElementById("trTrain").style.display = 'none';
		document.getElementById("trBus").style.display = 'table-row';
		document.getElementById("trFlight").style.display = 'none';
		document.getElementById("trCar").style.display = 'none';
	} else if(parseInt(transportType) == 3){
		document.getElementById("trTrain").style.display = 'none';
		document.getElementById("trBus").style.display = 'none';
		document.getElementById("trFlight").style.display = 'table-row';
		document.getElementById("trCar").style.display = 'none';
	} else if(parseInt(transportType) == 4){
		document.getElementById("trTrain").style.display = 'none';
		document.getElementById("trBus").style.display = 'none';
		document.getElementById("trFlight").style.display = 'none';
		document.getElementById("trCar").style.display = 'table-row';
	} else {
		document.getElementById("trTrain").style.display = 'none';
		document.getElementById("trBus").style.display = 'none';
		document.getElementById("trFlight").style.display = 'none';
		document.getElementById("trCar").style.display = 'none';
	}
	checkLimitType();
	checkLodgingType();
	checkFoodLimitType();
	checkLaundryLimitType();
	checkSundryLimitType();
}

function checkLodgingType(){
	var lodgingType = document.getElementById("lodgingType").value;
	if(parseInt(lodgingType) == 9){
		document.getElementById("trLodgingLimitType").style.display = 'table-row';
		checkLodgingLimitType();
	}else {
		document.getElementById("trLodgingLimitType").style.display = 'none';
		document.getElementById("trLodgingLimit").style.display = 'none';
	}
}

function checkLodgingLimitType(){
	var lodgingLimitType = document.getElementById("lodgingLimitType").value;
	if(parseInt(lodgingLimitType) == 2){
		document.getElementById("trLodgingLimit").style.display = 'table-row';
	}else {
		document.getElementById("trLodgingLimit").style.display = 'none';
	}
}

function checkFoodLimitType(){
	var foodLimitType = document.getElementById("foodLimitType").value;
	if(parseInt(foodLimitType) == 2){
		document.getElementById("trFoodLimit").style.display = 'table-row';
	}else {
		document.getElementById("trFoodLimit").style.display = 'none';
	}
}

function checkLaundryLimitType(){
	var laundryLimitType = document.getElementById("laundryLimitType").value;
	if(parseInt(laundryLimitType) == 2){
		document.getElementById("trLaundryLimit").style.display = 'table-row';
	}else {
		document.getElementById("trLaundryLimit").style.display = 'none';
	}
}

function checkSundryLimitType(){
	var sundryLimitType = document.getElementById("sundryLimitType").value;
	if(parseInt(sundryLimitType) == 2){
		document.getElementById("trSundryLimit").style.display = 'table-row';
	}else {
		document.getElementById("trSundryLimit").style.display = 'none';
	}
}

</script>

<% 
	UtilityFunctions uF = new UtilityFunctions();
%>
	
<div>
	<s:form theme="simple" name="formAddClaimReimbursement" id="formAddClaimReimbursement" action="AddClaimReimbursement" method="POST" cssClass="formcss">
		<s:hidden name="reimbPolicyId"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="userscreen" id="userscreen"/>
		<s:hidden name="navigationId" id="navigationId"/>
		<s:hidden name="toPage" id="toPage"/>
		
		<table class="table table_no_border">
			<tr>
				<th colspan="2">Travel Policy Eligibility and Limits<hr style="border:solid 1px #ececec"/></th>
			</tr>
			<tr>
				<td class="alignRight">Transportation Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="transportType" id="transportType" list="#{'1':'Train','2':'Bus','3':'Flight','4':'Car'}" 
						cssClass="validateRequired" onchange="checkTransportType();"/>
				</td>
			</tr>
			
			<tr id="trTrain" style="display: none;">
				<td class="alignRight">Train:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="trainType" id="trainType" list="#{'1':'3 Tier','2':'Chair Car','3':'AC 3 Tier','4':'AC 2 Tier','5':'AC 1st Class'}" 
						cssClass="validateRequired"/>
				</td>
			</tr>
			
			<tr id="trBus" style="display: none;">
				<td class="alignRight">Bus:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="busType" id="busType" list="#{'1':'A/c Bus','2':'Non- A/c Bus'}" cssClass="validateRequired"/>
				</td>
			</tr>
			
			<tr id="trFlight" style="display: none;">
				<td class="alignRight">Flight:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="flightType" id="flightType" list="#{'1':'Economy Class','2':'Business Class'}" cssClass="validateRequired"/>
				</td>
			</tr>
			
			<tr id="trCar" style="display: none;">
				<td class="alignRight">Car:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="carType" id="carType" list="#{'1':'Cab','2':'Self Owned'}" cssClass="validateRequired"/>
				</td>
			</tr>
			
			<tr> 
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="travelLimitType" id="travelLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired" onchange="checkLimitType();"/>
				</td>
			</tr>
			 
			<tr id="trLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="travelLimit" id="travelLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per trip</td>
			</tr>
			
			<tr>
			  <td colspan="2"><hr style="border: 1px solid #ececec; margin-left: 109px; width: 370px;"></td>
			</tr>
			
			<tr id="trTransportType">
				<td class="alignRight">Lodging Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="lodgingType" id="lodgingType" cssClass="validateRequired" listKey="lodgingTypeId" 
						listValue="lodgingTypeName" headerKey="" headerValue="Select Lodging Type" list="lodgingTypeList" key="" onchange="checkLodgingType();"/>					
				</td>
			</tr>
			
			<tr id="trLodgingLimitType" style="display: none;"> 
				<td class="alignRight">Lodging Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="lodgingLimitType" id="lodgingLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired" onchange="checkLodgingLimitType();"/>
				</td>
			</tr>
			
			<tr id="trLodgingLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="lodgingLimit" id="lodgingLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per day</td>
			</tr>
			
			<tr>
				<th colspan="2">Local Conveyance<hr style="border:solid 1px #ececec"/></th>
			</tr>
			
			<tr>
				<td class="alignRight">Transportation Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="localConveyanceTranType" id="localConveyanceTranType" listKey="typeId" cssClass="validateRequired" listValue="typeName" 
						headerKey="" headerValue="Select Mode" list="localConveyanceTranTypeList" key="" required="true" />					
				</td>
			</tr>
			
			<tr>
				<td class="alignRight">Conveyance Limit:<sup>*</sup></td> 
				<td><s:textfield name="localConveyanceLimit" id="localConveyanceLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per km</td>
			</tr>
			
			<tr>
				<th colspan="2">Food &amp; Beverage (Non-Alcoholic)<hr style="border:solid 1px #ececec"/></th>
			</tr>
			
			<tr> 
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="foodLimitType" id="foodLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired" onchange="checkFoodLimitType();"/>
				</td>
			</tr>
			
			<tr id="trFoodLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="foodLimit" id="foodLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per day</td>
			</tr>
			
			<tr>
				<th colspan="2">Laundry<hr style="border:solid 1px #ececec"/></th>
			</tr>
			
			<tr> 
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="laundryLimitType" id="laundryLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired" onchange="checkLaundryLimitType();"/>
				</td>
			</tr>
			
			<tr id="trLaundryLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="laundryLimit" id="laundryLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per day</td>
			</tr>
			
			<tr>
				<th colspan="2">Other Sundry<hr style="border:solid 1px #ececec"/></th>
			</tr>
			
			<tr> 
				<td class="alignRight">Limit Type:<sup>*</sup></td>
				<td>
					<s:select theme="simple" name="sundryLimitType" id="sundryLimitType" list="#{'1':'No Limit','2':'Actual'}" cssClass="validateRequired" onchange="checkSundryLimitType();"/>
				</td>
			</tr>
			
			<tr id="trSundryLimit" style="display: none;">
				<td class="alignRight">Limit:<sup>*</sup></td> 
				<td><s:textfield name="sundryLimit" id="sundryLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)" cssStyle="text-align:right; width:72px;"></s:textfield>&nbsp;per day</td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>
					<s:submit value="Save" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"/>  
				</td>
			</tr>
		</table>
	</s:form>
</div>
<script type="text/javascript">
checkTransportType();
</script>