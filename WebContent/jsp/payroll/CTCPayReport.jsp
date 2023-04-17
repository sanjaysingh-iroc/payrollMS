<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>




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

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="CTCPay Report" name="title"/>
</jsp:include>


	


    <div id="printDiv" class="leftbox reportWidth">
    
<s:form name="frm_from" action="CTCPayReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
				
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='CTCPayReport.action?f_org='+this.value"  cssStyle="width:200px;"/>
						
			<s:select name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						headerKey="-1" headerValue="All WorkLocation" 
						list="wLocationList" key=""  cssStyle="width:200px;"/>
						
			 <s:select name="f_department" list="departmentList" listKey="deptId"  
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			
    			></s:select> 
    			
			<s:select theme="simple" name="f_level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             list="levelList" key="" /> 
	            
	          <s:select list="#{'220':'LTA', '209':'Medical'}" name="salaryHead"   />
	             
	            
	          <s:submit value="Submit" cssClass="input_button"></s:submit>
		</div>

		</s:form>
 
    
	    <table class="display" id="lt">
				<thead>
					<tr> 
					<th style="text-align: left;">Sr. No.</th>
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Full Name</th>
						<th style="text-align: left;">Total Balance</th>
						<th style="text-align: left;">Pay Amount</th>
						
					</tr>
				</thead>
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) { %>
				 <% java.util.List<String> cinnerlist = couterlist.get(i); 
					%>
				 
					<tr id = <%= cinnerlist.get(0) %> >
					<td><%= i+1 %></td>
						<td><%= cinnerlist.get(1) %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						
					</tr>
					<% } %>
				</tbody>
			</table> 
   

    </div>
<div id="viewEmployeeDiv"></div>
    