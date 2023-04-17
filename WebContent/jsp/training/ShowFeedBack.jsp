<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

	
<script type="text/javascript" charset="utf-8">
	
	function getFeedBackData(empId,planId,type,empName) {
		var dialogEdit = '#feedBackid';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : true,
			height : 600,
			width : 850,
			modal : true,
			title : 'Feedback of '+empName,
			open : function() {
			var xhr = $.ajax({
				url : "FeedBackDetails.action?empid="+empId+"&planId="+planId+"&type="+type,
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


	<%
		UtilityFunctions uF=new UtilityFunctions();
		Map<String,String> hmLearnerTotal=(Map<String,String>)request.getAttribute("hmLearnerTotal");
		Map<String,String> hmTrainerTotal=(Map<String,String>)request.getAttribute("hmTrainerTotal");
		String planId=(String)request.getAttribute("planId");
	%>

	<div style="padding:10px 5px 10px 8px ;">

		<table class="tb_style" style="width:100%">
			<thead>
			<tr>
				<th style="text-align: left;">Learner Name</th>
				<th style="text-align: left;">Attendance(%)</th>
				<th style="text-align: left;">Completed</th>
				<th style="text-align: left;">Learner's Feedback</th>
				<th style="text-align: left;">Trainer's Feedback</th>
			</tr>
			</thead>
		
			<tbody>
			<% List<List<String>> alLearnersInfoDashboard = (List) request.getAttribute("alLearnersInfoDashboard"); %>

			<%
				for (int i = 0; i < alLearnersInfoDashboard.size(); i++) {
					List<String> alinner = alLearnersInfoDashboard.get(i);
			%>

			<tr>
				<td><%=alinner.get(1)%></td>
				<td ><%=uF.showData(alinner.get(2),"0")%> %</td>
				<td><%=alinner.get(3)%></td>
				<td align="right">
				<%if(hmLearnerTotal.get(alinner.get(0).trim())!=null){ %>
					<a href="javascript:void(0)" onclick="getFeedBackData('<%=alinner.get(0)%>','<%=planId%>','emp','<%=alinner.get(1)%>')"><%=hmLearnerTotal.get(alinner.get(0).trim()) %>%</a>
					<div id="starPrimary<%=alinner.get(0).trim()+""+planId%>"></div> 
					<input type="hidden" id="gradewithrating<%=alinner.get(0).trim()+""+planId%>" value="<%=hmLearnerTotal.get(alinner.get(0).trim()) != null ? uF.parseToDouble(hmLearnerTotal.get(alinner.get(0).trim())) / 20 + "" : "0"%>" name="gradewithrating<%=alinner.get(0).trim()+""+planId%>" />
					<script type="text/javascript">
				        $(function() {
				        	$('#starPrimary<%=alinner.get(0).trim()+""+planId%>').raty({
				        		readOnly: true,
				        		start: <%=hmLearnerTotal.get(alinner.get(0).trim()) != null ? uF.parseToDouble(hmLearnerTotal.get(alinner.get(0).trim())) / 20 + "" : "0"%>,
				        		half: true,
				        		targetType: 'number',
				        		click: function(score, evt) {
				        			$('#gradewithrating<%=alinner.get(0).trim()+""+planId%>').val(score);
				        		}
				        	});
				        });
					</script>			
								
				<% } else { %>
					NA
				<% } %>
				</td>				
				
				<td align="right">
				<%if(hmTrainerTotal.get(alinner.get(0).trim())!=null){ %>
					<a href="javascript:void(0)" onclick="getFeedBackData('<%=alinner.get(0)%>','<%=planId%>','trainer','<%=alinner.get(1)%>')"><%=hmTrainerTotal.get(alinner.get(0).trim()) %>%</a>
					<div id="astarPrimary<%=alinner.get(0).trim()+""+planId%>"></div> 
					<input type="hidden" id="agradewithrating<%=alinner.get(0).trim()+""+planId%>" value="<%=hmTrainerTotal.get(alinner.get(0).trim()) != null ? uF.parseToDouble(hmTrainerTotal.get(alinner.get(0).trim())) / 20 + "" : "0"%>"
							name="agradewithrating<%=alinner.get(0).trim()+""+planId%>" />
					<script type="text/javascript">
				        $(function() {
				        	$('#astarPrimary<%=alinner.get(0).trim()+""+planId%>').raty({
				        		readOnly: true,
				        		start: <%=hmTrainerTotal.get(alinner.get(0).trim()) != null ? uF.parseToDouble(hmTrainerTotal.get(alinner.get(0).trim())) / 20 + "" : "0"%>,
				        		half: true,
				        		targetType: 'number',
				        		click: function(score, evt) {
				        			$('#agradewithrating<%=alinner.get(0).trim()+""+planId%>').val(score);
				        		}
				        	});
			        	});
			        </script>
				<% } else { %>
					NA
				<% } %>
				</td>
			</tr>
			<% } %>

			<% if (alLearnersInfoDashboard.size() == 0) { %>

			<tr>
				<td colspan="5"><div class="nodata msg"><span>No learner added</span></div></td>
			</tr>

			<% } %> 
		</tbody>
	</table>


</div>
<div id="feedBackid"></div>