<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>
<script type="text/javascript" charset="utf-8">
		$(document).ready( function () {
						var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
						var sbCountryList = '<%= ((String)request.getAttribute("sbCountryList")) %>';
						
							$('#lt1').dataTable({ bJQueryUI: true, "bProcessing": true, "sPaginationType": "full_numbers",
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
	<jsp:param value="Work Force Joining Report" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">
 

<s:form name="frmWorkForce" action="WorkForceReport" theme="simple">

<div class="filter_div">
    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="wLocationList" key=""  
                         onchange="document.frmWorkForce.submit();"/>
                    
    <s:select name="f_department" list="departmentList" listKey="deptId"  cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments" 
    			onchange="document.frmWorkForce.submit();">
    			</s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            list="levelList" key="" required="true" onchange="document.frmWorkForce.submit();"/>
     
       
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>
    
    
   

    </div>
</s:form>



<div class="scroll" style="float:left;width:100%">
<%-- 		
<display:table name="alReport" cellspacing="1" class="tb_style" export="true" 
	 id="lt1" requestURI="WorkForceJoinReport.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="WorkForce.xls" />
	<display:setProperty name="export.xml.filename" value="WorkForce.xml" />
	<display:setProperty name="export.csv.filename" value="WorkForce.csv" />
	
	<display:column style="text-align:left" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	<display:column style="text-align:left" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	<display:column  style="width:120px;text-align:left" nowrap="nowrap" title="Location" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	<display:column  style="width:120px;text-align:left" nowrap="nowrap" title="Department" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	<display:column  style="width:120px;text-align:left" nowrap="nowrap" title="Designation" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	<display:column  style="width:120px;text-align:left" nowrap="nowrap" title="Joining Date"> <%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
</display:table> --%>





	<table class="display" id="lt1">
			<thead>
				<tr>
					<th>Employee Code</th>
					<th>Employee Name</th>
					<th>Location</th>
					<th>Department</th>
					<th>Designation</th>
					<th>Primary Email</th>
					<th>Secondary Email</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("alReport"); %>
			 <% for (int i=0; i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr>
					<td><%= cinnerlist.get(0) %></td>
					<td><%= cinnerlist.get(1) %></td>
					<td><%= cinnerlist.get(2) %></td>
					<td><%= cinnerlist.get(3) %></td>
					<td><%= cinnerlist.get(4) %></td>
					<td><%= cinnerlist.get(5) %></td>
					<td><%= cinnerlist.get(6) %></td>
				</tr>
				<% } %>
			</tbody>
		</table> 
		
</div>
	
	

</div>
