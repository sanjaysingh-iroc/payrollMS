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
			
			document.frm_ClusterWiseBudget.exportType.value='';
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
				url: 'ClusterWiseBudget.action?f_org='+org+paramValues,
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
			document.frm_ClusterWiseBudget.exportType.value='excel';
			document.frm_ClusterWiseBudget.submit();
		}
	
	    
	</script>
	
	<%
	
		UtilityFunctions uF = new UtilityFunctions();
		
		Map<String, String> alSbuMap = (Map<String, String>)request.getAttribute("alSbuMap");
		if(alSbuMap == null) alSbuMap = new HashMap<String, String>();
		
		Map<String, String> alLocationMap = (Map<String, String>)request.getAttribute("alLocationMap");
		if(alLocationMap == null) alLocationMap = new HashMap<String, String>();
		
		Map<String, String> hmReportData = (Map<String, String>)request.getAttribute("hmReportData");
		if(hmReportData == null) hmReportData = new HashMap<String, String>();
		
		List headerList = (List)request.getAttribute("headerList");
		
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
					<s:form name="frm_ClusterWiseBudget" id="frm_ClusterWiseBudget" action="ClusterWiseBudget" theme="simple" method="post">
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
						Iterator<String> it = alSbuMap.keySet().iterator();
						List<String> alSbuList = new ArrayList<String>();
						while (it.hasNext()) {
							String sbuId = it.next();
							alSbuList.add(sbuId);
					%>
					<th class="alignLeft" ><%= alSbuMap.get(sbuId) %></th>
					<% } %>
					<th class="alignLeft">Total</th>
				</tr>
				
				<%
					Iterator<String> itr = alLocationMap.keySet().iterator();
					while (itr.hasNext()) {
						String locId = itr.next();
						
				%>
				<tr>
					<th colspan="<%=alSbuList.size()+2 %>" class="alignCenter"><%=alLocationMap.get(locId) %></th>
				</tr>
				<tr>
					<td class="alignLeft"><%=headerList.get(0) %></td>
					<% for(int i=0; i<alSbuList.size(); i++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_LAST_YR_ACT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get(locId+"_LAST_YR_ACT_TOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft"><%=headerList.get(1) %></td>
					<% for(int j=0; j<alSbuList.size(); j++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(j)+"-"+locId+"_C_YR_BUDGET"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get(locId+"_C_YR_BUDGET_TOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft"><%=headerList.get(2) %></td>
					<% for(int j=0; j<alSbuList.size(); j++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(j)+"-"+locId+"_C_YR_COMMIT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get(locId+"_C_YR_COMMIT_TOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft"><%=headerList.get(3) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"-"+locId+"_NXT_YR_BUDGET"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get(locId+"_NXT_YR_BUDGET_TOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(4) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"-"+locId+"_NXT_YR_COMMIT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get(locId+"_NXT_YR_COMMIT_TOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" >Gap</td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"></td>
					<% } %>
					<td class="alignRight"></td>
				</tr>
				<% } %>
				
				
				<tr>
					<th colspan="<%=alSbuList.size()+2 %>" class="alignCenter">Total KPCA</th>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(0) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"_LAST_YR_ACT_KTOT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get("LAST_YR_ACT_KTOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(1) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"_C_YR_BUDGET_KTOT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get("C_YR_BUDGET_KTOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(2) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"_C_YR_COMMIT_KTOT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get("C_YR_COMMIT_KTOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(3) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"_NXT_YR_BUDGET_KTOT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get("NXT_YR_BUDGET_KTOT"),"0") %></td>
				</tr>
				<tr>
					<td class="alignLeft" ><%=headerList.get(4) %></td>
					<% for(int k=0; k<alSbuList.size(); k++){ %>
						<td class="alignRight"><%=uF.showData(hmReportData.get(alSbuList.get(k)+"_NXT_YR_COMMIT_KTOT"),"0") %></td>
					<% } %>
					<td class="alignRight"><%=uF.showData(hmReportData.get("NXT_YR_COMMIT_KTOT"),"0") %></td>
				</tr>
			</table>
		</div>
		
	</div>
	
</div>