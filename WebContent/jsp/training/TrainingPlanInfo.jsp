

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<style>
#lt_wrapper .row{
margin-left: 0px;
margin-right: 0px;
}
</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>  --%>
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.css' />
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.print.css' media='print' />
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/fullcalendar/fullcalendar.min.js"> </script>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
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
	

	function addRequest(operation,id) {

		var action="AddTrainingPlan.action?operation="+operation;
		if(id=='')
		window.location=action;
		else {
			action+="&ID="+id;
			window.location=action;
		}
	}
	
	function previewcertificate(id, certiName){
		  
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html(''+ certiName);
		 $.ajax({
				url : 'ViewCertificate.action?ID='+id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	  }
	
	
	function trainingCalendar(trainingId){
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('View Training Schedule Calendar');
		 $.ajax({
				url : "TrainingCalendarPopup.action?trainingId="+trainingId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		 /* $.ajax({
				url : "TrainingCalendarPopup.action?fromPage=LD&trainingId="+trainingId,
				cache : false,
				success : function(data) {
					
					document.getElementById("divTraining_cal").style.display ="block";
					document.getElementById("close_divTraining_cal").style.display ="block";
					document.getElementById("divTraining_cal").innerHTML =data; 
					
				}
			}); */
	  }
	
function closeCalender(){
		
		document.getElementById("divTraining_cal").style.display ="none";
		document.getElementById("close_divTraining_cal").style.display ="none";
		
	  }
	
	
function trainingScheduleAllDayDetails(trainingId){
		
		var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('View Training Schedule Details');
		 $.ajax({
				url : "TrainingScheduleAllDayDetails.action?trainingID="+trainingId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	  }

</script>

<%
	UtilityFunctions uF=new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String sbDataTP = (String) request.getAttribute("sbDataTP");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	//System.out.println("sbDataTP==>"+sbDataTP);

%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> 
        	<div class="col-md-12 no-padding" style="margin-bottom: 15px;">
				<div class="col-md-3 no-padding">
					<input type="text" id="strSearchJobTp" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
				</div>
	       
				<script>
			       $(function(){
			    	   $("#strSearchJobTp" ).autocomplete({
							source: [ <%=uF.showData(sbDataTP,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
					 <% if (strUserType != null &&  (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
							<a href="javascript:void(0)" style="float:right;" onclick="addTrainingPlan('LD')">
						    <input type="button" class="btn btn-primary pull-right" value="Add New Training Plan"></a> 					
					<%} %>
				</div>
			</div>	
         
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;">
					<div class="box box-primary">
						<div class="box-body">
						<div  id="divTPNamesResult" style="min-height: 600px;">
							
				        </div>
					</div>
					 <div class="custom-legends">
						<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Scheduled</div></div>
						<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp;Unscheduled</div></div>
					</div>
			 </div>
		  </div>    
	   
				<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
					<div class="box box-primary" style=" min-height: 600px;" id="actionResult">
					  <div class="box box-none">
		                 <div  id="subDivTPResult" style="min-height: 600px;">
					
		                 </div>
		             </div> 
				  </div>
			   </div>
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


<script type="text/javascript">
	$(document).ready(function(){
		getTrainingPlansNameList('TrainingPlansNameList','LD','<%=strSearchJob%>');
	});

	 function addTrainingPlan(fromPage) {
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'POST',
				url: 'AddTrainingPlan.action?operation=A&frmpage='+fromPage,
				cache: true,
				success: function(result){
					//alert("addTrainingplan result1==>"+result);
					$("#divResult").html(result);
		   		}
			});
	 }
	 
	 function getTrainingPlansNameList(strAction,fromPage,strSearch) {
		// alert("strSearch==>"+strSearch);
			$("#divTPNamesResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({ 
				type : 'GET',
				url: strAction+'.action?fromPage='+fromPage+'&strSearchJob='+strSearch,
				cache: true,
				success: function(result){
					$("#divTPNamesResult").html(result);
		   		}
			});
	 }


	 function submitForm(type) {
			var strSearch = document.getElementById("strSearchJobTp").value;
			//alert("strSearch==>"+strSearch);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'TrainingPlanInfo.action?strSearchJob='+strSearch,
				success: function(result){
		        	$("#divResult").html(result);
		        }
			});
			
		}
	
</script>
