<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<div id="divResult">

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String totalAmountDue = (String) request.getAttribute("totalAmountDue");
	String unpaidamount = (String) request.getAttribute("unpaidamount");
	String paidamount = (String) request.getAttribute("paidamount");
	String payMonts = (String) request.getAttribute("payMonts");
	String payYear = (String) request.getAttribute("payYear");
	String amountprinted = (String) request.getAttribute("amountprinted");
	// String printdate = (String)request.getAttribute("printdate");
	Map hmPrintTotal = (Map) request.getAttribute("hmPrintTotal");
	List<String> dateList = (List<String>) request.getAttribute("dateList");
	List<String> paidDateList = (List<String>) request.getAttribute("paidDateList");
	Map hmPrintPaidTotal = (Map) request.getAttribute("hmPrintPaidTotal");
	String strCurrency = (String) request.getAttribute("strCurrency");
%>
<script> 
	
	function deleteChallan(date) {
 		//alert("in deleteChallan");
 		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		
		//alert("sbEmp="+sbEmp);
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	   	$.ajax({
   			url : 'ESICUpdateChallanData.action?operation=del&challanDate='+date+'&f_org='+f_org
   					+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp,
   					
   			cache : false,
   			success : function(res) {
   				$("#divResult").html(res);
   			},
			error: function(result){
				$.ajax({
					url: 'ESICChallan.action',
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
   		});
	}

	
	function generatePdf(date){
		//alert(date);
	//	alert("in generatePdf");
 		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		
		var url='ESICUpdateChallanData.action?operation=pdf&challanDate='+date+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&financialYear='+financialYear+'&sbEmp='+sbEmp;
		window.location = url;
	}
	
	function generatePaidChallanPdf(challannum) {
		//alert(challannum);
		
		//alert("in generatePaidChallanPdf");
 		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
 		
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		
		var url='ESICUpdateChallanData.action?operation=pdf&challanNum='+challannum+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&financialYear='+financialYear+'&sbEmp='+sbEmp;
		//alert("url="+url);
		window.location = url;
	}
	
	function generateExcel(date){
		
	//	alert("in generateExcel");
 		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		
		var url='ESICUpdateChallanData.action?operation=excel&challanDate='+date+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&financialYear='+financialYear+'&sbEmp='+sbEmp;
	//	alert("url=="+url);
		window.location = url;
	}
	
	
	function challanPrintedButNotPaid(){
		
	//	alert("in challanPrintedButNotPaid");
 		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
 		
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		var	strMonth='<%=(String) request.getAttribute("months")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		
		var url1='ESICChallanDetailsView.action?printAction=printedNotPaid&financialYear='+financialYear;
		url1+="&strMonth="+strMonth+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp;
		
	//	alert("url1="+url1);
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Printed Challan');
		 $.ajax({
			url : url1,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
function payChallan(challanDate,amount){
	
	// alert("in payChallan");
		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		
	var financialYear=document.frm_fromESICChallan.financialYear.value;
	var f_org=document.frm_fromESICChallan.f_org.value;
	var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
	
	var url1='ESICChallanDetailsView.action?printAction=payChallan&challanDate='+challanDate;
	url1+='&payAmount='+amount;
	url1+='&financialYear='+financialYear+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp;
	
	// alert("url1=="+url1);
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Enter Challan Details');
	 $.ajax({
		url : url1,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
	
	function amountPaidDetails() {
		
	//	alert("in amountPaidDetails");
		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		var	strMonth='<%=(String) request.getAttribute("months")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		
		var url1='ESICChallanDetailsView.action?printAction=amountPaid&financialYear='+financialYear;
		url1+="&strMonth="+strMonth+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp;
	//	alert("url1==>"+url1);
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Challan Details');
		 $.ajax({
			url : url1,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 

	function challanPrintedDetails() {
		
	//	alert("in challanPrintedDetails");
		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		var	strMonth='<%=(String) request.getAttribute("months")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		
		var url1='ESICChallanDetailsView.action?printAction=printedAmount&financialYear='+financialYear;
		url1+="&strMonth="+strMonth+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp;
		
	//	alert("url1=="+url1);
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Challan Details');
		 $.ajax({
			url : url1,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 
	

	function challanUnpaidDetails() {
		
	//	alert("in amountPaidDetails");
		var sbEmp='<%=(String)request.getAttribute("sbEmp1")%>';
		
		var financialYear=document.frm_fromESICChallan.financialYear.value;
		var	strMonth='<%=(String) request.getAttribute("months")%>';
		var f_org=document.frm_fromESICChallan.f_org.value;
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		
		var url1='ESICChallanDetailsView.action?printAction=totalAmount&financialYear='+financialYear;
		url1+="&strMonth="+strMonth+'&f_org='+f_org+'&f_strWLocation='+f_strWLocation+'&sbEmp='+sbEmp;
		
	//	alert("url1=="+url1);
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Challan Details');
		 $.ajax({
			url : url1,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 
	
</script>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#strMonth").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_emptype").multiselect().multiselectfilter();
});

function submitForm(type){
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmpType = getSelectedValue("f_emptype");
	
	
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = getSelectedValue("strMonth");
	var paramValues = "";
	if(type == '2') {
		var f_strWLocation = document.frm_fromESICChallan.f_strWLocation.value;
		//paramValues = '&financialYear='+financialYear+'&strMonth1='+strMonth+'&f_strWLocation='+f_strWLocation;
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&financialYear='+financialYear+'&strMonth1='+strMonth+'&strEmpType='+strEmpType;

	}
	
	//alert("paramValues ===>> " + paramValues);
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ESICChallan.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}

	$(function() {
		$("body").on('click','#closeButton',function(){
			$(".modal-dialog").removeAttr('style'); 
			$(".modal-body").height(400);
			$("#modalInfo").hide();
	    });
		$("body").on('click','.close',function(){
			$(".modal-dialog").removeAttr('style');
			$(".modal-body").height(400);
			$("#modalInfo").hide();
		});
	});
</script>

<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #c5c5c5;
	width: auto;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #c5c5c5;
	background: #efefef;
	width: auto;
}

.p tr td.head {
	background: #efefef;
}

.graphv_red {
	background: #f00;
	width: 100px;
	float: left;
	margin: 0px 10px 0px 0px;
	height: 15px;
	line-height: 15px;
	color: #fff;
	padding: 4px;
}

.graphv_yellow {
	background: #ff0;
	width: 100px;
	float: left;
	margin: 0px 10px 0px 0px;
	height: 15px;
	padding: 4px;
	line-height: 15px;
}

.graphv_blue {
	background: #00f;
	width: 300px;
	float: left;
	margin: 0px 10px 0px 0px;
	height: 15px;
	color: #fff;
	padding: 4px;
	line-height: 15px;
}

.padtop {
	padding-top: 10px;
}
</style>

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
					<s:form name="frm_fromESICChallan" id="frm_fromESICChallan" action="ESICChallan" theme="simple">
						<s:hidden name="navigationId" id="navigationId"/>
						<s:hidden name="toPage" id="toPage"/>
						<%-- <div class="row row_without_margin">
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
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="width: 200px;" 
										list="wLocationList" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="submitForm('2');" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" multiple="true"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div> --%>
						<!--*******************************************************************  -->
						
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
										<p style="padding-left: 5px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Employee Type</p>
										<s:select theme="simple" name="f_emptype" id="f_emptype" listKey="empTypeId" cssStyle="float:left;margin-right: 10px;width:200px;" 
										listValue="empTypeName" multiple="true" list="empTypeList" key="" />
									</div>
							</div>
						</div>
					
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');" />
								</div>
	
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" multiple="true"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
	
					<!-- ******************************************** -->	
						
						
						
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
		
				
				<div style="text-align: center; margin: 10px"><strong>Payment Status for <%=uF.showData(payMonts, "")%></strong></div>

				<div class="row row_without_margin">
					<div class="col-lg-4 col-md-6 col-sm-12">
					<table class="tb_style">
						<tr>
							<th style="width: 60%">Total Amount Due <%=uF.showData(strCurrency, "")%></th>
							<td align="right"><b><%=uF.showData(totalAmountDue, "")%></b><br /></td>
						</tr>
						<tr>
							<th>Amount Paid <%=uF.showData(strCurrency, "")%></th>
							<td align="right"><a href="javascript:void(0)" onclick="amountPaidDetails()"> <%=uF.showData(paidamount, "")%></a><br /></td>
						</tr>
						<tr>
							<th>Challan Printed (not paid) <%=uF.showData(strCurrency, "")%></th>
							<td align="right"><a href="javascript:void(0)" onclick="challanPrintedButNotPaid()"> <%=uF.showData(amountprinted, "")%></a><br /></td>
						</tr>
						<tr>
							<th>Amount Unpaid <%=uF.showData(strCurrency, "")%></th>
							<td align="right"><a href="javascript:void(0)" onclick="challanUnpaidDetails()"> <%=uF.showData(unpaidamount, "")%></a><br /></td>
						</tr>
					</table>
					</div>
				</div>

				<div class="padtop"></div>

				<div class="row row_without_margin">
					<div class="col-lg-12 col-md-12 col-sm-12">
						<table class="tb_style" style="width: 100%">
							<tr>
								<th>Challan details</th>
								<th>Generated Challan</th>
								<th>Generate File</th>
								<th>Acknowledgement</th>
								<th>Challan No.</th>
							</tr>
							<%
								int count = 0;
								if (dateList != null && hmPrintTotal != null) {
									for (int i = 0; i < dateList.size(); i++) {
										count++;
							%>
							<tr>
								<td>Challan printed on <%=uF.getDateFormat(dateList.get(i), IConstants.DBDATE, CF.getStrReportDateFormat())%>
									for <strong><%=uF.showData(strCurrency, "")%> <%=hmPrintTotal.get(dateList.get(i))%></strong>
								</td>
								<td><a href="javascript:void(0)" style="float: left;" title="Delete Unpaid Challan" class="del" onclick="(confirm('Are you sure you want to delete this challan?')?deleteChallan('<%=(String) dateList.get(i)%>'):'')"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
									<a href="javascript:void(0)" title="Download Unpaid Challan" class="fa fa-file-pdf-o" onclick="generatePdf('<%=(String) dateList.get(i)%>')"></a>
								</td>
								<!-- <td><a href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;" title="export to Excel" class="excel" onclick="generateExcel('<%=(String) dateList.get(i)%>')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></td> -->
								<td><a onclick="generateExcel('<%=(String)dateList.get(i) %>');" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a></td>

								<td><a href="javascript:void(0)" title="Click to enter challan details" onclick="payChallan('<%=(String) dateList.get(i)%>','<%=hmPrintTotal.get(dateList.get(i))%>')">Pay</a></td>
								<td></td>
							</tr>
		
							<% }
								}
							%>
		
							<%
								if (paidDateList != null && hmPrintPaidTotal != null) {
									for (int i = 0; i < paidDateList.size(); i++) {
										count++;
							%>
							<tr>
								<td>Challan paid on <%=uF.getDateFormat(paidDateList.get(i), IConstants.DBDATE, CF.getStrReportDateFormat())%>
									for <strong><%=uF.showData(strCurrency, "")%> <%=hmPrintPaidTotal.get(paidDateList.get(i) + "_" + i)%></strong>
								</td>
								<td><a href="javascript:void(0)" title="Download Paid Challan" class="fa fa-file-pdf-o" onclick="generatePaidChallanPdf('<%=hmPrintPaidTotal.get(paidDateList.get(i) + "_" + i + "_CHALLANNUM")%>')">pdf</a></td>
								<td></td>
								<td></td>
								<td><%=hmPrintPaidTotal.get(paidDateList.get(i) + "_" + i + "_CHALLANNUM")%></td>
							</tr>
							<% }
							}
							%>
		
							<% if (count == 0) { %>
							<tr>
								<td colspan="5"><div class="msg nodata"><span>No challan details available for the current selection.</span></div></td>
							</tr>
							<% } %>
						</table>
					</div>
				</div>
		</div>
		<!-- /.box-body -->

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	
</div>