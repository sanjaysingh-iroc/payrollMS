<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags" %>

<style type="text/css">
	a {
		color:#993300;
		text-decoration:none;
	}
	#div1 {
		width:50%;
		display: none;
		padding:5px;
		border:1px solid;
		background-color:#7FFFD4;
	}
	#click_here {
		border:2px solid #FFEFEF;
	}
</style>
 
<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve / Deny Travel" name="title"/>
</jsp:include>
 --%>
 
<script type="text/javascript">
var type = '<%=(String) request.getAttribute("type") %>';
var userTypeName = '<%=(String) request.getAttribute("userTypeName") %>';
	$(function() {
		$("body").on("click","#strSubmit",function(){
			$(".validateRequired").prop('required',true);
	    });
		
		$("#click_here").click(function(event) {
			event.preventDefault();
			$("#div1").slideToggle();
		});
		
		$("#div1 a").click(function(event) {
			event.preventDefault();
			$("#div1").slideUp();
		});
	});
		
	function toggleSession(obj){
    	if(obj.checked){
    		document.getElementById("idSession").style.display="block";
    		document.getElementById("idLeaveTo").style.display="none";
    	}else{
    		document.getElementById("idSession").style.display="none";
    		document.getElementById("idLeaveTo").style.display="table-row";
    	}
    }
	
	
	$("#frmApproveTravel").submit(function(e){
		
		var currUserType = document.getElementById("currUserType").value;
		var leaveStatus = document.getElementById("leaveStatus").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		
		if (type != '' && type === "myhome") {
			
		} else {
			e.preventDefault();
			var divResult = "divResult";
			var CEO = '<%=IConstants.CEO%>';
			var HOD = '<%=IConstants.HOD%>';
			var MGR = '<%=IConstants.MANAGER%>';
			
			
			/* if(userTypeName != "" && (userTypeName === CEO || userTypeName === HOD || userTypeName === MGR)){ */
			if(userTypeName != "" && (userTypeName === CEO || userTypeName === HOD)){	
				divResult = "subDivResult";
			}
			var form_data = $("form[name='frmApproveTravel']").serialize();
			//alert("form_data ===>> " + form_data);
	     	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	     	$.ajax({
	 			url : "ApproveTravel.action",
	 			data: form_data,
	 			cache : false,
	 			success : function(res) {
	 				$("#"+divResult).html(res);
	 			},
				error : function(res) {
					$.ajax({
						url: "TravelApprovalReport.action?currUserType="+currUserType+'&leaveStatus='+leaveStatus+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate,
						cache: true,
						success: function(result){
							$("#"+divResult).html(result);
				   		}
					});
				} 
	 		});
	     	/* $.ajax({
				url: "TravelApprovalReport.action?currUserType="+currUserType+'&leaveStatus='+leaveStatus+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate,
				cache: true,
				success: function(result){
					
					$("#"+divResult).html(result);
		   		}
			}); */
		}		
	});
	
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strEmpType = (String) session.getAttribute("USERTYPE");
	String strMessage = (String) request.getAttribute("MESSAGE");
	if (strMessage == null) {
		strMessage = "";
	}
%>

<%-- <p class="message"><%=strMessage%></p> --%>

<s:form theme="simple" name="frmApproveTravel" action="ApproveTravel" id="frmApproveTravel" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="userType"></s:hidden>
	<table class="table table_no_border form-table">
		<tr><td colspan="2">

			<s:hidden name="currUserType" />
			<s:hidden name="leaveStatus" />
			<s:hidden name="strStartDate" />
			<s:hidden name="strEndDate" />
			<s:hidden name="leaveId" />
			<s:hidden name="empId" />
			<s:hidden name="userId" />
			<s:hidden name="leaveFromTo"/>
			<s:hidden name="leaveToDate"/>
			<s:hidden name="entryDate"/>
			<s:hidden name="reason"/>
			<s:hidden name="type"/>
			
		</td></tr>
		
		<%-- <tr><td height="10px" colspan="2" style="text-align: center;"><strong>Travel Plan</strong></td></tr> --%>
		<tr><td class="txtlabel alignRight">Emp Name:<sup>*</sup></td><td><s:label name="empName" label="Emp Name"/></td></tr>
		<tr><td class="txtlabel alignRight" valign="top">Half day:</td><td height=50 valign="top"><s:checkbox name="isHalfDay" onclick="toggleSession(this)" cssStyle="float:left"/>  <div id="idSession" style="float:left;width:100px;"><s:radio name="strSession" list="strWorkingSession" listKey="strHaldDayId" listValue="strHaldDayName"/></div> </td></tr>
		<tr><td class="txtlabel alignRight">Approval From Date:<sup>*</sup></td><td><s:textfield name="approvalFromTo" id="approvalFromTo" cssClass="validateRequired" readonly="true"></s:textfield></td></tr>
		<tr id="idLeaveTo"><td class="txtlabel alignRight">Approval To Date:<sup>*</sup></td><td><s:textfield name="approvalToDate" id="approvalToDate" cssClass="validateRequired" readonly="true"></s:textfield></td></tr>
		<%-- <tr><td class="txtlabel alignRight">Place To:<sup>*</sup></td><td><s:textfield cssClass="validateRequired" name="placeFrom"></s:textfield></td></tr>
		<tr><td class="txtlabel alignRight">Destinations:<sup>*</sup></td><td><s:textfield name="destinations" cssClass="validateRequired"></s:textfield></td></tr> --%>
		<tr><td class="txtlabel alignRight">Place From:<sup>*</sup></td><td><s:property value="placeFrom"/></td></tr>
		<tr><td class="txtlabel alignRight">Place To:<sup>*</sup></td><td><s:property value="destinations"/></td></tr>
		<tr><td class="txtlabel alignRight">Reason:</td><td><s:property value="reason"/></td></tr>
		 
		<tr>
			<td></td>
			<td><div id="div1"><a href="#" class="close">[x]</a><s:label name="reason" /></div></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Advance Requested: </td>
			<td><s:div>${strAdvRequested}</s:div></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Advance Date: </td>
			<td><s:div>${strAdvDate}</s:div></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Advance Eligibility: </td>
			<td><s:div>${strAdvEligibility}</s:div></td>
		</tr>
		
		<tr>
			<td class="txtlabel alignRight">Approve Amount:<sup>*</sup></td>
			<td><s:textfield name="approveAdvAmount" id="approveAdvAmount" cssClass="validateRequired" readonly="true"></s:textfield></td>
		</tr>
		<% 
			String conciergeService = (String) request.getAttribute("conciergeService");
			if(uF.parseToBoolean(conciergeService)) {
		%>
			<tr>
				<td class="txtlabel alignRight">Concierge Service: </td>
				<td><%=uF.showData(conciergeService,"") %></td>
			</tr>
			<%String travelMode = (String) request.getAttribute("travelMode"); %>
			<tr>
				<td class="txtlabel alignRight">Mode of Travel: </td>
				<td><%=uF.showData(travelMode,"") %></td>
			</tr>
			<%	String isBooking = (String) request.getAttribute("isBooking"); 
				if(uF.parseToBoolean(isBooking)){
			%>
				<tr>
					<td class="txtlabel alignRight">Booking: </td>
					<td><%=uF.showData(isBooking,"") %></td>
				</tr>
				<%String bookingDetails = (String) request.getAttribute("bookingDetails"); %>
				<tr>
					<td class="txtlabel alignRight">Booking Details: </td>
					<td><%=uF.showData(bookingDetails,"") %></td>
				</tr>
			<% } %>
			<%String isAccommodation = (String) request.getAttribute("isAccommodation"); 
			if(uF.parseToBoolean(isAccommodation)){
			%>
				<tr>
					<td class="txtlabel alignRight">Accommodation Required: </td>
					<td><%=uF.showData(isAccommodation,"") %></td>
				</tr>
				<%String accommodationDetails = (String) request.getAttribute("accommodationDetails"); %>
				<tr>
					<td class="txtlabel alignRight">Accommodation Details: </td>
					<td><%=uF.showData(accommodationDetails,"") %></td>
				</tr>
		<%	} 
		}%>
		<tr><td class="txtlabel alignRight" valign="top">Manager  Reason:</td><td><s:textarea cols="22" rows="05" name="managerReason" label="Manager  Reason"/></td></tr>
		<tr><td class="txtlabel alignRight">Approve/Deny:<sup>*</sup></td><td><s:select name="isapproved" label="Is Approved" listKey="approvalId" listValue="approvalName" list="approvalList" key="" required="true" cssClass="validateRequired"/><span class="hint">Select Approval Type.<span class="hint-pointer">&nbsp;</span></span></td></tr>
		<tr>
			<td></td>
			<td><s:submit cssClass="btn btn-primary" name="strSubmit" id="strSubmit" value="Submit" align="center" /></td>
		</tr>
		
	</table>

</s:form>


<script>toggleSession(document.frmLeave.isHalfDay);</script>