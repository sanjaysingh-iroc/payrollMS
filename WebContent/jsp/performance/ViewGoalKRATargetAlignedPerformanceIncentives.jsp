<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	UtilityFunctions uF = new UtilityFunctions();
	//System.out.println("hmOrientTypeAndUsers  " + hmOrientTypeAndUsers);
	List<Map<String, String>> alCondition = (List<Map<String, String>>)request.getAttribute("alCondition");
	if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
	
	Map<String, List<Map<String, String>>> hmCondiLogic = (Map<String, List<Map<String, String>>>)request.getAttribute("hmCondiLogic");
	if(hmCondiLogic == null) hmCondiLogic = new HashMap<String, List<Map<String, String>>>();
	
	//System.out.println("alCondition ===>> " + alCondition);
	//System.out.println("hmCondiLogic ===>> " + hmCondiLogic);
%>
	<div style="float:left; width: 98%; margin:0px 0px 10px 0px;">
			<ul class="level_list">
				<%
					int cnt1 = 0;
					for(int i = 0; i < alCondition.size(); i++) {
						cnt1++;
						Map<String,String> hmCondition = (Map<String,String>) alCondition.get(i);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
				%>
					<li>
						Condition Name/ Slab: <strong><%=hmCondition.get("ALLOWANCE_CONDITION_SLAB") %></strong>,&nbsp;
						Condition Type: <strong><%=hmCondition.get("ALLOWANCE_CONDITION") %></strong>,&nbsp;
						<%
						if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) {
							String StrType= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "Percentage";
							String StrTypeStatus= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "%";
						%>
							Pay Type: <strong><%=StrType %></strong>&nbsp;
						<% } else { %>
						<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID) { %>Achieved % <% } %>
							Min: <strong><%=hmCondition.get("MIN_CONDITION") %></strong>&nbsp;-&nbsp;
							<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID) { %>Achieved % <% } %>
							Max: <strong><%=hmCondition.get("MAX_CONDITION") %></strong>
						<%} %>
						<%-- <p style="font-size: 10px; padding-left: 66px; font-style: italic;">Last updated by <%=hmCondition.get("ADDED_BY") %> on <%=hmCondition.get("ENTRY_DATE") %></p> --%>
					</li>
					<li>
						<ul class="level_list">
							<%
								List<Map<String, String>> alLogic = hmCondiLogic.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
								if(alLogic == null) alLogic = new ArrayList<Map<String,String>>();
								System.out.println("alLogic ===>> " + alLogic);
								int cnt = 0;
								for(int j = 0; j < alLogic.size(); j++) {
									cnt++;
									Map<String,String> hmLogic = (Map<String,String>) alLogic.get(j);
									if(hmLogic == null) hmLogic = new HashMap<String, String>();
							%>
									<li>
										Payment Logic Name: <strong><%=hmLogic.get("PAYMENT_LOGIC_SLAB") %></strong>,&nbsp;
										Condition Name/ Slab: <strong><%=hmLogic.get("ALLOWANCE_CONDITION") %></strong>,&nbsp;
										Payment Logic: <strong><%=hmLogic.get("ALLOWANCE_PAYMENT_LOGIC") %></strong>
										<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_ACHIEVED_ID) { %>
											,&nbsp;Fixed Amount: <strong><%=hmLogic.get("FIXED_AMOUNT") %></strong>
										<%} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) { %>
											,&nbsp;Salary Head: <strong><%=hmLogic.get("CAL_SALARY_HEAD_NAME") %></strong>
										<%} %>
										<%-- <p style="font-size: 10px; padding-left: 66px; font-style: italic;">Last updated by <%=hmLogic.get("ADDED_BY") %> on <%=hmLogic.get("ENTRY_DATE") %></p> --%>
									</li>
							<%	} if(cnt == 0) { %>
									<li><div class="filter"><div class="msg nodata"><span>No allowance payment logic has been set.</span></div></div></li>
							<%	} %>
						</ul>
					</li>
				<%	} if(cnt1 == 0) { %>
						<li><div class="filter"><div class="msg nodata"><span>No allowance condition has been set.</span></div></div></li>
				<%	} %>
			</ul>
		</div>




