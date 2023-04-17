<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var strMonth = document.getElementById("strMonth").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&financialYear='+financialYear+'&strSelectedEmpId='+strSelectedEmpId+'&strMonth='+strMonth;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form5ESIC.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function generateForm5ESIC() {
	var financialYear=document.frm_from5.financialYear.value;
	var strMonth=document.frm_from5.strMonth.value;
	var f_org=document.frm_from5.f_org.value;
	var strSelectedEmpId = document.frm_from5.strSelectedEmpId.value;
	
	var url='Form5ESIC.action?formType=pdf&financialYear='+financialYear;
	url+='&strMonth='+strMonth+'&f_org='+f_org +'&strSelectedEmpId='+strSelectedEmpId;
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
</style> 

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, String>();
	Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
	if(hmEmp == null) hmEmp = new HashMap<String, String>();
	
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
	Map<String, String> hmMonthChallan = (Map<String, String>) request.getAttribute("hmMonthChallan");
	if(hmMonthChallan == null) hmMonthChallan = new LinkedHashMap<String, String>();
	Map<String, String> hmMonthChallanDate = (Map<String, String>) request.getAttribute("hmMonthChallanDate");
	if(hmMonthChallanDate == null) hmMonthChallanDate = new LinkedHashMap<String, String>();
	Map<String, String> hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
	if(hmContribution == null) hmContribution = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	if(hmEmpName == null) hmEmpName = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpContribution = (Map<String, String>) request.getAttribute("hmEmpContribution");
	if(hmEmpContribution == null) hmEmpContribution = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpWages = (Map<String, String>) request.getAttribute("hmEmpWages");
	if(hmEmpWages == null) hmEmpWages = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
	if(hmEmpPaidDays == null) hmEmpPaidDays = new LinkedHashMap<String, String>();
	Map<String, String> hmEmpInsuranceNo = (Map<String, String>) request.getAttribute("hmEmpInsuranceNo");
	if(hmEmpInsuranceNo == null) hmEmpInsuranceNo = new LinkedHashMap<String, String>();
	
	String strHalfYear = (String) request.getAttribute("strHalfYear");
	String yearType = (String)request.getAttribute("yearType");
	
	Map<String, Map<String, String>> hmESIC = (Map<String, Map<String, String>>) request.getAttribute("hmESIC");
	if(hmESIC==null) hmESIC = new HashMap<String, Map<String, String>>();
%>
		<div class="box-header with-border">
		    <h3 class="box-title">Form 5 ESIC</h3>
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
					<s:form name="frm_from5" action="Form5ESIC" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee</p>
									<s:select label="Select Employee" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" headerValue="Select Employee" listValue="employeeCode" headerKey="0" list="empNamesList" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key=""/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
		
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm5ESIC();" href="javascript:void(0)" class="fa fa-file-pdf-o" >&nbsp;&nbsp;</a>
			</div>  -->
			
<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>		
			<div style="margin:0 auto; width:100%; text-align:center; overflow:hidden;">
				<h3 style="font-size:20px;"><strong>FORM 5</strong></h3>
				<p style="text-transform:uppercase; font-size:16px;">Return of contributions</p>
				<p style="text-transform:uppercase; font-size:16px;">EMPLOYEES' STATE INSURANCE CORPORATION<br/><span style="font-size:12px; text-transform:capitalize;">(Regulation 26)</span></p>
				<div style="clear:both; overflow:hidden;">
					<p style="float:left; padding-left:40px;">Name of Branch Office ______________________________</p>
					<p style="float:right; padding-right:40px;">Employer's Code No. <%=uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "") %></p>
					<p style="float:left; padding-left:40px; clear:both;">Name and Address of the factory or establishment:&nbsp;&nbsp;<strong><%=uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "") %></strong></p>
					<!-- <p style="float:right; padding-right:40px;">________________________________________</p> -->
					
				</div>
				<div style="">
					<p style="text-align:left;padding-left:40px;">Particulars of the Principal employer(s)<br/></p>
					<table border="0" style="padding:0 30px; margin-left:40px;" cellpadding="5">
						<tr>
							<td style="text-align: left;">(a) Name:</td>
							<td style="text-align: left;"><strong><%=uF.showData(hmEmp.get("EMP_NAME"), "") %></strong></td>
						</tr>
						<tr>
							<td style="text-align: left;">(b) Designation:</td>
							<td style="text-align: left;"><strong><%=uF.showData(hmEmpCodeDesig.get(hmEmp.get("EMP_ID")), "") %></strong></td>
						</tr>
						<tr>
							<td style="text-align: left;">(c) Residential Address:</td>
							<td style="text-align: left;"><strong><%=uF.showData(hmEmp.get("EMP_ADDRESS"), "") %></strong></td>
						</tr>
					</table>
					<p style="text-align:left;padding-left:40px;">Contribution Period from <strong><%=uF.showData(strHalfYear, "") %></strong></p>
					<p style="text-align:left;padding:0 40px; text-indent:50px; word-spacing:1.5px; ">I furnish below the details of the Employer's and Employee's share of contribution in respect of the under mentioned insured persons. I hereby declare that the return includes each and every employee, employed directly or through an immediate employer or in connection with the work of the factory / establishment or any work __________________ connected with the administration of the factory / establishment or purchase of raw materials, sale or distribution of finished products etc. to whom the ESI Act, 1948 applies, in the contribution period to which this return relates and that the contributions in respect of employer's and employee's share have been correctly paid in accordance with the provisions of the Act and Regulations.</p>
					<%
					 	double dblEmpShare = uF.parseToDouble(hmContribution.get(""+IConstants.EMPLOYEE_ESI)); 
			        	double dblERShare = uF.parseToDouble(hmContribution.get(""+IConstants.EMPLOYER_ESI));
			        	double dblTotalShare = dblEmpShare + dblERShare;
					%>
					<table border="0" style="padding:0 30px; margin:0 auto;" cellpadding="5" align="center">
						<tr>
							<td>Employees's Share</td>
							<td style="text-align: right;"><strong><%=dblEmpShare %></strong></td>
						</tr>
						<tr>
							<td>Employer's Share</td>
							<td style="text-align: right;"><strong><%=dblERShare %></strong></td>
						</tr>
						<tr>
							<td>Total Contribution</td>
							<td style="text-align: right;"><strong><%=dblTotalShare %></strong></td>
						</tr>
					</table>
					
				</div>
				<div style="clear:both;">
					<p style="text-align:left;padding-left:40px;">Details of Challans :-</p>
					<div style="overflow:hidden; margin:0 auto; width:94%;">
					<table border="1" style="margin-bottom:10px; border-collapse:collapse; text-align:center;  width:100%;" cellpadding="5" align="center">
						<tr>
							<td>Sl.No.</td>
							<td>Month</td>
							<td>Date of Challan</td>
							<td>Amount</td>
							<td>Name of the Bank and Branch</td>
						</tr>
						<%
							int j = 10;
							
					        if(yearType!=null && yearType.equals("1")){
					        	j = 4;
					        }
					        
					        for (int i = 1; i <= 6; i++,j++) {
								if(j==13){
									j=1;
								}
								Map<String, String> hmESICInner = (Map<String, String>) hmESIC.get(""+j);
								if(hmESICInner==null)hmESICInner = new HashMap<String, String>();
						%>
							<tr>
								<td style="text-align: center;"><%=i %></td>
								<td style="text-align: center;"><%=(hmMonthChallan.get(""+j)!=null ? uF.getMonth(j) : "") %></td>
								<td style="text-align: center;"><%=uF.showData(hmMonthChallanDate.get(""+j), "") %></td>
								<td style="text-align: right;"><%=uF.showData(hmMonthChallan.get(""+j), "") %></td>
								<td><%=uF.showData(hmESICInner.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmESICInner.get("CHALLAN_BRANCH_NAME"),"") %></td>
							</tr>
							
						<%} %>
					</table>
					</div>
					<p style="float:left; padding-left:40px; display:inline-block;">Place: __________________</p>
					<p style="float:right; padding-right:40px; display:inline-block;">Total amount paid: Rs. <strong><%=dblTotalShare %></strong></p>
					
				</div>
				<p style="clear:both;float:left; padding-left:40px;">Date: __________________</p>
				<p style="clear:both;float:right; padding-right:40px;">Signature and Designation of the Employer <br/> (with Rubber Stamp)</p>
			</div>
			
			<div style="margin:0 auto; width:100%; text-align:center; overflow:hidden;">
			 	<p style="text-align: left; padding-left: 90px;">Important Instructions: Information to be given in 'Remarks Column(No.9)'</p>
				<ul style="list-style:lower-roman outside none;padding-left:90px; text-align:left; padding-right:40px;">
					<li style="margin-top: 10px;">(i)If any I.P. is appointed for the first time and / or leaves during the <br/>contribution period indicate "A ____________(date)" and / or "L ____________(date)"</li>
					<li style="margin-top: 10px;">(ii)Please indicate Insurance Nos. in ascending order.</li>
					<li style="margin-top: 10px;">(iii)Figures in Columns 4,5 &amp; 6 shall be in respect of wage periods ended during the contribution period.</li>
					<li style="margin-top: 10px;">(iv)Invariably strike totals of Columns 4,5 and 6 of the Return.</li>
					<li style="margin-top: 10px;">(v)No overwriting shall be made. Any corrections, if made, should be signed by the employer.</li>
					<li style="margin-top: 10px;">(vi)Every page of this Return should bear full signature and rubber stamp of the employer.</li>
					<li style="margin-top: 10px;">(vii)Daily wages in Column 7 of the return shall be calculated by dividing figures in Column 5 by <br /> figures in Column 4 to two decimal places.</li> 
				</ul>
				<p style="text-align:left; padding-left:40px;">For *CP ending 31<sup>st</sup> March, due date is 12<sup>th</sup> May</p>
				<p style="text-align:left; padding-left:40px;">For CP ending 30<sup>th</sup> September, due date is 11<sup>th</sup> November</p>
				<p style="text-transform:uppercase; font-size:16px;margin-top:30px;">EMPLOYEE'S STATE INSURANCE CORPORATION</p>
				<ul style="list-style:none; width:100%; margin:0 auto; padding-left:40px; margin-right:40px; overflow:hidden; text-align:left;">
					<li style="display:inline;">Employer's Name and Address <strong><%=uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "") %></strong></li>
					<!-- <li style="border-bottom:1px solid; display:inline-block; width:70%; padding-right:46px;"></li> -->
				</ul>
				<p style="text-align:left; padding-left:40px;">Employer's Code No. ___________________________ Period from <strong><%=uF.showData(strHalfYear, "") %></strong></p>
				<table border="1" style="border-collapse:collapse; margin:20px 40px;" cellpadding="5" align="center">
					<tr>
						<td style="text-align: center;">Sl.No.</td>
						<td style="text-align: center;">Insurance Number</td>
						<td style="text-align: center;">Name of Insured Person</td>
						<td style="text-align: center;">No. of days for which wages paid</td>
						<td style="text-align: center;">Total amount of wages paid (Rs.)</td>
						<td style="text-align: center;">Employee's contribution deducted (Rs.)</td>
						<td style="text-align: center;">Average Daily Wages (Rs.)</td>
						<td style="text-align: center;">Whether still continues working</td>
						<td style="text-align: center;">Remarks</td>
					</tr>
					<tr>
						<td style="text-align: center;">1</td>
						<td style="text-align: center;">2</td>
						<td style="text-align: center;">3</td>
						<td style="text-align: center;">4</td>
						<td style="text-align: center;">5</td>
						<td style="text-align: center;">6</td>
						<td style="text-align: center;">7</td>
						<td style="text-align: center;">8</td>
						<td style="text-align: center;">9</td>			
					</tr>
					<%
						Iterator<String> it = hmEmpName.keySet().iterator();
				        int i = 0;
				        double dblTotalDays = 0.0d;
				        double dblTotalWages = 0.0d;
				        double dblTotalContribution = 0.0d;
				        double dblTotalAvg = 0.0d;
				        while(it.hasNext()){
				        	String strEmpId = it.next();
				        	String strEmpName = hmEmpName.get(strEmpId);
				        	i++;
				        	dblTotalDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId));
				        	dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId));
				        	dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId));
				        	double dblAvg = uF.parseToDouble(hmEmpWages.get(strEmpId)) / uF.parseToDouble(hmEmpPaidDays.get(strEmpId));
						    dblTotalAvg += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAvg)); 
				    %>
				    	<tr>
				    		<td style="text-align: center;"><%=i %></td>
				    		<td style="text-align: center;"><%=uF.showData(hmEmpInsuranceNo.get(strEmpId), "") %></td>
				    		<td style="text-align: left;"><%=strEmpName %></td>
				    		<td style="text-align: right;"><%=uF.showData(hmEmpPaidDays.get(strEmpId), "0") %></td>
				    		<td style="text-align: right;"><%=uF.showData(hmEmpWages.get(strEmpId), "0") %></td>
				    		<td style="text-align: right;"><%=uF.showData(hmEmpContribution.get(strEmpId), "0") %></td>
				    		<td style="text-align: right;"><%=uF.formatIntoTwoDecimalWithOutComma(dblAvg) %></td>
				    		<td style="text-align: center;"></td>
				    		<td style="text-align: center;"></td>
				    	</tr>
				    <%    	
				        }
					%>
					<tr>
						<td colspan="3" style="text-align:right; padding-right:10px;">Total</td>
						<td style="text-align: right;"><%=uF.formatIntoTwoDecimalWithOutComma(dblTotalDays) %></td>
						<td style="text-align: right;"><%=uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) %></td>
						<td style="text-align: right;"><%=uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) %></td>
						<td style="text-align: right;"><%=uF.formatIntoTwoDecimalWithOutComma(dblTotalAvg) %></td>
						<td style="text-align: center;">&nbsp;</td>
						<td style="text-align: center;">&nbsp;</td>
					</tr>
				</table>
				<p style="text-align:left; padding-left:40px;">*Date of appointment and leaving the job may be given in remarks column.</p>
				<p style="text-align:right; padding-right:40px;">Signature of the Employer</p>
				<p style="text-transform:uppercase; font-size:16px;">(for official use)</p>
				<ul style="list-style:decimal outside none;padding-left:90px; text-align:left; padding-right:40px;">
					<li style="margin-top: 10px;">Entitlement position marked.</li>
					<li style="margin-top: 10px;">Total of Col. 5 of Return checked and Found correct/ correct amount is indicated.</li>
					<li style="margin-top: 10px;">Checked the amount of Employer's/ Employee's contribution paid which is in order / observation memo enclosed.</li> 
				</ul>
				<p style="text-align:right; padding-right:40px;">Countersignature ______________________________</p>
				<ul style="list-style:none; padding-left:40px; text-align:left; padding-right:40px; margin-top:30px;">
					<li style="margin-top: 10px; display:inline-block; width:33%">U.D.C.</li>
					<li style="margin-top: 10px; display:inline-block; width:55%;">Head Clerk</li>
					<li style="margin-top: 10px; display:inline;">Branch Officer</li> 
				</ul>
			</div>
		</div>
		<!-- /.box-body -->
	</div>

   
   