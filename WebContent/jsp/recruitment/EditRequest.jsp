<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Requirement Request" name="title" />
</jsp:include> --%>

<script>
	
	$(function() {
		$("#rdate").datepicker({
			dateFormat : 'dd/mm/yy'
		});
		// $( "#leaveToDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	});

	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});
	
	function deactivateDesig(){
		
	//	document.getElementById("strDesignationUpdate").selectedIndex=0;	
	}
	
</script>

<%
	String updateRequest = (String) request.getAttribute("updateRequest");
	List<String> requestList = (List<String>) request.getAttribute("requestList1");
	System.out.println("requestList in edit " + updateRequest);
	String recruit_id = requestList.get(0);
	/* String level_id=(String)requestList.get(9);
	System.out.println("level id " + level_id); */
%>
<div class="leftbox reportWidth">

	<%-- <%
		String strEmpType = (String) session.getAttribute("USERTYPE");
		String strEmpID = (String) session.getAttribute(IConstants.EMPID);
		String strMessage = (String) request.getAttribute("MESSAGE");
		if (strMessage == null) {
			strMessage = "";
		}
		
	%>
	<p class="message"><%=strMessage%></p> --%>


	<s:form id="formID" name="frmRequirementRequest" theme="simple"
		action="RequirementRequest" method="POST" cssClass="formcss"
		enctype="multipart/form-data">

		<table border="0" class="formcss" style="float: left">
			<tr>
				<td colspan=2><s:fielderror /></td>
			</tr>
			<tr>
				<td><s:hidden name="insertRecruitReq" value="edit" /> 
				<s:hidden name="recruitmentID"/></td>
			</tr>

			<tr>
				<td height="10px">&nbsp;</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Level:<sup>*</sup></td>
				<td><s:select cssClass="validateRequired" name="strLevel"
						listKey="levelId" listValue="levelCodeName" headerKey=""
						headerValue="Select Level" list="levelslist" required="true"
						onchange="getContent('myDesig','GetDesigList.action?strLevel='+this.options[this.selectedIndex].value)"
						value="{level_id}" />
				</td>
			</tr>

			<tr>
				<td class="txtlabel alignRight">Designation:<sup>*</sup></td>
				<td><div id="myDesig">
						<s:select theme="simple" name="strDesignationUpdate"
							listKey="desigId" cssClass="validateRequired"
							listValue="desigCodeName" headerKey=""
							headerValue="Select Designation" id="strDesignationUpdate"
							onchange="javascript:show_grade();return false;" list="desigList"
							key="" required="true"
							onclick="getContent('myGrade','GetGradeList.action?DId='+this.options[this.selectedIndex].value)"
							value="{desig_id}" />

					</div>
				</td>
				
				<td class="txtlabel alignRight">Custum Designation:</td>
				<td><input type="text" name="custumdesignation" id="custumdesig" onblur="deactivatecss();" onchange="deactivateDesig();" /></td>
				
			</tr>
			<tr>
				<td class="txtlabel alignRight">Grade:<sup>*</sup>
				</td>
				<td>
					<div id="myGrade">
						<s:select cssClass="validateRequired" name="empGrade"
							listKey="gradeId" listValue="gradeCode" headerKey=""
							headerValue="Select Grade" list="gradeList" value="{grade_id}" />
					</div></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Location:<sup>*</sup></td>
				<td><s:select cssClass="validateRequired" name="location"
						listKey="wLocationId" listValue="wLocationName" headerKey=""
						headerValue="Select Location" list="workLocationList"
						value="{manlocation}" /></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Service:<sup>*</sup></td>
				<td><s:select cssClass="validateRequired" name="services"
						listKey="serviceId" listValue="serviceName" headerKey=""
						headerValue="Select Service" list="serviceslist"
						value="{service_id}" /></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Skills:<sup>*</sup>
				</td>
				<td><s:select cssClass="validateRequired" name="skills"
						listKey="skillsName" listValue="skillsName" list="skillslist"
						multiple="true" size="4" /></td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">No. of Position(s):<sup>*</sup>
				</td>
				<td><input type="text" name="position" id="position"
					class="validateRequired"
					value="<%=requestList.get(4) == null ? "" : requestList.get(4)%>" />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight">Effective Date:<sup>*</sup>
				</td>
				<td><input type="text" name="rdate" id="rdate"
					class="validateRequired"
					value="<%=requestList.get(10) == null ? "" : requestList
						.get(10)%>"
					enabled />
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" valign="top">Notes:</td>
				<td><textarea name="notes" id="notes" cols="23" rows="4">"<%=requestList.get(5) == null ? "" : requestList.get(5)%></textarea>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><s:submit cssClass="input_button" value="Edit"
						align="center" />
				</td>
			</tr>
		</table>
	</s:form>
</div>




