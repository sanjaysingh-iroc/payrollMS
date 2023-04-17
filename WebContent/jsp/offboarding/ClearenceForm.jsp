<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %> 
<%@page import="java.util.*,com.konnect.jpms.util.*" %>
<%@ page trimDirectiveWhitespaces="true" %>
<script type="text/javascript" src="scripts/customAjax.js"></script>
<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"></script>
<%  
	UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF=new CommonFunctions();
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	String probationRemaining = (String) request.getAttribute("PROBATION_REMAINING");
	String noticePeriod = (String) request.getAttribute("NOTICE_PERIOD");

	Map<String, String> empMap = (Map<String, String>) request.getAttribute("empDetailsMp");
	String cTag = (String)request.getAttribute("currencyTag");//Created By Dattatray Date:09-12-21
%>

<table border="2" class="tb_style" style="width:100%; font-size: 12px;">
	<tr>
		<th colspan="4" style="font-size:26px; height:35px; text-align:center;">Full & Final Settlement Sheet / Clearance Slip</th>
	</tr>
	<tr>
		<th nowrap="nowrap" align="right" style="width: 200px;">Name of Employee</th>
		<td><%=hmEmpProfile.get("NAME") %> </td>
		<th nowrap="nowrap" align="right" style="width: 200px;">Employee Code No.</th>
		<td><%=hmEmpProfile.get("EMPCODE")%></td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Designation</th>
		<td><%=hmEmpProfile.get("DESIGNATION_NAME")%></td>
		<th nowrap="nowrap" align="right">Location</th>
		<td><%=hmEmpProfile.get("WLOCATION_NAME")%></td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Department</th>
		<td><%=hmEmpProfile.get("DEPARTMENT_NAME")%></td>
		<th nowrap="nowrap" align="right">Date of joining</th>
		<td><%=hmEmpProfile.get("JOINING_DATE")%> </td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Date of Resignation approved</th>
		<td><%=empMap.get("ACCEPTED_DATE")%></td>
		<th nowrap="nowrap" align="right">Date of Relieving (after notice)</th>
		<td><%=empMap.get("LAST_DAY_DATE")%> </td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Notice Period</th>
		<td> <%=uF.showData(noticePeriod, "0")%> days </td>
		<th nowrap="nowrap" align="right">Total Years of Service</th>
		<td><%=uF.showData((String)request.getAttribute("totalService"),"") %></td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Reason for Leaving Service</th>
		<td colspan="3"><%=uF.showData(empMap.get("EMP_RESIGN_REASON"),"") %> </td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Approval Reason of Manager</th>
		<td colspan="3"><%=uF.showData(empMap.get("MANAGER_APPROVE_REASON"),"") %> </td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" align="right">Approval Reason of HR Manager</th>
		<td colspan="3"><%=uF.showData(empMap.get("HR_MANAGER_APPROVE_REASON"),"") %> </td>
	</tr>
</table>

<%
Map<String,String> hmSalaryDetails =(Map<String,String> )request.getAttribute("hmSalaryDetails");
List<String> alEmpSalaryDetailsEarning = (List<String>)request.getAttribute("alEmpSalaryDetailsEarning");
List<String> alEmpSalaryDetailsDeduction = (List<String>)request.getAttribute("alEmpSalaryDetailsDeduction");

List<String> alEarningSalaryDuplicationTracer = (List<String>)request.getAttribute("alEarningSalaryDuplicationTracer");
List<String> alDeductionSalaryDuplicationTracer = (List<String>)request.getAttribute("alDeductionSalaryDuplicationTracer");

Map<String,Double> hmSalaryAmt=(Map<String,Double>)request.getAttribute("hmSalaryAmt");
double earningTotal=0.0;
double deductionTotal=0.0;
%>

 <table border="2" class="tb_style" style="width:100%; font-size: 12px;">
	<tr>
		<th colspan="2"><center><font size="5">Earning</font></center></th>
		<th colspan="2"><center><font size="5">Deductions</font></center></th>
	</tr>
	
	<% for(int i=0;i<alEmpSalaryDetailsDeduction.size() || i<alEmpSalaryDetailsEarning.size();i++) { %>
		
	<tr>
		<th><center>
		<%if(i<alEmpSalaryDetailsEarning.size()) { %>
			<%=hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i)) %>
		<% } %>
		</center></th>
		<td style="text-align:right">
		<% if(i<alEmpSalaryDetailsEarning.size()) {
			double salHeadAmt = 0;
			if(hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i)) != null) {
				salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
				earningTotal += hmSalaryAmt.get(alEmpSalaryDetailsEarning.get(i));
			}
		%>
		
		<%=uF.formatIntoOneDecimal(salHeadAmt) %>
		
		<% } %>
		</td>
		<th><center>
		<%if(i<alEmpSalaryDetailsDeduction.size()) { %>
			<%=hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i)) %>
		<% } %>
		</center></th>
		<td style="text-align:right">
		<%if(i<alEmpSalaryDetailsDeduction.size()) {
			double salHeadAmt = 0;
			if(hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i)) != null) {
				salHeadAmt = hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
				deductionTotal += hmSalaryAmt.get(alEmpSalaryDetailsDeduction.get(i));
			}
		%>
		
		<%=uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(salHeadAmt))) %><!--Created By dattatray Date:14-12-21  -->
		
		<% } %>
		</td>
	</tr>
		
	<% } %>
		
	<tr>
		<td style="text-align: right;">Gross Salary Earning Amount</td>
		<td style="text-align: right;"> <%=uF.formatIntoOneDecimal(earningTotal) %></td>
		<td style="text-align: right;">Gross Salary Deductions Amount</td>
		<td style="text-align: right;"><%=uF.formatIntoComma(uF.parseToDouble(uF.formatIntoZeroWithOutComma(deductionTotal))) %> </td><!--Created By dattatray Date:14-12-21  -->
	</tr>
	
	<%double netSalaryTotal = earningTotal - deductionTotal; %>
	<tr>
	
		<td>NET SALARY TOTAL:<!-- Amount Payable(Earning-Deduction) --></td>
		<!--Created By dattatray Date:14-12-21  -->
		<td colspan="3" style="text-align: right;"><%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),netSalaryTotal))) %> </td>
	</tr>
	
	<tr><th colspan="4"><center><font size="5">OTHERS</font></center></th></tr>
	<% 
		double dblOtherEarningTotal = 0.0d;
		double dblOtherDeductionTotal = 0.0d;
		
		double reimbursement = (Double) request.getAttribute("Reimbursement");
		dblOtherEarningTotal += reimbursement;
		double gratuity = (Double) request.getAttribute("gratuity");
		dblOtherEarningTotal += gratuity;
		double LTAAmt = (Double)request.getAttribute("LTAAmt");
		dblOtherEarningTotal += LTAAmt;
		double PerkAmt = (Double)request.getAttribute("PerkAmt");
		dblOtherEarningTotal += PerkAmt;
	
		double deductAmt = (Double)request.getAttribute("deductAmt");
		dblOtherDeductionTotal += deductAmt;
		
		double netOtherTotal = dblOtherEarningTotal - dblOtherDeductionTotal;
	%>
	
	<tr>
		<th style="text-align: center;">Reimbursement</th>
		<td align="right"><%=uF.formatIntoOneDecimal(reimbursement) %></td>
		<th style="text-align: center;">Other Deduction</th>
		<td align="right"><%=uF.formatIntoOneDecimal(deductAmt) %></td>
	</tr>
	
	<tr>
		<th style="text-align: center;">Gratuity</th>
		<td align="right"><%=uF.formatIntoOneDecimal(gratuity) %></td>
		<th style="text-align: center;">&nbsp;</th>
		<td align="right">&nbsp;</td>
	</tr>
	
	<tr>
		<th style="text-align: center;">LTA</th>
		<td align="right"><%=uF.formatIntoOneDecimal(LTAAmt) %></td>
		<th style="text-align: center;">&nbsp;</th>
		<td align="right">&nbsp;</td>
	</tr>
	
	<tr>
		<th style="text-align: center;">Perk</th>
		<td align="right"><%=uF.formatIntoOneDecimal(PerkAmt) %> </td>
		<th style="text-align: center;">&nbsp;</th>
		<td align="right">&nbsp;</td>
	</tr>
	
	<tr>
		<td style="text-align: right;">Gross Other Earnings</td>
		<td style="text-align: right;"><%=uF.formatIntoOneDecimal(dblOtherEarningTotal) %></td>
		<td style="text-align: right;">Gross Other Deductions</td>
		<td style="text-align: right;"><%=uF.formatIntoOneDecimal(dblOtherDeductionTotal) %></td>
	</tr>
	
	<tr>
		<td>NET OTHER TOTAL:<!-- Amount Payable(Earning-Deduction) --></td>
		<td colspan="3" style="text-align: right;"><%=uF.formatIntoOneDecimal(netOtherTotal) %> </td>
	</tr>
	
	<tr>
	<!-- Created By Dattatray Date:09-12-21 -->
		<td style="font-size: 14px;"><b>Total Settlement Amount(<%=cTag %>):</b><!-- Amount Payable(Earning-Deduction) --></td>
		<td colspan="3" style="text-align: right;">
		<%-- <%=uF.formatIntoOneDecimal(netSalaryTotal + netOtherTotal) %> --%>
		<!--Created By dattatray Date:14-12-21  -->
		<%=uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),netSalaryTotal + netOtherTotal))) %>

</td>
	</tr>
	<% 
		double settlementAmount = netSalaryTotal + netOtherTotal;
		String amountInWords = "";
		String strTotalAmt=""+settlementAmount;
		if(strTotalAmt.contains(".")){
			strTotalAmt=strTotalAmt.replace(".", ",");
			String[] temp=strTotalAmt.split(",");
			amountInWords = uF.digitsToWords(uF.parseToInt(temp[0]));
			if(uF.parseToInt(temp[1])>0){
				int pamt=0;
				if(temp[1].length()==1){
					pamt=uF.parseToInt(temp[1]+"0");
				}else{
					pamt=uF.parseToInt(temp[1]);
				}
				amountInWords+=" and "+uF.digitsToWords(pamt)+" paise only";
			}
		}else{
			int totalAmt1=(int)settlementAmount;
			amountInWords=uF.digitsToWords(totalAmt1)+" only";
		}
	%>
	<tr>
		<td style="font-size: 14px;"><b>Total Settlement Amount(in words):</b></td>
		<td colspan="3"><%=amountInWords %> </td>
	</tr>

</table> 

 <%-- <table border="2" class="tb_style" style="width:100%; font-size: 12px;">
 <tr>
 <th>Summary</th>
 <th>Amount</th>
 </tr>
 
 <tr>
 <th>Salary</th>
 <td><%=salry %></td>
 </tr>
 
 <tr>
 <th>Gratuity</th>
 <td><%=(Double)request.getAttribute("gratuity") %></td>
 </tr>
 
 <tr>
 <th>Reimbursement</th>
 <td><%=(Double)request.getAttribute("Reimbursement") %></td>
 </tr>
 
 <tr>
 <th>Leave Encashment</th>
 <td><%=(Double)request.getAttribute("leaveEncash") %></td>
 </tr>
 
 <tr>
 <th>Loan Amount</th>
 <td><%=(Double)request.getAttribute("loanAmt") %></td>
 </tr>
 
 <tr>
 <th>Travel Advance</th>
 <td><%=travellingAllowance %></td>
 </tr>
 
 <tr>
 <th>Other Earning</th>
 <td><%=(Double)request.getAttribute("accrued_salary") %></td>
 </tr>
 
 <tr>
 <th>Other Deduction</th>
 <td><%=(Double)request.getAttribute("deduction_salary") %></td>
 </tr>
 
 <tr>
 <th>Settlement Amount</th>
 <td><%=settletotal %></td>
 </tr>
</table> --%>

<!-- 
<tr>
<td>Basic + Per Bonus</td>
<td> </td>
<td> </td>

<td>P.F.</td>
<td> </td>
</tr>

<tr>
<td>DA</td>
<td> </td>
<td> </td>
<td>Professional tax</td>
<td> </td>
</tr>


<tr>
<td>Basic + DA</td>
<td> </td>
<td> </td>
<td>Income Tax</td>
<td> </td>
</tr>


<tr>
<td>H.R.A.</td>
<td> </td>
<td> </td>
<td>E.S.I.</td>
<td> </td>
</tr>


<tr>
<td>Medical</td>
<td> </td>
<td> </td>
<td>Loan /Advance</td>
<td> </td>
</tr>


<tr>
<td>Extra</td>
<td> </td>
<td> </td>
<td>Society</td>
<td> </td>
</tr>


<tr>
<td>Incentive</td>
<td> </td>
<td> </td>
<td>Other</td>
<td> </td>
</tr>


<tr>
<td>Conveyance</td>
<td> </td>
<td> </td>
<td>Other</td>
<td> </td>
</tr>


<tr>
<td>Performance Bon</td>
<td> </td>
<td> </td>
</tr>


<tr>
<td>EL Encash</td>
<td> </td>
<td> </td>
</tr>


<tr>
<td>Security Deposit</td>
<td> </td>
<td> </td>
</tr>


<tr>
<td>July 09 per bonus</td>
<td> </td>
<td> </td>
</tr>


<tr>
<td>round UP</td>
<td> </td>
<td> </td>
</tr>
 -->

<div style="float:left; font-size: 12px; width: 100%;">
	<table border="2" class="tb_style" style="width:100%; height:150px;">
	
		<tr> 
			<td colspan="2">No Dues Clearance- Approved by all Departments.</td> 
		</tr>
		
		<tr>
			<td colspan="2">Prepared By: <%=(String)session.getAttribute(IConstants.EMPNAME) %></td>
		</tr>
		<tr>
			<td colspan="2">HR Department:</td>
		</tr>
		
		<tr>
		<td colspan="2">I hereby agree and confirm having received the above amount before signing this settlement paper. There is nothing due on either side.</td>
	</tr>
	
	</table>
</div>

<!-- <div style="float:none;width:800px; font-size: 12px;">
<table border="2" class="tb_style" style="width:482px;height:280px;">
<tr>
<td>I hereby agree and confirm having recieved the above amount before signing this settlement paper. There is nothing due on either side.</td>
</tr>

<tr><td><textarea rows="4" cols="50"></textarea> </td></tr>
<tr><td>Signature of Employee</td></tr>

</table>
</div> -->

<div style="float:left; font-size: 12px; width: 100%;">
	<table border="2" class="tb_style" style="width:100%; ">
		<tr>
			<td colspan="2" style="font-size:25px;"><center>--For Account Department Only--</center></td>
		</tr>
		
		<tr><td colspan="2">Payment vide Cheque no.</td></tr>
		
		<tr><td colspan="2">Date of Payment</td></tr>
		
		<tr><td colspan="2">Name of Bank</td></tr>
		
		<tr><td colspan="2">Date</td> </tr>
		
		<tr><td colspan="2">For</td> </tr>
		<tr>
			<td>Manager-HR<br/>(Authorized signatory)</td>
			<td>Account Department<br/>(Authorized signatory)</td>
		</tr>
	
	</table>
</div>

<!-- <div style="float:none;width:800px;height:280px; font-size: 12px;">
<table border="2" class="tb_style" style="width:482px;height:280px;">
<tr>
<td>For</td>
</tr>
<tr>
<td>Co</td>
</tr>
<tr>
<td>Manager-HR</td>
</tr>
</table>
</div> -->

<%-- <div style="float:left;width:800px;height:280px;">

<table border="2" class="tb_style" style="width:967px">
<tr>
<td colspan="2"><center><font size="5">Check List for full and final settlement</font><br/><font size="5">(To be checked and filled by HOD and equivalent Authority of the location)</font></center></td>

</tr>
<tr>
<td>Company</td>

</tr>
<tr>
<td>Employee Code number</td>

</tr>
<tr>
<td>Name of the Employee</td>

</tr>
<tr>
<td colspan="2" style="font-size:30px;"><center>Folllowing points subject to applicability/if any</center></td>

</tr>


<tr>
<td>Supperannuation/Resignation letter duly signed by employee</td>
<td> <s:radio label="Answer" name="yourAnswer" list="#{'1':'Yes','2':'No'}"  /> </td>
</tr>

<tr>
<td>Supperannuation/Resignation dulty accepted by immediate superior</td>
<td><s:radio label="Answer" name="yourAnswer" list="#{'1':'Yes','2':'No'}"  /></td>
</tr>

<tr>
<td>One month notice given</td>
<td><s:radio label="Answer" name="yourAnswer" list="#{'1':'Yes','2':'No'}"  /></td></tr>

<tr>
<td>If notice not given</td>
<td><s:radio label="Answer" name="yourAnswer" list="#{'1':'Deduction','2':'Waived Off'}"  /></td></tr>

<tr>
<td>Notice amount to be paid</td>
<td><s:radio label="Answer" name="yourAnswer" list="#{'1':'Yes','2':'No'}"  /></td></tr>

<tr>
<td>Date of relieving</td>
<td></td>
</tr>

<tr>
<td>Charges properly handed over</td>
<td> </td>
</tr>

<tr>
<td>all accounts of vandors cleared</td>
<td> </td>
</tr>

<tr>
<td>Charge Taken by(Name)</td>
<td> </td>
</tr>

<tr>
<td>No dues Clearence from</td>
<td> </td>
</tr>

<tr>
<td>Conveyence Clearence</td>
<td>transport</td>
</tr>

<tr>
<td>Loan/Advance Clearence</td>
<td>Personnel </td>
</tr>

<tr>
<td>Company Property</td>
<td>Stores</td>
</tr>

<tr>
<td>HoD Clearence</td>
<td>HoD </td>
</tr>

<tr>
<td>Tax Clearence Paper</td>
<td>Accounts </td>
</tr>

<tr>
<td>hose Rent Reciept</td>
<td> </td>
</tr>

<tr>
<td>Investment made during the year</td>
<td> </td>
</tr>

<tr>
<td>Reimbursement Paper</td>
<td>Account(Sbmt/not sbmt) </td>
</tr>

<tr>
<td>remark(if any)</td>
<td> </td>
</tr>

<tr>
<td>Signature of Employee</td>
<td>Signature of HRD </td>
</tr>
</table>
</div> --%>
<div style="width: 100%; float: left; text-align: center;">
<span style="float: left; margin-right: 10px;">
	<s:form theme="simple" action="ExportPDF">
	<s:hidden name="emp_id"></s:hidden>
	<s:hidden name="resignId"></s:hidden>
	<s:submit value="Download PDF" cssClass="btn btn-primary"></s:submit>
	</s:form>
</span>
<span style="float: left; margin-right: 10px;">
	<s:form theme="simple" action="ExportFullFinalExcel">
	<s:hidden name="emp_id"></s:hidden>
	<s:hidden name="resignId"></s:hidden>
	<s:submit value="Download Excel" cssClass="btn btn-primary"></s:submit>
	</s:form>
</span>
</div>