<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>



	<script type="text/javascript" charset="utf-8">
					$(document).ready(function () {
						
							$('#lt').dataTable({ bJQueryUI: true, 
								  								
								"sPaginationType": "full_numbers",
								"aaSorting": [],
								"sDom": '<"H"lf>rt<"F"ip>',
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
	

    <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
    
     <div > 
   		<a href="<%=request.getContextPath() %>/jsp/reports/factory/factorydocs/form15.pdf"><i class="fa fa-file-pdf-o" aria-hidden="true" style="float:right;float: right;padding-right:20px;height:25px;"></i></a>
    </div>
    
    <div>
    	<table class="display" id="lt">
    	
				<thead>
					<tr> 
						<th style="text-align: left;" rowspan="3">Calendar year of service (i.e Previous Year) </th>
						<th style="text-align: left;" colspan="2" class="ui-state-default">Leave due as on 1st
						 january of the year in coloumn 1</th>
						<th style="text-align: left;" colspan="4" class="ui-state-default">Leave availed during the year</th>
						<th style="text-align: left;" rowspan="3">Leave refused out of regular leave mentioned in coloumn 3</th>
					<!-- 	<th style="text-align: left;" colspan="5" class="ui-state-default">Number of working days for computation of leave during the 
						year mentioned in Column 1</th> -->
						
						
						<th style="text-align: left;" rowspan="3">Regular leave earned for the year mentioned in col. 1</th>
						<th style="text-align: left;" colspan="2" class="ui-state-default">Admisible on 1st January of the year following the year mentioned in column 1</th>
						<th style="text-align: left;" rowspan="3">Leave period(i.e col.4 + col.5 in days)</th>
					<!-- 
						<th style="text-align: left;" colspan="3" class="ui-state-default">Details of wages paid</th>
					 -->
						<th style="text-align: left;" rowspan="3">Total wages paid for the period of leave with wages enjoyed(Rs)(col.17 * col.20)</th>
						<th style="text-align: left;" rowspan="3">Signature</th>
						
					</tr>
					
					
					<tr>
					<th style="text-align: left;" rowspan="2"></th>
					<th style="text-align: left;" rowspan="2"></th>
					
					<th style="text-align: left;" rowspan="2">Refused</th>
					<th style="text-align: left;" rowspan="2">Regular</th>
					<th style="text-align: left;" colspan="2" class="ui-state-default">Dates</th>
					
					<!-- <th style="text-align: left;" rowspan="2">Days worked</th>
					<th style="text-align: left;" rowspan="2">Lay off</th>
					<th style="text-align: left;" rowspan="2">Maternity leave upto 12 weeks</th>
					<th style="text-align: left;" rowspan="2">Leave with wages enjoyed</th>
					<th style="text-align: left;" rowspan="2">Total (9 to 12)</th>
				 -->	
					<th style="text-align: left;" rowspan="2">Refused(Col. 2+8-4)</th>
					<th style="text-align: left;" rowspan="2">Regular(Col. 4+14 -5-8)</th>
					
					<!-- <th style="text-align: left;" rowspan="2">Normal rates of wages excluding
					of any overtime as well as bonus but including of Dearness Allowance(Rs.)</th>
					<th style="text-align: left;" rowspan="2">Cash 
								equivalent of the advantages occurring through the concessional sale of food grains and other articles 
					</th>
					<th style="text-align: left;" rowspan="2">Rate of wages for leave with wages paid (Rs.) (Col.18 + Col.19)
								</th>
				 -->	
					</tr>
					
					<tr>
								<th style="text-align: left;" >From</th>
								<th style="text-align: left;" >To</th>
					</tr>
				</thead>
				
				<tbody>
				<%-- <% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) { %>
				 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
					
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
					</tr>
					<% } %> --%>
				</tbody>
				
				
			</table> 
   
	</div>

    </div>
