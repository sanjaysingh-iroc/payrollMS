<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%	
 	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strEmpId = (String) request.getAttribute("strEmpId");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String sbData = (String) request.getAttribute("sbData");
    String strSearchJob = (String) request.getAttribute("strSearchJob");
    String orgId = (String) request.getAttribute("f_org");
    String location = (String) request.getAttribute("strLocation");
    String department = (String) request.getAttribute("strDepartment");
    String level = (String) request.getAttribute("strLevel");
    String strSearch = (String) request.getAttribute("strSearchJob");
    
%>

<section class="content">
	<div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<s:form name="frm_oneOneDiscussion" action="ReviewDiscussionDashboardData" id="frm_oneOneDiscussion" theme="simple" cssStyle="margin-top: 10px;">
				<s:hidden name="dataType"></s:hidden>
				<s:hidden name="proPage" id="proPage" />
				<s:hidden name="minLimit" id="minLimit" />
				<s:hidden name="currUserType" id="currUserType"/>
				
				<div class="col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
		             <div style="float: left;line-height: 22px; width: 514px; margin-left: 350px;">
		                 <span style="float: left; display: block; width: 78px;">Search:</span>
		                 <div style="margin: 0px 0px 0px 16px; float: left; -moz-border-radius: 3px; -webkit-border-radius: 3px; border-radius: 3px;">
		                     <div style="float: left">
		                         <input type="text" id="strSearchJob" class="form-control" name="strSearchJob"
		                             style="margin-left: 0px; width: 250px; box-shadow: 0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>" />
		                     </div>
		                     <div style="float: right">
		                         <input type="button" value="Search" class="btn btn-primary" name="submit" onclick="submitForm('2');" style="margin-left: 10px;"/>
		                     </div>
		                 </div>
		             </div>
		             
		             <script>
		                 $( "#strSearchJob" ).autocomplete({
		                 	source: [ <%=uF.showData(sbData,"") %> ]
		                 });
		             </script>
	             </div>
			</s:form>
			
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;;max-height: 600px;overflow-y:hidden;" id="leftSection">
					<div class="box box-thin">
						<div class="nav-tabs-custom">
				             <ul class="nav nav-tabs">
				                 <li class="active"><a href="javascript:void(0)" onclick="getReviewDiscussionEmpList('ReviewDiscussionEmpList','L','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>', '');" data-toggle="tab">Live</a></li>
				                 <li><a href="javascript:void(0)" onclick="getReviewDiscussionEmpList('ReviewDiscussionEmpList','C','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>', '');" data-toggle="tab">Closed</a></li>
				             </ul>
				             <div class="tab-content" >
				                 <div class="active tab-pane" id="reviewDiscussionResult">
							
				                 </div>
				             </div>
				        </div>
					</div>
				</div>
				
				<div class="col-md-9" style="padding-left: 0px;max-height: 600px;overflow-y:hidden;" id="rightSection">
					<div class="box box-thin" style="padding: 5px;" id="actionResult">
						<div class="nav-tabs-custom">
				               <div class="tab-content" >
				                 <div class="active tab-pane" id="reviewDiscussionDetails"	>
							
				                 </div>
				             </div>
				        </div>
					</div>
			   </div>
			   
			</div>
			
		</section>
	</div>
</section>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
		getReviewDiscussionEmpList('ReviewDiscussionEmpList','<%=dataType%>','<%=currUserType%>','<%=orgId%>','<%=location%>','<%=department%>','<%=level%>','<%=strSearch %>','<%=strEmpId %>');
	});
	
	function getReviewDiscussionEmpList(strAction,dataType,currUserType,orgId,location,dept,level,strSearch,strEmpId){
		
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&strEmpId='+strEmpId;
		$("#reviewDiscussionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues+'&strSearchJob='+strSearch,
			cache: true,
			success: function(result){
				//alert("result2==>"+result);
				$("#reviewDiscussionResult").html(result);
	   		}
		});
	}
	
	function submitForm(type) {
		
		var value = "Search";
		
		var strSearch = document.getElementById("strSearchJob").value;
		var currUserType = document.getElementById("currUserType").value;
		var paramValues = "";
    	if(type == "2") {
    		value = "Submit";
    		paramValues = '&strSearchJob='+strSearch;
		}
        
    	var action = 'ReviewDiscussionDashboardData.action?currUserType='+currUserType+paramValues;
    	
    	$("#oneOneDiscussionData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		success: function(result){
            	$("#oneOneDiscussionData").html(result);
       		}
    	});
    	
    }
	
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#leftSection").scrollTop() != 0) {
	        	$("#leftSection").scrollTop($("#leftSection").scrollTop() - 30);
	        }
	        if($(window).scrollTop() == 0 && $("#rightSection").scrollTop() != 0) {
	        	$("#rightSection").scrollTop($("#rightSection").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#leftSection").scrollTop($("#leftSection").scrollTop() + 30);
	   		}
	         if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	 		   $("#rightSection").scrollTop($("#rightSection").scrollTop() + 30);
			}
	    }
	});
	
	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#leftSection").scrollTop($("#leftSection").scrollTop() + 50);
	   		}
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#rightSection").scrollTop($("#rightSection").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#leftSection").scrollTop() != 0) {
		    	$("#leftSection").scrollTop($("#leftSection").scrollTop() - 50);
		    }
			if($(window).scrollTop() == 0 && $("#rightSection").scrollTop() != 0) {
		    	$("#rightSection").scrollTop($("#rightSection").scrollTop() - 50);
		    }
		}
	});

</script>