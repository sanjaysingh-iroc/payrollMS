
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
border: 1px solid #DBDBDB !important;
}
th{
border-top-color: #FFF;
}
blockquote {
font-size: 14px;
}

/* ===start parvez date: 23-03-2022=== */
	#textlabel{
		white-space:pre-line;
	}
/* ===end parvez date: 23-03-2022=== */
</style>

<script type="text/javascript">
	$(function() {
		var a = '#from';
		$("#from").datepicker({
			format : 'dd/mm/yyyy'
		});
		$("#to").datepicker({
			format : 'dd/mm/yyyy'
		}); 
	});

	jQuery(document).ready(function() {
		
		//	jQuery("#frmClockEntries").validationEngine();
		  jQuery(".content1").hide();
		  //toggle the componenet with class msg_body
		  jQuery(".heading_dash").click(function()
		  {
		    jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("filter_close"); 
		  });
		});
</script>
<%
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
	//System.out.println("hmorientationMembers="+hmorientationMembers);
	Map hmScoreAggregateMap = (Map) request.getAttribute("hmScoreAggregateMap");
	
	Map<String, Map<String,String>> hmMeasureUserScore =(Map<String, Map<String,String>>)request.getAttribute("hmMeasureUserScore");
	Map<String, Map<String,String>> hmScoreMarksType1 =(Map<String, Map<String,String>>)request.getAttribute("hmScoreMarksType1");
	Map<String, Map<String,String>> hmObjectiveMarks =(Map<String, Map<String,String>>)request.getAttribute("hmObjectiveMarks");
	Map<String, Map<String,String>> hmGoalType1Marks =(Map<String, Map<String,String>>)request.getAttribute("hmGoalType1Marks");
	Map<String, Map<String,String>> hmGoalType2Marks =(Map<String, Map<String,String>>)request.getAttribute("hmGoalType2Marks");
	 
	Map hmAggregateScoreDetailsMap = (Map) request.getAttribute("hmAggregateScoreDetailsMap");		//added by parvez date: 13-03-2023===
		if(hmAggregateScoreDetailsMap==null) hmAggregateScoreDetailsMap= new HashMap();
	//System.out.println("hmScoreDetailsMap ===>> " + hmScoreDetailsMap);
//	System.out.println("hmScoreMarksType1 ===>> " + hmScoreMarksType1);
%>

	<%-- <jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Review Summary" name="title" />
	</jsp:include> --%>
<s:form action="#" id="formID" method="POST" theme="simple">
	<section class="content">
    	<div class="row jscroll">
        	<section class="col-lg-12 connectedSortable">
	        	<div class="box box-primary">
	                <div class="box-header with-border">
	                    <h3 class="box-title"><%=appraisalList.get(1)%></h3>
	                </div>
	                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                    <table class="table" width="98%">
						<tr>
							<th width="15%" align="right">
							Appraisal Type</th>
							<td><%=appraisalList.get(14)%></td>
						</tr>
						<tr>
							<th valign="top" align="right">Description</th>
						<!-- ===start parvez date: 23-03-2022=== -->	
							<td colspan="1" id="textlabel"><%=appraisalList.get(15)%></td>
						<!-- ===end parvez date: 23-03-2022=== -->	
						</tr>
						<tr>
							<th valign="top" align="right">Instruction</th>
						<!-- ===start parvez date: 23-03-2022=== -->
							<td colspan="1" id="textlabel"><%=appraisalList.get(16)%></td>
						<!-- ===end parvez date: 23-03-2022=== -->
						</tr>
						<tr>	
							<th align="right">Frequency</th>
							<td><%=appraisalList.get(7)%></td>
						</tr>
						<tr>	
							<th align="right">Effective Date</th>
							<td><%=appraisalList.get(17)%></td>
						</tr>
						<tr>	
							<th align="right">Due Date</th>
							<td><%=appraisalList.get(18)%></td>
						</tr>
						<tr>
							<th valign="top" align="right">Appraisee</th>
							<td colspan="1"><%=appraisalList.get(12)%></td>
						</tr>
						<tr>
							<th align="right">Orientation</th>
							<td colspan="1"><%=appraisalList.get(2)%></td>
						</tr>
					</table>
	                </div>
	                <!-- /.box-body -->
	            </div>
        	
        	<%
			List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");
			Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");
			Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
			//System.out.println("ApSS.jsp/115--levelMp ===> " + levelMp);
			UtilityFunctions uF = new UtilityFunctions();
		
		//===start parvez date: 15-12-2021===
			CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
			boolean isSelfRating = CF.getFeatureManagementStatus(request, uF, IConstants.F_DISABLE_SELF_APPRAISAL_RATING_DURING_FINAL_RATING_CALCULATION);
		//===end parvez date: 15-12-2021===
		
		//===start parvez date: 22-03-2022===
			Map<String,String> hmPriorUser = (Map<String,String>) request.getAttribute("hmPriorUser");
			if(hmPriorUser == null) hmPriorUser = new HashMap<String, String>();
			
			Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			List<String> alFeatureUserTypeId = hmFeatureUserTypeId.get(IConstants.F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE);
			
			boolean isUserTypeRating = uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_ENABLE_BALANCE_SCORECARD_CALCULATION_USERTYPE));
		//===end parvez date: 22-03-2022===	
			
			%>
			<h4>Sections (<%=mainLevelList != null ? mainLevelList.size() : "0" %>)</h4>
			<%		
				for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
					List<String> maininnerList = mainLevelList.get(a);
						
			%>
        	<div class="box box-primary collapsed-box">
                
                <div class="box-header with-border">
                    <h3 class="box-title"><%=a+1 %>)&nbsp;<%=maininnerList.get(1)%> 
						<div style="font-weight: normal; font-size: 13px;padding-top: 5px; ">
						<%=uF.showData(maininnerList.get(2), "")%>
						</div>
					</h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 400px;">
                    <div style="float: left; width: 100%;  text-align: left; margin-bottom:10px;">
			

		<div class="">
			<%
				List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
				for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
					List<String> innerList1 = outerList1.get(i);
					//System.out.println("innerList1.get(3) ===>> " + innerList1.get(3));
					if (uF.parseToInt(innerList1.get(3)) == 2) {
						List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
						Map<String, List<List<String>>> scoreMp = list.get(0);
						//System.out.println("scoreMp ===>> " + scoreMp);
			%>

			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote>
					<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")){ %>
					<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
					<%} %>
					<span>Aggregate Score: 
				<%
				Set set3 = hmScoreAggregateMap.keySet();
				Iterator it3 = set3.iterator();
				while(it3.hasNext()){
					String str = (String)it3.next();
					String[] userTypeId = str.split("_");
					String usertypeName = hmorientationMembers.get(userTypeId[0]);
					//System.out.println("ASSum.jsp/173--userTypeId="+userTypeId[0]);
					if(uF.parseToInt(userTypeId[1]) == 1) {
						usertypeName = "Reviewer";
					}
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
			//===start parvez date: 15-12-2021===
					/* if(hmTemp!=null) { */
					if(hmTemp!=null && (uF.parseToInt(userTypeId[0]) != 3 || !isSelfRating)) {
			//===end parvez date: 15-12-2021===

			
			//===start parvez date: 22-03-2022===
						if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(userTypeId[0]) && uF.parseToInt(userTypeId[0]) == uF.parseToInt(hmPriorUser.get(maininnerList.get(0))))){
			//===end parvez date: 22-03-2022===	
				%>
					<%=usertypeName %> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet") %>,				
				<%
				//===start parvez date: 22-03-2022===			
						}
				//===end parvez date: 22-03-2022===
					}
				}
				%>
				</span>
				</blockquote>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 99.8%;">
				
					<table class="table table-bordered" style="width: 100%; float: left;">
						<tr>
							<td width="90%"><b>Question</b></td>
							<td><b>Weightage</b></td>
							<th>Aggregate</th>
							<% 
							Set setms1 = hmScoreDetailsMap.keySet();
							Iterator itms1 = setms1.iterator();
							while(itms1.hasNext()) {
								String str = (String)itms1.next();
								String[] userTypeId = str.split("_");
								if(uF.parseToInt(userTypeId[1]) == 0) {
								%>
									<th><%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></th>
								<% } %>
							<% } %>
							<th>Reviewer</th>
						</tr>
						<%
							List<List<String>> goalList = scoreMp.get(innerList1.get(0));
							//System.out.println("goalList ===>> " + goalList);
							for (int k = 0; goalList != null && k < goalList.size(); k++) {
								List<String> goalinnerList = goalList.get(k);
								//System.out.println("goalinnerList ===>> " + goalinnerList);
						%>
						<tr>
							<td><%=a+1 %>.
							<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")){ %>
							<%=i+1 %>.
							<%} %>
							<%=k+1%>)&nbsp;<%=goalinnerList.get(0)%></td>
							<td style="text-align: right"><%=goalinnerList.get(1)%>%</td>			
							<%
							double avgPercent =0, totPercent = 0;
							int roleCnt=0;
							//===start parvez date: 13-03-2023===
								//Set set21 = hmScoreDetailsMap.keySet();
								Set set21 = hmAggregateScoreDetailsMap.keySet();
								Iterator it21 = set21.iterator();
								String actaulCalBasis = null;	//added by parvez date: 01-03-2023
								while(it21.hasNext()) {
									roleCnt++;
									String str = (String)it21.next();
									//Map hm = (Map)hmScoreDetailsMap.get(str);
									Map hm = (Map)hmAggregateScoreDetailsMap.get(str);
									if(hm==null)hm=new HashMap();
						//===end parvez date: 13-03-2023===			
									//System.out.println("goalinnerList.get(6) ---> "+goalinnerList.get(6));
									//System.out.println("ApSS/228--hm.get(goalinnerList.get(6)) ---> "+hm.get(goalinnerList.get(6)));
									String strPercent = hm.get(goalinnerList.get(6)) != null ? hm.get(goalinnerList.get(6)).toString().replace("%","") : "0";
									double percent = uF.parseToDouble(strPercent);
									totPercent += percent;
								//===start parvez date: 01-03-2023===	
									actaulCalBasis = (String)hm.get("ACTUAL_CAL_BASIS");
								//===end parvez date: 01-03-2023===
								}
								//System.out.println("roleCnt ---> "+roleCnt);
								avgPercent = totPercent / roleCnt;
							%>
					<!-- ===start parvez date: 01-03-2023=== -->		
							<%if(actaulCalBasis!=null && actaulCalBasis.equals("true")){ %>			
								<th style="text-align: right"><%=uF.formatIntoOneDecimal(avgPercent)%></th>	
							<%}else{ %>
								<th style="text-align: right"><%=Math.round(avgPercent)%>%</th>
							<%} %>
					<!-- ===end parvez date: 01-03-2023=== -->							
							<%
							Set set2 = hmScoreDetailsMap.keySet();
							Iterator it2 = set2.iterator();
							while(it2.hasNext()){
								String str = (String)it2.next();
								Map hm = (Map)hmScoreDetailsMap.get(str);
								if(hm==null)hm=new HashMap();
								String[] userTypeId = str.split("_");
								if(uF.parseToInt(userTypeId[1]) == 0) {
								%>
							<td style="text-align: right"> <%=uF.showData((String)hm.get(goalinnerList.get(6)), "Na")%> </td>	
							<% } } %>
							<td style="text-align: right"> 
								<% Map hmReviewer = (Map)hmScoreDetailsMap.get("REVIEWER_1");
								if(hmReviewer==null)hmReviewer = new HashMap(); %>
								<%=uF.showData((String)hmReviewer.get(goalinnerList.get(6)), "Na")%> 
							</td>
						</tr>
						<% } %>
					</table>
				
			</div>
			</div>
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(3)) == 1) {

							if (uF.parseToInt(innerList1.get(2)) == 1) {
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								int nWidth1 = 0, nWidth2 = 0, nWidth3 = 0, nWidth4 = 0, nWidth5 = 0;
								//int CGroles = 0, CMaggre = 0, CMeasure=0;	
								if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 1) {
									nWidth1 = 2250;
									nWidth2 = 1799;
									nWidth3 = 1349;
									nWidth4 = 899;
									nWidth5 = 449;
								} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 2) {
									nWidth1 = 2250;
									nWidth2 = 1799;
									nWidth3 = 1349;
									nWidth4 = 899;
									nWidth5 = 449;
								} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 3) {
									nWidth1 = 2563;
									nWidth2 = 2048;
									nWidth3 = 1536;
									nWidth4 = 1022;
									nWidth5 = 510;
								} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 4) {
									nWidth1 = 2870;
									nWidth2 = 2295;
									nWidth3 = 1719;
									nWidth4 = 1144;
									nWidth5 = 571;
								} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() > 4) {
									nWidth1 = 2870;
									nWidth2 = 2295;
									nWidth3 = 1719;
									nWidth4 = 1144;
									nWidth5 = 571;
								}
								/* 
								int nVar1 = 2 * (hmScoreDetailsMap.size()*4);
								int nVar = 71 * (hmScoreDetailsMap.size()*4);
								//System.out.println("1 nVar=====> "+nVar);
								//System.out.println("1 nVar1=====> "+nVar1);
								
								//int nWidth1 = 1545 + nVar;
								int nWidth1 = 1900 + nVar;
								
								int nWidth2 = 1203 + nVar;
								//int nWidth2 = 1000 + nVar;
								
								//int nWidth3 = 927 + nVar;
								int nWidth3 = 606 + nVar;
								
								//int nWidth4 = 618 + nVar;
								int nWidth4 = 9 + nVar;
								
								//int nWidth5 = 310 + nVar;
								int nWidth5 = nVar-587; */
								
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
								Map<String, List<List<String>>> objectiveMp = list.get(4);
			%>

			<%-- <div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;  text-align: left; margin-bottom:10px;">
				<div class="past heading_dash" style="text-align:left;padding-left:50px; height:auto;">
				<%=innerList1.get(1)%>
				<p style="font-weight: normal; font-size: 10px;">
				<%=innerList1.get(4)%>
				</p>
				
				<span>Aggregate Score: 
				<%
				Set set3 = hmScoreAggregateMap.keySet();
				Iterator it3 = set3.iterator();
				while(it3.hasNext()){
					String str = (String)it3.next();
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
					if(hmTemp!=null){
				%>
					<%=hmorientationMembers.get(str)%> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet")%>,				
				<%
					}
				}
				%>
				</span>
				
				</div> --%>
				
				<!-- <div class="content1" style="display: block; width: 100%; overflow: scroll;"> -->
				<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<%-- <blockquote><strong>Competencies + Goals + Objectives + Measures</strong> --%>
					<blockquote><strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
					<span>Aggregate Score: 
				<% 
				Set set3 = hmScoreAggregateMap.keySet();
				Iterator it3 = set3.iterator();
				while(it3.hasNext()){
					String str = (String)it3.next();
					String[] userTypeId = str.split("_");
					String usertypeName = hmorientationMembers.get(userTypeId[0]);
					if(uF.parseToInt(userTypeId[1]) == 1) {
						usertypeName = "Reviewer";
					}
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
					if(hmTemp!=null) {
				%>
		<!-- ===start parvez date: 11-04-2022=== -->		
				<% if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(userTypeId[0]) && uF.parseToInt(userTypeId[0]) == uF.parseToInt(hmPriorUser.get(maininnerList.get(0))))){ %>
					<%=usertypeName %> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet") %>,				
				<% } %>
		<!-- ===end parvez date: 11-04-2022=== -->		
				<%
					}
				}
				%>
				</span>
					</blockquote>
				</div>
			<%-- <div style="display: block; width: <%=nWidth1 + nVar1%>px; "> --%>
				<div style="display: block; width: <%=nWidth1%>px; ">
				
				<div style="float: left; border: 1px solid #eee; width: 200px;  text-align: center;">
					<b>Competencies</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b>Weightage</b>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center; background: #EFEFEF;">
					<b>Aggregate</b>
				</div>
				<%
				Set setms1 = hmScoreDetailsMap.keySet();
				Iterator itms1 = setms1.iterator();
				while(itms1.hasNext()) {
					String str = (String)itms1.next();
					String[] userTypeId = str.split("_");
					if(uF.parseToInt(userTypeId[1]) == 0) {
					%>
					<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
						<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
					</div>	
					<% } %>
				<% } %>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b> <%="Reviewer" %></b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 200px;  text-align: center;">
					<b>Goal </b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b>Weightage</b>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center; background: #EFEFEF;">
					<b>Aggregate</b>
				</div>
				<%
				Set setg1 = hmScoreDetailsMap.keySet();
				Iterator itg1 = setg1.iterator();
				while(itg1.hasNext()){
					String str = (String)itg1.next();
					String[] userTypeId = str.split("_");
					if(uF.parseToInt(userTypeId[1]) == 0) {
					%>
					<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
						<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
					</div>	
					<% } %>
				<% } %>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b> <%="Reviewer" %></b>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 200px;  text-align: center;">
					<b>Objective </b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b>Weightage</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center; background: #EFEFEF;">
					<b>Aggregate</b>
				</div>
				<% 
				Set seto1 = hmScoreDetailsMap.keySet();
				Iterator ito1 = seto1.iterator();
				while(ito1.hasNext()){
					String str = (String)ito1.next();
					String[] userTypeId = str.split("_");
					if(uF.parseToInt(userTypeId[1]) == 0) {
					%>
					<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
						<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
					</div>	
					<% } %>
				<% } %>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b> <%="Reviewer" %></b>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 200px;  text-align: center;">
					<b>Measure</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b>Weightage</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center; background: #EFEFEF;">
					<b>Aggregate</b>
				</div>
				<%
				Set setm1 = hmScoreDetailsMap.keySet();
				Iterator itm1 = setm1.iterator();
				while(itm1.hasNext()){
					String str = (String)itm1.next();
					String[] userTypeId = str.split("_");
					if(uF.parseToInt(userTypeId[1]) == 0) {
					%>
					<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
						<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
					</div>	
					<% } %>
				<% } %>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b> <%="Reviewer" %></b>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 200px;  text-align: center;">
					<b>Question</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b>Weightage</b>
				</div>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center; background: #EFEFEF;">
					<b>Aggregate</b>
				</div>
				<%
				Set set1 = hmScoreDetailsMap.keySet();
				Iterator it1 = set1.iterator();
				while(it1.hasNext()){
					String str = (String)it1.next();
					String[] userTypeId = str.split("_");
					if(uF.parseToInt(userTypeId[1]) == 0) {
					%>
					<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
						<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
					</div>	
					<% } %>
				<% } %>
				<div style="float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
					<b> <%="Reviewer" %></b>
				</div>
			</div>	
			<!-- <div style="overflow: hidden; float: left; width: 100%;"> -->
				<%
				//String aa
				double scoreTotAggregate = 0, scoreTotSelf = 0, scoreTotPeer = 0, scoreTotHr = 0, scoreTotManager = 0;
				double scoreTotWeightage = 0;
				List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
				for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
					List<String> innerList = scoreList.get(j);
					scoreTotWeightage += uF.parseToDouble(innerList.get(2));
				%>
				<div style="float: left; width: <%=nWidth1%>px;">
					<div style="float: left; border-top: 1px solid #eee; width: 202px;">
						<p style="padding-left:10px"><%=innerList.get(1)%></p>
					</div>
					<div style="float: left; border-top: 1px solid #eee; width: 61px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>
				<%
				double cavgPercent =0, ctotPercent = 0;
				int croleCnt=0;
				Set<String> setst121 = hmScoreMarksType1.keySet(); //aaaaa
				Iterator<String> itst121 = setst121.iterator();
				while(itst121.hasNext()) {
					croleCnt++;
					String str = (String)itst121.next();
					Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
					if(hm==null) hm=new HashMap<String, String>();
					
					double st1weightage=uF.parseToDouble(innerList.get(2));
					double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
					ctotPercent += scoreT1Total;
					}
					cavgPercent = ctotPercent / croleCnt;
					scoreTotAggregate += cavgPercent;
					%>
					<div style="float: left; border-top: 1px solid #eee; width: 63px; text-align: right; background: #EFEFEF; font-weight: bold;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(cavgPercent) %>%</p>
					</div>
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet(); //aaaaa
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()) {
							String str = (String)itst12.next();
							//System.out.println("str ===> "+str);
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String, String> hm = (Map<String, String>)hmScoreMarksType1.get(str);
								if(hm == null) hm = new HashMap<String, String>();
								double st1weightage = uF.parseToDouble(innerList.get(2));
								double scoreT1Total = st1weightage * (uF.parseToDouble(hm.get(innerList.get(0)))/100);
						%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "NA")+"%"%></p>
								</div>
							<% } %>
						<% } %>
					
						<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
						<% Map<String, String> hmReviewer = (Map<String, String>)hmScoreMarksType1.get("REVIEWER_1");
							if(hmReviewer==null)hmReviewer = new HashMap<String, String>(); 
							double st1weightageReviewer = uF.parseToDouble(innerList.get(2));
							double scoreT1TotalReviewer = st1weightageReviewer * (uF.parseToDouble(hmReviewer.get(innerList.get(0)))/100);
						%>
							<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalReviewer), "NA")+"%"%></p>
						</div>
						 

					<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth2%>px;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
							for (int k = 0; goalList != null && k < goalList.size(); k++) {
								List<String> goalinnerList = goalList.get(k);
						%>
						<div style="float: left; width: 100%;">
							<div style="float: left; border-top: 1px solid #eee; width: 202px">
								<p style="padding-left:10px"><%=goalinnerList.get(1)%></p>
							</div>
							<div style="float: left; border-top: 1px solid #eee; width: 60px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
						<%
						double gavgPercent =0, gtotPercent = 0;
						int groleCnt=0;
						Set<String> setg121 = hmGoalType1Marks.keySet(); //aaaa
						Iterator<String> itg121 = setg121.iterator();
						while(itg121.hasNext()){
							groleCnt++;
							String str = (String)itg121.next();
							Map<String,String> hm = (Map<String,String>)hmGoalType1Marks.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(goalinnerList.get(2));
							double goalT1Total=st1weightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
							gtotPercent += goalT1Total;
							}
							gavgPercent = gtotPercent / groleCnt;
							%>
							<div style="float: left; border-top: 1px solid #eee; width: 63px; text-align: right; background: #EFEFEF; font-weight: bold;">
								<p style="margin: 0px 10px 0px 0px;"><%=Math.round(gavgPercent)%>%</p>
							</div>
							<%
						Set<String> setg12 = hmGoalType1Marks.keySet(); //aaaa
						Iterator<String> itg12 = setg12.iterator();
						while(itg12.hasNext()) {
							String str = (String)itg12.next();
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String,String> hm = (Map<String,String>)hmGoalType1Marks.get(str);
								if(hm==null) hm=new HashMap<String, String>();
								
								double st1weightage=uF.parseToDouble(goalinnerList.get(2));
								double goalT1Total=st1weightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
							%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(goalT1Total), "Na")+"%"%></p>
								</div>
								<% } %>	
							<% } %>
							<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
							<% Map<String, String> hmGReviewer = (Map<String, String>)hmGoalType1Marks.get("REVIEWER_1");
								if(hmGReviewer==null)hmGReviewer = new HashMap<String, String>(); 
								double st1weightageGReviewer = uF.parseToDouble(innerList.get(2));
								double scoreT1TotalGReviewer = st1weightageGReviewer * (uF.parseToDouble(hmGReviewer.get(innerList.get(0)))/100);
							%>
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalGReviewer), "NA")+"%"%></p>
							</div>	
								
							<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;">
								<%
									List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
									for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
										List<String> objectiveinnerList = objectiveList.get(l);
								%>

								<div style="float: left; width: 100%;">
									<div style="float: left; border-top: 1px solid #eee; width: 202px;">
										<p style="padding-left:10px"><%=objectiveinnerList.get(1)%></p>										
									</div>
									<div style="float: left; border-top: 1px solid #eee; width: 60px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=objectiveinnerList.get(2)%>%</p>
									</div>
								<%
								double oavgPercent =0, ototPercent = 0;
								int oroleCnt=0;
								Set<String> setob121 = hmObjectiveMarks.keySet(); //aaaaaa
								Iterator<String> itob121 = setob121.iterator();
								while(itob121.hasNext()) {
									oroleCnt++;
									String str = (String)itob121.next();
									Map<String,String> hm = (Map<String,String>)hmObjectiveMarks.get(str);
									if(hm==null) hm=new HashMap<String, String>();
									
									double st1weightage=uF.parseToDouble(objectiveinnerList.get(2));
									double objectiveTotal=st1weightage*(uF.parseToDouble(hm.get(objectiveinnerList.get(0)))/100);
									ototPercent += objectiveTotal;
								}
								oavgPercent = ototPercent / oroleCnt;
								%>
								<div style="float: left; border-top: 1px solid #eee; width: 63px; text-align: right; background: #EFEFEF; font-weight: bold;">
									<p style="margin: 0px 10px 0px 0px;"><%=Math.round(oavgPercent)%>%</p>
								</div>
								<%
								Set<String> setob12 = hmObjectiveMarks.keySet(); //aaaaaa
								Iterator<String> itob12 = setob12.iterator();
								while(itob12.hasNext()) {
									String str = (String)itob12.next();
									String[] userTypeId = str.split("_");
									if(uF.parseToInt(userTypeId[1]) == 0) {
									Map<String,String> hm = (Map<String,String>)hmObjectiveMarks.get(str);
									if(hm==null) hm=new HashMap<String, String>();
									
									double st1weightage=uF.parseToDouble(objectiveinnerList.get(2));
									double objectiveTotal=st1weightage*(uF.parseToDouble(hm.get(objectiveinnerList.get(0)))/100);
								%>
									<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(objectiveTotal), "NA")+"%"%></p>
									</div>
									<% } %>	
								<% } %>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
								<% Map<String, String> hmOReviewer = (Map<String, String>)hmObjectiveMarks.get("REVIEWER_1");
									if(hmOReviewer==null)hmOReviewer = new HashMap<String, String>(); 
									double st1weightageOReviewer = uF.parseToDouble(innerList.get(2));
									double scoreT1TotalOReviewer = st1weightageOReviewer * (uF.parseToDouble(hmOReviewer.get(innerList.get(0)))/100);
								%>
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalOReviewer), "NA")+"%"%></p>
								</div>
							
								
									<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth4%>px;">
										<%
											List<List<String>> measureList = measureMp.get(objectiveinnerList.get(0));
											for (int m = 0; measureList != null && m < measureList.size(); m++) {
												List<String> measureinnerList = measureList.get(m);
										%>

										<div style="float: left; width: 100%;">
											<div style="float: left; border-top: 1px solid #eee; width: 202px;">
												<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
											</div>

											<div style="float: left; border-top: 1px solid #eee; width: 60px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
											</div>
									<% 
									double mavgPercent =0, mtotPercent = 0;
									int mroleCnt=0;
									Set<String> setms21 = hmMeasureUserScore.keySet(); //ccccccc
									Iterator<String> itms21 = setms21.iterator();
									while(itms21.hasNext()){
										mroleCnt++;
										String str = (String)itms21.next();
										Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
										if(hm==null) hm=new HashMap<String, String>();							
										double mweightage=uF.parseToDouble(measureinnerList.get(2));
										double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
										mtotPercent += measureTotal;
										}
										mavgPercent = mtotPercent / mroleCnt;
										%>
										<div style="float: left; border-top: 1px solid #eee; width: 63px; text-align: right; background: #EFEFEF; font-weight: bold;">
											<p style="margin: 0px 10px 0px 0px;"><%=Math.round(mavgPercent)%>%</p>
										</div>
										<% 
										Set<String> setms2 = hmMeasureUserScore.keySet(); //ccccccc
										Iterator<String> itms2 = setms2.iterator();
										while(itms2.hasNext()) {
											String str = (String)itms2.next();
											String[] userTypeId = str.split("_");
											if(uF.parseToInt(userTypeId[1]) == 0) {
											Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
											if(hm==null) hm=new HashMap<String, String>();							
											double mweightage=uF.parseToDouble(measureinnerList.get(2));
											double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
										%>
											<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "NA")+"%"%></p>
											</div>	
											<% } %>
										<% } %>
										<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
										<% Map<String, String> hmMReviewer = (Map<String, String>)hmMeasureUserScore.get("REVIEWER_1");
											if(hmMReviewer==null)hmMReviewer = new HashMap<String, String>(); 
											double st1weightageMReviewer = uF.parseToDouble(innerList.get(2));
											double scoreT1TotalMReviewer = st1weightageMReviewer * (uF.parseToDouble(hmMReviewer.get(innerList.get(0)))/100);
										%>
											<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalMReviewer), "NA")+"%"%></p>
										</div>
										

											<div style="float: left; width: <%=nWidth5%>px;">
												<%
													List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
													for (int n = 0; questionList != null && n < questionList.size(); n++) {
														List<String> question1List = questionList.get(n);
												%>
												<div style="float: left; width: 100%;">
													<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 201px;">
														<p style="padding-left:10px"><%=question1List.get(0)%></p>
													</div>
													<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
														<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
													</div>
													<%
														double avgPercent =0, totPercent = 0;
														int roleCnt=0;
															Set set21 = hmScoreDetailsMap.keySet();
															Iterator it21 = set21.iterator();
															while(it21.hasNext()){
																roleCnt++;
																String str = (String)it21.next();
																Map hm = (Map)hmScoreDetailsMap.get(str);
																if(hm==null)hm=new HashMap();
																String strPercent = hm.get(question1List.get(2)) != null ? hm.get(question1List.get(2)).toString().replace("%","") : "0";
																double percent = uF.parseToDouble(strPercent);
																totPercent += percent;
															}
															avgPercent = totPercent / roleCnt;
														%>
													<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 62px; text-align: right; background: #EFEFEF; font-weight: bold;">
														<p style="margin: 0px 10px 0px 0px;"><%=Math.round(avgPercent)%>%</p>
													</div>
													<%
													Set set2 = hmScoreDetailsMap.keySet();
													Iterator it2 = set2.iterator();
													while(it2.hasNext()){
														String str = (String)it2.next();
														String[] userTypeId = str.split("_");
														if(uF.parseToInt(userTypeId[1]) == 0) {
															Map hm = (Map)hmScoreDetailsMap.get(str);
															if(hm==null)hm=new HashMap();
														%>
															<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
																<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
															</div>
														<% } %>			
													<% } %>
													<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
													<% Map<String, String> hmQReviewer = (Map<String, String>)hmScoreDetailsMap.get("REVIEWER_1");
														if(hmQReviewer==null)hmQReviewer = new HashMap<String, String>(); 
													%>
														<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hmQReviewer.get(question1List.get(2)), "Na") %></p>
													</div>
												</div>
												<% } %>
											</div>
										</div>
										<% } %>
									</div>
								</div>
								<% } %>
							</div>
						</div>
						<% } %>
					</div>
				
				</div>
				
				<% } %>
				
				<div style="text-align: center; float: left; border-top: 1px solid #eee; width: 202px;">
					<b>Total</b>
				</div>
				<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; width: 56px; text-align: right;">
					<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotWeightage) %>%</p>
				</div>
				<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; border-right: 1px solid #eee; width: 56px; text-align: right; font-weight: bold;">
					<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotAggregate) %>%</p>
				</div>
					
			</div>
			
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 2) {
					//System.out.println("in 2 ------>>");
					List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
					int nWidth1 = 0, nWidth2 = 0, nWidth3 = 0;
					int nVar1 = 0, nVar2 = 0, nVar3 = 0;
					if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() == 1) { // checked with data
						nWidth1 = 1685;
						nWidth2 = 1150;
						nWidth3 = 600;
					} else if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() == 2) {
						nWidth1 = 1890;
						nWidth2 = 1290;
						nWidth3 = 670;
					} else if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() == 3) {
					//===start parvez date: 03-03-2023===	
						nWidth1 = 2595;
						nWidth2 = 1525;
					//===end parvez date: 03-03-2023===	
						nWidth3 = 740;
					} else if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() == 4) { // checked with data
						nWidth1 = 2300;
						nWidth2 = 1555;
						nWidth3 = 810;
					} else if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() == 5) {
						nWidth1 = 2505;
						nWidth2 = 1690;
						nWidth3 = 875;
					} else if(hmScoreDetailsMap != null && hmScoreDetailsMap.size() > 5) {
						nWidth1 = 2715;
						nWidth2 = 1825;
						nWidth3 = 945;
					}
					/* int nVar1 = 2 * (hmScoreDetailsMap.size()+hmMeasureUserScore.size());
					int nVar = 71 * (hmScoreDetailsMap.size()+hmMeasureUserScore.size()); */
					/* int nVar1 = 2 * (hmScoreDetailsMap.size()*2);
					int nVar = 71 * (hmScoreDetailsMap.size()*2);
					//System.out.println("nVar=====>"+nVar);
					nWidth1 = 1574 + nVar;
					//int nWidth1 = 1024 + nVar;
					nWidth2 = 882 + nVar;
					//int nWidth3 = 445 + nVar;
					nWidth3 = 182 + nVar; */
					
					Map<String, List<List<String>>> scoreMp = list.get(0);
					Map<String, List<List<String>>> measureMp = list.get(1);
					Map<String, List<List<String>>> questionMp = list.get(2);
			%>
		
			<!-- <div class="content1" style="display: block; width: 100%; overflow: scroll;"> -->
			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<%-- <blockquote><strong>Competencies + Measures</strong> --%>
					<blockquote><strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
						<span>Aggregate Score: 
							<%
							Set set3 = hmScoreAggregateMap.keySet();
							Iterator it3 = set3.iterator();
							while(it3.hasNext()) {
								String str = (String)it3.next();
								String[] userTypeId = str.split("_");
								String usertypeName = hmorientationMembers.get(userTypeId[0]);
								if(uF.parseToInt(userTypeId[1]) == 1) {
									usertypeName = "Reviewer";
								}
								Map hmTemp = (Map)hmScoreAggregateMap.get(str);
								if(hmTemp!=null) {
							%>
							
							<!-- ===start parvez date: 11-04-2022=== -->
							
								<%=usertypeName %> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet") %>,				
							<!-- ===end parvez date: 11-04-2022=== -->
							<%
								}
							} %>
						</span>
					</blockquote>
				</div>
			
			<%-- <div style="display: block; width: <%=nWidth1 + nVar1%>px; "> --%>
				<div style="display: block; float: left; width: <%=nWidth1 %>px; ">
					<div style="float: left; border: 1px solid #eee; width: 300px; text-align: center;">
						<b>Competencies</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set setms1 = hmScoreDetailsMap.keySet();
					Iterator itms1 = setms1.iterator();
					while(itms1.hasNext()){
						String str = (String)itms1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
					
					<div style="float: left; border: 1px solid #eee; width: 300px; text-align: center;">
						<b>Measure</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set setm1 = hmScoreDetailsMap.keySet();
					Iterator itm1 = setm1.iterator();
					while(itm1.hasNext()){
						String str = (String)itm1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
					
					<div style="float: left; border: 1px solid #eee; width: 360px; text-align: center;">
						<b>Question</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 80px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set set1 = hmScoreDetailsMap.keySet();
					Iterator it1 = set1.iterator();
					while(it1.hasNext()) {
						String str = (String)it1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
				</div>
		
			<!-- <div style="overflow: hidden; float: left; width: 100%;"> -->
				<%
				double scoreTotAggregate = 0, scoreTotSelf = 0, scoreTotPeer = 0, scoreTotHr = 0, scoreTotManager = 0;
				double scoreTotWeightage = 0;
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
					for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
						List<String> innerList = scoreList.get(j);
						scoreTotWeightage += uF.parseToDouble(innerList.get(2));
				%>

				<div style="float: left; width: <%=nWidth1%>px;">
					<div style="float: left; border-top: 1px solid #eee; width: 300px;">
						<p style="padding-left:10px"><%=innerList.get(1)%></p>
					</div>
					
					<div style="float: left; border-top: 1px solid #eee; width: 80px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>
					<%	
					double cavgPercent =0, ctotPercent = 0;
					int croleCnt=0;
						Set<String> setst121 = hmScoreMarksType1.keySet();
						Iterator<String> itst121 = setst121.iterator();
						while(itst121.hasNext()){
							croleCnt++;
							String str = (String)itst121.next();
							Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(innerList.get(2));
							double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
							ctotPercent += scoreT1Total;
						}
						cavgPercent = ctotPercent / croleCnt;
						scoreTotAggregate += cavgPercent;
							%>
					<div style="float: left; border-top: 1px solid #eee; width: 80px; text-align: right; background: #EFEFEF; font-weight: bold;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(cavgPercent)%>%</p>
					</div>
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet();
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()){
							String str = (String)itst12.next();
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
								if(hm==null) hm=new HashMap<String, String>();
								
								double st1weightage=uF.parseToDouble(innerList.get(2));
								double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
							%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "Na")+"%"%></p>
								</div>	
							<% } %>
						<% } %>
						<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
						<% Map<String, String> hmSReviewer = (Map<String, String>)hmScoreMarksType1.get("REVIEWER_1");
							if(hmSReviewer==null)hmSReviewer = new HashMap<String, String>(); 
							double st1weightageSReviewer = uF.parseToDouble(innerList.get(2));
							double scoreT1TotalSReviewer = st1weightageSReviewer * (uF.parseToDouble(hmSReviewer.get(innerList.get(0)))/100);
						%>
							<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalSReviewer), "NA")+"%"%></p>
						</div>
						
						
					<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth2%>px;"> <!-- 1450 -->
						<%
							List<List<String>> measureList = measureMp.get(innerList.get(0));
							for (int k = 0; measureList != null && k < measureList.size(); k++) {
								List<String> measureinnerList = measureList.get(k);
						%>

						<div style="float: left; border-top: 1px solid #eee; width: 300px;">
							<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
						</div>

						<div style="float: left; border-top: 1px solid #eee; width: 80px; text-align: right;">
							<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
						</div>
						<%
						double mavgPercent =0, mtotPercent = 0;
						int mroleCnt=0;
						Set<String> setms21 = hmMeasureUserScore.keySet();
						Iterator<String> itms21 = setms21.iterator();
						while(itms21.hasNext()){
							mroleCnt++;
							String str = (String)itms21.next();
							Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							double mweightage=uF.parseToDouble(measureinnerList.get(2));
							//System.out.println(str+" uF.parseToDouble(hm.get(measureinnerList.get(0)))/100====>"+uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							mtotPercent += measureTotal;
						}
						mavgPercent = mtotPercent / mroleCnt;
							%>
						<div style="float: left; border-top: 1px solid #eee; width: 80px; text-align: right; background: #EFEFEF; font-weight: bold;">
							<p style="margin: 0px 10px 0px 0px;"><%=Math.round(mavgPercent)%>%</p>
						</div>
						<%
						Set<String> setms2 = hmMeasureUserScore.keySet();
						Iterator<String> itms2 = setms2.iterator();
						while(itms2.hasNext()){
							String str = (String)itms2.next();
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
								if(hm==null) hm=new HashMap<String, String>();
								double mweightage=uF.parseToDouble(measureinnerList.get(2));
								double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "Na")+"%"%></p>
								</div>	
							<% } %>
						<% } %>
						<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
						<% Map<String, String> hmMReviewer = (Map<String, String>)hmMeasureUserScore.get("REVIEWER_1");
							if(hmMReviewer==null)hmMReviewer = new HashMap<String, String>(); 
							double st1weightageMReviewer = uF.parseToDouble(innerList.get(2));
							double scoreT1TotalMReviewer = st1weightageMReviewer * (uF.parseToDouble(hmMReviewer.get(innerList.get(0)))/100);
						%>
							<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalMReviewer), "NA")+"%"%></p>
						</div>
						
					
						<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;"> <!-- 736 -->
							<%
								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
								for (int l = 0; questionList != null && l < questionList.size(); l++) {
									List<String> question1List = questionList.get(l);
							%>
							<div style="float: left; width: 100%;">
								<div style="float: left; border-top: 1px solid #eee; width: 360px;">
										<p style="padding-left:10px"><%=question1List.get(0)%></p>
								</div>
								
								<div style="float: left; border-top: 1px solid #eee; width: 80px;">
										<p style="padding-left:10px"><%=question1List.get(1)%>%</p>
								</div>
								<%
									double avgPercent =0, totPercent = 0;
									int roleCnt=0;
										Set set21 = hmScoreDetailsMap.keySet();
										Iterator it21 = set21.iterator();
										while(it21.hasNext()){
											roleCnt++;
											String str = (String)it21.next();
											Map hm = (Map)hmScoreDetailsMap.get(str);
											if(hm==null)hm=new HashMap();
											String strPercent = hm.get(question1List.get(2)) != null ? hm.get(question1List.get(2)).toString().replace("%","") : "0";
											double percent = uF.parseToDouble(strPercent);
											totPercent += percent;
										}
										avgPercent = totPercent / roleCnt;
									%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 80px; text-align: right; background: #EFEFEF; font-weight: bold;">
									<p style="margin: 0px 10px 0px 0px;"><%=Math.round(avgPercent)%>%</p>
								</div>
								<%
								Set set2 = hmScoreDetailsMap.keySet();
								Iterator it2 = set2.iterator();
								while(it2.hasNext()) {
									String str = (String)it2.next();
									String[] userTypeId = str.split("_");
									if(uF.parseToInt(userTypeId[1]) == 0) {
										Map hm = (Map)hmScoreDetailsMap.get(str);
										if(hm==null)hm=new HashMap();
										%>
									<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
									</div>
									<% } %>
								<% } %>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
								<% Map<String, String> hmQReviewer = (Map<String, String>)hmScoreDetailsMap.get("REVIEWER_1");
									if(hmQReviewer==null)hmQReviewer = new HashMap<String, String>(); 
								%>
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hmQReviewer.get(question1List.get(2)), "Na")%></p>
								</div>
								
							</div>
							<% } %>
						</div>
						<% } %>
					</div>
					
				</div>
			<!-- </div> -->
				<% } %>
					<div style="text-align: center; float: left; border-top: 1px solid #eee; width: 300px;">
						<b>Total</b>
					</div>
					<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; width: 80px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotWeightage) %>%</p>
					</div>
					<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; border-right: 1px solid #eee; width: 80px; text-align: right; font-weight: bold;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotAggregate) %>%</p>
					</div>
			<!-- </div> -->
			</div>

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 3) {
					int nWidth1 = 0, nWidth2 = 0, nWidth3 = 0, nWidth4 = 0;
					int CGroles = 0, CMaggre = 0, CMeasure=0;	
					if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 1) {
						nWidth1 = 1960;
						nWidth2 = 1465;
						nWidth3 = 980;
						nWidth4 = 490;
						CGroles = 61;
						CMaggre = 62;
						CMeasure = 220;
					} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 2) {
						nWidth1 = 1960;
						nWidth2 = 1465;
						nWidth3 = 980;
						nWidth4 = 490;
						CGroles = 61;
						CMaggre = 62;
						CMeasure = 220;
					} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 3) {
						nWidth1 = 2240;
						nWidth2 = 1661;
						nWidth3 = 1115;
						nWidth4 = 559;
						CGroles = 60;
						CMaggre = 63;
						CMeasure = 221;
					} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() == 4) {
						nWidth1 = 2552;
						nWidth2 = 1930;
						nWidth3 = 1322;
						nWidth4 = 625;
						CGroles = 60;
						CMaggre = 63;
						CMeasure = 221;
					} else if(hmScoreDetailsMap != null &&hmScoreDetailsMap.size() > 4) {
						nWidth1 = 2552;
						nWidth2 = 1930;
						nWidth3 = 1322;
						nWidth4 = 625;
						CGroles = 60;
						CMaggre = 63;
						CMeasure = 221;
					}
					
					/* int nVar1 = 2 * (hmScoreDetailsMap.size()*4);
					int nVar = 71 * (hmScoreDetailsMap.size()*4);
					//System.out.println("3 nvar====>"+nVar);
					//int nWidth1 = 1340 + nVar;
					int nWidth1 = 1412 + nVar;
					
					//int nWidth2 = 1004 + nVar;
					int nWidth2 = 791 + nVar;
					
					//int nWidth3 = 670 + nVar;
					int nWidth3 = 186 + nVar;
					
					//int nWidth4 = 337 + nVar;
					int nWidth4 = nVar-432; */
					
					List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
					Map<String, List<List<String>>> scoreMp = list.get(0);
					Map<String, List<List<String>>> measureMp = list.get(1);
					Map<String, List<List<String>>> questionMp = list.get(2);
					Map<String, List<List<String>>> GoalMp = list.get(3);
			%>


				</div> --%>
			<!-- <div class="content1" style="display: block; width: 100%; overflow: scroll;"> -->
			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<%-- <blockquote><strong>Competencies + Goals + Measures</strong> --%>
					<blockquote><strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
					<span>Aggregate Score: 
				<%
				Set set3 = hmScoreAggregateMap.keySet();
				Iterator it3 = set3.iterator();
				while(it3.hasNext()){
					String str = (String)it3.next();
					String[] userTypeId = str.split("_");
					String usertypeName = hmorientationMembers.get(userTypeId[0]);
					if(uF.parseToInt(userTypeId[1]) == 1) {
						usertypeName = "Reviewer";
					}
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
					if(hmTemp!=null) {
				%>
					<%=usertypeName %> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet") %>,				
				<%
					}
				}
				%>
				</span>
					
				</blockquote>
				</div>
			<%-- <div style="display: block; width: <%=nWidth1 + nVar1%>px; "> --%>
					<div style="display: block; width: <%=nWidth1%>px; ">
					<div style="float: left; border: 1px solid #eee; width: 220px; text-align: center;">
						<b>Competencies</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 65px; text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 62px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set setms1 = hmScoreDetailsMap.keySet();
					Iterator itms1 = setms1.iterator();
					while(itms1.hasNext()) {
						String str = (String)itms1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>	
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
					
					<div style="float: left; border: 1px solid #eee; width: 223px;  text-align: center;">
						<b>Goal </b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 65px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set setg1 = hmScoreDetailsMap.keySet();
					Iterator itg1 = setg1.iterator();
					while(itg1.hasNext()) {
						String str = (String)itg1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: <%=CGroles%>px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>	
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
		
					<div style="float: left; border: 1px solid #eee; width: <%=CMeasure%>px;  text-align: center;">
						<b>Measure</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: <%=CMaggre%>px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set setm1 = hmScoreDetailsMap.keySet();
					Iterator itm1 = setm1.iterator();
					while(itm1.hasNext()) {
						String str = (String)itm1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>		
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
					
					<div style="float: left;  border: 1px solid #eee; width: 222px; text-align: center;">
						<b>Question</b>
					</div>
					<div style="float: left;  border: 1px solid #eee; width: 65px; text-align: center;">
						<b>Weightage</b>
					</div>
					<div style="float: left; border: 1px solid #eee; width: 64px; text-align: center; background: #EFEFEF;">
						<b>Aggregate</b>
					</div>
					<%
					Set set1 = hmScoreDetailsMap.keySet();
					Iterator it1 = set1.iterator();
					while(it1.hasNext()){
						String str = (String)it1.next();
						String[] userTypeId = str.split("_");
						if(uF.parseToInt(userTypeId[1]) == 0) {
						%>
						<div style="float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
							<b> <%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></b>
						</div>	
						<% } %>	
					<% } %>
					<div style="float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
						<b> <%="Reviewer" %></b>
					</div>
				</div>	
			
				<%
				double scoreTotAggregate = 0, scoreTotSelf = 0, scoreTotPeer = 0, scoreTotHr = 0, scoreTotManager = 0;
				double scoreTotWeightage = 0;
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
					for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
						List<String> innerList = scoreList.get(j);
						scoreTotWeightage += uF.parseToDouble(innerList.get(2));
				%>
			<div style="float: left; border: 1px solid #eee; width: <%=nWidth1%>px;">
				<div style="float: left; width: 100%;">
					<div style="float: left; border-top: 1px solid #eee; width: 222px;">
						<p style="padding-left:10px"><%=innerList.get(1)%>()</p>						
					</div>
					<div style="float: left; border-top: 1px solid #eee; width: 65px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>
					<%
					double cavgPercent =0, ctotPercent = 0;
					int croleCnt=0;
					Set<String> setst121 = hmScoreMarksType1.keySet();
					Iterator<String> itst121 = setst121.iterator();
					while(itst121.hasNext()){
						croleCnt++;
						String str = (String)itst121.next();
						Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
						if(hm==null) hm=new HashMap<String, String>();
						
						double st1weightage=uF.parseToDouble(innerList.get(2));
						double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
						ctotPercent += scoreT1Total;
					}
					cavgPercent = ctotPercent / croleCnt;
					scoreTotAggregate += cavgPercent;
						%>
					<div style="float: left; border-top: 1px solid #eee; width: 65px; text-align: right; background: #EFEFEF; font-weight: bold;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(cavgPercent)%>%</p>
					</div>
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet();
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()){
							String str = (String)itst12.next();
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
								if(hm==null) hm=new HashMap<String, String>();
								
								double st1weightage=uF.parseToDouble(innerList.get(2));
								double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
								%>
								<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 66px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "Na")+"%"%></p>
								</div>	
							<% } %>
						<% } %>
						<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 66px; text-align: right;">
						<% Map<String, String> hmSReviewer = (Map<String, String>)hmScoreMarksType1.get("REVIEWER_1");
							if(hmSReviewer==null)hmSReviewer = new HashMap<String, String>(); 
							double st1weightageSReviewer = uF.parseToDouble(innerList.get(2));
							double scoreT1TotalSReviewer = st1weightageSReviewer * (uF.parseToDouble(hmSReviewer.get(innerList.get(0)))/100);
						%>
							<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalSReviewer), "NA")+"%"%></p>
						</div>

					<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth2%>px;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
							for (int k = 0; goalList != null && k < goalList.size(); k++) {
								List<String> goalinnerList = goalList.get(k);
						%>

						<div style="float: left; width: 100%;">
							<div style="float: left; border-top: 1px solid #eee; width: 222px;">
								<p style="padding-left:10px"><%=goalinnerList.get(1)%></p>
							</div>
							<div style="float: left; border-top: 1px solid #eee; width: 68px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
							<%
							double gavgPercent =0, gtotPercent = 0;
							int groleCnt=0;
							Set<String> setgs21 = hmGoalType2Marks.keySet();
							Iterator<String> itgs21 = setgs21.iterator();
							while(itgs21.hasNext()){
								groleCnt++;
								String str = (String)itgs21.next();
								Map<String,String> hm = (Map<String,String>)hmGoalType2Marks.get(str);
								if(hm==null) hm=new HashMap<String, String>();							
								double gweightage=uF.parseToDouble(goalinnerList.get(2));
								double goalTotal=gweightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
								gtotPercent += goalTotal;
							}
							gavgPercent = gtotPercent / groleCnt;
							%>
							<div style="float: left; border-top: 1px solid #eee; width: 68px; text-align: right; background: #EFEFEF; font-weight: bold;">
								<p style="margin: 0px 10px 0px 0px;"><%=Math.round(gavgPercent) %>%</p>
							</div>
							
							<%
						Set<String> setgs2 = hmGoalType2Marks.keySet();
						Iterator<String> itgs2 = setgs2.iterator();
						while(itgs2.hasNext()){
							String str = (String)itgs2.next();
							String[] userTypeId = str.split("_");
							if(uF.parseToInt(userTypeId[1]) == 0) {
								Map<String,String> hm = (Map<String,String>)hmGoalType2Marks.get(str);
								if(hm==null) hm=new HashMap<String, String>();							
								double gweightage=uF.parseToDouble(goalinnerList.get(2));
								double goalTotal=gweightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
								%>
									<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(goalTotal), "Na")+"%"%></p>
									</div>	
								<% } %>
							<% } %>	
							<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
							<% Map<String, String> hmGReviewer = (Map<String, String>)hmGoalType2Marks.get("REVIEWER_1");
								if(hmGReviewer==null)hmGReviewer = new HashMap<String, String>(); 
								double st1weightageGReviewer = uF.parseToDouble(innerList.get(2));
								double scoreT1TotalGReviewer = st1weightageGReviewer * (uF.parseToDouble(hmGReviewer.get(innerList.get(0)))/100);
							%>
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalGReviewer), "NA")+"%"%></p>
							</div>
							
							<div style="float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;">
								<%
									List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
									for (int l = 0; measureList != null && l < measureList.size(); l++) {
										List<String> measureinnerList = measureList.get(l);
								%>
								<div style="float: left; width: 100%;">
									<div style="float: left; border-top: 1px solid #eee; width: 222px;">
										<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
									</div>
									<div style="float: left; border-top: 1px solid #eee; width: 66px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
									</div>
								<%
									double mavgPercent =0, mtotPercent = 0;
									int mroleCnt=0;
									Set<String> setms21 = hmMeasureUserScore.keySet();
									Iterator<String> itms21 = setms21.iterator();
									while(itms21.hasNext()){
										mroleCnt++;
										String str = (String)itms21.next();
										Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
										if(hm==null) hm=new HashMap<String, String>();							
										double mweightage=uF.parseToDouble(measureinnerList.get(2));
										double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
										mtotPercent += measureTotal;
									}
									mavgPercent = mtotPercent / mroleCnt;
										%>
									<div style="float: left; border-top: 1px solid #eee; width: 66px; text-align: right; background: #EFEFEF; font-weight: bold;">
										<p style="margin: 0px 10px 0px 0px;"><%=Math.round(mavgPercent)%>%</p>
									</div>
									<%
									Set<String> setms2 = hmMeasureUserScore.keySet();
									Iterator<String> itms2 = setms2.iterator();
									while(itms2.hasNext()){
										String str = (String)itms2.next();
										String[] userTypeId = str.split("_");
										if(uF.parseToInt(userTypeId[1]) == 0) {
											Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
											if(hm==null) hm=new HashMap<String, String>();							
											double mweightage=uF.parseToDouble(measureinnerList.get(2));
											double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
											%>
											<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 65px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "Na")+"%"%></p>
											</div>	
										<% } %>
									<% } %>
									<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 65px; text-align: right;">
									<% Map<String, String> hmMReviewer = (Map<String, String>)hmMeasureUserScore.get("REVIEWER_1");
										if(hmMReviewer==null)hmMReviewer = new HashMap<String, String>(); 
										double st1weightageMReviewer = uF.parseToDouble(innerList.get(2));
										double scoreT1TotalMReviewer = st1weightageMReviewer * (uF.parseToDouble(hmMReviewer.get(innerList.get(0)))/100);
									%>
										<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1TotalMReviewer), "NA")+"%"%></p>
									</div>
										
									
									<div style="float: left; width: <%=nWidth4%>px;">
										<%
										List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
										for (int m = 0; questionList != null && m < questionList.size(); m++) {
											List<String> question1List = questionList.get(m);
										%>
										<div style="float: left; width: 100%;">
											<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 222px;">
												<p style="padding-left:10px"><%=question1List.get(0)%></p>
											</div>
											<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 66px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=question1List.get(1)%>%</p>
											</div>
											<%
												double avgPercent =0, totPercent = 0;
												int roleCnt=0;
													Set set21 = hmScoreDetailsMap.keySet();
													Iterator it21 = set21.iterator();
													while(it21.hasNext()){
														roleCnt++;
														String str = (String)it21.next();
														Map hm = (Map)hmScoreDetailsMap.get(str);
														if(hm==null)hm=new HashMap();
														String strPercent = hm.get(question1List.get(2)) != null ? hm.get(question1List.get(2)).toString().replace("%","") : "0";
														double percent = uF.parseToDouble(strPercent);
														totPercent += percent;
													}
													avgPercent = totPercent / roleCnt;
												%>
											<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 66px; text-align: right; background: #EFEFEF; font-weight: bold;">
												<p style="margin: 0px 10px 0px 0px;"><%=Math.round(avgPercent)%>%</p>
											</div>
											
											<%
												Set set2 = hmScoreDetailsMap.keySet();
												Iterator it2 = set2.iterator();
												while(it2.hasNext()){
													String str = (String)it2.next();
													String[] userTypeId = str.split("_");
													if(uF.parseToInt(userTypeId[1]) == 0) {
														Map hm = (Map)hmScoreDetailsMap.get(str);
														if(hm==null)hm=new HashMap();
														%>  
														<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 65px; text-align: right;">
															<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
														</div>	
													<% } %>
												<% } %>
												<div style="float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
												<% Map<String, String> hmQReviewer = (Map<String, String>)hmScoreDetailsMap.get("REVIEWER_1");
													if(hmQReviewer==null)hmQReviewer = new HashMap<String, String>(); 
												%>
													<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hmQReviewer.get(question1List.get(2)), "Na")%></p>
												</div>
													
										</div>
										<% } %>
									</div>
								</div>
								<% } %>
							</div>
						</div>
						<!-- </div> -->
						<% } %>
					</div>	
				</div>
			
				</div>
				<% } %>
					<div style="text-align: center; float: left; border-top: 1px solid #eee; width: 222px;">
						<b>Total</b>
					</div>
					<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; width: 65px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotWeightage) %>%</p>
					</div>
					<div style="float: left; border-top: 1px solid #eee; border-left: 1px solid #eee; border-right: 1px solid #eee; width: 65px; text-align: right; font-weight: bold;">
						<p style="margin: 0px 10px 0px 0px;"><%=Math.round(scoreTotAggregate) %>%</p>
					</div>
				</div>
			<!-- </div> -->
			
			<%
				}
				} else {
					//System.out.println("levelMp ===> "+levelMp);
					//System.out.println("sub Section id ===> "+innerList1.get(0));
					List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
					//System.out.println("list ===> "+ list);
					Map<String, List<List<String>>> scoreMp = list.get(0);
					String systemtype = ""; 							
					if(uF.parseToInt(innerList1.get(3)) == 3) {
						systemtype="Goal";
					} else if(uF.parseToInt(innerList1.get(3)) == 4) {
						systemtype="KRA";
					} else {
						systemtype="Target";
					}
			%>

			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<blockquote>
					<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")) { %>
					<strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
					<% } %>
					<span>Aggregate Score: 
				<%
				Set set3 = hmScoreAggregateMap.keySet();
				Iterator it3 = set3.iterator();
				while(it3.hasNext()) {
					String str = (String)it3.next();
					String[] userTypeId = str.split("_");
					String usertypeName = hmorientationMembers.get(userTypeId[0]);
					if(uF.parseToInt(userTypeId[1]) == 1) {
						usertypeName = "Reviewer";
					}
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
			//===start parvez date: 17-03-2023===		
					//if(hmTemp!=null ) {
					if(hmTemp!=null && (uF.parseToInt(userTypeId[0]) != 3 || !isSelfRating)){	
					
						
						if(!isUserTypeRating || (isUserTypeRating && alFeatureUserTypeId.contains(userTypeId[0]) && uF.parseToInt(userTypeId[0]) == uF.parseToInt(hmPriorUser.get(maininnerList.get(0))))){
						
				%>
				<%if(hmFeatureStatus!=null && (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
											&& (uF.parseToInt(userTypeId[0])!=4 && uF.parseToInt(userTypeId[0])!=14 && uF.parseToInt(userTypeId[0])!=13)))){ 
				%>
					<%=usertypeName %> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet") %>,				
				<% } %>
				<% } %>
			<!-- ===end parvez date: 17-03-2023=== -->	
				<%
					}
				}
				%>
				</span>
				</blockquote>
				</div>
				
				<div style="float: left; border: 1px solid #eee; width: 99.8%;">
				<!-- ===start parvez 02-03-2023=== -->	
					<table class="table table-bordered" style="width: 100%; float: left;">
				<!-- ===end parvez 02-03-2023=== -->
						<tr>
							<td width="90%"><b><%=systemtype %></b></td>
							<td><b>Weightage</b></td>
							<th>Aggregate</th>
							<% 
							Set setms1 = hmScoreDetailsMap.keySet();
							Iterator itms1 = setms1.iterator();
							while(itms1.hasNext()){
								String str = (String)itms1.next();
								String[] userTypeId = str.split("_");
								if(uF.parseToInt(userTypeId[1]) == 0) {
								%>
								<!-- ===start parvez 02-03-2023=== -->	
									<%-- <th><%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></th> --%>
									<%if(hmFeatureStatus!=null && (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD))) 
											&& (uF.parseToInt(userTypeId[0])!=4 && uF.parseToInt(userTypeId[0])!=14 && uF.parseToInt(userTypeId[0])!=13))){ 
									%>
										<th><%=uF.showData(hmorientationMembers.get(userTypeId[0]), "")%></th>
									<% } %>
								<!-- ===end parvez 02-03-2023=== -->	
								<% } %>
							<% } %>
							<th>Reviewer</th>
						</tr>
						<%
						//System.out.println("scoreMp ===> " + scoreMp);
						//System.out.println("innerList1.get(0) ===> " + innerList1.get(0));
						List<List<String>> goalList = scoreMp.get(innerList1.get(0));
						for (int k = 0; goalList != null && k < goalList.size(); k++) {
							//System.out.println("goalList ===> " + goalList);
							List<String> goalinnerList = goalList.get(k);
							//System.out.println("goalinnerList ===> " + goalinnerList);
						%>
						<tr>
							<td><%=a+1 %>.
							<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")){ %>
							<%=i+1 %>.
							<% } %>
							<%=k+1%>)&nbsp;<%=goalinnerList.get(4)%></td>
							<td style="text-align: right"><%=goalinnerList.get(5)%>%</td>			
									<%
									double avgPercent =0, totPercent = 0;
									int roleCnt = 0;
									String actaulCalBasis = null;	//added by parvez date: 01-03-2023
							//===start parvez date: 13-03-2023===			
								//Set set21 = hmScoreDetailsMap.keySet();	
								Set set21 = hmAggregateScoreDetailsMap.keySet();
										Iterator it21 = set21.iterator();
										while(it21.hasNext()) {
											//roleCnt++;
											String str = (String)it21.next();
											//Map hm = (Map)hmScoreDetailsMap.get(str);
											Map hm = (Map)hmAggregateScoreDetailsMap.get(str);				
											if(hm==null)hm=new HashMap();
											String[] arrUserTypeIds = str.split("_");
											if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
													&& (uF.parseToInt(arrUserTypeIds[0])==4 || uF.parseToInt(arrUserTypeIds[0])==14 || uF.parseToInt(arrUserTypeIds[0])==13)){
												continue;
											}
											
											roleCnt++;
											String strPercent = hm.get(goalinnerList.get(0)) != null ? hm.get(goalinnerList.get(0)).toString().replace("%","") : "0";
											double percent = uF.parseToDouble(strPercent);
											totPercent += percent;
										//===start parvez date: 01-03-2023===	
											actaulCalBasis = (String)hm.get("ACTUAL_CAL_BASIS");
										//===end parvez date: 01-03-2023===
										}
										//System.out.println("roleCnt ---> "+roleCnt);
										avgPercent = totPercent / roleCnt;
										%>
									
								<!-- ===start parvez date: 01-03-2023=== -->		
									<%-- <th style="text-align: right"><%=Math.round(avgPercent)%>%</th> --%>		
											
										<%if(actaulCalBasis!=null && actaulCalBasis.equals("true")){ %>			
											<th style="text-align: right"><%=uF.formatIntoOneDecimal(avgPercent)%></th>	
										<%}else{ %>
											<th style="text-align: right"><%=Math.round(avgPercent)%>%</th>
										<%} %>
								<!-- ===end parvez date: 01-03-2023=== -->				
									<%
									
									Set set2 = hmScoreDetailsMap.keySet();
									Iterator it2 = set2.iterator();
									while(it2.hasNext()) {
										String str = (String)it2.next();
										String[] userTypeId = str.split("_");
										if(uF.parseToInt(userTypeId[1]) == 0) {
											Map hm = (Map)hmScoreDetailsMap.get(str);
											if(hm==null)hm=new HashMap();
										
										%>
									<!-- ===start parvez 02-03-2023=== -->	
										<%-- <td style="text-align: right"><%=uF.showData((String)hm.get(goalinnerList.get(0)), "NA")%></td> --%>
											<%if(hmFeatureStatus!=null && (!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) || (uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) 
												&& (uF.parseToInt(userTypeId[0])!=4 && uF.parseToInt(userTypeId[0])!=14 && uF.parseToInt(userTypeId[0])!=13)))){ 
											%>
												<td style="text-align: right"><%=uF.showData((String)hm.get(goalinnerList.get(0)), "NA")%></td>
											<% } %>	
									<!-- ===end parvez 02-03-2023=== -->		
										<% } %>	
									<% } %>
									<td style="text-align: right"> 
										<% Map hmReviewer = (Map)hmScoreDetailsMap.get("REVIEWER_1");
										if(hmReviewer==null)hmReviewer = new HashMap(); %>
										<%-- <%=uF.showData((String)hmReviewer.get(goalinnerList.get(6)), "Na")%> --%> 
									</td>
								</tr>
							<% } %>
						</table>
					</div>
				</div>
			<!-- </div> -->
			<% } } %>
			</div>
			</div>
		</div>
                <!-- /.box-body -->
            </div>
        	<% } %>
        </section>
        
    </div>
</section>
</s:form>