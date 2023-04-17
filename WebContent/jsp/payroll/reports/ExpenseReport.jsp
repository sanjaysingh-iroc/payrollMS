<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
			
function generateReportExcel(){
	window.location="ExportExcelReport.action";
}

</script>

<script>
$(function() {
    $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    
    var value = document.getElementById("selectOne").value;
     checkSelectType(value);
});

 
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});


function checkSelectType(value) {
	
	//fromToDIV financialYearDIV monthDIV paycycleDIV
	if(value == '1') {
		document.getElementById("fromToDIV").style.display = 'block';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '2') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'block';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '3') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'block';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '4') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'block';
	}
}

</script>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
$("#f_strWLocation").multiselect();
$("#f_department").multiselect();
$("#f_service").multiselect();
$("#f_level").multiselect();
$("#f_project_service").multiselect();
/* $("#f_client").multiselect(); */
$("#f_project").multiselect();
});    
</script>



<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

//String strOrg=(String)request.getAttribute("f_org");
//String strLocation=(String)request.getAttribute("f_strWLocation");

%>

<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Expense Report" name="title"/>
</jsp:include>
   


<div id="printDiv" class="leftbox reportWidth">


		<%-- <s:form name="frm_from" action="ExpenseReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName"					
					list="paycycleList" key="" onchange="document.frm_from.submit();" />
						
			<s:select theme="simple" name="f_org" listKey="orgId"  listValue="orgName" 
                         onchange="document.frm_from.submit();" list="orgList" key="" cssStyle="width:200px;"/>			
						
			<s:select name="f_strWLocation" listKey="wLocationId" listValue="wLocationName" 
						onchange="document.frm_from.submit();" list="wLocationList" key="" cssStyle="width:200px;"/>
						
			 <s:select name="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments"
    			onchange="document.frm_from.submit();" /> 
    			
			<s:select theme="simple" name="level" listKey="levelId" listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             onchange="document.frm_from.submit();" list="levelList" key="" /> 
	             
            <s:select name="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="0" headerValue="All Services"
   			onchange="document.frm_from.submit();" />   
				 
			<s:select name="strProject" listKey="projectID" listValue="projectName" headerKey="" headerValue="All Project"
						list="projectdetailslist" key="" cssClass="validate[required]" onchange="document.frm_from.submit();"/>
			
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>

		</div>

		</s:form>
		<br/> --%>
		
		
		<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 200px;">
		
    <s:form name="frmExpenseReport" action="ExpenseReport" theme="simple">
				<div style="float: left; width: 100%; margin-top: -5px;">
						<div style="float: left; margin-top: 10px;">
							<i class="fa fa-filter"></i>
						</div>
						
						<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Organisation</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
								onchange="document.frmExpenseReport.submit();" list="organisationList" />
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
								listValue="wLocationName" list="wLocationList" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Department</p>
							<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" cssStyle="float:left; margin-right: 10px;" 
								listValue="deptName" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">SBU</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
								listValue="serviceName" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;"
								listValue="levelCodeName" list="levelList" multiple="true"/>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 35px; width: 215px;">
							<p style="padding-left: 5px;">Service</p>
							<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
								listValue="serviceName" multiple="true"/>
						</div>
						
						<%-- <div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Client</p>
							<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" cssStyle="float:left;margin-right: 10px;" list="clientList" 
								key="" multiple="true" />
						</div> --%>
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Client</p>
							<s:select name="f_project" id="f_project" listKey="projectID" listValue="projectName" cssStyle="float:left;margin-right: 10px;"
								list="projectdetailslist" key="" multiple="true"/>
						</div>
				</div>
				
				<div style="float: left; width: 100%;">
						<div style="float: left; margin-top: 10px;">
							<i class="fa fa-calendar"></i>
						</div>
						
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Select Period</p>
							<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
								headerValue="Select Period" list="#{'4':'Paycycle'}" onchange="checkSelectType(this.value);"/>
						</div>
						
						<div id="fromToDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<s:textfield name="strStartDate" id="strStartDate" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
				      		<s:textfield name="strEndDate"  id="strEndDate" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
			      		</div>
			      		
			      		<div id="financialYearDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
								headerValue="Select Financial Year" list="financialYearList" />
			      		</div>
			      		
			      		<div id="monthDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 325px;">
							<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
							<s:select label="Select PayCycle" name="financialYear" listKey="financialYearId" listValue="financialYearName"
								headerValue="Select Financial Year" list="financialYearList" /> 
							<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px;" listKey="monthId" listValue="monthName" list="monthList" />	
			      		</div>
			      		
			      		<div id="paycycleDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" listValue="paycycleName"
								headerValue="Select Paycycle" list="paycycleList" />
			      		</div>
			      		
						<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
						</div>
				</div>
		</s:form>
	
		</div>
	</div>
		
		<div style="width: 100%;">
	<%
	List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	alInnerExport.add(new DataStyle("Expense Report from "+request.getAttribute("strD1")+" - "+request.getAttribute("strD2"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Division",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Project",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Project Working Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Total working days in Timesheet",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	alInnerExport.add(new DataStyle("Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
   	
   	reportListExport.add(alInnerExport); 
	%>
		<%-- <table cellpadding="2" cellspacing="2" border="0" class="tb_style" width="100%">
		<thead>
			<tr>
				<th nowrap="nowrap" class="alignCenter">Sr. No.</th>
				<th nowrap="nowrap" class="alignCenter">Division</th>
				<th nowrap="nowrap" class="alignCenter">Employee Name</th>
				<th nowrap="nowrap" class="alignCenter">Project</th>
				<th nowrap="nowrap" class="alignCenter">Project Working Days</th>
				<th nowrap="nowrap" class="alignCenter">Total working days in Timesheet</th>
				<th nowrap="nowrap" class="alignCenter">Salary</th>
			</tr>		
			</thead>
			<tbody>			
			<%
			
			double total=0;
			double totalHours=0;
			double timesheetTotalHours=0;
			for(int i=0;outerList!=null && i<outerList.size();i++){
				List<String> innerList=outerList.get(i);
				
				alInnerExport=new ArrayList<DataStyle>();
				alInnerExport.add(new DataStyle(""+(i+1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(innerList.get(0),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
				alInnerExport.add(new DataStyle(innerList.get(1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(innerList.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(innerList.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(innerList.get(4),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle(innerList.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				total+=uF.parseToDouble(innerList.get(5));
				timesheetTotalHours+=uF.parseToDouble(innerList.get(4));
				totalHours+=uF.parseToDouble(innerList.get(3));
			%>
				
			<tr>
			<td class="alignCenter"><%=i+1 %></td>
			<td nowrap="nowrap" class="alignLeft"><%=innerList.get(0) %></td>
			<td nowrap="nowrap" class="alignLeft"><%=innerList.get(1) %></td>
			<td nowrap="nowrap" class="alignLeft"><%=innerList.get(2) %></td>
			<td nowrap="nowrap" class="alignRight"><%=innerList.get(3) %></td>
			<td nowrap="nowrap" class="alignRight"><%=innerList.get(4) %></td>
			<td nowrap="nowrap" class="alignRight"><%=innerList.get(5) %></td>
			
			</tr>
			<%} %>
			<%if(outerList==null || outerList.size()==0){ %>
				<tr>
				<td class="alignCenter" colspan="6">No Employees found</td>
			</tr>
			<%}else{ %>
			<tr>
					<td class="alignLeft"></td>
					<td class="alignLeft"></td>
					<td class="alignLeft"></td>
					
					<td class="alignRight"><strong>Total</strong></td>
					<td class="alignRight"><%=uF.formatIntoOneDecimal(totalHours)    %></td>
					<td class="alignRight"><%=uF.formatIntoOneDecimal(totalHours)    %></td>
					<td class="alignRight"><strong><%=uF.formatIntoOneDecimal(total)    %></strong></td>
				
					
				</tr>
			<%} %>
			</tbody>     
		</table> --%>
		
		
		
	<% int ii = 0; %>
	<display:table name="outerList" cellspacing="1" class="tb_style" export="true" pagesize="20" id="lt1" requestURI="ExpenseReport.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="ExpenseReport.xls" />
	<display:setProperty name="export.xml.filename" value="ExpenseReport.xml" />
	<display:setProperty name="export.csv.filename" value="ExpenseReport.csv" />
	<display:setProperty name="export.pdf" value="true" />
	<% ii++; %>
	<display:column style="text-align:center" nowrap="nowrap" title="Sr. No."><%=ii %></display:column>
	<display:column style="text-align:left" nowrap="nowrap" title="SBU"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	<display:column style="text-align:left" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column style="text-align:left" nowrap="nowrap" title="Project"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	<display:column style="text-align:right" nowrap="nowrap" title="Project Working Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	<display:column style="text-align:right" nowrap="nowrap" title="Total working days in Timesheet"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column style="text-align:right" nowrap="nowrap" title="Salary"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
	
<%
			
			double total = 0;
			double totalHours = 0;
			double timesheetTotalHours = 0;
			for(int i=0;outerList!=null && i<outerList.size();i++) {
				List<String> innerList = outerList.get(i);
				
				total+=uF.parseToDouble(innerList.get(5));
				timesheetTotalHours += uF.parseToDouble(innerList.get(4));
				totalHours += uF.parseToDouble(innerList.get(3));
			}
			%>
	<display:footer>
			<tr>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
				<th style="text-align:right">Total</th>
				<th style="text-align:right"><%=totalHours %></th>
				<th style="text-align:right"><%=timesheetTotalHours %></th>
				<th style="text-align:right"><%=total %></th>
			</tr>
	</display:footer>


</display:table>

		<%session.setAttribute("reportListExport",reportListExport); %>
		</div>
    </div>
   
<%-- 
<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>