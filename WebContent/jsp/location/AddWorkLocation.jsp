<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillCurrency"%>
<%@page import="com.konnect.jpms.select.FillBank"%>
<%@page import="com.konnect.jpms.select.FillWeekDays"%>
<%@page import="com.konnect.jpms.select.FillWlocationType"%>
<%@page import="com.konnect.jpms.select.FillTimezones"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.select.FillCity"%>
<%@ page import="com.konnect.jpms.select.FillCountry"%>
<%@ page import="com.konnect.jpms.select.FillState"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*" %>
<%UtilityFunctions uF = new UtilityFunctions(); %>
<script>
$(function() { 
	 
	$("#btnAddNewRowOk").click(function(){
		$("#formAddNewRow").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formAddNewRow").find('.validateRequired').filter(':visible').prop('required',true);
		$(".validateNumber").prop('type','number');
		$(".validateNumber").prop('step','any');
		$(".validateEmail").prop('type', 'email');
	});
	
	var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59);
    
    $('#idStdTimeStart').datetimepicker({
		format: 'HH:mm',
		minDate: date_yest,
		defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	$('#idStdTimeEnd').data("DateTimePicker").minDate(e.date);
    });
	
	$('#idStdTimeEnd').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom,
		defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	$('#idStdTimeStart').data("DateTimePicker").maxDate(e.date);
    });
});

	
	function checkHalfDay1() {
		
		if(document.formAddNewRow.weeklyOffType1.value=='HD') {
			document.getElementById('idhalfdaylblTR1').style.display='table-row';
			document.getElementById('idhalfdaytxtTR1').style.display='table-row';
		} else {
			document.getElementById('idhalfdaylblTR1').style.display='none';
			document.getElementById('idhalfdaytxtTR1').style.display='none';
		}
	}
	
function checkHalfDay2() {
		
		if(document.formAddNewRow.weeklyOffType2.value=='HD') {
			document.getElementById('idhalfdaylblTR2').style.display='table-row';
			document.getElementById('idhalfdaytxtTR2').style.display='table-row';
		} else {
			document.getElementById('idhalfdaylblTR2').style.display='none';
			document.getElementById('idhalfdaytxtTR2').style.display='none';
		}
	}
	
function checkHalfDay3() {
	
	if(document.formAddNewRow.weeklyOffType3.value=='HD') {
		document.getElementById('idhalfdaylblTR3').style.display='table-row';
		document.getElementById('idhalfdaytxtTR3').style.display='table-row';
	} else {
		document.getElementById('idhalfdaylblTR3').style.display='none';
		document.getElementById('idhalfdaytxtTR3').style.display='none';
	}
}
	

function show_states() {
	dojo.event.topic.publish("show_states");
}
function show_cities() {
	dojo.event.topic.publish("show_cities");
}

function getState(country) {
	
	var action= 'GetStateDetails.action?type=location&country_id=' + country;
	$.ajax({
		type : 'GET',
		url:action,
		success:function(data){
			//alert("data==>"+data);
			document.getElementById('statetdid').innerHTML = data;
		}
	});
	//getContent('statetdid', action);
}
function getStateBilling(country) {
	var action= 'GetStateDetails.action?type=locationBilling&country_id=' + country;
	$.ajax({
		type : 'GET',
		url:action,
		success:function(data){
			//alert("data2==>"+data);
			document.getElementById('billingStateTdid').innerHTML = data;
			
		}
	});
	//getContent('billingStateTdid', action);
}


var documentcnt=0;
function addMachines() {
	documentcnt++;
        var table = document.getElementById('row_document_table');

        var rowCount = table.rows.length;
       
        var row = table.insertRow(rowCount);
        row.id = "row_document"+documentcnt;
        var cell1 = row.insertCell(0);
        cell1.setAttribute("class", "txtlabel alignRight");
       

       cell1.innerHTML = "<label for=\"address\">Machine Name:</label><br/>";
       
       var cell2 = row.insertCell(1);
       /* cell2.setAttribute("class", "txtlabel alignRight");
       cell2.setAttribute("style", "float:left"); */
       cell2.innerHTML = "<input type=\"text\" style=\"width:180px;\" name=\"machineName\" ></input>";
       
       documentcnt++;
       var rowCount1 = table.rows.length;
       
       var row1 = table.insertRow(rowCount1);
       row1.id = "row_document"+documentcnt;
       var cell11 = row1.insertCell(0);
       cell11.setAttribute("class", "txtlabel alignRight");
      
      cell11.innerHTML = "<label for=\"address\">Machine Serial:</label><br/>";
      
      var cell21 = row1.insertCell(1);
     /*  cell21.setAttribute("class", "txtlabel alignRight");
      cell21.setAttribute("style", "text-align: -moz-center"); */
      cell21.innerHTML = "<input type=\"text\" style=\"width:180px;float:left\" name=\"machineSerial\" ></input>"+
      "<a href=\"javascript:void(0)\" onclick=\"addMachines()\" class=\"add-font\"></a><a href=\"javascript:void(0)\"  onclick=\"removeMachines('"+row.id+"','"+row1.id+"')\"  class=\"remove-font\" ></a>";
      
}


function removeMachines(rowid,rowid1)  
{   
	//alert(rowid);
    var table =  document.getElementById('row_document_table');
    var row = document.getElementById(rowid);
    table.deleteRow(row.rowIndex);
    
    var row1 = document.getElementById(rowid1);
    table.deleteRow(row1.rowIndex);
    
}


function showBillingInfo(obj) {
	//OffAddressTR OffCityTR OffCountryTR OffStateTR OffPostcodeTR OffContactNoTR OffFaxNoTR OffEmailTR
	//alert("obj ===>> "+ obj.checked);
	
	if(obj.checked) {
		document.getElementById("billingAddress").value = document.getElementById("address").value;
		document.getElementById("billingCity").value = document.getElementById("city").value;
		document.getElementById("billingCountry").value = document.getElementById("country").value;
		var country = document.getElementById("country").value;
		var action= 'GetStateDetails.action?type=locationBilling&country_id=' + country;
		$.ajax({
			type : 'GET',
			url:action,
			success:function(data){
				//alert("data2==>"+data);
				document.getElementById('billingStateTdid').innerHTML = data;
				document.getElementById("billingState").value = document.getElementById("state").value;
			}
		});
		
		document.getElementById("billingPincode").value = document.getElementById("pincode").value;
		document.getElementById("billingContactNo").value = document.getElementById("contactNo").value;
		document.getElementById("billingFaxNo").value = document.getElementById("faxNo").value;
		document.getElementById("billingEmail").value = document.getElementById("email").value;
	} else {
		document.getElementById("billingAddress").value = '';
		document.getElementById("billingCity").value = '';
		document.getElementById("billingCountry").value = '';
		document.getElementById("billingState").value = '';
		document.getElementById("billingPincode").value = '';
		document.getElementById("billingContactNo").value = '';
		document.getElementById("billingFaxNo").value = '';
		document.getElementById("billingEmail").value = '';
	}
}


function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
      return false;

   return true;
}

function isOnlyNumberKey(evt){
	var charCode = (evt.which) ? evt.which : event.keyCode;
	if (charCode <= 31 || (charCode >= 48 && charCode <= 57)) {
		return true; 
	}
	return false;
}


function allLetter(inputtxt){   
	//alert("Name :"+inputtxt);
	var letters = /^[A-Za-z]+$/;  
	if(inputtxt.value.match(letters)) {  
		//alert('Your name have accepted : you can try another');  
		return true;  
	} else {  
		alert('Please input alphabet characters only');  
		return false;  
	}  
} 

function postcode_validate(zipcode)
{
    var regPostcode = /^([1-9])([0-9]){5}$/;

    obj = document.getElementById("status");

    if(regPostcode.test(zipcode) == false)
    {
        obj.innerHTML = "Postcode is not yet valid.";
    }
    else
    {
        obj.innerHTML = "Your India Postal Index Number is valid!";
    }

}

function phoneNo_validate(phoneNo){
     var regPhoneNo =/^[- +()]*[0-9][- +()0-9]*$/;

    obj = document.getElementById("phoneStatus");

    if(regPhoneNo.test(phoneNo) == false){
        obj.innerHTML = "Cotact Number is not valid.";
    }
}

function checkShftBaseType(){
	if(document.formAddNewRow.shiftBase.value=='2') {
		document.getElementById('trShiftBaseBuffer').style.display='table-row';
	} else {
		document.getElementById('trShiftBaseBuffer').style.display='none';
	}
}

function checkShiftBufferTime(val){
	if(val != '' && parseInt(val) > 0 && parseInt(val) <= 12){
		
	} else {
		document.formAddNewRow.shiftBufferTime.value='';
	}
}

</script>

<s:form theme="simple" id="formAddNewRow" name="formAddNewRow" action="AddWLocation" method="POST" cssClass="formcss">
	<s:hidden name="businessId"></s:hidden>
	<s:hidden name="strOrg"></s:hidden>
	<s:hidden name="userscreen"></s:hidden>
	<s:hidden name="navigationId"></s:hidden>
	<s:hidden name="toPage"></s:hidden>
	<input type="hidden" name="wlocationType" value="<%=request.getParameter("param") %>" />
	
		<table class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight"><label for="businessCode">Work Location Code:<sup>*</sup></label><br/></td>
				<td><s:textfield name="businessCode" id="businessCode" maxlength="20" cssClass="validateRequired" />
				<span class="hint">The office code should not exceed 20 characters including spaces.<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr>
			
			<tr>
				<td style="width: 30%;" class="txtlabel alignRight"><label for="businessName">Work Location Name:<sup>*</sup></label><br/></td>
				<td><s:textfield name="businessName" id="businessName" maxlength="40" cssClass="validateRequired" onclick="allLetter(text)"/>
				<span class="hint">Add a new office/branch here. The business name should not exceed 40 characters including spaces.<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr>
		
			<tr><td colspan=2 style="border-bottom:1px solid #346897">Work Location Contact Information:</td></tr>
			
			<tr>
				<td class="txtlabel alignRight"><label for="address">Work Location Address:<sup>*</sup></label><br/></td>
				<td><s:textarea name="address" id="address" cols="22" cssClass="validateRequired"/>
				<span class="hint">The Office Address<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr> 
			
			<tr>
				<td class="txtlabel alignRight"><label for="city">City:<sup>*</sup></label><br/></td>
				<td>
					<s:textfield name="city" id="city" maxlength="40" cssClass="validateRequired"/>
					<!-- <input type="checkbox" style="width:10px;height:10px" name="strIsMetro" rel="19"/> -->
					<s:checkbox cssStyle="width:10px;height:10px" name="isMetro"/>
					<span class="hint">Does this work location come under metro cities? This is required while calculating the HRA exemption for employees.<span class="hint-pointer">&nbsp;</span></span>
					Is this metro city?
					</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><label for="state">Select Country:<sup>*</sup></label><br/></td>
				<td>
			<%-- 		<select name="country" id="country" class="required">
						<% java.util.List  countryList = (java.util.List) request.getAttribute("countryList"); %>
						<% for (int i=0; i<countryList.size(); i++) { %>
						<option value=<%=((FillCountry)countryList.get(i)).getCountryId() %> > <%= ((FillCountry)countryList.get(i)).getCountryName() %></option>
						<% } %>
					</select> --%>
					
					<s:select id="country" cssClass="validateRequired" name="country" listKey="countryId" listValue="countryName" 
						headerKey="" headerValue="Select Country" list="countryList" key="" required="true" onchange="getState(this.value);"/>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight"><label for="state">Select State:<sup>*</sup></label><br/></td>
				<td id="statetdid">
					<%-- <select name="state" id="state" class="required">
						<% java.util.List  stateList = (java.util.List) request.getAttribute("stateList"); %>
						<% for (int i=0; i<stateList.size(); i++) { %>
						<option value=<%=((FillState)stateList.get(i)).getStateId() %> > <%= ((FillState)stateList.get(i)).getStateName() %></option>
						<% } %>
					</select> --%>
					
					<s:select theme="simple" id="state" cssClass="validateRequired" name="state" listKey="stateId" listValue="stateName" 
						headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
				</td>
			</tr>
			
			<tr>
			<!-- ===start parvez date: 30-07-2022=== -->
				<%-- <td class="txtlabel alignRight"><label for="pincode">Postcode:<sup>*</sup></label><br/></td> --%>
				<td class="txtlabel alignRight"><label for="pincode">Pincode:<sup>*</sup></label><br/></td>
			<!-- ===end parvez date: 30-07-2022=== -->	
				<td><s:textfield name="pincode" id="pincode" maxLength="6" onkeyup="postcode_validate(this.value)" onkeypress="return isNumberKey(event)" cssClass="validateNumber" />
				<span class="hint" id="status" >Pincode/zipcode for the work location .<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
	
			<tr>
				<td class="txtlabel alignRight"><label for="ContactNo">Contact No:<sup>*</sup></label><br/></td>
				<td><s:textfield name="contactNo" id="contactNo" onkeypress="return isNumberKey(event)" onkeyup="phoneNo_validate(this.value)" cssClass="validateRequired validateNumber"/>
				<span class="hint" id="phoneStatus">Add work location's contact number.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight"><label for="FaxNo">Fax No:</label><br/></td>
				<td><s:textfield name="faxNo" id="faxNo" />
				<span class="hint">Add fax number. (optional)<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>

			<tr>
				<td class="txtlabel alignRight"><label for="email">Email Address:<sup>*</sup></label><br/></td>
				<td><s:textfield name="email" id="email" maxlength="100" cssClass="validateRequired validateEmail"/>
				<span class="hint">The Email Address should not exceed 100 characters including spaces.<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr>
			
			<tr><td colspan=2 style="border-bottom:1px solid #346897">Work Location Billing Information:&nbsp
			<!-- <div style="float:right;"> -->
			<input type="checkbox" name="officeBillingInfoStatus" id="officeBillingInfoStatus" onclick="showBillingInfo(this);" /> Same as above<!-- </div> -->
			</td></tr>
			
			<tr id="OffAddressTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="address">Work Location Address:<sup>*</sup></label><br/></td>
				<td><s:textarea name="billingAddress" id="billingAddress" cols="22" cssClass="validateRequired"/>
				<span class="hint">The Office Address<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr> 
			
			<tr id="OffCityTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="billingcity">City:<sup>*</sup></label><br/></td>
				<td>
					<s:textfield name="billingCity" id="billingCity" maxlength="40" cssClass="validateRequired"/>
					<%-- <s:checkbox cssStyle="width:10px;height:10px" name="isMetro"/> 
					<span class="hint">Does this work location come under metro cities? This is required while calculating the HRA exemption for employees.<span class="hint-pointer">&nbsp;</span></span>
					Is this metro city?--%>
					</td>
			</tr>
			
			<tr id="OffCountryTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="billingcountry">Select Country:<sup>*</sup></label><br/></td>
				<td>
					<s:select id="billingCountry" cssClass="validateRequired" name="billingCountry" listKey="countryId" listValue="countryName" 
						headerKey="" headerValue="Select Country" list="countryList" key="" required="true" onchange="getStateBilling(this.value);"/>
				</td>
			</tr>
			
			<tr id="OffStateTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="billingstate">Select State:<sup>*</sup></label><br/></td>
				<td id="billingStateTdid">
					<s:select theme="simple" id="billingState" cssClass="validateRequired" name="billingState" listKey="stateId" listValue="stateName" 
						headerKey="" headerValue="Select State" list="stateList" key="" required="true" />
				</td>
			</tr>
			
			<tr id="OffPostcodeTR" style="display: table-row;">
			<!-- ===start parvez date: 30-07-2022=== -->	
				<%-- <td class="txtlabel alignRight"><label for="billingpincode">Postcode:<sup>*</sup></label><br/></td> --%>
				<td class="txtlabel alignRight"><label for="billingpincode">Pincode:<sup>*</sup></label><br/></td>
			<!-- ===end parvez date: 30-07-2022=== -->	
				<td><s:textfield name="billingPincode" id="billingPincode" maxLength="6" onkeyup="postcode_validate(this.value)" onkeypress="return isNumberKey(event)" cssClass=" validateNumber" />
				<span class="hint">Pincode/zipcode for the work location.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr> 
	
			<tr id="OffContactNoTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="billingContactNo">Contact No:<sup>*</sup></label><br/></td>
				<td><s:textfield name="billingContactNo" id="billingContactNo" onkeypress="return isNumberKey(event)" onkeyup="phoneNo_validate(this.value)" cssClass="validateRequired validateNumber"/>
				<span class="hint">Add work location's contact number.<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>
			
			<tr id="OffFaxNoTR" style="display: table-row;">
				<th class="txtlabel alignRight">Fax No:</th>
				<td><s:textfield name="billingFaxNo" id="billingFaxNo" />
				<span class="hint">Add fax number. (optional)<span class="hint-pointer">&nbsp;</span></span></td>
			</tr>

			<tr id="OffEmailTR" style="display: table-row;">
				<td class="txtlabel alignRight"><label for="billingemail">Email Address:<sup>*</sup></label><br/></td>
				<td><s:textfield name="billingEmail" id="billingEmail" maxlength="100" cssClass="validateRequired validateEmail"/>
				<span class="hint">The Email Address should not exceed 100 characters including spaces.<span class="hint-pointer">&nbsp;</span></span></td> 
			</tr>
			
			
			<tr><td colspan=2 style="border-bottom:1px solid #346897">Other Information:</td></tr>
			
			<tr>
				<th class="txtlabel alignRight">Select Timezone:<sup>*</sup></th>
				<td>
					<s:select id="timezone" cssClass="validateRequired" name="timezone" listKey="timezoneId" listValue="timezoneName" 
						headerKey="" headerValue="Select Timezone" list="timezoneList" key="" required="true" />
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Office Timing:<sup>*</sup></th>
				<td>
					<div class="input-box">
						<s:textfield  name="strStdTimeStart" id="idStdTimeStart" cssClass="validateRequired"/>
						<span class="unit">From:</span>
					</div>
				</td> 
			</tr>
			<tr>
				<td class="txtlabel alignRight"></td>
				<td>
					<div class="input-box">
						<s:textfield  name="strStdTimeEnd" id="idStdTimeEnd" cssClass="validateRequired"/>
						<span class="unit">To:</span>
					</div>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Shift Base Type:<sup>*</sup></th>
				<td>
					<s:select name="shiftBase" id="shiftBase" listKey="shiftBaseId" listValue="shiftBaseType" 
						headerKey="" headerValue="Select Shift Base" list="shiftBaseList" key="" cssClass="validateRequired" onchange="checkShftBaseType();"/>
				</td> 
			</tr> 
			
			<tr id="trShiftBaseBuffer" style="display:none;">
				<th class="txtlabel alignRight">Shift Base Buffer Before/After:<sup>*</sup></th>
				<td><s:textfield name="shiftBufferTime" id="shiftBufferTime" onkeyup="checkShiftBufferTime(this.value);" cssClass="validateRequired" cssStyle="width:50px !important; text-align:right;" onkeypress="return isOnlyNumberKey(event)"/></td>
			</tr>	
			
			<tr>
				<th class="txtlabel alignRight">Deduct Lunch Break (hrs):<sup>*</sup></th>
				<td><s:textfield name="strLunchBreak" cssClass="validateRequired" cssStyle="width:50px !important; text-align:right;" onkeypress="return isNumberKey(event)"/></td>
			</tr>
			
			<tr>
				<th valign="top" class="txtlabel alignRight">Adding Break Time Policy:</th>
				<td><s:checkbox name="addBreakTime" id="addBreakTime"/>Add Break Time.</td>
			</tr>
						
		</table>
		<table class="table table_no_border" id="row_document_table">
		<% List<String> machineName=(List<String>)request.getAttribute("machineName");
		 List<String> machineSerial=(List<String>)request.getAttribute("machineSerial");
		  	
			if(machineName!=null && machineName.size()!=0) {
			
		 	for(int i=0;machineName!=null && i<machineName.size(); i++) {
		 		
			%>
                  <tr id="machineName<%=machineName.get(i)%>">
                    <th style="width: 30%;" class="txtlabel alignRight">Machine Name:</th>
                    <td><input type="text" style="width: 180px;" name="machineName" value="<%=machineName.get(i) %>" ></input></td></tr>
                     
                    <tr id="machineSerial<%=machineSerial.get(i)%>"><th class="txtlabel alignRight">Machine Serial:</th>
                    <td><input type="text" style="width: 180px;float:left" name="machineSerial" value="<%=machineSerial.get(i) %>"></input>
                    <% if(i==0) { %>
                      <a href="javascript:void(0)" onclick="addMachines()" class="add-font"></a>
                      <% } else { %>
                      <a href="javascript:void(0)" onclick="addMachines()" class="add-font"></a>
                      <a href="javascript:void(0)"  onclick="removeMachines('machineName<%=machineName.get(i)%>','machineSerial<%=machineSerial.get(i)%>')"  class="remove-font" ></a>
                      <% } %>
                    </td>
                  </tr>    
		    
		 <%}
			 } else {
		 %>
                 <tr>
                  <th style="width: 30%;" class="txtlabel alignRight">Machine Name:</th>
                  <td><input type="text" style="width: 180px;"name="machineName" ></input></td>
              </tr>
                 <tr><th class="txtlabel alignRight">Machine Serial:</th>
                 <td><input type="text" style="width: 180px;float:left" name="machineSerial" ></input>
                     <a href="javascript:void(0)" onclick="addMachines()" class="add-font"></a></td>
                 </tr>    
		    	
		 <% } %>
		 
			<tr><td colspan=2 style="border-bottom:1px solid #346897">Geo-fence Settings:</td></tr>
			
			<tr>
				<th class="txtlabel alignRight">Work Location Latitude:</th>
				<td><s:textfield  name="strWLocationLatitude" id="strWLocationLatitude" /></td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Work Location Longitude:</th>
				<td><s:textfield  name="strWLocationLongitude" id="strWLocationLongitude" /></td> 
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Geo-fence Distance:</th>
				<td><s:textfield  name="strGeofenceDistance" id="strWLocationLongitude" /></td> 
			</tr>
			
	 </table>  
	 <table class="table table_no_border">
		 <tr>
			<td colspan="2" align="center">
				<s:submit  cssClass="btn btn-primary" value="Save Location" id="btnAddNewRowOk"/>
			</td>
		</tr>
	 </table>
</s:form>

		
<script>
	checkShftBaseType();
	//checkHalfDay1();
	//checkHalfDay2();
	//checkHalfDay3();
</script>