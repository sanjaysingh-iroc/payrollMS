<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

	<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
	<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
	<script type="text/javascript" src="scripts/_rating/js/jquery.raty.js"> </script>
	<script type="text/javascript" src="js/datatables_new/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="js/datatables_new/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript" src="js/datatables_new/dataTables.buttons.min.js"></script>
	<script type="text/javascript" src="scripts/jquery.lazyload.js"></script>

<script type="text/javascript">
	function giveRemark(id, empId, strIncumbentEmpId, strDesignation) { 
	    var dialogEdit = '.modal-body';
	    $(dialogEdit).empty();
	    $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $("#modalInfo").show();
	    $(".modal-title").html('Remark');
	    if($(window).width() >= 900) {
    		$('.modal-dialog').width(900);
    	}
	    $.ajax({
	   		url : "SuccessionPlanAction.action?id="+id+"&empid="+empId+"&strDesignation="+strDesignation+"&strIncumbentEmpId="+strIncumbentEmpId,
	   		cache : false,
	   		success : function(data) {
	   			$(dialogEdit).html(data);
	   		}
	   	});
	}
	
    $(function(){
    	/* $("#strWLocation").multiselect().multiselectfilter();
    	$("#department").multiselect().multiselectfilter(); */
    	
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
    	$("#lt").DataTable({
    		"order": [],
    		"columnDefs": [ {
    		      "targets"  : 'no-sort',
    		      "orderable": false
    		    }],
    		'dom': 'lBfrtip',
            'buttons': [
    			'copy', 'csv', 'excel', 'pdf', 'print'
            ]
      	});
    });
    
    
    function getEmpProfile(val) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Employee Profile');
		$.ajax({
			url : "AppraisalEmpProfile.action?empId=" + val,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
    
    function getDesignationDetails(desigId, desigName) {
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html(''+desigName);
    	$.ajax({
    		url : "DesignationDetails.action?desig_id=" + desigId,
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
    
    function changePlanStatus(val, empId, desigId) {
    	if(val != '') {
			var alertMsg = 'Are you sure, you want to change plan status of this Incumbent?';
			if(confirm(alertMsg)) {
				getContent('statusDiv_'+empId, 'ShowSuccessionPlan.action?operation=updatePlanStatus&planStatusName='+val+'&strEmpId='+empId+'&desigId='+desigId);
				
			}
		}
    }
</script>

<!-- Custom form for adding new records -->
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Succesion Plan" name="title"/>
    </jsp:include> --%>
    
<section class="content" style="padding: 0px 15px;">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-body"
                    style="padding: 0px; overflow-y: auto; min-height: 600px;">
                    <div id="printDiv" class="leftbox reportWidth">
                    	<div class="box box-default collapsed-box" style="margin-top: 10px;">
			                <div class="box-header with-border">
			                    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
			                    <s:form name="frmShowSuccessionPlan" action="ShowSuccessionPlan" theme="simple">
		                            <s:select theme="simple" name="designation" id="designation" listKey="desigId" listValue="desigCodeName" headerKey=""
                                        headerValue="All Designations" onchange="submitForm();" list="desigList" key=""/>
		                        </s:form>
			                </div>
			                <!-- /.box-body -->
			            </div>
                        <div>
                            <table class="table table-bordered" id="lt">
                                <thead>
                                    <tr>
                                        <th style="text-align: left; width: 250px;">Position</th>
                                        <th style="text-align: left; width: 450px;">Incumbent</th>
                                        <th style="text-align: left;">Current Rating</th>
                                        <th style="text-align: left;">Plan Status</th>
                                        <th style="text-align: left;">Number of Prospective Candidates</th>
                                        <!-- <th style="text-align: left; width: 40px;">Prospect Pic</th> -->
                                        <th style="text-align: left; width: 120px;">No. of Other Succession Plans</th>
                                        <th style="text-align: left; width: 50px;">Readiness</th>
                                        <th style="text-align: left; width: 60px;">Top Candidate</th>
                                        <th style="text-align: left; width: 50px;">Rank</th>
                                        <!-- <th style="text-align: left;">Time on Plan</th> -->
                                        <th style="text-align: left; width: 50px;">Achievable Level</th>
                                        <th style="text-align: left; width: 60px;">Retention Risk</th>
                                        <th style="text-align: left; width: 200px;">Skills</th>
                                        <!-- star rating -->
                                        <th style="text-align: left; width: 200px;">Potential</th>
                                        <!-- star rating -->
                                        <th style="text-align: left; width: 200px;">Performance</th>
                                        <!-- star rating -->
                                        <th style="text-align: left; width: 200px;">Evaluation Rating from Last Review</th>
                                        <!-- star rating -->
                                        <th style="text-align: left; width: 200px;">Promoted in last three years</th>
                                        <!-- yes no -->
                                        <th style="text-align: left; width: 50px;" class="no-sort">Actions</th>
                                        <!-- <th style="text-align: left; width: 120px;">Remove on request</th> -->
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                    UtilityFunctions uF = new UtilityFunctions();
                                        String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
                                        
                                        String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
                                        Map<String,String> hmRemark=(Map<String,String>)request.getAttribute("hmRemark");
                                        
                                        List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
                                          	Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
                                          	Map<String, List<List<String>>> hmSkillValue = (Map<String, List<List<String>>>) request.getAttribute("hmSkillValue");
                                          	Map<String, List<String>> hmPlanStatus = (Map<String, List<String>>) request.getAttribute("hmPlanStatus");
                                          	Map<String, String> hmLastReviewRating = (Map<String, String>) request.getAttribute("hmLastReviewRating");
                                          	Map<String, Map<String, List<String>>> hmDesignationwiseEmp = (Map<String, Map<String, List<String>>>) request.getAttribute("hmDesignationwiseEmp");
                                          	
                                          	Map<String, List<String>> hmDesigwiseEmpId = (Map<String, List<String>>) request.getAttribute("hmDesigwiseEmpId");
                                          	if(hmDesigwiseEmpId == null) hmDesigwiseEmpId = new HashMap<String, List<String>>();
                                          	
                                          	Map<String,String> empImageMap = (Map<String,String>)request.getAttribute("empImageMap");
                                          	Map<String, Map<String, String>> hmMainLevelDiffCount = (Map<String, Map<String, String>>) request.getAttribute("hmMainLevelDiffCount");
                                        	List<List<String>> empDataList = (List<List<String>>) request.getAttribute("empDataList");
                                        
                                        	for (int a = 0; empDataList != null && a < empDataList.size(); a++) {
                                        		List<String> cinnerlist = empDataList.get(a);
                                        		
                                        		String lastReviewRating = hmLastReviewRating.get(cinnerlist.get(0));
                                         %>
                                    <script type="text/javascript">
                                        $(function() {
                                        	
                                        	<%
                                            double allSkillValues1 = 0;
                                            List<List<String>> skillsList1 = hmSkillValue.get(cinnerlist.get(0));
                                            int skillCnt1=0;
                                            for (int i = 0; skillsList1 != null && !skillsList1.isEmpty() && i < skillsList1.size(); i++) {
                                            	List<String> alInner = skillsList1.get(i);
                                            	allSkillValues1 += uF.parseToDouble(alInner.get(2));
                                            	skillCnt1++;
                                            }
                                            double skillsValAvg1 = allSkillValues1 / skillCnt1;
                                            %>
                                        }); 
                                        
                                        
                                        $(function() {
                                        	
                                        	<%
                                            String potentialId1 ="", performanceId1 = "";
                                            for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                                            	List<String> innerList1 = elementouterList.get(i);
                                            	if(innerList1.get(1).trim().equalsIgnoreCase("Potential")){
                                            		potentialId1 = innerList1.get(0).trim();
                                            	} else if(innerList1.get(1).trim().equalsIgnoreCase("Performance")){
                                            		performanceId1 = innerList1.get(0).trim();
                                            	}
                                            }
                                            double potentialAvg1 = uF.parseToDouble(hmScoreAggregateMap.get(cinnerlist.get(0)+"_"+potentialId1));
                                            double performanceAvg1 = uF.parseToDouble(hmScoreAggregateMap.get(cinnerlist.get(0)+"_"+performanceId1));	
                                            %>
                                        });
                                        
                                    </script>
                                    <%
                                        Map<String, List<String>> hmEmpwiseData = hmDesignationwiseEmp.get(cinnerlist.get(7));
                                        List<String> empIdListDesigwise = hmDesigwiseEmpId.get(cinnerlist.get(7));
                                        List<String> empDataList11 = null;
                                        int noOfEmp = 0;
                                        //System.out.println ("hmEmpwiseData==>"+hmEmpwiseData+"==>empIdListDesigwise==>"+empIdListDesigwise);
                                        if(hmEmpwiseData != null && !hmEmpwiseData.isEmpty() && empIdListDesigwise != null && !empIdListDesigwise.isEmpty()) {
                                        empDataList11 = hmEmpwiseData.get(empIdListDesigwise.get(0));
                                        
                                        if(empIdListDesigwise != null && !empIdListDesigwise.isEmpty()) {
                                        	noOfEmp = empIdListDesigwise.size();
                                        }
                                        %>
                                    <script type="text/javascript">
                                        $(function() {
                                        	
                                        	<%
                                            double allSkillValues11 = 0;
                                            List<List<String>> skillsList11 = hmSkillValue.get(empDataList11.get(0));
                                            int skillCnt11=0;
                                            for (int i = 0; skillsList11 != null && !skillsList11.isEmpty() && i < skillsList11.size(); i++) {
                                            	List<String> alInner = skillsList11.get(i);
                                            	allSkillValues11 += uF.parseToDouble(alInner.get(2));
                                            	skillCnt11++;
                                            }
                                            double skillsValAvg11 = allSkillValues11 / skillCnt11;
                                            
                                            %>
                                        		$('#skillStars<%=cinnerlist.get(0)+a+"0"%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=skillsValAvg11/2 %>,
                                        				  half: true
                                        				});
                                        	}); 
                                        	
                                        	
                                        $(function() { 
                                        		
                                        		<%
                                            String potentialId11 ="", performanceId11 = "";
                                            for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                                            	List<String> innerList = elementouterList.get(i);
                                            	if(innerList.get(1).trim().equalsIgnoreCase("Potential")){
                                            		potentialId11 = innerList.get(0).trim();
                                            	} else if(innerList.get(1).trim().equalsIgnoreCase("Performance")){
                                            		performanceId11 = innerList.get(0).trim();
                                            	}
                                            }
                                            double potentialAvg11 = uF.parseToDouble(hmScoreAggregateMap.get(empDataList11.get(0)+"_"+potentialId11));
                                            double performanceAvg11 = uF.parseToDouble(hmScoreAggregateMap.get(empDataList11.get(0)+"_"+performanceId11));	
                                            %>
                                        		$('#potentialStars<%=cinnerlist.get(0)+a+"0"%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=potentialAvg11/20 %>,
                                        			  half: true
                                        			});
                                        		$('#performanceStars<%=cinnerlist.get(0)+a+"0"%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=performanceAvg11/20 %>,
                                        			  half: true
                                        			});
                                        		$('#lastReviewStars<%=cinnerlist.get(0)+a+"0"%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=uF.parseToDouble(lastReviewRating)/20 %>,
                                        			  half: true
                                        			});
                                         }); 
                                        
                                    </script>
                                    <% } %>
                                    <tr id=<%=cinnerlist.get(0)%>>
                                        <td valign="top">
                                        <a href="javascript:void(0);" onclick="getDesignationDetails('<%=cinnerlist.get(7) %>', '<%=cinnerlist.get(5)%>');"><%=cinnerlist.get(5)%></a>
                                        </td>
                                        <td valign="top">
	                                        <span style="float: left; margin: 0px 2px;">
							                    <%if(docRetriveLocation==null) { %>
							                      	<img class="lazy img-circle" width="22" height="22" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(cinnerlist.get(0))%>" />
							                    <% } else { %>
							                      	<img class="lazy img-circle" width="22" height="22" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+cinnerlist.get(0)+"/"+IConstants.I_60x60+"/"+empImageMap.get(cinnerlist.get(0))%>" />
							                    <% } %>
						                    </span>
					                    	<a class="users-list-name" href="javascript:void(0);" style="margin-top: 3px;" onclick="getEmpProfile('<%=cinnerlist.get(0) %>');" title="<%=cinnerlist.get(2)+" "+cinnerlist.get(3)+" "+cinnerlist.get(4) %>"><%=cinnerlist.get(2)+" "+cinnerlist.get(3)+" "+cinnerlist.get(4) %></a>
                                        </td>
                                        <%
                                            double currentRating = (skillsValAvg1 + (potentialAvg1/10) + (performanceAvg1/10)) / 3;
                                            int intcurRating = 0;
                                            if(!(currentRating+"").equals("NaN")){
                                            	intcurRating = (int)currentRating;
                                            }
                                            %>
                                        <td valign="top" align="center">
                                            <%-- <%=uF.formatIntoTwoDecimalWithOutComma(currentRating) %> --%>
                                            <%=intcurRating %>
                                        </td>
                                        <td valign="top" align="center">
                                        <% 
                                        String planStatus = null;
                                        String statusColor = "#ff1801";
                                        List<String> alPlanStatus = hmPlanStatus.get(cinnerlist.get(0));
                                        if(alPlanStatus !=null && alPlanStatus.size()>0) {
	                                        planStatus = alPlanStatus.get(0);
	                                        statusColor = alPlanStatus.get(1);
                                        }
                                        %>
	                                        <div id="statusDiv_<%=cinnerlist.get(0) %>">
	                                        	<select style="width: 100px !important; <%=((statusColor!=null) ? "background-color:" + statusColor + ";" : "")%>"
	                                          		onchange="changePlanStatus(this.value, '<%=cinnerlist.get(0) %>','<%=cinnerlist.get(7) %>');" >
	                                          		<option value="Active Status" <% if(planStatus != null && planStatus.equals("Active Status")){ %> selected <% } %> >Active</option>
	                                          		<option value="Better to Prepare" <% if(planStatus != null && planStatus.equals("Better to Prepare")){ %> selected <% } %> >Better to Prepare</option>
	                                          		<option value="Passive Status" <% if(planStatus == null || planStatus.equals("Passive Status")){ %> selected <% } %> >Passive</option>
	                                          	</select>
                                          	</div>
                                            <%-- <% if(planStatus != null && planStatus.equals("Active Status")) { %>
                                            	<img src="images1/icons/dot_green.png" title="Active Status">
                                            <%	} else if(planStatus != null && planStatus.equals("Better to Prepare")) { %>
                                            	<img src="images1/icons/dot_yellow.png" title="Better to Prepare"> 
                                            <% } else if(planStatus != null && planStatus.equals("Passive Status")) { %>
                                            	<img src="images1/icons/dot_red.png" title="Passive Status">
                                            <% } %> --%>
                                        </td>
                                        <td valign="top" align="center"><%=noOfEmp %></td>
                                        <% 
                                            if(hmEmpwiseData != null && !hmEmpwiseData.isEmpty() && empIdListDesigwise != null && !empIdListDesigwise.isEmpty()) { 
                                            
                                            	String empimg1 = uF.showData(empImageMap.get(empDataList11.get(0).trim()), "avatar_photo.png");
                                            	Map<String, String> hmLevelDiffCount1 = hmMainLevelDiffCount.get(cinnerlist.get(8));
                                            	String levelDiffCount1 = hmLevelDiffCount1.get(cinnerlist.get(8)+"_"+empDataList11.get(8));
                                            %>
                                        <td valign="top"><span style="float: left; margin: 0px 2px;">
                                            <img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"
                                                data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empDataList11.get(0).trim()+"/"+empimg1%>" />
                                            </span>
                                        <!-- </td>
                                        <td nowrap="nowrap" valign="top"> -->
                                        <a class="users-list-name" href="javascript:void(0);" style="margin-top: 3px;" onclick="getEmpProfile('<%=empDataList11.get(0) %>');" title="<%=empDataList11.get(2)+" "+empDataList11.get(3)+" "+empDataList11.get(4) %>"><%=empDataList11.get(2)+" "+empDataList11.get(3)+" "+empDataList11.get(4) %></a>
                                        </td>
                                        <td nowrap="nowrap" valign="top"><%=empDataList11.get(9) %></td>
                                        <td nowrap="nowrap" valign="top" align="center"><img
                                            src="images1/icons/hd_tick.png"
                                            style="width: 10px; height: 10px;" title="">
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=1 %></td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=uF.parseToInt(levelDiffCount1) > 0 ? (uF.parseToInt(levelDiffCount1)-1)+"-"+levelDiffCount1 : "0" %></td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <% 
                                                if(uF.parseToDouble(empDataList11.get(10))< 75) {
                                                %> <img src="images1/icons/dot_green.png" title=""> <%	
                                                } else if(uF.parseToDouble(empDataList11.get(10))>= 75 && uF.parseToDouble(empDataList11.get(10))<= 100){
                                                %> <img src="images1/icons/dot_yellow.png" title=""> <%	
                                                } else if(uF.parseToDouble(empDataList11.get(10))> 100){
                                                %> <img src="images1/icons/dot_red.png" title=""> <% } %>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="skillStars<%=cinnerlist.get(0)+a+"0"%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="potentialStars<%=cinnerlist.get(0)+a+"0"%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="performanceStars<%=cinnerlist.get(0)+a+"0"%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="lastReviewStars<%=cinnerlist.get(0)+a+"0"%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=empDataList11.get(11) %></td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <% String remark1=hmRemark.get("0"+empDataList11.get(0));
                                                if (strSessionUserType != null && (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {
                                                	if(remark1 ==null) {
                                                %> 
                                                <a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList11.get(0)%>', '<%=cinnerlist.get(0) %>', '<%=cinnerlist.get(7) %>');">Action</a>
                                            <% } else { %> 
                                            	<a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList11.get(0)%>', '<%=cinnerlist.get(0) %>', '<%=cinnerlist.get(7) %>');"><%="Action by "+remark1 %></a>
                                            <% }
                                                } else {
                                                	if(remark1 !=null) { %>
                                                	<a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList11.get(0)%>', '<%=cinnerlist.get(0) %>', '<%=cinnerlist.get(7) %>');"><%="Action by "+remark1 %></a>
                                            <% } } %>
                                        </td>
                                        <%-- <td nowrap="nowrap" valign="top" align="center"><a href="AddAndRemoveEmpFromSuccessionplan.action?empID=<%=empDataList11.get(0)%>&type=Remove">Remove</a>
                                        </td> --%>
                                        <% } else { %>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <% } %>
                                    </tr>
                                    <%
                                        if(hmEmpwiseData != null && !hmEmpwiseData.isEmpty() && empIdListDesigwise != null && !empIdListDesigwise.isEmpty()) {
                                        	
                                        	List<Double> ratingList = new ArrayList<Double>();
                                        
                                        	Map<String, String> hmLevelDiffCount = hmMainLevelDiffCount.get(cinnerlist.get(8));
                                        	
                                        	for(int j=1; empIdListDesigwise != null && j<empIdListDesigwise.size(); j++) {
                                        	List<String> empDataList1 = hmEmpwiseData.get(empIdListDesigwise.get(j));
                                        	//System.out.println("ratingEmpList.get(j) ===> " + ratingEmpList.get(j)+ " desigwiseInnerList.get(0) =====> " + desigwiseInnerList.get(0));
                                        	
                                        		//System.out.println(" in If ---- ratingEmpList.get(j) "+j+" ===> " + ratingEmpList.get(j)+ " desigwiseInnerList.get(0) =====> " + desigwiseInnerList.get(0));
                                        	String empimg = uF.showData(empImageMap.get(empDataList1.get(0).trim()), "avatar_photo.png");
                                        	
                                        	//System.out.println("hmLevelDiffCount ===> " + hmLevelDiffCount);
                                        	//System.out.println("cinnerlist.get(8)_empDataList1.get(8) ===> " + cinnerlist.get(8)+"_"+empDataList1.get(8));
                                        	String levelDiffCount = hmLevelDiffCount.get(cinnerlist.get(8)+"_"+empDataList1.get(8));
                                        	
                                        	double allSkillValue = 0;
                                        	List<List<String>> skillList = hmSkillValue.get(empDataList1.get(0));
                                        	int skillsCnt=0;
                                        	for (int i = 0; skillList != null && !skillList.isEmpty() && i < skillList.size(); i++) {
                                        		List<String> alInner = skillList.get(i);
                                        		allSkillValue += uF.parseToDouble(alInner.get(2));
                                        		skillsCnt++;
                                        	}
                                        	double skillValAvg = allSkillValue / skillsCnt;
                                        	
                                        	double potentialsAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList1.get(0)+"_"+potentialId1));
                                        	double performancesAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList1.get(0)+"_"+performanceId1));
                                        	
                                        	double currentRatings = (skillValAvg + (potentialsAvg/10) + (performancesAvg/10)) / 3;
                                        	
                                        		%>
                                    <script type="text/javascript">
                                        $(function() {
                                        	
                                        	<%
                                            double allSkillValues = 0;
                                            List<List<String>> skillsList = hmSkillValue.get(empDataList1.get(0));
                                            int skillCnt=0;
                                            for (int i = 0; skillsList != null && !skillsList.isEmpty() && i < skillsList.size(); i++) {
                                            	List<String> alInner = skillsList.get(i);
                                            	allSkillValues += uF.parseToDouble(alInner.get(2));
                                            	skillCnt++;
                                            }
                                            double skillsValAvg = allSkillValues / skillCnt;
                                            %>
                                        		$('#skillStars<%=cinnerlist.get(0)+a+j%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=skillsValAvg/2 %>,
                                        			  half: true
                                        			});
                                        }); 
                                        
                                        
                                        $(function() {
                                        	
                                        	<%
                                            String potentialId ="", performanceId = "";
                                            for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
                                            	List<String> innerList = elementouterList.get(i);
                                            	if(innerList.get(1).trim().equalsIgnoreCase("Potential")){
                                            		potentialId = innerList.get(0).trim();
                                            	} else if(innerList.get(1).trim().equalsIgnoreCase("Performance")){
                                            		performanceId = innerList.get(0).trim();
                                            	}
                                            }
                                            double potentialAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList1.get(0)+"_"+potentialId));
                                            double performanceAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList1.get(0)+"_"+performanceId));	
                                            %>
                                        		$('#potentialStars<%=cinnerlist.get(0)+a+j%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=potentialAvg/20 %>,
                                        			  half: true
                                        			});
                                        		$('#performanceStars<%=cinnerlist.get(0)+a+j%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=performanceAvg/20 %>,
                                        			  half: true
                                        			});
                                        		$('#lastReviewStars<%=cinnerlist.get(0)+a+j%>').raty({
                                        			  readOnly: true,
                                        			  start:    <%=uF.parseToDouble(lastReviewRating)/20 %>,
                                        			  half: true
                                        			});
                                        });
                                        
                                    </script>
                                    <tr>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <td valign="top"><span style="float: left; margin: 0px 2px;">
                                        <img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png"
											data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empDataList1.get(0).trim()+"/"+empimg%>" /> 
                                        </span>
                                        <!-- </td>
                                        <td nowrap="nowrap" valign="top"> -->
                                        <a class="users-list-name" href="javascript:void(0);" style="margin-top: 3px;" onclick="getEmpProfile('<%=empDataList1.get(0) %>');" title="<%=empDataList1.get(2)+" "+empDataList1.get(3)+" "+empDataList1.get(4) %>"><%=empDataList1.get(2)+" "+empDataList1.get(3)+" "+empDataList1.get(4) %></a>
                                        </td>
                                        <td nowrap="nowrap" valign="top"><%=empDataList1.get(9) %></td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <%if(j==0) { %> <img src="images1/icons/hd_tick.png"
                                                style="width: 10px; height: 10px;" title=""> <%} else { %>
                                            &nbsp; <%} %>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=j+1 %></td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=uF.parseToInt(levelDiffCount) > 0 ? (uF.parseToInt(levelDiffCount)-1)+"-"+levelDiffCount : "0" %></td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <% 
                                                if(uF.parseToDouble(empDataList1.get(10))< 75) {
                                                %> <img src="images1/icons/dot_green.png" title=""> <%	
                                                } else if(uF.parseToDouble(empDataList1.get(10))>= 75 && uF.parseToDouble(empDataList1.get(10))<= 100){
                                                %> <img src="images1/icons/dot_yellow.png" title=""> <%	
                                                } else if(uF.parseToDouble(empDataList1.get(10))> 100){
                                                %> <img src="images1/icons/dot_red.png" title=""> <% } %>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="skillStars<%=cinnerlist.get(0)+a+j%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="potentialStars<%=cinnerlist.get(0)+a+j%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="performanceStars<%=cinnerlist.get(0)+a+j%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <div id="lastReviewStars<%=cinnerlist.get(0)+a+j%>"></div>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center"><%=empDataList1.get(11) %></td>
                                        <td nowrap="nowrap" valign="top" align="center">
                                            <% String remark=hmRemark.get("0"+empDataList1.get(0));
                                                if (strSessionUserType != null && strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER)) {
                                                	if(remark ==null) {
                                                %> <a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList1.get(0)%>');">Action</a>
                                            <% } else { %> <a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList1.get(0)%>');"><%="Action by "+remark %></a>
                                            <% }
                                                } else {
                                                	if(remark !=null) { %> <a href="javascript:void(0)" onclick="giveRemark('0', '<%=empDataList1.get(0)%>');"><%="Action by "+remark %></a>
                                            <% } } %>
                                        </td>
                                        <td nowrap="nowrap" valign="top" align="center"><a
                                            href="AddAndRemoveEmpFromSuccessionplan.action?empID=<%=empDataList1.get(0)%>&type=Remove">Remove</a>
                                        </td>
                                    </tr>
                                    <%-- <div id="<%=k %>" style="float: left; width: 100%;"><span style="float:left;width:20px;height:20px;margin:2px;border:1px solid #000"><img src="userImages/<%=empimg %>" height="20"></span>  
                                        &nbsp;&nbsp; 
                                        &nbsp;&nbsp; &nbsp;&nbsp;  &nbsp;&nbsp;
                                        </div> --%>
                                    <%
                                        } %>
                                    <%
                                        }
                                        %>
                                    <%
                                        }
                                        %>
                                </tbody>
                            </table>
                        </div>
                        <div class="custom-legends">
                          <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><strong>Plan Status</strong></div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_green.png" style="padding-bottom: 7px;"> Active Status</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_yellow.png" style="padding-bottom: 7px;"> Better to Prepare</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_red.png" style="padding-bottom: 7px;"> Passive Status</div>
						  </div>
						  <br/>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><strong>Retention Risk</strong></div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_green.png" style="padding-bottom: 7px;"> below 75%</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_yellow.png" style="padding-bottom: 7px;"> 75-100%</div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info"><img src="images1/icons/dot_red.png" style="padding-bottom: 7px;"> above 100%</div>
						  </div>
						</div>
                        <%-- <jsp:include page="../common/Legends.jsp" /> --%>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </section>
    </div>
</section>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body"
                style="height: 400px; overflow-y: auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default"
                    data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script>


function submitForm(type) {
	
	var designation = "";
	if(document.getElementById("designation")){
		designation = document.getElementById("designation").value;
	} 
	var action = 'ShowSuccessionPlan.action?designation='+designation;
	
	$("#divSuccessionPlanResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		success: function(result){
        	$("#divSuccessionPlanResult").html(result);
   		}
	});
}

	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
	$(window).bind("load", function() {
	    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
	});

</script>


