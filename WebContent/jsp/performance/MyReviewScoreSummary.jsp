
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<script type="text/javascript">
	$(function() {
		var a = '#from';
		$("#from").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#to').datepicker('setStartDate', minDate);
        });
        
        $("#to").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#from').datepicker('setEndDate', minDate);
        });
	});
</script> 
<%
	List<String> appraisalList = (List<String>) request.getAttribute("appraisalList");
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	Map<String, String> hmorientationMembers = (Map) request.getAttribute("hmorientationMembers");
	Map hmScoreAggregateMap = (Map) request.getAttribute("hmScoreAggregateMap");
	
	Map<String, Map<String,String>> hmMeasureUserScore =(Map<String, Map<String,String>>)request.getAttribute("hmMeasureUserScore");
	Map<String, Map<String,String>> hmScoreMarksType1 =(Map<String, Map<String,String>>)request.getAttribute("hmScoreMarksType1");
	Map<String, Map<String,String>> hmObjectiveMarks =(Map<String, Map<String,String>>)request.getAttribute("hmObjectiveMarks");
	Map<String, Map<String,String>> hmGoalType1Marks =(Map<String, Map<String,String>>)request.getAttribute("hmGoalType1Marks");
	Map<String, Map<String,String>> hmGoalType2Marks =(Map<String, Map<String,String>>)request.getAttribute("hmGoalType2Marks");
	 
%>
<%-- 
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Review Summary" name="title" />
	</jsp:include>
 --%>

<s:form action="#" id="formID" method="POST" theme="simple">
	<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
	        <div class="box box-primary">
	        	<div class="box-header with-border">
					<h4><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></h4>
				</div>
				 <div class="box-body" style="padding: 5px; overflow-y: auto;">
					<table class="table table_no_border">
						<%-- <tr>
							<th width="15%" align="right">Appraisal Name</th>
							<td colspan="1"><b><%=appraisalList.get(1)%>&nbsp;&nbsp;<%=appraisalList.get(5)%>&nbsp;&nbsp;<%=appraisalList.get(9)%>&nbsp;&nbsp;<%=appraisalList.get(8)%></b>
							</td>
						</tr> --%>
						<tr>
							<th width="15%" align="right">
							<%-- <a href="EditAppraisal.action?id=<%=appraisalList.get(0) %>&appsystem=appraisal" class="edit_lvl" title="Edit Exist"></a> --%>
							Appraisal Type</th>
							<td><%=appraisalList.get(14)%></td>
						</tr>
						<tr>
							<th valign="top" align="right">Description</th>
							<td colspan="1"><%=appraisalList.get(15)%></td>
						</tr>
						<tr>
							<th valign="top" align="right">Instruction</th>
							<td colspan="1"><%=appraisalList.get(16)%></td>
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
					<%
		List<List<String>> mainLevelList = (List<List<String>>) request.getAttribute("mainLevelList");

		Map<String, List<List<String>>> hmSystemLevelMp =(Map<String, List<List<String>>>)request.getAttribute("hmSystemLevelMp");

		Map<String, List<Map<String, List<List<String>>>>> levelMp = (Map<String, List<Map<String, List<List<String>>>>>) request.getAttribute("levelMp");
		UtilityFunctions uF = new UtilityFunctions();
		%>
			<h4><img src="images1/bottom-right-arrow.png">Sections&nbsp;<span class="badge bg-blue"><%=mainLevelList != null ? mainLevelList.size() : "0" %></span></h4>
            <%for (int a = 0; mainLevelList != null && a < mainLevelList.size(); a++) {
				List<String> maininnerList = mainLevelList.get(a);
			%>
			<div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title">
                    	
                    	<%=a+1 %>)&nbsp;<%=maininnerList.get(1)%> 
                    	<%=uF.showData(maininnerList.get(2), "")%>
                    </h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;">
                    <%List<List<String>> outerList1 =hmSystemLevelMp.get(maininnerList.get(0));
				for (int i = 0; outerList1 != null && i < outerList1.size(); i++) {
						List<String> innerList1 = outerList1.get(i);
						if (uF.parseToInt(innerList1.get(3)) == 2) {
							List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
							Map<String, List<List<String>>> scoreMp = list.get(0);
			%>
			<div style="overflow: auto; margin-top: 30px; border: 1px solid #EEEEEE;">
				<div style="text-align:left;">
					<%-- <blockquote><strong><%=a %>.<%=i %>) Other</strong> --%>
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
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
					if(hmTemp!=null){
				%>
					<%=hmorientationMembers.get(str)%> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet")%>,				
				<%
					}
				}
				%>
				</span>
				</blockquote>
				</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 100%;">
				
					<table class="table table-bordered" style="width: 100%; float: left;">
						<tr>
							<td width="90%"><b>Question</b></td>
							<td><b>Weightage</b></td>
							<!-- <th>Description</th> -->
							<% 
			Set setms1 = hmScoreDetailsMap.keySet();
			Iterator itms1 = setms1.iterator();
			while(itms1.hasNext()){
				String str = (String)itms1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str); 
				if(hm==null)hm=new HashMap();
				%>
			<td><b><%=uF.showData(hmorientationMembers.get(str), "")%></b></td>
			<%
			}
			%>
						</tr>
						<%
							List<List<String>> goalList = scoreMp.get(innerList1.get(0));
										for (int k = 0; goalList != null && k < goalList.size(); k++) {
											List<String> goalinnerList = goalList.get(k);
						%>
						<tr>
							<td><%=a+1 %>.
							<%if(innerList1.get(1) != null && !innerList1.get(1).equals("")){ %>
							<%=i+1 %>.
							<%} %>
							<%=k+1%>)&nbsp;<%=goalinnerList.get(0)%></td>
									
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
											//System.out.println("goalinnerList.get(6) ---> "+goalinnerList.get(6));
											//System.out.println("hm.get(goalinnerList.get(6)) ---> "+hm.get(goalinnerList.get(6)));
											String strPercent = hm.get(goalinnerList.get(6)) != null ? hm.get(goalinnerList.get(6)).toString().replace("%","") : "0";
											double percent = uF.parseToDouble(strPercent);
											totPercent += percent;
										}
										avgPercent = totPercent / roleCnt;
										%>
										
							<td style="text-align: right"><%=avgPercent%>%</td>						
						<%
													Set set2 = hmScoreDetailsMap.keySet();
													Iterator it2 = set2.iterator();
													while(it2.hasNext()){
														String str = (String)it2.next();
														Map hm = (Map)hmScoreDetailsMap.get(str);
														if(hm==null)hm=new HashMap();
														%>
													<td>
														<%=uF.showData((String)hm.get(goalinnerList.get(6)), "Na")%>
													</td>	
													<%
													}
													%>
													</tr>
						<%
							}
						%>
					</table>
				
			</div>
			</div>
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(3)) == 1) {

							if (uF.parseToInt(innerList1.get(2)) == 1) {
								
								int nVar1 = 2 * (hmScoreDetailsMap.size()*4);
								int nVar = 71 * (hmScoreDetailsMap.size()*4);
								/* System.out.println("1 nVar=====> "+nVar);
								System.out.println("1 nVar1=====> "+nVar1); */
								
								//int nWidth1 = 1545 + nVar;
								int nWidth1 = 1900 + nVar;
								
								int nWidth4 = 1203 + nVar;
								//int nWidth4 = 1000 + nVar;
								
								//int nWidth3 = 927 + nVar;
								int nWidth3 = 606 + nVar;
								
								//int nWidth4 = 618 + nVar;
								//int nWidth4 = 9 + nVar;
								
								//int nWidth5 = 310 + nVar;
								int nWidth5 = nVar-587;
								
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
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
					Map hmTemp = (Map)hmScoreAggregateMap.get(str);
					if(hmTemp!=null){
				%>
					<%=hmorientationMembers.get(str)%> - <%=uF.showData((String)hmTemp.get(innerList1.get(0)), "Not Rated yet")%>,				
				<%
					}
				}
				%>
				</span>
					</blockquote>
				</div>
			<div style="display: block; width: <%=nWidth1 + nVar1%>px; ">
			<%-- <div style="display: block; width: <%=nWidth1%>px; "> --%>
			
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 240px;  text-align: center;">
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
<%
			Set setms1 = hmScoreDetailsMap.keySet();
			Iterator itms1 = setms1.iterator();
			while(itms1.hasNext()){
				String str = (String)itms1.next();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 240px;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
				<b>Weightage</b>
			</div>
			<%
			Set setg1 = hmScoreDetailsMap.keySet();
			Iterator itg1 = setg1.iterator();
			while(itg1.hasNext()){
				String str = (String)itg1.next();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 240px;  text-align: center;">
				<b>Objective </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
			<% 
			Set seto1 = hmScoreDetailsMap.keySet();
			Iterator ito1 = seto1.iterator();
			while(ito1.hasNext()){
				String str = (String)ito1.next();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
			
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 240px;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
			<%
			Set setm1 = hmScoreDetailsMap.keySet();
			Iterator itm1 = setm1.iterator();
			while(itm1.hasNext()){
				String str = (String)itm1.next();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
			
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 240px;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 61px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
			
			<%
			Set set1 = hmScoreDetailsMap.keySet();
			Iterator it1 = set1.iterator();
			while(it1.hasNext()){
				String str = (String)it1.next();
			%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 59px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
		</div>	
			<!-- <div style="overflow: hidden; float: left; width: 100%;"> -->
				<%
				//String aa
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: <%=nWidth1%>px;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 242px;">
						<p style="padding-left:10px"><%=innerList.get(1)%></p>
						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 66px; text-align: right;">
						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>
					</div>
					
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet(); //aaaaa
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()){
							String str = (String)itst12.next();
							Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(innerList.get(2));
							double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>
					

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth4%>px;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
						%>



						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 242px">
								
									<p style="padding-left:10px"><%=goalinnerList.get(1)%></p>
								
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 66px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
							
							<%
						Set<String> setg12 = hmGoalType1Marks.keySet(); //aaaa
						Iterator<String> itg12 = setg12.iterator();
						while(itg12.hasNext()){
							String str = (String)itg12.next();
							Map<String,String> hm = (Map<String,String>)hmGoalType1Marks.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(goalinnerList.get(2));
							double goalT1Total=st1weightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(goalT1Total), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>
								
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;">
								<%
									List<List<String>> objectiveList = objectiveMp.get(goalinnerList.get(0));
															for (int l = 0; objectiveList != null && l < objectiveList.size(); l++) {
																List<String> objectiveinnerList = objectiveList.get(l);
								%>

								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 242px;">
										<p style="padding-left:10px"><%=objectiveinnerList.get(1)%></p>										
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 66px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=objectiveinnerList.get(2)%>%</p>
									</div>
									
									<%
						Set<String> setob12 = hmObjectiveMarks.keySet(); //aaaaaa
						Iterator<String> itob12 = setob12.iterator();
						while(itob12.hasNext()){
							String str = (String)itob12.next();
							Map<String,String> hm = (Map<String,String>)hmObjectiveMarks.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(objectiveinnerList.get(2));
							double objectiveTotal=st1weightage*(uF.parseToDouble(hm.get(objectiveinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(objectiveTotal), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>
									<div
										style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth4%>px;">
										<%
											List<List<String>> measureList = measureMp.get(objectiveinnerList.get(0));
													for (int m = 0; measureList != null && m < measureList.size(); m++) {
														List<String> measureinnerList = measureList.get(m);
										%>

										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 242px;">
												<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
											</div>

											<div
												style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 66px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
											</div>
											 
						<% 
						Set<String> setms2 = hmMeasureUserScore.keySet(); //ccccccc
						Iterator<String> itms2 = setms2.iterator();
						while(itms2.hasNext()){
							String str = (String)itms2.next();
							Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
							if(hm==null) hm=new HashMap<String, String>();							
							double mweightage=uF.parseToDouble(measureinnerList.get(2));
							double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>

											<div style="overflow: hidden; float: left; width: <%=nWidth5%>px;">
												<%
													List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
														for (int n = 0; questionList != null && n < questionList.size(); n++) {
															List<String> question1List = questionList.get(n);
																						
												%>
												<div style="overflow: hidden; float: left; width: 100%;">
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 242px;">
															<p style="padding-left:10px"><%=question1List.get(0)%></p>
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
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
														<p style="margin: 0px 10px 0px 0px;"><%=uF.formatIntoTwoDecimalWithOutComma(avgPercent)%>%</p>
													</div>
													<%
													Set set2 = hmScoreDetailsMap.keySet();
													Iterator it2 = set2.iterator();
													while(it2.hasNext()){
														String str = (String)it2.next();
														Map hm = (Map)hmScoreDetailsMap.get(str);
														if(hm==null)hm=new HashMap();
														%>
													<div
														style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
														<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
													</div>	
													<% } %>
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
				
				<% //String asasa
					 }
				%>
			</div>
			<!-- </div> -->

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 2) {
					
								/* int nVar1 = 2 * (hmScoreDetailsMap.size()+hmMeasureUserScore.size());
								int nVar = 71 * (hmScoreDetailsMap.size()+hmMeasureUserScore.size()); */
								int nVar1 = 2 * (hmScoreDetailsMap.size()*2);
								int nVar = 71 * (hmScoreDetailsMap.size()*2);
								//System.out.println("nVar=====>"+nVar);
								int nWidth1 = 1574 + nVar;
								//int nWidth1 = 1024 + nVar;
								int nWidth4 = 882 + nVar;
								//int nWidth3 = 445 + nVar;
								int nWidth3 = 182 + nVar;
					
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
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
					</blockquote>
				</div>
			
			<%-- <div style="display: block; width: <%=nWidth1 + nVar1%>px; "> --%>
			<div style="display: block; width: <%=nWidth1%>px; ">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 370px;  text-align: center;">
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>
			<%
			Set setms1 = hmScoreDetailsMap.keySet();
			Iterator itms1 = setms1.iterator();
			while(itms1.hasNext()){
				String str = (String)itms1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 370px;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>
			<%
			Set setm1 = hmScoreDetailsMap.keySet();
			Iterator itm1 = setm1.iterator();
			while(itm1.hasNext()){
				String str = (String)itm1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 60px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 410px;  text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>

			<%
			Set set1 = hmScoreDetailsMap.keySet();
			Iterator it1 = set1.iterator();
			while(it1.hasNext()){
				String str = (String)it1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 63px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
		
			<div style="overflow: hidden; float: left; width: 100%;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {
										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: <%=nWidth1%>px;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 372px;">
							<p style="padding-left:10px"><%=innerList.get(1)%></p>
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 71px; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet();
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()){
							String str = (String)itst12.next();
							Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(innerList.get(2));
							double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>


					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth4%>px;"> <!-- 1450 -->
						<%
							List<List<String>> measureList = measureMp.get(innerList.get(0));
												for (int k = 0; measureList != null && k < measureList.size(); k++) {
													List<String> measureinnerList = measureList.get(k);
						%>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 372px;">
								<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
						</div>

						<div
							style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 70px; text-align: right;">
							<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
						</div>
						<%
						Set<String> setms2 = hmMeasureUserScore.keySet();
						Iterator<String> itms2 = setms2.iterator();
						while(itms2.hasNext()){
							String str = (String)itms2.next();
							Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							double mweightage=uF.parseToDouble(measureinnerList.get(2));
							//System.out.println(str+" mweightage====> "+mweightage);
							//System.out.println(str+" hm.get(measureinnerList.get(0)====> "+hm.get(measureinnerList.get(0)));
							//System.out.println(str+" uF.parseToDouble(hm.get(measureinnerList.get(0)))/100====>"+uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 61px; text-align: right;">
								<%-- <p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(measureinnerList.get(0)), "Na")%></p> --%>
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>

						<div
							style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;"> <!-- 736 -->

							<%
								List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
														for (int l = 0; questionList != null && l < questionList.size(); l++) {
															List<String> question1List = questionList.get(l);
							%>
							<div style="overflow: hidden; float: left; width: 100%;">
								<div
									style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 415px;">
										<p style="padding-left:10px"><%=question1List.get(0)%></p>
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
								<div
									style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.formatIntoTwoDecimalWithOutComma(avgPercent)%>%</p>
								</div>
								
								<%
								Set set2 = hmScoreDetailsMap.keySet();
								Iterator it2 = set2.iterator();
								while(it2.hasNext()){
									String str = (String)it2.next();
									Map hm = (Map)hmScoreDetailsMap.get(str);
									if(hm==null)hm=new HashMap();
									%>
								<div
									style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 60px; text-align: right;">
									<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
								</div>	
								<%
								}
								%>
								
							</div>
							<%
								}
							%>
						</div>

						<%
							}
						%>
					</div>
				</div>
				</div>
				<%
					}
				%>

			</div>
			</div>

			<%
				} else if (uF.parseToInt(innerList1.get(2)) == 3) {
					
						
								int nVar1 = 2 * (hmScoreDetailsMap.size()*4);
								int nVar = 71 * (hmScoreDetailsMap.size()*4);
								//System.out.println("3 nvar====>"+nVar);
								//int nWidth1 = 1340 + nVar;
								int nWidth1 = 1412 + nVar;
								
								//int nWidth4 = 1004 + nVar;
								int nWidth4 = 791 + nVar;
								
								//int nWidth3 = 670 + nVar;
								int nWidth3 = 186 + nVar;
								
								//int nWidth4 = 337 + nVar;
								//int nWidth4 = nVar-432;
								
								List<Map<String, List<List<String>>>> list = levelMp.get(innerList1.get(0));
								Map<String, List<List<String>>> scoreMp = list.get(0);
								Map<String, List<List<String>>> measureMp = list.get(1);
								Map<String, List<List<String>>> questionMp = list.get(2);
								Map<String, List<List<String>>> GoalMp = list.get(3);
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
					<%-- <blockquote><strong>Competencies + Goals + Measures</strong> --%>
					<blockquote><strong><%=a+1 %>.<%=i+1 %>)&nbsp;<%=innerList1.get(1) %></strong>
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
					
					</blockquote>
				</div>
			<div style="display: block; width: <%=nWidth1 + nVar1%>px; ">
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 260px;  text-align: center;">
				<b>Competencies</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>

<%
			Set setms1 = hmScoreDetailsMap.keySet();
			Iterator itms1 = setms1.iterator();
			while(itms1.hasNext()){
				String str = (String)itms1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>


			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 260px;  text-align: center;">
				<b>Goal </b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
			<%
			Set setg1 = hmScoreDetailsMap.keySet();
			Iterator itg1 = setg1.iterator();
			while(itg1.hasNext()){
				String str = (String)itg1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 65px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			

			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 260px;  text-align: center;">
				<b>Measure</b>
			</div>
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b>Weightage</b>
			</div>
			
			<%
			Set setm1 = hmScoreDetailsMap.keySet();
			Iterator itm1 = setm1.iterator();
			while(itm1.hasNext()){
				String str = (String)itm1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
			
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 260px; text-align: center;">
				<b>Question</b>
			</div>
			<div
				style="overflow: hidden; float: left;  border: 1px solid #eee; width: 70px; text-align: center;">
				<b>Weightage</b>
			</div>
		

			<%
			Set set1 = hmScoreDetailsMap.keySet();
			Iterator it1 = set1.iterator();
			while(it1.hasNext()){
				String str = (String)it1.next();
				Map hm = (Map)hmScoreDetailsMap.get(str);
				if(hm==null)hm=new HashMap();
				%>
			<div style="overflow: hidden; float: left; border: 1px solid #eee; width: 70px;  text-align: center;">
				<b> <%=uF.showData(hmorientationMembers.get(str), "")%></b>
			</div>	
			<%
			}
			%>
			
		</div>	
			<div
				style="overflow: hidden; float: left; border: 1px solid #eee; width: <%=nWidth1%>px;">
				<%
					List<List<String>> scoreList = scoreMp.get(innerList1.get(0));
									for (int j = 0; scoreList != null && j < scoreList.size(); j++) {

										List<String> innerList = scoreList.get(j);
				%>

				<div style="overflow: hidden; float: left; width: 100%;">
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 262px;">
						<p style="padding-left:10px"><%=innerList.get(1)%>()</p>						
					</div>
					<div
						style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 70px; text-align: right;">

						<p style="margin: 0px 10px 0px 0px;"><%=innerList.get(2)%>%</p>

					</div>
					<%
						Set<String> setst12 = hmScoreMarksType1.keySet();
						Iterator<String> itst12 = setst12.iterator();
						while(itst12.hasNext()){
							String str = (String)itst12.next();
							Map<String,String> hm = (Map<String,String>)hmScoreMarksType1.get(str);
							if(hm==null) hm=new HashMap<String, String>();
							
							double st1weightage=uF.parseToDouble(innerList.get(2));
							double scoreT1Total=st1weightage*(uF.parseToDouble(hm.get(innerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(scoreT1Total), "Na")+"%"%></p>
							</div>	
							<% 
						}
						%>
					

					<div
						style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth4%>px;">
						<%
							List<List<String>> goalList = GoalMp.get(innerList.get(0));
												for (int k = 0; goalList != null && k < goalList.size(); k++) {
													List<String> goalinnerList = goalList.get(k);
						%>

						<div style="overflow: hidden; float: left; width: 100%;">
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 262px;">
								<p style="padding-left:10px"><%=goalinnerList.get(1)%></p>
							</div>
							<div
								style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 73px; text-align: right;">

								<p style="margin: 0px 10px 0px 0px;"><%=goalinnerList.get(2)%>%</p>
							</div>
							
							<%
						Set<String> setgs2 = hmGoalType2Marks.keySet();
						Iterator<String> itgs2 = setgs2.iterator();
						while(itgs2.hasNext()){
							String str = (String)itgs2.next();
							Map<String,String> hm = (Map<String,String>)hmGoalType2Marks.get(str);
							if(hm==null) hm=new HashMap<String, String>();							
							double gweightage=uF.parseToDouble(goalinnerList.get(2));
							double goalTotal=gweightage*(uF.parseToDouble(hm.get(goalinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 66px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(goalTotal), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>
							
							
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; width: <%=nWidth3%>px;">
								<%
									List<List<String>> measureList = measureMp.get(goalinnerList.get(0));
															for (int l = 0; measureList != null && l < measureList.size(); l++) {
																List<String> measureinnerList = measureList.get(l);
								%>


								<div style="overflow: hidden; float: left; width: 100%;">
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 262px;">
											<p style="padding-left:10px"><%=measureinnerList.get(1)%></p>
									</div>
									<div
										style="overflow: hidden; float: left; border-top: 1px solid #eee; width: 71px; text-align: right;">
										<p style="margin: 0px 10px 0px 0px;"><%=measureinnerList.get(2)%>%</p>
									</div>
									<%
						Set<String> setms2 = hmMeasureUserScore.keySet();
						Iterator<String> itms2 = setms2.iterator();
						while(itms2.hasNext()){
							String str = (String)itms2.next();
							Map<String,String> hm = (Map<String,String>)hmMeasureUserScore.get(str);
							if(hm==null) hm=new HashMap<String, String>();							
							double mweightage=uF.parseToDouble(measureinnerList.get(2));
							double measureTotal=mweightage*(uF.parseToDouble(hm.get(measureinnerList.get(0)))/100);
							%>
							<div
								style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
								<p style="margin: 0px 10px 0px 0px;"><%=uF.showData(uF.formatIntoComma(measureTotal), "Na")+"%"%></p>
							</div>	
							<%
						}
						%>
									
									
									<div style="overflow: hidden; float: left; width: <%=nWidth4%>px;">
										<%
											List<List<String>> questionList = questionMp.get(measureinnerList.get(0));
																		for (int m = 0; questionList != null && m < questionList.size(); m++) {
																			List<String> question1List = questionList.get(m);
										%>
										<div style="overflow: hidden; float: left; width: 100%;">
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 262px;">
													<p style="padding-left:10px"><%=question1List.get(0)%></p>
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
											<div
												style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 71px; text-align: right;">
												<p style="margin: 0px 10px 0px 0px;"><%=uF.formatIntoTwoDecimalWithOutComma(avgPercent)%>%</p>
											</div>
											
											<%
												Set set2 = hmScoreDetailsMap.keySet();
												Iterator it2 = set2.iterator();
												while(it2.hasNext()){
													String str = (String)it2.next();
													Map hm = (Map)hmScoreDetailsMap.get(str);
													if(hm==null)hm=new HashMap();
													%>  
												<div
													style="overflow: hidden; float: left; border-left: 1px solid #eee; border-top: 1px solid #eee; width: 70px; text-align: right;">
													<p style="margin: 0px 10px 0px 0px;"><%=uF.showData((String)hm.get(question1List.get(2)), "Na")%></p>
												</div>	
												<%
												}
												%>
												
										</div>
										<%
											}
										%>
									</div>
								</div>
								<%
									}
								%>
							</div>
						</div>
						</div>
						<%
							}
						%>
			</div>		
				</div>
			
				<%
					}
				%>

			</div>
			<!-- </div> -->
			
			<%
				}
						}
					}%>
                </div>
                <!-- /.box-body -->
            </div>
				<%}
				%>
			
					
				</div>
			</div>
		</section>
    </div>
	</section>
</s:form>

		

		
	
