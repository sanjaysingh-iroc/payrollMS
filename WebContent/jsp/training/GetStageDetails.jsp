<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script>
		
</script>
	
	<% 
	List<String> allList = new ArrayList<String>();
	String stageType = (String)request.getAttribute("stageType");
	if(stageType != null && stageType.equals("C")) {
		allList = (List<String>)request.getAttribute("courseList");
	} else if(stageType != null && stageType.equals("A")) {
		allList = (List<String>)request.getAttribute("assessmentList");
	//===start parvez date: 22-09-2021===
	}
	//===end parvez date: 22-09-2021===
	
	String count = (String)request.getAttribute("count");
	
	//===start parvez date: 22-09-2021===
	//if(allList != null && !allList.isEmpty() && stageType != null && !stageType.equals("T")) {
	if(allList != null && !allList.isEmpty() && stageType != null && !stageType.equals("T") && !stageType.equals("V")) {
	//===end parvez date: 22-09-2021===	
	
	%>
		<table border="0" class="table table-bordered">
			<tr>
				<td class="txtlabel" align="right">Learning Stage Type:</td>
				<td colspan="3">
					<input type="hidden" name="lstagetypeid" value="<%=allList.get(0) %>" />
					<input type="hidden" name="lstagename" value="<%=allList.get(1) %>" />
					<input type="hidden" name="lstagetype" value="<%=allList.get(2) %>" />
					<%=allList.get(2) %>
					<span style="float: right;">
						<%if(allList.get(2) != null && allList.get(2).equals("Course")) { %>
							<a href="javascript:void(0)" onclick="viewCourseDetail('<%=allList.get(0) %>','<%=allList.get(1) %>')">Preview</a> 
						<% } else if(allList.get(2) != null && allList.get(2).equals("Assessment")) { %>
							<a href="javascript:void(0)" onclick="viewAssessmentDetail('<%=allList.get(0) %>','<%=allList.get(1) %>')">Preview</a>
						<% } %>
					</span>
				</td>
			</tr>
			<tr>
				<td class="txtlabel" align="right">Learning Stage Name:</td>
				<td colspan="3"><%=allList.get(1) %></td>
			</tr>
			<tr>
				<td class="txtlabel" align="right">Start Date:<sup>*</sup></td>
				<td><input type="text" name="startdate" id="startdate<%=count %>" class="validateRequired text-input" style="width: 100px;" required/></td>
				<td class="txtlabel" align="right">End Date:<sup>*</sup></td>
				<td><input type="text" name="enddate" id="enddate<%=count %>" class="validateRequired text-input" style="width: 100px;" required/></td>
			</tr>
			<tr>
				<td class="txtlabel" align="right">
				<input type="checkbox" name="everyday" id="everyday<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="checkAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="everyday">Everyday &nbsp;
				<td class="txtlabel" colspan="3">
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Mon">Mon
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Tue">Tue
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Wed">Wed
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Thu">Thu
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Fri">Fri
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Sat">Sat
					<input type="checkbox" name="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" id="weekdays<%=allList.get(2) %>_<%=allList.get(0) %>" onclick="uncheckAllDays('<%=allList.get(2) %>_<%=allList.get(0) %>');" value="Sun">Sun
				</td> 
			</tr>
			<tr>
				<td class="txtlabel" align="right">Start Time:<sup>*</sup></td>
				<td><input type="text" name="starttime" id="starttime<%=count %>" class="validateRequired text-input" style="width: 100px;" required/></td>
				<td class="txtlabel" align="right">End Time:<sup>*</sup></td>
				<td><input type="text" name="endtime" id="endtime<%=count %>" class="validateRequired text-input" style="width: 100px;" required/></td>
			</tr>
		</table>
	
	<% } %>
	
	<%if(stageType != null && stageType.equals("T")) { %>
		<div>
			<%
				List<String> trainingList = (List<String>) request.getAttribute("trainingList");
				if(trainingList == null) trainingList=new ArrayList<String>();
				List<String> trainingDataList = (List<String>) request.getAttribute("trainingDataList");
				if(trainingDataList == null) trainingDataList=new ArrayList<String>();
				String scheduleType = (String) request.getAttribute("scheduleTypeValue");
				String trainingSchedulePeriod = (String) request.getAttribute("trainingSchedulePeriod");
				String planId = (String) request.getAttribute("planId");
				List<List<String>> alSessionData = (List<List<String>>) request.getAttribute("alSessionData");
				if(alSessionData == null) alSessionData=new ArrayList<List<String>>();
			%>
			<% if(trainingList != null && trainingList.size() > 0 && !trainingList.isEmpty()) { %>
					<table border="0" class="table table-bordered" id="trainingScheduleTableId">
						<tr>
							<td class="txtlabel" align="right">Learning Stage Type:</td>
							<td colspan="3">
								<input type="hidden" name="lstagetypeid" value="<%=trainingList.get(0) %>" />
								<input type="hidden" name="lstagename" value="<%=trainingList.get(1) %>" />
								<input type="hidden" name="lstagetype" value="<%=trainingList.get(2) %>" />
								<%=trainingList.get(2) != null && trainingList.get(2).equals("Training") ? "Classroom Training" : "" %>
								<span style="float: right;"> 
									<a href="javascript:void(0)" onclick="editTraining('AddTrainingPlan.action?operation=E&step=2&frmpage=LPlan&ID=<%=trainingList.get(0) %>&lPlanId=<%=planId %>')">Edit Classroom Training</a> 
								</span>
							</td>
						</tr>
						<tr>
							<td class="txtlabel" align="right">Learning Stage Name:</td>
							<td colspan="3"> <%=trainingList.get(1) %> </td>
						</tr>
						<tr>
                            <td class="txtlabel" style="vertical-align: top; text-align: right">Periodic:</td>
                            <td>
	                            <div style="position:reletive;">
		                           <span style="float: left; margin-right: 20px">
		                           <%if(trainingSchedulePeriod != null && trainingSchedulePeriod.equals("1")) { %>
		                           		One Time
		                           <% } else  if(trainingSchedulePeriod != null && trainingSchedulePeriod.equals("2")) { %>
		                           		Weekly
		                           <% } else  if(trainingSchedulePeriod != null && trainingSchedulePeriod.equals("3")) { %>
		                           		Monthly
		                           <% } %>
		                           </span>                           
	                            </div>
                            </td>
                            <td colspan="2">
	                          	  <div style="position:reletive;">
	                           		 <span id="weekly" style="<%if(trainingSchedulePeriod != null && trainingSchedulePeriod.equals("2")) { %>display: block; <% } else { %> display: none; <% } %> float: left;">Day: <%=(String)request.getAttribute("weekdayValue") %></span>
	                         		 <span id="monthly" style="<%if(trainingSchedulePeriod != null && trainingSchedulePeriod.equals("3")) { %>display: block; <% } else { %> display: none; <% } %> float: left;"> <%=(String)request.getAttribute("dayValue") %></span>
	                              </div>
                            </td>
                        </tr> 
                        
						<%if(trainingDataList != null && !trainingDataList.isEmpty() && trainingDataList.size()>0) {%>
							<tr>
								<td class="txtlabel" style="text-align: right">Start Date:</td>
								<td><input type="hidden" name="startdate" id="startdate<%=count %>" value="<%=trainingDataList.get(3) %>" /><%=trainingDataList.get(1) %> </td>
								<td class="txtlabel" style="text-align: right; width: 100px;">End Date:</td>
								<td> <input type="hidden" name="enddate" id="enddate<%=count %>" value="<%=trainingDataList.get(4) %>" /><%=trainingDataList.get(2) %></td>
							</tr>
						<% } %>
						
						<tr>
							<td style="text-align: right;" class="txtlabel">Day Schedule</td>
							<td id="dayScheduleTD" style="display: table-cell;" colspan="3">
								<% if(scheduleType != null && scheduleType.equals("1")) { %>
									Daily
								<% } else if(scheduleType != null && scheduleType.equals("2")) { %>
									Occasionally
								<% } %>
							</td>
						</tr>
						
						<%
							if (alSessionData != null && alSessionData.size() != 0) {
								Map<String, String> hmWeekdays = (Map<String, String>) request.getAttribute("hmWeekdays");	
								if(hmWeekdays == null) hmWeekdays = new HashMap<String,String>();
						%>
								<tr id="weekDaysTR" <%if (scheduleType != null && scheduleType.equals("1") && trainingSchedulePeriod != null && trainingSchedulePeriod.equals("1")) { %>
									style="display: table-row;" <%} else { %> style="display: none;" <%}%>>
									<td class="txtlabel" align="right" colspan="4">
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Mon" <%=hmWeekdays.get(trainingList.get(0)+"_MON") != null ? hmWeekdays.get(trainingList.get(0)+"_MON") : "" %> disabled="disabled">Mon
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Tue" <%=hmWeekdays.get(trainingList.get(0)+"_TUE") != null ? hmWeekdays.get(trainingList.get(0)+"_TUE") : "" %> disabled="disabled">Tue
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Wed" <%=hmWeekdays.get(trainingList.get(0)+"_WED") != null ? hmWeekdays.get(trainingList.get(0)+"_WED") : "" %> disabled="disabled">Wed
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Thu" <%=hmWeekdays.get(trainingList.get(0)+"_THU") != null ? hmWeekdays.get(trainingList.get(0)+"_THU") : "" %> disabled="disabled">Thu
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Fri" <%=hmWeekdays.get(trainingList.get(0)+"_FRI") != null ? hmWeekdays.get(trainingList.get(0)+"_FRI") : "" %> disabled="disabled">Fri
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Sat" <%=hmWeekdays.get(trainingList.get(0)+"_SAT") != null ? hmWeekdays.get(trainingList.get(0)+"_SAT") : "" %> disabled="disabled">Sat
										<input type="checkbox" name="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" id="weekdays<%=trainingList.get(2) %>_<%=trainingList.get(0) %>" value="Sun" <%=hmWeekdays.get(trainingList.get(0)+"_SUN") != null ? hmWeekdays.get(trainingList.get(0)+"_SUN") : "" %> disabled="disabled">Sun
									</td> 
								</tr>
						
				<%				for (int i = 0; i < alSessionData.size(); i++) {
									List<String> alInner = (List<String>) alSessionData.get(i);
									if(alInner == null ) alInner = new ArrayList<String>();	
				%>	
										<tr id="trainingSchedulePeriodTR<%=i%>"
											<%if (alInner.get(0) != null && alInner.get(0).equals("1") && alInner.get(4) != null && alInner.get(4).equals("2")) {%>
												style="display: table-row;" <%} else { %> style="display: none;"
											<%}%>>
											<td class="txtlabel" style="text-align: right">
												<input type="hidden" name="hideScheduleType" id="hideScheduleType" value="<%=alInner.get(4) %>"/>
												<span style="float: left;margin-left: 50px;">Day <%=i+1 %></span> Select Date:<sup>*</sup>
											</td>
											<td><input type="text" id="oneTimeDate<%=i%>" name="oneTimeDate" value="<%=alInner.get(1)%>" required/></td>
										</tr>

										<tr id="startTimeTR<%=i%>"> 
										  <%if (alInner != null && !alInner.isEmpty() && alInner.size() > 0) { %>
												<td class="txtlabel" style="text-align: right">Start Time:</td>
												<td> <input type="hidden" name="starttime" id="starttime<%=count %>" value="<%=alInner.get(2)%>" /><%=alInner.get(2)%></td>
												<td class="txtlabel" style="text-align: right; width: 100px;">End Time:</td>
												<td><input type="hidden" name="endtime" id="endtime<%=count %>" value="<%=alInner.get(3)%>" /><%=alInner.get(3)%></td> 
										 <% } %>
										</tr>
									<%} 
								} %>
						</table>
					<% } %>
			</div>
	<% } %>
	
	<!-- ===start parvez date: 22-09-2021=== -->
	<% 
	if(stageType != null && stageType.equals("V")) {
	%>
		<div>
			<%
			List<String> videoList = (List<String>)request.getAttribute("videoList");
			if(videoList == null) videoList=new ArrayList<String>();
			
			if(videoList != null && videoList.size() > 0 && !videoList.isEmpty()) {
			%>
				<table border="0" class="table table-bordered">
					<tr>
						<td class="txtlabel" align="right">Learning Stage Type:</td>
						<td colspan="3">
							<input type="hidden" name="lstagetypeid" value="<%=videoList.get(0) %>" />
							<input type="hidden" name="lstagename" value="<%=videoList.get(1) %>" />
							<input type="hidden" name="lstagetype" value="<%=videoList.get(2) %>" />
							<%=videoList.get(2) %>
							<span style="float: right;">
								<a href="javascript:void(0)" onclick="viewVideoDetail('<%=videoList.get(0) %>','<%=videoList.get(1) %>')">Preview</a>
							</span>
						</td>
					</tr>
					<tr>
						<td class="txtlabel" align="right">Learning Stage Name:</td>
						<td colspan="3"><%=videoList.get(1) %></td>
					</tr>
					
					<tr>
						<td class="txtlabel" align="right">Start Date:<sup>*</sup></td>
						<td><input type="text" name="startdate" id="startdate<%=count %>" class="validateRequired text-input" style="width: 100px !important;" required/></td>
						<td class="txtlabel" align="right">End Date:<sup>*</sup></td>
						<td><input type="text" name="enddate" id="enddate<%=count %>" class="validateRequired text-input" style="width: 100px !important;" required/></td>
					</tr>
					<tr>
						<td class="txtlabel" align="right">
						<input type="checkbox" name="everyday" id="everyday<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="checkAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="everyday">Everyday &nbsp;
						<td class="txtlabel" colspan="3">
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Mon">Mon
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Tue">Tue
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Wed">Wed
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Thu">Thu
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Fri">Fri
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Sat">Sat
							<input type="checkbox" name="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" id="weekdays<%=videoList.get(2) %>_<%=videoList.get(0) %>" onclick="uncheckAllDays('<%=videoList.get(2) %>_<%=videoList.get(0) %>');" value="Sun">Sun
						</td> 
					</tr>
					<tr>
						<td class="txtlabel" align="right">Start Time:<sup>*</sup></td>
						<td><input type="text" name="starttime" id="starttime<%=count %>" class="validateRequired text-input" style="width: 100px !important;" required/></td>
						<td class="txtlabel" align="right">End Time:<sup>*</sup></td>
						<td><input type="text" name="endtime" id="endtime<%=count %>" class="validateRequired text-input" style="width: 100px !important;" required/></td>
					</tr>
					
				</table>
			
			<%} %>
		</div>
		
	<% } %>
	<!-- ===end parvez date: 22-09-2021=== -->