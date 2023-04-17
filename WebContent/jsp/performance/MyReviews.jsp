<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


  
<%
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>


<script type="text/javascript" charset="utf-8">
	
function openAppraisalPreview(id) {
	//alert("openQuestionBank id "+ id)
	 var dialogEdit = '#appraisalPreviewDiv';
			$(dialogEdit).dialog(
				{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 800,
				width : 1100,
				modal : true,
				title : 'Review Preview',
				open : function() {
					var xhr = $.ajax({
						url : "AppraisalPreview.action?id="+id,
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
	<jsp:param value="Reviews" name="title" />
</jsp:include>


<div class="leftbox reportWidth">


<%
					UtilityFunctions uF = new UtilityFunctions();
					List<List<String>> allAppraisalreport = (List<List<String>>) request.getAttribute("allAppraisalreport");
					
					int totalAppraisal1 = 0;
					int totalEmps1 = 0;
					int totalAprPending1 = 0;
					int totalAprUnderReview1 = 0;
					int totalAprFinalised1 = 0;
					
					for (int i = 0; i < allAppraisalreport.size(); i++) {
						List<String> alinner = (List<String>) allAppraisalreport.get(i);

						totalAppraisal1 = allAppraisalreport.size();
						totalEmps1 += uF.parseToInt(alinner.get(7));
						
						totalAprPending1 += uF.parseToInt(alinner.get(8));
						totalAprUnderReview1 += uF.parseToInt(alinner.get(9));
						//totalAprFinalised1 += uF.parseToInt(alinner.get(10)); 
					}
				%>

<div>
		<div class="filter_div">
			<div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAppraisal1 %></p>
                    <p class="sk_name" style="text-align:center">Reviews</p>
               </div>
               <%-- <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalEmps1 %></p>
                    <p class="sk_name" style="text-align:center">Reviewee</p>
               </div> --%>
               <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAprPending1 %></p>
                    <p class="sk_name" style="text-align:center">Pending</p>
               </div> 
               <div class="skill_div" style="width:14%">
                    <p class="sk_value" style="text-align:center"><%=totalAprUnderReview1 %></p>
                    <p class="sk_name" style="text-align:center">Under Review</p>
               </div>  
               <%-- <div class="skill_div" style="width:12%">
                    <p class="sk_value" style="text-align:center"><%=totalAprFinalised1 %></p>
                    <p class="sk_name" style="text-align:center">Finalised</p>
               </div> --%>
		</div>
	</div>


	<div class="attendance">

		<!-- <table class="display tb_style" >  -->
	<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %> --%>
	<%-- <% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.MANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%> --%>
	<% if(strSessionUserType != null && strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
	<div style="float: right; margin-bottom: 10px">
		<a href="CreateMyReview.action"><input type="button" class="input_button" value="Get Review"> </a>
	</div>
	<%} %>
		<table width="100%" cellspacing="0" cellpadding="2" align="left" class="tb_style">
			<tbody>
				<tr class="darktable">
					<th style="width: 40%;text-align: center;" rowspan="2">Reviews Name</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Deadline</th>
					<!-- <th style="width: 10%;text-align: center;" rowspan="2">Reviewees</th> -->
					<th style="width: 30%;text-align: center;" colspan="2">Reviews Status</th>
					<th style="width: 10%;text-align: center;" rowspan="2">Actions</th>
				</tr>

				<tr class="darktable">
					<th style="width: 10%">Pending</th>
					<th style="width: 10%">Under Review</th>
					<!-- <th style="width: 10%">Finalised</th> -->
				</tr>

				<%
					int totalAppraisal = 0;
					int totalEmps = 0;
					int totalAprPending = 0;
					int totalAprUnderReview = 0;
					int totalAprFinalised = 0;
					
					for (int i = 0; i < allAppraisalreport.size(); i++) {
						List<String> alinner = (List<String>) allAppraisalreport.get(i);
						totalAppraisal = allAppraisalreport.size();
						totalEmps += uF.parseToInt(alinner.get(7));
						totalAprPending += uF.parseToInt(alinner.get(8));

						totalAprUnderReview += uF.parseToInt(alinner.get(9));
						totalAprFinalised += uF.parseToInt(alinner.get(10));
						totalAppraisal += uF.parseToInt(alinner.get(7)); 
				
				%>
				<tr class="lighttable">
					<td>
						<b><%=alinner.get(1)%></b>
						<p style="padding-left:20px"><%=alinner.get(2)%>, <%=alinner.get(3)%>, <%=alinner.get(4)%></p>
					</td>
					
					<td align="center"><%=alinner.get(5)%></td>
					<%-- <td class="blueColor" align="center"><strong> <%=alinner.get(7)%></strong></td> --%>

					<td align="center"><%=alinner.get(8)%></td>
					<td align="center"><%=alinner.get(9)%></td>
					<%-- <td align="center"><%=alinner.get(10)%></td> --%> 
					<td><%=alinner.get(11)%></td>

				</tr>
				<%
					}
				%>
					<%
					if (allAppraisalreport.size() == 0) {
					%>

				<tr class="lighttable">

					<td colspan="7"><div class="nodata msg">
							<span> No Data Available</span>
						</div>
					</td>
				</tr>
				<%
					}else{
				%>

				<tr class="table_result">
					
					<td style="text-align: center;" colspan="2"><b>Total</b></td>

					<%-- <td style="text-align: center;padding-right:7px"><b><%=totalEmps%></b></td> --%>
					
					<td style="text-align: center; padding-right:7px"><b><%=totalAprPending%></b></td>
					
					<td style="text-align: center; padding-right:7px"><b><%=totalAprUnderReview%></b></td>
					
					<%-- <td style="text-align: center;padding-right:7px"><b><%=totalAprFinalised%></b></td>  --%>
					
					<td>&nbsp;</td>
					
				</tr>

               <%} %>
				
			</tbody>
		</table>

	</div>

</div>

<div id="appraisalPreviewDiv"></div>