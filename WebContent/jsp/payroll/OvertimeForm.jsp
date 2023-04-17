<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	String strpaycycle1=(String)request.getAttribute("paycycle");
	//System.out.println("paycycle----"+strpaycycle1);
	
	String fromPage=(String)request.getAttribute("fromPage");
	//System.out.println("fromPage----"+fromPage);
 %>

<script> 
function Payroll_dashboard_link(strpaycycle1)
{
	window.location='PayrollDashboard_2.action?strpaycycle1='+strpaycycle1;
}


function prevOvertime(emp_id) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Overtime Details');
	 $.ajax({
			url : "PrevOvertime.action?strEmpId="+emp_id,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


function validateField(id){
	var field = document.getElementById("idStrIncentiveAmount"+id);
	if(field.value==''){
		alert('Please enter valid amount');
		return false;
	}else{
		return true;
	}
}


function submitForm(type){
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle;
	}
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'OvertimeForm.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#subDivResult").html(result);
   		}
	});
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

</script>



<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Overtime" name="title"/>
</jsp:include> --%>  

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_Overtime" action="OvertimeForm" theme="simple" method="post">
			<div class="box box-default collapsed-box">
                <div class="box-header with-border">
                    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select id="paycycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
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
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
                </div>
                <!-- /.box-body -->
            </div>
		
		<!-- <div style="float:right; margin:10px 0px 0px 0px; text-align: right; width: 100%;"><a href="OverTimeHours.action">Approve Overtime</a></div> -->
		<%
			List alEmpReport = (List) request.getAttribute("alEmpReport");
			Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
			Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
			Map hmOvertime = (Map) request.getAttribute("hmOvertime");
			Map hmOvertimeId = (Map) request.getAttribute("hmOvertimeId");
			Map hmOvertimeValue = (Map) request.getAttribute("hmOvertimeValue");
			
			List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
			if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
			
			UtilityFunctions uF = new UtilityFunctions();
			
			Map<String, String> hmEmpOverTimeHRsAmt = (Map<String, String>) request.getAttribute("hmEmpOverTimeHRsAmt");
			if(hmEmpOverTimeHRsAmt == null) hmEmpOverTimeHRsAmt = new HashMap<String, String>();
			
			String roundOffCondition = (String)request.getAttribute("roundOffCondition");
		%>  
    
		<table class="table table-bordered">
			<tr>	
				<th align="center">Employee Name</th>
				<th align="center">Percent (%) of Salary Heads</th>
				<th align="center">&nbsp;</th>
				<th align="center">Fixed Amount</th>
				<th align="center" colspan="2">Action</th>
			</tr>
			<%
            	int i = 0;
            	for (; alEmpReport != null && i < alEmpReport.size(); i++) {
           			List alEmpReportInner = (List) alEmpReport.get(i);
           			String payStatus="0";
           			if(ckEmpPayList.contains((String) alEmpReportInner.get(0))){
           				payStatus="1";
           			}
            %>
			<tr>
				<td nowrap="nowrap"><%=(String) alEmpReportInner.get(1)%>
					<input type="hidden" id="idStrEmpId<%=i%>" name="strEmpId" value="<%=(String) alEmpReportInner.get(0)%>">
				</td>
				<td align="center" style="background-color: #efe;" nowrap="nowrap">
					<input style="width:100px !important; text-align: right" type="text" id="idStrIncentivePercent<%=i%>" name="strIncentivePercent"> of 
					<select style="width:122px !important;" id="salaryId_<%=i%>">
					<%
					List alSalaryDetails = (List) hmSalaryList.get((String) alEmpReportInner.get(0));
						for (int x = 0; alSalaryDetails != null && x < alSalaryDetails.size(); x++) {
					%>
						<option value="<%=alSalaryDetails.get(x)%>"><%=(String) hmSalaryHeadsMap.get(alSalaryDetails.get(x))%></option>
					<% } %>
					</select>
				</td>
				<td align="center"> - OR - </td>
		    	<td align="center" style="background-color: #eee;">
				<%
					String strOvertimeAmt = "";
					if(hmOvertimeValue!=null && uF.parseToDouble(""+hmOvertimeValue.get((String) alEmpReportInner.get(0))) > 0.0d){
						strOvertimeAmt = (String) hmOvertimeValue.get((String) alEmpReportInner.get(0));
					} else if(hmEmpOverTimeHRsAmt.get((String) alEmpReportInner.get(0)) !=null){
						strOvertimeAmt = hmEmpOverTimeHRsAmt.get((String) alEmpReportInner.get(0));
					}
				%>
					<input style="width:75px;text-align: right" type="text" id="idStrIncentiveAmount<%=i%>" name="strIncentiveAmount" value="<%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strOvertimeAmt)) %>">
				</td>
				<td align="center">
					<% if (hmOvertime != null && uF.parseToInt((String) hmOvertime.get((String) alEmpReportInner.get(0))) == 1) { %>
					<div id="myDiv_<%=i%>">
						<!-- <img src="images1/icons/approved.png" width="17px" />  -->
						<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
						<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
					</div>
					<% } else if (hmOvertime != null && uF.parseToInt((String) hmOvertime.get((String) alEmpReportInner.get(0))) == -1) { %>
					<div id="myDiv_<%=i%>">
						<!-- <img src="images1/icons/denied.png" width="17px" /> -->
						<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
						<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
					</div>
					<% } else if (hmOvertime != null && uF.parseToInt((String) hmOvertime.get((String) alEmpReportInner.get(0))) == 2) { %>
					<div id="myDiv_<%=i%>">
						 <%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png"/>
						<img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"/> --%>
						
						<i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
						<i class="fa fa-times-circle cross" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateOvertime.action?requestid=<%=uF.parseToInt((String) hmOvertimeId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>


					</div>
					<% } else if (hmOvertime != null && uF.parseToInt((String) hmOvertime.get((String) alEmpReportInner.get(0))) == 0) { %>
						<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>((validateField(<%=i%>))?getContent('myDiv_<%=i%>', 'UpdateOvertime.action?emp_id='+document.getElementById('idStrEmpId<%=i%>').value+'&salary_id='+document.getElementById('salaryId_<%=i%>').value+'&paycycle='+document.getElementById('paycycle').value+'&amt='+document.getElementById('idStrIncentiveAmount<%=i%>').value+'&percent='+document.getElementById('idStrIncentivePercent<%=i%>').value+'&count=<%=i%>'):'')<%} %>" value="Update"></div>
					<% } %>
				</td>
				<td><a href="javascript:void(0)" onclick="prevOvertime(<%=(String) alEmpReportInner.get(0)%>)">Previous Overtime</a></td>
			</tr>
		    <% }
		        if (i == 0) {
		    %>
			<tr><td colspan="6"><div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div></td></tr>
			<% } %>
		</table>
    </s:form>
	</div>
	<!-- /.box-body -->
	
	 <%if(fromPage != null && fromPage.equalsIgnoreCase("AO")){ %>
			<div>
			   <button type="button" id="PD_button"  onclick="Payroll_dashboard_link('<%= strpaycycle1%>')">Go Back to PayrollDashboard</button>
			</div>
		<%}%> 

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;"></div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>


	<script type="text/javascript">
		$(function(){
			$("#f_strWLocation").multiselect().multiselectfilter();
			$("#f_department").multiselect().multiselectfilter();
			$("#f_service").multiselect().multiselectfilter();
			$("#f_level").multiselect().multiselectfilter();
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
