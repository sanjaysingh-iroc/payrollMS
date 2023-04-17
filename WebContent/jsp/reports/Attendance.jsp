<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                <!-- Started By Dattatray Date:30-09-21  -->
                    <li class="active"><a  href="javascript:void(0)" onclick="getAttendancePage('AttendanceReport','0');" data-toggle="tab" id="attendanceId_0">Realtime Attendance</a></li>
                    <li><a href="javascript:void(0)" onclick="getAttendancePage('AttendanceRegister','1');" data-toggle="tab" id="attendanceId_1">Attendance Register</a></li>
                    <li><a href="javascript:void(0)" onclick="getAttendancePage('AttendanceRegisterInOut','2');" data-toggle="tab" id="attendanceId_2">Attendance Register Details</a></li>
              <!-- Ended By Dattatray Date:30-09-21  -->
                </ul>
                <div class="tab-content" >
                  <!-- ===start parvez date: 24-02-2023=== --> 
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;">
                  <!-- ===end parvez date: 24-02-2023=== -->  
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(function(){
	getAttendancePage('AttendanceReport','0');//Created By Dattatray Date:18-10-21
});

function getAttendancePage(strAction,index){
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(3,'attendanceId_',index,true);//Created By Dattatray Date:18-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(3,'attendanceId_',index,false);//Created By Dattatray Date:18-10-21
   		}
	});
}



</script>


