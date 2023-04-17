<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>


<script type="text/javascript" charset="utf-8">
	
function uploadPerformanceFile(reviewId) {
	//alert("openQuestionBank id "+ id)
	var pageTitle = 'Upload Performance File';
	removeLoadingDiv('the_div');
	 var dialogEdit = '#addNewPerformanceDiv';
	 dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 450,
				width : 600,
				modal : true,
				title : ''+pageTitle,
				open : function() {
					var xhr = $.ajax({
						url : "AddEmpPerformance.action?reviewId="+reviewId,
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

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Reviews" name="title" />
</jsp:include>

<div class="leftbox reportWidth">

	<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> allAppraisalreport = (List<List<String>>) request.getAttribute("allAppraisalreport");
		String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
		
		String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
		//System.out.println("strUserType ---------->> " +strUserType);
	%>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

	<% if(strUserType != null && strUserType.equals(IConstants.EMPLOYEE)) { %>
	
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="tb_style">
			<tbody>
				<tr class="darktable">
					<th style="text-align: left;">Reviews Name</th>
					<th style="width: 15%;text-align: center;">Download</th>
					<th style="width: 25%;text-align: center;">Upload</th>
				</tr>

				<%
					for (int i = 0; i < allAppraisalreport.size(); i++) {
						List<String> alinner = (List<String>) allAppraisalreport.get(i);
				%>
				<tr class="lighttable">
					<td><%=alinner.get(1)%></td>
					<td align="center"><a href="<%=docRetriveLocation+IConstants.I_PERFORMANCE+"/"+alinner.get(0)+"/"+alinner.get(2)%>">Download</a></td>
					<td> 
					<% if(alinner.get(3) != null && alinner.get(3).length()> 0) { %>
						<a href="<%=docRetriveLocation+IConstants.I_PERFORMANCE+"/"+alinner.get(4)+"/"+alinner.get(3)%>"><%=alinner.get(3) %></a>
					<% } else { %>
						No file uploaded.
					<% } %>
					&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" class="input_button" value="Upload" onclick="uploadPerformanceFile('<%=alinner.get(0) %>');"/>
					</td>
				</tr>
				<% } 
					if(allAppraisalreport.size() == 0) {
				%>

				<tr class="lighttable">
					<td colspan="8"><div class="nodata msg"> <span> No Data Available</span> </div> </td>
				</tr>
				<% } %>

			</tbody>
		</table>

	<% } else { %>
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="tb_style">
			<tbody>
				<tr class="darktable">
					<th style="text-align: left;">Reviews Name</th>
					<th style="width: 15%;text-align: center;">Employee Name</th>
					<th style="width: 25%;text-align: center;">Download</th>
				</tr>

				<%
					for (int i = 0; i < allAppraisalreport.size(); i++) {
						List<String> alinner = (List<String>) allAppraisalreport.get(i);
				%>
				<tr class="lighttable">
					<td><%=alinner.get(1)%></td>
					<td><%=alinner.get(5)%></td>
					<td align="center"><a href="<%=docRetriveLocation+IConstants.I_PERFORMANCE+"/"+alinner.get(4)+"/"+alinner.get(3)%>"><%=alinner.get(3) %></a></td>
				</tr>
				<% } 
					if(allAppraisalreport.size() == 0) {
				%>

				<tr class="lighttable">
					<td colspan="8"><div class="nodata msg"> <span> No Data Available</span> </div> </td>
				</tr>
				<% } %>

			</tbody>
		</table>
		
	<% } %>	

</div>

<div id="addNewPerformanceDiv"></div>