<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_state = document.getElementById("f_state").value;
	var strMonth = document.getElementById("strMonth").value;
	var strGender = document.frm_from3PT.strGender.value;
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&financialYear='+financialYear+'&f_state='+f_state+'&strMonth='+strMonth+'&strGender=' + strGender;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form3PT.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function generateForm3PT() {
	var financialYear=document.frm_from3PT.financialYear.value;
	var strMonth=document.frm_from3PT.strMonth.value;
	var f_org=document.frm_from3PT.f_org.value;
	var f_state = document.frm_from3PT.f_state.value;
	var strGender = document.frm_from3PT.strGender.value;
	
	var url='Form3PT.action?formType=pdf&financialYear='+financialYear;
	url+='&strMonth='+strMonth+'&f_org='+f_org +'&f_state=' + f_state+'&strGender=' + strGender; 
	window.location = url;
}
	
</script> 

<style type="text/css">
body 
{
 margin:0 auto;
}
.fill:after
{
 content:"_______________________________________________________________________________________________________"	
}
.tdBorder{
	 border: 1px solid black;
}
</style>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab"); 
	if(hmPTSlab == null) hmPTSlab = new LinkedHashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmPTDetails =  (Map<String, Map<String, String>>) request.getAttribute("hmPTDetails"); 
	if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
	Map<String, String> hmOrg =  (Map<String, String>) request.getAttribute("hmOrg"); 
	if(hmOrg == null) hmOrg = new HashMap<String, String>();
	
	String paidfrom = (String) request.getAttribute("paidfrom");
	String paidto = (String) request.getAttribute("paidto");
	String strQuarter = (String) request.getAttribute("strQuarter");
	String strChalanNo = (String) request.getAttribute("strChalanNo");
%>
 
 
		<div class="box-header with-border">
		    <h3 class="box-title">Form 3</h3>
		</div>
		<!-- /.box-header -->
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
					<s:form name="frm_from3PT" action="Form3PT" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');" cssStyle="width: 160px !important;"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key="" cssStyle="width: 160px !important;"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">State</p>
									<s:select name="f_state" id="f_state" listKey="stateId" listValue="stateName" onchange="submitForm('2');" list="stateList" key=""  headerKey="" headerValue="Select State" cssStyle="width: 160px !important;"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Gender</p>
									<s:select name="strGender" id="strGender" listKey="genderId" list="genderList" listValue="genderName" headerKey="" headerValue="Select Gender" onchange="submitForm('2');" cssStyle="width: 100px !important;"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key="" cssStyle="width: 120px !important;"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm3PT();" href="javascript:void(0)" class="fa fa-file-pdf-o" >&nbsp;&nbsp;</a>
			</div>  -->
			
<div class="col-md-2 pull-right">
					
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>

			
			<div style="margin:0 auto; width:100%; text-align:center; overflow:hidden;">
				<h3 style="font-size:20px;"><strong>FORM III <br/>Part I-A</strong></h3>
				<p style="text-transform:uppercase; font-size:16px;">Return-cum-Chalan<br/>For the Profession Tax Officer</p>
				<p style="text-align:left;padding-left:40px;">B.S.T.R.C. No., if any.</p>
				<h3 style="font-size:20px;"><strong>The Maharashtra State Tax on Professions, Trades, Callings And Employments<br/>Act, 1975 AND Rule 11, 11-A, 11-B, 11-C</strong></h3>
				<p style="padding-left:40px;">
					0028, Other Taxes on Income and Expenditure-Taxes on Professions, Trades,<br/>
					Callings and Employments- taxes on Employments
				</p>
				<div style="clear:both;">
					<div style="overflow:hidden; margin:0 auto; width:94%;">
						
						<table border="0" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
							<tr>
								<td colspan="4" class="tdBorder">Employees whose monthly Salaries, Wages</td>
								<td class="tdBorder" colspan="2">Rate of Tax per month</td>
								<td class="tdBorder">No. of Employees</td>
								<td class="tdBorder">Amount of Tax deducted</td>
							</tr>
							<%
							 Iterator<String> it = hmPTSlab.keySet().iterator();
					        int i = 0;
					        double dblTaxAmt = 0.0d;
					        while(it.hasNext()){
					        	String strAmount = it.next();
					        	Map<String, String> hmPTSlabDetails = hmPTSlab.get(strAmount);
					        	Map<String, String> hmSalPT = hmPTDetails.get(strAmount);
					        	if (hmSalPT == null) hmSalPT = new HashMap<String, String>();
					        	
					        	String strMsg = "";
					        	String strRate = "";
					        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "");
					        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
					        	if(i==0){
					        		strMsg = "Do not exceed Rs. "+ hmPTSlabDetails.get("INCOME_TO");
					        		strRate = "Nil";
					        		strTotalAmt = "Nil";
					        	} else if(i == hmPTSlab.size()-1){
					        		strMsg = "Exceeds Rs."+hmPTSlabDetails.get("INCOME_FROM");
					        		double dblAnnualamt = uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"));
					        		strRate = "Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"))) +" per annum to be paid in the following" +
					        				" manner:-<br/>(a)Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")+" per month except in the month of February<br/>" +
					        				"(b)Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE"))+100)+" per month of February";
					        	} else {
					        		strMsg = "Exceed Rs. "+hmPTSlabDetails.get("INCOME_FROM")+" but do not exceed Rs. "+hmPTSlabDetails.get("INCOME_TO");
					        		strRate = "Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE");
					        	}
							%>
							<tr>
								<td colspan="4" class="tdBorder" align="left"><%=strMsg %></td>
								<td class="tdBorder" colspan="2" align="left"><%=strRate %></td>
								<td class="tdBorder"><%=uF.showData(hmSalPT.get("EMP_COUNT"), "") %></td>
								<td class="tdBorder" align="right"><%=strTotalAmt %></td>
							</tr>
							<%
								i++;
					        } 
					        %>
					        <tr>
								<td colspan="4" class="tdBorder" align="left">Tax Amount</td>
								<td colspan="4" class="tdBorder" align="right"><%=dblTaxAmt %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Interest Amount</td>
								<td colspan="4" class="tdBorder" align="right"><%="" %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Less-Excess tax paid, if any, in the previous</td>
								<td colspan="4" class="tdBorder" align="right"><%="" %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Year/Quarter/Month</td>
								<td colspan="4" class="tdBorder" align="center"><%=uF.showData(strQuarter, "") %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Net Amount Payable</td>
								<td colspan="4" class="tdBorder" align="right"><%="" %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Total Amount Paid (in words)</td>
								<td colspan="4" class="tdBorder" align="right"><%="" %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Profession Tax Registration Certificate No.</td>
								<td colspan="2" class="tdBorder" align="center">Period From<br/><%=uF.showData(paidfrom, "") %></td>
								<td colspan="2" class="tdBorder" align="center">Period To<br/><%=uF.showData(paidto, "") %></td>
							</tr>
							<tr>
								<td colspan="4" class="tdBorder" align="left">Name and Address</td>
								<td colspan="4" class="tdBorder" align="left"><%=uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "") %></td>
							</tr>
							<tr>
								<td colspan="8" align="left">The above statements are true to the best of my knowledge and belief.</td>
							</tr>
							<tr>
								<td colspan="8" align="left">Date: __________________</td>
							</tr>
							<tr>
								<td colspan="6" align="left">Place: __________________</td>
								<td colspan="2">Signature and Designation</td>
							</tr>
							<tr>
								<td colspan="8"><strong>For the Treasury Use Only</strong></td>
							</tr>
							<tr>
								<td colspan="6" align="left">Received Rs. (in words)</td>
								<td colspan="2" align="left">Rupees (in Figures) __________________</td>
							</tr>
							<tr>
								<td colspan="6" align="left">Date of Entry:</td>
								<td colspan="2" align="left">Chalan No.</td>
							</tr>
							<tr>
								<td colspan="2"><strong>Treasure.</strong></td>
								<td colspan="3"><strong>Accountant.</strong></td>
								<td colspan="3"><strong>Treasury Officer/Agent or Manager</strong></td>
							</tr>
						</table>
					</div>
					
				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>
   