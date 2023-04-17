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
	
	<jsp:include page="../../common/SubHeader.jsp">
		<jsp:param value="<%=IMessages.TChildWorer %>" name="title"/>
	</jsp:include>



    <div id="printDiv" class="leftbox reportWidth">
          
    <div > 
    <!-- <a style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;padding-right:20px;height:25px;" href="ChildWorker.action?pdfGeneration=true">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
    
    <a href="ChildWorker.action?pdfGeneration=true"><i class="fa fa-file-pdf-o" aria-hidden="true" style="float: right;padding-right:20px;height:25px;"></i></a>
    
    
    </div>
    
    <div>
    	<table class="display" id="lt">
    	
				<thead>
					<tr> 
						<th style="text-align: left;">Sr.No</th>
						<th style="text-align: left;">Name</th>
						<th style="text-align: left;">Date of Birth</th>
						<th style="text-align: left;">Sex</th>
						<th style="text-align: left;">Residentail Address</th>
						<th style="text-align: left;">Father's/Husband Name</th>
						<th style="text-align: left;">Date of Appointment</th>
						<th style="text-align: left;">Alphabet Assigned</th>
						<th style="text-align: left;">Nature of Work</th>
						<th style="text-align: left;">Number of relay</th>
						<th style="text-align: left;">Remarks</th>
					</tr>
				</thead>
				
				<tbody>
				<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
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
					<% } %>
				</tbody>
				
				
			</table> 
   
</div>
    </div>
