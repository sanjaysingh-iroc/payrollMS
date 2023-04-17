<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.*"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%@ taglib uri="http://granule.com/tags" prefix="g" %>

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


function sendDocument(doc_id) {

	removeLoadingDiv("the_div");
	var dialogEdit = '#sendMail';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'Send Document', 
		open : function() {
			var xhr = $.ajax({
				url : "SendDocument.action?doc_id="+doc_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
			xhr = null;

		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		}
	});

	$(dialogEdit).dialog('open');
}
	
</script>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Issue Documents" name="title"/>
</jsp:include>


  

<div id="printDiv" class="leftbox reportWidth">
 
 <%=(session.getAttribute(IConstants.MESSAGE)!=null)?session.getAttribute(IConstants.MESSAGE):"" %>
 
 
<div class="filter_div">
<div class="filter_caption">Filter</div>
<s:form name="frmDocuments" action="IssueDocument" theme="simple">
	<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         onchange="document.frmDocuments.submit();"
                         list="organisationList" key=""  />
</s:form>
</div>
 
<table width="100%" class="tb_style">

<thead>
	<tr>
		<th width="45%">Document Name</th>
		<th width="45%">Aligned Node</th>
		<th width="5%">View</th>
		<th width="5%">Issue</th>
	</tr>
</thead>	
	<tbody>
	
	<%
		List alReport = (List)request.getAttribute("alReport");
		for(int i=0; i<alReport.size(); i++){
			List alInner = (List)alReport.get(i);
	%>
	
	<tr>
		
		<td><%=alInner.get(1)%></td>
		<td><%=alInner.get(2)%></td>
		<td align="right"><%=alInner.get(3)%></td>
		<td align="right"><%=alInner.get(4)%></td>
	</tr>
	
	<%} %>

</tbody>

</table>






</div>






