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
    
    
    
    <s:form name="frm_Attendance" action="ReportAnalyser" theme="simple">
    
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
                      
            <s:select list="#{'0':'Absentism', '1':'Late Coming', '2':'Early Going', '3':'Irregularity', '4':'ManDays', '5':'OverTime', '6':'Tea Report(worker)', '7':'Tea Report(Staff)', '8':'Leave Report'}" name="option"/>	
            	<s:textfield id="fromDate" name="fromDate"></s:textfield>
			<s:textfield id="toDate" name="toDate"></s:textfield>
	<s:submit value="Submit" cssClass="input_button" ></s:submit>
	                      
	                      
	 <!--                      
	  <a onclick="generateReportPdf();" href="javascript:void(0)" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	      
 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>               
	                       
	   -->                    
	</div>	                      
	                            
	</s:form>
	<%String option=(String)request.getAttribute("option"); %>
	
	
	<%if(option==null || option.equals("0")){ %>
	<%
UtilityFunctions uF = new UtilityFunctions();

List alDates = (List)request.getAttribute("alDates"); 

%>
	
	 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
					
						<th style="text-align: left;">SBU</th>
						
						<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 4+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				%>
				<th style="text-align: left;"><%=strDate %></th>
				<%
			}  
			%>
					<th style="text-align: left;">Total</th>	
					</tr>
				</thead>
				<tbody>	<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); 
				Map<String,String> mp=new HashMap<String,String>();%>
				 <% for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 int totalcount=0;%>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= cinnerlist.get(0) %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
							<%
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 4+ii;
					if(cinnerlist.get(count).equalsIgnoreCase("<div style=\"text-align:center\" class=\"redColor\">A</div>")){
						totalcount++;
						int total=uF.parseToInt(mp.get((String)alDates.get(ii)));
						total++;
						mp.put((String)alDates.get(ii),total+"");
					}
						%>
				<td style="text-align: left;"><%=cinnerlist.get(count) %></td>
				<%
			}  
			%>
			<td><%=totalcount %></td>
					</tr>
					<%} %>
				<tr>
				<td></td>
				<td></td>
				<td></td>
				<td>Total</td>
				
					<% int a=0;
			for (int ii=0; ii<alDates.size(); ii++){
				int count = 4+ii;
				String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
				a+=uF.parseToInt(mp.get((String)alDates.get(ii)));
				%>
				<td style="text-align: left;"><%=uF.showData(mp.get((String)alDates.get(ii)),"0") %></td>
				<%
			}  
			%>
			<td><%=a %></td>
			</tr>
				</tbody>
			</table> 
	<%}else if(option.equals("1") || option.equals("2")) {%>
		 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Date</th>
						<!-- <th style="text-align: left;">SBU</th> -->
						<%if(option.equals("1")){ %>
						<th style="text-align: left;">Roster In</th>
						<th style="text-align: left;">Punch In</th>
						<th style="text-align: left;">Late by</th>
						<%}else{ %>
						<th style="text-align: left;">Roster Out</th>
						<th style="text-align: left;">Punch Out</th>
						<th style="text-align: left;">Early by</th>
						<%} %>
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
							
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if(option.equals("3")){ %>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Date</th>
						<th style="text-align: left;">Roster IN</th>
						<th style="text-align: left;">Punch IN</th>
						<th style="text-align: left;">Late by</th>
						<th style="text-align: left;">Roster Out</th>
						<th style="text-align: left;">Punch Out</th>
						<th style="text-align: left;">Early by</th>
						<th style="text-align: left;">Absent</th>
						<th style="text-align: left;">Single Punch</th>
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
						<td><%= cinnerlist.get(8) %></td>
						
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(9) %></td>
						<td><%= cinnerlist.get(6) %></td>
						<td><%= cinnerlist.get(7) %></td>
						<td><%= cinnerlist.get(10) %></td>
						<td><%= cinnerlist.get(11) %></td>
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if(option.equals("4")){ %>
	<%Map<String,String> attendanceMap=(Map<String,String>)request.getAttribute("attendanceMap");
	List<String> alDates = (List<String> )request.getAttribute("alDates");
	UtilityFunctions uF = new UtilityFunctions();
	%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<%for(int i=0;i<alDates.size();i++){ %>
							<th style="text-align: left;"><%=alDates.get(i) %></th>
						<%} %>
						<th style="text-align: left;">Total</th>
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				
				 <% Map<String,String> countMap=new HashMap();
				 for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 	int count=0;
				 	
				 %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<%for(int j=0;j<alDates.size();j++){
							int countmp=uF.parseToInt(countMap.get(alDates.get(j)));
							if(attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) )!=null){
				 		count++;countmp++;
				 	}%>
							<td style="text-align: left;"><%=attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) )!=null?"P":"-" %></td>
						<%countMap.put(alDates.get(j),countmp+"");} %>
						<td><%=count %></td>
					</tr>
					<%} %>
					<tr id="total">
					<td></td>
					<td></td>
					<td>Total</td>
					<% int grandtotal=0;
					for(int j=0;j<alDates.size();j++){
						grandtotal+=uF.parseToInt(countMap.get(alDates.get(j)));
					%>
					<td><%=countMap.get(alDates.get(j)) %></td>
					<%} %>
					<td><%=grandtotal %></td>
					</tr>
				</tbody>
			</table> 
		<%}else if(option.equals("5")){ %>
	<%Map<String,String> attendanceMap=(Map<String,String>)request.getAttribute("attendanceMap");
	List<String> alDates = (List<String> )request.getAttribute("alDates");
	UtilityFunctions uF = new UtilityFunctions();
	%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<%for(int i=0;i<alDates.size();i++){ %>
							<th style="text-align: left;"><%=alDates.get(i) %></th>
						<%} %>
						<th style="text-align: left;">Total</th>
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				
				 <% Map<String,String> countMap=new HashMap();
				 for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 	int count=0;
				 	
				 %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<%for(int j=0;j<alDates.size();j++){
							int countmp=uF.parseToInt(countMap.get(alDates.get(j)));
							if(attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) )!=null){
								count+=uF.parseToDouble(attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) ));
								countmp+=uF.parseToDouble(attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) ));
				 		
				 	}%>
							<td style="text-align: left;"><%=uF.showData(attendanceMap.get(alDates.get(j)+"_"+cinnerlist.get(0) ),"") %></td>
						<%countMap.put(alDates.get(j),countmp+"");} %>
						<td><%=count %></td>
					</tr>
					<%} %>
					<tr id="total">
					<td></td>
					<td></td>
					<td>Total</td>
					<% int grandtotal=0;
					for(int j=0;j<alDates.size();j++){
						grandtotal+=uF.parseToDouble(countMap.get(alDates.get(j)));
					%>
					<td><%=uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(countMap.get(alDates.get(j)))) %></td>
					<%} %>
					<td><%=grandtotal %></td>
					</tr>
				</tbody>
			</table> 
			<%}else if(option.equals("6") || option.equals("7")){ %>
	<%Map<String,String> attendanceMap=(Map<String,String>)request.getAttribute("attendanceMap");
	List<String> alDates = (List<String> )request.getAttribute("alDates");
	UtilityFunctions uF = new UtilityFunctions();
	
	%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Date</th>
						<th style="text-align: left;">No. of Employee</th>
						<th style="text-align: left;">No. of Tea</th>
						<th style="text-align: left;">Total</th>
					</tr>
				</thead>
				<tbody>	
				
				 <% Map<String,String> countMap=new HashMap();
				 Map<String,String> rosterMap=(Map<String,String> )request.getAttribute("rosterMap");
				 int count=0;
				 int noofTea=3;
				 if(option.equals("6")){
					 noofTea=2;
				 }
				 for (int i=0; i<alDates.size(); i++) {%>
				 <% String date = alDates.get(i);
				 	
				 count+=uF.parseToInt( rosterMap.get(date));
				 %>
				
				 
					<tr id = <%= date %> >
						<td><%= i+1 %></td>
						<td><%= date %></td>
						<td><%=uF.showData(rosterMap.get(date),"0")  %></td>
						<td><%=noofTea %></td>
						<td><%=uF.parseToInt(rosterMap.get(date))*noofTea %></td>
						
						
					</tr>
					<%} %>
					<tr id="total">
					<td></td>
					
					<td>Total</td>
					<td><%=count %></td>
					<td><%=noofTea %></td>
					<td><%=count*noofTea %></td>
					</tr>
				</tbody>
			</table> 
	<%}else if(option.equals("8")){ %>
	<%List<String> alDates = (List<String> )request.getAttribute("alDates");
	UtilityFunctions uF = new UtilityFunctions();
	Map<String,String> empLeaveMap=(Map<String,String> )request.getAttribute("empLeaveMap");
	List<String> leaveList=(List<String> )request.getAttribute("leaveList");%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th rowspan="2" style="text-align: left;">Sr. No.</th> 
						<th rowspan="2" style="text-align: left;">Emp Code</th>
						<th rowspan="2" style="text-align: left;">Employee Name</th>
						<%for(int i=0;i<alDates.size();i++){ %>
						<th style="text-align: left;" colspan="<%=leaveList.size()%>"><%=alDates.get(i) %></th>
						<%} %>
						<th style="text-align: left;" colspan="<%=leaveList.size()%>">Total</th>
						</tr>
						<tr>
						<%for(int i=0;i<alDates.size();i++){ %>
							<%for(int j=0;j<leaveList.size();j++){ %>
									<th style="text-align: left;" ><%=leaveList.get(j) %></th>
							<%} %>	
						<%} %>
						<%for(int j=0;j<leaveList.size();j++){ %>
									<th style="text-align: left;" ><%=leaveList.get(j) %></th>
							<%} %>	
					</tr>
				</thead>
				<tbody>	
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList");
				Map<String,String> mpTotal=new HashMap<String,String>();
				%>
				 <% for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 	Map<String,String> mp=new HashMap<String,String>();
				 	
				 %>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<%for(int k=0;k<alDates.size();k++){ %>
							<%for(int j=0;j<leaveList.size();j++){
								mpTotal.put(alDates.get(k)+"_"+leaveList.get(j),uF.parseToInt(empLeaveMap.get(alDates.get(k)+"_"+cinnerlist.get(0)+"_"+leaveList.get(j)))+"");
								mp.put(leaveList.get(j),uF.parseToInt(empLeaveMap.get(alDates.get(k)+"_"+cinnerlist.get(0)+"_"+leaveList.get(j)))+"");%>
									<td style="text-align: left;" ><%=uF.showData(empLeaveMap.get(alDates.get(k)+"_"+cinnerlist.get(0)+"_"+leaveList.get(j)),"0") %></td>
							<%} %>	
						<%} %>
						<%for(int j=0;j<leaveList.size();j++){ %>
									<td style="text-align: left;" ><%=uF.showData(mp.get(leaveList.get(j)),"0") %></td>
							<%} %>	
					</tr>
					
					
					<%} %>
					<tr>
					<td></td>
					<td></td>
					<td>Total</td>
					<%
					Map<String,String> mp=new HashMap<String,String>();
					for(int i=0;i<alDates.size();i++){ %>
							<%for(int j=0;j<leaveList.size();j++){
								mp.put(leaveList.get(j),mpTotal.get(alDates.get(i)+"_"+leaveList.get(j)));%>
									<td style="text-align: left;" ><%=uF.showData(mpTotal.get(alDates.get(i)+"_"+leaveList.get(j)),"0") %></td>
							<%} %>	
						<%} %>
						
						<%for(int j=0;j<leaveList.size();j++){ %>
									<td style="text-align: left;" ><%=uF.showData(mp.get(leaveList.get(j)),"0") %></td>
							<%} %>	
					</tr>
				</tbody>
			</table>
	<%} %>
	
	
    </div>
   