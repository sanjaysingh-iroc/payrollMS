<%@page import="com.konnect.jpms.select.FillRosterWeeklyOff"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="javax.swing.Icon"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.IConstants,java.util.*"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="com.konnect.jpms.roster.FillShift"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>




<style>
.shift_emp tr td {
	border-top: solid #d2d2d2 1px;
	padding: 5px;
}
.filter_div{
padding: 5px;
background: rgb(240, 240, 240);
border: 1px solid rgb(202, 216, 224);
}
</style>

<script type="text/javascript">
	
$(function() {
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

	function submitForm(type, caller) {
		var costCenterName = ''; 
		if(document.getElementById("costCenterName")) {
			costCenterName =getSelectedValue("costCenterName");
		}
		
		var org = '';
		if(document.getElementById("strOrg")) {
			org = document.getElementById("strOrg").value;
		}
		
		var rosterDependant = document.getElementById("isRosterDependant");
		var isRosterDependant = 'false';
		if (rosterDependant.checked) {
			isRosterDependant = 'true';
		}
		var strGender = document.getElementById("strGender").value;

		var location = '';
		if(document.getElementById("f_strWLocation")) {
			location = getSelectedValue("f_strWLocation");
		}
		
		var department = '';
		if(document.getElementById("f_department")) {
			department = getSelectedValue("f_department");
		}
		
		var level = '';
		if(document.getElementById("f_level")) {
			level = getSelectedValue("f_level");
		}
		
		var strManager = ''; 
		if(document.getElementById("strManager")) {
			strManager =getSelectedValue("strManager");
		}
		
		var fromDate =  document.getElementById("fromDate").value;
		
		var toDate =  document.getElementById("toDate").value;
		
		if(fromDate != '' && toDate != '') {
			var paramValues = '&fromDate='+fromDate+'&toDate='+toDate;
			if(type == '2') {
				paramValues += '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+"&strCostCenterName="+costCenterName
					+'&isRosterDependant='+isRosterDependant+'&strGender='+strGender+'&manager='+strManager;
			}
			var action = 'ManageRoster.action?strOrg='+org+paramValues;
			
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: action, 
				data: $("#"+this.id).serialize(),
				success: function(result) {
		        	console.log(result);
		        	$("#divResult").html(result);
		   		}
			});
		} else {
			alert('Please, select the proper date.');
		}
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
    
   function getCheckedValue(id) {
		var checkboxes = document.getElementsByName(id);
		var exportchoice = "";
		for (var i=0, n=checkboxes.length;i<n;i++) {
		    if (checkboxes[i].checked) {
		    	exportchoice += ","+checkboxes[i].value;
		    }
		}
		if (exportchoice) exportchoice = exportchoice.substring(1);

		return exportchoice;
	}
   
   
	function assignShift() {
		if(confirm('Are you sure, you want to assign shift for selected employee?')) {
			var data = $("#frm_ManageRoster").serialize();
			//alert("data ===>> " + data); 
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ManageRoster.action?update=update',
				data: data,
				success: function(result) {
		        	$("#divResult").html(result); 
		   		}
			});
		}
	}
	
	
	function hideAssignShiftButton() {
		var ruleIds = getCheckedValue("rosterPolicyRuleIds");
		if(ruleIds != null && ruleIds.length>0) {
			document.getElementById("assignShiftDiv").style.display = 'none';
			document.getElementById("tdRosterWOff").style.display = 'none';
			document.getElementById("tdRosterWOffLbl").style.display = 'none';
		} else {
			document.getElementById("assignShiftDiv").style.display = 'block';
			document.getElementById("tdRosterWOff").style.display = 'table-cell';
			document.getElementById("tdRosterWOffLbl").style.display = 'table-cell';
		}
	}
	
	
	function submitFormStep2() {
		var ruleIds = getCheckedValue("rosterPolicyRuleIds");
		if(ruleIds != null && ruleIds.length>0) {
			var data = $("#frm_ManageRoster").serialize();
			//alert("data ===>> " + data); 
			//$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'ManageRoster.action?checkRule=checkRule',
				data: data,
				success: function(result) {
					if(result.trim() != '') {
						alert(result);
					} else {
						if(confirm('Are you sure, you want to assign shift in bulk?')) {
							var data = $("#frm_ManageRoster").serialize();
							//alert("data ===>> " + data); 
							$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
							$.ajax({
								type : 'POST',
								url: 'ManageRoster.action?strUpdate=strUpdate',
								data: data,
								success: function(result) {
						        	$("#divResult").html(result); 
						   		}
							});
						}
					}
		        	//$("#divResult").html(result); 
		   		}
			});
		} else {
			if(confirm('Are you sure, you want to assign shift in bulk?')) {
				var data = $("#frm_ManageRoster").serialize();
				//alert("data ===>> " + data); 
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'ManageRoster.action?strUpdate=strUpdate',
					data: data,
					success: function(result) {
			        	$("#divResult").html(result); 
			   		}
				});
			}
		}
	}
	
	
	function previewRosterPolicyRules(strId) {

		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		var height = $(window).height()* 0.90;
		var width = $(window).width()* 0.90;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
		$('.modal-title').html('View Roster Policy Rule');
		$.ajax({
			url : 'AddRosterPolicyRules.action?operation=PREVIEW&ID='+strId, 
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
</script>


<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

	List<String> shiftDetails = (List<String>) request.getAttribute("shiftDetails");
	List<List<String>> shiftList = (List<List<String>>) request.getAttribute("shiftList");
	List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
	List<FillShift> shift = (List<FillShift>) request.getAttribute("shift");
	Map<String, String> hmEmpGenderMap = (Map<String, String>) request.getAttribute("hmEmpGenderMap");
	if (hmEmpGenderMap == null) hmEmpGenderMap = new HashMap<String, String>();
	Map<String, String> hmEmpDepartment = (Map<String, String>) request.getAttribute("hmEmpDepartment");
	if (hmEmpDepartment == null) hmEmpDepartment = new HashMap<String, String>();
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if (hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
	List<FillRosterWeeklyOff> rosterWOffList = (List<FillRosterWeeklyOff>) request.getAttribute("rosterWOffList");

	//System.out.println("isRosterDependant====>"+(String)request.getAttribute("isRosterDependant"));
	
	List<String> alSalPaidEmpList = (List<String>)request.getAttribute("alSalPaidEmpList");
	if(alSalPaidEmpList == null) alSalPaidEmpList = new ArrayList<String>();
	List<String> alApproveClockEntrieEmp = (List<String>)request.getAttribute("alApproveClockEntrieEmp");
	if(alApproveClockEntrieEmp == null) alApproveClockEntrieEmp = new ArrayList<String>();
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Manage Shifts" name="title"/>
</jsp:include> --%>
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">

				<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
				<%session.setAttribute(IConstants.MESSAGE, ""); %>

				<div class="cat_heading" style="color: #777777;">
					<p style="background-color: #FFFFD8; padding: 4px; border: 1px solid #cccccc;">
						<strong>Step 1:</strong><br/>
						1. Select the group by applying proper filter.<br/>
						2. Click Roster Dependent option in case you want to include people who are already roster dependent.<br/>
						3. Select From and To date.<br/>
						4. Click Display Employees and follow step 2.<br/><br/>
						
						<strong>Step 2:</strong><br/>
						1. Select Shift and Weekly Off<br/><br/>
						
						<strong>Check the name of the employees and press Assign Shift button once confirmed.</strong>
					</p>
				</div>

				<div class="filter_div">
					<div class="filter_caption">
						<strong>Step 1</strong><br />Select Group </div>
					<s:form name="frmRoster" theme="simple" id="ManageRoster" action="ManageRoster" method="POST">
						<div class="row row_without_margin">
							<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
								<%if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select name="strOrg" id="strOrg" listKey="orgId" list="orgList" listValue="orgName" onchange="submitForm('1',this);" />
								</div>
								<% } %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Cost Center</p>
									<%-- <s:select name="costCenterName" id="costCenterName" listKey="empOffId" cssClass="validateRequired" list="costList" listValue="costCode" headerKey="" headerValue="Select Cost Center" list="costList" onchange="submitForm('2',this);" /> --%>
									<s:select name="costCenterName" id="costCenterName" listKey="empOffId" listValue="costCode" list="costList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Gender</p>
									<s:select name="strGender" id="strGender" listKey="genderId" list="genderList" listValue="genderName" headerKey="" headerValue="All" onchange="submitForm('2',this);"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<s:checkbox name="isRosterDependant" id="isRosterDependant" value="defaultRosterDependant"></s:checkbox>Roster Dependant
								</div>
							</div>
						</div><br>
						<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)){ %>
							<div class="row row_without_margin">
								<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Manager</p>
										<s:select theme="simple" name="strManager" id="strManager" listKey="employeeId" cssStyle="width:100px;" listValue="employeeCode" multiple="true" list="managerList" key=""/>
									</div>
								</div>
							</div>
						<%} %>
						
						<div class="row row_without_margin">
							<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date<sup>*</sup></p>
									<s:textfield name="fromDate" id="fromDate" cssClass="validateRequired" cssStyle="width:100px !important;" readonly="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date<sup>*</sup></p>
									<s:textfield name="toDate" id="toDate" cssClass="validateRequired" cssStyle="width:100px !important;" readonly="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="hidden" name="filterType" value="filter" />
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2',this);"/>
								</div>
							</div>
						</div>
						
					</s:form>
				</div>
				<br/>
				<s:form theme="simple" name="frm_ManageRoster" id="frm_ManageRoster" action="ManageRoster" method="POST">
					<% if (empList != null && !empList.isEmpty()) {
						Map<String, String> hmRosterPolicyRules = (Map<String, String>) request.getAttribute("hmRosterPolicyRules");
						if(hmRosterPolicyRules == null) hmRosterPolicyRules = new HashMap<String, String>();
					%>
					
					<div class="filter_div" style="width: 100%; float: left;">
						<div class="col-lg-6 col-md-6 col-sm-12">
							<div class="filter_caption"><strong>Step 2</strong></div>
							<input type="hidden" name="strOrg" value="<%=request.getAttribute("strOrg")%>"/>
							<s:hidden name="costCenterName"></s:hidden>
							<input type="hidden" name="strGender" value="<%=request.getAttribute("strGender")%>"/>
							
							<s:hidden name="f_strWLocation"></s:hidden>
							<s:hidden name="f_department"></s:hidden>
							<s:hidden name="f_level"></s:hidden>
							<s:hidden name="strManager"></s:hidden>
							
							<s:hidden name="isRosterDependant"></s:hidden>
							<s:hidden name="fromDate"></s:hidden>
							<s:hidden name="toDate"></s:hidden>
	
							<table align="center" border="0" style="width: 100%;margin-left: 5px;" cellpadding="5" cellspacing="0">
								<tr>
									<td class="txtlabel alignCenter" style="width:100px;">Assign Shift</td>
					    			<td class="txtlabel alignCenter" id="tdRosterWOffLbl" style="width:100px;">Weekly Off</td>
					    			<td>&nbsp;</td>
								</tr>
								<tr>
									<td>
										<s:select name="shiftName" id="shiftName" listKey="shiftId" cssStyle="width:150px !important;"
											listValue="shiftCode" headerKey="0" list="shiftList" key="" required="true" cssClass="validateRequired"/>
									</td>
									<td id="tdRosterWOff">
										<s:select name="f_RosterOff" id="f_RosterOff" listKey="typeId" cssStyle="width:150px !important;"
											listValue="typeName" headerKey="0" list="rosterWOffList" key="" required="true" cssClass="validateRequired"/>
									</td>
									<td colspan="3"><input type="button" name="strUpdate" value="Assign Shift in Bulk" class="btn btn-primary" style="margin:0px" onclick="submitFormStep2();"/></td>
								</tr>
							</table>
						</div>
						<div class="col-lg-6 col-md-6 col-sm-12">
							<div class="filter_caption"><strong>Roster Policy Rules</strong></div>
							<% Iterator<String> it = hmRosterPolicyRules.keySet().iterator();
								while(it.hasNext()) {
									String rpRuleId = it.next();
							%>
							<div class="col-lg-6 col-md-6 col-sm-12"><input type="checkbox" name="rosterPolicyRuleIds" id="rosterPolicyRuleIds" value="<%=rpRuleId %>" onclick="hideAssignShiftButton();"/>
								<a href="javascript:void(0)" onclick="previewRosterPolicyRules('<%=rpRuleId %>')" title="View Roster Policy Rule"><%=hmRosterPolicyRules.get(rpRuleId) %></a>
							</div>
							<% } %>
						</div>
					</div>

				<%
					String strSelectedShift = (String) request.getParameter("shiftName");
					String strSelectedWOff = (String) request.getParameter("f_RosterOff");
				%>

				<div class="clr"></div>
				
				<div>
					<%-- <s:form name="frm_ManageRoster" id="frm_ManageRoster" action="ManageRoster" theme="simple"> --%>
						<%-- <s:checkbox name="isWeekEnd"></s:checkbox>Assign Weekends
							<s:checkbox name="isHoliday"></s:checkbox>Assign Holidays --%>
						<div id="assignShiftDiv" style="float: right;padding: 5px; margin-top: 14px; ">
							<%-- <s:hidden name="fromDate"></s:hidden>
							<s:hidden name="toDate"></s:hidden>
							<s:hidden name="costCenterName"></s:hidden>
							<s:hidden name="shiftName"></s:hidden>
							<input type="hidden" name="strOrg" value="<%=request.getAttribute("strOrg")%>" />
							<input type="hidden" name="strWLocation" value="<%=request.getAttribute("strWLocation")%>"/>

							<s:hidden name="strWLocation"></s:hidden>
							<s:hidden name="f_department"></s:hidden>
							<s:hidden name="level"></s:hidden>

							<s:hidden name="isRosterDependant"></s:hidden> --%>
							<input type="button" value="Assign Shift" name="approveSubmit" class="btn btn-primary" onclick="assignShift();"/>
						</div>
						<br>
						<div>
							<table class="table table-bordered" cellpadding="5" cellspacing="0" id="lt">
								<thead>
									<tr>
										<th align="left">Choose Employees<br/><input type="checkbox" onclick="selectall(this,'strIsAssig')" checked="checked"/></th>
										<th align="left"> Employee Name</th>
										<th align="left"> Department</th>
										<th align="left"> Designation</th>
										<th align="center">Gender</th>
										<th>IS Roster Dependent</th>
										<th>Select Shift</th>
										<th>Weekly OFF</th>
										<th> Existing Shift</th>
										<th> Existing Weekly Off</th>
										<th> Existing Shift Starts</th>
										<th> Existing Shift Ends</th>
									</tr>
								</thead>
								<tbody>


								<%
									for (int i = 0; i < empList.size(); i++) {
								%>
								<%
									if (shiftList.get(i).isEmpty()) {
								%>
								<tr>			
									<td align="left"><input type="hidden" value="<%=empList.get(i).getEmployeeId()%>" name="empId"/>
										<%if(alSalPaidEmpList.contains((String)empList.get(i).getEmployeeId())){ %>
											<a href="javascript:void(0)" onclick="alert('Payroll has been processed for these date.')">
												<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
											</a>
										<%} else if(alApproveClockEntrieEmp.contains((String)empList.get(i).getEmployeeId())){ %>
											<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for these dates.')">
												<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
											</a>
										<%} else { %>
											<input type="checkbox" name="strIsAssig" value="<%=empList.get(i).getEmployeeId()%>" checked/>
										<%} %>
									</td>
									<td align="left"><%=empList.get(i).getEmployeeName()%></td>
									<td align="left"><%=uF.showData(hmEmpDepartment.get(empList.get(i).getEmployeeId()),"")%></td>
									<td align="left"><%=uF.showData(hmEmpCodeDesig.get(empList.get(i).getEmployeeId()),"")%></td>
									<td align="center"><%=hmEmpGenderMap.get(empList.get(i).getEmployeeId())%></td>
									<td align="center"><%=(uF.parseToBoolean((String)empList.get(i).getRoster()))?"Yes":"No"%> </td>
									<td align="center">
										<select name="innerShiftName" style="width:58px !important;">
											<option value="-1">Select</option>
											<%
												for (int j = 0; shift!=null && j < shift.size(); j++) {
											%>	
													<option value="<%=shift.get(j).getShiftId()%>" > <%=shift.get(j).getShiftCode()%></option>	
													<%-- <%if (shift.get(j).getShiftId().equals(strSelectedShift)) {
														%>
														<option value="<%=shift.get(j).getShiftId()%>" selected="selected" > <%=shift.get(j).getShiftCode()%></option>
														<%
													}else{
														%>
														<option value="<%=shift.get(j).getShiftId()%>" > <%=shift.get(j).getShiftCode()%></option>
													<%}%> --%>
											<%		
												}
											%>
										</select>
									</td>
									<td align="center">
										<select name="strRosterWOff" style="width:100px !important;">
											<option value="-1">Select</option>
											<%
												for (int j = 0; rosterWOffList!=null &&  j < rosterWOffList.size(); j++) {
											%>		
													<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>
													<%-- <%if (rosterWOffList.get(j).getTypeId().equals(strSelectedWOff)) {
														%>
														<option value="<%=rosterWOffList.get(j).getTypeId()%>" selected="selected" > <%=rosterWOffList.get(j).getTypeName()%></option>
														<%
													}else{
														%>
														<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>
													<%}%> --%>
											<%	}
											%>
										</select>
									</td>
									<td align="center" colspan="4">No previous Shift For above dates</td>
								</tr>
								<% 
									}
								%>
								<%
									int shiftSize = (shiftList.get(i).size() + 1) / 4;
									
								%>
								<%
									if (shiftSize == 1) {
										
								%>
									<tr>
									
									<td align="left"><input type="hidden" value="<%=empList.get(i).getEmployeeId()%>" name="empId"/>
										<%if(alSalPaidEmpList.contains((String)empList.get(i).getEmployeeId())){ %>
											<a href="javascript:void(0)" onclick="alert('Payroll has been processed for these date.')">
												<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
											</a>
										<%} else if(alApproveClockEntrieEmp.contains((String)empList.get(i).getEmployeeId())){ %>
											<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for these dates.')">
												<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
											</a>
										<%} else { %>
											<input type="checkbox" name="strIsAssig" value="<%=empList.get(i).getEmployeeId()%>" checked />
										<%} %>
									</td>
									<td align="left"><%=empList.get(i).getEmployeeName()%></td>
									<td align="left"><%=uF.showData(hmEmpDepartment.get(empList.get(i).getEmployeeId()),"")%></td>
									<td align="left"><%=uF.showData(hmEmpCodeDesig.get(empList.get(i).getEmployeeId()),"")%></td>
									<td align="center"><%=hmEmpGenderMap.get(empList.get(i).getEmployeeId())%></td>
									<td align="center"><%=(uF.parseToBoolean((String)empList.get(i).getRoster()))?"Yes":"No"%> </td>
									<td align="center">
										<select name="innerShiftName" style="width:58px !important;">
											<option value="-1">Select</option>
											<%
												for (int j = 0; shift!=null && j < shift.size(); j++) { 
											%>
													<option value="<%=shift.get(j).getShiftId()%>"> <%=shift.get(j).getShiftCode()%></option>
												<%-- <%	if (shift.get(j).getShiftId().equals(strSelectedShift)) {%>
														<option value="<%=shift.get(j).getShiftId()%>" selected="selected"> <%=shift.get(j).getShiftCode()%></option>
												<%
													} else {
												%>
													<option value="<%=shift.get(j).getShiftId()%>"> <%=shift.get(j).getShiftCode()%></option>
												<%
													}
												%> --%>
											<%
												}
											%>
										</select>
									</td>
									<td align="center">
										<select name="strRosterWOff" style="width:100px !important;">
											<option value="-1">Select</option>
											<%
												for (int j = 0;rosterWOffList!=null &&  j < rosterWOffList.size(); j++) {
											%>
													<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>
															
											<%-- <%		if (rosterWOffList.get(j).getTypeId().equals(strSelectedWOff)) {%>
														<option value="<%=rosterWOffList.get(j).getTypeId()%>" selected="selected" > <%=rosterWOffList.get(j).getTypeName()%></option>
											<%
													}else{
											%>
														<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>
											<%		}%> --%>
											<%	}
											%>
										</select>
									</td>
										<%for (int k = 0; k < shiftList.get(i).size(); k++) {%>
											<td align="center"><%=(String) shiftList.get(i).get(k)%></td>
										<%
											}
										%>
										
								<%
									} else {
								%>
										<%
											int z = 0;
											int l = 0;
										%>
										<%
										
											for (int m = 0; m < shiftSize; m++) {
										%>
										
										
												<tr>
												<td align="left">
													<%if(alSalPaidEmpList.contains((String)empList.get(i).getEmployeeId())){ %>
														<a href="javascript:void(0)" onclick="alert('Payroll has been processed for these date.')">
															<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
														</a>
													<%} else if(alApproveClockEntrieEmp.contains((String)empList.get(i).getEmployeeId())){ %>
														<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for these dates.')">
															<img src="images1/icons/popup_arrow.gif" style="width: 10px;"/>
														</a>
													<%} else { %>
														<input type="checkbox" name="strIsAssig" value="<%=empList.get(i).getEmployeeId()%>" checked/>
													<%} %>	
												</td>
												<td align="left"><%=empList.get(i).getEmployeeName()%></td>
												<td align="left"><%=uF.showData(hmEmpDepartment.get(empList.get(i).getEmployeeId()),"")%></td>
												<td align="left"><%=uF.showData(hmEmpCodeDesig.get(empList.get(i).getEmployeeId()),"")%></td>
												<td align="center"><%=hmEmpGenderMap.get(empList.get(i).getEmployeeId())%></td>
												<td align="center"> <%=(uF.parseToBoolean((String)empList.get(i).getRoster()))?"Yes":"No" %> </td>
												<td align="center">
												<select name="innerShiftName" style="width:58px !important;">
													<option value="-1">Select</option>
													<%
														for (int j = 0; shift!=null && j < shift.size(); j++) {
													%>
															<option value="<%=shift.get(j).getShiftId()%>"> <%=shift.get(j).getShiftCode()%></option>
														<%-- <%
																if (shift.get(j).getShiftId().equals(strSelectedShift)) {
																
														%>
															<option value="<%=shift.get(j).getShiftId()%>" selected="selected"> <%=shift.get(j).getShiftCode()%></option>
														<%
															} else {
														%>
														
															<option value="<%=shift.get(j).getShiftId()%>"> <%=shift.get(j).getShiftCode()%></option>
														<%
															}
														%> --%>
													<%
														}
													%>
												</select>
												</td>
												<td align="center">
													<select name="strRosterWOff" style="width:100px !important;">
														<option value="-1">Select</option>
														<%
															for (int j = 0; rosterWOffList!=null &&  j < rosterWOffList.size(); j++) {
														%>
																<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>									
														<%-- <%		if (rosterWOffList.get(j).getTypeId().equals(strSelectedWOff)) {
														%>
																	<option value="<%=rosterWOffList.get(j).getTypeId()%>" selected="selected" > <%=rosterWOffList.get(j).getTypeName()%></option>
														<%
																}else{
														%>
																	<option value="<%=rosterWOffList.get(j).getTypeId()%>" > <%=rosterWOffList.get(j).getTypeName()%></option>
														<%		}%> --%>
														<%	}
														%>
													</select>
												</td>
											<%
												for (l = z; l < z + 4; l++) {
											%>
													<td align="center"><%=(String) shiftList.get(i).get(l)%></td>	
											<%
												}
												z = z + 4;
											}
										%>
										
									</tr>
								<%
									}
								}
								%>
								</tbody>
							</table>
						</div>
						<% for (int z = 0; z < shiftDetails.size() - 1;) {
							String strColour = shiftDetails.get(z++);
						%>
						<div class="col-lg-3 col-md-4 col-sm-6">
							<div class="box box-solid" style="border: 1px solid <%=strColour%>;">
					            <div class="box-header with-border" style="background: <%=strColour%>;background-color: <%=strColour%>;padding: 5px;">
					              <h3 class="box-title"><%=shiftDetails.get(z++)%></h3>
					              <div class="box-tools pull-right">
					                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					              </div>
					            </div>
					            <div class="box-body" style="padding: 5px;">
						            <p style="padding-left: 5px; padding-right: 5px">
										<span style="font-weight: bold">Shift Start</span>
										<%=shiftDetails.get(z++)%>
										<span style="font-weight: bold">End</span>
										<%=shiftDetails.get(z++)%></p>
									<p style="padding-left: 5px; padding-right: 5px">
										<span style="font-weight: bold">Break Start</span>
										<%=shiftDetails.get(z++)%>
										<span style="font-weight: bold">End</span>
										<%=shiftDetails.get(z++)%></p>
					            </div>
					          </div>
						</div>
						<% } %>
				</div>
				<% } else { %>
                    <div class="nodata msg"><span> No data available, please select filter and submit the form.</span></div>
				<% } %>
				</s:form>
		</div>
		<!-- /.box-body -->
		
		
	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Candidate Information</h4>
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

$(function() {
	$("#fromDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#toDate').datepicker('setStartDate', minDate);
    });
    
    $("#toDate").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#fromDate').datepicker('setEndDate', minDate);
    });
    
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#costCenterName").multiselect().multiselectfilter();
	$("#strManager").multiselect().multiselectfilter();
	

	$("input[type='button'").click(function(){
		$("#"+ this.form.id ).find('.validateRequired').filter(':hidden').prop('required',false);
		$("#"+ this.form.id ).find('.validateRequired').filter(':visible').prop('required',true);
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

	function selectall(x, strEmpId) {
		var status = x.checked;
		var arr = document.getElementsByName(strEmpId);
		for (i = 0; i < arr.length; i++) {
			arr[i].checked = status;
		}
	}
</script>