<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.performance.FillAttribute"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>

<style>
.factsheet-link a{
	color: #fff;
	/* top: 60px; */
}
</style>


<%
    List alEmpCompareData = (List)request.getAttribute("alEmpCompareData");
    List alEmployeeNames = (List)request.getAttribute("alEmployeeNames");
    List alGrossComp = (List)request.getAttribute("alGrossComp");
    List alLoggedHours = (List)request.getAttribute("alLoggedHours");
	
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
    List<List<String>> attriblist = (List<List<String>>) request.getAttribute("attriblist");
    UtilityFunctions uF=new UtilityFunctions();
	
    String dataType = (String) request.getAttribute("dataType"); 
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	//EncryptionUtils EU = new EncryptionUtils();//Created by Dattatray Date : 21-07-21 Note:Encryption 
    
    %>
                   <% if(alEmpCompareData!=null && alEmpCompareData.size()!=0) {
                	   int empCnt=0;
                	   
                       for(int i=0; i<alEmpCompareData.size(); i++) {	
                    	   empCnt++;
                    	   int cnt=0;
                            List<String> alInner = (List<String>)alEmpCompareData.get(i); 
                    		//List<String> empPerformanceAttribWiseList =(List<String>) request.getAttribute("empPerformanceAttribWiseList");
                    		Map<String, String> hmEmpPerformanceAvg = (Map<String, String>) request.getAttribute("hmEmpPerformanceAvg");
                    		if(hmEmpPerformanceAvg == null) hmEmpPerformanceAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpListOverallAvg = (Map<String, String>) request.getAttribute("hmEmpListOverallAvg");
                    		if(hmEmpListOverallAvg == null) hmEmpListOverallAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpSkillAvg = (Map<String, String>) request.getAttribute("hmEmpSkillAvg");
                    		if(hmEmpSkillAvg == null) hmEmpSkillAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpReviewAvg = (Map<String, String>) request.getAttribute("hmEmpReviewAvg");
                    		if(hmEmpReviewAvg == null) hmEmpReviewAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpKRAAvg = (Map<String, String>) request.getAttribute("hmEmpKRAAvg");
                    		if(hmEmpKRAAvg == null) hmEmpKRAAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpGoalsAvg = (Map<String, String>) request.getAttribute("hmEmpGoalsAvg");
                    		if(hmEmpGoalsAvg == null) hmEmpGoalsAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpTargetAvg = (Map<String, String>) request.getAttribute("hmEmpTargetAvg");
                    		if(hmEmpTargetAvg == null) hmEmpTargetAvg = new HashMap<String, String>();
                    		
                    		Map<String, String> hmEmpGoalsKRATargetsAvg = (Map<String, String>) request.getAttribute("hmEmpGoalsKRATargetsAvg");
                    		if(hmEmpGoalsKRATargetsAvg == null) hmEmpGoalsKRATargetsAvg = new HashMap<String, String>();
                    		
			%>
			<% if(empCnt==1) { %>
			<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
			<% } %>
			<div class="col-lg-4 col-md-6 col-sm-12 paddingright0">
			<div class="box box-widget widget-user-2">
	            <div class="widget-user-header" style="max-height: 110px; min-height: 105px;">
	            	<!-- Created by Dattatray Date : 21-07-21 Note:empId Encrypt  -->
	            	<div class="factsheet-link pull-right" style="margin-right: 15px;"><a class="factsheet" href="MyProfile.action?empId=<%=alInner.get(10) %>"> </a></div>
	              	<div class="widget-user-image">
	              	<%if(docRetriveLocation==null) { %>
						<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInner.get(0)%>">
	              	<%} else { %>
	              		<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+alInner.get(10)+"/"+IConstants.I_100x100+"/"+alInner.get(0)%>">
	              	<%} %>
		            </div>
		              <h3 class="widget-user-username" title="<%=alInner.get(1)%>"><%=alInner.get(1)%></h3>
		              <h3 class="widget-user-username" title="<%=alInner.get(2)%>"><%=alInner.get(2)%></h3>
		              <h5 class="widget-user-desc" title="<%=alInner.get(3)%>"><%=alInner.get(3)%></h5>
		              <h5 class="widget-user-desc" title="<%=alInner.get(5)%>[<%=alInner.get(6)%>]"><%=alInner.get(5)%>[<%=alInner.get(6)%>]</h5>
	            </div>
	            
	            <div class="box-footer no-padding">
	              <ul class="nav nav-stacked" style="min-height: 165px;"> <!-- overflow-y: auto; -->
	                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Date of Joining: <span class="pull-right badge bg-blue"><%=alInner.get(7)%></span></a></li>
	                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Reporting Manager: <span class="pull-right"><%=uF.showData(alInner.get(4), "NA") %></span></a></li>
	                <%if(request.getAttribute("GC")!=null) {%>
                             <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Gross Compensation:<span class="pull-right"><%=uF.showData(alInner.get(8), "0")%></span></a></li>
                             <%} %>
	                <%if(request.getAttribute("LH")!=null) { %>
                             <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Logged Hours:<span class="pull-right"><%=uF.showData(alInner.get(9), "0")%></span></a></li>
                             <%} %>
                             <%if(request.getAttribute("SKILL")!=null) { %>
                             <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Skills:
                             <span class="pull-right">
                             	<%if(hmEmpSkillAvg == null || hmEmpSkillAvg.isEmpty() || hmEmpSkillAvg.get(alInner.get(10)+"_AVG")==null || uF.parseToDouble(hmEmpSkillAvg.get(alInner.get(10)+"_AVG")) == 0) { %>
                                 <%="NA" %>
                                 <% } else { %>
                                 <span id="starSkill<%=alInner.get(10)%>"></span>
                                 <script type="text/javascript">
                                     $(function() {
                                     	$('#starSkill<%=alInner.get(10)%>').raty({
                                     		readOnly: true,
                                     		start: <%=uF.showData(hmEmpSkillAvg.get(alInner.get(10)+"_AVG"),"0.00")%>,
                                     		half: true,
                                     		targetType: 'number'
                                     	});
                                     	});
                                 </script>
                                 <% } %>
                             </span></a></li>
                             <%} %>
	                <%if(request.getAttribute("REVIEW")!=null) { %>
	                	<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Reviews:
	                	<span class="pull-right">
	                		<%if(hmEmpReviewAvg == null && hmEmpReviewAvg.isEmpty() && hmEmpReviewAvg.get(alInner.get(10))==null) { %>
								<%="NA" %>
							<% } else { %>
							<span id="starREVIEW<%=alInner.get(10)%>"></span> 
								<script type="text/javascript">
							        $(function() {
							        	$('#starREVIEW<%=alInner.get(10)%>').raty({
							        		readOnly: true,
							        		start: <%=uF.showData(hmEmpReviewAvg.get(alInner.get(10)),"0.00")%>,
							        		half: true,
							        		targetType: 'number'
							        	});
							        	});
							        </script>
							  <% } %> 
	                	</span></a></li>     
						<% } %>
						
						
						<%if(request.getAttribute("GOAL_KRA_TARGET")!=null) { %>
						<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Goals/ BSC/ KRAs/ Targets:<span class="pull-right">
							<%if(hmEmpGoalsKRATargetsAvg == null || hmEmpGoalsKRATargetsAvg.isEmpty() || hmEmpGoalsKRATargetsAvg.get(alInner.get(10))==null) { %>
								<%="NA" %>
							<% } else { %>	
							<span id="starGoals<%=alInner.get(10)%>"></span> 
								<script type="text/javascript">
							        $(function() {
							        	$('#starGoals<%=alInner.get(10)%>').raty({
							        		readOnly: true,
							        		start: <%=uF.showData(hmEmpGoalsKRATargetsAvg.get(alInner.get(10)),"0.00")%>,
							        		half: true,
							        		targetType: 'number'
							        	});
							        	});
							        </script>
							  <% } %> 
						</span></a></li>     
						<%} %>

                              <%if(request.getAttribute("AT")!=null) { %>
                              <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Attributes: <span class="pull-right">
                              <%if(hmEmpListOverallAvg == null || hmEmpListOverallAvg.isEmpty() || hmEmpListOverallAvg.get(alInner.get(10)) == null) { %>
                                  <%="NA" %>
                                  <% } else { %>
                                  <span id="starATOverall<%=alInner.get(10)%>"></span>
                                  <script type="text/javascript">
                                      $(function() {
                                      	$('#starATOverall<%=alInner.get(10)%>').raty({
                                      		readOnly: true,
                                      		start: <%=uF.showData(hmEmpListOverallAvg.get(alInner.get(10)), "0.00")%>,
                                      		half: true,
                                      		targetType: 'number'
                                      	});
                                      	});
                                  </script>
                                  <%} %>
                              </span></a></li>
                              <%
                                  for(int ii=0; attriblist != null && !attriblist.isEmpty() && ii< attriblist.size();ii++) { 
                                  	List<String> innList=attriblist.get(ii);
                                  %>
                                  <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;"><%=innList.get(1) %>:<span class="pull-right">
                                  <%if(hmEmpPerformanceAvg == null || hmEmpPerformanceAvg.isEmpty() || hmEmpPerformanceAvg.get(alInner.get(10)+"_"+innList.get(0))==null){ %>
                                  <%="NA" %>
                                  <%}else{ %>
                                  <span id="starPrimary<%=alInner.get(10)+innList.get(0)%>"></span>
                                  <script type="text/javascript">
                                      $(function() {
                                      	$('#starPrimary<%=alInner.get(10)+innList.get(0)%>').raty({
                                      		readOnly: true,
                                      		start: <%=uF.showData(hmEmpPerformanceAvg.get(alInner.get(10)+"_"+innList.get(0)), "0.00")%>,
                                      		half: true,
                                      		targetType: 'number'
                                      	});
                                      });
                                  </script>
                                  <% } %>
                                  </span></a></li>
                              
                                <% }
                                } %>
			              </ul>
			              </div>
		            </div>
		          </div>
		          <% if(empCnt==3) { 
		        	  empCnt=0;
		          %>
		          	</div>
		          <% } %>
		
                       <% } %>
                   
                   
                   <div class="clr"></div>
                   <div id="container" style="width: 100%; height: 500px"></div>
                   <script type="text/javascript">
                     $(function() {
                       	var chartSkill;
                       	chartSkill = new Highcharts.Chart({
                       		chart : {
                       			renderTo : 'container',
                       			defaultSeriesType : 'column',
                       			plotBorderWidth : 1
                       		},
                       		credits : {
                       			enabled : false
                       		},
                       		title : {
                       			text : 'Employee Capital Comparison'
                       		},
                       		xAxis : {
                       			categories : <%=alEmployeeNames%>
                         },
                       		yAxis : {
                       			lineWidth : 2, //y axis itself
                       			title : {
                       			text : ''
                       			}
                       		},
                       		credits : {
                       			enabled : false
                       		},
                       		title : {
                       			text : '',
                       			floating : true
                       		},
                       		plotOptions : {
                       			bar : {
                       				pointPadding : 0.2,
                       				borderWidth : 0
                       			}
                       		},
                       		series : [{
                       			name : 'Gross Compensation',
                       			data : <%=alGrossComp %>
                       		}, {
                       			name : 'Logged Efforts',
                       			data : <%=alLoggedHours %>
                       		}]
                       	});
                       });
                   </script>
                   <% } else { %>
                   <div class="nodata msg">
                       <span>You have not selected any employee from the list.</span>
                   </div>
                   <%}%>
