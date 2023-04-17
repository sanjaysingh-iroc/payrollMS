<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%
UtilityFunctions uF = new UtilityFunctions();
List couterlist = (List)request.getAttribute("reportList"); %>

<script type="text/javascript">
	function getGrades(value) {
		 var action = 'GetGradeList.action?strDesignation=' + value + "&type=EA";
		 getContent('gradeListSpan', action);
	}
	
	$(function(){
		$( "#idEffectiveDate").datepicker({dateFormat: 'dd/mm/yy'});
			$("input[type='submit']").click(function(){
				$("#frmLearningPlanAssessmentFinalize").find('.validateRequired').filter(':hidden').prop('required',false);
				$("#frmLearningPlanAssessmentFinalize").find('.validateRequired').filter(':visible').prop('required',true);
		});
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

	function changeLabelValuesE1(id1) {
		//alert("id1 000 --->> " + id1);
		//alert("id1.indexOf ===>> " + id1.indexOf("lbl_value_"));
		if(id1.indexOf("lbl_value_") != -1) {
			document.getElementById("tempValue_"+(id1.split("lbl_value_")[1])).innerHTML = document.getElementById(id1).value;
		
		} else {
		
			//alert("id1 --->> " + id1);
			var txt_amount = document.getElementById("1").value;
			if(txt_amount.length == 0)
				txt_amount = 0;
			document.getElementById("tempValue_1").innerHTML = txt_amount;
			
			<%  //java.util.List couterlist = (java.util.List)request.getAttribute("reportList");  
				for (int i=0; couterlist!=null && i<couterlist.size(); i++) {
				java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
			//alert("id1 1 --->> " + id1);
			var findId = "lbl_amount_"+<%=cinnerlist.get(0)%>+<%=cinnerlist.get(4)%>;
			
			var txt_amount2;
			if(document.getElementById("2")) {
				txt_amount2 = document.getElementById("2").value;
			} else if(document.getElementById("lbl_value_2_1")) {
				txt_amount2 = document.getElementById("lbl_value_2_1").value;
			} else {
				txt_amount2 = 0;
			}
			
			//alert("findId --->> " + findId);
			if(document.getElementById(findId)) {
				//alert("findId 1 --->> " + findId);	
				var amount = document.getElementById(findId).innerHTML;
				
				var txt_amount20001 = 0;
				/* if(document.getElementById("lbl_value_20001_1")) {
					txt_amount20001 = document.getElementById("lbl_value_20001_1").value;
				}
				if(document.getElementById("20001")) {
					txt_amount20001 = document.getElementById("20001").value;
				} */
				
				var result = (parseFloat(txt_amount) + parseFloat(txt_amount2)) * parseFloat(amount) / 100;
				if(<%=cinnerlist.get(0)%>==2) {
					result = parseFloat(txt_amount) * parseFloat(amount) / 100;
				} else {
					result = ( parseFloat(txt_amount) + parseFloat(txt_amount2) + parseFloat(txt_amount20001)) * parseFloat(amount) / 100;
				}
				
				document.getElementById("lbl_value_"+<%=cinnerlist.get(0)%>+<%=cinnerlist.get(4)%>).value = result;
				document.getElementById("tempValue_"+<%=cinnerlist.get(0)%>+<%=cinnerlist.get(4)%>).innerHTML = result;
			} else {
				if(document.getElementById(id1)) {
					document.getElementById("tempValue_"+id1).innerHTML = document.getElementById(id1).value;
				}
			}
				
			<% } %>
		
		}
			
		var total = 0;
		var elem = document.getElementById("div_earning").getElementsByTagName("span");
		for(var i = 0; i < elem.length; i++)
        {	
			if(elem[i].innerHTML != '')
				total =  parseFloat(total) + parseFloat(elem[i].innerHTML);
        }
		
		document.getElementById("total_earning_value").innerHTML = parseFloat(total);
		
		//Change Net Ammount 
		
		var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML);
			
			if (netAmmount < 0 ) {
			
				document.getElementById("lbl_net_amount").innerHTML = 0;
			} else {
				
				document.getElementById("lbl_net_amount").innerHTML = netAmmount;
			}
   	}
    
	
	function changeLabelValuesD1(id1) {
		
		if(id1.indexOf("lbl_value_") != -1) {
			document.getElementById("tempValue_"+(id1.split("lbl_value_")[1])).innerHTML = document.getElementById(id1).value;
		
		} else {
		
			var txt_amount = document.getElementById(id1).value;
			if(txt_amount.length == 0)
				txt_amount = 0;
			document.getElementById("tempValue_"+id1).innerHTML = txt_amount;
			
			<%  for (int i=0; i<couterlist.size(); i++) {
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
			%>
			
			var findId = "lbl_amount_"+<%=cinnerlist.get(0)%>+id1;
			if(document.getElementById(findId)) {
				var amount = document.getElementById(findId).innerHTML;
				var result = parseFloat(txt_amount) * parseFloat(amount) / 100;
				document.getElementById("lbl_value_"+<%=cinnerlist.get(0)%>+id1).value = result;
				document.getElementById("tempValue_"+<%=cinnerlist.get(0)%>+id1).innerHTML = result;
			}
				
			<% } %>
		
		}
		
		var total = 0;
		var elem = document.getElementById("div_deduction").getElementsByTagName("span");
		for(var i = 0; i < elem.length; i++)
        {	
			if(elem[i].innerHTML != '')
				total =  parseFloat(total) + parseFloat(elem[i].innerHTML);
        }
		
		document.getElementById("total_deduction_value").innerHTML = parseFloat(total);
		
		//Change Net Ammount 
		
		var netAmmount = parseInt(document.getElementById("total_earning_value").innerHTML) - 
			parseInt(document.getElementById("total_deduction_value").innerHTML);
			
			if (netAmmount < 0 ) {
			
				document.getElementById("lbl_net_amount").innerHTML = 0;
			}
			
			else {
				
				document.getElementById("lbl_net_amount").innerHTML = netAmmount;
			}
		
   	}
	
	function saveAll() {
		
		var sample_salary_value = document.getElementById("sample_salary").value;
		var total_earning = document.getElementById("total_earning_value").innerHTML;
		var total_deduction = document.getElementById("total_deduction_value").innerHTML;
		var testValue = 0;
		testValue = parseInt(total_earning) - parseInt(total_deduction);
		
		if(testValue == sample_salary_value) {
			alert('Saving..');frmEmpActivity
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
		//alert("displayId=====>"+displayId);
		var headId = displayId.substring(displayId.indexOf("_")+1, displayId.length);
		//alert("headId====>"+headId);
		
		if(!document.getElementById(displayId).checked) {
			//alert("if====>");
			if(document.getElementById("lbl_value_"+headId)) {
				//alert("if if====>");
				oldValues1[headId.split("_")[1]] = document.getElementById("lbl_value_"+headId).value;
				document.getElementById("lbl_value_"+headId).value = 0;
				changeLabelValuesE1("lbl_value_"+headId);
			}else if(document.getElementById(headId)) {
				//alert("if else if====>");
				//alert("before document.getElementById(headId).value====>"+document.getElementById(headId).value);
				//alert("before oldValues====>"+oldValues1.length);
				//alert("before oldValues[headId]====>"+oldValues1[headId]);
				oldValues1[headId] = document.getElementById(headId).value;
				//alert("oldValues[headId]====>"+oldValues1[headId]);
				document.getElementById(headId).value = 0;
				//alert("document.getElementById(headId).value====>"+document.getElementById(headId).value);
				changeLabelValuesE1(headId);
			}
		
		}else {
			//alert("else====>");
			if(document.getElementById("lbl_value_"+headId)) {
				//alert("else if====>");
				document.getElementById("lbl_value_"+headId).value = oldValues1[headId.split("_")[0]];
				changeLabelValuesE1("lbl_value_"+headId);
			}else if(document.getElementById(headId)) {
				//alert("else else if====>");
				document.getElementById(headId).value = oldValues1[headId];
				changeLabelValuesE1(headId);
			}
			
		}
		
	}
</script>
<%
		
	//String if_approved = (String) request.getAttribute("if_approved");
	boolean flag = (Boolean) request.getAttribute("flag");
	String finalizedBy = (String)request.getAttribute("finalizedBy");
	Map<String, String> hmActivityMap = (Map<String, String>) request.getAttribute("hmActivityMap"); 
	if(hmActivityMap == null) hmActivityMap = new HashMap<String, String>();
	
	if (!flag) {
%>
		<jsp:include page="../common/SubHeader.jsp">
			<jsp:param value="Comments" name="title" />
		</jsp:include>
<%} %>
<div class="leftbox reportWidth" id="formBody" >
	<s:form method="POST" action="LearningPlanAssessmentFinalize" theme="simple" id="frmLearningPlanAssessmentFinalize" name="frmLearningPlanAssessmentFinalize">
		<s:hidden name="learningId"></s:hidden>
		<s:hidden name="trainingId"></s:hidden>
		<s:hidden name="assessmentId"></s:hidden>
		<s:hidden name="empid"></s:hidden>
		<s:hidden name="remarktype"/>
		
		<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px; margin-top: 20px;">
			<table style="width:100%;" class="table table_no_border">
				<tr style="height: 10px;">
					<td colspan="2"><div style="float: left;">Send to Gap <s:checkbox name="sendtoGapStatus" id="sendtoGapStatus"/></div> </td>
				</tr>
				<tr style="height: 10px;">
				    <td colspan="2"><div style="float: left;">Award Certificate <s:checkbox name="certificateStatus" id="certificateStatus"/></div> </td>
				</tr>
				<tr style="height: 10px;">
					<td colspan="2"><div style="float: left;"> Award Thumbsup <s:checkbox name="thumbsupStatus" id="thumbsupStatus"/> </div></td>
				</tr>
				<tr>
				    <td style="text-align: right" valign="top" width="10%"><b>Remark:<sup>*</sup></b>&nbsp;</td>
					<td><s:textarea name="finalizeRemark" rows="7" cssClass="validateRequired" cols="80" ></s:textarea></td>
				</tr>
			</table>
		</div>
		
		<div class="box box-none nav-tabs-custom clr">
				<%
				String dataType = (String) request.getAttribute("dataType");
				String strLabel = "";
				String urlA = "LearningPlanAssessmentFinalize.action?dataType=A&learningId=" + (String) request.getAttribute("learningId") +"&strEmpId="+(String) request.getAttribute("strEmpId2")+ "&empid=" + (String) request.getAttribute("empid") + "&trainingId=" + (String) request.getAttribute("trainingId")+ "&assessmentId=" + (String) request.getAttribute("assessmentId") + "&remarktype=" + (String) request.getAttribute("remarktype");
				String urlD = "LearningPlanAssessmentFinalize.action?dataType=D&learningId=" + (String) request.getAttribute("learningId") +"&strEmpId="+(String) request.getAttribute("strEmpId2")+ "&empid=" + (String) request.getAttribute("empid") + "&trainingId=" + (String) request.getAttribute("trainingId")+ "&assessmentId=" + (String) request.getAttribute("assessmentId") + "&remarktype=" + (String) request.getAttribute("remarktype");
				 if(dataType == null || dataType.equals("A")) { 
					strLabel ="Activity";
				%>
				
					<ul class="nav nav-tabs" >
						<li class="active">
							<a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
						</li>
						<li>
							<a href="javascript:void(0)" onclick="getTabContent('activitywdoc')" data-toggle="tab">Activity W/Doc</a>
						</li>
					</ul>
					
				<% } else if(dataType != null && dataType.equals("D")) { 
					 strLabel ="Activity W/Doc";
				%>
					<ul class="nav nav-tabs" >
						<li>
							<a href="javascript:void(0)" onclick="getTabContent('activity')" data-toggle="tab">Activity</a>
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
		
		<s:hidden name="strEmpId2" id="strEmpId2"/>
		<s:hidden name="dataType"></s:hidden>
		<div style="float: left;">
			<table class="table table_no_border autoWidth">
			<tr>
		    	<td class="txtlabel" colspan="2"><strong>New <%=strLabel %> Information</strong></td>
		    	
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
		</div>
		<div id="salaryDetailsDiv" style="float: left;" >
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
			      	 				
			      	 				<%if(cinnerlist.get(3).equals("P")) {%>
			      	 				
							      	 	<input type="text" id="lbl_value_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>" name="salary_head_value" 
							      	 		style="width:60px;text-align:right" onkeyup="changeLabelValuesE1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)"/>

										
						       			<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" value="true" checked="checked"  onclick="makeZeroOnUncheck1(this.id)">
					       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
							      	 	
							      	 	<label id="lbl_amount_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
			      	 					
							      	 	 % of <label id="lbl_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(5).equals("0") ? "": cinnerlist.get(5) %></label>
							      	 	
							      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"></span>
							      	 	
							      	 <%} else { %>
							      	 	
		       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
		       							style="width:60px;text-align:right" onkeyup="changeLabelValuesE1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
			       					<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)%>"></span>
			       					
			       					<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck1(this.id)">
					       			<!-- <input type="hidden" value="false" name="isDisplay"> -->
			       					
			       					
			       					<% } %>
					       				
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
			      	 				
			      	 				<%if(cinnerlist.get(3).equals("P")) {%>
			      	 				
							      	 	<input type="text" id="lbl_value_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>" name="salary_head_value"
							      	 	style="width:60px;text-align:right" onkeyup="changeLabelValuesD1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
							      	 	
							      	 	<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"></span>
							      	 	
							      	 	<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)+"_"+(String)cinnerlist.get(4)%>" value="true" checked="checked" onclick="makeZeroOnUncheck1(this.id)">
					       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
					       				
					       				<label id="lbl_amount_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(6) %></label>
			      	 					
							      	 	 % of <label id="lbl_<%=(String)cinnerlist.get(0)+(String)cinnerlist.get(4)%>"><%= cinnerlist.get(5).equals("0") ? "": cinnerlist.get(5) %></label>
							      	 	
							      	<%}else{%>
							      	 	
			       						<input type="text" id="<%=cinnerlist.get(0)%>" name="salary_head_value" value="<%=(String)cinnerlist.get(6)%>"
			       							style="width:60px;text-align:right" onkeyup="changeLabelValuesD1(this.id)" maxlength="15" onkeypress="return isNumberKey(event)" />
				       					<span style="color:green; display: none;" id="tempValue_<%=(String)cinnerlist.get(0)%>"></span>
			       					
			       						<input type="checkbox" name="isDisplay_<%=(String)cinnerlist.get(0)%>" id="isDisplay_<%=(String)cinnerlist.get(0)%>" value="true" checked="checked" onclick="makeZeroOnUncheck1(this.id)">
					       				<!-- <input type="hidden" value="false" name="isDisplay"> -->
			       					
			       					<%}%>
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

$("#frmLearningPlanAssessmentFinalize").submit(function(event){
	event.preventDefault();
	//	alert("updateMyReview jsp ==appId==>"+appId+"==>appFreqId==>"+appFreqId);
	var form_data = $("#frmLearningPlanAssessmentFinalize").serialize();
	$("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	
	$.ajax({ 
		type : 'POST',
	//	url: strAction+'.action?appId='+appId+'&appFreqId='+appFreqId+'&fromPage='+fromPage+'&appsystem='+appsystem,
		url: "LearningPlanAssessmentFinalize.action",
		data: form_data+"&submit=Finalise",
		cache: true,
		success: function(result){
			
			$("#divLPDetailsResult").html(result);
   		}
	});
});


</script>

<script>
//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>


