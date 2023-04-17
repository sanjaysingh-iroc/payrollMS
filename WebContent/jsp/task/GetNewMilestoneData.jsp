	
	<%@page import="com.konnect.jpms.util.UtilityFunctions" %>
	<%@page import="java.util.List"%>
	
	<% 
		UtilityFunctions uF = new UtilityFunctions();
		List<String> alMilestoneData = (List<String>)request.getAttribute("alMilestoneData");
		String proId = (String)request.getAttribute("proId");
		String proFreqId = (String)request.getAttribute("proFreqId");
		String mileCnt = (String)request.getAttribute("mileCnt");
		String srNoCnt = (String)request.getAttribute("srNoCnt");
		String partiCnt = (String)request.getAttribute("partiCnt");
		String type = (String)request.getAttribute("type");
	%>
	
	<% if(alMilestoneData != null && !alMilestoneData.isEmpty()) { %>
	
	<% if(type != null && type.equals("INR")) { %>
		<% int srMileCnt = uF.parseToInt(mileCnt) + 1; %>
		<div id="milestoneDiv_<%=srMileCnt %>" style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">	
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 54%;">
				<div style="float: left;">
					<span style="float: left;">
					<input type="hidden" name="strMileParticularsId<%=partiCnt %>" id="strMileParticularsId<%=partiCnt %>" value="<%=srMileCnt %>" />
					<input type="hidden" name="strMilestoneId<%=partiCnt %>_<%=srMileCnt %>" id="strMilestoneId<%=partiCnt %>_<%=srMileCnt %>" value="<%=alMilestoneData.get(0) %>" />
					<%=srNoCnt %>.<%=srMileCnt+1 %>. <input type="text" style="width: 220px !important;" id="strMileParticulars" value="<%=alMilestoneData.get(1)+", " + alMilestoneData.get(2) %>" name="strMileParticulars<%=partiCnt %>_<%=srMileCnt %>" class="validateRequired"></span>
					<span id="addNewMileSpan_<%=srMileCnt %>" style="float: left; height: 15px;">
						<a title="Add" class="fa fa-plus" onclick="addMilestoneParticular('<%=proId %>', '<%=proFreqId %>', '<%=alMilestoneData.get(0) %>', '<%=srMileCnt %>', '<%=srNoCnt %>', '<%=partiCnt %>')" href="javascript:void(0)">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeMileParticular('milestoneDiv_<%=srMileCnt %>', '<%=srMileCnt %>', 'INR')" class="fa fa-remove">&nbsp;</a>
					</span>
				</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">
					<span style="float: left;"> 
					<% if(uF.parseToInt(alMilestoneData.get(4)) > 0) { %>
						<%=alMilestoneData.get(5) %>
					<% } else { %>
						<%=alMilestoneData.get(3) %> %
					<% } %>
					</span>
				</div>
			</div>
			<div style="float: left; width: 20%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strMileParticularsAmt<%=partiCnt %>_<%=srMileCnt %>" id="strMileParticularsAmt<%=srMileCnt %>" style="width: 65px !important; text-align: right;" onkeyup="calAmt();" onkeypress="return isNumberKey(event)" class="validateRequired" value="<%=alMilestoneData.get(6) %>"/>
				</div>
			</div>
			
		</div>
	<% } else { %>
	
		<% 
		String srNoCntOtherCurr = (String)request.getAttribute("srNoCnt");
		int srMileCntOtherCurr = uF.parseToInt(mileCnt) + 1; %>
		<div id="milestoneDivOtherCurr_<%=srMileCntOtherCurr %>" style="float: left; width: 100%; color: #404040; background-color: #EFEFEF; padding: 4px 0px;">
			<div style="float: left; width: 6%;">
				<div style="float: left; margin-left: 10px;">&nbsp;</div>
			</div>
			<div style="float: left; width: 46%;">
				<div style="float: left;">
					<span style="float: left;"><%=srNoCntOtherCurr %>.<%=srMileCntOtherCurr+1 %>. 
						<input type="hidden" name="strMileParticularsIdOtherCurr<%=partiCnt %>" id="strMileParticularsIdOtherCurr<%=partiCnt %>" value="<%=srMileCntOtherCurr %>" />
						<input type="hidden" name="strMilestoneIdOtherCurr<%=partiCnt %>_<%=srMileCntOtherCurr %>" id="strMilestoneIdOtherCurr<%=partiCnt %>_<%=srMileCntOtherCurr %>" value="<%=alMilestoneData.get(0) %>" />
						<input type="text" style="width: 220px !important;" id="strMileParticularsINRCurr" value="<%=alMilestoneData.get(1)+", " + alMilestoneData.get(2) %>" name="strMileParticularsINRCurr<%=partiCnt %>_<%=srMileCntOtherCurr %>" class="validateRequired"></span>
					<span id="addNewMileSpanOtherCurr_<%=srMileCntOtherCurr %>" style="float: left; height: 15px;">
						<a title="Add" class="fa fa-plus" onclick="addMilestoneParticularOtherCurr('<%=proId %>', '<%=proFreqId %>', '<%=alMilestoneData.get(0) %>', '<%=srMileCntOtherCurr %>', '<%=srNoCnt %>', '<%=partiCnt %>')" href="javascript:void(0)">&nbsp;</a>
						<a href="javascript:void(0)" onclick="removeMileParticular('milestoneDivOtherCurr_<%=srMileCntOtherCurr %>', '<%=srMileCntOtherCurr %>', 'Other')" class="fa fa-remove">&nbsp;</a>
					</span>
				</div>
			</div>
			<div style="float: left; width: 12%;">
				<div style="float: left; margin-left: 10px;">
					<span style="float: left;"> 
					<% if(uF.parseToInt(alMilestoneData.get(4)) > 0) { %>
						<%=alMilestoneData.get(5) %>
					<% } else { %>
						<%=alMilestoneData.get(3) %> %
					<% } %>
					</span>
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strMileParticularsAmtINRCurr<%=partiCnt %>_<%=srMileCntOtherCurr %>" id="strMileParticularsAmtINRCurr<%=srMileCntOtherCurr %>" style="width: 65px !important; text-align: right;" onkeyup="calAmtOtherCurr();" onkeypress="return isNumberKey(event)" class="validateRequired" 
						value="<%=alMilestoneData.get(6) %>"/>
				</div>
			</div>
			<div style="float: left; width: 18%;">
				<div style="float: right; margin-right: 10px;">
					<input type="text" name="strMileParticularsAmtOtherCurr<%=partiCnt %>_<%=srMileCntOtherCurr %>" id="strMileParticularsAmtOtherCurr<%=srMileCntOtherCurr %>" style="width: 65px !important; text-align:right;" readonly="readonly"/>
				</div>
			</div>
		</div>
	<% } %>
		
<% } %>	
	