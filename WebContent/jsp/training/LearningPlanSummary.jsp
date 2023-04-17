<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<style>
.label{
  font-size:13px;
}
</style>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String learningPlanId = (String)request.getAttribute("learningPlanId");
	List<String> learningInfo = (List<String>) request.getAttribute("learningInfo");
	if(learningInfo  == null) learningInfo = new ArrayList<String>();

%>
	<div>
		<%if(learningInfo != null && learningInfo.size()>0 && !learningInfo.isEmpty()) {%>
			   <div class="box-header with-border">
                   <h3 class="box-title">
                   	 <%=uF.showData(learningInfo.get(7),"0")%>                 	
                   	<div style="display:inline;"><%=uF.showData(learningInfo.get(1), "")%></div>
                  </h3>
                  <div class="box-tools pull-right">
  						<span class="label label-warning">Pending:<%=uF.showData(learningInfo.get(12),"0")%></span>		
  						<span class="label label-info">Ongoing:<%=uF.showData(learningInfo.get(13),"0")%></span>
  						<span class="label label-success">Finalised:<%=uF.showData(learningInfo.get(16),"0") %></span>
		          </div>
				</div>
				 <div class="box-body" style="padding: 5px; overflow-y: auto;">
                    <table class="table table-striped table_no_border">
						<tr><th width="15%" align="right" style="border-top-color: #FFF;">Objective:</th>
							<td>
								<% if(learningInfo.get(2) != null && learningInfo.get(2).length() < 20){ %>
									<%=learningInfo.get(2)%>
								<% } else { %>
									<a href="javascript:void(0);" onclick="showLPlanReason('<%=learningInfo.get(2) %>');"><%=learningInfo.get(2).substring(0, learningInfo.get(2).length()>9 ? 9 : learningInfo.get(2).length()) %>...</a>
								<% } %>
							</td>
						</tr>
						<tr><th valign="top" align="right">Aligned with:</th><td colspan="1"><%=uF.showData(learningInfo.get(3),"-")%></td></tr>
						<tr><th valign="top" align="right">Type:</th><td colspan="1"><%=uF.showData(learningInfo.get(8),"-")%></td></tr>
						<tr><th valign="top" align="right">Certificate:</th>
							<% if(learningInfo.get(9) != null && learningInfo.get(9).equals("No Certificate")) { %>
								<td colspan="1"> - </td>
							<%} else { %>
								<td colspan="1"><img src="images1/icons/hd_tick_20x20.png">  <br/>
								<a href="javascript:void(0)" onclick="viewCertificate('<%=learningInfo.get(14)%>','')"><%=learningInfo.get(9)%></a> </td>
							<% } %>
						</tr>
						<tr><th valign="top">Duration:</th><td colspan="1">From <%=uF.showData(learningInfo.get(10),"-")%></td></tr>
						<tr><th valign="top">Created by:</th><td colspan="1"><%=uF.showData(learningInfo.get(11),"-")%></td></tr>
						<tr><th valign="top">Learners:</th><td colspan="1"><%=uF.showData(learningInfo.get(5),"-")%></td></tr>
						<%-- <tr><th valign="top">Learning Status:</th><td colspan="1">Pending:<%=uF.showData(learningInfo.get(12),"0")%>,
						Ongoing:<%=uF.showData(learningInfo.get(13),"0")%>,Finalised:<%=uF.showData(learningInfo.get(16),"0") %></td></tr> --%>
					</table>
                </div>
				  
		 
		<%} else { %>
			<div class="nodata msg">No learning plan summary.</div>
		<%} %>	               
     </div>
    

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog modal-dialog1">
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
<script>

	
    function openEmpProfilePopup(empId){
    	var id=document.getElementById("panelDiv");
   		if(id) {
    		id.parentNode.removeChild(id);
    	}
    
   		var dialogEdit = '.modal-body';
	 	$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
	    $(".modal-title").html('Employee Information');
	    if($(window).width() >= 900){
		  $(".modal-dialog").width(900);
	    }
	  
	    $.ajax({
		    url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
		    cache : false,
		    success : function(data) {
		   		 $(dialogEdit).html(data);
		    }
		 });
    }
	 function editLearningPlan(planId) {
		    
		 $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');			
			$.ajax({ 
				url: 'AddLearningPlan.action?ID='+planId+'&step=0&operation=E',
				cache: false,
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
	 }
	 
	function deleteLearningPlan(planId) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		if(confirm('Are you sure, you want to delete this learning?')) {
			$.ajax({
				type:'GET',
				url:'AddLearningPlan.action?ID='+planId+'&step=0&operation=D',
				success:function(result){
					$("#divResult").html(result);
				}, 
	 			error : function(err) {
	 				$.ajax({ 
						url: 'LearningPlanDashboard.action',
						cache: true,
						success: function(result){
							$("#divResult").html(result);
				   		}
					});
	 			}
			});
		}
	}
		
	 function getPublishLearningPlan(lPlanId,status) {
		 var msg = 'Are you sure, you want to publish this learning?';
		 if(status != "" && status == "0") {
			 msg = 'Are you sure, you want to unpublish this learning?';
		 }
		 $("#divLPDetailsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 if(confirm(msg)) {
			 $.ajax({
				 type:'GET',
				 url:'PublishLearningPlan.action?id='+lPlanId,
				 cache:false,
			     success:function(result){
			    	 $("#divLPDetailsResult").html(result);
			     }, 
	 			error : function(err) {
	 				$.ajax({
						url: 'LearningPlanSummary.action?learningPlanId='+lPlanId,
						cache: true,
						success: function(result) {
							$("#divLPDetailsResult").html(result);
				   		}
					});
	 			}
			 });
		 } else {
			 $.ajax({
				url: 'LearningPlanSummary.action?learningPlanId='+lPlanId,
				cache: true,
				success: function(result) {
					$("#divLPDetailsResult").html(result);
		   		}
			});
		 }
	 }
	 
	 function closeLPlan(lPlanId, type) {
			//alert("openQuestionBank id "+ id)
			var pageTitle = 'Close Learning Plan';
			if(type=='view') {
				pageTitle = 'Close Learning Plan Reason';
			}
			var dialogEdit = '.modal-body';
			 $(dialogEdit).empty();
			 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			 $("#modalInfo").show();
			 $(".modal-title").html(''+pageTitle);
			 $.ajax({
					url : "CloseLearningPlan.action?lPlanId="+lPlanId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
		}
	 
</script>