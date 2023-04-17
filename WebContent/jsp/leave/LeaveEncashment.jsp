<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

	<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		UtilityFunctions uF =  new UtilityFunctions();
		Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
		if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
		String policy_id = (String) request.getAttribute("policy_id");
		String strUserTYpe = (String)session.getAttribute(IConstants.USERTYPE); 
	%>


<script type="text/javascript" charset="utf-8">
 
$(function(){
 
	$("#strStartDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf()); 
        $('#strEndDate').datepicker('setStartDate', minDate);
    });
    
    $("#strEndDate").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strStartDate').datepicker('setEndDate', minDate);
    });
    
	$(".multiselect").prop('required',true);
	
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
	
	$("#lt").DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});
});

function isNumberKey(evt) {
   var charCode = (evt.which) ? evt.which : event.keyCode;
   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46){
      return false;
   }
   return true;
}

function checkDays(val){
	document.getElementById("strMessageId").innerHTML='';
	//document.getElementById("submitTrId").style.display='none';
	
	var availEncash = document.getElementById("strAvailableEncashment").value;
	var MaxEncash = document.getElementById("strMaxLeavesForEncashment").value;
	
	if(val =='' || parseFloat(val)==0){
		document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
	}else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(MaxEncash) == 0){
		
	}else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(val)<=parseFloat(MaxEncash)){
		
	}else{
		document.getElementById("strMessageId").innerHTML='You have entered more than available encashment.';
	}
} 

function checkDays1(){
	document.getElementById("strMessageId").innerHTML='';
	//document.getElementById("submitTrId").style.display='none';
	var val = document.getElementById("strNoOfDays").value;
	
	var availEncash=document.getElementById("strAvailableEncashment").value;
	var MaxEncash=document.getElementById("strMaxLeavesForEncashment").value;
	
	if(val =='' || parseFloat(val)==0){
		document.getElementById("strMessageId").innerHTML='You have entered 0 or none.';
		return false;
	}else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(MaxEncash) == 0){
		return true;
	}else if(parseFloat(val) >0 && parseFloat(val)<=parseFloat(availEncash) && parseFloat(val)<=parseFloat(MaxEncash)){
		return true;
	}else{
		document.getElementById("strMessageId").innerHTML='You have entered more than available encashment.';
		return false;
	}
} 

function getApprovalEncashment(approveStatus,leaveEncashId,empname,empId,userType,currUserType){
	///alert(userType);
	 
	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Leave Encashment Approval Status of '+empname);
		 $.ajax({
				url : 'UpdateLeaveEncashment.action?approveStatus='+approveStatus+'&leaveEncashId='+leaveEncashId+'&empId='+empId
						+'&userType='+userType+'&currUserType='+currUserType,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}

function getApprovalStatus(id,empname){
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Work flow of '+empname);
	 $.ajax({
			url : "GetLeaveApprovalStatus.action?effectiveid="+id+"&type=7", 
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
 
 
function applyEncashments(strPaycycle, policy_id, pageType) {
	
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Apply Leave Encashment');
	 $.ajax({
			url : 'ApplyLeaveEncashment.action?strPaycycle='+strPaycycle+'&policy_id='+policy_id+'&pageType='+pageType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}



function approveDeny(apStatus, leaveEncashId, userType) {
	//alert(userType);
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var currUserType = document.getElementById("currUserType").value;
			$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			/* alert('UpdateLeaveEncashment.action?approveStatus='+apStatus+'&leaveEncashId='+leaveEncashId+'&mReason='+reason
				+'&userType='+userType+'&currUserType='+currUserType); */
			$.ajax({
				type : 'POST',
				url: 'UpdateLeaveEncashment.action?approveStatus='+apStatus+'&leaveEncashId='+leaveEncashId+'&mReason='+reason
					+'&userType='+userType+'&currUserType='+currUserType,
				success: function(result){
		        	$("#"+divResult).html(result); 
		   		},
		   		error: function(result){
		   			$.ajax({
						url: 'LeaveEncashment.action?currUserType='+currUserType,
						cache: true,
						success: function(result){
							$("#"+divResult).html(result);
				   		}
					}); 
		   		}
			});
			/* var action = 'UpdateLeaveEncashment.action?approveStatus='+apStatus+'&leaveEncashId='+leaveEncashId+'&mReason='+reason+'&userType='+userType;
			window.location = action; */
		}
	}
}


function cancelLeaveEncashment(approveStatus, leaveEncashId) {
	if(confirm('Are you sure, you want to remove this?')) {
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'UpdateLeaveEncashment.action?approveStatus=2&leaveEncashId='+leaveEncashId,
			data: $("#"+this.id).serialize()/* ,
			success: function(result){
	        	$("#divResult").html(result);
	   		} */
		});
		
		$.ajax({ 
			url: 'LeaveEncashment.action',
			cache: true,
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
}


function submitForm(type){
	var divResult = 'divResult';
	var strBaseUserType = document.getElementById("strBaseUserType").value;
	var strCEO = '<%=IConstants.CEO %>';
	var strHOD = '<%=IConstants.HOD %>';
	
	if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
		divResult = 'subDivResult';
	}
	var currUserType = document.getElementById("currUserType").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	//alert("service ===>> " + service);
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveEncashment.action?strStartDate='+strStartDate+'&strEndDate='+strEndDate+'&currUserType='+currUserType,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#"+divResult).html(result);
   		}
	});
}

</script>



    <%
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	%>
	
	        <%-- <% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>	
				<div class="box box-none nav-tabs-custom">
					<ul class="nav nav-tabs">
						<li class="<%=(currUserType == null || currUserType.equals("MYTEAM")) ? "active" : "" %>"><a href="javascript:void(0)" onclick="window.location='LeaveEncashment.action?currUserType=MYTEAM'" data-toggle="tab">My Team</a></li>
						<li class="<%=(currUserType == null || currUserType.equals(strBaseUserType)) ? "active" : "" %>"><a href="javascript:void(0)" onclick="window.location='LeaveEncashment.action?currUserType=<%=strBaseUserType %>'" data-toggle="tab"><%=strBaseUserType %></a></li>
					</ul>
				</div>
			<% }else{ %>
			<div class="box box-primary">
			<% } %> --%>
                <% if(strUserTYpe!=null && (strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserTYpe.equalsIgnoreCase(IConstants.ARTICLE) || strUserTYpe.equalsIgnoreCase(IConstants.CONSULTANT))){%>
					<div class="box-header with-border">
	                    <h3 class="box-title">Applied Encashments</h3>
	                </div>
				<%} else { %>
					<!-- <div class="box-header with-border" style="padding: 3px 10px;">
	                    <h3 class="box-title">Encashment Requests</h3>
	                </div> -->
				<% } %>
                
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height:600px;">
                <div class="box box-default collapsed-box">
					<div class="box-header with-border">
					    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					    <div class="box-tools pull-right">
					        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					    </div>
					</div>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<s:form name="frm_SectionReport" action="LeaveEncashment" theme="simple">
							<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
							<s:hidden name="currUserType" id="currUserType"/>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">From Date</p>
										<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
							     		<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" />
									</div>	
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
							    		<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
						</s:form>
						
					</div>
				</div>
		
			<% if(strUserTYpe!=null && (strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserTYpe.equalsIgnoreCase(IConstants.ARTICLE) || strUserTYpe.equalsIgnoreCase(IConstants.CONSULTANT))){ %>
				<div class="col-md-12 col_no_padding" style="margin: 10px 0px;">
					<a href="javascript:void(0)" onclick="applyEncashments('<%=(String)request.getAttribute("strPaycycle") %>', '<%=(String)request.getAttribute("policy_id") %>', '<%=(String)request.getAttribute("pageType") %>')" title="Apply Encashment"><i class="fa fa-plus-circle" aria-hidden="true"></i> Apply Encashment</a>
				</div>
			<% } %>
        		
				<table class="table table-bordered" id="lt">
					<thead>
						<tr>
							<th style="text-align: left; width: 80%;">Encashment</th>
							<th style="text-align: left;">Workflow</th> 
						</tr>
					</thead>
					<tbody>
					<% 	java.util.List cOuterList = (java.util.List)request.getAttribute("alReport");
						for (int i=0; cOuterList!=null && i<cOuterList.size(); i++) {
					 	java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
						<tr>
							<td><%=cInnerList.get(0) %></td>
							<td><%=cInnerList.get(1) %></td>
						</tr>
						<% } %>
					</tbody>
				</table>
              </div>

<%-- <% if(strUserTYpe!=null && (strUserTYpe.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserTYpe.equalsIgnoreCase(IConstants.ARTICLE) || strUserTYpe.equalsIgnoreCase(IConstants.CONSULTANT))){%>
</div>
<%} %> --%>



<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>


