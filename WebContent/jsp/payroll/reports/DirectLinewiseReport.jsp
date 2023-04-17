<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">
	<%
	    UtilityFunctions uF = new UtilityFunctions(); 
	    
	    List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
	    if(reportListExport == null) reportListExport = new ArrayList<List<DataStyle>>();
	    
	    List<Map<String, String>> alProductionLine = (List<Map<String,String>>) request.getAttribute("alProductionLine");
	    if(alProductionLine == null) alProductionLine = new ArrayList<Map<String,String>>();
    %>
	<script type="text/javascript" charset="utf-8">
	$(function() {
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		
	
		$('#lt1').DataTable({
			dom: 'lBfrtip',
	        buttons: [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
		});
		
	});
	
	function submitForm(type){
		var org = document.getElementById("f_org").value;
		var f_level = getSelectedValue("f_level");
		var paycycle = document.getElementById("paycycle").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+f_level+'&paycycle='+paycycle;
		}

		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'DirectLinewiseReport.action?f_org='+org+paramValues,
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
	
    function exportpdf(){
    	  window.location="ExportExcelReport.action";
    }
	</script>
	
	
	
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
				<s:form name="frm_DirectLinewiseReport" id="frm_DirectLinewiseReport" action="DirectLinewiseReport" theme="simple" method="post">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">SBU</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
							</div>
						</div>
					</div>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
		         				<s:select id="paycycle" name="paycycle" listKey="paycycleId" headerKey="" headerValue="Select Paycycle" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		
		<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<a onclick="exportpdf();" href="javascript:void(0)" class="excel" ></a>
		</div>
		
		<table class="table table-hover table-bordered" id="lt1">
			<thead>
				<tr>
					<th class="alignCenter">Line</th>
					<th class="alignCenter">Man</th>
					<th class="alignCenter">Salary</th>
					<th class="alignCenter">Average Salary</th>
				</tr>
			</thead>
			
			<tbody>
			<%
				
				int nProductionLine = alProductionLine.size();
				for(int i = 0; i < nProductionLine; i++) {
					Map<String, String> hmProductionLineData = alProductionLine.get(i);
					if(hmProductionLineData == null) hmProductionLineData = new HashMap<String, String>();
			%>
					<tr>
						<td nowrap="nowrap"><%=uF.showData(hmProductionLineData.get("PRODUCTION_LINE"), "")%></td>
						<td class="alignCenter"><%=uF.showData(hmProductionLineData.get("MAN_POWER"), "0")%></td>
						<td class="alignRight"><%=uF.showData(hmProductionLineData.get("SALARY"), "0")%></td>
						<td class="alignRight"><%=uF.showData(hmProductionLineData.get("SALARY_AVG"), "0")%></td>
					</tr>
			<% } %>
			</tbody>
		</table>
		
		<% session.setAttribute("reportListExport", reportListExport); %>  
	</div>
	<!-- /.box-body -->
</div>
