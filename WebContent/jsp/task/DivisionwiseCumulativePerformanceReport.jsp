<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">
	<script type="text/javascript" charset="utf-8">
	
		$(document).ready(function() {
			$("#f_strWLocation").multiselect().multiselectfilter();
			$("#f_department").multiselect().multiselectfilter();
			$("#f_service").multiselect().multiselectfilter();
			$("#f_level").multiselect().multiselectfilter();
		});
	
		function submitForm(type) {
			
			document.frm_DivisionwiseCumulativePerformanceReport.exportType.value='';
			var org = document.getElementById("f_org").value;
			/* var strMonth = document.getElementById("strMonth").value; */
			var financialYear = document.getElementById("financialYear").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				/* paramValues = '&strLocation='+location+'&strMonth='+strMonth+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level; */
				paramValues = '&strLocation='+location+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			//alert("paramValues ===>> " + paramValues);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'DivisionwiseCumulativePerformanceReport.action?f_org='+org+paramValues,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	//console.log(result);
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
		
		function generateReportExcel(){
			document.frm_DivisionwiseCumulativePerformanceReport.exportType.value='excel';
			document.frm_DivisionwiseCumulativePerformanceReport.submit();
		}
	
	    
	</script>
	
	<%
	
		UtilityFunctions uF = new UtilityFunctions();
		//List alSbuList = (List) request.getAttribute("alSbuList");
		Map<String, String> hmSbuListMap = (Map<String, String>) request.getAttribute("hmSbuListMap");
		if(hmSbuListMap == null) hmSbuListMap = new HashMap<String, String>();
		
		Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
		if(hmReportData == null) hmReportData = new HashMap<String, String>();
		
	%>
	
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px; color:#232323;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<s:form name="frm_DivisionwiseCumulativePerformanceReport" id="frm_DivisionwiseCumulativePerformanceReport" action="DivisionwiseCumulativePerformanceReport" theme="simple" method="post">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
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
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								
							</div>
						</div><br>
						 <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
					      		</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" headerKey="0" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
	          <!-- /.box-body -->
			</div>
		</div>
		<div class="box-body">
			<div class="col-md-2 pull-right">
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
		</div>
		<!-- 54 -->
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<table cellspacing="1" class="table table-bordered" id=lt1>
				<tr>
					<th class="alignLeft" colspan="<%=(hmSbuListMap.size()*3+1) %>" ><%= uF.showData((String) request.getAttribute("orgName"),"") %> CHARTERED ACCOUNTANTS</th>
				</tr>
				<tr>
					<th class="alignLeft" colspan="<%=hmSbuListMap.size()*3+1 %>" >DIVISIONWISE DETAILED ANALYSIS OF CUMULATIVE PERFORMANCE FOR THE YEAR <%= uF.showData((String) request.getAttribute("strFinancialYear"),"") %></th>
				</tr>
				<tr>
					<th class="alignLeft" colspan="<%=hmSbuListMap.size()*3+1 %>" >RS. IN LACS</th>
				</tr>
				<tr>
					<th class="alignCenter" rowspan="2">PARTICULARS</th>
					<%-- <% for(int i=0; i<hmSbuListMap.size(); i++){ %>
						<th class="alignCenter" colspan="3"><%=hmSbuListMap.get(i) %></th>
					<%} %> --%>
					
					<%
						StringBuilder sbTC = new StringBuilder();
						StringBuilder sbTNC = new StringBuilder();
						StringBuilder sbEmpCost = new StringBuilder();
						StringBuilder sbOPE = new StringBuilder();
						StringBuilder sbPE = new StringBuilder();
						StringBuilder sbTotRAct = new StringBuilder();
						StringBuilder sbTotal = new StringBuilder();
						StringBuilder sbContribution = new StringBuilder();
						
						Iterator<String> it = hmSbuListMap.keySet().iterator();
						while (it.hasNext()) {
							String sbuId = it.next();
							
							sbTC.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_CHARGEBLE"),"0")+"</td>"+"<td class=\"alignRight\">"+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_CHARGEBLE"),"0")+"</td>");
							
							sbTNC.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_NON_CHARGEBLE"),"0")+"</td>"+"<td class=\"alignRight\">"+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_NON_CHARGEBLE"),"0")+"</td>");
							
							sbEmpCost.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_EMP_COST"),"0")+"</td>"+"<td class=\"alignRight\">"+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_EMP_COST"),"0")+"</td>");
							
							sbOPE.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_OTHER_DIRECT_COST"),"0")+"</td>"+"<td class=\"alignRight\">"+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_OTHER_DIRECT_COST"),"0")+"</td>");
							
							sbPE.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_PROFESSIONAL_COST"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_BUDGET_PROFESSIONAL"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_PROFESSIONAL_COST"),"0")+"</td>");
							
							sbTotRAct.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_TOTAL_REC_ACTUAL"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_BUDGET_REC_TOTAL"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_TOTAL_REC"),"0")+"</td>");
							
							sbTotal.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_TOTAL_ACTUAL"),"0")+"</td>"+"<td class=\"alignRight\">"+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_TOTAL"),"0")+"</td>");
							
							sbContribution.append("<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_ACTUAL_CONTRIBUTION"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_BUDGET_CONTRIBUTION"),"0")+"</td>"
									+"<td class=\"alignRight\">"+uF.showData(hmReportData.get(sbuId+"_LASTYR_CONTRIBUTION"),"0")+"</td>");
					%>
						<th class="alignCenter" colspan="3"><%=hmSbuListMap.get(sbuId) %></th>
					<% } %>
				</tr>
				<tr>
					
					<% for(int i=0; i<hmSbuListMap.size(); i++){ %>
						<th class="alignCenter" >Actual</th>
						<th class="alignCenter" >Budget</th>
						<th>Last Yr</th>
					<%} %>
				</tr>
				<tr>
				<th class="alignLeft">Receipts</th>
				</tr>
				<tr>
				<td class="alignLeft">a) Professional Fees</td>
				<%=sbPE.toString() %>
				</tr>
				<tr>
				<td class="alignLeft">b) Out Of Pocket</td>
				<%=sbOPE.toString() %>
				</tr>
				<tr>
				<th class="alignLeft">Total Receipts</th>
				<%=sbTotRAct.toString() %>
				</tr>
				<tr>
				<th class="alignLeft">Direct Expenses</th>
				</tr>
				<tr>
				<td class="alignLeft">a) Partners related</td>
				</tr>
				<tr>
				<td class="alignLeft">b) Employees Cost</td>
				<%=sbEmpCost.toString() %>
				</tr>
				<tr>
				<td class="alignLeft">c) Travelling N.C.</td>
				<%=sbTNC.toString() %>
				</tr>
				<tr>
				<td class="alignLeft">d) Travelling C.</td>
				<%=sbTC.toString() %>
				</tr>
				<tr>
				<th class="alignLeft">TOTAL</th>
				<%=sbTotal.toString() %>
				</tr>
				<tr>
				<th class="alignLeft">Contribution</th>
				<%=sbContribution.toString() %>
				</tr>
				<tr>
				<th class="alignLeft">Direct Expenses</th>
				</tr>
				<tr>
				<td class="alignLeft">e) I.T. 7% on Receipt</td>
				</tr>
				<tr>
				<td class="alignLeft">f) Facilities exp</td>
				</tr>
				<tr>
				<td class="alignLeft">g) Admin & Office</td>
				</tr>
				<tr>
				<th class="alignLeft">Total Indirect Expenses</th>
				</tr>
				<tr>
				<th class="alignLeft">NET SURPLUS</th>
				</tr>
			</table>
		</div>
		
	</div>

</div>