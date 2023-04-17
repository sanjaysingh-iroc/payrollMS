<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script>

function generateReportPdf(){

	
	window.location = "ExportPdfReport.action";
}function generateReportExcel(){
		
		
		window.location = "ExportExcelReport.action";
		
	
}
</script>


 <script>

$(function() {
	
    $( "#fromDate" ).datepicker({dateFormat: 'dd/mm/yy', changeYear: true});
    $( "#toDate" ).datepicker({dateFormat: 'dd/mm/yy', changeYear: true});
    
});
</script> 


	<script type="text/javascript" charset="utf-8">
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


<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Report Analyser" name="title"/>
</jsp:include>
   


    <div class="leftbox reportWidth">
    
    
    
    <s:form name="frm_Attendance" action="EmployeeWiseReport" theme="simple">
    
	<div class="filter_div">
    <div class="filter_caption">Filter</div>			
			<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         onchange="document.frm_Attendance.submit();"  	
                         list="organisationList" key=""  />
                         
			<s:select theme="simple" name="strWLocation" listKey="wLocationId"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frm_Attendance.submit();" 		
                         list="wLocationList" key=""  />
                    
             <s:select name="department" list="departmentList" listKey="deptId" 
             			listValue="deptName" headerKey="0" headerValue="All Departments" 
             			onchange="document.frm_Attendance.submit()"></s:select>
             			
			<s:select name="service" list="serviceList" listKey="serviceId" 
						listValue="serviceName" headerKey="0" headerValue="All Services"
						 onchange="document.frm_Attendance.submit()"></s:select>     
                      
            <s:select list="#{'0':'Department Wise', '1':'Company Wise', '2':'Grade Wise', '3':'Designation Wise', '4':'Qualification Wise', '5':'Bloodgroup Wise'}" name="option"/>		
			
	<s:submit value="Submit" cssClass="input_button" ></s:submit>
	                      
	                      
	 <!--                      
	  <a onclick="generateReportPdf();" href="javascript:void(0)" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	      
 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	                       
	   -->                    
	</div>	                      
	                            
	</s:form>

		 <table class="display" id="lt">
				
					
				
				
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList");
				List<String> headlist = couterlist.get(0);
					%>
					<thead>
					<tr id = <%= headlist.get(0) %> >
					<% for(int j=0;j<headlist.size();j++){
						 %>
						<th style="text-align: left;"><%= headlist.get(j) %></th>
						 <%} %>
						 </tr>
						 </thead>
						 <tbody>	
				 <% for (int i=1; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);%>
				 <tr id = <%= cinnerlist.get(0) %> >
				 <%for(int j=0;j<cinnerlist.size();j++){
				 %>
					<td><%= cinnerlist.get(j) %></td>
					 <%} %>
				</tr>
				 
					
					<%} %>
				</tbody>
			</table> 
	
	
	
    </div>
   