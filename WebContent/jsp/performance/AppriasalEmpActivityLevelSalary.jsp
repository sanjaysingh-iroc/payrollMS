<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<% 
UtilityFunctions uF = new UtilityFunctions();
List<List<String>> couterlist = (List<List<String>>)request.getAttribute("reportList");
if(couterlist == null) couterlist = new ArrayList<List<String>>();
%>

<script type="text/javascript">
           
      function getGrades(value) {
		var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
  		getContent('gradeListSpan', action);
      }


      $(function(){
     	 $( "#idEffectiveDate").datepicker({format: 'dd/mm/yyyy'});
    		$("input[type='submit']").click(function(){
    			$("#frmEmpActivity").find('.validateRequired').filter(':hidden').prop('required',false);
    			$("#frmEmpActivity").find('.validateRequired').filter(':visible').prop('required',true);
    		});
    	 });
  
function show_designation() {
	dojo.event.topic.publish("show_designation");
}

function show_grade() {
	dojo.event.topic.publish("show_grade");
}
	//addLoadEvent(prepareInputsForHints);
	
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
	/* 
	$(document).ready(function() {
	    	
		changeAllLabelValues();
	    	
	}); */
	
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

	
</script>




<%
	String hrremark = (String) request.getAttribute("hrremark");
	String fromPage = (String) request.getAttribute("fromPage");
	String id = (String) request.getAttribute("id");
	String appFreqId = (String) request.getAttribute("appFreqId");
	String if_approved = (String) request.getAttribute("if_approved");
	boolean flag = (Boolean) request.getAttribute("flag");
	String strApprovedBy = (String)request.getAttribute("strApprovedBy");
	Map<String, String> hmActivityMap = (Map<String, String>) request.getAttribute("hmActivityMap"); 
	if(hmActivityMap == null) hmActivityMap = new HashMap<String, String>(); 
	
	if (!flag && (fromPage == null || fromPage.equals(""))) {
%>
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Comments" name="title" />
	</jsp:include>
<%} %> 
<div class="leftbox reportWidth">
	<s:form method="POST" action="AppraisalRemark" theme="simple" id="frmEmpActivity">
		<s:hidden name="id"></s:hidden>
		<s:hidden name="empid"></s:hidden>
		<s:hidden name="appFreqId"></s:hidden>
		<s:hidden name="thumbsFlag"/>
		<s:hidden name="remarktype"/>
		<s:hidden name="fromPage"/>
		<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px; margin-top: 20px;">
			<table class="table table_no_border autoWidth" >
				<tr><td colspan=4><s:fielderror/></td></tr>
				<tr>
					<td colspan=4>
						<%
						String thumbsFlag = (String) request.getAttribute("thumbsFlag");
						if(!uF.parseToBoolean(thumbsFlag)) { %>
						 <%-- <span style="float: left; margin-left: 10px; margin-top: 5px;"><img style="height: 16px; width: 16px;" src="images1/thumbs_up_green.png"></span> --%>
						 <span style="float: left; margin-left: 10px; margin-top: 5px;"><i class="fa fa-thumbs-up" style="color:#68ac3b;height: 16px; width: 16px;" aria-hidden="true"></i></span>
						<% } else { %>
							<span style="float: left; margin-left: 10px; margin-top: 5px;">
								<!-- <img style="height: 16px; width: 16px;" src="images1/thumbs_down_red.png"> -->
								<i class="fa fa-thumbs-down" style="color:#e22d2c;height: 16px; width: 16px;" aria-hidden="true"></i>
								
								Send to Learning Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus"/>
							</span>
						<% } %>
					</td>
				</tr>
				
				<tr>
					<td style="text-align: right;" class="txtlabel" valign="top"><b>Remark:<sup>*</sup></b>&nbsp;</td>
					<td colspan="3">
						<s:textarea name="remark" rows="7" cssClass="validateRequired" cols="100" ></s:textarea>
					</td>
				</tr>
			</table>
		</div>
		
		
		<div class="box box-none nav-tabs-custom clr">
				<%
				String dataType = (String) request.getAttribute("dataType");
				String strLabel = "";
				String strAction = "AppraisalRemark.action?";
				if(fromPage != null && fromPage.equalsIgnoreCase("ABF")) {
					strAction = "AppraisalBulkFinalization.action?";
				}
				
				String urlA = strAction+"dataType=A&strEmpId="+(String) request.getAttribute("strEmpId2")+"&id=" +(String) request.getAttribute("id")+ "&empid=" +(String) request.getAttribute("empid") + "&thumbsFlag=" + (String) request.getAttribute("thumbsFlag") + "&remarktype=" + (String) request.getAttribute("remarktype")+ "&appFreqId=" + (String) request.getAttribute("appFreqId");
				String urlD = strAction+"dataType=D&strEmpId="+(String) request.getAttribute("strEmpId2")+"&id=" +(String) request.getAttribute("id")+ "&empid=" +(String) request.getAttribute("empid") + "&thumbsFlag=" + (String) request.getAttribute("thumbsFlag") + "&remarktype=" + (String) request.getAttribute("remarktype")+ "&appFreqId=" + (String) request.getAttribute("appFreqId");
				
				if(dataType == null || dataType.equals("A")) { 
					strLabel ="Activity";
				%>
					<ul class="nav nav-tabs" >
						<li class="active"><a href="javascript:void(0)"
							onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
						</li>
						<li><a href="javascript:void(0)"
						onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
						</li>
					</ul>
					<%-- <a href="<%=urlA  %>" class="all">Activity</a>
					<a href="<%=urlD  %>" class="live_dull" style="width: 100px;">Activity W/Doc</a> --%>
				<% } else if(dataType != null && dataType.equals("D")) { 
					strLabel ="Activity W/Doc";
				%>
					<ul class="nav nav-tabs" >
						<li><a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
						</li>
						<li class="active"><a href="javascript:void(0)"
							onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
						</li>
					</ul>
				<% } %>	
					<script>
					function getTabContent(condition){
						
						$("#formBody").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
						if(condition === 'activity'){
							var action = '<%=urlA  %>';
						}else{ 
							var action = '<%=urlD  %>';
						}
						$.ajax({ 
							type : 'GET',
							url: action,
							cache: true,
							success: function(result){
								$("#formBody").html(result);
					   		}
						});
					}
			
				</script>
		</div>
		
		<s:hidden name="strEmpId2" id="strEmpId2"/>
		<s:hidden name="dataType"></s:hidden>
		<div class="tab-content" id="activity_div">
			<table class="table table_no_border autoWidth">
			<tr>
		    	<td class="txtlabel" colspan="2"><strong>New <%=strLabel %> Information</strong></td>
		    	
		    </tr>
			<tr>
		   		<td colspan="4"><hr width="100%" style="border: 1px solid #ececec;"></td>
		   	</tr>
			<tr>
				<td colspan=4><s:fielderror />
				</td>
			</tr>	
			<tr>
				<td class="txtlabel alignRight">Activity:<sup>*</sup></td>
				<td>
					<s:hidden name="strActivity" id="strActivity"></s:hidden>
					<%=uF.showData(((String) request.getAttribute("strActivityName")),"") %>
				</td>					
				<td class="txtlabel alignRight">Effective Date:<sup>*</sup></td>
				<td><s:textfield name="effectiveDate" id="idEffectiveDate" cssClass="validateRequired" /></td>
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
				<td class="txtlabel alignRight" valign="top" id="reasonLTD">Reason:<sup>*</sup></td>
				<td colspan="3" id="reasonVTD">
				<s:textarea name="strReason" rows="10" cssClass="validateRequired" cols="60" ></s:textarea><span class="hint">Add the reason for this activity for reference.<span class="hint-pointer">&nbsp;</span></span>
				</td>
				
			</tr>
				<tr>
					<td>&nbsp;</td>
					<td colspan="3">
						<s:submit name="submit" value="Finalise" cssClass="btn btn-primary"></s:submit> 
					</td>
				</tr>
			</table>
		</div>
		
		<div id="salaryDetailsDiv" style="float: left;">
				<s:checkbox name="disableSalaryStructure" id="disableSalaryStructure1" onclick="checkSalaryHeadDisable1();"/>Calculation from level/grade based structure is disabled
				<input type="hidden" name="reimbursementCTC" id="reimbursementCTC" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTC")) %>"/>
				<input type="hidden" name="reimbursementCTCOptional" id="reimbursementCTCOptional" value="<%=uF.parseToDouble((String) request.getAttribute("dblReimbursementCTCOptional")) %>"/>
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
				<div class="crdb_details">
				
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
						       					<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
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
					       						<% if(cinnerlist.get(3)!=null && ((String)cinnerlist.get(3)).trim().equals("P")) { %>
							       					<label id="lbl_amount_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
										      	 	 % of<label id="lblMulCal_<%=(String)cinnerlist.get(0) %>">[<%=uF.showData(""+cinnerlist.get(8),"") %>]</label>
							       				<%} %>	
						       				</div>
					       				</div>
					       				<div class="clr"></div>
					       				
				       					<%}%>
			   					<%}%>
			   					
								<div class="row" style="text-align:center; float: left; font-size: 18px; color: #298CE9;">
									<label>Total Deduction:</label>
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
</div>

<script>
$("#frmEmpActivity").submit(function(event){
	event.preventDefault();
	//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var from = '<%=fromPage%>';
	var action = "AppraisalRemark.action";
	if(from != "" && from == 'ABF') {
		action = "AppraisalBulkFinalization.action";
	}
	var form_data = $("#frmEmpActivity").serialize();
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: action,
		data: form_data+"&submit=Finalise",
		cache: true,
		success: function(result){
			getReviewStatus('AppraisalStatus','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
   		},
		error: function(result){
			getReviewStatus('AppraisalStatus','<%=id%>','<%=appFreqId %>','<%=fromPage %>');
		}
	});
});



</script>

