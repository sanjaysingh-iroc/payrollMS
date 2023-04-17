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
	var strMonth = document.getElementById("strMonth").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strMonth='+strMonth+'&financialYear='+financialYear;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form10.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function generateForm10() {
	var financialYear=document.frm_from10.financialYear.value;
	var strMonth=document.frm_from10.strMonth.value;
	var f_org=document.frm_from10.f_org.value;
	
	var url='ITFormReports.action?formType=form10&financialYear='+financialYear;
	url+='&strMonth='+strMonth+'&f_org='+f_org;;
	window.location = url;
	} 
</script>
	<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
	String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
	List alList = (List)request.getAttribute("alList");
	String  strMonth = (String)request.getAttribute("strMonth");
	if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	
	Map<String, String>  hmOrg = (Map<String, String>)request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, String>();
	
	%>


<!-- Custom form for adding new records -->

		<div class="box-header with-border">
		    <h3 class="box-title">Form 10 for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
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
					<s:form name="frm_from10" action="Form10" theme="simple">
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
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" headerValue="Select Month" onchange="submitForm('2');" list="monthList" key="" />
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm10();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div> -->
 <div class="col-md-2 pull-right">
					
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
</div>


					
			<div>
				<p style="font-size:13px;font-weight:bold;text-align:center">Form 10</p>
				<p style="font-size:12px;text-align:center">THE EMPLOYEEs PROVIDENT FUND SCHEME, 1952 [PARA 36(2)(a)(b)]</p>
				<p style="font-size:12px;text-align:center">AND THE EMPLOYEES' PENSION SCHEME, 1995 [Para - 20 (2)]</p>
						
			</div>		
							
			<table width="100%">
				<tr>
					<td valign="top">Return of members leaving service during the month of <%=((strMonth!=null)?strMonth.toUpperCase():"")%></td>
				</tr>
				<tr>
					<td valign="top" width="20%">Name and address of the establishment</td>
					<td valign="top" width="50%"><%=uF.showData(hmOrg.get("ORG_NAME"), "")%><br/>
						<%=uF.showData(hmOrg.get("ORG_ADDRESS"), "")%>						
					</td>
					<td valign="top" width="20%">
						<p>Code No: <%=uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), "")%></p>
						<%-- <p>Group No: </p>
						<%if(strMonth!=null){ %>
						<p>Month: <%=strMonth%></p>
						<%}%> --%>
					</td>
				</tr>
			</table>
							
							
			<table width="100%" class="table table-bordered">
				
				<tr>
					<td align="center" class="reportHeading">Sr. No.<br/>(1)</td>
					<td align="center" class="reportHeading">Account Number<br/>(2)</td>
					<td align="center" class="reportHeading">Name of the Member<br/>(3)</td>
					<td align="center" class="reportHeading">Father's/Husband's Name<br/>(4)</td>
					<td align="center" class="reportHeading">Date of leaving service<br/>(5)</td>
					<td align="center" class="reportHeading">Reason for leaving<br/>(6)</td>
					<td align="center" class="reportHeading">Remarks<br/>(7)</td>
				</tr>
				<%
					for(int i=0; i<alList.size(); i++){
						List alInner = (List)alList.get(i);
						if(alInner==null)alInner=new ArrayList();
				%>
				
				<tr>
					<td align="center" class="reportLabel"><%=i+1%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(0), "")%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(1), "")%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(2), "")%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(3), "")%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(4), "")%></td>
					<td align="center" class="reportLabel"><%=uF.showData((String)alInner.get(5), "")%></td>
				</tr>
				
				<%}if(alList.size()==0){ %>
				<tr>
					<td colspan="7"><div class="msg nodata"><span>No employees found</span></div></td>
				</tr>
				<%}%>
				
				
			</table>
						
		</div>
		<!-- /.box-body -->
	</div>

    
   
 <%-- 
<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>