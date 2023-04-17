<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<script>
function prevMobileReimbursement(emp_id) {
	removeLoadingDiv('the_div');
	var dialogEdit = '#prevMobileReimbursements';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'Mobile Reimbursement', 
		open : function() {
			var xhr = $.ajax({
				url : "PrevMobileReimbursements.action?strEmpId="+emp_id,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
			xhr = null;
		},
		overlay : {
			backgroundColor : '#000',
			opacity : 0.5
		}
	});
	$(dialogEdit).dialog('open');
}
</script>


<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_wLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();     
});    
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Mobile Reimbursement" name="title"/>
</jsp:include> 

  
<div id="printDiv" class="leftbox reportWidth">
    
    
<s:form name="frm_MobileReimbursement" action="MobileReimbursementForm" theme="simple" method="post">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			<div
				style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
					cssStyle="float:left;margin-right: 10px;" listValue="orgName"
					onchange="document.frm_MobileReimbursement.submit();"
					list="organisationList" key="" />
			</div> 
			<div
				style="float: left; margin-top: 8px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Paycycle</p>
				<s:select id="idPaycycleId" name="paycycle" listKey="paycycleId"
					listValue="paycycleName" headerKey="0"
					headerValue="Select Paycycle" list="paycycleList" key=""
					onchange="document.frm_MobileReimbursement.submit();" />
			</div>
			
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="f_wLocation" id="f_wLocation" listKey="wLocationId"
					listValue="wLocationName" list="wLocationList" key=""
					multiple="true" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Department</p>
				<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId"
					listValue="deptName" multiple="true"></s:select>
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Service</p>
				<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"
					listValue="serviceName" multiple="true"></s:select>
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Level</p>
				<s:select theme="simple" name="f_level" id="f_level" listKey="levelId"
					listValue="levelCodeName" list="levelList" key="" multiple="true" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<s:submit value="Submit" cssClass="input_button"
					cssStyle="margin:0px" />
			</div>
		</div>





		<%
			List alEmpReport = (List) request.getAttribute("alEmpReport");
				Map hmSalaryList = (Map) request.getAttribute("hmSalaryList");
				Map hmSalaryHeadsMap = (Map) request.getAttribute("hmSalaryHeadsMap");
				
				Map<String, String> hmMobileReimbursement = (Map<String, String>) request.getAttribute("hmMobileReimbursement");
				if(hmMobileReimbursement == null) hmMobileReimbursement = new HashMap<String, String>();
				Map<String, String> hmMobileReimbursementId = (Map<String, String>) request.getAttribute("hmMobileReimbursementId");
				if(hmMobileReimbursementId == null) hmMobileReimbursement = new HashMap<String, String>();
				Map<String, String> hmMobileReimbursementValue = (Map<String, String>) request.getAttribute("hmMobileReimbursementValue");
				if(hmMobileReimbursementValue == null) hmMobileReimbursement = new HashMap<String, String>();
				  
				
				List<String> ckEmpPayList = (List<String>) request.getAttribute("ckEmpPayList");
				if(ckEmpPayList == null) ckEmpPayList = new ArrayList<String>();
				
				UtilityFunctions uF = new UtilityFunctions();
		%>  
    
    
    
    <table class="tb_style" style="float:left;width:70%">
    
    <tr>	
    	<th align="center">Employee Name</th>
    	<th align="center">Percent (%) of Salary Heads</th>
    	<th align="center"></th>
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
    	<td><%=(String) alEmpReportInner.get(1)%>
    		<input type="hidden" id="idStrEmpId<%=i%>" name="strEmpId" value="<%=(String) alEmpReportInner.get(0)%>">
    	</td>
    
    	<td align="center" style="background-color: #efe;">
    		<input style="width:75px;text-align: right" type="text" id="idStrIncentivePercent<%=i%>" name="strIncentivePercent"> of 
    		<select style="width:100px" id="salaryId_<%=i%>">
    		<%
    			List alSalaryDetails = (List) hmSalaryList.get((String) alEmpReportInner.get(0));
    					for (int x = 0; alSalaryDetails != null && x < alSalaryDetails.size(); x++) {
    		%>
				<option value="<%=alSalaryDetails.get(x)%>"><%=(String) hmSalaryHeadsMap.get(alSalaryDetails.get(x))%></option>
				<%
					}
				%>
    		</select>
    	</td>
    	<td align="center">
    		- OR -
    	</td>
    	<td align="center" style="background-color: #eee;"><input style="width:75px;text-align: right" type="text" id="idStrIncentiveAmount<%=i%>" name="strIncentiveAmount" value="<%=uF.showData((String) hmMobileReimbursementValue.get((String) alEmpReportInner.get(0)), "")%>"></td>
    	<td align="center">
    	<%
    		if (hmMobileReimbursement != null && uF.parseToInt((String) hmMobileReimbursement.get((String) alEmpReportInner.get(0))) == 1) {
    	%>
    		<div id="myDiv_<%=i%>">
	    		<!-- <img src="images1/icons/approved.png" width="17px" /> -->
	    		<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
	    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
	    	</div>
    	<%
    		} else if (hmMobileReimbursement != null && uF.parseToInt((String) hmMobileReimbursement.get((String) alEmpReportInner.get(0))) == -1) {
    	%>
    		<div id="myDiv_<%=i%>">
	    		<!-- <img src="images1/icons/denied.png" width="17px" />  -->
	    		<i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i>
	    		<img style="margin-left: 4px;" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&type=revoke')<%} %>" width="20px" src="images1/icons/icons/undo_icon.png" title="Revoke"/>
	    	</div>
    	<%
    		} else if (hmMobileReimbursement != null && uF.parseToInt((String) hmMobileReimbursement.get((String) alEmpReportInner.get(0))) == 2) {
    	%>  
    		<div id="myDiv_<%=i%>">
	    		<%-- <img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="17px" src="images1/icons/icons/approve_icon.png">
	    		<img onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" width="16px" src="images1/icons/icons/close_button_icon.png"> --%> 
	    		
	    		<i class="fa fa-check-circle checknew" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&approval=1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>"></i>
				<i class="fa fa-times-circle cross" aria-hidden="true" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?requestid=<%=uF.parseToInt((String) hmMobileReimbursementId.get((String) alEmpReportInner.get(0)))%>&approval=-1&payStatus=<%=payStatus %>&emp_id=<%=(String) alEmpReportInner.get(0)%>&count=<%=i%>')<%} %>" ></i>

    		</div>
    	<%
    		} else if (hmMobileReimbursement != null && uF.parseToInt((String) hmMobileReimbursement.get((String) alEmpReportInner.get(0))) == 0) {
    	%>
    		<div id="myDiv_<%=i%>"><input type="button" class="input_button" onclick="<%if(uF.parseToBoolean(payStatus)){%>alert('<%=(String) alEmpReportInner.get(1)%>\'s payroll has been processed for this paycycle.');<%} else{%>getContent('myDiv_<%=i%>', 'UpdateMobileReimbursement.action?emp_id='+document.getElementById('idStrEmpId<%=i%>').value+'&salary_id='+document.getElementById('salaryId_<%=i%>').value+'&paycycle='+document.getElementById('idPaycycleId').value+'&amt='+document.getElementById('idStrIncentiveAmount<%=i%>').value+'&percent='+document.getElementById('idStrIncentivePercent<%=i%>').value+'&count=<%=i%>')<%} %>" value="Update"></div>
    	<%
    		}
    	%>
    	</td>
    	<td><a href="javascript:void(0)" onclick="prevMobileReimbursement(<%=(String) alEmpReportInner.get(0)%>)">Previous Mobile Reimbursements</a></td>
    	
    </tr>
    <%
    	}
    		if (i == 0) {
    %>
    	<tr><td colspan="5" class="msg nodata"><span>No employee found for the current selection</span></td></tr>
    <%
    	}
    %>
    </table>
    
    </s:form>
    
</div>	

<div id="prevMobileReimbursements"></div>