<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#f_wLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
	});
	
	function submitForm(type){
		document.frm_QuarterlyProjectSummaryReport.exportType.value='';
		var org = document.getElementById("f_org").value;
		var calendarYear = document.getElementById("calendarYear").value;
		var strMonth = document.getElementById("strMonth").value;
		var location = getSelectedValue("f_wLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
			+'&strLevel='+level+'&calendarYear='+calendarYear+'&strMonth='+strMonth;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'QuarterlyProjectSummaryReport.action?f_org='+org+paramValues,
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
		document.frm_QuarterlyProjectSummaryReport.exportType.value='excel';
		document.frm_QuarterlyProjectSummaryReport.submit();
	}

</script>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
		if(alOuter == null) alOuter = new ArrayList<List<String>>();
		List<String> monthYearsList = (List<String>) request.getAttribute("monthYearsList");
		
		Map<String,List<String>> hmAlProjects = (Map<String,List<String>>)request.getAttribute("hmAlProjects");
		if(hmAlProjects == null) hmAlProjects = new HashMap<String, List<String>>();
		Map<String,String> hmProjectName = (Map<String,String>)request.getAttribute("hmProjectName");
		if(hmProjectName == null) hmProjectName = new HashMap<String, String>();
		int size = alOuter!=null && alOuter.size()>0 ? alOuter.get(0).size() : 0 ;
	%>


	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px;color:#232323;">
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
                    <s:form name="frm_QuarterlyProjectSummaryReport" id="frm_QuarterlyProjectSummaryReport" action="QuarterlyProjectSummaryReport" theme="simple" method="post">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
								</div>
	
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
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
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
									headerKey="0" onchange="submitForm('2');" list="calendarYearList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Quarterly Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" onchange="submitForm('2');" />
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
					<th class="alignCenter" colspan="<%=size %>">Summary of <%=uF.showData((String)request.getAttribute("quarterNo"),"") %> hours-<%=uF.getDateFormat(monthYearsList.get(1),"MM/yyyy","yyyy") %></th>
				</tr>
				<tr>
					<th class="alignCenter" rowspan="2">Particulars</th>
					<% for(int i=0;i<monthYearsList.size();i++){ 
						List alProIds = hmAlProjects.get(monthYearsList.get(i));
					%>
						<th class="alignCenter" colspan="<%=alProIds!=null ? alProIds.size() : "0" %>"><%=monthYearsList.get(i) %></th>
					<% } %>
					<th class="alignCenter" rowspan="2">Total</th>
				</tr>
				<tr>
					<% for(int i=0;i<monthYearsList.size();i++){ 
						List alProIds = hmAlProjects.get(monthYearsList.get(i));
					%>
						<%for(int j=0; alProIds!=null && j<alProIds.size();j++){ %>
							<td class="alignRight"><%=uF.showData(hmProjectName.get(alProIds.get(j)),"") %></td>
						<% } %>
					<% } %>
				</tr>
				<% for(int i=0; alOuter!=null && i<alOuter.size();i++){ %> 
				<tr>
					<% List<String> innerList = (List<String>)alOuter.get(i);
						for(int j=0; innerList!=null && j<innerList.size();j++){ %>
							<td class="alignRight"><%=uF.showData((String)innerList.get(j),"-") %></td>
					<% } %>
				</tr>
				<% } %>
			</table>
		</div>
	</div>
</div>