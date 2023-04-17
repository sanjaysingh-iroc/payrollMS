<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script>

</script>

<s:form theme="simple" id="formAddNewRow" action="AddCategory" method="POST" cssClass="formcss" cssStyle="display: none;">

	<table border="0" class="formcss" style="width: 675px">
		
		<tr>
			<td class="txtlabel alignRight">Category Code<sup>*</sup>:</td>
			<td>
				<input type="text" name="categoryCode" id="categoryCode" rel="0" class="required" /> 
				<span class="hint">Category Code<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>
	
		<tr>
			<td class="txtlabel alignRight">Category Description:</td>
			<td>
				<input type="text" name="categoryDesc" id="categoryDesc" rel="1" /> 
				<span class="hint">Category Description<span class="hint-pointer">&nbsp;</span></span>
			</td>
		</tr>

		<tr>
			<td></td>
			<td>
				<s:submit cssClass="input_button" value="Ok" id="btnAddNewRowOk" /> 
				<s:submit cssClass="input_button" value="Cancel" id="btnAddNewRowCancel" />
			</td>
		</tr>

	</table>
	
</s:form>

