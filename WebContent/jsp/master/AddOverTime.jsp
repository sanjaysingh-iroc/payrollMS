<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.ArrayList"%>
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
$(function(){
    $("#submitButton").click(function(){
    	$("#formAddOverTime").find('.validateRequired').filter(':hidden').prop('required',false);
	    $("#formAddOverTime").find('.validateRequired').filter(':visible').prop('required',true);
    });

    $("#idFrom").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#idTo').datepicker('setStartDate', minDate);
    });
    
    $("#idTo").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#idFrom').datepicker('setEndDate', minDate);
    });
    $("select[multiple='multiple']").multiselect().multiselectfilter();
}); 

//addLoadEvent(prepareInputsForHints);


function getSalaryHead(strLevel){
	//var strLevel=document.getElementById("strLevel").value;
	var action = 'GetSalaryHead1.action?strLevel='+ strLevel;
	getContent('tdSalaryHeadId', action);
}

function checkDayCalculation(val){
	// fixedDayCalSpanid 
	if(val=='F'){
		document.getElementById("fixedDayCalSpanid").style.display = "inline";
	}else{
		document.getElementById("fixedDayCalSpanid").style.display = "none";
	}
}
function checkStWorkingHr(val){
	//fixedStWorkingHrSpanid 
	if(val=='F'){
		document.getElementById("fixedStWorkingHrSpanid1").style.display = "inline";
	}else{
		document.getElementById("fixedStWorkingHrSpanid1").style.display = "none";
	}
}
function checkOverTimeWHrs(val){
	//fixedOverTimeWHrsSpanid 
	if(val=='F'){
		document.getElementById("fixedOverTimeWHrsSpanid").style.display = "block";
	}else{
		document.getElementById("fixedOverTimeWHrsSpanid").style.display = "none";
	}
}

function checkCalBasis(calBasis){
	var strOTPType = document.getElementById('strOverTimePaymentType').value;
	
	if(calBasis == 'H' && strOTPType == 'P'){
		document.getElementById("trDayCal").style.display = "table-row";
		document.getElementById("trSWH").style.display = "table-row";
		document.getElementById("trBufferRSTime").style.display = "table-row";
		document.getElementById("trMinHrs").style.display = "table-row";
		document.getElementById("trRoundOff").style.display = "table-row";
		
		document.getElementById("trSalaryHead").style.display = "table-row";
		document.getElementById("trPtypeCal").style.display = "table-row";
		document.getElementById("ptypeCalId").innerHTML='%';
		
		document.getElementById("trOvertimePayType").style.display = "table-row";
		document.getElementById("trRoundOffSlab").style.display = "none";
	} else if(calBasis == 'H' && strOTPType == 'A'){
		document.getElementById("trDayCal").style.display = "none";
		document.getElementById("trSWH").style.display = "table-row";
		document.getElementById("trBufferRSTime").style.display = "table-row";
		document.getElementById("trMinHrs").style.display = "table-row";
		document.getElementById("trRoundOff").style.display = "table-row";
		
		document.getElementById("trSalaryHead").style.display = "none";
		document.getElementById("trPtypeCal").style.display = "table-row";
		document.getElementById("ptypeCalId").innerHTML='Amount';
		
		document.getElementById("trOvertimePayType").style.display = "table-row";
		document.getElementById("trRoundOffSlab").style.display = "none";
	} else if(calBasis == 'FD' && strOTPType == 'P'){
		document.getElementById("trDayCal").style.display = "none";
		document.getElementById("trSWH").style.display = "none";
		document.getElementById("trBufferRSTime").style.display = "none";
		document.getElementById("trMinHrs").style.display = "none";
		document.getElementById("trRoundOff").style.display = "none";
		
		document.getElementById("trSalaryHead").style.display = "table-row";
		document.getElementById("trPtypeCal").style.display = "table-row";
		document.getElementById("ptypeCalId").innerHTML='%';
		
		document.getElementById("trOvertimePayType").style.display = "table-row";
		document.getElementById("trRoundOffSlab").style.display = "none";
	} else if(calBasis == 'M'){
		document.getElementById("trDayCal").style.display = "none";
		document.getElementById("trSWH").style.display = "table-row";
		document.getElementById("trBufferRSTime").style.display = "table-row";
		document.getElementById("trMinHrs").style.display = "none";
		document.getElementById("trRoundOff").style.display = "none";
		
		document.getElementById("trSalaryHead").style.display = "none";
		document.getElementById("trPtypeCal").style.display = "none";
		document.getElementById("ptypeCalId").innerHTML='';
		
		document.getElementById("trOvertimePayType").style.display = "none";
		document.getElementById("trRoundOffSlab").style.display = "table-row";
	} else {
		document.getElementById("trDayCal").style.display = "none";
		document.getElementById("trSWH").style.display = "none";
		document.getElementById("trBufferRSTime").style.display = "none";
		document.getElementById("trMinHrs").style.display = "none";
		document.getElementById("trRoundOff").style.display = "none";
		
		document.getElementById("trSalaryHead").style.display = "none";
		document.getElementById("trPtypeCal").style.display = "table-row";
		document.getElementById("ptypeCalId").innerHTML='Amount';
		
		document.getElementById("trOvertimePayType").style.display = "table-row";
		document.getElementById("trRoundOffSlab").style.display = "none";
	}	
}

function checkPaymentType(ptype){
	var strCalBasis = document.getElementsByName('calBasis');
	if(strCalBasis[1].checked && strCalBasis[1].value == 'H'){
		if(ptype == 'P'){
			document.getElementById("trSalaryHead").style.display = "table-row";
			document.getElementById("ptypeCalId").innerHTML='%';
			
			document.getElementById("trDayCal").style.display = "table-row";
			document.getElementById("trSWH").style.display = "table-row";
			document.getElementById("trBufferRSTime").style.display = "table-row";
			document.getElementById("trMinHrs").style.display = "table-row";
			document.getElementById("trRoundOff").style.display = "table-row";
			document.getElementById("trRoundOffSlab").style.display = "none";
		} else {
			document.getElementById("trSalaryHead").style.display = "none";
			document.getElementById("ptypeCalId").innerHTML='Amount';
			
			document.getElementById("trDayCal").style.display = "none";
			document.getElementById("trSWH").style.display = "table-row";
			document.getElementById("trBufferRSTime").style.display = "table-row";
			document.getElementById("trMinHrs").style.display = "table-row";
			document.getElementById("trRoundOff").style.display = "table-row";
			document.getElementById("trRoundOffSlab").style.display = "none";
		}
		
	}else{
		if(ptype == 'P'){
			document.getElementById("trSalaryHead").style.display = "table-row";
			document.getElementById("ptypeCalId").innerHTML='%';
		} else {
			document.getElementById("trSalaryHead").style.display = "none";
			document.getElementById("ptypeCalId").innerHTML='Amount';
		}
		
		document.getElementById("trDayCal").style.display = "none";
		document.getElementById("trSWH").style.display = "none";
		document.getElementById("trBufferRSTime").style.display = "none";
		document.getElementById("trMinHrs").style.display = "none";
		document.getElementById("trRoundOff").style.display = "none";
		document.getElementById("trRoundOffSlab").style.display = "none";
	}
}

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
       return false;
    }
    return true;
}

function addMinuteSlab() {
	var cnt = document.getElementById("cnt").value;
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_minuteslab"+cnt;
    divTag.setAttribute('style','padding: 10px 0px');
    divTag.innerHTML =  "<div style=\"float:left\">"+
    					"Min:<input type=\"text\" name=\"strMinMinute\" style=\"width: 31px !important; text-align: right;\" onkeypress=\"return isNumberKey(event)\"/>min&nbsp;&nbsp;"+
    					"Max:<input type=\"text\" name=\"strMaxMinute\" style=\"width: 31px !important; text-align: right;\" onkeypress=\"return isNumberKey(event)\"/>min&nbsp;&nbsp;"+
    					"=&nbsp;&nbsp;"+
    					"<select name=\"strRoundOffMinunte\" style=\"width: 85px !important;\">"+
						"<option value=\"0\">0 Minute</option>"+
						"<option value=\"15\">15 Minute</option>"+
						"<option value=\"30\">30 Minute</option>"+
						"<option value=\"45\">45 Minute</option>"+
						"<option value=\"60\">1 Hour</option>"+
						"</select></div>"+
    					"<a href=\"javascript:void(0)\" onclick=\"addMinuteSlab()\" class=\"add-font\"></a>"+	
    					"<a href=\"javascript:void(0)\" onclick=\"removeMinuteSlab("+cnt+")\" id=\"removeMinuteSlabId"+cnt+"\" class=\"remove-font\" style=\"float:left;margin:0px;\"></a>";
    document.getElementById("div_minuteslab").appendChild(divTag);
    document.getElementById("cnt").value = cnt;
}

function removeMinuteSlab(removeId) {
	var remove_elem = "row_minuteslab"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById("div_minuteslab").removeChild(row_kra);
}

</script>


	<% 	
		UtilityFunctions uF = new UtilityFunctions();
		String operation = (String)request.getAttribute("operation");
		Map<String,String> hmOverTime = (Map<String,String>)request.getAttribute("hmOverTime");
		if(hmOverTime==null) hmOverTime=new HashMap<String,String>();
		List<String> headList = (List<String>)request.getAttribute("headList");
		
		String level_id=(String)request.getAttribute("LEVEL_ID");
		
		List<Map<String, String>> alMinuteSlab = (List<Map<String, String>>)request.getAttribute("alMinuteSlab");
		if(alMinuteSlab == null) alMinuteSlab = new ArrayList<Map<String,String>>();
	
	%>

<div>
	<s:form  theme="simple"  name="formAddOverTime" id="formAddOverTime" action="AddOverTime" method="POST" cssClass="formcss">
		<s:hidden name="id"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="org_id"></s:hidden>
		<s:hidden name="strLevel"></s:hidden>
		<s:hidden name="strOverTimeType"></s:hidden>
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
	
		<table border="0" class="table table_no_border">
			<tr>
				<td class="txtlabel alignRight">Overtime Code:<sup>*</sup></td> 
				<td>
					<input type="text" name="strOvertimeCode" id="overtimeCode" class="validateRequired" value="<%=hmOverTime.get("OVERTIME_CODE")!=null ? hmOverTime.get("OVERTIME_CODE") : "" %>" /> 
					<span class="hint">Overtime Code<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
		
			<tr>
				<td class="txtlabel alignRight">Overtime Description:<sup>*</sup></td>
				<td>
					<input type="text" name="strOvertimeDescription" id="overtimeDescription" class="validateRequired" value="<%=hmOverTime.get("OVERTIME_DESCRIPTION")!=null ? hmOverTime.get("OVERTIME_DESCRIPTION") : ""%>"/> 
					<span class="hint">Overtime Description<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Effective From:<sup>*</sup></td>
				<td>
					<input type="text" name="strFrom" id="idFrom" class="validateRequired" value="<%=hmOverTime.get("DATE_FROM")!=null ? hmOverTime.get("DATE_FROM") : ""%>"/> 
					<span class="hint">From<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Effective To:<sup>*</sup></td>
				<td>
					<input type="text" name="strTo" id="idTo" class="validateRequired"value="<%=hmOverTime.get("DATE_TO")!=null ? hmOverTime.get("DATE_TO") : ""%>"/> 
					<span class="hint">To<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<%-- <tr>
				<td class="txtlabel alignRight">Overtime Type:</td>
				<td>
				<s:select theme="simple" name="strOverTimeType" id="strOverTimeType"
							list="#{'PH':'Public Holiday','BH':'Weekend','EH':'Extra Hour worked'}" />
	
						<span class="hint">Overtime Type <span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr> --%>
			
			<tr>
				<td class="txtlabel alignRight" valign="top">Monthly Calculation Basis:<sup>*</sup></td>
				<td>
					<s:radio name="calBasis" id="calBasis"  list="#{'FD':'Daily','H':'Hourly','M':'Minute'}" value="defaultCalBasis" cssClass="validateRequired" onclick="checkCalBasis(this.value);"/>				
				</td>
			</tr>
			
			<%
			String strOTPType = (String) request.getAttribute("strOverTimePaymentType");
			String strCalBasis = (String) request.getAttribute("calBasis");
		
			String strOTPDisplay = "none";
			String strOTPMsg = "Amount";
			String hDisplay = "none";
			String hWHDisplay = "none";
			String hDayCalDisplay = "none";
			String strOvertimePayTypeDisplay = "none";
			String strPtypeCal = "none";
			String hRoundOff = "none";
			String hRoundOffSlab = "none";
			String hBuffer = "none";
			if(strCalBasis!=null && strCalBasis.equals("H") && (strOTPType !=null && strOTPType.equals("P"))){
				strOvertimePayTypeDisplay = "table-row";
				strOTPDisplay = "table-row";
				strPtypeCal = "table-row";
				strOTPMsg = "%";
				hWHDisplay = "table-row";
				hDisplay = "table-row";
				hDayCalDisplay = "table-row";
				hRoundOff = "table-row";
				hRoundOffSlab = "none";
				hBuffer = "table-row";
			} else if(strCalBasis!=null && strCalBasis.equals("H") && (strOTPType !=null && strOTPType.equals("A"))){
				strOvertimePayTypeDisplay = "table-row";
				strOTPDisplay = "none";
				strPtypeCal = "table-row";
				strOTPMsg = "Amount";
				hWHDisplay = "table-row";
				hDisplay = "table-row";
				hDayCalDisplay = "none";
				hRoundOff = "table-row";
				hRoundOffSlab = "none";
				hBuffer = "table-row";
			} else if(strCalBasis!=null && strCalBasis.equals("FD") && (strOTPType !=null && strOTPType.equals("P"))){
				strOvertimePayTypeDisplay = "table-row";
				strOTPDisplay = "table-row";
				strPtypeCal = "table-row";
				strOTPMsg = "%";
				hWHDisplay = "none";
				hDisplay = "none";
				hDayCalDisplay = "none";
				hRoundOff = "none";
				hRoundOffSlab = "none";
				hBuffer = "none";
			} else if(strCalBasis!=null && strCalBasis.equals("M")){
				strOvertimePayTypeDisplay = "none";
				strOTPDisplay = "none";
				strPtypeCal = "none";
				strOTPMsg = "";
				hWHDisplay = "table-row";
				hDisplay = "none";
				hDayCalDisplay = "none";
				hRoundOff = "none";
				hRoundOffSlab = "table-row";
				hBuffer = "table-row";
			} else {
				strOvertimePayTypeDisplay = "table-row";
				strOTPDisplay = "none";
				strPtypeCal = "table-row";
				strOTPMsg = "Amount";
				hWHDisplay = "none";
				hDisplay = "none";
				hDayCalDisplay = "none";
				hRoundOff = "none";
				hRoundOffSlab = "none";
				hBuffer = "none";
			}
		%>
			
			<tr id="trOvertimePayType" style="display: <%=strOvertimePayTypeDisplay %>">
			<td class="txtlabel alignRight">Overtime Payment Type:<sup>*</sup></td>
			<td>
				<s:select theme="simple" name="strOverTimePaymentType" id="strOverTimePaymentType"
						list="#{'A':'Fixed Amount','P':'Percent'}" onchange="checkPaymentType(this.value);" cssClass="validateRequired"/> 
				<span class="hint">Overtime Payment Type<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr id="trSalaryHead" style="display: <%=strOTPDisplay %>">
			<td class="txtlabel alignRight" valign="top">Salary Head:<sup>*</sup></td>
			<td>
				<select rel="7" name="strSalaryHead" id="strSalaryHead" multiple="multiple" size="4" class="validateRequired">
					<% java.util.List  salaryHeadList = (java.util.List) request.getAttribute("salaryHeadList"); %>
					<% for (int i=0; i<salaryHeadList.size(); i++) { %>
					<option value=<%= ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId() %> <%if(headList!=null && headList.contains(((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadId())){ %>selected<%} %>> <%= ((FillSalaryHeads)salaryHeadList.get(i)).getSalaryHeadName() %></option>
					<% } %>
				</select>
				<span class="hint">Salary Type<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr> 
		
		<tr id="trPtypeCal" style="display: <%=strPtypeCal %>">
			<td class="txtlabel alignRight"><span id="ptypeCalId"><%=strOTPMsg %></span>:<sup>*</sup></td>
			<td>
				<input type="text" name="strAmount" id="strAmount" class="validateRequired" value="<%=hmOverTime.get("OVERTIME_PAYMENT_AMOUNT")!=null ? hmOverTime.get("OVERTIME_PAYMENT_AMOUNT") : ""%>" onkeypress="return isNumberKey(event)"/> 
				<span class="hint">Amount / Percentage<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
		
		<tr id="trDayCal" style="display:<%=hDayCalDisplay %>">
			<td class="txtlabel alignRight">Days Calculation:<sup>*</sup></td>
			<td>
			<div>
				<s:select theme="simple" name="dayCalculation" id="dayCalculation"  cssClass="validateRequired" list="#{'AMD':'Actual Month Days','AWD':'Actual Working Days','F':'Fixed Days'}" 
					onchange="checkDayCalculation(this.value);"/>
                 <% String dayCalDisplay=(String)request.getAttribute("dayCalculation")!=null && ((String)request.getAttribute("dayCalculation")).equals("F") ? "block" : "none";  %>
				<span id="fixedDayCalSpanid" style="display:<%=dayCalDisplay %>; float: right; margin-right: 10px;" >
				<input type="text" name="fixedDayCal" id="fixedDayCal" style="width: 50px !important;" value="<%=hmOverTime.get("FIXED_DAY_CALCULATION")!=null ? hmOverTime.get("FIXED_DAY_CALCULATION") : ""%>" onkeypress="return isNumberKey(event)"/> days.
				</span>
				</div>
			</td> 
		</tr>  
		
		<tr id="trSWH" style="display:<%=hWHDisplay %>">
			<td class="txtlabel alignRight">Standard Working Hours:<sup>*</sup></td>
			<td>
				<s:select theme="simple" name="stWorkingHr" id="stWorkingHr"  cssClass="validateRequired"
                                        list="#{'RH':'Roster Hours','SWH':'Standard Working Hours','F':'Fixed Hours'}"  onchange="checkStWorkingHr(this.value);"/>
                 <% String stWorkingHrDisplay=(String)request.getAttribute("stWorkingHr")!=null && ((String)request.getAttribute("stWorkingHr")).equals("F") ? "block" : "none";  %> 
				<span id="fixedStWorkingHrSpanid1" style="display:<%=stWorkingHrDisplay %>; float: right; margin-right: 10px;">
				<input type="text" name="fixedStWorkingHr" id="fixedStWorkingHr" style="width: 50px !important;" value="<%=hmOverTime.get("FIXED_STWKG_HOURS")!=null ? hmOverTime.get("FIXED_STWKG_HOURS") : ""%>" onkeypress="return isNumberKey(event)"/> Hrs.
				</span>
			</td>
		</tr> 
		<%-- <tr id="fixedStWorkingHrSpanid1" style="display: none;">
			<td class="txtlabel alignRight">&nbsp;</td>
			<td>
				<input type="text" name="fixedStWorkingHr" id="fixedStWorkingHr" style="width: 50px;" value="<%=hmOverTime.get("FIXED_STWKG_HOURS")!=null ? hmOverTime.get("FIXED_STWKG_HOURS") : ""%>"/>
			</td>
		</tr>  --%>
		
		<tr id="trBufferRSTime" style="display:<%=hBuffer %>"> 
			<td class="txtlabel alignRight" nowrap="nowrap">Buffer After Roster/Standard Time:<sup>*</sup>
				<%-- <input type="radio" name="standardTime" id="standardTime" value="BST"  <%if(hmOverTime.get("STANDARD_TIME")==null || hmOverTime.get("STANDARD_TIME").equals("BST")){ %> checked <%} %>/> --%>
			</td>
			<td>
				<input type="text" name="bufferStandardTime" id="bufferStandardTime" style="width: 50px !important;" class="validateRequired" value="<%=hmOverTime.get("BUFFER_STANDARD_TIME")!=null ? hmOverTime.get("BUFFER_STANDARD_TIME") : ""%>" onkeypress="return isNumberKey(event)"/> 
			</td>
		</tr>
		<%-- <tr> 
			<td class="txtlabel alignRight" nowrap="nowrap">Overtime Working Hours
				<input type="radio" name="standardTime" id="standardTime" value="OT" <%if(hmOverTime.get("STANDARD_TIME")!=null && hmOverTime.get("STANDARD_TIME").equals("OT")){ %> checked <%} %>/>:
			</td>
			<td>
				<s:select theme="simple" name="overTimeWHrs" id="overTimeWHrs" headerKey="AR"
                                        headerValue="After Roster"
                                        list="#{'AST':'After Standard Time','F':'Fixed'}" onchange="checkOverTimeWHrs(this.value);"/>
                 <% String overTimeWHrsDisplay=(String)request.getAttribute("overTimeWHrs")!=null && ((String)request.getAttribute("overTimeWHrs")).equals("F") ? "block" : "none";  %>                       
                <span id="fixedOverTimeWHrsSpanid" style="display:<%=overTimeWHrsDisplay %>;float: right; margin-right: 140px;">
                <input type="text" name="fixedOverTimeWHrs" id="fixedOverTimeWHrs" style="width: 50px;" value="<%=hmOverTime.get("FIXED_OVERTIME_HOURS")!=null ? hmOverTime.get("FIXED_OVERTIME_HOURS") : ""%>"/>
                </span>
			</td>
		</tr>  --%>
		
		<tr id="trMinHrs" style="display:<%=hDisplay %>"> 
			<td class="txtlabel alignRight">Min Hrs Working for Overtime:<sup>*</sup></td>
			<td>
				<input type="text" name="minOverTime" id="minOverTime" style="width: 50px !important;" class="validateRequired" value="<%=hmOverTime.get("MIN_OVERTIME")!=null ? hmOverTime.get("MIN_OVERTIME") : ""%>" onkeypress="return isNumberKey(event)"/> 
			</td>
		</tr>
		<tr id="trRoundOff" style="display:<%=hRoundOff %>"> 
			<td class="txtlabel alignRight" nowrap="nowrap">Round off to nearest(on daily basis):</td>
			<td>
				<s:select theme="simple" name="roundOffTime" id="roundOffTime" headerKey="" headerValue="Select Round off" 
				list="#{'15':'15 Minute','30':'30 Minute','45':'45 Minute','60':'1 Hour'}"/> 
			</td>
		</tr>
		<tr id="trRoundOffSlab" style="display:<%=hRoundOffSlab %>"> 
			<td class="txtlabel alignRight" nowrap="nowrap" valign="top">Slab:</td>
			<td>
				<%
				int cnt = 0;
				int nAlMinSlab = alMinuteSlab !=null ? alMinuteSlab.size() : 0;
				if(operation !=null && operation.equalsIgnoreCase("U") && nAlMinSlab > 0){ %>
					<div style="width: 456px;">
						<div id="div_minuteslab">
							<% for(int i = 0; i < nAlMinSlab; i++){
								cnt++;
								Map<String, String> hmMinuteSlab = (Map<String, String>) alMinuteSlab.get(i);	
								int nRoundOffMinute = uF.parseToInt(hmMinuteSlab.get("ROUNDOFF_MINUTE"));
							%>
								<div id="row_minuteslab<%=cnt %>" style="padding: 10px 0px;">
									<div style="float: left;">
										Min:<input type="text" name="strMinMinute" value="<%=uF.parseToInt(hmMinuteSlab.get("MIN_MINUTE")) %>" style="width: 31px !important; text-align: right;" onkeypress="return isNumberKey(event)"/>min&nbsp;
										Max:<input type="text" name="strMaxMinute" value="<%=uF.parseToInt(hmMinuteSlab.get("MAX_MINUTE")) %>" style="width: 31px !important; text-align: right;" onkeypress="return isNumberKey(event)"/>min&nbsp;
										=&nbsp;
										<select name="strRoundOffMinunte" style="width: 85px !important;">
											<option value="0" <%if(nRoundOffMinute == 0){ %>selected="selected"<%} %>>0 Minute</option>
											<option value="15" <%if(nRoundOffMinute == 15){ %>selected="selected"<%} %>>15 Minute</option>
											<option value="30" <%if(nRoundOffMinute == 30){ %>selected="selected"<%} %>>30 Minute</option>
											<option value="45" <%if(nRoundOffMinute == 45){ %>selected="selected"<%} %>>45 Minute</option>
											<option value="60" <%if(nRoundOffMinute == 60){ %>selected="selected"<%} %>>1 Hour</option>
										</select>
									</div>
									<a href="javascript:void(0)" onclick="addMinuteSlab()" class="add-font"></a>
									<%if(i > 0){ %>
										<a href="javascript:void(0)" onclick="removeMinuteSlab(<%=cnt %>)" id="removeMinuteSlabId<%=cnt %>" class="remove-font" style="float:left;margin:0px;"></a>
									<%} %>
								</div>
							<%} %>
						</div>
					</div>		
				<%} else {
					cnt++;
				%>
					<div style="width: 456px;">
						<div style="padding: 10px 0px;">
							<div style="float: left;"> 
								Min:<input type="text" name="strMinMinute" value="" style="width: 31px !important; text-align: right;" onkeypress="return isNumberKey(event)"/>min&nbsp;
								Max:<input type="text" name="strMaxMinute" value="" style="width: 31px !important; text-align: right;" onkeypress="return isNumberKey(event)"/>min&nbsp;
								=&nbsp;
								<select name="strRoundOffMinunte" style="width: 85px !important;">
									<option value="0">0 Minute</option>
									<option value="15">15 Minute</option>
									<option value="30">30 Minute</option>
									<option value="45">45 Minute</option>
									<option value="60">1 Hour</option>
								</select>
							</div>	
							<a href="javascript:void(0)" onclick="addMinuteSlab()" class="add-font"></a>
						</div>
						<div id="div_minuteslab"></div>	
					</div>
					
				<%} %>
				<input type="hidden" name="cnt" id="cnt" value="<%=cnt %>"/>
			</td>
		</tr>
			<tr>
				<td>&nbsp;</td>
				<td><s:submit value="Save" cssClass="btn btn-primary" name="submit" theme="simple" id="submitButton"></s:submit></td>
			</tr>
		</table>
	</s:form>
</div>

