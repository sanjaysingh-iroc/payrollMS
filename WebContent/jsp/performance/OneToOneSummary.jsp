<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<script src="scripts/ckeditor_cust/ckeditor.js"></script> 
<%-- <script src="js/customJsForAppraisalSummary.js"></script> --%>
<script type="text/javascript" src="scripts/customAjax.js"></script>
<%-- <script src="js/customAjaxForReview3.js"></script> --%>
<jsp:include page="../performance/CustomJsForAppraisalSummary.jsp"></jsp:include>
<jsp:include page="../performance/CustomAjaxForReview.jsp"></jsp:include>

<%

String finalFlag = null;
String flagProcess = null;
String fromPage =  null;
Map<String,List<String>> hmOneToOneDetails =(Map<String,List<String>>)request.getAttribute("hmOneToOneDetails");
if(hmOneToOneDetails == null) hmOneToOneDetails = new HashMap<String,List<String>>();
UtilityFunctions uF = new UtilityFunctions();

%>




<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
        	  <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
        	   <section class="content">
						    <div class="row jscroll">
						        <section class="col-lg-12 connectedSortable" style="margin-top: 10px;">
						        <div class="box box-primary" style="border-top: 3px solid #d2d6de;">
						        	<% if(hmOneToOneDetails != null && hmOneToOneDetails.size()>0) { 
								                 			Iterator<String> it = hmOneToOneDetails.keySet().iterator();
								                 				while(it.hasNext()) {
								                 					String strId = it.next();
								                 					List<String> gInner = hmOneToOneDetails.get(strId);
								                 					System.out.println("gInner::"+gInner);
								                 	%> 
								                <div class="box-header with-border">
								                    <h3 class="box-title">
								                    	<%if(!(uF.parseToBoolean(gInner.get(6)))){ %>
								                    		<div id="myDivM" style="float:left">
								                    			<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this one-to-one?'))getPublishOneToOne('<%=strId%>','publish');">
								                    				<img src="images1/icons/icons/unpublish_icon_b.png" title="Waiting to be publish">
								                    			</a>
								                    		</div>
								                    		<%} %>
								                    		<%if(uF.parseToBoolean(gInner.get(6))){ %>
								                    			<div id="myDivM" style="float:left">
								                    				<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this one-to-one?'))getPublishOneToOne('<%=strId%>','unPublish');">
								                    					<img src="images1/icons/icons/publish_icon_b.png" title="Published">
								                    				</a>
								                    			</div>
								                    		<%} %>
								                    		<a href="javascript: void(0)" onclick="openEditOneToOne('<%=strId%>')" title="Edit OneToOne"><i class="fa fa-pencil-square-o"></i></a>
															<a title="Delete" style="color:#F02F37;" href="javascript:void();" onclick="deleteOneToOne('<%=strId%>')" ><i class="fa fa-trash" aria-hidden="true"></i></a>
													</h3>
								                </div>
								                 <div class="box-body" style="padding: 5px; overflow-y: auto;">
								                 <table class="table table-striped" width="100%">
															<tr>
																<th valign="top" align="right">Name:</th><td colspan="1"><%=gInner.get(0)%></td>
															</tr>
															<tr>
																<th valign="top" align="right">Description:</th><td colspan="1"><%=gInner.get(1)%></td>
															</tr>
															<tr>
																<th align="right">Effective Date:</th><td><%=gInner.get(3)%> </td>
															</tr>
															<tr>
																<th align="right">Due Date:</th><td><%=gInner.get(4)%> </td>
															</tr>
															<tr>
																<th align="right">Reviewer:</th><td colspan="1"><%=gInner.get(2)%></td>
															</tr>
															<tr>
																<th align="right">Goals:</th><td colspan="1"><%=gInner.get(5)%></td>
															</tr>
														</table>
												</div>	
													<%} 
								                 	}else{%>
								                 		<div class="nodata msg">No Summary avaialble.</div>
								                 		
								                 	<% }
													%>
									            
								</div>
								</section>
							</div>
				</section>
				</div>
			</div>
		</section>
	</div>
</section>
<script type="text/javascript">

function openEditOneToOne(id){
	///alert("create");
	$("#divReviewsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: 'CreateOneToOne.action?oneToOneId='+id+'&opeartion=E',
		cache: true,
		success: function(result){
			//alert("result1==>"+result);
			$("#divReviewsResult").html(result);
   		}
	});
}
function deleteOneToOne(id){
	if(confirm('Are you sure you want to delete this one to one?')) {
		//alert("fromPage 00 ===>> " + fromPage);
		$.ajax({
			url: 'CreateOneToOne.action?oneToOneId='+id+'&opeartion=D'
		});
		$.ajax({
			url: 'OneToOneNamesList.action',
			cache: true,
			success: function(result){
				//alert("result ===>> " + result);
				$("#oneToOneNameResult").html(result);
	   		}
		});
	}	
		
}

function getPublishOneToOne(id,operation){
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
    }else{
	 var xhr = $.ajax({
         url : "PublishOneToOne.action?oneToOneid=" + id + '&opeartion=' +operation,
         cache : false,
         success : function(data) {
      	   		//alert("data ===>> " + data);
         		var allData = data.split("::::");
         		document.getElementById("myDivM"+dcount).innerHTML = allData[0];
         	}
         });
    }
    
    
}
function openEditOneToOne111(id) {

		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="divReviewsResult"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Edit  One-To-One Details');
		if($(window).width() >= 1100) {
		  $(".modal-dialog").width(1100);
		}
	 	$.ajax({
			url : "EditorDelateOneToOne.action?oneToOneId="+id+"&opeartion=E",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}
</script>
								   
								                
								                