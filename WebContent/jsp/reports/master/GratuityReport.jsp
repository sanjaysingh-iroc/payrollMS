<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<style>
.calcInfo>p{
margin-bottom: 0px;
}
</style> 

<% 
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
if(CF==null)return;
%>

<script type="text/javascript" charset="utf-8">

$(document).ready( function () {
	$("input[name='gratuityUpdate']").click(function(){
		$("#frm_gratuityReport").find('.validateRequired').filter(':hidden').prop('required',false);
	    $("#frm_gratuityReport").find('.validateRequired').filter(':visible').prop('required',true);
	});
	$(".salaryHeadName").multiselect().multiselectfilter();
	$("#effectiveDate").datepicker({
    	format : 'dd/mm/yyyy'
    });
	$('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
});

function submitForm(type){
	var form_data = $("#frm_gratuityReport").serialize();
	if(type == 2) {
		$.ajax({
			url : 'GratuityReport.action',
			data : form_data + "&gratuityUpdate=Update",
			cache :false,
			success:function(data) {
				$("#actionResult").html(data);
			}
		});
	}else {
		$.ajax({
			url : 'GratuityReport.action',
			data : form_data ,
			cache :false,
			success:function(data) {
				$("#actionResult").html(data);
			}
		});
	}
}

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkCalBasis(val) {
	if(val == 'AFD'){
		document.getElementById("trFixedDays").style.display="table-row";
	} else {
		document.getElementById("trFixedDays").style.display="none";
	}
}

</script>

<!-- Custom form for adding new records -->

<%
UtilityFunctions uF = new UtilityFunctions();
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);

%>

<div id="printDiv" class="leftbox reportWidth" >
<p style="float: right; font-style: italic; font-size: 10px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>         
    
<div class="calcInfo">
<p style="font-weight:bold">Calculation</p>
<p>(BASIC + DA) * Gratuity Days * No of years</p>
<p>No of years will be calcualted only if the no of service years is between 'Service From' to 'Service To'</p>
<p>Gratuity amount can not be more than 'Max Gratuity Amount'</p>
</div>
    <br/><br/>
    <s:form name="frm_gratuityReport" id="frm_gratuityReport" theme="simple" action="GratuityReport">
		<s:hidden name="userscreen" id="userscreen" />
		<s:hidden name="navigationId" id="navigationId" />
		<s:hidden name="toPage" id="toPage" />
			
		<table class="table table_no_border">
			<tr>
			    <td class=" alignRight">Organization: <sup>*</sup></td>
				<td class =""><s:select theme="simple" name="strOrg" id="strOrg" cssClass="validateRequired" listKey="orgId" listValue="orgName" onchange="submitForm(0)" list="orgList"/></td>
			</tr>
			
			<tr>
			    <td class=" alignRight">Effective Date: <sup>*</sup></td>
			    <td class =""><s:textfield name="effectiveDate" id="effectiveDate" cssClass="validateRequired"/> </td>
			</tr>
			
			<tr>
			    <td class=" alignRight">Salary Heads: <sup>*</sup></td>
			    <td class =""> <s:select theme="simple" list="salaryHeadList" cssClass="validateRequired salaryHeadName" listValue="salaryHeadName" listKey="salaryHeadId"
						 multiple="true" size="5" name="strSalaryHeadId"></s:select></td>
			</tr>
			
			<tr>
			    <td class=" alignRight">Calculation %:<sup>*</sup></td>
				<td class =""><s:textfield name="strCalPercent" id="strCalPercent" cssStyle="width:50px !important; text-align:right;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			
			<%-- <tr>
			    <td class=" alignRight">Calculation Basis: <sup>*</sup></td>
				<td class =""><s:select theme="simple" name="strCalBasis" cssClass="validateRequired" id="strCalBasis" list="calBasisList" listKey="salaryCalcId" listValue="salaryCalcName" onchange="checkCalBasis(this.value);"/></td>
			</tr>
			
			<tr id="trFixedDays" style="display: none;">	
				<td class=" alignRight">Fixed Days:<sup>*</sup></td>
				<td><s:textfield name="strFixedDays" id="strFixedDays" cssStyle="width:50px !important; text-align:right;" cssClass="validate[required]" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			
			<tr>	
				<td class=" alignRight">Service From (years): <sup>*</sup></td>
				<td><s:textfield name="strServiceFrom" id="strServiceFrom" cssClass="validateRequired" cssStyle="width:50px !important; text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			
			<tr>	
				<td class=" alignRight">Service To (years): <sup>*</sup></td>
				<td><s:textfield name="strServiceTo" id="strServiceTo" cssClass="validateRequired" cssStyle="width:50px !important; text-align:right;" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			
			<tr>
				<td class=" alignRight">Gratuity Days: <sup>*</sup></td>
				<td><s:textfield name="strGratuityDays" id="strGratuityDays" cssStyle="width:50px !important; text-align:right;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr>
			<tr>
				<td class=" alignRight">Max Gratuity Amount: <sup>*</sup></td>
				<td><s:textfield name="strMaxGratuityAmount" id="strMaxGratuityAmount" cssStyle="width:100px !important; text-align:right;" cssClass="validateRequired" onkeypress="return isNumberKey(event)"></s:textfield></td>
			</tr> --%>
			<tr>	
				<td>&nbsp;</td>
				<td align="left">
					<input type="button" style="" class="btn btn-primary" name="gratuityUpdate" value="Update" onclick="submitForm(2);"/>
				</td>
			</tr>
		</table>
	
	</s:form>
    
<!-- Place holder where add and delete buttons will be generated -->
	<div class="add_delete_toolbar"></div>
	<div class="pagetitle clr margintop20">Gratuity Details</div>
	<table class="table table-bordered" id="lt">
		<thead>
			<tr>
				<th class="alignCenter">Organization</th>
				<th class="alignCenter">Effective Date</th>
				<th class="alignCenter">Salary Heads</th>
				<th class="alignCenter">Calculate Percent</th>
			</tr>
		</thead>
		<tbody>
			<%
				List<Map<String, String>> alGratuity = (List<Map<String, String>>) request.getAttribute("alGratuity");
				if(alGratuity == null) alGratuity = new ArrayList<Map<String,String>>();
				int nGratuity = alGratuity.size();
				for(int i = 0; i < nGratuity; i++){
					Map<String, String> hmGratuity = alGratuity.get(i);
					if(hmGratuity == null) hmGratuity = new HashMap<String, String>();
				%>
					<tr>
						<td><%=hmGratuity.get("ORG_NAME") %></td>
						<td class="alignRight"><%=hmGratuity.get("EFFECTIVE_DATE") %></td>
						<td><%=hmGratuity.get("SALARY_HEAD") %></td>
						<td><%=hmGratuity.get("CALCULATE_PERCENT") %></td>
					</tr>
				<%} %>
		</tbody>
	</table> 
	
	<%-- <table class="table table-bordered" id="lt">
		<thead>
			<tr>
				<th class="alignCenter">Organization</th>
				<th class="alignCenter">Service From (years)</th>
				<th class="alignCenter">Service To (years)</th>
				<th class="alignCenter">Gratuity Days</th>
				<th class="alignCenter">Max Gratuity Amount</th>
				<th class="alignCenter">Calculation Basis</th>
				<th class="alignCenter">Salary Heads</th>
			</tr>
		</thead>
		<tbody>
			<%
				List<Map<String, String>> alGratuity = (List<Map<String, String>>) request.getAttribute("alGratuity");
				if(alGratuity == null) alGratuity = new ArrayList<Map<String,String>>();
				int nGratuity = alGratuity.size();
				for(int i = 0; i < nGratuity; i++){
					Map<String, String> hmGratuity = alGratuity.get(i);
					if(hmGratuity == null) hmGratuity = new HashMap<String, String>();
					
					String strCalculationBasis = hmGratuity.get("SALARY_CAL_BASIS");
					if(hmGratuity.get("SALARY_CAL_BASIS_ID").equals("AFD")){
						strCalculationBasis += " ("+hmGratuity.get("FIXED_DAYS")+" days)";
					}
				%>
					<tr>
						<td><%=hmGratuity.get("ORG_NAME") %></td>
						<td class="alignRight"><%=hmGratuity.get("SERVICE_FROM") %></td>
						<td class="alignRight"><%=hmGratuity.get("SERVICE_TO") %></td>
						<td class="alignRight"><%=hmGratuity.get("GRATUITY_DAYS") %></td>
						<td class="alignRight"><%=hmGratuity.get("MAX_AMOUNT") %></td>
						<td><%=strCalculationBasis %></td>
						<td><%=hmGratuity.get("SALARY_HEAD") %></td>
					</tr>
				<%} %>
		</tbody>
	</table>  --%>
</div>   

<script type="text/javascript">
checkCalBasis('<%=uF.showData((String)request.getAttribute("strCalBasis"),"") %>');
</script> 