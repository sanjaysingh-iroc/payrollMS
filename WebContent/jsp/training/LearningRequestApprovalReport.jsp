<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%-- <script src="scripts/ckeditor_cust/ckeditor.js"></script> --%>

<style>
    .list_req>li{
    padding-bottom: 5px;
    padding-top: 5px;
    border-bottom: 1px solid #F0F0F0;
    } 
    .list_req>li>span{
    top:0px;
    }
</style>

<g:compress>
    <script>
      
        
        function GetXmlHttpObject() {
            if (window.XMLHttpRequest) {
                    return new XMLHttpRequest();
            }
            if (window.ActiveXObject) {
                    return new ActiveXObject("Microsoft.XMLHTTP");
            }
            return null;
        }
        
       
		
		function approveDeny(apStatus,learningId,userType,currUserType){
			var divResult = 'divResult';
			var strBaseUserType = document.getElementById("strBaseUserType").value;
			//var currUserType = document.getElementById("currUserType").value;
			if(strBaseUserType == '<%=IConstants.CEO %>' || strBaseUserType == '<%=IConstants.HOD %>') {
				divResult = 'subDivResult';
			}
			var status = '';
			if(apStatus == '1'){
				status='approve';
			} else if(apStatus == '-1'){
				status='deny';
			}
			//alert(divResult);
			if(confirm('Are you sure, do you want to '+status+' this request?')){
				var reason = window.prompt("Please enter your "+status+" reason.");
				if (reason != null) {
					//alert("divResult ===>>" + divResult);
					
					$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
					$.ajax({
						type : 'POST',
						url: 'ApproveLearningRequest.action?apType=auto&apStatus='+apStatus+'&lNomineeId='+learningId
								+'&mReason='+reason +'&userType='+userType+'&currUserType='+currUserType,
						data: $("#"+this.id).serialize(),
						success: function(result){
							//alert("result ===>>" + result);
				        	$("#"+divResult).html(result);
			 			},
			 	//===start parvez date: 11-10-2021=== 
			 			error: function(res){
			 				$.ajax({
			 					url: 'LearningRequestApprovalReport.action',
			 					cache: true,
			 					success: function(result){
			 						
			 						$("#"+divResult).html(result);
			 			   		}
			 				});
			 			}
			 	//===end parvez date: 11-10-2021=== 
					});
				}
			}
		}
		
		function getApprovalStatus(lPlan_id){
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title').html('Work flow');
			$("#modalInfo").show();
			$.ajax({
				url : "GetLRApprovalStatus.action?effectiveid="+lPlan_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}
        
    </script>
</g:compress>

<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	    UtilityFunctions uF = new UtilityFunctions();
	    String strUserType = (String) session.getAttribute("USERTYPE");
		String currUserType = (String) request.getAttribute("currUserType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		//System.out.println("LRAR/88--strUserType="+strUserType);
		//System.out.println("LRAR/88--currUserType="+currUserType);
		//System.out.println("LRAR/88--strBaseUserType="+strBaseUserType);
	%>
	
	
	<s:form name="frm_LearningRequestApprovalReport" action="LearningRequestApprovalReport" theme="simple">
       	<input type="hidden" name="currUserType" id=currUserType value="<%=currUserType %>"/>
        <input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
        <s:hidden name="fromPage"></s:hidden>
            	<div class="box box-default collapsed-box">
					<div class="box-header with-border">
						<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						<div class="box-tools pull-right">
							<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						</div>
					</div>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (strBaseUserType != null && strBaseUserType.equals(currUserType)) || (strUserType!=null && !strUserType.equals(IConstants.CEO) && !strUserType.equals(IConstants.EMPLOYEE)) ) { %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>					
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="organisationList" key="" headerKey="" headerValue="All Organisations" onchange="submitForm('1');" value="strOrg" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<%-- <s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" /> --%>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="workLocationList" key="0" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select theme="simple" name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments"/>
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="submitForm('2');" list="levelList" key="" required="true"/>
								</div> --%>
									
							</div>
						</div><br>
					<% } %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="checkStatus" id="checkStatus" headerKey="-2" headerValue="All Status" list="#{'1':'Approved', '-1':'Denied', '0':'Pending'}"/>
								</div>
					<% 
						String fdate = (String)request.getAttribute("fdate");
						String tdate = (String)request.getAttribute("tdate");
						if(fdate == null || fdate.equals("null") || fdate.equals("")) {
							fdate = "";
						}
						if(tdate == null || tdate.equals("null") || tdate.equals("")) {
							tdate = "";
						}
					%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<input type="text" name="fdate" id="fdate" style="width: 100px !important;" value="<%=fdate %>" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date</p>
									<input type="text" name="tdate" id="tdate" style="width: 100px !important;" value="<%=tdate %>" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</div>
				</div>
   	</s:form>
   	  
    <table class="table table-bordered" id="lt">
				<thead>
					<tr>
						<th>Employee Name</th>
						<th>Learning Plan Name</th>
						<th>Apply date</th>
						<!-- <th>Manager Reason</th> -->
						<th>Status</th>
						<th>Approving Profile</th>
						<%if(uF.parseToBoolean(CF.getIsWorkFlow())){ %>
							<th width="10%" class=" alignLeft">WorkFlow</th>
						<%} %>
					</tr>
				</thead>
				<tbody>
				<% java.util.List couterlist = (java.util.List)request.getAttribute("requestList");
				//System.out.println("LRAR.jsp/204--requestList"+couterlist);
					//System.out.println("reportList:"+couterlist);
				 for (int i=0; couterlist!=null && i<couterlist.size(); i++) { 
				 	java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
						<tr>
							<td nowrap="nowrap"><%=cinnerlist.get(0) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(1) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(2) %></td>
							<td nowrap="nowrap"><%=cinnerlist.get(5) %></td>
							<td><%= cinnerlist.get(7) %></td>
							<%if(uF.parseToBoolean(CF.getIsWorkFlow())) { %>
								<td class="alignLeft"><%=(String)cinnerlist.get(4) %></td>
							<% } %>
							
						</tr>
					<% } %>
				</tbody>
			</table>           
  
 <div class="custom-legends">
	<div class="custom-legend pullout">
		<div class="legend-info">Waiting for workflow</div>
	</div>
	<div class="custom-legend pending">
		<div class="legend-info">Waiting for approval</div>
	</div>
	<div class="custom-legend approved">
		<div class="legend-info">Approved</div>
	</div>
	<div class="custom-legend denied">
		<div class="legend-info">Denied</div>
	</div>
</div>   

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
        <!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">-</h4>
			</div>
			<div class="modal-body" style="height:400px; overflow-y:auto; padding-left: 25px;"></div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>              
                   
<script type="text/javascript" charset="utf-8">
    $(function(){
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
    
    	$("#fdate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#tdate').datepicker('setStartDate', minDate);
        });
        
        $("#tdate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#fdate').datepicker('setEndDate', minDate);
        });
    });
    
	function submitForm(type) {
		var currUserType = "<%=currUserType%>";
		var org = "";
		if(document.getElementById("f_org"))
    		org = document.getElementById("f_org").value;
		var location = "";
    	if(document.getElementById("f_strWLocation"))
    		location = document.getElementById("f_strWLocation").value;
    	var department = "";
    	if(document.getElementById("f_department"))
			department = document.getElementById("f_department").value;
    	
    	var level = "";
    	if(document.getElementById("f_level")) {
    		level = getSelectedValue("f_level");
    	}
		var checkStatus = document.getElementById("checkStatus").value;
		var fdate = document.getElementById("fdate").value;
		var tdate = document.getElementById("tdate").value;
		
		var divResult = 'divResult';
		var strBaseUserType = document.getElementById("strBaseUserType").value;
		var strCEO = '<%=IConstants.CEO %>';
		var strHOD = '<%=IConstants.HOD %>';
		
		if(strBaseUserType == strCEO || strBaseUserType == strHOD) {
			divResult = 'subDivResult';
		}
		
		var paramValues = "";
		
		if(type=='1'){
			paramValues = '&currUserType='+currUserType;
			}
		
		if(type != "" && type == '2') {
			paramValues = '&f_department='+department+'&f_strWLocation='+location+'&checkStatus='+checkStatus
			+'&f_level='+level+'&fdate='+fdate+'&tdate='+tdate+'&currUserType='+currUserType;
		}
		
    	var action = 'LearningRequestApprovalReport.action?f_org='+org+paramValues;
    	
    	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
    			$("#"+divResult).html(result);
       		}
    	});
    	
    }
</script>