<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%-- <script type="text/javascript">
$(document).ready(function(){
	$('input[name="ok"]').click(function(){
		 $("#formID").find('.validateRequired').filter(':hidden').prop('required',false);
	     $("#formID").find('.validateRequired').filter(':visible').prop('required',true);
	});
});
</script> --%>
<%
	String opt = (String) request.getAttribute("option");
	String count = (String) request.getAttribute("count");
	
%>
<div>
	<s:form id="formID" name="frmselectQue" theme="simple" action="SelectLearningPlanQuestion" method="POST" cssClass="formcss">

		<table class="tb_style" style="width: 100%">
			<tr>
				<th width="30%" align="right">Select Question:<sup>*</sup></th>
				<td>
					<s:hidden name="count" id="count"></s:hidden> 
					<select name="questionSelect" id="questionSelect" style="width: 80%;">
						<option value="">Select Question</option><%=opt%>
					</select>
			   </td>
			</tr>

			<tr>
				<th align="right">&nbsp;</th>
				<td>&nbsp;</td>
			</tr>
		</table>

		<div align="center">
			<input type="button" value="Ok" class="input_button" name="ok" onclick="setQuestionInTextfield();" />
			
		</div>
	</s:form>
</div>




