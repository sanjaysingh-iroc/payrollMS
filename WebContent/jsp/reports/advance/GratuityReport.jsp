<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>

	<script type="text/javascript" charset="utf-8">
	 
	
	function generateReportExcel(){
		window.location="ExportExcelReport.action";
	}


				$(document).ready(function () {
					
						$('#lt').DataTable({
							"order": [],
							"columnDefs": [ {
							      "targets"  : 'no-sort',
							      "orderable": false
							    }],
							'dom': 'lBfrtip',
					        'buttons': [
								'copy', 'csv', 'excel', 'pdf', 'print'
					        ]
					  	});
				});
				
	</script>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Gratuity Report" name="title"/>
</jsp:include>
 --%>

	


    <div id="printDiv" class="leftbox reportWidth">
    
<s:form name="frm_from" action="GratuityReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
				
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='ManPowerReport.action?f_org='+this.value"  cssStyle="width:200px;"/>
						
			<s:select name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						headerKey="-1" headerValue="All WorkLocation" 
						list="wLocationList" key=""  cssStyle="width:200px;"/>
						
			 <s:select name="f_department" list="departmentList" listKey="deptId"  
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			
    			></s:select> 
    			
			<s:select theme="simple" name="level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             list="levelList" key="" /> 
	            
	             
	            
	          <s:submit value="Submit" cssClass="input_button"></s:submit>
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

		</div>

		</s:form>
 <%List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
String type=(String)request.getAttribute("type");

	
		alInnerExport.add(new DataStyle("Gratuity Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("Emp Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Full Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Gender",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Grade",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	
	alInnerExport.add(new DataStyle("Date of Birth",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Date of Joining",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Experience",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Gratuity amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Amount Recieved",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	reportListExport.add(alInnerExport);  %>
    
	    <table class="display" id="lt">
				<thead>
					<tr> 
					<th style="text-align: left;">Sr. No.</th>
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Full Name</th>
						<th style="text-align: left;">Gender</th>
						<th style="text-align: left;">Department</th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Grade</th>
						<th style="text-align: left;">Date of Birth</th>
						<th style="text-align: left;">Date of Joining</th>
						<th style="text-align: left;">Experience</th>
						<th style="text-align: left;">Gratuity amount</th>
						<th style="text-align: left;">Amount Recieved</th>
						<th style="text-align: left;">Balance</th>
					</tr>
				</thead>
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("outerList"); %>
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
					alInnerExport.add(new DataStyle(cinnerlist.get(12),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(11),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					reportListExport.add(alInnerExport); %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
					<td><%= i+1 %></td>
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
						<td><%= cinnerlist.get(12) %></td>
						<td><%= cinnerlist.get(11) %></td>
					</tr>
					<% } %>
				</tbody>
			</table> 
   
     <%session.setAttribute("reportListExport",reportListExport); %>
		   
			

    </div>
    