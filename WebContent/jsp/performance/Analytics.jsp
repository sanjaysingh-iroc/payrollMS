<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

<script type="text/javascript">
$(function(){
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo1").hide();
    });
	
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo1").hide(); 
	});
});

</script>

<script type="text/javascript">
$(function(){
	
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo1").hide();
    });
	
	$("body").on('click','.close1',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo1").hide(); 
	});
});

</script>


<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                     <!-- Started By Dattatray Date:28-09-21  Note: Id applied on all tab-->
                    <li class="active"><a href="javascript:void(0)" onclick="getAnalyticsPage('TeamPerformance','0');" data-toggle="tab" id="id0">Team Performance</a></li>
                    <li><a href="javascript:void(0)" onclick="getAnalyticsPage('EmployeePerformance','1');" data-toggle="tab" id="id1">Team KPIs</a></li>
                    <li><a href="javascript:void(0)" onclick="getAnalyticsPage('EmployeeComparison','2');" data-toggle="tab" id="id2">Team Comparison</a></li>
                    <li><a href="javascript:void(0)" onclick="getAnalyticsPage('AppraisalGraphReport',3);" data-toggle="tab" id="id3">Bell Curve</a></li>
               <!-- Ended By Dattatray Date:28-09-21  Note: Id applied on all tab-->
                </ul>
                <div class="tab-content" >
                <!-- ===start parvez date: 24-02-2023=== -->   
                    <div class="active tab-pane" id="divResult" style="min-height: 600px; max-height: 500px !important; overflow-y: hidden;">
				<!-- ===end parvez date: 24-02-2023=== -->		
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- 
<div class="backtop  pull-right">
	<i class="fa fa-question" onclick="getHelpPage('Analytics')"></i>
</div>
-->

<div class="modal" id="modalInfo1" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content1">
	            <div class="modal-header1">
	                <button type="button" class="close1" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title" style="font-weight:bold">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;">
	            </div>
	            <!-- <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>-->
	        </div>
	    </div>
	</div>

<script type="text/javascript" charset="utf-8">
$(function(){
	getAnalyticsPage('TeamPerformance','0');//Created By Dattatray Date:19-10-21
});

function getHelpPage(callPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$('.modal-title').html('About this Page');
	$("#modalInfo1").show();
	if($(window).width() >= 800){
		 $(".modal-dialog").width(800);
	 }
	$.ajax({
		url : "HelpPage.action?callPage="+callPage,
		cache : false,
		success : function(data) {
			/*alert("data ===>> " + data);*/
			$(dialogEdit).html(data);
		}
	});
}


function getAnalyticsPage(strAction,index){
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(4,'id',index,true);//Created By Dattatray Date:19-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(4,'id',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}

/* ===start parvez date: 24-02-2023=== */
$(window).bind('mousewheel DOMMouseScroll', function(event){
    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
        // scroll up
        if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
        	$("#divResult").scrollTop($("#divResult").scrollTop() - 30);
        }
    } else {
        // scroll down
        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
    		   $("#divResult").scrollTop($("#divResult").scrollTop() + 30);
   		}
    }
});

$(window).keydown(function(event){
	if(event.which == 40 || event.which == 34){
		if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
			$("#divResult").scrollTop($("#divResult").scrollTop() + 50);
   		}
	} else if(event.which == 38 || event.which == 33){
		if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
	    	$("#divResult").scrollTop($("#divResult").scrollTop() - 50);
	    }
	}
});
/* ===end parvez date: 24-02-2023=== */

</script>

