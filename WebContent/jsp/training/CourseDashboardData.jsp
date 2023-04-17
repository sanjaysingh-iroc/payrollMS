<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%
    UtilityFunctions uF=new UtilityFunctions();
    String strUserType = (String) session.getAttribute("USERTYPE"); 
    String dataType = (String) request.getAttribute("dataType");
    String strCourseId = (String) request.getAttribute("strCourseId");
    String strAssessId = (String) request.getAttribute("strAssessId");
    String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
  //  System.out.println("strCourseId==>"+strCourseId+"==>strAssessId==>"+strAssessId);
 %>
 <div class="row row_without_margin clr" style="padding-bottom: 10px;">
	 <div class="box-header with-border">
	  	<div class="col-md-12 no-padding" style="margin-bottom: 15px;">
				<div class="col-md-3 no-padding">
					<input type="text" id="strSearchJob" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('<%=dataType%>');">
				</div>
	       
				<script>
			       $(function(){
			    	   $("#strSearchJob" ).autocomplete({
							source: [ <%=uF.showData(sbData,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
			  	    <%if(dataType != null && dataType.equals("C")) {%>
			     	 	<%if(strUserType != null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
				        	    
							    	<a href="javascript:void(0)" style="float:right;" onclick="addNewCourse()">
									<input type="button" class="btn btn-primary pull-right" value="Add New Course"></a>
								
							
						<% } %>
				      <% } else if(dataType != null && dataType.equals("A")) {%>
				      		   <%if(strUserType != null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
					        	 
								    	<a href="javascript:void(0)" style="float:right;" onclick="addNewAssessment()">
										<input type="button" class="btn btn-primary pull-right" value="Add New Assessment"></a>
									
								
							<% } %>
				      <% } %>
					 
				</div>
			</div>
 	    </div>
   </div>

<div class="row row_without_margin">
	<div class="col-md-3" style="padding-left: 0px;min-height: 600px;">
		<div class="box box-primary">
			<div class="box-body">
				<%if(dataType != null && dataType.equals("C")) { %>
                    <ul class="products-list product-list-in-box">
                   <%
                        List<String> courseIDList = (List<String>) request.getAttribute("courseIDList");
                   
                        Map<String, String> hmCourseData = (Map<String, String>) request.getAttribute("hmCourseData");
                        Map<String, List<String>> hmAllCourseData = (Map<String, List<String>>) request.getAttribute("hmAllCourseData");
                        Map<String, String> hmCourseDetails = (Map<String, String>) request.getAttribute("hmCourseDetails");
                                              
                        for(int i=0; courseIDList != null && i<courseIDList.size(); i++){
                        	String latestCourseId = hmCourseData.get(courseIDList.get(i));
               				if(latestCourseId != null && !latestCourseId.equals("")) { 
            		 %>
								<li class="item">
			                       <span style="float: left; width: 100%;">
			                           <%=hmCourseDetails.get(courseIDList.get(i)+"_STATUS")%>
			                           <div style="float:left;">
										<%if(uF.parseToInt(latestCourseId) == uF.parseToInt(strCourseId)) { %>
											<a href="javascript:void(0);" class="activelink" onclick="getCourseDetails('ViewCourseDetails','<%=latestCourseId%>')"><%=hmCourseDetails.get(latestCourseId.trim()+"_NAME")%></a>
											<br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
										<%} else { %>
											<a href="javascript:void(0);" onclick="getCourseDetails('ViewCourseDetails','<%=latestCourseId%>')"><%=hmCourseDetails.get(latestCourseId.trim()+"_NAME")%></a>
											<br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
										<%} %>
									 </div>
									</span>
		                           <div style="width: 2%;"> 
		                              <input type="hidden" name="hideCourseDivStatus" id="hideCourseDivStatus<%=i %>" value = "0"/>
		                                 <a href="javascript: void(0);" onclick="showAllCourses('<%=i %>');">
		                                    <span id="CdownarrowSpan<%=i %>">
		                                       <i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i>
		                                    </span>
		                                    <span id="CuparrowSpan<%=i %>" style="display: none;">
		                                       <i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i>
		                                    </span>
		                                 </a>
		                             </div>
		                             <div id="oldCoursesDIv<%=i %>" style="display: none; width: 98%;">
		                                  <ul style="width:101%; margin: 0px 0px 0px 0px;">
		                                  <%
		                                       List<String> courseIdList = hmAllCourseData.get(courseIDList.get(i));
		                                       for(int j=0; courseIdList != null && j<courseIdList.size(); j++) {
		                                          if(!latestCourseId.equals(courseIdList.get(j))) {
		                                        %>
		                                        	<li class="item">
														<span style="float: left; width: 100%;">
														    <%=hmCourseDetails.get(courseIDList.get(i)+"_STATUS")%>
														    <div style="float:left;">
																<a href="javascript:void(0);" onclick="getCourseDetails('ViewCourseDetails','<%=courseIdList.get(j)%>')"><%=hmCourseDetails.get(courseIdList.get(j).trim()+"_NAME") %></a>
																<br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
															</div>
														</span>
													</li>
		                                           
		                                       <% }
		                                       } %>
		                                       
		                                       <li class="item">
												  <span style="float: left; width: 100%;">
												  	<%=hmCourseDetails.get(courseIDList.get(i)+"_STATUS")%>
												     <div style="float:left;">
													    <a href="javascript:void(0);" onclick="getCourseDetails('ViewCourseDetails','<%=courseIDList.get(i).trim()%>')"><%=hmCourseDetails.get(courseIDList.get(i).trim()+"_NAME")%></a>
													    <br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
													</div>
												 </span>
											  </li>
		                                  </ul>
		                              </div>
			                      </li>
	                         <%} else { %>
	                                
	                                <li class="item"">
										<span style="float: left; width: 100%;">
											<%=hmCourseDetails.get(courseIDList.get(i)+"_STATUS")%>
											 <div style="float:left;">
											<%if(uF.parseToInt(courseIDList.get(i)) == uF.parseToInt(strCourseId)) { %>
												<a href="javascript:void(0);" class="activelink" onclick="getCourseDetails('ViewCourseDetails','<%=courseIDList.get(i)%>')"><%=hmCourseDetails.get(courseIDList.get(i)+"_NAME")%></a>
												<br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
											<%} else { %>
												<a href="javascript:void(0);" onclick="getCourseDetails('ViewCourseDetails','<%=courseIDList.get(i)%>')"><%=hmCourseDetails.get(courseIDList.get(i)+"_NAME")%></a>
												<br/><%=hmCourseDetails.get(courseIDList.get(i)+"_SUB") %> 
											<%} %>
										    </div>
										</span>
									</li>
	                                
                            <%}
                          } if(courseIDList==null || (courseIDList != null && courseIDList.size() == 0)) { %>
                       		 <li class="nodata msg"> No course added yet. </li>
                       <% } %>
 					</ul>                      
                <%} else if(dataType != null && dataType.equals("A")) { %>
               		  <ul class="products-list product-list-in-box">
                      <%
                          List<String> assessmentIDList = (List<String>) request.getAttribute("assessmentIDList");
                          Map<String, String> hmAssessmentData = (Map<String, String>) request.getAttribute("hmAssessmentData");
                          Map<String, List<String>> hmAllAssessmentData = (Map<String, List<String>>) request.getAttribute("hmAllAssessmentData");
                          Map<String, String> hmAssessmentDetails = (Map<String, String>) request.getAttribute("hmAssessmentDetails");
                          
                          
                          for(int i=0; assessmentIDList != null && i<assessmentIDList.size(); i++){
                          	String latestAssessmentId = hmAssessmentData.get(assessmentIDList.get(i));
                          	if(latestAssessmentId != null && !latestAssessmentId.equals("")) {
                          %>
                               <li class="item">
                              	 <span style="float: left; width: 100%;">
                              	    <%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_STATUS") %>
                              	     <div style="float:left;">
									<%if(uF.parseToInt(latestAssessmentId) == uF.parseToInt(strAssessId)) { %>
										<a href="javascript:void(0);" class="activelink" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=latestAssessmentId%>')"><%=hmAssessmentDetails.get(latestAssessmentId.trim()+"_NAME")%></a>
										<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
									<%} else { %>
										<a href="javascript:void(0);" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=latestAssessmentId%>')"><%=hmAssessmentDetails.get(latestAssessmentId.trim()+"_NAME")%></a>
										<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
									<%} %>
									</div>
								 </span>
                           		  <div style="width: 2%;"> 
	                               	  <input type="hidden" name="hideAssessmentDivStatus" id="hideAssessmentDivStatus<%=i %>" value = "0"/>
	                              		  <a href="javascript: void(0);" onclick="showAllAssessments('<%=i %>');">
	                               		 <span id="AdownarrowSpan<%=i %>">
	                               			<i class="fa fa-angle-down" aria-hidden="true" style="width: 14px;"></i> 
	                                	     </span>
	                                 	<span id="AuparrowSpan<%=i %>" style="display: none;">
	                                	     <i class="fa fa-angle-up" aria-hidden="true" style="width: 14px;"></i> 
	                                    </span>
	                               	  </a> 
                           	   	  </div>
                               	
	                              <div id="oldAssessmentsDIv<%=i %>" style="display: none; width: 98%;">
                                      <ul style=" width:101%; margin: 0px 0px 0px 0px;">
                                        <%
                                            List<String> assessmentIdList = hmAllAssessmentData.get(assessmentIDList.get(i));
                                            for(int j=0; assessmentIdList != null && j<assessmentIdList.size(); j++) {
                                            	if(!latestAssessmentId.equals(assessmentIdList.get(j))){
                                            %>
                                            		<li class="item">
														<span style="float: left; width: 100%;">
														    <%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_STATUS") %>
															<div style="float:left;">
																<a href="javascript:void(0);" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=assessmentIdList.get(j)%>')"><%=hmAssessmentDetails.get(assessmentIdList.get(j).trim()+"_NAME") %></a>
																<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
															</div>
														</span>
													</li>
                                       			 
                                       		 <% } 
                                            } %>
                                            
                                            <li class="item">
												  <span style="float: left; width: 100%;">
												     <%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_STATUS") %>
												     <div style="float:left;">
													 	<a href="javascript:void(0);" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=assessmentIDList.get(i).trim()%>')"><%=hmAssessmentDetails.get(assessmentIDList.get(i).trim()+"_NAME")%></a>
														<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
													</div>
												 </span>
											  </li>
                                       </ul>
	                              </div>
                         	  </li>
                          <%} else {%>
                          		<li class="item"">
									<span style="float: left; width: 100%;">
									     <%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_STATUS") %>
									     <div style="float:left;">
											<%if(uF.parseToInt(assessmentIDList.get(i)) == uF.parseToInt(strAssessId)) { %>
												<a href="javascript:void(0);" class="activelink" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=assessmentIDList.get(i)%>')"><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_NAME")%></a>
												<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
											<%} else { %>
												<a href="javascript:void(0);" onclick="getAssessmentDetail('ViewAssessmentDetails','<%=assessmentIDList.get(i)%>')"><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_NAME")%></a>
												<br/><%=hmAssessmentDetails.get(assessmentIDList.get(i)+"_SUB") %> 
											<%} %>
									    </div>
									</span>
								</li>
                            
                          <%}
                         } 
                         
                          if(assessmentIDList==null || (assessmentIDList != null && assessmentIDList.size() == 0)) {
                        %>
                         	<li class="nodata msg"> No assessment added yet. </li>
                        <% } %>
                    </ul>
                <%} %>
	      		</ul>
     		</div>
 		</div>
 	</div>    
  
	<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
		<div class="box box-primary" style="min-height: 600px;" id="actionResult">
		  <div class="box-body">
                <div class="active tab-pane" id="subDivCDResult" style="min-height: 600px;">
		
                </div>
            </div> 
	  </div>
   </div>
</div>
<div class="custom-legends">
	<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Live</div></div>
	<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp;Waiting for live</div></div>
</div>
<script>
 $(document).ready(function(){
	 var dataType = '<%=dataType%>';
	// alert("dataType==>"+dataType);
	 if(dataType != "" && dataType == "C") {
		 getCourseDetails('ViewCourseDetails','<%=strCourseId%>');
	 } else {
		 getAssessmentDetail('ViewAssessmentDetails','<%=strAssessId%>');
	 }
 });
 
 function getCourseDetails(strAction,courseId) {
     $("#subDivCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
 	$.ajax({
			url : strAction+ ".action?courseId="+courseId+"&fromPage=LD",
			cache : false,
			success : function(data) {
				$("#subDivCDResult").html(data);
			}
		});
 }
 
 function getAssessmentDetail(strAction,assessmentId) {
 	$("#subDivCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
 	$.ajax({
			url : strAction+".action?assessmentId="+assessmentId+"&fromPage=LD",
			cache : false,
			success : function(data) {
				$("#subDivCDResult").html(data);
			}
		});
 }
 
	function addNewCourse(){
		$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
		$.ajax({
			type:'POST',
			url:'AddNewCourse.action?operation=A',
	        cache:true,
			success:function(result){
				//alert("add course result2==>"+result);
				$("#divCDResult").html(result);
			}
		});
	}  
  
	function addNewAssessment(){
		$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
		$.ajax({
			type:'POST',
			url:'AddNewAssessment.action?operation=A',
	        cache:true,
			success:function(result){
				//alert("add assessment result2==>"+result);
				$("#divCDResult").html(result);
			}
		});
	}
	
	function submitForm(type) {
		var strSearch = document.getElementById("strSearchJob").value;
		//alert("strSearch==>"+strSearch);
		$("#divCDResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'CourseDashboardData.action?dataType='+type+'&strSearchJob='+strSearch,
			success: function(result){
	        	$("#divCDResult").html(result);
	        }
		});
		
	}
</script>