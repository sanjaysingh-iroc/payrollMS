<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Requirement Request" name="title" />
</jsp:include> --%>


<% 	String nCount = (String)request.getAttribute("nCount");
	String operation = (String)request.getAttribute("operation");
	String sbStautsApproveDeny = (String)request.getAttribute("sbStautsApproveDeny");
%>

<% if(operation != null && operation.equals("VIEW")) { %>
	
	<%=sbStautsApproveDeny != null ? sbStautsApproveDeny : "" %>

<% } else { %>

<script>

function displayTR(){
	
	if(document.getElementById("existingDesigCheck").checked==true) {
		//alert("sfdbdskhfkj");
		document.getElementById("desigTR").style.display = "table-row";
		document.getElementById("gradeTR").style.display = "table-row";
		document.getElementById("custDesigTextSpan").style.display = "none";
		document.getElementById("custGradeTextSpan").style.display = "none";
		document.getElementById("custDesigLblSpan").style.display = "block";
		document.getElementById("custGradeLblSpan").style.display = "block";
		//document.getElementById("custDesigTR").style.display = "none";
		//document.getElementById("custGradeTR").style.display = "none";
		
		//document.getElementById("customDesignation").readOnly = "true";
		//document.getElementById("customGrade").readOnly = "true";
	} else {
		//document.getElementById("customDesignation").readOnly = "false";
		//document.getElementById("customGrade").readOnly = "false";
		//document.getElementById("custDesigTR").style.display = "table-row";
		//document.getElementById("custGradeTR").style.display = "table-row";
		document.getElementById("custDesigTextSpan").style.display = "block";
		document.getElementById("custGradeTextSpan").style.display = "block";
		document.getElementById("custDesigLblSpan").style.display = "none";
		document.getElementById("custGradeLblSpan").style.display = "none";
		document.getElementById("desigTR").style.display = "none";
		document.getElementById("gradeTR").style.display = "none";
	}
	//changeDesig();
}


/* function changeDesig(){
	document.getElementById("strDesignationUpdate").value='';
} */ 
	
	
function getGradebyDesig(desig){
	getContent('myGrade', 'GetGradefromDesig.action?strDesignation='+desig);
}

</script>



	<s:form theme="simple" action="AddCustomDesignation" method="POST" cssClass="formcss" enctype="multipart/form-data">
       	<s:hidden name="recruitmentID" id="recruitmentID"></s:hidden>
       	<s:hidden name="strLevel" id="strLevel"></s:hidden>
       	<s:hidden name="nCount" id="nCount"></s:hidden>
       	<s:hidden name="operation" id="operation"></s:hidden>
       	<s:hidden name="userType" id="userType"></s:hidden>
		<table border="0" class="table table_no_border" style="float: left">
	 		<tr> 
	 			<td colspan="2" class="txtlabel">For <b><%=(String)request.getAttribute("strLevelName") %></b> level </td>
			</tr>
			
        	<tr id="custDesigTR">
 				<td class="txtlabel alignRight">Custom Designation:</td>
				<td>
					<span id="custDesigTextSpan"> <s:textfield name="customDesignation" id="customDesignation"></s:textfield> </span>
					<span id="custDesigLblSpan" style="display: none;"> <%=request.getAttribute("customDesignation") %> </span>
				</td>
			 
			</tr>
			<tr id="custGradeTR">
 				<td class="txtlabel alignRight">Custom Grade:</td>
				<td>
					<span id="custGradeTextSpan"> <s:textfield name="customGrade" id="customGrade"></s:textfield> </span>
					<span id="custGradeLblSpan" style="display: none;"> <%=request.getAttribute("customGrade") %> </span>
				</td>
			</tr>
			
			<tr>
				<td class="txtlabel alignRight">Select from Existing:</td>
				<td>
					<s:checkbox id="existingDesigCheck" name="existingDesigCheck" label="Select from existing" onchange="displayTR();" theme="simple"/>
				</td>
			</tr>
			
			<tr id="desigTR" style="display:none;">
				<td class="txtlabel alignRight">Designation:</td>
				<td id="myDesig">
					<s:select theme="simple" name="strDesignationUpdate" id="strDesignationUpdate" listKey="desigId" listValue="desigCodeName" 
						headerKey="" headerValue="Select Designation" list="desigList" key="" onchange="getGradebyDesig(this.value);" />
				</td>			
			</tr>
			<tr id="gradeTR" style="display:none;">
				<td class="txtlabel alignRight">Grade:</td>
				<!-- <div id="myGrade"> -->
				<td id="myGrade">
					<s:select theme="simple" name="strGradeUpdate" id="strGradeUpdate" listKey="gradeId" listValue="gradeCode" headerKey=""
							headerValue="Select Grade" list="gradeList" key="" />
				</td>			
			</tr>
			<tr>
			<td></td>
				<td><%-- <s:submit cssClass="input_button" value="Approve" name="ApproveSubmit" align="center"></s:submit> --%>
				<input type="button" class="btn btn-primary" value="Approve" name="ApproveSubmit" onclick="approveDesigAndRequest('<%=nCount %>')" />
				</td>	
			</tr>
			
			</table>

	</s:form>

<% } %>
