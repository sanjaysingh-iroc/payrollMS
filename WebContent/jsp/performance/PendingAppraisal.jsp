<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<script type="text/javascript">
	jQuery(document).ready(function() {
		//toggle the componenet with class msg_body
		jQuery(".close_div").click(function() {

			jQuery(this).next(".con2").slideToggle(500);
			$(this).toggleClass("heading_dash");
		});
	});

	function seeQuestions(id, empId) {

		var dialogEdit = '#comment';
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 600,
					width : 850,
					modal : true,
					title : 'See Comment',
					open : function() {
						var xhr = $.ajax({
							url : "AppraisalDetail.action?id=" + id + "&empId="
									+ empId,
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

	function seeSattlement(id, empId) {

		var dialogEdit = '#comment';
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 600,
					width : 850,
					modal : true,
					title : 'See Comment',
					open : function() {
						var xhr = $.ajax({
							url : "AppraisalSattlement.action?id=" + id
									+ "&empId=" + empId,
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


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pending Appraisal" name="title" />
</jsp:include>



<%
	Map<String, String> empMap = (Map<String, String>) request
			.getAttribute("appraisalMp");
	//Map<String,String> answerStatus=(Map<String,String> )request.getAttribute("answerStatus");
	List<String> employeeList = (List<String>) request
			.getAttribute("employeeList");
	Map<String, String> hmEmpName = (Map<String, String>) request
			.getAttribute("hmEmpName");
	UtilityFunctions uF = new UtilityFunctions();
	//Map<String,Boolean> approvalList=(Map<String,Boolean> )request.getAttribute("approvalList");
	//String type=(String)request.getAttribute("type");
	//Map<String,String> mp=(Map<String,String> )request.getAttribute("mp");

	Map<String, Map<String, String>> mp = (Map<String, Map<String, String>>) request
			.getAttribute("mp");
%>
<div class="leftbox reportWidth">
	<s:form name="frm_PendingAppraisal" action="PendingAppraisal"
		theme="simple">

		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			<s:select theme="simple" name="checkStatus" headerKey="0"
				headerValue="All" list="#{'1':'Approved','2':'Pending'}"
				cssStyle="width:110px" onchange="document.frm_PendingAppraisal.submit();" />
			<s:hidden value="%{id}" name="id" />
		</div>

	</s:form>

	<div id="profilecontainer">
		<p class="past close_div" style="padding-left: 45px; text-align: left">Appraisal
			Profile</p>
		<div class="con2">
			<div class="holder">
				<table class="tb_style" cellpadding="0" cellspacing="0" width="90%"
					align="center">
					<tr align="left">
						<th>Name of the Appraisal</th>
						<td valign="top"><%=empMap.get("APPRAISAL")%></td>
						<th>Orientation</th>
						<td valign="top"><%=empMap.get("ORIENT")%></td>
					</tr>

					<tr align="left">
						<th>Employee Name</th>
						<td valign="top"><%=uF.showData(empMap.get("EMPLOYEE"), "")%></td>
						<th>Level</th>
						<td valign="top"><%=empMap.get("LEVEL")%></td>
					</tr>

					<tr align="left">
						<th>Designation</th>
						<td valign="top"><%=uF.showData(empMap.get("DESIG"), "")%></td>
						<th>Grade</th>
						<td valign="top"><%=uF.showData(empMap.get("GRADE"), "")%></td>
					</tr>

					<tr align="left">
						<th>Frequency</th>
						<td valign="top"><%=empMap.get("FREQUENCY")%></td>
						<th>Plan Type</th>
						<td valign="top"><%=empMap.get("PLAN_TYPE")%></td>

					</tr>
					<tr align="left">
						<th>From</th>
						<td valign="top"><%=empMap.get("FROM")%></td>
						<th>To</th>
						<td valign="top"><%=empMap.get("TO")%></td>

					</tr>

				</table>

			</div>
		</div>
	</div>

	<div style="width: 100%; float: left;">
		<table style="width: 80%;" class="tb_style">
			<tr>
				<th>Employee Name</th>
				<%
					if (empMap.get("ORIENT") != null
							&& !empMap.get("ORIENT").equals("90")) {
				%>
				<th>Manager Approval</th>
				<%
					}
				%>
				<th>HR Manager Approval</th>
				<%if(uF.parseToInt(empMap.get("ORIENT"))==270 || uF.parseToInt(empMap.get("ORIENT"))==360){ %>
				<th>Peer Approval</th>
				<%} %>

			</tr>
			<%
				for (int i = 0; employeeList != null && i < employeeList.size(); i++) {
					Map<String, String> innerMp = mp.get(employeeList.get(i));
					if (innerMp == null)
						innerMp = new HashMap<String, String>();
					System.out.println("man " + innerMp.get("2"));
					System.out.println("hrman " + innerMp.get("7"));
			%>
			<tr>
				<th><%=hmEmpName.get(employeeList.get(i))%></th>
				<%
					if (empMap.get("ORIENT") != null&& !empMap.get("ORIENT").equals("90")) {
				%>
				<th><%=(innerMp.get("2")!=null && innerMp.get("2").equals("t") ? "Approved": "Pending")%></th>
				<%}%>
				<th><%=(innerMp.get("7")!=null && innerMp.get("7").equals("t") ? "Approved"	: "Pending")%></th>
				
				<%if(uF.parseToInt(empMap.get("ORIENT"))==270 || uF.parseToInt(empMap.get("ORIENT"))==360){ %>
				<th><%=(innerMp.get("3")!=null && innerMp.get("3").equals("t") ? "Approved"	: "Pending")%></th>
				<%} %>
			</tr>
			<%
				}
			%>

		</table>

	</div>
</div>

<div id="comment"></div>
