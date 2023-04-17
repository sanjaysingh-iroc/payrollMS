

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

	

			<%
			UtilityFunctions uF=new UtilityFunctions();
			List<List<String>> alTrainingPlan = (List) request.getAttribute("alTrainingPlan");
			Map<String,String> hmPlanRating=(Map<String,String>)request.getAttribute("hmPlanRating");
			if(hmPlanRating == null) hmPlanRating = new HashMap<String,String>();
			
			%>

<div style="padding:10px 5px 10px 8px ;">

		<table class="table table-bordered" style="width:100%">
		<thead>
			<tr>
				<th style="text-align: left;">Learning Title</th>
				<!-- <th style="text-align: left;">Learning Location</th> -->
				<th style="text-align: left;">Organisation</th>
				<th style="text-align: left;">Duration</th>
				<th style="text-align: left;">Start Date</th>
				<th style="text-align: left;">End Date</th>
				<th style="text-align: left;">Feedback</th>
			</tr>
		</thead>
		<tbody>
			
			<%
				for (int i = 0; i < alTrainingPlan.size(); i++) {
					List<String> alinner = alTrainingPlan.get(i);
			%>

			<tr>
				<td><%=alinner.get(1)%></td>
				<%-- <td><%=alinner.get(2)%></td> --%>
				<td><%=alinner.get(7)%></td>
				<td style="text-align: center;"><%=alinner.get(3)%></td>
				<td><%=alinner.get(4)%></td>
				<td><%=alinner.get(5)%></td>
				<td  align="right">
				<% 
				double dblTotal = uF.parseToDouble(hmPlanRating.get(alinner.get(0).trim()));
				int nTotal = (int) dblTotal;
				//String total=hmPlanRating.get(alinner.get(0).trim()) != null ? uF.parseToInt(hmPlanRating.get(alinner.get(0).trim()))  + "" : "0";
				String total=""+nTotal;
				%>
				<%=total%>%
				<div id="starPrimary<%=alinner.get(0).trim()+""+i%>"></div> <input
							type="hidden" id="gradewithrating<%=alinner.get(0).trim()+""+i%>"
							value="<%=total%>"
							name="gradewithrating<%=alinner.get(0).trim()+""+i%>" /> <script
								type="text/javascript">
											        $(function() {
											        	$('#starPrimary<%=alinner.get(0).trim()+""+i%>').raty({
											        		readOnly: true,
											        		start: <%=total %>,
											        		half: true,
											        		targetType: 'number',
											        		click: function(score, evt) {
											        			$('#gradewithrating<%=alinner.get(0).trim()+""+i%>').val(score);
											        			}
											        	});
											        	});
											        </script>	
				
				</td>
				

			</tr>
			<%
				}
			%>

			<%
				if (alTrainingPlan.size() == 0) {
			%>
     
			<tr>
				<td colspan="9"><div class="nodata msg">
						<span>No Plan Added yet</span>
					</div></td>
			</tr>

			<%
				}
			%>
		</tbody>
	</table>


</div>
<div id="feedBackid"></div>