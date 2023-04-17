<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>


<script type="text/javascript">
 
			
					
		</script>
	
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Candidates" name="title"/>
	</jsp:include>


    <div id="printDiv" class="leftbox reportWidth">


<%-- 	<s:form name="frm_candidate_report_filter" action="CandidateReport1" 
		theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>

					<s:select theme="simple" name="checkStatus_reportfilter" headerKey="0"
				headerValue="Scheduled" 
				list="#{'1':'All' }"
				cssStyle="width:110px" />
				
			<s:submit cssClass="input_button" value="Submit" align="center"  cssStyle="margin:0px" />

		</div>
		</s:form> --%>


  
   <%
   List<List<String>> reportList = (List)request.getAttribute("reportList");
   %>
   


  		<table class="display tb_style" >
    	
				<thead>
					<tr> 
<!-- 						<th style="text-align: left;">Job Code </th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">Work Location</th> -->
						<th style="text-align: left;">Job Code </th>
						<th style="text-align: left;">NAME</th>						
						<th style="text-align: left;">Panel list</th>
						<th style="text-align: left;">Added Date</th>
						<th style="text-align: left;">Facts</th>
						<th style="text-align: left;">Interview Status</th>
					</tr>
				</thead>
				
				<tbody>
		
				 <% for (int i=0; i<reportList.size(); i++) { %>
				 <% List<String> cinnerlist = (List)reportList.get(i); %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
					
<%-- 						<td><%= cinnerlist.get(0) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td> --%>
						<td><%= cinnerlist.get(1) %></td>	
						<td><%= cinnerlist.get(2) %></td>				
						<td><%= cinnerlist.get(6) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(7) %></td>
						<td><%= cinnerlist.get(8) %></td>
					<%-- 	<td><a href="javascript:void(0)" onclick="(confirm('Are you sure you want to delete this employee?')?window.location='AddEmployee.action?operation=D&id=<%= cinnerlist.get(0) %>':'')" class="del"></a></td>
					 --%>	
					</tr>
					<% } %>
					
					<%
				if (reportList.size() == 0 || reportList == null) {
			%>
			<tr>
				<td colspan="6"><div class="nodata msg">
						<span>No Application </span>
					</div></td>
			</tr>
			<%
				}
			%>
					
				</tbody>
	</table>
	
	<br />
	<br />
	<div style="float: left; width: 90%">
		<div>
			<%-- <img style="padding: 5px 5px 0 5px;" border="0"
				src="<%=request.getContextPath()%>/images1/icons/pending.png"> --%><i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5"></i> Interview yet to be Taken
			
		</div>
		<div>
			<%-- <img style="padding: 5px 5px 0 5px;" border="0"
				src="<%=request.getContextPath()%>/images1/icons/approved.png">  --%><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>Approved for next Round
		</div>
		<div>
			<%-- <img style="padding: 5px 5px 0 5px;" border="0"
				src="<%=request.getContextPath()%>/images1/icons/denied.png"> --%> <i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>Rejected
		</div>
	</div>
    </div>
