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
<style>
.ui-state-default {
margin-top: 0px;
margin-bottom: 6px;
} 

#weeklyOffType1,#weeklyOff1,#weeklyOffType2,#weeklyOff2,#weeklyOffType3,#weeklyOff3,#weeklyOff3
{

margin-bottom:10px;

}


</style>
<script>
$(function () { 
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
	
	var date_yest = new Date();
    var date_tom = new Date();
    date_yest.setHours(0,0,0);
    date_tom.setHours(23,59,59); 
    
	$('#idHdTimeStart1').datetimepicker({
		format: 'HH:mm',
		minDate: date_yest,
		defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	$('#idHdTimeEnd1').data("DateTimePicker").minDate(e.date);
    });
	
	$('#idHdTimeEnd1').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom,
		defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	$('#idHdTimeStart1').data("DateTimePicker").maxDate(e.date);
    });
	
	$('#idHdTimeStart2').datetimepicker({
		format: 'HH:mm',
		minDate: date_yest,
		defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	$('#idHdTimeEnd2').data("DateTimePicker").minDate(e.date);
    });
	
	$('#idHdTimeEnd2').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom,
		defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	$('#idHdTimeStart2').data("DateTimePicker").maxDate(e.date);
    });
	
	$('#idHdTimeStart3').datetimepicker({
		format: 'HH:mm',
		minDate: date_yest,
		defaultDate: date_yest
    }).on('dp.change', function(e){ 
    	$('#idHdTimeEnd3').data("DateTimePicker").minDate(e.date);
    });
	
	$('#idHdTimeEnd3').datetimepicker({
		format: 'HH:mm',
		maxDate: date_tom,
		defaultDate: date_tom
    }).on('dp.change', function(e){ 
    	$('#idHdTimeStart3').data("DateTimePicker").maxDate(e.date);
    });
	
	$( "#weekno1" ).multiselect().multiselectfilter();
	$( "#weekno2" ).multiselect().multiselectfilter();
	$( "#weekno3" ).multiselect().multiselectfilter();
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

function getState(country){
	var action= 'GetStateDetails.action?type=location&country_id=' + country;
	getContent('statetdid', action);
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
      "<a href=\"javascript:void(0)\" onclick=\"addMachines()\" class=\"add\">Add</a><a href=\"javascript:void(0)\" style=\"float:left\" onclick=\"removeMachines('"+row.id+"','"+row1.id+"')\"  class=\"remove\" >Remove</a>";
      
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

</script>
                   
		<s:form theme="simple" id="formAddNewRow" name="formAddNewRow" action="AddWeeklyOff" method="POST" cssClass="formcss">
		<s:hidden name="businessId"></s:hidden>
		<s:hidden name="strOrg"></s:hidden>
		<input type="hidden" name="wlocationType" value="<%=request.getParameter("param") %>" />
		<s:hidden name="strLocation"></s:hidden>
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
			<table class="table">
				<tr>
					<td class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off:</label><br/></td>
					<td>
						<s:select id="weeklyOffType1" cssClass="validateRequired"
						name="weeklyOffType1" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Type"
						list="weeklyOffTypeList" onchange="checkHalfDay1();" key="" required="true" />
						<br/>
						<span id="weeklyOff1Span">
							<s:select id="weeklyOff1" cssClass="validateRequired"
							name="weeklyOff1" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Day"
							list="weeklyOffList" onchange="getRemainingWeekDays(this.value, '1');" key="" required="true" />
						</span>
						<br/>
						<s:select id="weekno1" multiple="true" size="2"
						name="weekno1" listKey="weekDayId" listValue="weekDayName" 
						list="weeklyOffList1" key="" required="true" />
					<p class="hint" style="clear: both;">Please enter the week off day for this branch (optional)<span class="hint-pointer">&nbsp;</span></p></td>
				</tr>
				
				<tr id="idhalfdaylblTR1">
					<td class="txtlabel" style="width: 25%;"></td>
					<td style="padding:0px 10px" class="txtlabel">
					Start Time   
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;
					End Time
				</td> 
				</tr>
				
				<tr id="idhalfdaytxtTR1">
					<td class="txtlabel alignRight" style="width: 25%;"><label for="address">Office Half day Timings</label><br/></td>
					<td>
					<s:textfield  name="strStdTimeStartHd1" id="idHdTimeStart1" cssStyle="width:93px !important"/> 
					<%-- <p class="hint" style="clear: both;">Please enter standard office start time for half days<span class="hint-pointer">&nbsp;</span></p> --%>
					<s:textfield  name="strStdTimeEndHd1" id="idHdTimeEnd1" cssStyle="width:93px !important"/>
					<%-- <p class="hint" style="clear: both;">Please enter standard office end time for half days<span class="hint-pointer">&nbsp;</span></p> --%>
					</td> 
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off:</label><br/></td>
					<td>
						<s:select id="weeklyOffType2"
						name="weeklyOffType2" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Type"
						list="weeklyOffTypeList" onchange="checkHalfDay2();" key="" required="true" />
						<br/>
						<span id="weeklyOff2Span">
							<s:select id="weeklyOff2"
							name="weeklyOff2" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Day"
							list="weeklyOffList" onchange="getRemainingWeekDays(this.value, '2');" key="" required="true" />
						</span>
						<br/>
						<s:select id="weekno2" multiple="true" size="2"
						name="weekno2" listKey="weekDayId" listValue="weekDayName" 
						list="weeklyOffList1" key="" required="true" />
					<p class="hint" style="clear: both;">Please enter the week off day for this branch (optional)<span class="hint-pointer">&nbsp;</span></p></td>
				</tr>
				
				<tr id="idhalfdaylblTR2">
					<td class="txtlabel" style="width: 25%;"></td>
					<td style="padding:0px 10px" class="txtlabel">
					Start Time   
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;
					End Time
					</td> 
				</tr>
				
				<tr id="idhalfdaytxtTR2">
					<td class="txtlabel alignRight" style="width: 25%;"><label for="address">Office Half day Timings</label><br/></td>
					<td>
					<s:textfield  name="strStdTimeStartHd2" id="idHdTimeStart2" cssStyle="width:93px !important"/>
					<s:textfield  name="strStdTimeEndHd2" id="idHdTimeEnd2" cssStyle="width:93px !important"/>
					<%-- <p class="hint" style="clear: both;">Please enter standard office start and end time for half days<span class="hint-pointer">&nbsp;</span></p> --%>
					</td>  
				</tr>
				
				<tr>
					<td valign="top" class="txtlabel alignRight" style="width: 25%;"><label for="BankDetails">Weekly Off:</label><br/></td>
					<td valign="top">
						<s:select id="weeklyOffType3"
						name="weeklyOffType3" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Type"
						list="weeklyOffTypeList" onchange="checkHalfDay3();" key="" required="true" />
						<br/>
						<span id="weeklyOff3Span">
							<s:select id="weeklyOff3"
							name="weeklyOff3" listKey="weekDayId" listValue="weekDayName" headerKey="" headerValue="Select Day"
							list="weeklyOffList" onchange="getRemainingWeekDays(this.value, '3');" key="" required="true" />
						</span>
						<br/>
						<s:select id="weekno3" multiple="true" size="2"
						name="weekno3" listKey="weekDayId" listValue="weekDayName" 
						list="weeklyOffList1" key="" required="true" />
					<p class="hint" style="clear: both;">Please enter the week off day for this branch (optional)<span class="hint-pointer">&nbsp;</span></p></td>
				</tr>
				
				<tr id="idhalfdaylblTR3">
					<td class="txtlabel" style="width: 25%;"></td>
					<td style="padding:0px 10px" class="txtlabel">
					Start Time   
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;
					End Time
					</td> 
				</tr>
				
				<tr id="idhalfdaytxtTR3">
					<td class="txtlabel alignRight" style="width: 25%;"><label for="address">Office Half day Timings</label><br/></td>
					<td>
					<s:textfield  name="strStdTimeStartHd3" id="idHdTimeStart3" cssStyle="width:93px !important"/>
					<s:textfield  name="strStdTimeEndHd3" id="idHdTimeEnd3" cssStyle="width:93px !important"/>
					<%-- <p class="hint" style="clear: both;">Please enter standard office start and end time for half days<span class="hint-pointer">&nbsp;</span></p> --%>
					</td> 
				</tr>
				<tr>
					<td class="txtlabel" style="width: 25%;">&nbsp;</td>
					<td>
						<s:submit  cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
					</td>
				</tr>
				
			</table>
		</s:form>

		
<script>
	checkHalfDay1();
	checkHalfDay2();
	checkHalfDay3();
</script>