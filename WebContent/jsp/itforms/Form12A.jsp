<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
});

function submitForm(type){
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form12A.action?strMonth='+strMonth+'&financialYear='+financialYear,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
	strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
}

%> 

<!-- Custom form for adding new records -->

		<div class="box-header with-border">
			<h3 class="box-title">Form 12A for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
		</div>

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
					<s:form name="frm_from12A" action="Form12A" theme="simple">
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
									<p style="padding-left: 5px;">Month</p>
									<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" onchange="submitForm('2');" list="monthList" key="" />
								</div>
								<!-- <div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input name="strSubmit" type="submit" class="input_button" value="Generate Form 12A" style="float:right">
								</div> -->
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
	 		<div style="float:left;width:30%">
				<div style="padding:10px;border:1px solid #ccc;margin-bottom:20px">	
					<p style="font-size:12px;font-weight:bold;">Name and address of the establishment</p>
					<p style="font-size:12px;font-weight:bold;">Company Name</p>
					<p style="font-size:12px;font-weight:bold;">Pune Maharashtra</p>
				</div>
				
				<div style="padding:10px;border:1px solid #ccc">	
					<p style="font-size:12px;font-weight:bold;">Code no of the establishment: 213324</p>
				</div>
				
			</div>

			<div style="float:left;margin-left:20px;width:50%">
				<p style="font-size:12px;font-weight:bold;text-align:center">Form 12 A(Revised)</p>
				<p style="font-size:10px;text-align:center;font-weight:bold">EMPLOYEE'S PROVIDENT FUND AND MISC ACT 1952</p>
				<p style="font-size:10px;text-align:center;font-weight:bold">EMPLOYEE'S PENSION SCHEME, 1995 [PARA 20(4)]</p>
				<p style="font-size:10px;text-align:center">Currency period from 1st April 2009 to 31st March 2010</p>
				<p style="font-size:10px;text-align:center">Statement of contribution for the month of June 2009</p>
				<p style="font-size:10px;text-align:center">Statutory rate of contribution 12 %</p>
			</div>		
			

			<table width="98%">
				
				<tr>
					<td rowspan="2" align="center" class="reportHeading">Particulars<br/>(1)</td>
					<td rowspan="2" align="center" class="reportHeading">Wages on which contributions are payable<br/>(2)</td>
					<td align="center" class="reportHeading" colspan="2">Amount of contribution Payable<br/>(3)</td>
					<td align="center" class="reportHeading" colspan="2">Amount of contribution Remitted<br/>(4)</td>
					<td rowspan="2" align="center" class="reportHeading">Amount of administrative charges due<br/>(5)</td>
					<td rowspan="2" align="center" class="reportHeading">Amount of administrative charges remitted<br/>(6)</td>
					<td rowspan="2" align="center" class="reportHeading">Date of remittance (enclose triplicate copies of challan)<br/>(7)</td>
				</tr>
				
				<tr>
					<td align="center" class="reportHeading">Recovery from the workers</td>
					<td align="center" class="reportHeading">Payable by the employer</td>
					<td align="center" class="reportHeading">Worker's Share</td>
					<td align="center" class="reportHeading">Employer's Share</td>
				</tr>
				<%for(int i=0; i<2; i++){ %>
				<tr>
					<td align="right" class="reportLabel">1</td>
					<td align="right" class="reportLabel padRight20">2</td>
					<td align="right" class="reportLabel padRight20">3</td>
					<td align="right" class="reportLabel padRight20">5</td>
					<td align="right" class="reportLabel padRight20">5</td>
					<td align="right" class="reportLabel padRight20">6</td>
					<td align="right" class="reportLabel padRight20">7</td>
					<td align="right" class="reportLabel padRight20">8</td>
					<td align="right" class="reportLabel padRight20">9</td>
				</tr>
				<%} %>
			</table>


			<div style="width:30%;margin:10px;float:left">
				<div style="padding:10px 0 10px 20%;">
				Total No of employees
				</div>
				<div style="width: 30%; float: left;padding-left:10px">
					<p></p>
					<p>(a) Contract</p>
					<p>(b) Rest</p>
					<p>(b) Total</p>
				</div>
				<div style="border: 1px solid #ccc; width: 40%; float: left;padding-left:10px">
					<p></p>
					<p>Nil</p>
					<p>5</p>
					<p>5</p>
				</div>
			</div>


			<div style="width:100%;float:left">
				<table width="60%" style="border:solid 1px #ccc" cellspacing="0" cellpadding="3">
					<tr>
						<td style="border-bottom:solid 1px #ccc;border-right:solid 1px #ccc"><strong>Details of Subscriber</strong></td>
						<td align="right" class="padRight20" style="border-bottom:solid 1px #ccc;border-right:solid 1px #ccc"><strong>EPF</strong></td>
						<td align="right" class="padRight20" style="border-bottom:solid 1px #ccc;border-right:solid 1px #ccc"><strong>Pension Fund</strong></td>
						<td align="right" class="padRight20" style="border-bottom:solid 1px #ccc;"><strong>E.D.L.I.</strong></td>
					</tr>
					
					<tr>
						<td style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">No of Subscribers as per last month</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">5</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">5</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;">5</td>
					</tr>
					
					<tr>
						<td style="border-right:solid 1px #ccc">No of Subscribers (Vide Form 5)</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">0</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">0</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;">0</td>
					</tr>
					
					
					<tr>
						<td style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">No of Subscribers left service (Vide Form 10)</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">0</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;border-right:solid 1px #ccc">0</td>
						<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc;">0</td>
					</tr>
					
					<tr>
						<td style="border-right:solid 1px #ccc"><strong>(Net) Total no of Subscribers</strong></td>
						<td align="right" class="padRight20" style="border-right:solid 1px #ccc">5</td>
						<td align="right" class="padRight20" style="border-right:solid 1px #ccc">5</td>
						<td align="right" class="padRight20" style="">5</td>
					</tr>
					
				</table>
			</div>

    </div>
</div>
