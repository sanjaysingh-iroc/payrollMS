<%@page import="java.util.*"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<script type="text/javascript" charset="utf-8">
$(document).ready(function () {
		$('#lt').dataTable({ bJQueryUI: true, 
			"sPaginationType": "full_numbers",
			"iDisplayLength": 1000,
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
	<jsp:param value="Documents" name="title"/>
</jsp:include>


  

<div id="printDiv" class="leftbox reportWidth">
 
 
<div class="filter_div">
<div class="filter_caption">Filter</div>
<s:form name="frmDocuments" action="DocumentReport" theme="simple">
	<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         onchange="document.frmDocuments.submit();"
                         list="organisationList" key=""  />
                         
    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         onchange="document.frmDocuments.submit();"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId" cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			onchange="document.frmDocuments.submit();" 
    			></s:select>
    
    <s:select name="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
    			listValue="serviceName" headerKey="0" headerValue="All SBUs"
    			onchange="document.frmDocuments.submit();" 
    			></s:select>
     
	
</s:form>
</div>
 
<table id="lt" width="100%" class="tb_style display">

<thead>
	<tr>
		<th width="10%">Employee Code</th>
		<th width="25%">Employee Name</th>
		<th width="15%">Document Name</th>
		<th width="10%">Issued By</th>
		<th width="10%">Issued On</th>
		<th width="10%">With Header & Footer</th>
		<th width="10%">Without Header & Footer</th>
	</tr>
</thead>	
	<tbody>
	
	<%
		List alReport = (List)request.getAttribute("alReport");
		for(int i=0; i<alReport.size(); i++){
			List alInner = (List)alReport.get(i);
	%>
	
	<tr>
		<td><%=alInner.get(0)%></td>
		<td><%=alInner.get(1)%></td>
		<td><%=alInner.get(2)%></td>
		<td><%=alInner.get(3)%></td>
		<td><%=alInner.get(4)%></td>
		<td align="right"><%=alInner.get(5)%></td>
		<td align="right"><%=alInner.get(6)%></td>
	</tr>
	
	<%} %>

</tbody>

</table>






</div>






