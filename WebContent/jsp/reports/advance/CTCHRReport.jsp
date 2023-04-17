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
    
    
    
    <s:form name="frm_Attendance" action="CTCHRReport" theme="simple">
    
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
                      
            <s:select list="#{'0':'Joining CTC Report', '1':'Promotion Report', '2':'Transfer Report', '3':'Increment Report'}" name="option"/>		
			
	<s:submit value="Submit" cssClass="input_button" ></s:submit>
	                      
	                      
	 <!--                      
	  <a onclick="generateReportPdf();" href="javascript:void(0)" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	      
 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	                       
	   -->                    
	</div>	                      
	                            
	</s:form>
	<%String option=(String)request.getAttribute("option"); %>
	
	
	
	<%if(option==null || option.equals("0") ) {%>
		 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;"> Joining CTC</th>
						<th style="text-align: left;"> Deduction Amount</th>
						<th style="text-align: left;"> Net Salary</th>
						
						
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i); %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if( option.equals("1")){ %>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
							<th style="text-align: left;">Grade</th>
							<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Department</th>
							<th style="text-align: left;">Level</th>
							<th style="text-align: left;">Effective Date</th>
							
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i); %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(6) %></td>
						<td><%= cinnerlist.get(7) %></td>
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if(option.equals("2")){ %>
	<%Map<String,List<String>> documentMp=(Map<String,List<String>>)request.getAttribute("documentMp");
	UtilityFunctions uF = new UtilityFunctions();
	%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				
				 <%
				 for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 List<String> innerlist=documentMp.get(cinnerlist.get(0));
				 
				 %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td>
						<table>
						<%for(int j=0;innerlist!=null && j<innerlist.size();j=j+2){ %>
							<tr>
							<td><%=innerlist.get(j) %></td>
							<td><%=innerlist.get(j+1) %></td>
							</tr>
						<%} %>
						</table>
						</td>
					</tr>
					<%} %>
					
				</tbody>
			</table> 
		<%}else if(option.equals("3") ) {%>
		 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Effective Date</th>
						<th style="text-align: left;">Previous Salary Amount</th>
						<th style="text-align: left;">New Salary Amount</th>
						<th style="text-align: left;">Difference in amount</th>
						<th style="text-align: left;">Difference in %</th>
						
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i); %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(6) %></td>
						<td><%= cinnerlist.get(7) %></td>
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%} %>
	
	
    </div>
   