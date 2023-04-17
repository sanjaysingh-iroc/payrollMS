<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	Map<String, Integer> hmChart1 = (Map<String, Integer>) request.getAttribute("hmchart1");
	Map<String, Integer> hmChart2 = (Map<String, Integer>) request.getAttribute("hmchart2");
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String dataType = (String)request.getParameter("dataType");
	//System.out.println("strSessionUserType -----> " + strSessionUserType);
%>

<style>
.site-stats li{
width: 15%;
}
</style>

<script type="text/javascript" charset="utf-8"> 
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
function showLPlanReason(lPlanReason) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Learning Plan Reason');
	 $.ajax({
			url : "LearningPlanReasonPopup.action?lPlanReason="+lPlanReason,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
function viewCertificate(id,viewMode) { 
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('View Certificate');
	 $.ajax({
			url : "ViewCertificate.action?ID="+id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}

	function addLearningPlan(operation) {
		//alert("add Learning Plan");
     	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
     		url : 'AddLearningPlan.action?operation='+operation,
     		cache : false,
     		success : function(data) { 
     			$("#divResult").html(data);
     		}
     	});
     }
	 
</script>

<section class="content">
  <s:hidden name="dataType"></s:hidden>
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
                <%
						int strLearningId;
						UtilityFunctions uF = new UtilityFunctions();
						List<List<String>> allLearningreport = (List<List<String>>) request.getAttribute("allLearningreport");
						if(allLearningreport == null) allLearningreport = new ArrayList<List<String>>();
						String sbDataLP = (String) request.getAttribute("sbDataLP");
						String strSearchJob = (String) request.getAttribute("strSearchJob");
						//System.out.println("sbDataLP jsp==>"+sbDataLP);
					%>
					 
					<%--  <%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %> --%>
					 
							<%				
									int totalLearning1 = 0;
									int totalLearners1 = 0;
									int totalLrnsPending1 = 0;
									int totalLrnsUnderLearning1 = 0;
									int totalLrnsFinalised1 = 0;
									
									for (int i = 0; i < allLearningreport.size(); i++) {
										List<String> alinner = (List<String>) allLearningreport.get(i);
				
										totalLearning1 = allLearningreport.size();
										totalLearners1 += uF.parseToInt(alinner.get(2));
										
										totalLrnsPending1 += uF.parseToInt(alinner.get(3));
										totalLrnsUnderLearning1 += uF.parseToInt(alinner.get(4));
										totalLrnsFinalised1 += uF.parseToInt(alinner.get(5)); 
									}
								%>
					    <ul class="site-stats-new paddingtop10">
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalLearning1 %></strong> <small>Learnings</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalLearners1 %></strong> <small>Learners</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalLrnsPending1 %></strong> <small>Pending</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalLrnsUnderLearning1 %></strong> <small>Ongoing</small></li>
							<li class="bg_lh" style="cursor: unset;"><strong><%=totalLrnsFinalised1 %></strong> <small>Finalised</small></li>
						</ul>
				
			
			
		<div class="col-md-12 no-padding" style="margin-bottom: 15px;">
				<div class="col-md-3 no-padding">
					<input type="text" id="strSearchJobLp" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
				</div>
	       
				<script>
			       $(function(){
			    	   $("#strSearchJobLp" ).autocomplete({
							source: [ <%=uF.showData(sbDataLP,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
					 <% if (strSessionUserType != null &&  (strSessionUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.MANAGER) || strSessionUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
							<a href="javascript:void(0)" onclick="addLearningPlan('A')">
							<input type="button" class="btn btn-primary pull-right" value="Add New Learning Plan"></a>
						
					<%} %>
				</div>
			</div>	
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;">
					<div class="box box-thin">
						<div class="nav-tabs-custom">
				             <ul class="nav nav-tabs">
				                 <!-- Started By Dattatray Date:29-09-21 Note: id and index pass-->
				                 <li class="active"><a href="javascript:void(0)" onclick="getLearningPlanList('LearningPlanList','L','<%=strSearchJob %>','0');" data-toggle="tab" id="lpDash_0">Live</a></li>
				                 <li><a href="javascript:void(0)" onclick="getLearningPlanList('LearningPlanList','C','<%=strSearchJob %>','1');" data-toggle="tab" id="lpDash_1">Closed</a></li>
				             <!-- Ended By Dattatray Date:29-09-21  -->
				             </ul>
				             
				              <div  id="divLPResult" style="min-height: 600px;">
							
				              </div>
				            
				        </div>
					</div>
				</div>  
			
				<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
					<div class="box box-none" style="overflow-y: auto; min-height: 600px;" id="actionResult">
	                	 <div  id="subDivLPResult" style="min-height: 600px;">
	                	
	                	 </div>
					</div>
		   		</div>
	  </section>
    </div>
</section>


<div class="custom-legends">
	<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Published</div></div>
	<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp;Unpublished</div></div>
	<div class="custom-legend denied"><div class="legend-info">&nbsp;&nbsp;Closed</div></div>
</div>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
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
</div>

<script>

$(document).ready(function() {
	getLearningPlanList('LearningPlanList','L','<%=strSearchJob%>','0');//Created By Dattatray Date:19-10-21
});

function getLearningPlanList(strAction,dataType,strSearch,index){
	disabledPointerAddAndRemove(2,'lpDash_',index,true);//Created By Dattatray Date:19-10-21
	$("#divLPResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?dataType='+dataType+'&strSearchJob='+strSearch,
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divLPResult").html(result);
			disabledPointerAddAndRemove(2, 'lpDash_', index, false);//Created By Dattatray Date:19-10-21
   		}
	});
}

function submitForm(type) {
	var strSearch = document.getElementById("strSearchJobLp").value;
	//alert("strSearch==>"+strSearch);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LearningPlanDashboard.action?strSearchJob='+strSearch,
		success: function(result){
        	$("#divResult").html(result);
        }
	});
	
}
</script>