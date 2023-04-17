<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">
 
<%--   <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
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
		$(document).ready(function() {
			$('#lt1').DataTable({
				aLengthMenu: [
		  			[25, 50, 100, 200, -1],
		  			[25, 50, 100, 200, "All"]
		  		],
		  		"aaSorting": [],
		  		iDisplayLength: -1,
				dom: 'lBfrtip',
				buttons: [
				            'copy',
				            {
				                extend: 'csv',
				                title: 'Employee CTC Report'
				            },
				            {
				                extend: 'excel',
				                title: 'Employee CTC Report'
				            },
				            {
				                extend: 'pdf',
				                title: 'Employee CTC Report'
				            },
				            {
				                extend: 'print',
				                title: 'Employee CTC Report'
				            }
				        ]
			});
			$("#f_strWLocation").multiselect().multiselectfilter();
			$("#f_department").multiselect().multiselectfilter();
			$("#f_service").multiselect().multiselectfilter();
			$("#f_level").multiselect().multiselectfilter();
		});
		
		function submitForm(type){
			var strSearch = document.getElementById("strSearch").value;
			var org = document.getElementById("f_org").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'EmpCTCReport.action?strSearch='+strSearch+'&f_org='+org+paramValues,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	//console.log(result);
		        	$("#divResult").html(result);
		   		}
			});
		}
		
		function loadMore(proPage, minLimit) {
			var strSearch = document.getElementById("strSearch").value;
			var org = document.getElementById("f_org").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues ='&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;

			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'EmpCTCReport.action?strSearch='+strSearch+'&f_org='+org+'&proPage='+proPage+'&minLimit='+minLimit+paramValues,
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
	
		function generateReportExcel() {
			window.location="ExportExcelReport.action";
		}
		
	</script>
	<%
		String pageCount = (String)request.getAttribute("pageCount");
		UtilityFunctions uF = new UtilityFunctions();
		
		String sbData = (String) request.getAttribute("sbData");
		String strSearch = (String) request.getAttribute("strSearch");
	%>
	
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form name="frm_fromEmpCTC" action="EmpCTCReport" theme="simple">
					<s:hidden name="exportType"></s:hidden>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Employee</p>
								<s:select name="strEmpId" id="strEmpId" listKey="employeeId" headerValue="Select Employee" listValue="employeeCode" headerKey="0" onchange="submitForm('2');" list="empList" key=""/>
							</div> --%>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
							</div>
	
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">SBU</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
							</div>
						</div>
					</div><br>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		
		<div class="col-lg-12 col-md-12 col-sm-12 no-padding" style="text-align: center;">
			<%-- <span>Search:</span> --%>
			<input type="text" id="strSearch" class="form-control" name="strSearch" placeholder="Search" value="<%=uF.showData(strSearch, "") %>"/>
			<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
		</div>
		
		<script type="text/javascript">
			$("#strSearch").autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		</script>
		
		<%List<List<String>> reportList = (ArrayList<List<String>>) request.getAttribute("reportList");
			if(reportList == null) reportList = new ArrayList<List<String>>();
		%>
		
		<div style="text-align: center; float: left; width: 100%; padding-top: 15px; padding-bottom: 15px;">
			<%
			int intPageCnt = uF.parseToInt(pageCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intPageCnt; i++) { 
					minLimit = pageCnt * 100;
					pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-100) + "";
				}
				if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
			%>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"<% } %>><%=pageCnt %></a></span>
				
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
					<b>...</b>
				<% } %>
			
			<% } %>
			
			<% if(i > 1 && i < intPageCnt) { %>
				<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
					<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
					<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
					style="color: black;"<% } %>><%=pageCnt %></a></span>
				<% } %>
			<% } %>
			
			<% if(i == intPageCnt && intPageCnt > 1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)+100) + "";
				 if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
				%>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intPageCnt) { %>
					<b>...</b>
				<% } %>
			
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"<% } %>><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
		</div>
		<div>
			<%if(reportList != null && !reportList.isEmpty()&& reportList.size() > 0) { %>
				<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
					 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;/*float: right; */" ><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				</div> 
			<%} %>
			<table class="table table-bordered" id="lt1">
				<thead>
					<tr>
						<th nowrap="nowrap">Employee Code</th>
						<th nowrap="nowrap">Employee Name</th>
						<th nowrap="nowrap">Pan No.</th>
						<th nowrap="nowrap">Designation</th>
						<th nowrap="nowrap">Grade</th>
						<th nowrap="nowrap">Work Location</th>
						<th nowrap="nowrap">CTC(Yearly)</th>
						<th nowrap="nowrap">CTC(Monthly)</th>
						<th nowrap="nowrap">Net Salary</th>
						<th nowrap="nowrap">Gross Salary</th>
						<th nowrap="nowrap">Total Deduction</th>
						<th nowrap="nowrap">Employer Contribution</th>
					</tr>
				</thead>
				<tbody>
				<% 
				List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
				List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
				
				alInnerExport.add(new DataStyle("Employee CTC Report",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Pan No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Designation", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Grade", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Work Location", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("CTC(Yearly)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("CTC(Monthly)", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Net Salary", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Gross Salary", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Total Deduction", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				alInnerExport.add(new DataStyle("Employer Contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
				
				reportListExport.add(alInnerExport);
				
				for(int i=0; i<reportList.size(); i++) { 
					List<String> innerList = reportList.get(i);
					
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(innerList.get(0), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(1), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(2), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(3), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(4), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(5), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(6), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(7), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(8), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(9), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(10), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(innerList.get(11), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					
					reportListExport.add(alInnerExport);
				%>
					<tr>
						<td style="text-align: center;" nowrap="nowrap"><%=innerList.get(0) %></td>
						<td style="text-align: left;" nowrap="nowrap"><%=innerList.get(1) %></td>
						<td style="text-align: left;" nowrap="nowrap"><%=innerList.get(2) %></td>
						<td style="text-align: left;" nowrap="nowrap"><%=innerList.get(3) %></td>
						<td style="text-align: left;" nowrap="nowrap"><%=innerList.get(4) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(5) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(6) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(7) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(8) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(9) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(10) %></td>
						<td style="text-align: right;" nowrap="nowrap"><%=innerList.get(11) %></td>
					</tr>
				<% } %>	
				</tbody>
			</table>
		</div>
		
		<% session.setAttribute("reportListExport", reportListExport); %>
	</div>
<!-- /.box-body -->
</div>

