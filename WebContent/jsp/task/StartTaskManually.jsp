<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

$(function() {
	$("#btnOk").click(function(){
		$(".validateRequired").prop('required',true);
	});

	$(document).on("focusin", "#strTime_0", function() {
	   $(this).prop('readonly', true);  
	});

	$(document).on("focusout", "#strTime_0", function() {
	   $(this).prop('readonly', false); 
	});
		
	$('#idStdTimeStart').datetimepicker({
		format: 'HH:mm'
    }).on('dp.change', function(e){ 
    	$('#idStdTimeEnd').data("DateTimePicker").minDate(e.date);
    });
	
	$('#idStdTimeEnd').datetimepicker({
		format: 'HH:mm'
    }).on('dp.change', function(e){ 
    	$('#idStdTimeStart').data("DateTimePicker").maxDate(e.date);
    });
	
	//$( "#idStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	
	<%-- format : 'dd/mm/yyyy', minDate:"<%=request.getAttribute("pro_startDate")%>", maxDate: "<%=request.getAttribute("pro_deadline")%>" --%>

	$("#idStartDate").datepicker({ format : 'dd/mm/yyyy' });
	
	<%-- $(".idStartDate").datepicker({dateFormat:'dd/mm/yy',minDate:'<%=request.getAttribute("pro_startDate")%>', maxDate:'<%=request.getAttribute("pro_deadline")%>'}); --%>
});


function checkBillHours() {
	var arrDates = document.getElementsByName('strt_date');
	var arrTime = document.getElementsByName('strTime');
	var arrBillableTime = document.getElementsByName('strBillableTime');
	
	var arrTotal = {}; 
	 
	for(i=0; i<arrDates.length; i++) {
		var time = arrTotal["\""+arrDates[i].value+"\""];
		var totalTime = 0;
		
		if(time!=undefined) {
			totalTime = parseFloat(time) + parseFloat(arrBillableTime[i].value);	
		} else {
			totalTime = parseFloat(arrBillableTime[i].value);
		}
		
		var strbilltime = arrBillableTime[i].value;
		var strtime = arrTime[i].value;
		//alert("strtime ===> "+ strtime + "  strbilltime ==>>" + strbilltime);
		if(strtime == '') {
			strtime = 0;
			}
			if(strbilltime == '') {
				strbilltime = 0;
			}
		if(parseFloat(strbilltime) > parseFloat(strtime)) {
			alert('Billing Time is exceeding total hours.\nPlease ensure time does not exceed '+strtime+' hours.');
			document.getElementById('strBillableTime_'+i).value = '0.00';
		} else if(parseFloat(totalTime) > 24) {
			alert('Time is exceeding 24 hours for '+arrDates[i].value+".\nPlease ensure time does not exceed 24 hours.");
			document.getElementById('strBillableTime_'+i).value = '0.00';
			return;
		}
		arrTotal["\""+arrDates[i].value+"\""] = totalTime;
    }
	
}


function setBillableValue() {
	if (document.getElementById("strBillableYesNo_0").checked == 1) {
          document.getElementById("strBillableYesNoT_0").value='1';
          document.getElementById("strBillableTime_0").readOnly = false;
	} else {
		document.getElementById("strBillableTime_0").readOnly = true;
		document.getElementById("strBillableYesNoT_0").value='0';
	}
	checkAndAddBillableTime();
}


function setValue() {
	if (document.getElementById("strTaskOnOffSite_0").checked == 1) {
          document.getElementById("strTaskOnOffSiteT_0").value='1';
	} else {
		document.getElementById("strTaskOnOffSiteT_0").value='0';
	}
}


function checkAndAddBillableTime() {
	//alert("cnt :::: "+cnt +"   checkboxcnt ::: " +checkboxcnt);
	convertTimeFormat();
	//checkDateApprovedStatus();
	var checkboxval = document.getElementById("strBillableYesNoT_0").value;
	//alert("checkboxval :::: "+checkboxval);
	if(checkboxval == '1') {
		document.getElementById("strBillableTime_0").value = document.getElementById("strTime_0").value;
	} else {
		document.getElementById("strBillableTime_0").value = '0.00';
	}
}


function convertTimeFormat() {
		// var time = $("#starttime").val();

		var startDate = document.getElementById('idStartDate').value;
		var strTime = document.getElementById('idStdTimeStart').value;
		var edTime = document.getElementById('idStdTimeEnd').value;
		
		if(startDate != null && startDate != '' && strTime != null && strTime != '' && edTime != null && edTime != '') {
			var strtTime = new Date(startDate+" "+strTime+":00");
			var endTime = new Date(startDate+" "+edTime+":00");
	
			var minute = 60 * 1000,
	        hour = minute * 60,
	        day = hour * 24,
	        month = day * 30,
	        ms = Math.abs(endTime - strtTime);
			//alert("ms ===>> " + ms);
			
		    var months = parseInt(ms / month, 10);
		        ms -= months * month;
		
		    var days = parseInt(ms / day, 10);
		        ms -= days * day;
		
		    var hours = parseInt(ms / hour, 10);
				ms -= hours * hour;
		    var minutes = parseInt(ms / minute, 10);
		    if(parseInt(minutes) < 10) {
		    	minutes = "0"+minutes;
		    }
		    
		    var totTime = hours+"."+minutes;
			if(parseFloat(totTime) < 0) {
				totTime = 0;
			}
			document.getElementById("strTime_0").value = totTime;
			
			var checkboxval = document.getElementById("strBillableYesNoT_0").value;
			if(checkboxval == '1') {
				document.getElementById("strBillableTime_0").value = document.getElementById("strTime_0").value;
			} else {
				document.getElementById("strBillableTime_0").value = '0.00';
			}
			//checkDateApprovedStatus();
			checkOneDayHrs();
		}
		
	    //alert("days ===>> " + days + " -- hours ===>> " + hours + " -- minutes ===>> " + minutes);
	    
	    /* var time = document.getElementById('idStdTimeStart').value;
		alert("time ===>> " + time);
		var hrs = Number(time.match(/^(\d+)/)[1]);
		var mnts = Number(time.match(/:(\d+)/)[1]);
		alert("hrs ===>> " + hrs + " -- mnts ===>> " + mnts);
		var format = time.match(/\s(.*)$/)[1];
		if (format == "PM" && hrs < 12) hrs = hrs + 12;
		if (format == "AM" && hrs == 12) hrs = hrs - 12;
		var hours =hrs.toString();
		var minutes = mnts.toString();
		if (hrs < 10) hours = "0" + hours;
		if (mnts < 10) minutes = "0" + minutes;
		alert(hours + ":" + minutes);
		 
		 var date1 = new Date();
		date1.setHours(hours );
		date1.setMinutes(minutes);
		alert(date1);
		 
		var time = document.getElementById('idStdTimeEnd').value;
		var hrs = Number(time.match(/^(\d+)/)[1]);
		var mnts = Number(time.match(/:(\d+)/)[1]);
		var format = time.match(/\s(.*)$/)[1];
		if (format == "PM" && hrs < 12) hrs = hrs + 12;
		if (format == "AM" && hrs == 12) hrs = hrs - 12;
		var hours = hrs.toString();
		var minutes = mnts.toString();
		if (hrs < 10) hours = "0" + hours;
		if (mnts < 10) minutes = "0" + minutes;
		alert(hours+ ":" + minutes);
		var date2 = new Date();
		date2.setHours(hours );
		date2.setMinutes(minutes);
		alert(date2);
		 
		var diff = date2.getTime() - date1.getTime();
		 
		var hours = Math.floor(diff / (1000 * 60 * 60));
		diff -= hours * (1000 * 60 * 60);
		 
		var mins = Math.floor(diff / (1000 * 60));
		diff -= mins * (1000 * 60);
		alert( hours + " hours : " + mins + " minutes : " );
		
		var totTime = hours+"."+minutes;
	
		if(parseFloat(totTime) < 0) {
			totTime = 0;
		}
		document.getElementById("strTime_0").value = totTime;
		
		var checkboxval = document.getElementById("strBillableYesNoT_0").value;
		if(checkboxval == '1') {
			document.getElementById("strBillableTime_0").value = document.getElementById("strTime_0").value;
		} else {
			document.getElementById("strBillableTime_0").value = '0.00';
		}
		//checkDateApprovedStatus();
		checkOneDayHrs(); */
	}
	
	
	function checkOneDayHrs() {
		
		var strDate = document.getElementById("idStartDate").value;
		var strTime = document.getElementById("strTime_0").value;
		
		var action = 'GetOneDaySingleEmpTaskHours.action?strDate=' + strDate+'&strTime='+strTime;
		getContent('filledHrsSpan', action);
		window.setTimeout(function() {
			var filledHrs = document.getElementById("filledHrs").value;
			//alert(filledHrs);
			if(parseFloat(filledHrs) > 24) {
				alert('Actual time is more than 24 hours');
				document.getElementById("strTime_0").value = '';
				document.getElementById("strBillableTime_0").value = '';
			}
		}, 500);
		
	}
	
	function checkDateApprovedStatus() {
		//alert("dfsaf adf ");
		var strDate = document.getElementById("idStartDate").value;
		var tid = document.getElementById("tid").value;
		//alert("tid ===>> " + tid);
		//var strDateStatus = document.getElementById("hideDateApprovedStatus").value;
			//alert("strDateStatus bfr ===>> " + strDateStatus);
		getContent('dateApprovedStatus', 'CheckDateApprovedStatus.action?strDate='+strDate+'&tId='+tid);
		window.setTimeout(function() {
			var strDateStatus = document.getElementById("hideDateApprovedStatus").value;
			//alert("strDateStatus ===>> " + strDateStatus);
			
			if(strDateStatus == '1') {
				alert("Timesheet for this date is already approved.");
				return false;
			} else {
				//document.formAddNewRow.submit();
				return true;
			}
		}, 500);
		//return false;
	}

	
	function isNumberKey(evt) {
		   var charCode = (evt.which) ? evt.which : event.keyCode;
		   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
		      return false;
		
		   return true;
		}

	function checkPercentage(value) {
		//alert("value ===>> " + value);
		if(parseFloat(value) > 100) {
			alert("Please check, completion percentage greater than 100");
			document.getElementById("workStatus").value = '0';
		}
	}
	
</script>

<s:form action="TaskUpdateTime" name="formAddNewRow" id="formAddNewRow" method="post" onsubmit="return checkDateApprovedStatus()" theme="simple">
	<s:hidden name="type" value="start" />
	<input type="hidden" name="id" id="tid" value="${id}" />
	<% int proid=(Integer)request.getAttribute("pro_id"); %>
	<input type="hidden" name="pro_id" value="<%=proid %>"/>
	<table class="table">
		<tr>
			<td>Enter Date:<sup>*</sup></td>
			<td>
			<span id="filledHrsSpan"> <input type="hidden" name="filledHrs" id="filledHrs" value="0"/></span>
			<span id="dateApprovedStatus">
				<input type="hidden" name="hideDateApprovedStatus" id="hideDateApprovedStatus" value="0"/>
			</span>
			<s:textfield name="strt_date" id="idStartDate" cssClass="validateRequired" cssStyle="width:95px !important;"/>
			
			</td>
		</tr>
		
		<tr>
			<td>Enter Start Time:<sup>*</sup></td>
			<td><s:textfield name="strt_time" id="idStdTimeStart" cssClass="validateRequired" cssStyle="width:95px !important;"/></td>
		</tr>
		
		<tr>
			<td>Enter End Time:<sup>*</sup></td>
			<td><s:textfield name="end_time" id="idStdTimeEnd" cssClass="validateRequired" cssStyle="width:95px !important;"/>&nbsp;&nbsp;
				<a href="javascript:void(0);" onclick="convertTimeFormat()">Get Actual Hrs.</a>
			</td>
		</tr>
		
		<tr>
			<td>Actual Hours:<sup>*</sup></td>
			<td><s:textfield name="strTime" id="strTime_0" cssClass="validateRequired" cssStyle="width:95px !important;"/>&nbsp;&nbsp;
				<!-- <a href="javascript:void(0);" onclick="convertTimeFormat()"></a> -->
			</td>
		</tr>
		
		<tr>
			<td>Billable:</td>
			<td><input type="hidden" style="width: 30px;"  id="strBillableYesNoT_0" name="strBillableYesNoT" value="1">
				<input type="checkbox" name="strBillableYesNo" id="strBillableYesNo_0" onchange="setBillableValue()" style="width: 30px;" checked="checked">
			</td>
		</tr>
		
		<tr>
			<td>Billable Hours:</td>
			<td>
			<input type="text" value="0" id="strBillableTime_0" name="strBillableTime" style="width:65px !important;" onkeyup="checkBillHours();" onkeypress="return isNumberKey(event)">
			</td>
		</tr>
		
		<tr>
			<td>On-Site:</td>
			<td><input type="hidden" style="width: 30px;"  id="strTaskOnOffSiteT_0" name="strTaskOnOffSiteT" value="1">
				<input type="checkbox" name="strTaskOnOffSite" id="strTaskOnOffSite_0" onchange="setValue()" style="width: 30px;" checked="checked">
			</td>
		</tr>
		<tr>
			<td>Description:</td>
			<td>
				<textarea cols="50" rows="2" style="width: 220px;" name="taskDescription"></textarea>
			</td>
		</tr>
		
		<tr>
			<td>Completion Status (%)</td>
			<td><s:textfield name="workStatus" id="workStatus" cssStyle="width:95px !important;" onkeyup="checkPercentage(this.value);" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr>
			<td align="center" colspan="2">
				<s:submit value="Submit" cssClass="btn btn-primary" id="btnOk" name="submit" onclick="checkAction('');" />
				<s:submit value="100 % Complete" cssClass="btn btn-primary" id="btnOk" name="btncomplete" onclick="checkAction('Complete');" />
				<!-- <input type="button" class="input_button" value="Submit"  onclick="checkDateApprovedStatus();"/> -->
			</td>
		</tr>
	</table>    
	
</s:form>


<script type="text/javascript">

function checkAction(strAction) {
	var strParam = '';
	if(strAction != null && strAction === 'Complete') {
		strParam = 'btncomplete='+strAction;
	}
	$("#formAddNewRow").submit(function(e){
		var taskId = document.getElementById("tid").value;
		e.preventDefault();
		var form_data = $("form[name='formAddNewRow']").serialize();
	 	  $.ajax({
			url : "TaskUpdateTime.action?"+strParam,
			data: form_data,
			cache : false,
			success: function(result){
				$("#subSubDivResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'EmpViewProject.action?taskId='+taskId,
					cache: true,
					success: function(result){
						$("#subSubDivResult").html(result);
			   		}
				});
			}
		});
	 	 $("#modalInfo").hide();
	});
}

</script>