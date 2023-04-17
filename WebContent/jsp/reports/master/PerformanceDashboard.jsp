<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>


<script type="text/javascript" charset="utf-8">
	
function addNewPerformance() {
	//alert("openQuestionBank id "+ id)
	var pageTitle = 'New Performance Form';
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
						url : "AddNewPerformance.action",
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
		
		int totalAppraisal1 = 0;
		int totalEmps1 = 0;
		
		for (int i = 0; i < allAppraisalreport.size(); i++) {
			List<String> alinner = (List<String>) allAppraisalreport.get(i);

			totalAppraisal1 = allAppraisalreport.size();
			totalEmps1 += uF.parseToInt(alinner.get(5));
			
		}
	%>

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

	<%-- <% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%> --%>
	
		<div class="filter_div">
			<div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAppraisal1 %></p>
                    <p class="sk_name" style="text-align:center">Reviews</p>
               </div>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalEmps1 %></p>
                    <p class="sk_name" style="text-align:center">Reviewee</p>
               </div>
		</div>
	
	<%-- <% } %> --%>

	<div class="attendance">

	<% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
	<div style="float: right; margin-bottom: 10px">
		<a href="javascript:void(0);" onclick="addNewPerformance();"><input type="button" class="input_button" value="Add New Performance"> </a>
	</div>
	<%} %>
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="tb_style">
			<tbody>
				<tr class="darktable">
					<th style="text-align: left;">Reviews Name</th>
					<th style="width: 15%;text-align: center;">Reviewees</th>
					<th style="width: 10%;text-align: center;">Action</th>
				</tr>

				<%
					int totalAppraisal = 0;
					int totalEmps = 0;
					
					for (int i = 0; i < allAppraisalreport.size(); i++) {
						List<String> alinner = (List<String>) allAppraisalreport.get(i);
						totalAppraisal = allAppraisalreport.size();
						totalEmps += uF.parseToInt(alinner.get(5));
				%>
				<tr class="lighttable">
					<td><%=alinner.get(1)%></td>
					<td class="blueColor" align="center"><strong> <%=alinner.get(5)%></strong></td>
					<td align="center">
					<% if(uF.parseToInt(alinner.get(6)) == 0) { %>
						<a onclick="return confirm('Are you sure, you wish to delete this review?')" class="del" style="float: left; margin-left: 5px;" 
							href="PerformanceDashboard.action?operation=D&reviewId=<%=alinner.get(0)%>">-</a>
					<% } else { %>
						<a onclick="alert('You can not delete this review, Employee upload their file?')" class="del" style="float: left; margin-left: 5px;" href="javascript:void(0);">-</a>
					<% } %>	
					</td>
				</tr>
				<% } 
				if (allAppraisalreport.size() == 0) {
				%>

				<tr class="lighttable">
					<td colspan="8"><div class="nodata msg"> <span> No Data Available</span> </div> </td>
				</tr>
				<% } else { %>

				<tr class="table_result">
					
					<td style="text-align: right; padding-right: 55px;"><b>Total</b> </td>
					<td style="text-align: center;padding-right:7px"><b><%=totalEmps%></b> </td>
					<td style="text-align: center; padding-right:7px">&nbsp;</td>
				</tr>

               <%} %>
				
			</tbody>
		</table>

	</div>
	
</div>

<div id="addNewPerformanceDiv"></div>