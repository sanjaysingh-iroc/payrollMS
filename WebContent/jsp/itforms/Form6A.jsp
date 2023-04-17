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
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_level = document.getElementById("f_level").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_level='+f_level+'&financialYear='+financialYear;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form6A.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


function generateForm6A(){
	
	var financialYear=document.frm_from16.financialYear.value;
	var f_org=document.frm_from16.f_org.value;
	var f_level=document.frm_from16.f_level.value;
	var url='ITFormReports.action?formType=form6A&financialYear='+financialYear+'&f_org='+f_org+'&f_level='+f_level;
	window.location = url;
}
</script>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
Map  hmDetails = (Map)request.getAttribute("hmDetails");
Map  hmDetailsTotal = (Map)request.getAttribute("hmDetailsTotal");
Map  hmEmployeeDetails = (Map)request.getAttribute("hmEmployeeDetails");

Map<String, String>  hmEarningTotal = (Map<String, String>)request.getAttribute("hmEarningTotal");
if(hmEarningTotal == null) hmEarningTotal = new HashMap<String, String>();

Map<String, String>  hmOrg = (Map<String, String>)request.getAttribute("hmOrg");
if(hmOrg == null) hmOrg = new HashMap<String, String>();
 
if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
	strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
}

%>


<!-- Custom form for adding new records -->

		<div class="box-header with-border">
		    <h3 class="box-title">Form 6A for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
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
					<s:form name="frm_from16" action="Form6A" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" headerValue="All Levels" headerKey="0" onchange="submitForm('2');" list="levelList" key=""/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm6A();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div>  -->
<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>				
			<div style="float:left;width:30%;margin-top: 20px;">
				<div style="padding:12px;border:1px solid #ccc;margin-bottom:20px">	
					<p style="font-size:13px;font-weight:bold;">Name and address of the establishment</p>
					<p style="font-size:13px;font-weight:bold;"><%=uF.showData(hmOrg.get("ORG_NAME"), "")%></p>
					<p style="font-size:13px;font-weight:bold;"><%=uF.showData(hmOrg.get("ORG_ADDRESS"), "")%></p>
				</div>
				
				<div style="padding:12px;border:1px solid #ccc">	
					<p style="font-size:13px;font-weight:bold;">Code no of the establishment: <%=uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "")%></p>
				</div>
			</div>
				
			<div style="float:left;margin-left:20px;width:50%;margin-top: 20px;">
				<p style="font-size:13px;font-weight:bold;text-align:center">Form 6A (Revised)</p>
				<p style="font-size:12px;font-weight:bold;text-align:center">(for unexempted Establishments Only)</p>
				<p style="font-size:12px;text-align:center">THE EMPLOYEES' PROVIDENT FUND SCHEME, 1952(PARA 43)</p>
				<p style="font-size:12px;text-align:center">AND</p>
				<p style="font-size:12px;text-align:center">Annual statement of contribution for the currency period form <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></p>
				<br/><br/>
				
				<p style="font-size:12px;font-weight:bold;text-align:center">THE EMPLOYEES PENSION SCHEME, 1995 [PARA 20(4)]</p>
				<p style="font-size:12px;font-weight:bold;text-align:center">Statutory rate of contribution 12%</p>
				<p style="font-size:12px;font-weight:bold;text-align:center">No. of members voluntarily contributing at the higher rate</p>
			</div>		

				
			<table width="100%" class="table table-bordered">
				<tr>
					<td rowspan="2" align="center" class="reportHeading">Sr. No.<br/>(1)</td>
					<td rowspan="2" align="center" class="reportHeading">Account Number<br/>(2)</td>
					<td rowspan="2" align="center" class="reportHeading">Name of the Member<br/>(3)</td>
					<td rowspan="2" align="center" class="reportHeading">Wages, Retaining allowance (if any) & D.A. including cash value of food concession paid during currency period<br/>(4)</td>
					<td rowspan="2" align="center" class="reportHeading">Amount of workers contributions deducted from the wages<br/>(5)</td>
					<td align="center" class="reportHeading" colspan="2">Employer's Contribution</td>
					<td rowspan="2" align="center" class="reportHeading">Refund of advances<br/>(8)</td>
					<td rowspan="2" align="center" class="reportHeading">Rate of higher voluntary contribution (if any)<br/>(9)</td>
					<td rowspan="2" align="center" class="reportHeading">Remarks<br/>(10)</td>
				</tr>
				<tr>
					<td align="center" class="reportHeading">E.P.F. difference between <%=uF.showData((String)request.getAttribute("EPF_PER"), "0")%>% &amp; <%=uF.showData((String)request.getAttribute("ERPS_PER"), "0")%>%<br/>(6)</td>
					<td align="center" class="reportHeading">Pension Fund contribution <%=uF.showData((String)request.getAttribute("ERPS_PER"), "0")%>%<br/>(7)</td>
				</tr>
				<%
					Set set = hmDetails.keySet();
					Iterator it = set.iterator();
					int count=0;
					while(it.hasNext()){
						count++;
						String strEmpId = (String)it.next();
						Map hmInner = (Map)hmDetails.get(strEmpId);
						if(hmInner==null)hmInner=new HashMap();
						
						Map hmInnerEmpDetails = (Map)hmEmployeeDetails.get(strEmpId);
						if(hmInnerEmpDetails==null)hmInnerEmpDetails=new HashMap();
				 %>
				
				<tr>
					<td width="5%" align="center" class="reportLabel"><%=count%></td>
					<td width="10%" align="left" class="reportLabel padRight20" nowrap="nowrap"><%=uF.showData((String)hmInnerEmpDetails.get("EPF_ACC_NO"),"")%></td>
					<td width="15%" align="left" class="reportLabel padRight20"><%=uF.showData((String)hmInnerEmpDetails.get("NAME"),"")%></td>
					<td width="10%" align="right" class="reportLabel padRight20"><%=uF.showData((String)hmEarningTotal.get(strEmpId),"")%></td>
					<td width="10%" align="right" class="reportLabel padRight20"><%=uF.showData((String)hmInner.get("EMPLOYEE_SHARE"),"")%></td>
					<td width="10%" align="right" class="reportLabel padRight20"><%=uF.showData((String)hmInner.get("EMPLOYER_SHARE_EPF"),"")%></td>
					<td width="10%" align="right" class="reportLabel padRight20"><%=uF.showData((String)hmInner.get("EMPLOYER_SHARE_EPS"),"")%></td>
					<td width="10%" align="right" class="reportLabel padRight20"></td>
					<td width="10%" align="right" class="reportLabel padRight20"></td>
					<td width="10%" align="left" class="reportLabel padRight20"></td>
				</tr>
				
				<%}
					if(count==0){
				%>
				<tr><td colspan="11"><div class="msg nodata"><span>No record found for the current selection.</span></div></td></tr>
				<%}else{ %>
				<tr>
					<td align="center" class="" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"><strong><%=uF.showData((String)hmDetailsTotal.get("TOTAL_EARNING"),"")%></strong></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"><strong><%=uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYEE_SHARE"),"")%></strong></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"><strong><%=uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYER_SHARE_EPF"),"")%></strong></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"><strong><%=uF.showData((String)hmDetailsTotal.get("TOTAL_EMPLOYER_SHARE_EPS"),"")%></strong></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
					<td align="right" class="padRight20" style="border-top:1px solid #000;border-bottom:1px solid #000"></td>
				</tr>
				<%} %>
			</table>
		</div>
		<!-- /.box-body -->
	</div>
