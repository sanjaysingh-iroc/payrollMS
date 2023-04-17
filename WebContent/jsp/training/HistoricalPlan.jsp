

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

	
	
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
}); 		


function showFeedback(planId) {

	var dialogEdit = '#showFeedbackId';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
			.appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 500,
		width : 700,
		modal : true,
		title : 'Feedback Information',
		open : function() {
			var xhr = $.ajax({
				url : "ShowFeedBack.action?planId=" + planId,
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
	<jsp:param value="Historical Plan" name="title" />
</jsp:include>



<%
List<List<String>> historicalPlanList = (List) request.getAttribute("historicalPlanList");
Map<String,String> hmPlanRating=(Map<String,String>)request.getAttribute("hmPlanRating"); 
UtilityFunctions uF=new UtilityFunctions();
%>
<div  id="printDiv" class="leftbox reportWidth">


<%-- <s:form name="frm_AddTrainer" action="TrainerInfo" theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>

			<s:select theme="simple" name="strLocation" listKey="wLocationId"
				listValue="wLocationName" headerKey="" headerValue="All Locations"
				list="wLocationList" key="0" />
				
				<s:select name="strLevel" list="levelList"
									listKey="levelId" id="strLevel" listValue="levelCodeName"
									headerKey="" headerValue="Select Level" ></s:select>
				

			
			<s:submit cssClass="input_button" value="Search" align="center" />

		</div>
	</s:form> --%>

	<table class="display" id="lt">
		<thead>
			<tr>
							
				<th style="text-align: left;">Training Title</th>
				<th style="text-align: left;">Training Locations</th>
				<th style="text-align: left;">Duration</th>
				<th style="text-align: left;">Date Period</th>
				<th style="text-align: left;">Certificate Name</th>
				<th style="text-align: left;">People Attended</th>
				<th style="text-align: left;">Feedback</th>
			</tr>
		</thead>
		<tbody>
			<%
				
				for (int i = 0; i < historicalPlanList.size(); i++) {
					List<String> alinner = historicalPlanList.get(i);
			%>
			<tr>
				<td><%=alinner.get(1) %></td>
				<td><%=alinner.get(2) %></td>
				<td><%=alinner.get(3) %></td>
				<td><%=alinner.get(4) %></td>
				<td><%=alinner.get(5) %></td>
				<td><%=alinner.get(6) %></td>
				<td align="right">
				<%
					String total=uF.showData(hmPlanRating.get(alinner.get(0).trim()),"0");
				%>
				
				<a href="javascript:void(0)"
								onclick="showFeedback('<%=alinner.get(0)%>')"><%=total %>%</a>
				
				<div id="starPrimary<%=alinner.get(i).trim()%>"></div> <input
							type="hidden" id="gradewithrating<%=alinner.get(i).trim()%>"
							value="<%=total != null ? uF.parseToDouble(total) / 20 + "" : "0"%>"
							name="gradewithrating<%=alinner.get(i).trim()%>" /> <script
								type="text/javascript">
											        $(function() {
											        	$('#starPrimary<%=alinner.get(i).trim()%>').raty({
											        		readOnly: true,
											        		start: <%=total != null ? uF.parseToDouble(total) / 20 + "" : "0"%>,
											        		half: true,
											        		targetType: 'number',
											        		click: function(score, evt) {
											        			$('#gradewithrating<%=alinner.get(i).trim()%>').val(score);
											        			}
											        	});
											        	});
											        </script>
				
				</td>
			</tr>
			<%}
			if (historicalPlanList.size() == 0) {
			%>

			<tr>
				<td colspan="7"><div class="nodata msg">
						<span>No Historical Plans</span>
					</div></td>
			</tr>
			<%}%>
			
		</tbody>
	</table>

</div>
<div id="showFeedbackId"></div>
