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
    
    
    
    <s:form name="frm_Attendance" action="HRReportAnalyser" theme="simple">
    
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
                      
            <s:select list="#{'0':'Language Report', '1':'Qualification Report', '2':'Email', '3':'Address', '4':'Document Report', '5':'25 years completion report'}" name="option"/>		
			
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

	List<String> languageList=(List<String> )request.getAttribute("languageList");%>

	
	 <table class="display" id="lt">
				<thead>
					<tr>
						<th rowspan="2" style="text-align: left;">Sr. No.</th> 
						<th rowspan="2" style="text-align: left;">Emp Code</th>
						<th rowspan="2" style="text-align: left;">Employee Name</th>
						<%for(String language:languageList){ %>
						<th colspan="3" style="text-align: center;"><%=language %></th>
						<%} %>
					</tr>
					<tr>
					<%for(String language:languageList){ %>
						<th style="text-align: center;">Read</th>
						<th style="text-align: center;">Write</th>
						<th style="text-align: center;">Speak</th>
						<%} %>
						</tr>
				</thead>
				<tbody>	<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); 
				Map<String,Map<String,Map<String,String>>> mp=(Map<String,Map<String,Map<String,String>>>)request.getAttribute("mp");
				Map<String,String> mpLanguage=new HashMap<String,String>();
			for (int i=0; i<couterlist.size(); i++) {%>
				 <% List<String> cinnerlist = couterlist.get(i);
				 Map<String,Map<String,String>> innerMp= mp.get(cinnerlist.get(0));
				 if(innerMp==null)innerMp=new HashMap<String,Map<String,String>>();
				 int totalcount=0;%>
				
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<%for(String language:languageList){ 
							Map<String,String> innerlist=innerMp.get(language);
							if(innerlist==null)innerlist=new HashMap<String,String>();
							if(innerlist.get("READ") !=null && innerlist.get("READ").equals("1")){
								int a=uF.parseToInt(mpLanguage.get(language+"_READ"));
								a++;
								mpLanguage.put(language+"_READ",a+"");
							%>
								<td class="textblue yes"></td>
							<%}else{%>
								<td class="textblue no"></td>
							<%}
							if(innerlist.get("WRITE") !=null && innerlist.get("WRITE").equals("1")){
								int a=uF.parseToInt(mpLanguage.get(language+"_WRITE"));
								a++;
								mpLanguage.put(language+"_WRITE",a+"");%>
							
								<td class="textblue yes"></td>
							<%}else{%>
								<td class="textblue no"></td>
							<%}
							
							if(innerlist.get("SPEAK") !=null && innerlist.get("SPEAK").equals("1")){
							int a=uF.parseToInt(mpLanguage.get(language+"_SPEAK"));
								a++;
								mpLanguage.put(language+"_SPEAK",a+"");%>
								<td class="textblue yes"></td>
							<%}else{%>
								<td class="textblue no"></td>
							<%}%>
						
						
						<%} %>
						
					
			
					</tr>
					<%} %>
				<tr>
				<td></td>
				<td></td>
				<td>Total</td>
				<%for(String language:languageList){ %>
						<td style="text-align: center;"><%=uF.showData(mpLanguage.get(language+"_READ"),"0") %></td>
						<td style="text-align: center;"><%=uF.showData(mpLanguage.get(language+"_WRITE"),"0") %></td>
						<td style="text-align: center;"><%=uF.showData(mpLanguage.get(language+"_SPEAK"),"0") %></td>
						<%} %>
				</tr>
				</tbody>
			</table> 
	<%}else if(option.equals("1") ) {%>
		 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Qualification</th>
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
							
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if( option.equals("2") || option.equals("3")){ %>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<%if( option.equals("2")){ %>
							<th style="text-align: left;">Email ID(Official)</th>
							<th style="text-align: left;">Email ID(Personal)</th>
							
						<%}else if( option.equals("3")){ %>
						<th style="text-align: left;">Present Adress</th>
							<th style="text-align: left;">Permanent Adress</th>
							
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
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%}else if(option.equals("4")){ %>
	<%Map<String,List<String>> documentMp=(Map<String,List<String>>)request.getAttribute("documentMp");
	UtilityFunctions uF = new UtilityFunctions();
	%>
	<table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Document Details</th>
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
							<td><a href="<%=request.getContextPath() %>/<%=innerlist.get(j+1) %>">View</a></td>
							</tr>
						<%} %>
						</table>
						</td>
					</tr>
					<%} %>
					
				</tbody>
			</table> 
		<%}else if(option.equals("5") ) {%>
		 <table class="display" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Sr. No.</th> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Employee Name</th>
						<th style="text-align: left;">Joining Date</th>
						<th style="text-align: left;">Experience(in Years)</th>
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
							
					</tr>
					<%} %>
				</tbody>
			</table> 
	<%} %>
	
	
    </div>
   