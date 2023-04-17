

<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
	
	$(function() {
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

</script>
<%
UtilityFunctions uF=new UtilityFunctions();
String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
Map<String,List<String>> hmTrainers=(Map<String,List<String>>)request.getAttribute("hmTrainers");
if(hmTrainers == null) hmTrainers = new HashMap<String,List<String>>();
String proCount = (String) request.getAttribute("proCount");
String sbDataT = (String) request.getAttribute("sbDataT");
String strSearchJob = (String) request.getAttribute("strSearchJob");
//System.out.println("sbDatat jsp==>"+sbDataT);

%>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> 
           <div class="col-md-12 no-padding" style="margin-bottom: 15px;">
				<div class="col-md-3 no-padding">
					<input type="text" id="strSearchJobT" class="form-control" name="strSearchJob" placeholder="Search" value="<%=uF.showData(strSearchJob,"") %>"/>
					<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');">
				</div>
	       
				<script>
			       $(function(){
			    	   $("#strSearchJobT" ).autocomplete({
							source: [ <%=uF.showData(sbDataT,"") %> ]
						});
			       });
					
			  	</script>
			  	<div class="col-md-9 no-padding">
					 <% if (strUserType != null &&  (strUserType.equalsIgnoreCase(IConstants.HRMANAGER) || strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equalsIgnoreCase(IConstants.ADMIN))) {%>
							<a href="javascript:void(0);" onclick="addTrainer()" style="float:right;">
							<input type="button" class="btn btn-primary pull-right" value="Add New Trainer"></a> 					
					<%} %>
				</div>
			</div>	
          
			<div class="row row_without_margin">
				<div class="col-md-3" style="padding-left: 0px;">
					<div class="box box-primary">
						<div class="box-body">
							<div class="active tab-pane" id="divTrainerResult" style="min-height: 600px;">
							
				            </div>
				            <div class="custom-legends">
								<div class="custom-legend approved"><div class="legend-info">&nbsp;&nbsp;Internal Trainer</div></div>
								<div class="custom-legend pullout"><div class="legend-info">&nbsp;&nbsp;External Trainer</div></div>
							</div>
					  </div>
				</div>
		 	 </div>    
	   
				<div class="col-md-9" style="padding-left: 0px;min-height: 600px;">
					<div class="box box-thin" style=" min-height: 600px;" id="actionResult">
					  <div class="box-body">
			              <div class="active tab-pane" id="subDivTrainerResult" style="min-height: 600px;">
					
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
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getTrainersNameList('TrainersNameList','<%=strSearchJob%>');
	});

function getTrainersNameList(strAction,strSearch) {
	$("#divTrainerResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?strSearchJob='+strSearch,
		cache: true,
		success: function(result){
			//alert("getTrainersNameList result1==>"+result);
			$("#divTrainerResult").html(result);
   		}
	});
}

 function addTrainer() {
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'TrainerCheck.action?fromPage=LD',
		cache: true,
		success: function(result){
			//alert("addTrainer result1==>"+result);
			$("#divResult").html(result);
   		}
	});
	
} 

 function submitForm(type) {
		var strSearch = document.getElementById("strSearchJobT").value;
		//alert("strSearch==>"+strSearch);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'TrainerInfo.action?strSearchJob='+strSearch,
			success: function(result){
	        	$("#divResult").html(result);
	        }
		});
		
	}
</script> 
