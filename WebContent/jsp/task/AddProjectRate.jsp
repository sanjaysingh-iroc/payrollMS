<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#formID_0").click(function(){
		$(".validateRequired").prop('required',true);
	});
});

$(document).ready( function () {
/* 	jQuery("#formID").validationEngine();
 */	
	var locationId = document.getElementById("wLocation").value;
	getCurrencyType(locationId);
});


function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;

   return true;
}

function calculateRateAmount(value, type) {
	
	var strStdHrs = document.getElementById("strStdHrs").value;
	
	if(value == '') {
		value = '0';
	}
	if(strStdHrs == '') {
		strStdHrs = '0';
	}
	//alert("value ===>>> " + value);
	
	if(type == 'M') {
		var dayRate = 0;
		var hrRate = 0;
		dayRate = parseFloat(value) / 30;
		document.getElementById("rate").value = dayRate.toFixed(1);
		
		if(strStdHrs > 0) {
			hrRate = parseFloat(dayRate) / parseFloat(strStdHrs);
		}
		document.getElementById("rate1").value = hrRate.toFixed(1);
	} else if(type == 'D') {
		var hrRate = 0;
		var mnthRate = 0;
		mnthRate = parseFloat(value) * 30;
		document.getElementById("monthRate").value = mnthRate.toFixed(1);
		
		if(strStdHrs > 0) {
			hrRate = parseFloat(value) / parseFloat(strStdHrs);
		}
		document.getElementById("rate1").value = hrRate.toFixed(1);
	} else if(type == 'H') {
		var dayRate = parseFloat(value) * parseFloat(strStdHrs);
		document.getElementById("rate").value = dayRate.toFixed(1);
		var mnthRate = 0;
		mnthRate = parseFloat(dayRate) * 30;
		document.getElementById("monthRate").value = mnthRate.toFixed(1);
	}
}


function getCurrencyType(locationId) {
	
	//alert("locationId --->>> " + locationId);
	getContent('monthRateSpan', 'GetOrganizationCurrency.action?locationId='+locationId+'&from=AddPRate&type=');
	getContent('rateSpan', 'GetOrganizationCurrency.action?locationId='+locationId+'&from=AddPRate&type=');
	getContent('rate1Span', 'GetOrganizationCurrency.action?locationId='+locationId+'&from=AddPRate&type=');
}

</script> 
<%
	String operation = (String) request.getAttribute("operation");
%>
	<s:form id="formID" action="AddProjectRate" cssClass="formcss" method="post" theme="simple">
		<s:hidden name="service_porject_id" />
		<s:hidden name="skillId" />
		<s:hidden name="ID" />
		<s:hidden name="strStdHrs" id="strStdHrs"/>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<%if (operation != null) { %>
		<s:hidden name="operation" value="A" />
		<% } %>
		<table class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Select Level:<sup>*</sup></th>
				<td><s:select name="lavel" id="lavel" listKey="levelId" headerKey="" headerValue="Select Level"
						listValue="levelCodeName" list="levelList" key="" cssStyle="width:140px" cssClass="validateRequired"/>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Select Work Location:<sup>*</sup></th>
				<td><s:select name="wLocation" id="wLocation" listKey="wLocationId" headerKey="" headerValue="Select Work Location" 
						listValue="wLocationName" list="wLocationList" key="" cssStyle="width:140px" cssClass="validateRequired" onchange="getCurrencyType(this.value);"/>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Rate/Month:<sup>*</sup></th>
				<td>
					<span id="monthRateSpan" style="float: left; margin-right: 5px;"> </span>
					<span style="float: left;"><s:textfield name="monthRate" id="monthRate" cssClass="validateRequired" cssStyle="width: 100px;" onkeypress="return isNumberKey(event)" onkeyup="calculateRateAmount(this.value, 'M');" /></span>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Rate/Day:<sup>*</sup></th>
				<td>
					<span id="rateSpan" style="float: left; margin-right: 5px;"> </span>
					<span style="float: left;"><s:textfield name="rate" id="rate" cssClass="validateRequired" cssStyle="width: 100px;" onkeypress="return isNumberKey(event)" onkeyup="calculateRateAmount(this.value, 'D');" /></span>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Rate/Hour:<sup>*</sup></th>
				<td>
					<span id="rate1Span" style="float: left; margin-right: 5px;"> </span>
					<span style="float: left;"><s:textfield name="rate1" id="rate1" cssClass="validateRequired" cssStyle="width: 100px;" onkeypress="return isNumberKey(event)" onkeyup="calculateRateAmount(this.value, 'H');" /></span>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><s:submit value="Save Rate"	cssClass="btn btn-primary" /></td>
			</tr>
		</table>

	</s:form>
