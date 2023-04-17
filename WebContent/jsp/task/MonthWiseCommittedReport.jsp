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
			
			document.frm_MonthWiseCommitedReport.exportType.value='';
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
				url: 'MonthWiseCommittedReport.action?f_org='+org+paramValues,
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
			document.frm_MonthWiseCommitedReport.exportType.value='excel';
			document.frm_MonthWiseCommitedReport.submit();
		}
	
	    
	</script>
	
	<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
		List<String> monthYearsList = (List<String>) request.getAttribute("monthYearsList");
		List reportAllHeads = (List)request.getAttribute("reportAllHeads");
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
					<s:form name="frm_MonthWiseCommitedReport" id="frm_MonthWiseCommitedReport" action="MonthWiseCommittedReport" theme="simple" method="post">
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
		<div class="col-md-2 pull-right">
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div>
		<div  style="width:100%;">
			<table cellspacing="1" class="table table-bordered" id=lt1>
				<tr>
					<th class="alignCenter" colspan="20"><%=reportAllHeads.get(0) %></th>
				</tr>
				<tr>
					<th class="alignCenter" colspan="20">CHARTERED ACCOUNTANTS</th>
				</tr>
				<tr>
					<th class="alignCenter" colspan="20"><%=reportAllHeads.get(1) %> </th>
				</tr>
				<tr>
					<th class="alignCenter" colspan="20"><%=reportAllHeads.get(2) %> </th>
				</tr>
				<tr>
					<th class="alignCenter" colspan="20">MONTHWISE COMMITTED RECEIPTS</th>
				</tr>
				<tr>
					<!-- <th></th> -->
					<th colspan="2" class="alignCenter"></th>
					<th colspan="5" class="alignCenter">TOTAL ASSIGNMENTS</th>
					<th colspan="13" class="alignCenter">PLANNED RECEIPTS</th>
				</tr>
				<tr>
					<th class="alignCenter">SR.NO</th>
					<th class="alignCenter">DIVISION</th>
					<th class="alignCenter">OP.COMM.AT THE BEGINNING OF THE YEAR</th>
					<th colspan="2" class="alignCenter">ADDITIONS/ DELETIONS DURING THE YEAR</th>
					<th colspan="2" class="alignCenter">COMMITED AT THE END OF THE MONTH</th>					
				</tr>
				<tr>
					<!-- <th></th>
					<th></th> -->
					<th colspan="3"></th>
					<th class="alignCenter">ADDITION UPTO THE BEGINNING OF THE MONTH</th>
					<th class="alignCenter">ADDITION DURING THIS MONTH</th>
					<th class="alignCenter">RECEIPTS TO HAPPEN DURING THIS YEAR</th>
					<th class="alignCenter">RECEIPTS TO HAPPEN IN THE NEXT YEAR</th>
					<%
						
						for(int i=0; i<monthYearsList.size(); i++){
					%>
					<th class="alignCenter"><%=uF.getDateFormat(monthYearsList.get(i),"MM/yyyy","MMM").toUpperCase() %></th>
					<% } %>
					<th class="alignCenter">TOTAL</th>
				</tr>
				
				<% for(int j=0; j<alOuter.size();j++){ 
						List innerList = alOuter.get(j);
				%>
					<tr>
						<td><%=j+1 %></td>
						<td ><%=innerList.get(0) %></td>
						<td class="alignRight"><%=innerList.get(14) %></td>
						<td class="alignRight"><%=innerList.get(15) %></td>
						<td class="alignRight"><%=innerList.get(16) %></td>
						<td class="alignRight"><%=innerList.get(17) %></td>
						<td class="alignRight"><%=innerList.get(18) %></td>
						<% for(int k=1; k<=12; k++){ %>
							<td class="alignRight"><%=innerList.get(k) %></td>
						<% } %>
						<td class="alignRight"><%=innerList.get(13) %></td>
					</tr>
				<% } %>
			</table>
		</div>
	</div>
</div>	