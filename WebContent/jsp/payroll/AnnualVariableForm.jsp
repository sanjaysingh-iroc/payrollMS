<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script>
function prevAnnualVariable(emp_id,empname,edtype) {
	var f_salaryhead=document.getElementById("f_salaryhead").value;
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Annual Variables of '+empname);
	$.ajax({
		url : "PrevAnnualVariable.action?strEmpId="+emp_id+"&SHID="+f_salaryhead,
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

function updateEarning(id){
	var field = document.getElementById("idStrIncentiveAmount"+id);
	if(field.value=='') {
		alert('Please enter valid amount');
	} else {
		var emp_id=document.getElementById('idStrEmpId'+id).value;
		var salary_id=document.getElementById('f_salaryhead').value;
		var paycycle=document.getElementById('paycycle').value;
		var amt=document.getElementById('idStrIncentiveAmount'+id).value;
		var action ='UpdateAnnualVariable.action?emp_id='+emp_id+'&salary_id='+salary_id+'&paycycle='+paycycle+'&amt='+amt+'&percent=0&count='+id;
		getContent('myDiv_'+id, action);		
	}
}

function submitForm(type) {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
	var f_salaryhead = document.getElementById("f_salaryhead").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level
			+'&strPaycycleDuration='+strPaycycleDuration+'&f_salaryhead='+f_salaryhead;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'AnnualVariableForm.action?f_org='+org+'&paycycle='+paycycle+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
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

function selectall(x,strEmpId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){
	  	arr[i].checked=status;
	}
	
	if(x.checked == true){
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
}

function checkAll(){
	
	var approveAll = document.getElementById("approveAll");		
	var strEmpIds = document.getElementsByName('strEmpIds');
	var cnt = 0;
	var chkCnt = 0;

	for(var i=0;i<strEmpIds.length;i++) {
		cnt++;
		 if(strEmpIds[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseInt(chkCnt) > 0) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		approveAll.checked = true;
	} else {
		approveAll.checked = false;
	}
}

function approveAnnualVariable(){
	if(confirm('Are you sure, you want to approve annual variable of selected employee?')){
		var data = $("#frm_AnnualVariableForm").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AnnualVariableForm.action?formType=approve',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		});
	}
}

</script>



<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Annual Variable Form" name="title"/>
</jsp:include>  --%>
  
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<s:form name="frm_AnnualVariableForm" id="frm_AnnualVariableForm" action="AnnualVariableForm" theme="simple" method="post">
			<div class="box box-default collapsed-box">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
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
								<p style="padding-left: 5px;">Duration</p>
								<s:select theme="simple" name="strPaycycleDuration" id="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" headerKey="" headerValue="Select Duration" onchange="submitForm('2');" list="paycycleDurationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select list="orgList" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select id="paycycle" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Salary Head</p>
								<s:select theme="simple" name="f_salaryhead" id="f_salaryhead" list="salaryHeadList" listKey="salaryHeadId" listValue="salaryHeadName"  headerKey="" headerValue="Select Salary Head" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Search" value="Search" class="btn btn-primary" onclick="submitForm('2');"/> 
							</div>
						</div>
					</div>	
                </div>
                <!-- /.box-body -->
            </div>
            
            <div style="float: left; margin-bottom: 24px; width: 100%;">
				<span id="unApproveSpan">
					<input type="button" name="Submit" class="btn btn-default" value="Approve" onclick="alert('Please select employee for approve annaul variable.');"/>
				</span>
				<span id="approveSpan" style="display: none;">
					<input type="button" value="Approve" name="Submit" class="btn btn-primary" onclick="approveAnnualVariable();"/>
				</span>
			</div>
							
			<%
				List<List<String>> alEmpReport = (List<List<String>>) request.getAttribute("alEmpReport");
				if(alEmpReport == null) alEmpReport = new ArrayList<List<String>>();
				Map<String, List<String>> hmSalaryList = (Map<String, List<String>>) request.getAttribute("hmSalaryList");
				if(hmSalaryList == null) hmSalaryList = new HashMap<String, List<String>>();
				Map<String, String> hmSalaryHeadsMap = (Map<String, String>) request.getAttribute("hmSalaryHeadsMap");
				if(hmSalaryHeadsMap == null) hmSalaryHeadsMap = new HashMap<String, String>();
				Map<String, String> hmAnnualVariable = (Map<String, String>) request.getAttribute("hmAnnualVariable");
				if(hmAnnualVariable == null) hmAnnualVariable = new HashMap<String, String>();
				Map<String, String> hmAnnualVariableId = (Map<String, String>) request.getAttribute("hmAnnualVariableId");
				if(hmAnnualVariableId == null) hmAnnualVariableId = new HashMap<String, String>();
				Map<String, String> hmAnnualVariableValue = (Map<String, String>) request.getAttribute("hmAnnualVariableValue");
				if(hmAnnualVariableValue == null) hmAnnualVariableValue = new HashMap<String, String>();
				
				UtilityFunctions uF = new UtilityFunctions();

				String sHeadType = (String) request.getAttribute("sHeadType");
				List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
				if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
				Map<String, String> hmEmpLevel = (Map<String, String>)request.getAttribute("hmEmpLevel");
				if(hmEmpLevel == null) hmEmpLevel = new HashMap<String, String>();
				Map<String, String> hmAnnualPolicyEmpAmt = (Map<String, String>)request.getAttribute("hmAnnualPolicyEmpAmt");
				if(hmAnnualPolicyEmpAmt == null) hmAnnualPolicyEmpAmt = new HashMap<String, String>();
				Map<String, String> hmFYAnnualEmpAmt = (Map<String, String>) request.getAttribute("hmFYAnnualEmpAmt");
				if(hmFYAnnualEmpAmt == null) hmFYAnnualEmpAmt = new HashMap<String, String>();				
			%>  
	    	<div class="row row_without_margin">
	    		<table class="table table-bordered" style="float:left;width:70%">
				    <tr>	
				    	<th class="alignCenter">Approve<br/><input type="checkbox" name="approveAll" id="approveAll" onclick="selectall(this,'strEmpIds')"/></th>
				    	<th style="text-align: center;">Employee Name</th>
				    	<th style="text-align: center;">Annual Variable Amount</th>
				    	<th style="text-align: center;">Amount</th>
				    	<th style="text-align: center;" colspan="2">Action</th>
				    </tr>
		    		<%
		             	int i = 0;
		             	for (i=0; alEmpReport != null && i < alEmpReport.size(); i++) {
		             		List<String> alEmpReportInner = (List<String>) alEmpReport.get(i);
		             		String strEmpId = alEmpReportInner.get(0);
		             		String payStatus="0";
		            		if(ckEmpPayList.contains(strEmpId)){
		            			payStatus="1";
		            		}
		            		String levelId = hmEmpLevel.get(strEmpId);
		            		String annualPolicyEmpAmt = hmAnnualPolicyEmpAmt.get(strEmpId);
		            		String assignAnnualPolicyEmpAmt = hmAnnualPolicyEmpAmt.get(strEmpId+"_ANNUAL");
		            		double dblAmt = uF.parseToDouble(assignAnnualPolicyEmpAmt);
		            		if(hmAnnualVariableValue.containsKey(strEmpId)){
		            			dblAmt = uF.parseToDouble(hmAnnualVariableValue.get(strEmpId));
		            		}
		            		
		            		if (hmAnnualVariable != null && uF.parseToInt(hmAnnualVariable.get(strEmpId)) == 0) {
		            			dblAmt = dblAmt - uF.parseToDouble(hmFYAnnualEmpAmt.get(strEmpId));
		            			dblAmt = dblAmt > 0 ? dblAmt : 0.0d; 
		            		}
		            		
		             %>
					    <tr>
					    	<td class="alignCenter">
					    	   <%if(!uF.parseToBoolean(payStatus) && hmAnnualVariable != null && !hmAnnualVariable.containsKey(strEmpId)){%>
					    	   		<input type="checkbox" name="strEmpIds" onclick="checkAll();" style="width:10px; height:10px" value="<%=strEmpId%>"/>
					    	   <%} %>
				    	   </td>
					    	<td><%=alEmpReportInner.get(1)%>
					    		<input type="hidden" id="idStrEmpId<%=i%>" name="strEmpId" value="<%=strEmpId%>">
					    	</td>
					    	
					    	<td style="text-align: right; background-color: #eee;">
					    		<%=uF.showData(annualPolicyEmpAmt, "0")%>
					    	</td>
					    	
					    	<td style="text-align: center; background-color: #eee;">
					    		<input style="width:75px !important;text-align: right" type="text" id="idStrIncentiveAmount<%=i%>" name="strIncentiveAmount_<%=strEmpId%>" value="<%=dblAmt %>">
					    	</td>
					    	
					    	<td style="text-align: center;">
						    	<% if (hmAnnualVariable != null && uF.parseToInt(hmAnnualVariable.get(strEmpId)) == 1) { %>
						    		<div id="myDiv_<%=i%>">
							    		<!-- <img src="images1/icons/approved.png" width="17px" /> -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
						    	<% } else if (hmAnnualVariable != null && uF.parseToInt(hmAnnualVariable.get(strEmpId)) == -1) { %>
						    		<div id="myDiv_<%=i%>">
							    		<!-- <img src="images1/icons/denied.png" width="17px" /> -->
							    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
							    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
							    	</div>
						    	<% } else if (hmAnnualVariable != null && uF.parseToInt(hmAnnualVariable.get(strEmpId)) == 2) { %>
						    		<div id="myDiv_<%=i%>">
							    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=strEmpId%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png"/> --%>
							    		<i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=strEmpId%>&count=<%=i%>')<%} %>"></i>
							    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=strEmpId%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png">  --%>
							    		<i class="fa fa-times-circle cross" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateAnnualVariable.action?requestid=<%=uF.parseToInt(hmAnnualVariableId.get(strEmpId))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=strEmpId%>&count=<%=i%>')<%} %>"></i>
						    		</div>
						    	<% } else if (hmAnnualVariable != null && uF.parseToInt(hmAnnualVariable.get(strEmpId)) == 0) { %>
						    		<div id="myDiv_<%=i%>"><input type="button" class="btn btn-primary" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>updateEarning(<%=i%>);<%} %>" value="Update"></div>
						    	<% } %>
					    	</td>
					    	<td>
						    	<% if (sHeadType != null && sHeadType.equals("E")) { %>
						    		<a href="javascript:void(0)" onclick="prevAnnualVariable(<%=strEmpId%>,'<%=alEmpReportInner.get(1)%>','E')">Previous Annual Variable</a>
						    	<% } else if (sHeadType != null && sHeadType.equals("D")) { %>
						    		<a href="javascript:void(0)" onclick="prevAnnualVariable(<%=strEmpId%>,'<%=alEmpReportInner.get(1)%>','D')">Previous Annual Variable</a>
						    	<% } %>
					    	</td>
					    </tr>
				    <% }
				    	if (i == 0) {
				    %>
				    	<tr><td colspan="4"><div style="width: 96%;" class="msg nodata"><span>No employee found for the current selection</span></div></td></tr>
				    <% } %>
				 </table>
	    	</div>
	    </s:form>
	</div>


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
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