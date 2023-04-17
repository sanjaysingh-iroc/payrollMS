<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 --%>
<script type="text/javascript">
	$(function() {
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
    
    
    /* function editSuccessionplanCriteria(orgid, desigId, criteriaId, userscreen, navigationId, toPage) { 
    
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Edit Succession Plan Criteria');
    	if($(window).width() >= 1000){
    		$(".modal-dialog").width(1000);
    	}
    	$.ajax({
			url : 'AddSuccessionPlanCriteria.action?orgId='+orgid+'&desigId='+desigId+'&criteriaId='+criteriaId+'&operation=E&userscreen='+userscreen
					+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    } */
    	
    	
    /* function addSuccessionplanCriteria(orgid, desigId, userscreen, navigationId, toPage) { 
    
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$("#modalInfo").show();
    	$('.modal-title').html('Add Succession Plan Criteria');
    	if($(window).width() >= 1000){
    		$(".modal-dialog").width(1000);
    	}
    	$.ajax({
			url : 'AddSuccessionPlanCriteria.action?orgId='+orgid+'&desigId='+desigId+'&operation=A&userscreen='+userscreen
					+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    } */
    
    
</script>

<%
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    UtilityFunctions uF = new UtilityFunctions();
    Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
    Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
    String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
    
    Map<String, List<List<String>>> hmLevelMapOrgwise = (Map<String, List<List<String>>>) request.getAttribute("hmLevelMapOrgwise");
    Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
    
    /* Map hmLevelMap = (java.util.Map)request.getAttribute("hmLevelMap"); */
    Map hmDesigMap = (java.util.Map)request.getAttribute("hmDesigMap");
    Map hmGradeMap = (java.util.Map)request.getAttribute("hmGradeMap");
    Map hmEmpGradeMap = (java.util.Map)request.getAttribute("hmEmpGradeMap");
    Map<String, String> hmCriteriaId = (Map<String, String>) request.getAttribute("hmCriteriaId");
    String []arrEnabledModules = CF.getArrEnabledModules();
    
    String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
    %>
    
	<div class="box-body">
			
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="SuccessionPlanReport" theme="simple">
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm();"></s:select>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>

    <div class="col-sm-12 col-md-6 col-lg-3 col_no_padding" style="overflow-y: hidden;max-height: 550px;" id="leftSectionSucceessSetting"><!-- @author Dattatray  -->
        <ul class="level_list col_no_padding">
            <% 
                Set setLevelMap = hmLevelMapOrgwise.keySet();
                Iterator it = setLevelMap.iterator();
                String desigId = null;
                String operation = "A";
                String criteriaId = null;
                String activeClass = "";
                while(it.hasNext()) {
                	String strOrgId = (String)it.next();
                	List<List<String>> levelList = (List<List<String>>)hmLevelMapOrgwise.get(strOrgId);
                %>
            <li>
                <strong><%=hmOrgName.get(strOrgId) %> </strong>
                <ul>
                    <%
                        for(int i=0; levelList != null && !levelList.isEmpty() && i<levelList.size(); i++) {
                        	List<String> alLevel = levelList.get(i);
                        	%>
                    <li>
                        <strong><%=alLevel.get(2)%> [<%=alLevel.get(1)%>]</strong>
                        <ul>
                            <%  List alDesig = (List)hmDesigMap.get(alLevel.get(0));
                                if(alDesig==null)alDesig=new ArrayList();
                                
                                for(int d=0; d<alDesig.size(); d+=3) {
                                String strDesigId = (String)alDesig.get(d);
                                if(desigId==null) {
                                	desigId = strDesigId;
                                	activeClass = "activelink";
                                } else {
                                	activeClass = "";
                                }
                                List alGrade = (List)hmGradeMap.get(strDesigId);
                                if(alGrade==null)alGrade=new ArrayList();
                                %>
                            <li class="item">
                                <%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> --%>
                                <%
                                    if(ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
                                    if(hmCriteriaId != null && hmCriteriaId.get(alDesig.get(d)) != null ) { 
                                    	if(hmCriteriaId != null && hmCriteriaId.get(desigId) != null ) {
                                        	operation = "E";
                                        	criteriaId = hmCriteriaId.get(alDesig.get(d));
                                        }
                                    %>
                                <a href="AddSuccessionPlanCriteria.action?orgId=<%=strOrgId %>&desigId=<%=alDesig.get(d) %>&operation=D&criteriaId=<%=hmCriteriaId.get(alDesig.get(d)) %>" title="Delete Succession Plan Criteria" onclick="return confirm('Are you sure you wish to delete this succession plan criteria?')" style="color:red;"> <i class="fa fa-trash" aria-hidden="true"></i></a>
                                <a class="<%=activeClass %>" href="javascript:void(0)" onclick="addSuccessionplanCriteria('AddSuccessionPlanCriteria', '<%=strOrgId %>','<%=alDesig.get(d)%>', 'E', '<%=hmCriteriaId.get(alDesig.get(d)) %>');">
                                <img src="images1/icons/succession_planning_green.png" title="Edit Succession Plan Criteria" /> <%=alDesig.get(d+2)%> [<%=alDesig.get(d+1)%>] </a>
                                <% } else { %>
                                <a class="<%=activeClass %>" href="javascript:void(0)" onclick="addSuccessionplanCriteria('AddSuccessionPlanCriteria', '<%=strOrgId %>','<%=alDesig.get(d)%>', 'A', '');">
                                <img src="images1/icons/succession_planning_icon.png" title="Add Succession Plan Criteria" /> <%=alDesig.get(d+2)%> [<%=alDesig.get(d+1)%>] </a>
                                <% } %>
                                <%} %>
                            </li>
                            <% } %>
                        </ul>
                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>
        </ul>
    </div>
    
    
    <div class="col-lg-9 col-md-9 col-sm-12">
		<div class="tab-content" style="overflow-y: hidden;" id="rightSectionSucceessSetting"><!-- @author Dattatray -->
			<div class="active tab-pane" id="successionPlanDetails" style="min-height: 600px;"></div>
		</div>
	</div>
		 
    
</div>


<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		//alert("1");
		<%-- getGoalSummary('GoalSummary','<%=dataType%>','<%=currUserType%>', ''); --%>
		var orgid = document.getElementById("strOrg").value;
		//alert("2");
		var desigId = '<%=desigId %>';
		var operation = '<%=operation %>';
		var criteriaId = '<%=criteriaId %>';
		//alert("desigId ==>>> " + desigId);
		addSuccessionplanCriteria('AddSuccessionPlanCriteria', orgid, desigId, operation, criteriaId);
	});
	
	function addSuccessionplanCriteria(strAction, orgid, desigId, operation, criteriaId) {
	//alert("action ==> " + strAction + " -- orgid ==> " + orgid + " -- desigId ==> " + desigId + " -- criteriaId ==> " + criteriaId);
		//var orgid = document.getElementById("strOrgId").value;
		var paramValues = 'orgId='+orgid+'&desigId='+desigId+'&operation='+operation+'&criteriaId='+criteriaId;
		$("#successionPlanDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#successionPlanDetails").html(result);
	   		}
		});
	}
	/* @uthor : Dattatray */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#leftSectionSucceessSetting").scrollTop() != 0) {
	        	$("#leftSectionSucceessSetting").scrollTop($("#leftSectionSucceessSetting").scrollTop() - 30);
	        }
	        if($(window).scrollTop() == 0 && $("#rightSectionSucceessSetting").scrollTop() != 0) {
	        	$("#rightSectionSucceessSetting").scrollTop($("#rightSectionSucceessSetting").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSectionSucceessSetting").scrollTop($("#leftSectionSucceessSetting").scrollTop() + 30);
	   		}
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	 		   $("#rightSectionSucceessSetting").scrollTop($("#rightSectionSucceessSetting").scrollTop() + 30);
			}
	    }
	});

	/* @uthor : Dattatray */
	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#leftSectionSucceessSetting").scrollTop($("#leftSectionSucceessSetting").scrollTop() + 50);
	   		}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#rightSectionSucceessSetting").scrollTop($("#leftSectionSucceessSetting").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#leftSectionSucceessSetting").scrollTop() != 0) {
		    	$("#leftSectionSucceessSetting").scrollTop($("#leftSectionSucceessSetting").scrollTop() - 50);
		    }
			if($(window).scrollTop() == 0 && $("#rightSectionSucceessSetting").scrollTop() != 0) {
		    	$("#rightSectionSucceessSetting").scrollTop($("#rightSectionSucceessSetting").scrollTop() - 50);
		    }
		}
	}); 
</script> 


<!-- <div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        Modal content
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div> -->
