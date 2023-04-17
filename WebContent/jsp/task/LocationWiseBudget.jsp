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
			
			document.frm_LocationWiseBudget.exportType.value='';
			var org = document.getElementById("f_org").value;
			var financialYear = document.getElementById("financialYear").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				paramValues = '&strLocation='+location+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			//alert("paramValues ===>> " + paramValues);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'LocationWiseBudget.action?f_org='+org+paramValues,
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
			document.frm_LocationWiseBudget.exportType.value='excel';
			document.frm_LocationWiseBudget.submit();
		}
	
	    
	</script>
	
	<%
	Map<String, String> hmLocationMap = (Map<String, String>)request.getAttribute("hmLocationMap");
	if(hmLocationMap == null) hmLocationMap = new HashMap<String, String>();
	
	Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
	if(hmReportData == null) hmReportData = new HashMap<String, String>();
	
	UtilityFunctions uF = new UtilityFunctions();
	
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
					<s:form name="frm_LocationWiseBudget" id="frm_LocationWiseBudget" action="LocationWiseBudget" theme="simple" method="post">
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
		
		<div class="box-body" style="padding: 5px; overflow-y: auto;">
			
			<table cellspacing="1" class="table table-bordered" id=lt1>
				<tr>
					<th></th>
			<%
			
			StringBuilder sbCYrCommit = new StringBuilder();
			StringBuilder sbCYrBudget = new StringBuilder();
			StringBuilder sbNxtYrBudget = new StringBuilder();
			StringBuilder sbLastYrAct = new StringBuilder();
			StringBuilder sbGap = new StringBuilder();
			StringBuilder sbTotal = new StringBuilder();
			Iterator<String> it = hmLocationMap.keySet().iterator();
			while (it.hasNext()) {
				
				String locId = it.next();
				sbCYrCommit.append("<td class=\"alignRight\">"+hmReportData.get(locId+"_CURR_YR_COMMIT")+"</td>");
				sbCYrBudget.append("<td class=\"alignRight\">"+hmReportData.get(locId+"_CURR_YR_BUDGET")+"</td>");
				sbNxtYrBudget.append("<td class=\"alignRight\">"+hmReportData.get(locId+"_NXT_YR_BUDGET")+"</td>");
				sbLastYrAct.append("<td class=\"alignRight\">"+hmReportData.get(locId+"_LAST_YR_ACTUAL")+"</td>");
				sbGap.append("<td class=\"alignRight\">"+"</td>");
				sbTotal.append("<td class=\"alignRight\">"+hmReportData.get(locId+"_TOTAL")+"</td>");
			%>
			<th class="alignLeft" ><%= hmLocationMap.get(locId) %></th>
			<% } %>
			<th class="alignLeft" >Total</th>
				</tr>
				<tr>
					<td class="alignLeft" >Last Year Actual</td>
					<%=sbLastYrAct.toString() %>
					<td class="alignRight" ><%=uF.showData(hmReportData.get("LAST_YR_ACT_TOTAL"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" >Current Year Budget</td>
					<%=sbCYrBudget.toString() %>
					<td class="alignRight" ><%=uF.showData(hmReportData.get("CURRENT_YR_BUDGET_TOTAL"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" >Current Year Commitment</td>
					<%=sbCYrCommit.toString() %>
					<td class="alignRight" ><%=uF.showData(hmReportData.get("CURRENT_YR_COMMIT_TOTAL"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" >Next Year Budget</td>
					<%=sbNxtYrBudget.toString() %>
					<td class="alignRight" ><%=uF.showData(hmReportData.get("NXT_YR_BUDGET_TOTAL"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" >Gap</td>
					<%=sbGap.toString() %>
					<td class="alignRight" ></td>
				</tr>
				<tr>
					<td class="alignLeft" >Total</td>
					<%=sbTotal.toString() %>
					<td class="alignRight" ><%=uF.showData(hmReportData.get("AL_TOTAL"),"0") %></td>
				</tr>
			</table>
		</div>
		
	</div>
	
</div>