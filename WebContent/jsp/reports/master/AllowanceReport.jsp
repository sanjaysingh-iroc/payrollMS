<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
    UtilityFunctions uF = new UtilityFunctions(); 
    String strTitle = (String) request.getAttribute(IConstants.TITLE);  
    
    List<Map<String, String>> alEmp = (List<Map<String, String>>) request.getAttribute("alEmp");
    if(alEmp == null) alEmp = new ArrayList<Map<String,String>>();
    List<Map<String, String>> alCondition = (List<Map<String, String>>) request.getAttribute("alCondition");
    if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
    List<Map<String, String>> alLogic = (List<Map<String, String>>) request.getAttribute("alLogic");
    if(alLogic == null) alLogic = new ArrayList<Map<String,String>>();
    
    Map<String, String> hmAssignConditionAmt = (Map<String, String>) request.getAttribute("hmAssignConditionAmt");
    if(hmAssignConditionAmt == null) hmAssignConditionAmt = new HashMap<String, String>();
    Map<String, String> hmAssignLogicAmt = (Map<String, String>) request.getAttribute("hmAssignLogicAmt");
    if(hmAssignLogicAmt == null) hmAssignLogicAmt = new HashMap<String, String>();
    
    List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
    List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
    %>
    
    
<div id="divResult">

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>
 
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 
 
<script type="text/javascript" charset="utf-8">

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		dom: 'lBfrtip',
		buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Allowance Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Allowance Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Allowance Report'
		            },
		            {
		                extend: 'print',
		                title: 'Allowance Report'
		            }
		        ]
	});
	


function submitForm1(type){
	
	
	var org = document.getElementById("f_org").value;
	var f_level = document.getElementById("f_level").value;
	var paycycle = document.getElementById("paycycle").value;
	var f_salaryhead = document.getElementById("f_salaryhead").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&f_level='+f_level
			+'&f_salaryhead='+f_salaryhead+'&paycycle='+paycycle;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'AllowanceReport.action?f_org='+org+paramValues,
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


     function submitForm(type){
    	/* if(type == '1'){
    		document.getElementById("idPaycycleId").selectedIndex = "0";
    		document.getElementById("f_level").selectedIndex = "0";
    		document.getElementById("idsalaryhead").selectedIndex = "0";
    	} else if(type == '2'){
    		document.getElementById("idsalaryhead").selectedIndex = "0";
    	}
    	document.frm_AllowanceReport.submit(); */
    	
    	 	//document.frm_fromPaymentHeld.exportType.value='';
    		var org = document.getElementById("f_org").value;
    		var paycycle = document.getElementById("paycycle").value;
    		var f_level = document.getElementById("f_level").value;
    		var f_salaryhead = document.getElementById("f_salaryhead").value;
    		var location = getSelectedValue("f_strWLocation");
    		var department = getSelectedValue("f_department");
    		var service = getSelectedValue("f_service");
    		
    		var paramValues = "";
    		if(type == '2') {
    			paramValues = '&paycycle='+paycycle+'&f_level='+f_level+'&f_salaryhead='+f_salaryhead
    			+'&location='+location+'&department='+department+'&service='+service;
    		}
    		//alert("service ===>> " + service);
    		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    		$.ajax({
    			type : 'POST',
    			url: 'AllowanceReport.action?f_org='+org+paramValues,
    			data: $("#"+this.id).serialize(),
    			success: function(result){
    	        	// console.log(result);
    	        	$("#divResult").html(result);
    	   		}
    		});
    } 
    
    function exportpdf(){
    	  window.location="ExportExcelReport.action";
    }
</script>


<%-- <jsp:include page="../../common/SubHeader.jsp">
    <jsp:param value="Allowance Report" name="title"/>
    </jsp:include> --%>
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
				<s:form name="frm_AllowanceReport" action="AllowanceReport" theme="simple" method="post">
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
								<p style="padding-left: 5px;">Paycycle</p>
		         				<s:select id="paycycle" name="paycycle" listKey="paycycleId" headerKey="" headerValue="Select Paycycle" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" onchange="submitForm('2');" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Allowance Head</p>
								<s:select theme="simple" name="f_salaryhead" id="f_salaryhead" headerKey="" headerValue="Select Allowance Head" listKey="salaryHeadId" listValue="salaryHeadName" onchange="submitForm('2');" list="salaryHeadList" />
							</div>
						</div>
					</div>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
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
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm1('2');" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		<% if(alEmp.size() > 0 && alCondition.size() > 0  && alLogic.size() > 0){ %>
		<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			 <!-- <a onclick="exportpdf();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
			 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			 
			<!-- <a onclick="exportpdf();" href="javascript:void(0)" class="excel" ></a> -->
		</div>
		<%} %>
	
	<!-- <div style="float: left; width: 100%; overflow-x: auto;"> -->
	<% if(alEmp.size() > 0 && alCondition.size() > 0  && alLogic.size() > 0){ %>
		<table class="table table-bordered" id="lt1">
			<thead>
	    		<tr>
					<th align="center">Employee Code</th>
					<th align="center" nowrap="nowrap">Employee Name</th>
					<th align="center" nowrap="nowrap">Pan No</th>
					<%
					alInnerExport.add(new DataStyle("Allowance Report", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Pan No", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					for(int j = 0; j < alCondition.size(); j++){ 
						Map<String,String> hmCondition = (Map<String,String>) alCondition.get(j);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
					%>
					<th align="center" style="background-color: #efe;" nowrap="nowrap">
						<%=hmCondition.get("ALLOWANCE_CONDITION_SLAB") %><br/>
						<span style="font-size: 10px;"><%=hmCondition.get("ALLOWANCE_CONDITION") %></span><br/>
						<%
							if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID){
								String StrType= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "Percentage";
								String StrTypeStatus= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "%";
						%>
						<span style="font-size: 10px;">Type: <%=StrType %></span>
						<%alInnerExport.add(new DataStyle(" "+hmCondition.get("ALLOWANCE_CONDITION_SLAB")+hmCondition.get("ALLOWANCE_CONDITION")+" Type:"+StrType, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
						<%} else {%>
						<span style="font-size: 10px;">Min: <%=hmCondition.get("MIN_CONDITION") %>&nbsp;-&nbsp;
						Max: <%=hmCondition.get("MAX_CONDITION") %></span>
						<%alInnerExport.add(new DataStyle(" "+hmCondition.get("ALLOWANCE_CONDITION_SLAB")+hmCondition.get("ALLOWANCE_CONDITION")+" Min:"+hmCondition.get("MIN_CONDITION")+"- Max:"+hmCondition.get("MAX_CONDITION"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
						<%} %>
					</th>
				<%} %>
				<%
				for(int k = 0; k < alLogic.size(); k++){
					Map<String,String> hmLogic = (Map<String,String>) alLogic.get(k);
					if(hmLogic == null) hmLogic = new HashMap<String, String>();
				%>
					<th align="center" style="background-color: #FFFFCC;" nowrap="nowrap">
						<%=hmLogic.get("PAYMENT_LOGIC_SLAB") %><br/>
						<span style="font-size: 10px;">Condition: <%=hmLogic.get("ALLOWANCE_CONDITION") %></span><br/>
						<span style="font-size: 10px;">Payment Logic: <%=hmLogic.get("ALLOWANCE_PAYMENT_LOGIC") %></span><br/>
					<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID){ %>
						<span style="font-size: 10px;">Fixed Amount: <%=hmLogic.get("FIXED_AMOUNT") %></span>
						<%alInnerExport.add(new DataStyle(" "+hmLogic.get("PAYMENT_LOGIC_SLAB")+" Condition:"+hmLogic.get("ALLOWANCE_CONDITION")+" Payment Logic:"+hmLogic.get("ALLOWANCE_PAYMENT_LOGIC")+" Fixed Amount:"+hmLogic.get("FIXED_AMOUNT"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
					<%} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID){ %>
						<span style="font-size: 10px;">Salary Head: <%=hmLogic.get("CAL_SALARY_HEAD_NAME") %></span>
						<%alInnerExport.add(new DataStyle(" "+hmLogic.get("PAYMENT_LOGIC_SLAB")+" Condition:"+hmLogic.get("ALLOWANCE_CONDITION")+" Payment Logic:"+hmLogic.get("ALLOWANCE_PAYMENT_LOGIC")+" Salary Head:"+hmLogic.get("CAL_SALARY_HEAD_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
					<%} %>
					</th>
				<%} %>
					<th align="center">Allowance Amount</th>
				<%
					alInnerExport.add(new DataStyle("Allowance Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					reportListExport.add(alInnerExport);
				%>
	 			</tr>
			</thead>
 		
			<tbody>
		 		<%
					int cnt = 0;
					for(int i = 0; i < alEmp.size(); i++){
						cnt++;
						Map<String, String> hmEmp = (Map<String, String>)alEmp.get(i);
						String strEmpId = hmEmp.get("EMP_ID");
				%>
				<%
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle((String) hmEmp.get("EMP_CODE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle((String) hmEmp.get("EMP_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle((String) hmEmp.get("EMP_PAN_NO"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				%>
				<tr>
					<td align="center"><%=hmEmp.get("EMP_CODE")%><input type="hidden" id="idStrEmpId_<%=i%>" name="strEmpId" value="<%=strEmpId%>"></td>
					<td><%=hmEmp.get("EMP_NAME")%></td>
					<td><%=hmEmp.get("EMP_PAN_NO")%></td>
				<%
					for(int j = 0; j < alCondition.size(); j++){
						Map<String,String> hmCondition = (Map<String,String>) alCondition.get(j);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
				%>
					<td align="right" style="background-color: #efe;">
				<%
					if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID){
						String strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
				%>
					<%=strConditionVal %>	
					<%alInnerExport.add(new DataStyle((String) strConditionVal, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
				<%
					} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID){
						String strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
				%>
					<%=strConditionVal %>	
					<%alInnerExport.add(new DataStyle((String) strConditionVal, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
				<% 
					} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID){
						String strConditionVal = uF.showData(hmAssignConditionAmt.get(strEmpId+"_"+hmCondition.get("ALLOWANCE_CONDITION_ID")),"");
				%>
					<%=strConditionVal %>
					<%alInnerExport.add(new DataStyle((String) strConditionVal, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
				<%} %>
					</td>
				<% } %>
			
				<%
					for(int k = 0; k < alLogic.size(); k++){
						Map<String,String> hmLogic = (Map<String,String>) alLogic.get(k);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
				%>
					<td align="right" style="background-color: #FFFFCC;"><%=uF.showData(hmAssignLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")),"") %></td>
					<%alInnerExport.add(new DataStyle((String) uF.showData(hmAssignLogicAmt.get(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")),""), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
				<%} %>
					<td align="right"><%=hmEmp.get("ALLOWANCE_AMOUNT")%></td>
					<%alInnerExport.add(new DataStyle((String) hmEmp.get("ALLOWANCE_AMOUNT"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY)); %>
		 		</tr>
	 		<% reportListExport.add(alInnerExport); %>
 			</tbody>
		</table>
		<%} 
			}else { %>
			<div style="width: 92%;" class="msg nodata"><span>No data found for the current selection</span></div>
		<%}%>
	<!-- </div> -->
	<% session.setAttribute("reportListExport", reportListExport); %>  
	</div>
	<!-- /.box-body -->
</div>
