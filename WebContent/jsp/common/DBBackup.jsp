<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@taglib uri="/struts-tags" prefix="s"%>




<script type="text/javascript" charset="utf-8">
$(document).ready( function () {
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
	<jsp:param value="Database Backup" name="title"/>
  </jsp:include>
  
<div class="reportWidth">

 
 <s:form action="DBBackup" theme="simple" cssStyle="float:right;margin-bottom:10px">
 	<s:submit name="submit" value="Take Backup" cssClass="input_button"></s:submit>
 </s:form>
 
 
 <%
 if(request.getAttribute(IConstants.MESSAGE)!=null){
	 out.println(request.getAttribute(IConstants.MESSAGE));
 }
 %>
 
 <%List alReport = (List)request.getAttribute("alReport"); %>
 
 
<table id="lt" class="tb_style" width="100%">
<thead>
	<tr>	
		<th>Backup File Name</td>
		<th>Backup Taken Date</td>
		<th>Backup Size</td>
		<th>Download Backup</td>
	</tr>
</thead>
 
<tbody>
<%
for(int i=0;alReport!=null && i<alReport.size(); i++){
	List alInner = (List)alReport.get(i);
%>
 
 		<tr>	
 			<td><%=alInner.get(0)%></td>
 			<td><%=alInner.get(1)%></td>
 			<td><%=alInner.get(2)%></td>
 			<td><%=alInner.get(3)%></td>
 		</tr>
<%} %>
</tbody>
</table>

</div>
