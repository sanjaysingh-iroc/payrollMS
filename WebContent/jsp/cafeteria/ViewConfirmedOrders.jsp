<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<link rel="stylesheet" type="text/css" href="js/datatables_new/dataTables.bootstrap.min.css" />
<script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" src="js/datatables_new/dataTables.responsive.min.js"></script>
<script type="text/javascript">


$(document).ready(function () {
	
	<%-- $('#lt').dataTable({ bJQueryUI: true, 
		  								
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
	}); --%>
	$('#lt').DataTable();
});

</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, List<String>> hmDishOrders = (Map<String, List<String>>)request.getAttribute("hmDishOrders");
	if(hmDishOrders == null ) hmDishOrders = new HashMap<String, List<String>>();
    String dishId = (String)request.getAttribute("dishId");
   
  
%>

<div style="float:left;width:100%;">	
	<s:form id = "frmViewConfirmedOrders" name="frmViewConfirmedOrders" theme ="simple">
	    <s:hidden name="dishId"/>
	   
			<table class="table-bordered table" id="lt">
				<thead>
					<tr>
						<th style="text-align: left;">Employee</th>
						<th style="text-align: left;">Ordered by</th>
						<th style="text-align: left;">Quantity</th>
						<th style="text-align: left;">Ordered on</th>
						<th style="text-align: left;">Confirmed by </th>
						<th style="text-align: left;">Confirmed on </th>
						<th style="text-align: left;">Guests </th>
					</tr>
				</thead>
				<tbody>
				<% if(hmDishOrders != null && hmDishOrders.size() >0){
					 Set orderSet = hmDishOrders.keySet();
					 Iterator<String> it = orderSet.iterator();
					 while(it.hasNext()) {
					   String orderId = it.next();
					   List<String> dishDetailsList = (List<String>) hmDishOrders.get(orderId);
					   if(dishDetailsList == null) dishDetailsList = new ArrayList<String>();
					   if(dishDetailsList != null && dishDetailsList.size() > 0) {
					%> 
				 		
						<tr id = "mainDiv_<%=orderId %>" >
							<td><%=dishDetailsList.get(6) %></td>
							<td><%=dishDetailsList.get(5) %></td>
							<td><%=dishDetailsList.get(3)%></td>
							<td><%=dishDetailsList.get(4)%></td>
							<td><%=dishDetailsList.get(7)%></td>
							<td><%=dishDetailsList.get(8)%></td>
							<td><%=dishDetailsList.get(9)%></td>
						</tr>
					<% 
						}
					  }
					}%>
				</tbody>
			</table>
	</s:form>
</div>
	


