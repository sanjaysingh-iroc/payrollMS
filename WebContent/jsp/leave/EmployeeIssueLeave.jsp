<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillApproval"%>
<%@page import="com.konnect.jpms.select.FillLeaveType"%>
<%@page import="com.konnect.jpms.select.FillUserType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div class="aboveform">
<script type="text/javascript"> 
$(function(){
	$("#btnAddNewRowOk").click(function(){
		/* $("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
        $("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true); */ 
        $("#formEmpIssueLeave").find('.validateRequired').filter(':hidden').prop('required',false);
		$("#formEmpIssueLeave").find('.validateRequired').filter(':visible').prop('required',true);
        
	});
	
	
	
	$("#formEmpIssueLeave_levelType").multiselect().multiselectfilter(); //{noneSelectedText: 'Select Something (required)'}
	$("#leaveAvailable").multiselect().multiselectfilter(); //{noneSelectedText: 'Select Something (required)'}
	$("#salaryHeadId").multiselect().multiselectfilter(); //{noneSelectedText: 'Select Something (required)'}
	$("#formEmpIssueLeave_combination").multiselect().multiselectfilter();
	$("#formEmpIssueLeave_prefix").multiselect().multiselectfilter();
	$("#formEmpIssueLeave_suffix").multiselect().multiselectfilter();
	$("select[name='sandwichHoliday']").multiselect().multiselectfilter();
    checkLeaveAccrual();
}); 

function cdate(){ 
	
	 if(document.forms['frmleavedate'].approvalDate[i].value=="CD"){
		  document.getElementById("other").style.display="block";
	 } 
	
}	 

/* 
function showPolicy(val) {
	var action = 'GetPolicyDetailsByAjax.action?policyid='+val;
	var el = document.getElementById("ifrmpolicy");
	el.setAttribute('src', action);
} */ 

function calculateLeave(val){
	if(val == ''){
		document.getElementById('idnoOfLeaveAnnually').value = '0';
	} else {
		var var1 = (parseFloat(val) * 12);
		document.getElementById('idnoOfLeaveAnnually').value = var1.toFixed(1);
	}
}

function showBalanceLimit(val){
	
	if(val=== 'false') {
		document.getElementById('balanceTRID').style.display='table-row';
		$("input[name='balanceLimit']").prop('required',true);
	}else{
		document.getElementById('balanceTRID').style.display='none';
		$("input[name='balanceLimit']").prop('required',false);
		$("input[name='balanceLimit']").removeClass("validateRequired");
	}
	
}

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkLeaveAccrual() {
	var isLeaveAccrual=document.getElementById("isLeaveAccrual");
	if(isLeaveAccrual.checked==true) {
		document.getElementById("idNoOfLeaveAccrualType").style.display = 'table-row';
		/* $("#accrualType").prop('required',true); */
		/* document.getElementById("idNoOfLeaveMonth").style.display = 'table-row';
		//document.getElementById("idNoOfLeaveAnnualy").style.display = 'table-row';  */
		document.getElementById("idNoOfLeaveAnnualy").style.display = 'table-row'; 
		document.getElementById("idnoOfLeaveAnnually").readOnly = true;
		/* document.getElementById("idNoOfLeaveAccSystem").style.display = 'table-row';
		document.getElementById("idNoOfLeaveAccrualFrom").style.display = 'table-row'; */
		document.getElementById("spanIdNoOfLeave").innerHTML = 'No. of Leaves (Monthly)';
		checkAccrualType();
	} else {
		document.getElementById("idNoOfLeaveAccrualType").style.display = 'none';
		/* $("#accrualType").prop('required',false); */
		document.getElementById("idNoOfLeaveMonth").style.display = 'none';
		/* $("#noOfLeaveMonthly").prop('required',false);
		$("#noOfLeaveMonthly").removeClass("validateRequired"); */
		document.getElementById("idNoOfLeaveAnnualy").style.display = 'table-row';
		document.getElementById("idnoOfLeaveAnnually").readOnly = false;
		document.getElementById("idNoOfLeaveAccSystem").style.display = 'none';
		document.getElementById("idNoOfLeaveAccrualFrom").style.display = 'none';
		document.getElementById("idDays").style.display = 'none';
		/* $("#noOfAccrueDays").prop('required',false);
		$("#noOfAccrueDays").removeClass("validateRequired"); */
		document.getElementById("idActualCalDays").style.display = 'none';
		document.getElementById("spanIdNoOfLeave").innerHTML = 'No. of Leaves (Monthly)';
		if(document.getElementById("idJoiningMonthDay")){
			document.getElementById("idJoiningMonthDay").style.display = 'none';
		}
		if(document.getElementById("idJoiningMonthLeaveBalance")){
			document.getElementById("idJoiningMonthLeaveBalance").style.display = 'none';
		}
	}
}

function checkAccrualType(){
	var accrualType = $('input[name=accrualType]:checked').val();
	if(parseInt(accrualType)==1){
		document.getElementById("idNoOfLeaveMonth").style.display = 'table-row';
		/* $("#noOfLeaveMonthly").prop('required',true); */
		document.getElementById("idNoOfLeaveAnnualy").style.display = 'table-row';
		document.getElementById("idnoOfLeaveAnnually").readOnly = true;
		document.getElementById("idNoOfLeaveAccSystem").style.display = 'table-row';
		document.getElementById("idNoOfLeaveAccrualFrom").style.display = 'table-row';
		document.getElementById("idDays").style.display = 'none';
		document.getElementById("idActualCalDays").style.display = 'none';
		document.getElementById("spanIdNoOfLeave").innerHTML = 'No. of Leaves (Monthly)';
		document.getElementById("idCarryForwardAccrualMonthly").style.display = 'table-row';
		if(document.getElementById("idJoiningMonthDay")){
			document.getElementById("idJoiningMonthDay").style.display = 'table-row';
		}
		if(document.getElementById("idJoiningMonthLeaveBalance")){
			document.getElementById("idJoiningMonthLeaveBalance").style.display = 'table-row';
		}
		
		/* $("#noOfAccrueDays").prop('required',false);
		$("#noOfAccrueDays").removeClass("validateRequired"); */
	} else if(parseInt(accrualType)==2){
		document.getElementById("idNoOfLeaveMonth").style.display = 'table-row';
		/* $("#noOfLeaveMonthly").prop('required',true); */
		document.getElementById("idnoOfLeaveAnnually").readOnly = true;
		document.getElementById("idNoOfLeaveAnnualy").style.display = 'none';
		document.getElementById("idNoOfLeaveAccSystem").style.display = 'none';
		document.getElementById("idNoOfLeaveAccrualFrom").style.display = 'none';
		
		document.getElementById("idActualCalDays").style.display = 'table-row';
		document.getElementById("spanIdNoOfLeave").innerHTML = 'No. of Leaves Accrued';
		document.getElementById("idCarryForwardAccrualMonthly").style.display = 'none';
		
		var isActualCalDays=document.getElementById("isActualCalDays");
		if(isActualCalDays.checked==true) {
			document.getElementById("idDays").style.display = 'none';
		} else {
			document.getElementById("idDays").style.display = 'table-row';
		}
		
		if(document.getElementById("idJoiningMonthDay")){
			document.getElementById("idJoiningMonthDay").style.display = 'table-row';
		}
		if(document.getElementById("idJoiningMonthLeaveBalance")){
			document.getElementById("idJoiningMonthLeaveBalance").style.display = 'table-row';
		}
		
		/* $("#noOfAccrueDays").prop('required',true); */
	} else {
		document.getElementById("idNoOfLeaveMonth").style.display = 'none';
		/* $("#noOfLeaveMonthly").prop('required',false);
		$("#noOfLeaveMonthly").removeClass("validateRequired"); */
		document.getElementById("idNoOfLeaveAnnualy").style.display = 'table-row';
		document.getElementById("idnoOfLeaveAnnually").readOnly = false;
		document.getElementById("idNoOfLeaveAccSystem").style.display = 'none';
		document.getElementById("idNoOfLeaveAccrualFrom").style.display = 'none';
		document.getElementById("idDays").style.display = 'none';
		document.getElementById("idActualCalDays").style.display = 'none';
		document.getElementById("spanIdNoOfLeave").innerHTML = 'No. of Leaves (Monthly)';
		document.getElementById("idCarryForwardAccrualMonthly").style.display = 'none';
		
		if(document.getElementById("idJoiningMonthDay")){
			document.getElementById("idJoiningMonthDay").style.display = 'table-row';
		}
		if(document.getElementById("idJoiningMonthLeaveBalance")){
			document.getElementById("idJoiningMonthLeaveBalance").style.display = 'table-row';
		}
		/* $("#noOfAccrueDays").prop('required',false);
		$("#noOfAccrueDays").removeClass("validateRequired"); */
	}
}

function checkOptionalHolidayLimit(){
	var orgId = document.getElementById("orgId").value;
	var strWlocation = document.getElementById("strLocation").value;
	var calendarYear = document.getElementById("calendarYear").value;
	var optionalLeaveLimit = document.getElementById("optionalLeaveLimit").value;
	
	if(optionalLeaveLimit!=''){
		var xmlhttp = GetXmlHttpObject();
		if (xmlhttp == null) {
			alert("Browser does not support HTTP Request");
			return false;
		} else {
	
			var xhr = $.ajax({
				url : "OptionalHolidayLimit.action?optionalLeaveLimit="+ optionalLeaveLimit+"&calendarYear="+calendarYear+"&orgId="+orgId+"&strWlocation="+strWlocation,
				cache : false,
				success : function(data) {
					if(data.length>1){
						alert(data);
						document.getElementById("optionalLeaveLimit").value = '';
						return false;
					}
				}
			});
		}
	} else {
		document.getElementById("optionalLeaveLimit").value = '';
	}
}

 function GetXmlHttpObject() {
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		return new XMLHttpRequest();
	}
	if (window.ActiveXObject) {
		// code for IE6, IE5
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
	return null;
}
 
 function checkCarriedforward(){ 
		var isCarryForward = document.getElementById("isCarryForward");
		if(isCarryForward.checked == true) {
			document.getElementById("trIsCarriedForwardLimit").style.display = 'table-row';
			
			var isCarriedForwardLimit = document.getElementById("isCarriedForwardLimit");
			if(isCarriedForwardLimit.checked == true) {
				document.getElementById("trcarriedForwardLimit").style.display = 'table-row';
			} else {
				document.getElementById("trcarriedForwardLimit").style.display = 'none';
			}
		} else {
			document.getElementById("trIsCarriedForwardLimit").style.display = 'none';
			document.getElementById("trcarriedForwardLimit").style.display = 'none';
		}
	}
 
 	function showTimePeriod(id){ 
		if(document.getElementById(id).checked==true){
			document.getElementById("trTimeFrom").style.display = "table-row";
			document.getElementById("trTimeTo").style.display = "table-row";
		} else {
			document.getElementById("trTimeFrom").style.display = "none";
			document.getElementById("trTimeTo").style.display = "none";
		}
	}

	function addTimePeriod() {
	 	var cnt1 = document.getElementById("cnt1").value;
	 	cnt1++;
	 	var divTag = document.createElement("div");
	     divTag.id = "row_timeperiod"+cnt1;
	     divTag.setAttribute('style','float:left;margin-top: 5px;');
	     divTag.innerHTML = "<div style=\"float:left\">"+
	 		"<input type=\"text\" name=\"strTimeFromDate\" class=\"validateRequired\" style=\"width:91px !important;float: left;margin-right: 10px;\"/>&nbsp;&nbsp;"+
	 		"<input type=\"text\" name=\"strTimeToDate\" class=\"validateRequired\" style=\"width:91px !important;float: left;margin-right: 10px;\"/>&nbsp;&nbsp;"+
	 		"<a href=\"javascript:void(0)\" onclick=\"removeTimePeriod("+cnt1+")\" id=\"removetimeperiodId"+cnt1+"\" class=\"close-font\" style=\"float:right;margin:0px;\"></a>"+
	 		"<a href=\"javascript:void(0)\" onclick=\"addTimePeriod()\" style=\"float:right;margin:0px;\"><i class=\"fa fa-plus-circle\"></i></a>" +
	     	"</div>";
	     document.getElementById("divTimePeriod").appendChild(divTag);
	     document.getElementById("cnt1").value=cnt1; 
	     
	     $("input[name=strTimeFromDate]").datepicker({format: 'dd/mm/yyyy'});
	     $("input[name=strTimeToDate]").datepicker({format: 'dd/mm/yyyy'});
	 }

	 function removeTimePeriod(removeId) {
	 	var remove_elem = "row_timeperiod"+removeId;
	 	var row_timeperiod = document.getElementById(remove_elem); 
	 	document.getElementById("divTimePeriod").removeChild(row_timeperiod);
	 	
	 }
	 
	 function checkLongLeave(longLeaveId){
		if(document.getElementById(longLeaveId).checked==true){
			document.getElementById('trLongLeave').style.display='table-row';
			document.getElementById('trMinimumLongLeave').style.display='table-row';
			document.getElementById('trLongLeaveGap').style.display='table-row';
		} else{ 
			document.getElementById('trLongLeave').style.display='none';
			document.getElementById('trMinimumLongLeave').style.display='none';
			document.getElementById('trLongLeaveGap').style.display='none';
		}
	 }

</script>
<%
	List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
	String policy_id = (String) request.getAttribute("policy_id");
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String param = (String) request.getAttribute("param");
	int cnt1=0;
	String empLeaveTypeId = (String) request.getAttribute("empLeaveTypeId");
	
//===start parvez date: 16-09-2022===
	Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
//===end parvez date: 16-09-2022===
	
%>

<s:form theme="simple" name="frmleavedate" action="EmployeeIssueLeave" id="formEmpIssueLeave" method="post" cssClass="formcss" >
<div style="float: left;">
	<table border="0" class="table table_no_border">
	
		<s:hidden name="empLeaveTypeId" />
		<s:hidden name="orgId" id="orgId"/>
		<s:hidden name="strLocation" id="strLocation"/>
		<input type="hidden" name="param" value="<%=param%>" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		
		<%-- 
		<tr>
			<td class="txtlabel alignRight">Level Type:<sup>*</sup></td>
			<td>
				<s:select name="levelType" listKey="levelId" cssClass="validateRequired" listValue="levelCodeName" headerKey="0" headerValue="Select Level" list="levelTypeList" key="" required="true" />
				<span class="hint">Select type of level.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> --%>
		
		<%List<String> levelType=(List<String>)request.getAttribute("levelType"); 
			if(levelType==null){
		%>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Level Type:<sup>*</sup></td>
			<td>
				<s:select name="levelType" listKey="levelId" multiple="true" cssClass="validateRequired" listValue="levelCodeName" list="levelTypeList" key="" required="true" />
				<span class="hint">Select type of level.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<%}else{ %>
			<tr>
				<td>
					<input type="hidden" name="levelType" value="<%=levelType.get(0) %>"/>
				</td>
			</tr>
		<%} %>
		
		<tr>
			<td class="txtlabel alignRight">Is Leave Accrual:</td>
			<td><s:checkbox name="isLeaveAccrual" id="isLeaveAccrual" onclick="checkLeaveAccrual()"/></td>
		</tr>
		
		<tr id="idNoOfLeaveAccrualType" style="display: none;">
			<td class="txtlabel alignRight">Accrual Type:<sup>*</sup></td>
			<td><s:radio name="accrualType" id="accrualType" cssClass="validateRequired" list="#{'1':'Month','2':'Days'}" value="defaultAccrualType" onclick="checkAccrualType()"/></td>
		</tr>
		
		<tr id="idActualCalDays" style="display: none;">
			<td class="txtlabel alignRight">Actual Calendar Days:</td>
			<td><s:checkbox name="isActualCalDays" id="isActualCalDays" onclick="checkAccrualType()"/></td>
		</tr>
		
		<tr id="idDays" style="display: none;">
			<td class="txtlabel alignRight">No. of Days:<sup>*</sup></td>
			<td><s:textfield name="noOfAccrueDays" id="noOfAccrueDays" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr id="idNoOfLeaveMonth" style="display: none;"> 
			<td class="txtlabel alignRight"><span id="spanIdNoOfLeave">No. of Leaves (Monthly):</span><sup>*</sup></td>
			<td><s:textfield name="noOfLeaveMonthly" id="noOfLeaveMonthly" label="No Of Leaves" required="true" cssClass="validateRequired" onkeyup ="calculateLeave(this.value)" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr id="idNoOfLeaveAnnualy" style="display: none;">
			<td class="txtlabel alignRight">No. of Leaves (Annually):<sup>*</sup></td>
			<td><s:textfield id="idnoOfLeaveAnnually" name="noOfLeave" label="No Of Leaves" required="true" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<% if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH))){ %>
			<tr id="idJoiningMonthDay" ><!-- style="display: none;" --> 
				<td class="txtlabel alignRight">Joining Month Day:<sup>*</sup></td>
				<td>
					<%-- <s:textfield name="joiningMonthDay" id="joiningMonthDay" label="Joining Month Day" required="true" cssClass="validateRequired" /> --%>
					<s:select theme="simple" name="joiningMonthDay" id="joiningMonthDay" label="Joining Month Day"
										cssStyle="width:100px !important;" headerKey="" headerValue="Date" cssClass="validateRequired"
										list="#{'1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9', '10':'10', '11':'11', 
	                                   '12':'12', '13':'13', '14':'14', '15':'15', '16':'16', '17':'17', '18':'18', '19':'19', '20':'20', '21':'21', 
	                                   '22':'22', '23':'23', '24':'24', '25':'25', '26':'26', '27':'27', '28':'28', '29':'29', '30':'30', '30':'30'}" />
				</td>
			</tr>
			
			<tr id="idJoiningMonthLeaveBalance" ><!-- style="display: none;" -->
				<td class="txtlabel alignRight">Joining Month Leave Balance:<sup>*</sup></td>
				<td><s:textfield id="joiningMonthLeaveBalance" name="joiningMonthLeaveBalance" label="Joining Month Leave Balance" required="true" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr>
		<% } %>
		
		<!-- ===start parvez on 29-07-2021=== -->
		<tr id="idDistributedMonth" style="display: <%=(uF.parseToBoolean((String)request.getAttribute("isLeaveOptHoliday"))) ? "table-row" : "none" %>;">
			<td class="txtlabel alignRight">Distribution (Month):</td>
			<td><s:textfield id="distributedMonth" name="distributedMonth" label="No Of Distribution Month" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		<!-- ===end parvez on 29-07-2021=== -->
		
		<tr id="idNoOfLeaveAccSystem" style="display: none;">
			<td class="txtlabel alignRight">Accrual System:<sup>*</sup></td>
			<td><s:radio name="accrualSystem" cssClass="validateRequired" list="#{'1':'Begining of month','2':'End of month','3':'Once'}"/></td>
		</tr>
		
		<tr id="idCarryForwardAccrualMonthly" style="display: none;">
			<td class="txtlabel alignRight">Is Carried Forward Accrual Monthly:</td>
			<td>
				<s:checkbox cssStyle="width:10px" name="isCarryForwardAccrualMonthly" id="isCarryForwardAccrualMonthly"/>
			</td>
		</tr>
		
		<tr id="idNoOfLeaveAccrualFrom" style="display: none;">
			<td class="txtlabel alignRight">Accrual From:<sup>*</sup></td>
			<td><s:radio name="accrualFrom" cssClass="validateRequired" list="#{'1':'Joining Date','2':'Probation Date'}"/></td>
		</tr>
		
		
		<tr id="idLeaveCalculation">
			<td class="txtlabel alignRight">Leave Calculation:<sup>*</sup></td>
			<td>
				<s:select name="approvalDate" cssClass="validateRequired" listKey="approvalId" listValue="approvalName" headerKey="" headerValue="Select Approval Type" list="approvalList" key="" required="true"/><span class="hint">Select Approval Type.<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Carried Forward:</td>
			<td>
				<s:checkbox cssStyle="width:10px" name="isCarryForward" id="isCarryForward" onclick="checkCarriedforward()"/>
			</td>
		</tr> 
	
		<tr id="trIsCarriedForwardLimit" style="display: none;">
			<td class="txtlabel alignRight">Is Carried Forward Limit:</td>
			<td>
				<s:checkbox name="isCarriedForwardLimit" id="isCarriedForwardLimit" onclick="checkCarriedforward()"/>
			</td>
		</tr> 
		
		<tr id="trcarriedForwardLimit" style="display: none;">  
			<td class="txtlabel alignRight">Carried Forward Limit:<sup>*</sup></td>
			<td><s:textfield name="carriedForwardLimit" id="carriedForwardLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Pro-rata:</td>
			<td>
				<s:checkbox cssStyle="width:10px" name="isProrata" label="Is Pro-rata" required="false"/>
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Paid:</td>
			<td>
				<s:checkbox cssStyle="width:10px" name="ispaid" label="Is Paid" required="false"/>
			</td>
		</tr>
		
		<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){ %>
			<tr>
				<td class="txtlabel alignRight">Is Long Leave:</td>
				<td>
					<%-- <s:checkbox cssStyle="width:10px" name="isLongLeave" id="isLongLeave" onclick="if(this.checked==true){document.getElementById('trLongLeave').style.display='table-row';}else{document.getElementById('trLongLeave').style.display='none';}"/> --%>
					<s:checkbox cssStyle="width:10px" name="isLongLeave" id="isLongLeave" onclick="checkLongLeave(this.id)"/>
				</td>
			</tr>
			<%
				Boolean isLong = (Boolean) request.getAttribute("isLongLeave");
				String isLongdisplay = "none";
				if (isLong) {
					isLongdisplay = "table-row";
				}
			%>
			
			<tr id="trLongLeave"  style="display: <%=isLongdisplay%>;">
				<td class="txtlabel alignRight">Long Leave Limit:<sup>*</sup></td>
				<td><s:textfield name="longLeaveLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr>
			
			<tr id="trMinimumLongLeave"  style="display: <%=isLongdisplay%>;">
				<td class="txtlabel alignRight">Minimum Long Leave Limit:<sup>*</sup></td>
				<td><s:textfield name="minimumLongLeaveLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr> 
			
			<tr id="trLongLeaveGap"  style="display: <%=isLongdisplay%>;">
				<td class="txtlabel alignRight">Minimum Gap Between Long Leave Date:<sup>*</sup></td>
				<td><s:textfield name="longLeaveGap" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr>
		<%} else{ %>
			<tr>
				<td class="txtlabel alignRight">Is Long Leave:</td>
				<td>
					<s:checkbox cssStyle="width:10px" name="isLongLeave" id="isLongLeave" onclick="if(this.checked==true){document.getElementById('trLongLeave').style.display='table-row';}else{document.getElementById('trLongLeave').style.display='none';}"/>
					
				</td>
			</tr>
			<%
				Boolean isLong = (Boolean) request.getAttribute("isLongLeave");
				String isLongdisplay = "none";
				if (isLong) {
					isLongdisplay = "table-row";
				}
			%>
			
			<tr id="trLongLeave"  style="display: <%=isLongdisplay%>;">
				<td class="txtlabel alignRight">Long Leave Limit:<sup>*</sup></td>
				<td><s:textfield name="longLeaveLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
			</tr> 
			
		<%} %>
		<tr>
			<td><input type="hidden" name="entryDate" rel="6"/></td>
		</tr>
		
		<%-- <tr>
			<td class="txtlabel alignRight">Monthly Leave Limit:<sup>*</sup></td>
			<td><s:textfield name="monthlyLeaveLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Consecutive Leave Limit:<sup>*</sup></td>
			<td><s:textfield name="consLeaveLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
		</tr> --%>
		
		<%-- <tr>
			<td class="txtlabel alignRight">Is Monthly Carried forward:</td>
			<td><s:checkbox cssStyle="width:10px" name="isMonthlyCarryForward" /></td>
		</tr> --%>
		
		<tr>
			<td class="txtlabel alignRight" valign="top">Leave Available for:<sup>*</sup></td>
			<td><s:select theme="simple" name="leaveAvailable" id="leaveAvailable"  cssClass="validateRequired" 
				list="#{'0':'All','1':'Probation','2':'Permanent','4':'Temporary','3':'Notice Period'}" size="4" multiple="true" key="" required="true" />
			</td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Is Time Period:</td>
			<td><s:checkbox name="isTimePeriod" id="isTimePeriod" onclick="showTimePeriod(this.id);"/></td>
		</tr>
		
		<%-- <tr>
			<td class="txtlabel alignRight">Is Apply Leave Limit:</td>
			<td><s:checkbox name="isApplyLeaveLimit" id="isApplyLeaveLimit"/></td>
		</tr> --%>
		
		<%
			Boolean isTimePeriod = (Boolean) request.getAttribute("isTimePeriod");
			String strTimePeriodDisplay = "none";
			if(isTimePeriod){
				strTimePeriodDisplay = "table-row";
			}
			cnt1++;
		%> 
		
		<tr id="trTimeFrom" style="display: <%=strTimePeriodDisplay %>">
			<td class="txtlabel alignRight">Time Period:<sup>*</sup></td>
			<td style="padding:0px 10px" class="txtlabel">
			Time From   
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			Time To
			</td> 
		</tr>
		
		<%if(uF.parseToInt(empLeaveTypeId) > 0){ %>
			<tr id="trTimeTo" style="display: <%=strTimePeriodDisplay %>">
				<td class="txtlabel alignRight">&nbsp;</td>
				<td>
					<div id="divTimePeriod">
					<%
						List<List<String>> alTimePeriod = (List<List<String>>)request.getAttribute("alTimePeriod");
						if(alTimePeriod == null) alTimePeriod = new ArrayList<List<String>>();
						int nAlTimePeriod = alTimePeriod.size();
						for(int i = 0; i < nAlTimePeriod; i++){
							List<String> alInner = alTimePeriod.get(i);
							cnt1++;
						%>
							<div id="row_timeperiod<%=cnt1 %>" style="float:left;margin-top: 5px;">
    							<div style="float:left;">
							 		<input type="text" name="strTimeFromDate" value="<%=alInner.get(0) %>" class="validateRequired" style="width:91px !important;float: left;margin-right: 10px;"/>&nbsp;&nbsp;
							 		<input type="text" name="strTimeToDate" value="<%=alInner.get(1) %>" class="validateRequired" style="width:91px !important;float: left;margin-right: 10px;"/>&nbsp;&nbsp;
							 		<%if(i > 0){ %>
							     		<a href="javascript:void(0)" onclick="removeTimePeriod(<%=cnt1 %>)" id="removetimeperiodId<%=cnt1 %>" class="close-font" style="float:right;margin:0px;"></a>
							     	<%} %>
							     	<a href="javascript:void(0)" onclick="addTimePeriod()" style="float:right;margin:0px;"><i class="fa fa-plus-circle"></i></a>
						 		</div>
						 	</div>
							
						<%}
						if(nAlTimePeriod == 0){
							cnt1++;
						%>
							<s:textfield name="strTimeFromDate" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>&nbsp;&nbsp;
							<s:textfield name="strTimeToDate" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;" />
							<a href="javascript:void(0)" onclick="addTimePeriod()"><i class="fa fa-plus-circle"></i></a>
						<%} %>
					</div>
				</td>
			</tr>
		<% } else { %>
			<tr id="trTimeTo" style="display: <%=strTimePeriodDisplay %>">
				<td class="txtlabel alignRight">&nbsp;</td>
				<td>
					<s:textfield name="strTimeFromDate" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>&nbsp;&nbsp;
					<s:textfield name="strTimeToDate" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;" />
					<a href="javascript:void(0)" onclick="addTimePeriod()"><i class="fa fa-plus-circle"></i></a>
					<div id="divTimePeriod"></div>
				</td>
			</tr>
		<% } %>
		
		<tr>
			<td class="txtlabel alignRight">Is Apply Leave Limit:</td>
			<td><s:checkbox name="isApplyLeaveLimit" id="isApplyLeaveLimit"/></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Does this need approval?:</td>
			<td><s:checkbox name="isApproval" id="isApproval"/></td>
		</tr>  
				
		<%-- <tr>
			<td class="txtlabel alignRight">Does this need approval?:</td>
			<td>
			<s:checkbox name="isApproval" id="isApproval"
					 onchange="if(this.checked==true){document.getElementById('leavePolicyTrId').style.display='table-row';}else{document.getElementById('leavePolicyTrId').style.display='none';}" />
			</td>
		</tr>   
		
		 <%if (uF.parseToBoolean(CF.getIsWorkFlow())) {
  				Boolean isApproval = (Boolean) request.getAttribute("isApproval");
  				String isdisplay = "none";
  				if (isApproval) {
  					isdisplay = "table-row";
  				}
  				%>
		
				<tr id="leavePolicyTrId" style="display: <%=isdisplay%>;">
					<td class="txtlabel alignRight">Policy:<sup>*</sup></td>
					<td>
					<select name="policy" id="policy" onchange="showPolicy(this.value)" class="validateRequired">
						<option value="">Select Policy</option>
									<%
										for (int i = 0; reportList != null && i < reportList.size(); i++) {
													List<String> workFlowPolicy = (List<String>) reportList.get(i);
													if (workFlowPolicy == null) workFlowPolicy = new ArrayList<String>();
		
													if (workFlowPolicy.get(7) != null && workFlowPolicy.get(7).equals(policy_id)) {
									%>
									<option value="<%=workFlowPolicy.get(7)%>" selected="selected"><%=workFlowPolicy.get(8)%></option>
									<%
										} else {
									%>
									<option value="<%=workFlowPolicy.get(7)%>"><%=workFlowPolicy.get(8)%></option>
									<%
										}
												}
									%>
							</select> 
					</td>
				</tr>
			<%} %> --%>

				<%
					String isCompensatory = (String) request.getAttribute("isCompensatory");
						if (uF.parseToBoolean(isCompensatory)) {
				%>
				<tr>
					<td class="txtlabel alignRight" valign="top">Compensate with:</td>
					<td><s:select theme="simple" name="compensateWith" listKey="leaveTypeId" headerValue="Select Leave"
							listValue="leavetypeName" headerKey="0" list="leaveList2" key="" required="true" />
					</td>
				</tr>
				
		<!-- ===start parvez date: 16-09-2022=== -->		
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE))){ %>
					<tr>
						<td class="txtlabel alignRight" valign="top">Laps Days:</td>
						<td><s:textfield name="lapsDays" /></td>	<!-- cssClass="validateRequired" -->
					</tr>
				<% } %>
		<!-- ===end parvez date: 16-09-2022=== -->		
				<%
					}
				%>

				<tr>
					<td class="txtlabel alignRight">Balance Validation Required:<sup>*</sup></td>
					<td>
						<s:radio name="balance" id="balance"  list="#{'true':'Yes','false':'No'}" value="defaultBalance" 
							cssClass="validateRequired" onclick="showBalanceLimit(this.value);"/>
					</td>
				</tr>
				<%
				String strIsBalance = (String) request.getAttribute("defaultBalance");
				String negatvieDisplay ="none";
				if(!uF.parseToBoolean(strIsBalance)){
					negatvieDisplay ="table-row";
				}
				%>		
				<tr id="balanceTRID" style="display:<%=negatvieDisplay %>;">
					<td class="txtlabel alignRight">Negative Balance Limit:<sup>*</sup></td>
					<td><s:textfield  name="balanceLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Monthly Apply Leave Limit:<sup>*</sup></td>
					<td><s:textfield  name="monthlyApplyLimit" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Combination Leave:</td>
					<td><s:select theme="simple" label="Select Leave" name="combination" listKey="leaveTypeId" size="6"
							listValue="leavetypeName" headerKey="" multiple="true" list="leaveList1" key="" required="true" /></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Select Leave Prefix:</td>
					<td><s:select theme="simple" label="Select Prefix" name="prefix" listKey="leaveTypeId" size="3" headerValue="No Leave Prefix"
							listValue="leavetypeName" headerKey="0" multiple="true" list="leaveList" key="" required="true" /></td>
				</tr>
				
				
				<tr>
					<td class="txtlabel alignRight" valign="top">Select Leave Suffix:</td>
					<td><s:select theme="simple" label="Select Suffix" name="suffix" listKey="leaveTypeId" size="3" headerValue="No Leave Suffix"
							listValue="leavetypeName" headerKey="0" multiple="true" list="leaveList" key="" required="true" />
					</td>
				</tr>

				<%-- <tr>
					<td class="txtlabel alignRight">Is it Sandwich Leave:<sup>*</sup></td>
					<td><s:checkbox name="isSandwich" id="isSandwich"
					 onchange="if(this.checked==true){document.getElementById('sandwichLeaveTypeTR').style.display='table-row';}else{document.getElementById('sandwichLeaveTypeTR').style.display='none';}" /> <span class="hint"><span class="hint-pointer">&nbsp;</span></span></td>
				</tr>
				
				<tr id="sandwichLeaveTypeTR" 
				<s:if test="isSandwich==false">
				style="display:none"
				</s:if>
				>
					<td class="txtlabel alignRight" valign="top">Select Sandwich Leave type:</td>
					<td><s:select theme="simple" label="Select Employees"
							name="sandwichHoliday" listKey="leaveTypeId" size="3" headerValue="Select Sandwich Leave type"
							listValue="leavetypeName" headerKey="0" multiple="true"
							list="leaveList" key="" required="true" /></td>
				</tr> --%>
				
				<tr>
					<td class="txtlabel alignRight">Sandwich Type:</td>
					<td>
						<s:select theme="simple"  name="isSandwich" id="isSandwich" headerKey="" headerValue="Select Type" list="#{'1':'Sandwich','2':'Ultra Sandwich'}" 
						onchange="if(this.value==''){document.getElementById('sandwichLeaveTypeTR').style.display='none';}else{document.getElementById('sandwichLeaveTypeTR').style.display='table-row';}"/>
					</td>
				</tr>
				<tr id="sandwichLeaveTypeTR" 
				<s:if test="isSandwich==null || isSandwich=='' || isSandwich==0">
				style="display:none"
				</s:if>
				>
					<td class="txtlabel alignRight" valign="top">Select Sandwich Leave type:</td>
					<td><s:select theme="simple" label="Select Employees" name="sandwichHoliday" listKey="leaveTypeId" size="3" headerValue="No Sandwich Leave type"
							listValue="leavetypeName" headerKey="0" multiple="true" list="leaveList" key="" required="true" /></td>
				</tr>
				
				<tr>
					<td class="txtlabel alignRight">Prior days that can be applied:<sup>*</sup></td>
					<%-- <td class="txtlabel alignRight">Prior intimation for more than one day leave that can be applied:<sup>*</sup></td> --%>
					<td><s:textfield name="priorApply" cssClass="validateRequired"/>
					</td>
				</tr>
				
		<!-- ===start parvez date: 15-9-2022=== -->		
				<%if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LEAVE_PRIOR_DAYS_NOTIFICATION))){ %>	
					<tr>
						<td class="txtlabel alignRight">Future days that can be applied:<sup>*</sup></td>
						<td>
							&nbsp;&nbsp;&nbsp;&nbsp;<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">No. of Leaves</span>
							<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">Notication days</span>
						</td>
					</tr>
					<tr>
						<td class="txtlabel alignRight"></td>
						<td>
							<s:textfield name="noOfLeave1" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
							<s:textfield name="priorApplyOneDayLeave" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
						</td>
					</tr>
					
					<%-- <tr>
						<td class="txtlabel alignRight"></td>
						<td>
							&nbsp;&nbsp;&nbsp;&nbsp;<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">No. of Leaves</span>
							<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">Notication days</span>
						</td>
					</tr> --%>
					<tr>
						<td class="txtlabel alignRight"></td>
						<td>
							<s:textfield name="noOfLeave2" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
							<s:textfield name="futureApply" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
						</td>
					</tr>
					
					<%-- <tr>
						<td class="txtlabel alignRight"></td>
						<td>
							&nbsp;&nbsp;&nbsp;&nbsp;<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">No. of Leaves</span>
							<span class="txtlabel" style="width:91px !important;float: left;margin-right: 10px;">Notication days</span>
						</td>
					</tr> --%>
					<tr>
						<td class="txtlabel alignRight"></td>
						<td>
							<s:textfield name="noOfLeave3" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
							<s:textfield name="futureApply1" cssClass="validateRequired" cssStyle="width:91px !important;float: left;margin-right: 10px;"/>
						</td>
					</tr>
				<% } else{ %>
					<tr>
						<%-- <td class="txtlabel alignRight">Prior intimation for one day leave that can be applied:<sup>*</sup></td> --%>
						<td class="txtlabel alignRight">Future intimation days for one day leave that can be applied:<sup>*</sup></td>
						<td><s:textfield name="priorApplyOneDayLeave" cssClass="validateRequired"/>
						</td>
					</tr>
					
					<tr>
						<%-- <td class="txtlabel alignRight">Future days that can be applied:<sup>*</sup></td> --%>
						<td class="txtlabel alignRight">Future intimation days for more than one day leave that can be applied:<sup>*</sup></td>
						<td><s:textfield name="futureApply" cssClass="validateRequired"/>
						</td>
					</tr>
				<% } %>
		<!-- ===end parvez date: 15-09-2022=== -->			
				
				<tr>
					<td class="txtlabel alignRight">Maximum future days that can be applied:<sup>*</sup></td>
					<td><s:textfield name="futureApplyMax" cssClass="validateRequired"/>
					</td>
				</tr>
				<%-- <tr>
					<td class="txtlabel alignRight">Leave Limit:</td>
					<td><s:textfield name="leaveLimit" onkeypress="return isNumberKey(event)"/>
					</td>
				</tr> --%>
				<%-- <tr>
					<td class="txtlabel alignRight">Can be taken only next year:</td>
					<td><s:checkbox name="NY" value="T" /></td>
					
				</tr> --%>
				
				<%-- <s:if test="isMaternity==true">
				<tr>
					<td class="txtlabel alignRight">Maternity Leave frequency for service:</td>
					<td><s:textfield name="maternityFrequency" /></td>
				</tr>
				</s:if> --%>
				
	<!-- ===start parvez date: 26-09-2022=== -->			
			<%
				/* System.out.println("EIL/725---isDocument=="+request.getAttribute("isDocumentRequired"));*/
				String isDocumentRequired=(String)request.getAttribute("isDocumentRequired");
					if(uF.parseToBoolean(isDocumentRequired)){
				%>
				<tr>
					<td colspan="2"><h4>Documents Required</h4><hr style="border:solid 1px #000"/></td>
  				</tr>
				
				<tr> 
					<td class="txtlabel alignRight" nowrap="nowrap">Number of Leave Days:<sup>*</sup></td>
					<td><s:textfield name="noDaysForDocument" onkeypress="return isNumberKey(event)"/></td> <!-- cssClass="validateRequired" -->
				</tr>
				
			<%	} %>
	<!-- ===end parvez date: 26-09-2022=== -->		
				
				<%
				/* System.out.println("EIL/725---isDocument=="+request.getAttribute("isDocumentRequired"));
				System.out.println("EIL/726---isLeaveEncashment=="+request.getAttribute("isLeaveEncashment")); */
				String isLeaveEncashment=(String)request.getAttribute("isLeaveEncashment");
					if(uF.parseToBoolean(isLeaveEncashment)){
				%>
				<tr>
					<td colspan="2"><h4>Encashment</h4><hr style="border:solid 1px #000"/></td>
  				</tr>
				
				<tr> 
					<td class="txtlabel alignRight" nowrap="nowrap">Min Leaves required for Encashment:<sup>*</sup></td>
					<td><s:textfield name="minLeavesRequiredEncashment" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td> 
				</tr>
				<tr> 
					<td class="txtlabel alignRight" nowrap="nowrap">Max Leave that can be applied:<sup>*</sup></td>
					<td><s:textfield name="maxLeavesAppliedEncashment" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/></td> 
				</tr>
				
				<tr> 
					<td class="txtlabel alignRight">Leave Encashment applicable for:<sup>*</sup></td>  
					<td nowrap="nowrap"><s:radio name="strEncashApplicable" id="strEncashApplicable" cssClass="validateRequired"  list="#{'1':'Previous Year','2':'Current Year'}" value="defaultSelectEncash"/></td>
				</tr> 
				
				<tr>
					<td class="txtlabel alignRight">No of Times:<sup>*</sup></td>
					<td>
						<s:textfield name="noOfTimes" id="noOfTimes" cssClass="validateRequired" onkeypress="return isNumberKey(event)"/>
					</td> 
				</tr>
				<%
					String strLeaveTypeID = (String) request.getAttribute("empLeaveTypeId");
				if(uF.parseToInt(strLeaveTypeID) > 0){
				%>
				<tr>
					<td class="txtlabel alignRight" valign="top">Salary Heads:<sup>*</sup></td>
					<td>
						<s:select theme="simple" list="salaryHeadList" listKey="salaryHeadId" listValue="salaryHeadName" id="salaryHeadId" name="salaryHeadId" size="5" multiple="true"/>
						<span class="hint">Salary Heads<span class="hint-pointer">&nbsp;</span></span>
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight">%:<sup>*</sup></td>
					<td>
						<s:textfield name="percentage" id="percentage" cssClass="validateRequired" onkeypress="return isNumberKey(event)" />
					</td>          
				</tr>
		<%	} 
					}
				%>
		
		<%
			String isLeaveOptHoliday=(String)request.getAttribute("isLeaveOptHoliday");
			if(uF.parseToBoolean(isLeaveOptHoliday)){
		%>		
			<tr>
				<td colspan="2"><h4>Optional Holidays</h4><hr style="border:solid 1px #000"/></td>
 			</tr>
			<tr> 
				<td class="txtlabel alignRight" nowrap="nowrap">Calendar Year:<sup>*</sup></td>
				<td><s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
					list="calendarYearList" key=""/>
				</td> 
			</tr>
			<tr>
				<td class="txtlabel alignRight">Optional Leave Limit<sup>*</sup></td>
				<td>
					<s:textfield name="optionalLeaveLimit" id="optionalLeaveLimit" cssClass="validateRequired" onkeyup="checkOptionalHolidayLimit();" onkeypress="return isNumberKey(event)" />
				</td>          
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td><strong>Optional Holiday Details</strong></td>
 			</tr>
 			
 			<tr>
				<td class="txtlabel alignRight">&nbsp;</td>
				<td nowrap="nowrap">
					<%
						List<Map<String, String>> alOptionalHoliday = (List<Map<String, String>>)request.getAttribute("alOptionalHoliday");
						if(alOptionalHoliday == null) alOptionalHoliday = new ArrayList<Map<String,String>>();
					%>
					<table width="100%" class="tb_style">
						<tr>
							<th>Calendar Year</th>
							<th>Leave Limit</th> 
						</tr>
						<%
							for(int i=0; alOptionalHoliday!=null && i < alOptionalHoliday.size(); i++){
								Map<String, String> hmInner = (Map<String, String>) alOptionalHoliday.get(i);
						%>
							<tr>
								<td class="alignCenter"><%=uF.showData(hmInner.get("CALENDAR_YEAR"), "")%></td>
								<td class="alignCenter"><%=uF.showData(hmInner.get("LEAVE_LIMIT"), "0")%></td>
							</tr>
						<%} %>
					</table>
				</td>          
			</tr>
		<%	} %>
				
		<tr>
			<td colspan="2" align="center"><s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/></td>
		</tr>
	
	</table>
	<input type="hidden" name="cnt1" id="cnt1" value="<%=cnt1 %>"/>
</div>
</s:form>

</div>
<script type="text/javascript">
window.setTimeout(function() {  
	checkCarriedforward();
}, 500); 	

$(function() {
    $("input[name=strTimeFromDate]").datepicker({format: 'dd/mm/yyyy'});
    $("input[name=strTimeToDate]").datepicker({format: 'dd/mm/yyyy'});
});
</script>

				