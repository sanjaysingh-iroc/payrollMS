<%@page import="com.konnect.jpms.select.FillFinancialYears"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%String fromPage = (String) request.getAttribute("fromPage"); 
  String currUserType = (String) request.getAttribute("currUserType"); %>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
    <%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<%} %>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function(){
		$("body").on('click','#closeButton',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
		});
		$("body").on('click','.close',function(){
			$(".modal-body").height(400);
			$(".modal-dialog").removeAttr('style');
			$("#modalInfo").hide();
		});
	});

	
	function editResourcePlan(from) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Edit Workforce Plan');
		var finansyr = document.getElementById("f_strFinancialYear").value;
		var orgid1="";
		var lvlid="";
		if(document.getElementById("f_org")) {
			orgid1 = document.getElementById("f_org").value;
		}
		if(document.getElementById("f_level")) {
			lvlid = document.getElementById("f_level").value;
		}
		if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		}
		var currUserType = document.getElementById("currUserType").value;
		$.ajax({
			url : 'AddResourcePlanner.action?finansyr='+finansyr+'&orgid='+orgid1+'&lvlid='+lvlid
					+'&currUserType='+currUserType+'&fromPage='+from,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function getDesignationDetails(desigId, desigName) {
		//alert("desigName ===>> " + desigName);
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('' + desigName);
		$.ajax({
			url : "DesignationDetails.action?desig_id=" + desigId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
				
			}
		});
	}
	
	
	function getResourcePlanner(dataType){
		//alert("service ===>> " + service);
		var fromPage = document.getElementById("fromPage").value;
		$("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ResourcePlanner.action?currUserType='+dataType+'&fromPage='+fromPage,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divWFResult").html(result);
	   		}
		});
	}
	
	function submitForm(from){
		//alert("service ===>> " + service);
		var from = document.getElementById("fromPage").value;
		/* if(from != "" && from == "WF") { */
			var f_org ="";
			if(document.getElementById("f_org")) {
				f_org = document.getElementById("f_org").value;
			}
			var f_strFinancialYear = document.getElementById("f_strFinancialYear").value;
			var f_level = document.getElementById("f_level").value;
			var currUserType = document.getElementById("currUserType").value;;
			$("#divWFResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ResourcePlanner.action?f_org='+ f_org+'&f_strFinancialYear='+f_strFinancialYear+'&f_level='+f_level
						+'&fromPage='+from+'&currUserType='+currUserType,
				success: function(result){
					$("#divWFResult").html(result);
		   		}
			});
		/* } else {
			document.getElementById("frm_ResourcePlanner").submit();
		} */
	}
	
	
	function expandColapsData(type, desigId) {
		var skillIds = document.getElementsByName('skillId'+desigId);
		//alert("skillIds ===>> " + skillIds);
		if(type=='P') {
			document.getElementById('spanPlus'+desigId).style.display= 'none';
			document.getElementById('spanMinus'+desigId).style.display= 'block';
			for(i=0; i<skillIds.length; i++) {
				var skillSpanId = skillIds[i].value;
				document.getElementById(skillSpanId).style.display= 'table-row';
			}
		} else {
			document.getElementById('spanPlus'+desigId).style.display= 'block';
			document.getElementById('spanMinus'+desigId).style.display= 'none';
			for(i=0; i<skillIds.length; i++) {
				var skillSpanId = skillIds[i].value;
				document.getElementById(skillSpanId).style.display= 'none';
			}
		}
	}
	
</script>


<%
	List<List<String>> outerList = (List<List<String>>) request.getAttribute("designationList");
	Map<String, String> hmDesigEmpCount = (Map<String, String>) request.getAttribute("hmDesigEmpCount");
	UtilityFunctions uF = new UtilityFunctions();

	Map<String, String> hmInductEmpCount = (Map<String, String>) request.getAttribute("hmInductEmpCount");
	Map<String, String> hmRequirementEmpCount = (Map<String, String>) request.getAttribute("hmRequirementEmpCount");
	Map<String, String> hmPlannedRequiredEmpCount = (Map<String, String>) request.getAttribute("hmPlannedRequiredEmpCount");
	
	Map<String, Map<String, Map<String, String>>> hmRequiredDesigwiseSkillwiseEmpCount = (Map<String, Map<String, Map<String, String>>>) request.getAttribute("hmRequiredDesigwiseSkillwiseEmpCount");
	if(hmRequiredDesigwiseSkillwiseEmpCount==null) hmRequiredDesigwiseSkillwiseEmpCount = new HashMap<String, Map<String, Map<String, String>>>();
	
	Map<String, String> hmSkillName = (Map<String, String>) request.getAttribute("hmSkillName");
	if(hmSkillName==null) hmSkillName = new HashMap<String, String>();
	
	Map<String, String> hmAttritionBackfillEmpCount = (Map<String, String>) request.getAttribute("hmAttritionBackfillEmpCount");
	if(hmAttritionBackfillEmpCount==null) hmAttritionBackfillEmpCount = new HashMap<String, String>();
	
	String monthStart = (String) request.getAttribute("monthStart");
	String yearStart = (String) request.getAttribute("yearStart");
	String monthEnd = (String) request.getAttribute("monthEnd");
	String yearEnd = (String) request.getAttribute("yearEnd");

	int currentMonth = (Integer) request.getAttribute("currentMonth");
	
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);

%>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
 <section class="content">
 <%}%>
 	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
			 <%if(fromPage != null && fromPage.equals("WF")) { %>
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="javascript:void(0);" onclick="getResourcePlanner('MYTEAM');" style="padding: 4px 15px !important; border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="javascript:void(0);" onclick="getResourcePlanner('<%=strBaseUserType %>');" style="padding: 4px 15px !important; border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			<% } else { %>
				<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ResourcePlanner.action?currUserType=MYTEAM" style="padding: 4px 15px !important; border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="ResourcePlanner.action?currUserType=<%=strBaseUserType %>" style="padding: 4px 15px !important; border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			<% } %>
			</ul>
		</div>
	<% } %>
	
	<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	 <div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary"> 
     <%}%>
				<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
					<s:form name="frm_ResourcePlanner" id= "frm_ResourcePlanner"  action="ResourcePlanner" theme="simple">
						<s:hidden name="currUserType" id="currUserType"/>
						<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage%>"/>
							<div class="box box-default collapsed-box">
								<div class="box-header with-border">
								    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
								    <div class="box-tools pull-right">
								        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								    </div>
								</div>
								<div class="box-body" style="padding: 5px; overflow-y: auto;">
									<div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div>
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Financial Year</p>
												<s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear" listKey="financialYearId"
													listValue="financialYearName" list="financialYearList" key="" onchange="submitForm();" />
											</div>
											<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Organisation</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="orgList" key=""  onchange="submitForm();"/>
											</div>
											<% } %>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Level</p>
												<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" list="levelList" key="" required="true" onchange="submitForm();"/>
											</div>
										</div>
									</div>
								</div>
							</div>
						</s:form>
						
					<div class="col-md-12" style="margin: 0px 0px 10px 0px">
						<a href="javascript: void(0)" onclick="editResourcePlan('<%=fromPage%>');"><i class="fa fa-plus-circle" aria-hidden="true"></i>Edit Workforce Plan </a>
					</div>
						
					<br/><br/>
					<div class="attendance" style="overflow-x: auto;">
						<!-- <table class="display tb_style" >  -->
						<table class="table table-bordered" cellspacing="0" cellpadding="2" align="left">
							<tbody>
								<tr class="darktable">
									<td rowspan="3" style="text-align: center;">Designation</td>
									<% if (currentMonth == 4) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">April</td>
	
									<% if (currentMonth == 5) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">May</td>
	
									<% if (currentMonth == 6) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">June</td>
	
									<% if (currentMonth == 7) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">July</td>
	
									<% if (currentMonth == 8) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">August</td>
	
									<% if (currentMonth == 9) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">September</td>
	
									<% if (currentMonth == 10) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">October</td>
	
									<% if (currentMonth == 11) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">November</td>
	
									<% if (currentMonth == 12) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">December</td>
	
									<% if (currentMonth == 1) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">January</td>
	
									<% if (currentMonth == 2) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">February</td>
	
									<% if (currentMonth == 3) { %>
									<td valign="bottom" style="text-align: center;" rowspan="3">Exist</td>
									<% } %>
									<td style="width: 75px; text-align: center;" colspan="5">March</td>
								</tr>
								
								<tr class="darktable">
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
	
									<td valign="bottom" rowspan="2" style="width: 75px">Planned</td>
									<td colspan="2" style="width: 150px">Required</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Induct</td>
									<td valign="bottom" rowspan="2" style="width: 75px">Variance</td>
								</tr>
	
								<tr class="darktable">
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
									<td style="width: 75px">New Requirement</td>
									<td style="width: 75px">Attrition Backfill</td>
									
								</tr>
								<%
									for (int i = 0; outerList != null && i < outerList.size(); i++) {
										List<String> innerList = outerList.get(i);
										Map<String, Map<String, String>> hmRequiredSkillwiseEmpCount = hmRequiredDesigwiseSkillwiseEmpCount.get(innerList.get(0));
										if(hmRequiredSkillwiseEmpCount==null) hmRequiredSkillwiseEmpCount = new HashMap<String, Map<String, String>>();
 								%>
								<tr class="lighttable"> <!-- nowrap="nowrap"  -->
									<td style="width: 220px !important;"><%-- <span style="float: left; margin-right: 5px;"> --%> 
									<a class="users-list-name" style="float: left; width: 85% !important;" href="javascript:void(0);" title="<%="[" + innerList.get(1) + "] " + innerList.get(2)%>" onclick="getDesignationDetails('<%=innerList.get(0)%>','<%="[" + innerList.get(1) + "] " + innerList.get(2)%>');">
										<%="[" + innerList.get(1) + "] " + innerList.get(2)%></a><%-- </span> --%>
										<% if(hmRequiredSkillwiseEmpCount!=null && hmRequiredSkillwiseEmpCount.size()>0) { %>
											<span style="float: right;">
												<span id="spanPlus<%=innerList.get(0)%>" ><a href="javascript:void(0);" onclick="expandColapsData('P','<%=innerList.get(0)%>')"><i class="fa fa-plus" style="font-size: 12px !important;"></i></a></span>
												<span id="spanMinus<%=innerList.get(0)%>" style="display: none;"><a href="javascript:void(0);" onclick="expandColapsData('M','<%=innerList.get(0)%>')"><i class="fa fa-minus" style="font-size: 12px !important;"></i></a></span>
											</span>
										<% } %>
									</td>
	
									<% if (currentMonth == 4) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "4"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "4" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "4" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "4" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											String planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "4"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "4"+ yearStart), "0");
											String attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "4"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "4" + yearStart), "0");
											int variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 5) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "5"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "5"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "5" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0)+ "5" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "5"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "5"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "5"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "5" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 6) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "6" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "6"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "6" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "6" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "6"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "6"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "6"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "6" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
									
									<% if (currentMonth == 7) { %>
										<td align="center"><%=uF.showData( hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "7" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "7" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "7" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "7" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "7"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "7"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "7"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "7" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 8) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "8"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "8"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "8" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "8" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "8"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "8"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "8"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "8" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 9) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "9" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "9"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "9" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "9" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "9"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "9"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "9"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "9" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 10) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "10"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "10"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "10" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "10"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "10"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "10"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "10"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "10" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 11) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "11" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "11"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "11" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "11"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "11"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "11"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "11"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "11" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 12) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "12" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "12"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "12" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "12"+ yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "12"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "12"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "12"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "12" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 1) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "1" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "1"+ yearEnd), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "1" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "1" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "1"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "1"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "1"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "1" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 2) { %>
										<td align="center"><%=uF.showData(hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "2" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "2"+ yearEnd), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "2" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "2" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "2"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "2"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "2"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "2" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
	
									<% if (currentMonth == 3) { %>
										<td align="center"><%=uF.showData( hmDesigEmpCount.get(innerList.get(0)), "0")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "3" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "3"+ yearEnd), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "3" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
										<%=uF.showData(hmInductEmpCount.get(innerList.get(0) + "3" + yearEnd),"0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											planned = uF.showData(hmPlannedRequiredEmpCount.get("PLANNED_"+innerList.get(0) + "3"+ yearStart), "0");
											required = uF.showData(hmPlannedRequiredEmpCount.get("REQUIRED_"+innerList.get(0) + "3"+ yearStart), "0");
											attritionBackfill = uF.showData(hmAttritionBackfillEmpCount.get(innerList.get(0) + "3"+ yearStart), "0");
											induct = uF.showData(hmInductEmpCount.get(innerList.get(0) + "3" + yearStart), "0");
											variance = (uF.parseToInt(required)+uF.parseToInt(attritionBackfill)) - uF.parseToInt(induct);
										%>
										<%=variance %></div>
									</td>
								</tr>
								
								
								
								
								<% Iterator<String> it = hmRequiredSkillwiseEmpCount.keySet().iterator();
									while(it.hasNext()) {
										String skillId = it.next();
										Map<String, String> hmSkillInner = hmRequiredSkillwiseEmpCount.get(skillId);
										if(hmSkillInner==null) hmSkillInner = new HashMap<String, String>();
								%>
								
								<tr class="lighttable" id="<%=innerList.get(0)+"_"+skillId %>" style="display: none;">  <!-- style="display: none;" -->
									<td > <!-- nowrap="nowrap" -->
									<input type="hidden" name="skillId<%=innerList.get(0) %>" id="skillId<%=innerList.get(0) %>" value="<%=innerList.get(0)+"_"+skillId %>" />
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=uF.showData(hmSkillName.get(skillId), "") %></td>
	
									<% if (currentMonth == 4) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "4" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "4"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "4"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "4" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 5) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "5" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "5"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "5"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "5" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 6) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "6" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "6"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "6"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "6" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
									
									<% if (currentMonth == 7) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "7" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "7"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "7"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "7" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 8) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "8" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "8"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "8"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "8" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 9) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "9" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "9"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "9"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "9" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 10) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "10" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "10"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "10"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "10" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 11) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "11" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "11"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "11"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "11" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
									
									<% if (currentMonth == 12) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "12" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "12"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "12"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "12" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 1) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "1" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "1"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "1"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "1" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 2) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "2" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "2"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "2"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "2" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
	
									<% if (currentMonth == 3) { %>
										<td align="center"><%=uF.showData("", "-")%></td>
									<% } %>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #F7FE2E;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData(hmSkillInner.get(skillId + "3" + yearStart), "0")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #90EE90;">
										<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #00FFFF;">
									<%=uF.showData("", "-")%></div>
									</td>
									<td align="center"><div class="blueColor" style="width: 100%; height: 100%; background-color: #a9cfff;">
										<%
											/* String planned = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "3"+ yearStart), "0");
											String required = uF.showData(hmPlannedRequiredEmpCount.get(skillId + "3"+ yearStart), "0");
											String induct = uF.showData(hmInductEmpCount.get(skillId + "3" + yearStart), "0");
											int variance = (uF.parseToInt(required)) - uF.parseToInt(induct); */
										%>
										<%="-"%></div>
									</td>
								</tr>
								<% } %>
								
								
								
								
								
								
								<% } %>
	
							</tbody>
						</table>
					</div>
		
				</div>
				<!-- /.box-body -->
			<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
			 </div>
		</section>
	</div>
</section> 
<%} %>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">View Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
