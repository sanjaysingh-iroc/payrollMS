 <%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<script type="text/javascript" src="js_bootstrap/jQuery/jQuery-3.1.1.min.js"></script>



<style> 
	.listMenu1 .icon .fa{
		font-size: 45px;
		vertical-align: top;
		margin-top: 20px;
	}
	
.vl {
  border-left: 2px solid black;
  height: auto;
}

</style>

<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
		UtilityFunctions uF = new UtilityFunctions();
		
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String currUserType = (String) request.getAttribute("currUserType");  
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		
		List<String> alLeaveCountList=(List<String>)request.getAttribute("alLeaveCountList");
		List<String> alListAttendanceCountList=(List<String>)request.getAttribute("alListAttendanceCountList");
		List<String> alList_ApprovePay=(List<String>)request.getAttribute("alList_ApprovePay");
		List<String> alList_ApprovePayroll=(List<String>)request.getAttribute("alList_ApprovePayroll");
		
		String strpaycycle1=(String)request.getAttribute("strpaycycle1");
	    String exceptionCount =(String)request.getAttribute("exceptionCount");
		String Overtime_approvedCount=(String)request.getAttribute("Overtime_approvedCount");
		String Overtime_unApprovedCount=(String)request.getAttribute("Overtime_unApprovedCount");
		
		
		List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");
		
 %>
		

<script type="text/javascript">

$(document).ready(function(){
	
	/* $("body").on('click','#skip1',function(){
		$("#div1").hide();
		$("#div2").show();
		$("#div3").show();
		$("#div4").show();
		$("#div5").show();
	});
	
	$("body").on('click','#skip2',function(){
		$("#div2").hide();
		$("#div1").show();
		$("#div3").show();
		$("#div4").show();
		$("#div5").show();
	});
	
	$("body").on('click','#skip3',function(){
		$("#div3").hide();
		$("#div2").show();
		$("#div1").show();
		$("#div4").show();
		$("#div5").show();
	});
	
	$("body").on('click','#skip4',function(){
		$("#div4").hide();
		$("#div2").show();
		$("#div1").show();
		$("#div3").show();
		$("#div5").show();
	});
	
	$("body").on('click','#skip5',function(){
		$("#div5").hide();
		$("#div2").show();
		$("#div1").show();
		$("#div3").show();
		$("#div4").show();
	}); */
});	

 function approveLeave(strpaycycle1){
	 
	//alert("in approve leave");	
	if(confirm('Are you sure, do you want to ApproveAll this Leave?')){
		var reason = window.prompt("Please enter your reason.");
		//alert("reason=="+reason);
		if (reason != null) {
			
			//window.location='PayrollDashboard_2.action?formType=approve&strpaycycle1='+encodeURIComponent(strpaycycle1)+'&mReason='+reason ;
			
			 $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'PayrollDashboard_2.action?formType=approve&strpaycycle1='+strpaycycle1+'&mReason='+reason,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	$("#divResult").html(result);
		   		}
			}); 
		}
	}
} 
 

 function ViewApprovalPage(strpaycycle1) {
	 window.location='Approvals.action?fromPage=P&strpaycycle1='+encodeURIComponent(strpaycycle1);
 }
 
 function ClockOnOffException_Page(strpaycycle1) {
	 var uri='TimeApprovals.action?callFrom=HRDashTimeExceptions&fromPage=CP&strpaycycle1='+encodeURIComponent(strpaycycle1);
	 window.location=uri;
 }
 
 function ApprovePay_overtimePage(strpaycycle1) {
	 window.location='PayApprovals.action?callFrom=NotiOvertime&fromPage=AO&paycycle='+encodeURIComponent(strpaycycle1);
 }
 
 function Approvals_ApproveClockEntriesPage(strpaycycle1) {
	 window.location='TimeApprovals.action?fromPage=AC&strpaycycle1='+encodeURIComponent(strpaycycle1);
 }
 
</script>

	<div class="col-lg-9 col-md-8 col-sm-12">

		<%--  <div style="float: left;width:100%;margin-top: 10px;">
				<%  
				 String strMessage = (String) session.getAttribute("MESSAGE");
		       	 if (strMessage == null) {
		        	strMessage = "";
		       	 }else{%>
					<p class="message"><%=strMessage%></p>
					<% session.setAttribute("MESSAGE"," ");
		       	 } %>
		 </div>  --%>
	
	<s:form method="post" name="frm1" action="PayrollDashboard_2" theme="simple">
				<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
				<input type="hidden" name="currUserType" id="currUserType" value="<%=currUserType %>" />
				
			<div style="width: 100%; overflow-x: auto;">
				<% if(alLeaveCountList != null && alLeaveCountList.size()>0) { 
						String strBgClass = "bg-gray";
						if(uF.parseToInt(alLeaveCountList.get(0)) > 0 && uF.parseToInt(alLeaveCountList.get(1)) == 0) {
							strBgClass = "bg-red";
						} else if(uF.parseToInt(alLeaveCountList.get(0)) > 0 && uF.parseToInt(alLeaveCountList.get(1)) > 0 && uF.parseToInt(alLeaveCountList.get(0)) > uF.parseToInt(alLeaveCountList.get(1))) {
							strBgClass = "bg-yellow";
						} else if(uF.parseToInt(alLeaveCountList.get(0)) > 0 && uF.parseToInt(alLeaveCountList.get(1)) == uF.parseToInt(alLeaveCountList.get(0))) {
							strBgClass = "bg-green";
						}
				%>
				<div class="small-box <%=strBgClass %>" style="overflow-x: auto;padding-top: 10px">
					<div class="col-lg-2 col-xs-6 col-sm-12">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-umbrella" aria-hidden="true"></i>
						</div>
					</div>
					
					<div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px; text-align: center;">
							<h3 style="margin: 0px; font-size: 24px;"><%=alLeaveCountList.get(0) %></h3>
							<div style="margin-top: -5px;">Applied</div>
						</div>
					</div>
				 	
				    <div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px 2px; text-align: center;">
							<h4 style="margin: 0px; font-size:24px"><%=alLeaveCountList.get(1) %></h4>
							<div style="margin-top: -5px;">Approved/Denied</div>
						</div>
				    </div>
				
				</div>
				<% } %>
			</div>

	<div style="padding-top: 10px">
		
	<div id="div1">
		<div class="box" style="padding-top: 0px">
			<div class="box-header with-border">Approve Exceptions- Approved | <%= exceptionCount %> Unapproved
    				<div class="box-tools pull-right">
     				 <!-- Collapse Button -->
      				<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
      				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-minus"></i></button>
      				</div>
    				<!-- /.box-tools -->
  				</div>
 					<!-- /.box-header -->
 				  <div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
 				  
						 <input type="button" class="btn btn-primary"  data-dismiss="modal" id="skip1" value="Skip"/> 
						 <input type="button" class="btn btn-primary"  data-dismiss="modal" value="ApproveAll"/>
					   <input type="button" class="btn btn-primary"   data-dismiss="modal" onclick="ClockOnOffException_Page('<%=strpaycycle1%>')" value="ViewDetails"/>
				  </div>
  		 </div>
  	</div>
  	
  	<div id="div2">	
  			<% if(alLeaveCountList != null && alLeaveCountList.size()>0) { 
					int Unapproved_Count=uF.parseToInt(alLeaveCountList.get(0))-uF.parseToInt(alLeaveCountList.get(1));	
				%>
  			<div class="box" style="padding-top: 0px">
 	 			<div class="box-header with-border">Approve Leave- <%=alLeaveCountList.get(1)%> Approved | <%=Unapproved_Count%> Unapproved
    				<div class="box-tools pull-right">
     				 <!-- Collapse Button -->
      				<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
      				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-minus"></i></button>
      				</div>
    				<!-- /.box-tools -->
  				</div>
 					<!-- /.box-header -->
 				  <div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
  					   <input class="btn btn-primary"  data-dismiss="modal" type="button" id="skip2" value="Skip"/>
  					   
					 <%if(reportList!=null && reportList.size()>0){ %>
					 
					 	<input class="btn btn-primary"  data-dismiss="modal" type="button" value="ApproveAll" onclick="approveLeave('<%= strpaycycle1%>')"/> 				  
					  
					  <%} %>
					  
					   <input class="btn btn-primary"  data-dismiss="modal" type="button" onclick="ViewApprovalPage('<%= strpaycycle1%>')" value="ViewDetails"/>
				  </div>
  			</div>
  			<%} %>
  	 </div>		
  			
  		<div id="div3">		
  			<div class="box" style="padding-top: 0px">
 	 			<div class="box-header with-border">Import
    				<div class="box-tools pull-right">
     				 <!-- Collapse Button -->
      				<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
      				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-minus"></i></button>
      				</div>
    				<!-- /.box-tools -->
  				</div>
 					<!-- /.box-header -->
 				  <div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
  					   <input type="button"  data-dismiss="modal" class="btn btn-primary" id="skip3" value="Skip"/>
					   <input type="button"  data-dismiss="modal" class="btn btn-primary" value="ApproveAll"/>
					   <input type="button"  data-dismiss="modal" class="btn btn-primary" value="ViewDetails"/>
				  </div>
  			</div>
  		</div>
  		
  		<div id="div4">	
  			<div class="box" style="padding-top: 0px">
 	 			<div class="box-header with-border">Approve Overtime -<%=Overtime_approvedCount %> Approved | <%=Overtime_unApprovedCount %> Unapproved
    				<div class="box-tools pull-right">
     				 <!-- Collapse Button -->
      				<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
      				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-minus"></i></button>
      				</div>
    				<!-- /.box-tools -->
  				</div>
 					<!-- /.box-header -->
 				  <div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
  					    <input type="button"  data-dismiss="modal" class="btn btn-primary" 	id="skip4" value="Skip"/>
					   	<input type="button"  data-dismiss="modal" class="btn btn-primary"  value="ApproveAll"/>
					   	<input type="button" data-dismiss="modal"  class="btn btn-primary"  onclick="ApprovePay_overtimePage('<%=strpaycycle1%>')" value="ViewDetails"/>
				  </div>
  			</div>
  		</div>	
  			
  		<div id="div5">	
  			<% if(alListAttendanceCountList != null && alListAttendanceCountList.size()>0) { 
				%>
  			<div class="box" style="padding-top: 0px">
 	 			<div class="box-header with-border">Approve Clock Entries- <%=alListAttendanceCountList.get(0) %> Approved |  <%=alListAttendanceCountList.get(1) %> Unapproved
    				<div class="box-tools pull-right">
     				 <!-- Collapse Button -->
      				<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
      				<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-minus"></i></button>
      				</div>
    				<!-- /.box-tools -->
  				</div>
 					<!-- /.box-header -->
 				  <div class="box-body" style ="padding: 5px; overflow-y: auto; display: none;">
  					   <input type="button" data-dismiss="modal" class="btn btn-primary" id="skip5" value="Skip"/>
					   <input type="button" data-dismiss="modal" class="btn btn-primary" value="ApproveAll" />
					   <input type="button" data-dismiss="modal" class="btn btn-primary" onclick="Approvals_ApproveClockEntriesPage('<%=strpaycycle1%>')" value="ViewDetails"/>
				  </div>
  			</div>
  			<%} %>
		</div>		
	</div>
		
		<div style="width: 100%; overflow-x: auto;padding-top: 10px">
				<% if(alListAttendanceCountList != null && alListAttendanceCountList.size()>0 ) { 
					String strBgClass="bg-gray";
					if(uF.parseToInt(alListAttendanceCountList.get(1)) > 0 && uF.parseToInt(alListAttendanceCountList.get(0)) == 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(alListAttendanceCountList.get(1)) > 0 && uF.parseToInt(alListAttendanceCountList.get(0)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(alListAttendanceCountList.get(1)) > 0 && uF.parseToInt(alListAttendanceCountList.get(0)) == 0) {
						strBgClass = "bg-green";
					}
				%>
				<div class="small-box <%=strBgClass %>" style="overflow-x: auto;padding-top: 10px">
				 
					<div class="col-lg-2 col-xs-6 col-sm-12">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-clock-o" aria-hidden="true" ></i>
						</div>
					</div>
					
					<div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px; text-align: center;">
							<h3 style="margin: 0px; font-size: 24px;"><%=alListAttendanceCountList.get(0) %></h3>
							<div style="margin-top: -5px;">Approved</div>
						</div>
					</div>
				 	
				    <div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px 2px; text-align: center;">
							<h4 style="margin: 0px; font-size:24px;"><%=alListAttendanceCountList.get(1) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
				    </div>
				 </div>
				
				<% } %>
		</div>
		
		
		<div style="width: 100%; overflow-x: auto; padding-top: 10px"">
				<% if(alList_ApprovePay != null && alList_ApprovePay.size()>0 ) { 
					String strBgClass="bg-gray";
					if(uF.parseToInt(alList_ApprovePay.get(0)) == 0 && uF.parseToInt(alList_ApprovePay.get(1)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(alList_ApprovePay.get(0)) > 0 && uF.parseToInt(alList_ApprovePay.get(1)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(alList_ApprovePay.get(0)) > 0 && uF.parseToInt(alList_ApprovePay.get(1)) == 0) {
						strBgClass = "bg-green";
					}
				%>
				<div class="small-box <%=strBgClass %>" style="overflow-x: auto;padding-top: 10px">
					<div class="col-lg-2 col-xs-6 col-sm-12">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-check" aria-hidden="true" ></i>
						</div>
					</div>
					
					<div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px; text-align: center;">
							<h3 style="margin: 0px; font-size: 24px;"><%=alList_ApprovePay.get(0) %></h3>
							<div style="margin-top: -5px;">Processed</div>
						</div>
					</div>
				 	
				    <div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px 2px; text-align: center;">
							<h4 style="margin: 0px;"><%=alList_ApprovePay.get(1) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
				    </div>
				 </div>
				<% } %>
		</div>
		
		
		<div style="width: 100%; overflow-x: auto;padding-top: 10px">
				<% if(alList_ApprovePayroll != null && alList_ApprovePayroll.size()>0 ) { 
					String strBgClass="bg-gray";
					if(uF.parseToInt(alList_ApprovePayroll.get(0)) == 0 && uF.parseToInt(alList_ApprovePayroll.get(1)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(alList_ApprovePayroll.get(0)) > 0 && uF.parseToInt(alList_ApprovePayroll.get(1)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(alList_ApprovePayroll.get(0)) > 0 && uF.parseToInt(alList_ApprovePayroll.get(1)) == 0) {
						strBgClass = "bg-green";
					}
				%>
				<div class="small-box <%=strBgClass %>" style="overflow-x: auto;padding-top: 10px">
					<div class="col-lg-2 col-xs-6 col-sm-12">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-money" aria-hidden="true" ></i>
						</div>
					</div>
					
					<div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px; text-align: center;">
							<h3 style="margin: 0px; font-size: 24px;"><%=alList_ApprovePayroll.get(0) %></h3>
							<div style="margin-top: -5px;">Paid</div>
						</div>
					</div>
				 	
				    <div class="col-lg-3 col-xs-6 col-sm-12 ">
						<div class="inner" style="padding: 0px 10px 2px; text-align: center;">
							<h4 style="margin: 0px; font-size:24px"><%=alList_ApprovePayroll.get(1) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
				    </div>
				 </div>
				<% } %>
		 </div>
	  </s:form>
   </div>


<div class="col-lg-3 col-md-6 col-sm-12">
	<ul class="timeline timeline-inverse">
	
		<li class="time-label" >
			<i class="fa fa-check-circle" style="font-size:24px"></i>
			<div  class="timeline-item">
				<h3 style="margin: 0px; font-size: 24px;">Approve Time</h3>
			</div>
		</li>

		<li>
			<i class="fa fa-check-circle" style="font-size:24px"></i>
			<div  class="timeline-item" >
				<h3 style="margin: 0px; font-size: 24px;">Approve Exception</h3>
			</div>
		</li>	
	
		<li>
			<i class="fa fa-minus-circle " style="font-size:24px"></i>
			<div  class="timeline-item" >
				<h3 style="margin: 0px; font-size: 24px;">Skipped</h3>
			</div>
		</li>	
	
		<li>
			<i class="fa fa-minus-circle" style="font-size:24px"></i>
			<div  class="timeline-item" >
				<h3 style="margin: 0px; font-size: 24px;">Waiting</h3>
			</div>
		</li>	
	
		<li>
			<i class="fa fa-minus-circle " style="font-size:24px"></i>
			<div  class="timeline-item" >
				<h3 style="margin: 0px; font-size: 24px;">Approve pay</h3>
			</div>
		</li>
	</ul>	

</div>
<div id="divResult"> </div>






