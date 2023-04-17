<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

	<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript">

$(document).ready(function(){
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
});

$(document).ready(function() {
	$('#lt').DataTable({
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


function viewAttributeTraining(id,empid,tgap_id,fromPage) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Learning Details');
	 $.ajax({
			url : "AttributeTrainingDetails.action?attribute_id="+id+"&empid="+empid+"&tgap_id="+tgap_id+"&fromPage="+fromPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmDesignation = (Map<String, String>) request.getAttribute("hmDesignation");
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	Map<String, String> hmEmpGradeMap = (Map<String, String>) request.getAttribute("hmEmpGradeMap");
	Map<String, String> hmGradeMap = (Map<String, String>) request.getAttribute("hmGradeMap");
	Map<String, String> hmEmpDepartMap = (Map<String, String>) request.getAttribute("hmEmpDepartMap");
	Map<String, String> hmDepartMap = (Map<String, String>) request.getAttribute("hmDepartMap");
	
	Map<String, String> hmLocationName = (Map<String, String>) request.getAttribute("hmLocationName");
	List<List<String>> outerList = (List<List<String>>) request.getAttribute("outerList");

	Map<String, String> checkAttribute = (Map<String, String>) request.getAttribute("checkAttribute");
	Map<String, String> hmEmpOrg = (Map<String, String>) request.getAttribute("hmEmpOrg");
	String fromPage = (String)request.getAttribute("fromPage");
%>

<section class="content">
	<div class="row jscroll">
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:500px;">
						<div>
							<s:form name="frm_Search" action="TrainingGap" theme="simple">
							    <input type ="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
								<div class="box box-default collapsed-box" style="margin-top: 10px;">
					                <div class="box-header with-border">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                    <div class="row row_without_margin">
											<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
												<i class="fa fa-filter" aria-hidden="true"></i>
											</div>
											<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Training</p>
													<s:select theme="simple" name="trainingFilter" id = "trainingFilter" headerKey="3"
													headerValue="UnScheduled Training"
													list="#{'1':'Scheduled Training', '2':'Completed Training'}"
													onchange="submitFrm()"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Organisation</p>
													<s:select theme="simple" name="f_org" listKey="orgId" id="f_org"
													listValue="orgName" headerKey="" headerValue="All Organisations"
													list="organisationList" key=""
													onchange="submitFrm();"/>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Location</p>
													<s:select theme="simple" name="location" id="location" listKey="wLocationId"
													listValue="wLocationName" headerKey="" headerValue="All Locations"
													list="workLocationList" key=""
													onchange="submitFrm();" />
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Department</p>
													<s:select name="strDepart" list="departmentList" id="depart"
													listKey="deptId" listValue="deptName" headerKey=""
													headerValue="All Department" key=""
													onchange="submitFrm();"></s:select>
												</div>
												<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
													<p style="padding-left: 5px;">Level</p>
													<s:select name="strLevel" list="levelList" listKey="levelId"
													id="strLevel" listValue="levelCodeName" headerKey=""
													headerValue="All Level" key=""
													onchange="submitFrm();"></s:select>
												</div>
											</div>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
							</s:form>
						</div>
						<div class="row row_without_margin">
                        	<div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        		<ul class="site-stats-new">
									<li class="bg_lh"><strong><%=uF.parseToInt((String)request.getAttribute("unSchedule")) %></strong> <small>Unscheduled</small></li>
									<li class="bg_lh"><strong><%=uF.parseToInt((String)request.getAttribute("schedule")) %></strong> <small>Scheduled</small></li>
									<li class="bg_lh"><strong><%=uF.parseToInt((String)request.getAttribute("completed")) %></strong> <small>Completed</small></li>
								</ul>
                        	</div>
                        </div>
						
						<div class="clr margintop20"></div>
						<%
							if (outerList != null && outerList.size() != 0) {
						%>
						<!-- <table class="tb_style" width="100%" id="lt"> -->
						<table class="table table-bordered" id="lt">
							<thead>
								<tr>
									<th>Employee Name</th>
									<th>Designation</th>
									<th>Grade</th>
									<th>Department</th>
									<th>Organization</th>
									<th>Location</th>
									<th>Attribute</th>
									<th>System</th>
									<th>Actual Score</th>
									<th>Required Score</th>
									<th nowrap="nowrap">Gap <br/>(Actual Score - Required Score)</th>
									<th class="no-sort">Action</th>
									
								</tr>
							</thead>
							<tbody>
								<%
									for (int i = 0; i < outerList.size(); i++) {
											List<String> innerList = outerList.get(i);
											System.out.println("innerList --->> " + innerList);
								%>
								<tr>
									<td><%=hmEmpName.get(innerList.get(1))%></td>
									<td><%=uF.showData(hmDesignation.get(innerList.get(2)), "-")%></td>
									<td><%=uF.showData(hmGradeMap.get(hmEmpGradeMap.get(innerList.get(1).trim())), "-")%></td>
									<td><%=uF.showData(hmDepartMap.get(hmEmpDepartMap.get(innerList.get(1).trim())), "-")%></td>
									<td><%=uF.showData(hmEmpOrg.get(innerList.get(1).trim()), "-")%></td>
									<td><%=uF.showData(hmLocationName.get(innerList.get(3)), "-")%></td>
									<td><%=uF.showData(innerList.get(4), "-") %></td>
									<td><%=uF.showData(innerList.get(5), "-") %></td>
									<td align="right"><%=uF.showData(innerList.get(6), "0")%>% 
										<% String actual_score = innerList.get(6) != null ? uF.parseToDouble(innerList.get(6)) / 20 + "" : "0";%>
										<div id="firststarPrimary<%=i%>"></div> 
										<input type="hidden" id="firstgradewithrating<%=i%>" value="<%=actual_score%>" name="firstgradewithrating<%=i%>" /> 
										
									</td>
									<td align="right"><%=uF.showData(innerList.get(7), "0")%>% 
										<% String require_score = innerList.get(7) != null ? uF.parseToDouble(innerList.get(7)) / 20 + "" : "0";%>
										<div id="secondstarPrimary<%=i%>"></div> 
										<input type="hidden" id="secondgradewithrating<%=i%>" value="<%=require_score%>" name="secondgradewithrating<%=i%>" /> 
										
									</td>
									
									<td align="right"><%=uF.showData(innerList.get(10), "0")%>% 
										<% String gap_score = innerList.get(10) != null ? uF.parseToDouble(innerList.get(10)) / 20 + "" : "0";%>
									</td>
						
									<td>
										<%
											if (uF.parseToBoolean(innerList.get(8)) == true) {
										%> Scheduled 
										<%} else {
											List<String> attributeList = innerList.get(9)!=null && innerList.get(9).length()>0 ? Arrays.asList(innerList.get(9).trim().split(",")) : null;
											boolean flag = false;
											for(int x= 0; attributeList!=null && x < attributeList.size(); x++){
												if(checkAttribute.containsKey(attributeList.get(x).trim())){
													flag =true;
													break;
												}
											}
						 					if (!flag) {
						 					  if(fromPage != null && fromPage.equalsIgnoreCase("LD")) {
						 				%> 
						 						<a href="javascript:void(0)" onclick="addLearningPlan('A','<%=innerList.get(0) %>','<%=innerList.get(1) %>','<%=innerList.get(9)%>','<%=fromPage %>')" name="create<%=i%>" id="create<%=i%>">Create</a>
						 					<%} else { %>
						 						<a href="AddLearningPlan.action?operation=A&strGapId=<%=innerList.get(0) %>&strGapEmpId=<%=innerList.get(1) %>&strAttribute=<%=innerList.get(9)%>" name="create<%=i%>" id="create<%=i%>">Create</a>        
						 				   <% }
						 					 } else { %> 
						 					<a href="javascript:void(0)" onclick="viewAttributeTraining('<%=innerList.get(9)%>','<%=innerList.get(1)%>','<%=innerList.get(0)%>','<%=fromPage %>')" title="Align">Align</a> <%
						 					}
						 				}
						 				%>
									</td>
								</tr>
								<%
									}
								%>
							</tbody>
						</table>
						 <%
							for (int i = 0; i < outerList.size(); i++) {
									List<String> innerList = outerList.get(i);
									String require_score = innerList.get(7) != null ? uF.parseToDouble(innerList.get(7)) / 20 + "" : "0";
									String actual_score = innerList.get(6) != null ? uF.parseToDouble(innerList.get(6)) / 20 + "" : "0";
						%>
									<script type="text/javascript">
								
											$('#firststarPrimary<%=i%>').raty({
								        		readOnly: true,
								        		start: '<%=actual_score%>',
								        		half: true,
								        		targetType: 'number',
								        		click: function(score, evt) {
								        			$('#firstgradewithrating<%=i%>').val(score);
								        			}
								        	});
											
								        	$('#secondstarPrimary<%=i%>').raty({
								        		readOnly: true,
								        		start: '<%=require_score%>',
								        		half: true,
								        		targetType: 'number',
								        		click: function(score, evt) {
								        			$('#secondgradewithrating<%=i%>').val(score);
								        			}
								        	});
								        	
								        	
								        	
								   
									</script>
									
									
						<% } %>
						<%
							} else {
						%>
						
						<div class="nodata msg">No learning gap identified.</div>
						<%
							}
						%>
					
                </div>
	</div>
</section>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div id="viewAttributeTraining"></div>

<script>
function submitFrm() {
	
	var strTrainingType = document.getElementById("trainingFilter").value;
	var strOrg = document.getElementById("f_org").value;
	var strLocation = document.getElementById("location").value;
	var depart = document.getElementById("depart").value;
	var strLevel = document.getElementById("strLevel").value;
	var from = '<%=fromPage%>';
	var action = 'TrainingGap.action?trainingFilter='+strTrainingType+'&f_org='+strOrg+'&location='+strLocation
			+'&strDepart='+depart+'&strLevel='+strLevel+'&fromPage=LD';
	//alert("action==>"+action);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}

function addLearningPlan(op,strGapId,strGapEmpId,strAttribute,fromPage){
	//alert("training gap");
	$.ajax({
		type :'GET',
		url  :'AddLearningPlan.action?operation='+op+'&strGapId='+strGapId+'&strGapEmpId='+strGapEmpId+'&strAttribute='+strAttribute
				+'&fromPage='+fromPage,
		cache:true,
		success : function(result) { 
		
			$("#divResult").html(result);
			$(".nav-tabs-custom .nav-tabs").find("li").removeClass("active");
			$(document).find('a:contains(Learnings)').parent().addClass("active");
		}
	});
}
</script>
