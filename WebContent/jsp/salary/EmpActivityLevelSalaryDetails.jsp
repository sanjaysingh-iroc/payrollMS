<%@page import="java.util.Arrays"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
UtilityFunctions uF = new UtilityFunctions();
List<List<String>> couterlist = (List<List<String>>)request.getAttribute("reportList");
if(couterlist == null) couterlist = new ArrayList<List<String>>();
%>

<script type="text/javascript">
      jQuery(document).ready(function(){
          jQuery("#frmEmpActivity").validationEngine();
          $('input[type=text]').attr('autocomplete', 'off');
      });
      
      function getGrades(value) {
    	  
    	  var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
  		getContent('gradeListSpan', action);
      }
	  
</script>

<script>
$(function() {
    $( "#idEffectiveDate").datepicker({dateFormat: 'dd/mm/yy'});
});


function show_designation() {
	dojo.event.topic.publish("show_designation");
}

function show_grade() {
	dojo.event.topic.publish("show_grade");
}
	addLoadEvent(prepareInputsForHints);
	
function show_employees() {
	dojo.event.topic.publish("show_employees");
}

</script>




<style>


.clr 
{
 clear:both;
}

.input {
width : 100px;
}

.crdb_details
{
  width:97%;
  border:#a4a4a4 solid 1px;
  height:auto;
  float:left;
  margin:10px;
  padding:10px 10px 10px 10px;
  -moz-border-radius:5px;
  -webkit-border-radius:5px;
   border-radius:5px;
}

.credit
{
  width:50%;
  height:auto;
  float:left;
  border-right:#489BE9 solid 1px;
  margin:0px 5px 0px 0px;
  padding:5px 10px 5px 5px;
 
}

.deduction
{
width:47%;
  height:auto;
  float:left;
 /* border:#FF0000 solid 1px;*/
  padding:5px;
  
}

.heading h3
{
	margin: 0 0 10px;
	padding:0px 0px 10px 0px;
    border-bottom: 2px solid #EDEBEF;
    color: #000000;
    font-family: verdana,arial,sans-serif;
    font-size: 18px;
	text-align:center;
}

.details_lables
{
 width:auto;
 height:auto;
/* border:#0000CC solid 1px;*/
 float:left;
}

.row
{
  width:auto;
  /*border:#009966 solid 1px;*/
  float:left;
  margin:0px 0px 10px 0px;
}

.col1
{
   width:150px; 
 /* border:#00CC33 solid 1px;*/ 
  float:left;
  text-align:right;
}

.col2
{
float:left;
/*text-align:left;*/
width:auto;
margin:0px 0px 0px 10px;

}

.buttons
{
  float:left;
  width:310px;
  text-align:center;
 /* border:#FF9900 solid 1px;*/
  margin:10px 0px 10px 0px;
}

h4
{
 margin:0px;
 color:#666666;
}

.netvalue
{
  float:right;
  margin:10px 5px 10px 5px;
  font-size:22px;
}

.tdDashLabel {
    color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 12px;
    padding: 3px;
}

.tdDashLabel_net
{
   color: #298CE9;
    font-family: verdana,arial,helvetica,sans-serif;
    font-size: 18px;
    padding: 3px;
}

#popup_deduction label {
    color: #FFFFFF;
    text-align: left;
}







</style>

<script type="text/javascript">
	
	var gross_lbl_values = new Array();
	var deduction_lbl_values = new Array();
	var expected_deduction = 0;
	
	/* function changeAllLabelValues() {
		changeLabelValue();
		changeDeductionLabelValue();
	}
	 */
	function isNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}
	 
	function checkSalaryHeadDisable1(){
		changeLabelValuesE1('1');
	}

	 var roundOffCondition='<%=(String)request.getAttribute("roundOffCondition") %>';

	 function changeLabelValuesE1(id)  {
			var disableSalaryStructure = document.getElementById("disableSalaryStructure1");		
			if(disableSalaryStructure.checked == false){
			   		var reimbursementCTC = document.getElementById("reimbursementCTC").value;
			   		var reimbursementCTCOptional = document.getElementById("reimbursementCTCOptional").value;
			   		
			   		<%  
					for (int i=0; i<couterlist.size(); i++) {
						List<String> cinnerlist = (List<String>)couterlist.get(i); 
						if(cinnerlist.get(3)!=null && (cinnerlist.get(3)).trim().equals("P") 
								&& cinnerlist.get(7) != null && !cinnerlist.get(7).trim().equals("") 
								&& !cinnerlist.get(7).trim().equalsIgnoreCase("NULL") && cinnerlist.get(7).trim().length() > 0){
							List<String> al = Arrays.asList(cinnerlist.get(7).trim().split(","));
							int nAl = al != null ? al.size() : 0;
				%>	
							var formula = "";
							var cnt = 0;
							var isReimbursementCTC = new Boolean(false);
					<%		for(int j = 0; j < nAl; j++){
								String str = al.get(j);
								if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")){
									boolean isInteger = uF.isInteger(str.trim());
									if(isInteger){
										if(uF.parseToInt(str.trim()) == IConstants.REIMBURSEMENT_CTC){
					%>
											formula += ""+(parseFloat(getRoundOffValue(reimbursementCTC)) + parseFloat(getRoundOffValue(reimbursementCTCOptional)));
											cnt++;
											isReimbursementCTC = true;
					<%					} else {
					%>
											var sHeadDisplay = "isDisplay_"+<%=str.trim()%>;
											if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
												var sHeadId = ""+<%=str.trim()%>;
												var sAnnualHeadId = "annual_"+<%=str.trim()%>;
												if(document.getElementById(sAnnualHeadId)){
													formula += ""+ parseFloat(getRoundOffValue(document.getElementById(sAnnualHeadId).value));
													cnt++;
												} else if(document.getElementById(sHeadId)){
													formula += ""+parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));
													cnt++;
												}
											} else {
												formula += ""+parseFloat(getRoundOffValue('0'));
												cnt++;
											}
					<%					}
									} else {
					%>			
										formula += '<%=str.trim() %>'; 	
					<%				}
								}
							}
						%>
							var total = 0;
							if(cnt > 0 && formula.trim() != ''){
								var formulaCal = eval(formula);
								var percentage = '<%=cinnerlist.get(6) %>';
								total = (parseFloat(percentage) * parseFloat(formulaCal))/100;
								total = isNaN(total) ? 0 : total;
								
								/* if(isReimbursementCTC){
									total += parseFloat(getRoundOffValue(reimbursementCTCOptional));
								} */
							}
							document.getElementById(""+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
							document.getElementById("tempValue_"+<%=cinnerlist.get(0)%>).value = getRoundOffValue(total);
						<%}%>		
					<%}  %>
				}
			calculateTotalEarningandDeduction1();
			
		}

	function calculateTotalEarningandDeduction1(){
		var total = 0;
		var totalD = 0;
	    <%  
			for (int j=0; j<couterlist.size(); j++) {
				java.util.List innerlist = (java.util.List)couterlist.get(j); 
				if(uF.parseToInt(""+innerlist.get(0)) == IConstants.CTC){
					continue;
				}
		%>
				var sSalED = ""+'<%=innerlist.get(2)%>';
				if(sSalED == 'E'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId)){
							total =  parseFloat(total) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						}
					}
				} else if(sSalED == 'D'){
					var sHeadDisplay = "isDisplay_"+<%=innerlist.get(0)%>;
					if((document.getElementById(sHeadDisplay) && document.getElementById(sHeadDisplay).checked == true)) {
						var sHeadId = ""+<%=innerlist.get(0)%>;
						if(document.getElementById(sHeadId)){
							totalD =  parseFloat(totalD) + parseFloat(getRoundOffValue(document.getElementById(sHeadId).value));	
						}
					}
				}
		<%}%>
		document.getElementById("total_earning_value").innerHTML = getRoundOffValue(Math.round(parseFloat(total)));
		document.getElementById("total_deduction_value").innerHTML = getRoundOffValue(Math.round(parseFloat(totalD)));
	}
	
	function saveAll() {
		
		var sample_salary_value = document.getElementById("sample_salary").value;
		var total_earning = document.getElementById("total_earning_value").innerHTML;
		var total_deduction = document.getElementById("total_deduction_value").innerHTML;
		var testValue = 0;
		testValue = parseInt(total_earning) - parseInt(total_deduction);
		
		if(testValue == sample_salary_value) {
			alert('Saving..');
			return true;
		
		}else {
			alert('Net Amount is not equal to sample Ammount Entered..');
			return false;
		}
		
	}
	
	function removeField(id1) {
		
		if(confirm('Are you sure, you want to remove this field?', 'Please Confirm!')) {
			window.location="EmployeeSalaryDetails.action?id=" +id1; 
  			return true;
  		}
  		else {
  			return false;
  		}
	}
	
	$(document).ready(function() {
	    	
		changeAllLabelValues();
	    	
	});
	
var cnt;
	
	var oldValues1 = new Array();
	
	<%  
	//System.out.println("couterlist=====>"+couterlist.toString());
	
	for (int i=0; couterlist != null && !couterlist.isEmpty() && i<couterlist.size(); i++) {
			java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			//System.out.println("cinnerlist=====>"+cinnerlist.toString());
	%>
			oldValues1[<%=cinnerlist.get(0)%>] = "<%=cinnerlist.get(6)%>"; 
			<%-- alert("oldValues1====>"+oldValues1[<%=cinnerlist.get(0)%>]); --%>	
	<%}%>
	
function makeZeroOnUncheck1(displayId) {
	var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
	
	if(!document.getElementById(displayId).checked) {
		if(document.getElementById(headId)) {
			oldValues[headId] = document.getElementById(headId).value;
			changeLabelValuesE1(headId);
		}
	}else {
		if(document.getElementById(headId)) {
			document.getElementById(headId).value = oldValues[headId];
			changeLabelValuesE1(headId);
		}
	}		
}

function getRoundOffValue(val) {
	var roundOffVal = 0;
	if(parseInt(roundOffCondition) == 1){
		roundOffVal = parseFloat(val).toFixed(1);
	} else if(parseInt(roundOffCondition) == 2){
		roundOffVal = parseFloat(val).toFixed(2);
	} else {
		roundOffVal = Math.round(parseFloat(val));
	}
	
	return roundOffVal;
}

function submitDoc(dataType){
	document.frmEmployeeActivity.dataType.value=dataType;
	document.frmEmployeeActivity.submit();
}


	
	<%  String empId= (String) request.getAttribute("empId");
		String CCID= (String) request.getAttribute("CCID");
		String ccName = (String) request.getAttribute("CCNAME");
		String EMPNAME = (String)session.getAttribute("EMPNAME_P");
		
		if(EMPNAME==null)
			EMPNAME = (String)request.getAttribute("EMPNAMEFORCC");
	%>
	
</script>
	
	
<% 
String strMessage = (String)request.getAttribute(IConstants.MESSAGE);
if(strMessage == null) {
	strMessage = "";
}


Map hmEmpActivityDetails = (Map)request.getAttribute("hmEmpActivityDetails");

Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
if (hmEmpProfile == null) {
	hmEmpProfile = new HashMap<String, String>();
}
String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");

String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
List alSkills = (List) request.getAttribute("alSkills");
//EncryptionUtils encryption = new EncryptionUtils();// Created By Dattatray Date : 21-July-2021 Note : Encryption

%>
 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee Activity" name="title"/>
</jsp:include>
 


<div class="leftbox reportWidth">
 
    <%= strMessage%>
    
    <div style="float: left; width: 100%;">
	    <s:form name="frmEmployeeActivity" id="frmEmployeeActivity" theme="simple" action="EmployeeActivity" method="POST">
		<s:hidden name="dataType"></s:hidden>
	    <s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;" listValue="orgName" headerKey="" 
	    	headerValue="All Organisations" onchange="document.frmEmployeeActivity.submit();" list="organisationList" key=""  />

	    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;" listValue="wLocationName" 
		    headerKey="" headerValue="All Locations" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)" 		
		    list="wLocationList" key="" />

	    <s:select name="f_department" list="departmentList" listKey="deptId"  cssStyle="float:left;margin-right: 10px;" listValue="deptName" 
	    	headerKey="0" headerValue="All Departments" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)" />

	    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;" 
	    	listValue="levelCodeName" headerKey="0" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.frmEmployeeActivity.f_level.options[document.frmEmployeeActivity.f_level.selectedIndex].value+'&f_department='+document.frmEmployeeActivity.f_department.options[document.frmEmployeeActivity.f_department.selectedIndex].value+'&f_strWLocation='+document.frmEmployeeActivity.f_strWLocation.options[document.frmEmployeeActivity.f_strWLocation.selectedIndex].value)"
            list="levelList" key="" required="true" />

	        <div id="empDiv"  style="float:left; margin-right: 10px;">
				<s:select name="strEmpId" listKey="employeeId"  cssStyle="float:left" listValue="employeeName" headerKey="" headerValue="Select Employee"
					list="empList" key="" required="true" onchange="document.frmEmployeeActivity.submit();" />

				<%if(hmEmpActivityDetails!=null){%>				
	   				<!--  Created By Dattatray Date : 21-July-2021 Note : empId Encrypt -->		
	   				<a class="factsheet" href="MyProfile.action?empId=<%=(String)hmEmpActivityDetails.get("EMP_ID")%>"></a>
				<%} %>
			</div>

	    </s:form>
    </div>
    <%
    
    if(hmEmpActivityDetails!=null) { %>
    
    <div class="tableblock"  style="background:#efefef; padding:5px; border:solid 1px #d4d4d4; width: 80%; margin-top: 10px;">
    <table width="100%" class="table_font">
		<tr>
			<th colspan="4" align="left" style="padding-left: 20px">Current Information</th>
		</tr>
		
		<tr><td valign="top" style="width: 50%;">

			<div class="trow" style="background:#fff; margin:0px; width:100%; height: 255px;">
        
               <div style="float:left;padding:5px; width:100px">
               		<div style="height:82px;width:84px;border:1px solid #CCCCCC;float:left;margin:2px 10px 0px 0px">
               		<%-- <img height="100" width="100" class="lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation + hmEmpProfile.get("IMAGE")%>" /> --%>
               		<%if(docRetriveLocation == null) { %>
						<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE") %>" />
					<%} else { %>
						<img height="100" width="100" class="lazy img-circle" id="profilecontainerimg" style="border:1px solid #CCCCCC;" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+hmEmpProfile.get("EMP_ID")+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE") %>" />
					<%} %>
          			</div>
               </div>
               
              <div style="float:left; border:0px #ccc solid">
                        <table class="table_font">
                        <tr><td class="textblue" style="font-size: 12px;font-weight: bold"><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%>]</td></tr>
                        <tr><td><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>]</td></tr>
                        <tr><td><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%>]</td></tr>
                        <tr><td><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></td></tr>
                        <%-- <tr><td><span style="float: left; margin: 2px 5px 0px 0px;"><img src="images1/mail_icon.png" style="width: 15px; height: 15px;"/></span> <%=uF.showData((String) hmEmpProfile.get("EMP_EMAIL"), "-")%></td></tr>
                        <tr><td><span style="float: left; margin: 1px 5px 0px 0px;"><img src="images1/telephone.png" style="width: 15px; height: 15px;"/></span> <%=uF.showData((String) hmEmpProfile.get("CONTACT_MOB"), "-")%></td></tr> --%>
                        <%-- <tr><td>Date of Joining: <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td></tr>
                        <tr><td><% if(alSkills!=null && alSkills.size()!=0) {
                                for(int i=0; i<alSkills.size(); i++) { %>
                                <strong><%=(i<alSkills.size()-1) ? ((List)alSkills.get(i)).get(1) + ", " : ((List)alSkills.get(i)).get(1)%></strong>
                            <%}
                            }%></td></tr>
                        <tr><td><div id="skillPrimary"></div></td></tr> --%>
                        </table>
                        
                </div>   
			</div>
                   
	    
	       </td>
	       <td valign="top" style="width: 50%;">
			  <div class="trow" style="background:#fff; margin:0px; width:100%; height: 255px;">
				        
						    <table class="table_font">
							    <tr><td class="alignRight">Employee Type:</td><td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("EMP_TYPE"), "-")%></td></tr>
							    <tr><td class="alignRight">Date of Joining:</td><td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("JOINING_DATE"), "-")%></td></tr>
							    <tr><td class="alignRight">Probation Status:</td><td class="textblue"> 
							    <% if(probationRemaining != null) {
							    	if(uF.parseToInt(probationRemaining) > 0) {
						    	%>
							    <%=probationRemaining+ " days remaining." %>
							    <% } else { %>
							    Probation completed.
							    <% } %>	
							    
							    <% } else { %>
							    No probation.
							    <% } %>
							    </td></tr>
							    <tr><td class="alignRight">Notice Period:</td><td class="textblue"> <%=uF.showData(noticePeriod, "0")%> days</td></tr>
								<tr><td class="alignRight">&nbsp;</td><td class="textblue"></td></tr>
								<tr><td class="alignRight">Total Experience:</td><td class="textblue"> <%= uF.showData((String) hmEmpProfile.get("TOTAL_EXP"), "-") %></td></tr>
								<tr><td class="alignRight">Exp with Current Org:</td><td class="textblue"> <%= uF.showData((String)request.getAttribute("TIME_DURATION"), "-") %></td></tr>
								<tr><td class="alignRight">Education Qualification:</td><td class="textblue"> <%= uF.showData((String)request.getAttribute("educationsName"), "-") %></td></tr>
								<tr><td class="alignRight">Skills:</td><td class="textblue"> <%=uF.showData((String) hmEmpProfile.get("SKILLS_NAME"), "-")%></td></tr>
							</table>
						
						<%if(!uF.parseToBoolean(hmEmpProfile.get("OFFICIAL_FILLED_STATUS"))) {%>
							<div style="float:left;margin-top:60px;margin-left:60px "><img src="images1/warning.png" /> </div>
						<%} %>
					
			  </div>	
			

		</td></tr>

	</table>
</div>


<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px;">
		<%
		String dataType = (String) request.getAttribute("dataType");
		String strLabel = "";
		if(dataType == null || dataType.equals("A")) { 
			strLabel ="Activity";
		%>
			<%-- <a href="EmployeeActivity.action?dataType=A&strEmpId=<%=(String) request.getAttribute("strEmpId2")  %>" class="all">Activity</a>
			<a href="EmployeeActivity.action?dataType=D&strEmpId=<%=(String) request.getAttribute("strEmpId2")  %>" class="live_dull" style="width: 100px;">Activity W/Doc</a> --%>
			<a href="javascript:void(0)" onclick="submitDoc('A');" class="all">Activity</a>
			<a href="javascript:void(0)" onclick="submitDoc('D');" class="live_dull" style="width: 125px;">Activity W/Doc</a>
		<% } else if(dataType != null && dataType.equals("D")) { 
			strLabel ="Activity W/Doc";
		%>
			<%-- <a href="EmployeeActivity.action?dataType=A&strEmpId=<%=(String) request.getAttribute("strEmpId2")  %>" class="all_dull">Activity</a>
			<a href="EmployeeActivity.action?dataType=D&strEmpId=<%=(String) request.getAttribute("strEmpId2")  %>" class="live" style="width: 100px;">Activity W/Doc</a> --%>
			<a href="javascript:void(0)" onclick="submitDoc('A');" class="all_dull">Activity</a>
			<a href="javascript:void(0)" onclick="submitDoc('D');" class="live" style="width: 125px;">Activity W/Doc</a>
		<% } %>	
</div>

    
<s:form theme="simple" action="EmployeeActivity" method="POST" id="frmEmpActivity">

	<s:hidden name="strEmpId2" id="strEmpId2"/>
	<%-- <s:hidden name="levelId" id="levelId"/> --%>
	<s:hidden name="dataType"></s:hidden>
	<div style="float: left;">
	<table style="float: left; width: 720px;">
	
	<tr>
    	<td class="txtlabel" colspan="2">New <%=strLabel %> Information</td>
    	
    </tr>
	<tr>
   		<td colspan="4"><hr width="100%" style="border: 1px solid black;"></td>
   	</tr>
		
	<tr>
		<td colspan=4><s:fielderror />
		</td>
	</tr>	

	<tr>
		
		<td class="txtlabel alignRight">Activity<sup>*</sup>:</td>
		<td>
			<s:hidden name="strActivity" id="strActivity"></s:hidden>
			<%=uF.showData(((String) request.getAttribute("strActivityName")),"") %>
		</td>
		
		<td class="txtlabel alignRight">Effective Date<sup>*</sup>:</td>
		<td><s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired text-input" /></td>
		
	</tr>
	
	
	<tr>
		<td class="txtlabel alignRight" id="levelLTD">Level<sup>*</sup>:</td>
		<td id="levelVTD">
			<s:hidden name="strLevel" id="strLevel"></s:hidden>
			<%=uF.showData(((String) request.getAttribute("strLevelName")),"") %>
		</td>
	</tr> 
	
	<tr>
		<td class="txtlabel alignRight" id="desigLTD">Designation<sup>*</sup>:
			<%-- <s:hidden name="strDesignation"/> --%>
		</td>
		<td id="desigVTD"> 
			<span id="desigListSpan">
				<s:select theme="simple" name="strDesignation" id="strDesignation" listKey="desigId" cssClass="validateRequired" listValue="desigCodeName" 
					headerKey="" headerValue="Select Designation" list="desigList" key="" required="true" onchange="getGrades(this.value);" />
			</span>
		
		</td>
		
		<td class="txtlabel alignRight" id="gradeLTD">Grade<sup>*</sup>:
		<s:hidden name="strGrade"/>
		</td>
		<td id="gradeVTD">
		<span id="gradeListSpan">
			<s:select theme="simple" name="empGrade" id="empGrade" cssClass="validateRequired" list="gradeChangeList" 
			listKey="gradeId" listValue="gradeCode" headerKey="" headerValue="Select Grade" required="true" />
		</span>
			
		</td>
		
	</tr>
	
	
	<tr>
		<td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason<sup>*</sup>:</td>
		<td colspan="3" id="reasonVTD">
		<s:textarea name="strReason" rows="10" cssClass="validateRequired" cols="60" ></s:textarea><span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span></span>
		</td>
		
	</tr>

	<tr>
		<td></td>
		<td colspan="3">
			<s:submit name="strUpdate" cssClass="input_button" value="Update" />
			<% if(dataType != null && dataType.equals("D")){ %>
				<s:submit name="strUpdateDocument" cssClass="input_button" value="Update & Send Document" />
			<%} %>
		</td>
	</tr>

	</table>
	</div>
	
	<div id="salaryDetailsDiv" style="float: left; width: 44%;">
		<div class="crdb_details">
			<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure1" onclick="checkSalaryHeadDisable1();"/>Calculation from level/grade based structure is disabled
					<%
					Map<String, String> hmEmpAnnualVarPolicyAmount = (Map<String, String>) request.getAttribute("hmEmpAnnualVarPolicyAmount");
					if(hmEmpAnnualVarPolicyAmount == null) hmEmpAnnualVarPolicyAmount = new HashMap<String, String>();
					Iterator<String> it = hmEmpAnnualVarPolicyAmount.keySet().iterator();
					if(it.hasNext()){
						String strSalaryHeadId = it.next();
						String strAmt = hmEmpAnnualVarPolicyAmount.get(strSalaryHeadId);
					%>
						<input type="hidden" name="annual_<%=strSalaryHeadId %>" id="annual_<%=strSalaryHeadId %>" value="<%=strAmt %>"/>
					<%} %>
					
					<input type="hidden" name="reimbursementCTC" id="reimbursementCTC" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTC")) %>"/>
					<input type="hidden" name="reimbursementCTCOptional" id="reimbursementCTCOptional" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTCOptional")) %>"/>
					
					<div class="clr"></div>
					
					<div id="div_earning" style=" width: 48%; float: left; margin: 0px 5px 0px 0px; border-right: 1px solid #489BE9;">
		
					    <div style="width: 200px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
			      			<h3>EARNING DETAILS</h3>
					    </div>
				    	
				    	<div class="details_lables" >
				    	
						<% couterlist = (java.util.List)request.getAttribute("reportList"); 
							//System.out.println("couterlist --->> " + couterlist);
						%>
		 				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
		 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
						
						<% if(cinnerlist.get(2).equals("E")) { %>	
							
								<div class="row">
									<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
								 	<div class="col1 tdDashLabel">
						      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
			      	 				</div>
			      	 				
			      	 				<div class="col2" id="col2">
			       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
			       							style="width:60px;text-align:right" onchange="changeLabelValuesE1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
			       						<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">	
				       					
				       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck1(this.id)">
				       					<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equalsIgnoreCase("P")) { %>
					       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
								      	 	 % of<label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
					       				<%} %>
					       				
				       				</div>
			       				</div>
			       				<div class="clr"></div>
			       				
		       					<%}%>
		       				
	   					<%}%>
	   					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
								<label id="lbl_total_gross">Total Gross Salary:</label>
								<label id= "total_earning_value" style="color: green;">0</label>
							</div>
						</div>
						
					    <div class="clr"></div>
		  
		  			</div> 
		 
					<div id="div_deduction" style="float: left; width: 50%;">
		  
						<div style="width: 200px; margin-bottom: 10px; background-color: #EFEFEF; background-position: 10px 6px; background-repeat: no-repeat; cursor: pointer; text-shadow: 0 1px 0 #FFFFFF;">
							<h3>DEDUCTION DETAILS</h3>
						</div>
		    
			         	<div class="details_lables" >
			 				<% couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 				<% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
			 				<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
							
								<% if(cinnerlist.get(2).equals("D")) {%>	
								
									<input type="hidden" name="salary_head_id" value='<%= cinnerlist.get(0) %>'></input>
									<div class="row">
									 	<div class="col1 tdDashLabel">
							      	 		<label id="lbl"><%= cinnerlist.get(1) %>:</label>	
				      	 				</div>
				      	 				
				      	 				<div class="col2" id="col2">
				       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
				       							style="width:60px;text-align:right" onchange="changeLabelValuesE1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
					       					<input type="hidden" id="tempValue_<%=(String)cinnerlist.get(0) %>">
				       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck1(this.id)">
				       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equalsIgnoreCase("P")) { %>
						       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
									      	 	 % of<label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
						       				<%} %>	
					       				</div>
				       				</div>
				       				<div class="clr"></div>
				       				
			       					<%}%>
		   					<%}%>
		   					
							<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
								<label >Total Deduction:</label>
								<label id= "total_deduction_value" style="color: green;">0</label>
							</div>
					
						    <div class="clr"></div>
		   				</div>
					</div>
		       	<!-- </form> -->
		       	
       	<div class="clr"></div>
       	<div style="float: left; width: 100%;">This salary has to be approved again in salary approval</div>
		       	
		  <div class="clr"></div>		  
		  <div class="netamount" style="display:none;">
		      <div class="netvalue"> 
		      		<label class="tdDashLabel_net" id="lblNetAmountRs">Net Amount Rs:</label> 
		      		<s:label id="lbl_net_amount" name="lbl_net_amount" cssStyle="color: green;"></s:label>
		      </div>
		  </div>
		  
		</div>
	</div>
</s:form>


<% } else { %>
<!-- <hr style="float:left;" width="100%"> -->
<div class="nodata msg"><span>No Employee Selected</span></div>

<% } %>



</div>

<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  

changeLabelValuesE1('1');
</script>