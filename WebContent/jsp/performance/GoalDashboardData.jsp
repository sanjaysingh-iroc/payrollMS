<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

 <%	
 	UtilityFunctions uF = new UtilityFunctions();
 	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
 	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
 	
	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
%> 
  
<section class="content" style="padding-top: 0px;">
   <div class="row jscroll">
   
   <div class="col-lg-12 col-md-12 col-lg-12 col_no_padding">
   		<% if((strSessionUserType!= null && !strSessionUserType.equals(IConstants.MANAGER) && !strSessionUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
       		<div class="box box-default collapsed-box">
				<div class="box-header with-border">
					<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<s:form name="frm_Search" action="GoalDashboardData" theme="simple">
					<s:hidden name="dataType" id="dataType" />
					<s:hidden name="currUserType" id="currUserType" />
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter" aria-hidden="true"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm();" list="organisationList" key=""/>  <!-- value="strOrg" -->
							</div>
						</div>
					</div>
					</s:form>
				</div>
			</div>
		<%} %>
    
		<div class="row row_without_margin" style="padding: 0px 0px 15px;"> 
			<div class="col-lg-10 col-md-10 col-sm-12">
				<%-- <div style="float: left; margin-bottom: 10px">
				&nbsp;&nbsp;<span style="background-color:#d2d6de;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Corporate Goal&nbsp;&nbsp;&nbsp;
				<span style="background-color:#FFEDE0;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Manager Goal&nbsp;&nbsp;&nbsp;
				<span style="background-color:#F0F8CF;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Team Goal&nbsp;&nbsp;&nbsp;
				<span style="background-color:#ECD0F1;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Individual Goal
				</div> --%>
				<div style="float: left; margin-bottom: 10px">
					&nbsp;&nbsp;<span style="background-color:#d2d6de;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Company&nbsp;&nbsp;&nbsp;
					<span style="background-color:#FFEDE0;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Departmental&nbsp;&nbsp;&nbsp;
					<span style="background-color:#F0F8CF;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Team&nbsp;&nbsp;&nbsp;
					<span style="background-color:#ECD0F1;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;Individual OKR
				</div>
			</div>
		</div>
    
       <section class="col-lg-12 connectedSortable col_no_padding" >
          <div class="col-lg-2 col-md-2 col-sm-12 col_no_padding" >
		      <div class="box box-none">
				 <div class="nav-tabs-custom">
				      <ul class="nav nav-tabs">
				          <li class="active"><a href="javascript:void(0)" onclick="getCorporateGoalNameList('CorporateGoalNameList','L','<%=currUserType %>');" data-toggle="tab">Live</a></li>
				          <li><a href="javascript:void(0)" onclick="getCorporateGoalNameList('CorporateGoalNameList','C','<%=currUserType %>');" data-toggle="tab">Closed</a></li>
				      </ul>
				      
				     <!--  ===start parvez date: 23-02-2023=== --> 
						<div class="tab-content" style="overflow-y: hidden; max-height: 450px;" id="leftSectionOKR"><!-- @uthor Dattatray  -->
			         <!--  ===end parvez date: 23-02-2023=== -->       
			                 <div class="active tab-pane" id="corporateGoalNameList" style="height: 600px;"></div>
		                 </div>
				  </div>
			    </div>
			</div>
			
			<div class="col-lg-10 col-md-10 col-sm-12">
			<div class="box box-none">
				 <div class="nav-tabs-custom">
				      <ul class="nav nav-tabs">
				          <li class="active" id="glSummary"><a href="javascript:void(0)" onclick="getGoalList('GoalSummary', '<%=currUserType %>');" data-toggle="tab"><i class="fa fa-bars" aria-hidden="true" title="List view"></i></a></li>
				          <li id="glChart"><a href="javascript:void(0)" onclick="getGoalChart('GoalChart', '<%=currUserType %>');" data-toggle="tab"><i class="fa fa-sitemap" aria-hidden="true" title="Chart view"></i></a></li>
				      </ul>
				      
				 <!--  ===start parvez date: 23-02-2023=== -->     
				      <div class="tab-content" style="overflow-y: hidden; max-height: 450px;" id="rightSectionOKR"><!-- @uthor : Dattatray  -->
		         <!--  ===end parvez date: 23-02-2023=== -->        
		                 <div class="active tab-pane" id="corporateGoalDetails" style="height: 600px;"></div>
	                 </div>
				  </div>
			    </div>
			 </div>   
	   </section>
    </div>
</section> 
 

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		//alert("inside getGoalDashboardData");
		<%-- getGoalSummary('GoalSummary','<%=dataType%>','<%=currUserType%>', ''); --%>
		getCorporateGoalNameList('CorporateGoalNameList', '<%=dataType %>', '<%=currUserType %>');
	});
	
	function getCorporateGoalNameList(strAction, dataType, currUserType) {
	//	alert("getGoalDashboardData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		var f_org = "";
		if(document.getElementById("f_org")) {
			f_org = document.getElementById("f_org").value;
		}
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&f_org='+f_org;
		$("#corporateGoalNameList").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#corporateGoalNameList").html(result);
	   		}
		});
		document.getElementById("glSummary").className = "active";
		document.getElementById("glChart").className = "";
	}
	

	function submitForm() {
		var org = "";
		if(document.getElementById("f_org")) {
			org = document.getElementById("f_org").value;
		}
		//alert("org ===>> " + org);
		var datatype = '<%=dataType %>';
		var currUserType = '<%=currUserType %>';
		//alert("1 -- org ===>> " + org);
		var action = 'GoalDashboardData.action?f_org='+org+'&datatype='+datatype+'&currUserType='+currUserType;
		$("#goalKraTargetData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: action,
			success: function(result){
	        	$("#goalKraTargetData").html(result);
	   		}
		});
	}
	
	/* @uthor : Dattatray */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#leftSectionOKR").scrollTop() != 0) {
	        	$("#leftSectionOKR").scrollTop($("#leftSectionOKR").scrollTop() - 30);
	        }
	        if($(window).scrollTop() == 0 && $("#rightSectionOKR").scrollTop() != 0) {
	        	$("#rightSectionOKR").scrollTop($("#rightSectionOKR").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSectionOKR").scrollTop($("#leftSectionOKR").scrollTop() + 30);
	   		}
	         if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	 		   $("#rightSectionOKR").scrollTop($("#rightSectionOKR").scrollTop() + 30);
			}
	    }
	});

	/* @uthor : Dattatray */
	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() + 50);
	   		}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#rightSectionOKR").scrollTop($("#rightSectionOKR").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#leftSectionKRA").scrollTop() != 0) {
		    	$("#leftSectionKRA").scrollTop($("#leftSectionKRA").scrollTop() - 50);
		    }
			if($(window).scrollTop() == 0 && $("#rightSectionOKR").scrollTop() != 0) {
		    	$("#rightSectionOKR").scrollTop($("#rightSectionOKR").scrollTop() - 50);
		    }
		}
	});
	
</script>          
    