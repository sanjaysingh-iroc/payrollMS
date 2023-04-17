
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>


	<script type="text/javascript" charset="utf-8">
	
	
	function generateReportExcel(){
		window.location="ExportExcelReport.action";
	}


				$(document).ready(function () {
					
						$('#lt').dataTable({ bJQueryUI: true, 
							  								
							"sPaginationType": "full_numbers",
							"aaSorting": [],
							"sDom": '<"H"lTf>rt<"F"ip>',
							oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
							aButtons: [
									"csv", "xls", {
										sExtends: "pdf",
										sPdfOrientation: "landscape"
										//sPdfMessage: "Your custom message would go here."
	 								}, "print" 
								]
							}
						});
				});
				
	</script>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Employees Full Report" name="title"/>
</jsp:include>


	


    <div id="printDiv" class="leftbox reportWidth">
    
<s:form name="frm_from" action="EmployeeAdvanceReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
			<table class="tb_style" style=" width: 88%;">
				<tr>
					<th style="text-align:end;">Select Organisation</th>
					<td style="text-align:start;"><s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='EmployeeAdvanceReport.action?f_org='+this.value"  cssStyle="width:200px;"/></td>
					<th style="text-align:end;">Select WorkLocation</th>
					<td style="text-align:start;"><s:select name="f_strWLocation" listKey="wLocationId"
							listValue="wLocationName" headerKey="-1"
							headerValue="All WorkLocation" list="wLocationList" key=""
							cssStyle="width:200px;" /></td>
				</tr>
				<tr>
					<th style="text-align:end;">Select Department</th>
					<td style="text-align:start;"><s:select name="f_department" list="departmentList"
							listKey="deptId" listValue="deptName" headerKey="0"
							headerValue="All Departments"></s:select></td>
					<th style="text-align:end;">Select Level</th>
					<td style="text-align:start;"><s:select theme="simple" name="f_level" listKey="levelId"
							listValue="levelCodeName" headerKey="-1" headerValue="All Levels"
							list="levelList" key="" /></td>
				</tr>
				<tr>
					<th style="text-align:end;">In Experience From(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="inExpFrom"></s:textfield></td>
					<th style="text-align:end;">In Experience To(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="inExpTo"></s:textfield></td>
				</tr>
				<tr>
					<th style="text-align:end;">Out Experience From(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="outExpFrom"></s:textfield></td>
					<th style="text-align:end;">Out Experience To(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="outExpTo"></s:textfield></td>
				</tr>
				<tr>
					<th style="text-align:end;">Total Experience From(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="totalExpFrom"></s:textfield></td>
					<th style="text-align:end;">Toal Experience To(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="totalExpTo"></s:textfield></td>
				</tr>
				<tr>
					<th style="text-align:end;">Age From(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="ageFrom"></s:textfield></td>
					<th style="text-align:end;">Age To(Yrs)</th>
					<td style="text-align:start;"><s:textfield name="ageTo"></s:textfield>
				</tr>
			</table>
			<s:submit value="Submit" cssClass="input_button"></s:submit>
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>-->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

		</div>

		</s:form>
<%List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
alInnerExport.add(new DataStyle("Employees Full Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("Emp Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Full Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Grade",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Report To",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	
	alInnerExport.add(new DataStyle("Work Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("BirthDate",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Age",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Retire Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("In Exp.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Out Exp.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	reportListExport.add(alInnerExport);  %>
    
	    <table class="display" id="lt">
				<thead>
					<tr> 
						<th style="text-align: left;">Sr.No.</th>
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Full Name</th>
						<th style="text-align: left;">Department</th>
						<th style="text-align: left;">Grade</th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Report To</th>
						<th style="text-align: left;">Work Location</th>
						<th style="text-align: left;">BirthDate</th>
						<th style="text-align: left;">Age</th>
						<th style="text-align: left;">Joining Date</th>
						<th style="text-align: left;">Retire Date</th>
						<th style="text-align: left;">In Exp.</th>
						<th style="text-align: left;">Out Exp.</th>
						
					</tr>
				</thead>
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) { %>
				 <% java.util.List<String> cinnerlist = couterlist.get(i); 
				 	alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(i+1+"",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 

					alInnerExport.add(new DataStyle(cinnerlist.get(1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(4),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(6),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(7),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(8),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(9),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(10),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(11),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(12),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(13),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					reportListExport.add(alInnerExport);
				 %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td class="alignCenter"><%=i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(6) %></td>
						
						<td><%= cinnerlist.get(7) %></td>
						<td><%= cinnerlist.get(8) %></td>
						<td><%= cinnerlist.get(9) %></td>
						<td><%= cinnerlist.get(10) %></td>
						<td><%= cinnerlist.get(11) %></td>
						<td><%= cinnerlist.get(12) %></td>
						<td><%= cinnerlist.get(13) %></td>
					</tr>
					<% } %>
				</tbody>
			</table> 
   <%session.setAttribute("reportListExport",reportListExport); %>
    
		   
			

    </div>
