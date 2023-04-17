<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<style>
.list{
border-bottom: 1px solid #F1F1F1;
padding-bottom: 5px; 
margin-bottom: 5px;
}
</style>

<script type="text/javascript" charset="utf-8">
        $(function() {
        	
        	$("#fdate").datepicker({
        		format : 'dd/mm/yyyy'
        	});
        	$("#tdate").datepicker({ 
        		format : 'dd/mm/yyyy'
        	});

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
</script>
<g:compress>
    <script type="text/javascript" charset="utf-8">
       
        var dialogEdit2 = '#newVersionAssessmentDiv';
        function createNewVersionOfAssessment(assessmentId) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$('.modal-title').html('Create New Version Of Assessment');
        	$("#modalInfo").show();
        	$.ajax({
				url : "CreateNewVersionAssessmentPopup.action?assessmentId="+assessmentId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
        	}
        
        
        function closePopup(){
        	$(dialogEdit2).dialog('close');
        }
        
        
        var dialogEdit3 = '#newVersionCourseDiv';
        function createNewVersionOfCourse(courseId) {
        	var dialogEdit = '.modal-body';
        	$(dialogEdit).empty();
        	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
        	$('.modal-title').html('Create New Version Of Course');
        	$("#modalInfo").show();
        	$.ajax({
				url : "CreateNewVersionCoursePopup.action?courseId="+courseId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
        	}
        
        
        function closeCoursePopup(){
        	$(dialogEdit3).dialog('close');
        }
        
        
        function showAllCourses(value) {
        	//alert(value);
        	var status = document.getElementById("hideCourseDivStatus"+value).value;
        	if(status == '0'){
        		document.getElementById("hideCourseDivStatus"+value).value = "1";
        		document.getElementById("oldCoursesDIv"+value).style.display = "block";
        		document.getElementById("CuparrowSpan"+value).style.display = "block";
        		document.getElementById("CdownarrowSpan"+value).style.display = "none";
        	} else {
        		document.getElementById("hideCourseDivStatus"+value).value = "0";
        		document.getElementById("oldCoursesDIv"+value).style.display = "none";
        		document.getElementById("CuparrowSpan"+value).style.display = "none";
        		document.getElementById("CdownarrowSpan"+value).style.display = "block";
        	}
        }
        
        
        function showAllAssessments(value) {
        	//alert(value);
        	var status = document.getElementById("hideAssessmentDivStatus"+value).value;
        	if(status == '0'){
        		document.getElementById("hideAssessmentDivStatus"+value).value = "1";
        		document.getElementById("oldAssessmentsDIv"+value).style.display = "block";
        		document.getElementById("AuparrowSpan"+value).style.display = "block";
        		document.getElementById("AdownarrowSpan"+value).style.display = "none";
        	} else {
        		document.getElementById("hideAssessmentDivStatus"+value).value = "0";
        		document.getElementById("oldAssessmentsDIv"+value).style.display = "none";
        		document.getElementById("AuparrowSpan"+value).style.display = "none";
        		document.getElementById("AdownarrowSpan"+value).style.display = "block";
        	}
        }
    </script>
</g:compress>

<%
    UtilityFunctions uF=new UtilityFunctions();
    String strUserType = (String) session.getAttribute("USERTYPE"); 
    String dataType = (String) request.getAttribute("dataType");
    String strCourseId = (String) request.getAttribute("strCourseId");
    String strAssessId = (String) request.getAttribute("strAssessId");
 %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-none nav-tabs-custom">
            	<ul class="nav nav-tabs">
            	 <!-- Started By Dattatray Date:30-09-21  -->
			        <li class="active"><a href="javascript:void(0)" onclick="getCourseDashboardData('CourseDashboardData','C','0')" data-toggle="tab" id="cdID_0">Courses</a></li>
					<li><a href="javascript:void(0)" onclick="getCourseDashboardData('CourseDashboardData','A','1')" data-toggle="tab" id="cdID_1">Assessments</a></li>
				 <!-- ===start parvez date: 24-09-2021=== -->
				 	<li><a href="javascript:void(0)" onclick="getCourseDashboardData('VideosDashboard','V','2');" data-toggle="tab" id="cdID_2">Videos</a></li>
				<!-- ===end parvez date: 24-09-2021=== -->
				 <!-- Started By Dattatray Date:30-09-21  -->
				 </ul>
				 <div class="box box-none" style="overflow-y: auto; min-height: 600px;" id="actionResult">
					<div class="active tab-pane" id="divCDResult" style="min-height: 600px;">
					
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
                <h4 class="modal-title">Information</h4>
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
$(document).ready(function(){
	getCourseDashboardData('CourseDashboardData','C','0');
});

function getCourseDashboardData(strAction, dataType,index){
	//alert("getCourseDashboardData dataType=>"+dataType);
	disabledPointerAddAndRemove(3,'cdID_',index,true);//Created By Dattatray Date:30-09-21
	$("#divCDResult").html('<div id="the_div"><div id = "ajaxLoadImage"></div></div>');
	var form_data = $("#"+this.id).serialize();
	$.ajax({
		type:'POST',
		url:strAction+'.action?fromPage=LD&dataType='+dataType,
		data:form_data,
		success:function(result){
			//alert("result1==>"+result);
			$("#divCDResult").html(result);
			disabledPointerAddAndRemove(3,'cdID_',index,false);//Created By Dattatray Date:19-10-21
		}
	});
}
</script>