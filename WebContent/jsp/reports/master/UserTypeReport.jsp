<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
		$(document).ready( function () {
				$('#lt').dataTable({ bJQueryUI: true, "sPaginationType": "full_numbers",
					"aaSorting": []
				})
		});
</script>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Access Control Level" name="title"/>
</jsp:include>

   <div id="printDiv" class="leftbox reportWidth">
   
   <%-- 
   <table class="display" id="lt">
			<thead>
				<tr>
					<th>User Type</th>
					<th>ACL</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr>
					<td><%= cinnerlist.get(0) %></td>
					<td><%= cinnerlist.get(1) %></td>
				</tr>
				<% } %>
			</tbody>
	</table> 
	--%>
	
	<div style="text-align:center">
		<img src="<%=request.getContextPath()%>/images1/acl.png" border="0" usemap="#aclMap" />
	</div>
	
	<map name="aclMap" id="aclMap">
		<area shape="rect" coords="338,5,545,75" href="javascript:void(0)" onclick="alert('This is the highest level and can not be edited');" alt="Administrator" /> 
		<area shape="rect" coords="64,115,286,186" href="ManageACL.action?U=5" alt="ceo" />
		<area shape="rect" coords="527,112,723,183" href="ManageACL.action?U=6" alt="cfo" />
		<area shape="rect" coords="97,225,311,295" href="ManageACL.action?U=7" alt="HR_Manager" />
		<area shape="rect" coords="496,226,705,295" href="ManageACL.action?U=4" alt="Accountant" />
		<area shape="rect" coords="320,342,472,412" href="ManageACL.action?U=2" alt="manager" />
		<area shape="rect" coords="303,458,512,539" href="ManageACL.action?U=3" alt="Employees" />
	</map>
	
	
	
	 
   
</div>