<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
}); 		
			
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Exist Appraisal Systems" name="title"/>
</jsp:include>
<% String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);%>

    <div id="printDiv" class="leftbox reportWidth">  
    <s:property value="message"/>    
   
    	<table class="display" id="lt">
    	<% UtilityFunctions uF=new UtilityFunctions(); %>
				<thead>
					<tr> 
						<th style="text-align: left;">Appraisal Name</th>
						<th style="text-align: left;">Appraisal Type</th>
						<th style="text-align: left;">Orientation Type</th>
						<th style="text-align: left;">Frequency Type</th>
						<th style="text-align: left;">From Date</th>
						<th style="text-align: left;">End Date</th>
						<th style="text-align: left;">Location</th>
						<th style="text-align: left;">Added By</th>
						<th style="text-align: left;">Entry Date</th>
						<th style="text-align: left;">Choose</th>
						
					</tr>
				</thead>
				
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("outerList"); 
				
				for(int i=0;couterlist!=null && i<couterlist.size();i++){
					List<String> innerList=couterlist.get(i);%>
					
					<tr id = "<%= innerList.get(0) %>">
						<td><%= innerList.get(1) %></td>
						<td><%= innerList.get(3) %></td>
						<td><%= innerList.get(2) %>&deg;</td>
						<td><%= innerList.get(4) %></td>
						<td><%= innerList.get(5) %></td>
						<td><%= innerList.get(6) %></td>	
						<td><%= innerList.get(7) %></td>		
						<td><%= innerList.get(8) %></td>
						<td><%= innerList.get(9) %></td>		
						<td>
							<a href="javascript:void(0);" onclick="if(confirm('Are you sure, You want to create Appraisal from this template?')) window.location='CreateAppraisalFromTemplate.action?existID=<%=innerList.get(0) %>&appFreqId=<%=innerList.get(11)%>';">Choose</a>
						</td>
							
					</tr>
				<%}	%>
				
				</tbody>
			</table> 
    </div>
