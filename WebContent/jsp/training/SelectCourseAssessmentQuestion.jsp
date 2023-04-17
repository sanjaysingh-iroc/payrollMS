<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<%
	String opt = (String) request.getAttribute("option");
	//System.out.println("opt in new " + opt);
	String count = (String) request.getAttribute("count");
	//System.out.println("count in new " + count);
%>
<div>
	<s:form id="formID" name="frmselectQue" theme="simple" action="SelectCourseAssessmentQuestion"
		method="POST" cssClass="formcss">

		<table class="tb_style" style="width: 100%">
			<tr>
				<th width="30%" align="right">Select Question</th>
				<td>
				<s:hidden name="count" id="count"></s:hidden>
				<s:hidden name="divCnt" id="divCnt"></s:hidden>
				<s:hidden name="chaptercnt" id="chaptercnt"></s:hidden>
				<select name="questionSelect" id="questionSelect" style="width: 80%;"><option value="">Select Question</option><%=opt %></select>
				</td>
			</tr>
			 
			<tr>
				<th align="right">&nbsp;</th>
				<td>&nbsp;</td>
			</tr>

		</table>
		
		<div align="center">
			<input type="button" value="Ok" class="btn btn-primary" name="ok" onclick="setQuestionInTextfield();" />
			<!-- <input type="button" value="Cancel" class="input_button" name="cancel" onclick="closePopup();" /> -->
		</div>
	</s:form>
</div>




