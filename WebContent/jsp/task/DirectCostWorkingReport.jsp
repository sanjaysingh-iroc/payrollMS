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
			
			document.frm_DirectCostWorkingReport.exportType.value='';
			var org = document.getElementById("f_org").value;
			var strMonth = document.getElementById("strMonth").value;
			var financialYear = document.getElementById("financialYear").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				paramValues = '&strLocation='+location+'&strMonth='+strMonth+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			//alert("paramValues ===>> " + paramValues);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'DirectCostWorkingReport.action?f_org='+org+paramValues,
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
			document.frm_DirectCostWorkingReport.exportType.value='excel';
			document.frm_DirectCostWorkingReport.submit();
		}
	
	    
	</script>
	<%-- <%
		List alPartnerList = (List) request.getAttribute("alPartnerList");
		List monthYearsList = (List) request.getAttribute("monthYearsList");
		UtilityFunctions uF = new UtilityFunctions();
		
		List<List<String>> alOuter = (List<List<String>>) request.getAttribute("alOuter");
		System.out.println("alOuter="+alOuter);
	%> --%>
	<%
		
		List monthYearsList = (List) request.getAttribute("monthYearsList");
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, String> hmPartner = (Map<String, String>) request.getAttribute("hmPartner");
		if(hmPartner == null) hmPartner = new HashMap<String, String>();
		
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
					<s:form name="frm_DirectCostWorkingReport" id="frm_DirectCostWorkingReport" action="DirectCostWorkingReport" theme="simple" method="post">
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
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" headerKey="0" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div>
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
		
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			<table cellspacing="1" class="table table-bordered" id=lt1>
				
				<tr>
					<th></th>
					<th class="alignCenter" colspan="5">Total KPCA</th>
					
					
					<%
						List<String> alPartnerId = new ArrayList<String>();
						Iterator<String> it = hmPartner.keySet().iterator();
						while (it.hasNext()) {
							String partnerId = it.next();
							alPartnerId.add(partnerId);
					%>
					
					<th class="alignCenter" colspan="5" ><%=hmPartner.get(partnerId) %></th>
					<% } %>
					
				</tr>
				<tr>
					<th class="alignCenter">Particulars</th>
					<th class="alignCenter">Employee Cost</th>
					<th class="alignCenter">Prof Fees</th>
					<th class="alignCenter">Travelling chargeble</th>
					<th class="alignCenter">Travelling Non Charg</th>
					<th class="alignCenter">Other Direct cost</th>
					<% for(int j=0; j<alPartnerId.size(); j++){ %>
						<th class="alignCenter">Employee Cost</th>
						<th class="alignCenter">Prof Fees</th>
						<th class="alignCenter">Travelling chargeble</th>
						<th class="alignCenter">Travelling Non Charg</th>
						<th class="alignCenter">Other Direct cost</th>
					<% } %>
				</tr>
				
				<% for(int k=0; k<monthYearsList.size();k++){ %>
					<tr>
						<td class="alignLeft"><%=uF.getDateFormat(monthYearsList.get(k)+"","MM/yyyy","MMMM") %></td>
						
						
						<td class="alignRight"><%=uF.showData(hmReportData.get(monthYearsList.get(k)+"__EMP_COST_TOT"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(monthYearsList.get(k)+"_PROFESSIONAL_TOT"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(monthYearsList.get(k)+"_CHARGEBLE_TOT"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(monthYearsList.get(k)+"_NON_CHARGEBLE_TOT"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(monthYearsList.get(k)+"_OTHER_COST_TOT"),"0") %></td>
						<% for(int i=0; i<alPartnerId.size(); i++){ 
						%>
							<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_"+monthYearsList.get(k)+"_EMP_COST"),"0") %></td>
							<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_"+monthYearsList.get(k)+"_PROFESSIONAL_COST"),"0") %></td>
							<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_"+monthYearsList.get(k)+"_CHARGEBLE"),"0") %></td>
							<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_"+monthYearsList.get(k)+"_NON_CHARGEBLE"),"0") %></td>
							<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_"+monthYearsList.get(k)+"_OTHER_DIRECT_COST"),"0") %></td></td>
						<%} %>
						
					</tr>
				<% } %>
				<tr>
					<th>Total</th>
					<td class="alignRight"><%=uF.showData(hmReportData.get("TOTAL_MONTH_EMP_COST"),"0") %></td>
					<td class="alignRight"><%=uF.showData(hmReportData.get("TOTAL_MONTH_PROFESSIONAL_FEES"),"0") %></td>
					<td class="alignRight"><%=uF.showData(hmReportData.get("TOTAL_MONTH_CHARGEBLE"),"0") %></td>
					<td class="alignRight"><%=uF.showData(hmReportData.get("TOTAL_MONTH_NON_CHARGE"),"0") %></td>
					<td class="alignRight"><%=uF.showData(hmReportData.get("TOTAL_MONTH_OTHER_COST"),"0") %></td>
					<% for(int i=0; i<alPartnerId.size(); i++){ 
					%>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_EMP_COST_TOTAL"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_PROFESSIONAL_FEES_TOTAL"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_CHARGEBLE_TOTAL"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_NON_CHARGE_TOTAL"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alPartnerId.get(i)+"_OTHER_COST_TOTAL"),"0") %></td>
					
					<%} %>
				</tr>
			</table>
		</div>
	</div>

</div>