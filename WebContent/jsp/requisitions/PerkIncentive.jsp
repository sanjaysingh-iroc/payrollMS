<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String strUserTYpe = (String) session
			.getAttribute(IConstants.USERTYPE);
%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script type="text/javascript">
$(function(){
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		
		$("#lt").DataTable({
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

function isNumberKey(evt){
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkPerkAmount(val,maxVal,id){
	var val1 = parseFloat(val);
	var maxVal1 = parseFloat(maxVal);
	
	if(maxVal1 > 0 && val1 > maxVal1){
		alert('Entered amount is more than perk limit amount.');
		document.getElementById(id).value = '';
	}
}

function selectall(x,strEmpId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){ 
  		arr[i].checked=status;
 	}
}

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&financialYear='+financialYear
		+'&strMonth='+strMonth;
	}
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PerkIncentive.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#subDivResult").html(result);
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

/* function submitForm(){
	document.frm_PerkIncentive.submit();
} */


function approvePerkIncentive(){
	if(confirm('Are you sure, you want to approve Perk Incentive of selected employee?')){
		var data = $("#frm_PerkIncentive").serialize();
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'PerkIncentive.action?strApprove=Approve',
			data: data,
			success: function(result){
	        	$("#subDivResult").html(result); 
	   		}
		});
		
	}		
}

</script>


<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include>  --%>


		<%-- <% if (strUserTYpe != null && !strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
				<ul class="nav nav-tabs">
					<li><a href="javascript:void(0)" onclick="window.location='Perks.action'" data-toggle="tab">Approve</a></li>
					<li class="active"><a href="javascript:void(0)" onclick="window.location='PerkIncentive.action'" data-toggle="tab">Apply</a></li>
					<li><a href="javascript:void(0)" onclick="window.location='PerkInSalary.action'" data-toggle="tab">Approve Perk In Salary</a></li>
				</ul>
		<% } %> --%>
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height:600px;">
		<s:form name="frm_PerkIncentive" id="frm_PerkIncentive" action="PerkIncentive" theme="simple" method="post">
			<div class="box box-default collapsed-box">
				<div class="box-header with-border">
					<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
    					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
    					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				
			    <div class="box-body" style="padding: 5px; overflow-y: auto;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"	onchange="submitForm('1');" list="organisationList" key="" />											
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select theme="simple" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" onchange="submitForm('2');" list="financialYearList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key="" />
							</div>
						</div>
					</div><br>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row row_without_margin">
				<div class="col-lg-12">
					<input type="button" value="Approve" name="strApprove" class="btn btn-primary" onclick="approvePerkIncentive();"/>
				</div>
			</div>

				<%
					List<String> alEmp = (List<String>) request.getAttribute("alEmp");
					if (alEmp == null)
						alEmp = new ArrayList<String>();
					Map<String, Map<String, String>> hmEmpData = (Map<String, Map<String, String>>) request.getAttribute("hmEmpData");
					if (hmEmpData == null)
						hmEmpData = new HashMap<String, Map<String, String>>();
					Map<String, List<Map<String, String>>> hmPerkLevel = (Map<String, List<Map<String, String>>>) request.getAttribute("hmPerkLevel");
					if (hmPerkLevel == null)
						hmPerkLevel = new HashMap<String, List<Map<String, String>>>();
					Map<String, String> hmAppliedPerk = (Map<String, String>) request.getAttribute("hmAppliedPerk");
					if (hmAppliedPerk == null)
						hmAppliedPerk = new HashMap<String, String>();
					Map<String, String> hmApprovedPerk = (Map<String, String>) request.getAttribute("hmApprovedPerk");
					if (hmApprovedPerk == null)
						hmApprovedPerk = new HashMap<String, String>();
					Map<String, String> hmPaidPerk = (Map<String, String>) request.getAttribute("hmPaidPerk");
					if (hmPaidPerk == null)
						hmPaidPerk = new HashMap<String, String>();
				%>
			<div style="margin-top: 10px;">
				<table class="table table-bordered" id="lt">
					<thead>
					<tr>
						<th class="textcenter no-sort"><input type="checkbox" onclick="selectall(this,'strIsAssignEmpId');" checked="checked" /></th>
						<th class="textcenter">Employee Name</th>
						<th class="textcenter">Perk Policy</th>
						<th class="textcenter">Perk Payment Type</th>
						<th class="textcenter">Perk Limit</th>
						<th class="textcenter">Applied Amount</th>
						<th class="textcenter">Amount</th>
						<th class="textcenter">Approved Amount</th>
						<th class="textcenter">Paid Amount</th>
					</tr>
					</thead>
					<tbody>
						<%
							int i = 0;
							for (; alEmp != null && i < alEmp.size(); i++) {
								String strEmpId = alEmp.get(i);
								Map<String, String> hmEmp = (Map<String, String>) hmEmpData.get(strEmpId);
								if (hmEmp == null)
									hmEmp = new HashMap<String, String>();

								String strLevelId = hmEmp.get("EMP_LEVEL");
								List<Map<String, String>> alPerkList = (List<Map<String, String>>) hmPerkLevel.get(strLevelId);
								if (alPerkList == null)
									alPerkList = new ArrayList<Map<String, String>>();

								for (int j = 0; j < alPerkList.size(); j++) {
									Map<String, String> hmPerk = (Map<String, String>) alPerkList.get(j);
									if (hmPerk == null)
										hmPerk = new HashMap<String, String>();

									String strPerkPaymentType = "";
									if (hmPerk.get("PERK_PAYMENT_CYCLE") != null && hmPerk.get("PERK_PAYMENT_CYCLE").trim().equals("M")) {
										strPerkPaymentType = "Monthly";
									} else if (hmPerk.get("PERK_PAYMENT_CYCLE") != null && hmPerk.get("PERK_PAYMENT_CYCLE").trim().equals("A")) {
										strPerkPaymentType = "Annual";
									}
									double dblRemainingAmt = uF.parseToDouble(hmPerk.get("PERK_MAX_AMOUNT")) - uF.parseToDouble(hmAppliedPerk.get(strEmpId+ "_" + hmPerk.get("PERK_ID")));
						%>
						<tr>
							<td>
								<input type="checkbox" name="strIsAssignEmpId" value="<%=strEmpId + "_" + hmPerk.get("PERK_ID")%>" checked />
							</td>
							<td><%=hmEmp.get("EMP_NAME")%></td>
							<td><%=hmPerk.get("PERK_NAME") + "[" + hmPerk.get("PERK_CODE") + "]"%></td>
							<td style="text-align: center;"><%=strPerkPaymentType%></td>
							<td style="text-align: right; background-color: #eee;"><%=uF.parseToDouble(hmPerk.get("PERK_MAX_AMOUNT"))%></td>
							<td style="text-align: right; background-color: #eee;"><%=uF.parseToDouble(hmAppliedPerk.get(strEmpId + "_" + hmPerk.get("PERK_ID")))%></td>
							<td style="text-align: center;">
								<input style="width: 75px; text-align: right" type="text" id="idStrPerkAmount_<%=strEmpId + "_" + hmPerk.get("PERK_ID")%>" name="strPerkAmount_<%=strEmpId + "_" + hmPerk.get("PERK_ID")%>" value="" onkeyup="checkPerkAmount(this.value,'<%=dblRemainingAmt%>','idStrPerkAmount_<%=strEmpId + "_" + hmPerk.get("PERK_ID")%>')" onkeypress="return isNumberKey(event)">
							</td>
							<td style="text-align: right; background-color: #eee;"><%=uF.parseToDouble(hmApprovedPerk.get(strEmpId + "_" + hmPerk.get("PERK_ID")))%></td>
							<td style="text-align: right; background-color: #eee;"><%=uF.parseToDouble(hmPaidPerk.get(strEmpId + "_" + hmPerk.get("PERK_ID")))%></td>
						</tr>
						<% } %>
						<% }
							if (i == 0) {
						%>
						<tr>
							<td colspan="9">
								<div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div>
							</td>
						</tr>
						<% } %>
					</tbody>
				</table>
			</div>
		</s:form>
		</div>
		<!-- /.box-body -->
