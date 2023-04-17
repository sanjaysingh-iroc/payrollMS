<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<style> 
	.listMenu1 .icon .fa{
		font-size: 45px;
		vertical-align: top;
		margin-top: 20px;
	}
</style>

<script type="text/javascript" src="js_bootstrap/jQuery/jQuery-3.1.1.min.js"></script>
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
	
	$("#lt").DataTable({
		"order": [],
		"columnDefs": [{
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect();
	$("#f_grade").multiselect();
	
});

function submitForm(type,strPaycycle) 
{
	
	var org = document.getElementById("f_org").value;
	
	var location = getSelectedValue("f_strWLocation");
	
	var department = getSelectedValue("f_department");

	var service = getSelectedValue("f_service");
	
	var level = getSelectedValue("f_level");
	
	var strGrade = getSelectedValue("f_grade");
	
	var strEmployeType = getSelectedValue("f_employeType");
	/* var startDate =document.getElementById("strStartDate").value;
	var endDate = document.getElementById("strEndDate").value;
	 */
	 
	 var paycycle = document.getElementById("paycycle").value;
		if(strPaycycle.length>0) {
			paycycle = strPaycycle;
		}
		
	var paramValues = "";
	
	if(type == '2') {
		paramValues = '&strLocation=' + location + '&strDepartment=' + department + '&strSbu=' + service 
			+ '&strLevel=' + level +'&paycycle='+paycycle+ '&strGrade='+strGrade + '&strEmployeType='+strEmployeType;
	}
	
	window.location='ProccessingDashboard_1.action?f_org=' + org + paramValues;
}

function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}

function funPayrollDashboard(strpaycycle1)
{
	window.location='PayrollDashboard_2.action? strpaycycle1='+strpaycycle1;
}

</script>
	<%
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
		UtilityFunctions uF = new UtilityFunctions();
		
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		//System.out.println("user Type===>"+strUserType);
		String currUserType = (String) request.getAttribute("currUserType");  
		//System.out.println("Current Type===>"+currUserType);
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		//System.out.println("Base Type===>"+strBaseUserType);
		
		String selectfilter=(String)request.getAttribute("selectedFilter");
		
		List<List<String>> alPaycycleList = (List<List<String>>) request.getAttribute("alPaycycleList");
		List<List<String>>alPaycycleList_ApproveAttendance=(List<List<String>>)request.getAttribute("alPaycycleList_ApproveAttendance");
		List<List<String>>alPaycycleList_ApprovePay=(List<List<String>>)request.getAttribute("alPaycycleList_ApprovePay");
		List<List<String>>alPaycycleList_approvePayroll=(List<List<String>>)request.getAttribute("alPaycycleList_approvePayroll");
	%>
	
		<div class="col-lg-3 col-md-3 col-sm-12 paddingright0 listMenu1" style="padding-left: 0px;">	
			<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
				<s:form name="frm" action="ProccessingDashboard_1" theme="simple">
					<input type="hidden" name="strBaseUserType" id=strBaseUserType value="<%=strBaseUserType %>"/>
					<input type="hidden" name="currUserType" id="currUserType" value="<%=currUserType %>" />
					<div class="box box-primary collapsed-box" style="border-top-color: #F0F0F0;">
						<div class="box-header with-border">
							<p class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></p>
							<div class="box-tools pull-right">
								<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button> 				
							</div>
						</div>
						
						<!-- /.box-header -->
						<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Status</p>
										<s:select theme="simple" name="leaveStatus" id="leaveStatus" list="#{'0':'All','1':'Approved', '2':'Pending','3':'Denied'}" onchange="document.frm_Bonus.submit();" />
									</div>
									<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER) && !strUserType.equals(IConstants.EMPLOYEE)) || (currUserType != null && currUserType.equals(strBaseUserType))) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organisation</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1','');" list="organisationList" key="" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Location</p>
											<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Department</p>
											<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Service</p>
											<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Level</p>
											<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Grade</p>
											<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Employee Type</p>
											<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
										</div>
									<% } %>
								</div>
							</div><br>
							<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date</p>
									<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" />
								</div> --%>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" onchange="submitForm('2', '');" list="paycycleList" key=""/>
								</div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2', '');" />
								</div>
							</div>
							</div>
						</div>
						
					</div>
				
				<!--*******************************************-->	
					
				<div class="box box-primary collapsed-box" style="border-top-color: #F0F0F0;">
						
						<div class="box-header with-border">
							<p class="box-title" style="font-size: 14px;"><u>Processes</u></p>
						</div>
						
						<div class="row row_without_margin">
							
							<div class="col-lg-12 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;"><i class="fa fa-umbrella" aria-hidden="true"></i></p>
								</div>
								<div class="col-lg-10 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status of Leave Apllied Vs Approved</p>
								</div>
								
							</div>
							
							
							<div class="col-lg-12 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;"><i class="fa fa-clock-o" aria-hidden="true"></i></p>
								</div>
								<div class="col-lg-10 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status of Time Approved Vs Waiting</p>
								</div>
							</div>
							
							<div class="col-lg-12 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;"><i class="fa fa-check" aria-hidden="true"></i></p>
								</div>
								<div class="col-lg-10 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status of Pay Approved Vs Waiting</p>
								</div>
							</div>
							
							<div class="col-lg-12 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;"><i class="fa fa-money" aria-hidden="true"></i></p>
								</div>
								<div class="col-lg-10 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status of Paid Vs Waiting to be paid </p>
								</div>
							</div>
							
						</div>
					
					</div>
		<!--**********************************************************  -->	
				</s:form>
			</div>
		</div>	
		
		<div class="col-lg-9 col-md-9 col-sm-12 paddingright0 listMenu1">
		
			<div style="width: 100%; overflow-x: auto;">
				<% if(alPaycycleList != null && alPaycycleList.size()>0) { 
					for(int i=0; i<alPaycycleList.size(); i++) {
						List<String> innerList = alPaycycleList.get(i);
						String strBgClass = "bg-gray";
						if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
							strBgClass = "bg-red";
						} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > uF.parseToInt(innerList.get(3))) {
							strBgClass = "bg-yellow";
						} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == uF.parseToInt(innerList.get(2))) {
							strBgClass = "bg-green";
						}
				 %>
					<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
					<!-- small box -->
						<div class="small-box <%=strBgClass %>">
							<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
								<i class="fa fa-umbrella" aria-hidden="true"></i>
							</div>
							<div class="inner" style="padding: 0px 10px; text-align: right;">
								<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
								<div style="margin-top: -5px;">Applied</div>
							</div>
							<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
								<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
								<div style="margin-top: -5px;">Approved/Denied</div>
							</div>
							<a href="javascript:void(0);" style="font-size: 12px;" onclick="funPayrollDashboard('<%=innerList.get(0) %>')" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a></div>
					</div>
					<% } %>
				<% } %>
			</div>
			
			
		<div style="width: 100%; overflow-x: auto;">
			<% if(alPaycycleList_ApproveAttendance != null && alPaycycleList_ApproveAttendance.size()>0) { 
				for(int i=0; i<alPaycycleList_ApproveAttendance.size(); i++) {
					List<String> innerList = alPaycycleList_ApproveAttendance.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) == 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-clock-o" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Approved</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="funPayrollDashboard('<%=innerList.get(0) %>')" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
				<% } %>
			<% } %>
		</div>
			
		<div style="width: 100%; overflow-x: auto;">
			<% 	if(alPaycycleList_ApprovePay != null && alPaycycleList_ApprovePay.size()>0) { 
				for(int i=0; i<alPaycycleList_ApprovePay.size(); i++) {
					List<String> innerList = alPaycycleList_ApprovePay.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(2)) == 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
		
					<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-check" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 5px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Processed</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="funPayrollDashboard('<%=innerList.get(0) %>')" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
					
		
				<% } %>
			<% } %>
		</div>
		
		
		<% 	if(alPaycycleList_approvePayroll != null && alPaycycleList_approvePayroll.size()>0) {
				for(int i=0; i<alPaycycleList_approvePayroll.size(); i++) {
					List<String> innerList = alPaycycleList_approvePayroll.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(2)) == 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-money" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Paid</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="funPayrollDashboard('<%=innerList.get(0) %>');" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
				<% } %>
			<% } %>
		</div>
		<!-- /.box-body -->
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

