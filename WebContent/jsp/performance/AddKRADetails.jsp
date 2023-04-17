<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
	$(document).ready(function() {
		jQuery("#frmId11").validationEngine();
	});
</script>


<div>

	<s:form id="frmId11" method="POST" theme="simple" action="AddKRADetails">
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="type"></s:hidden>
		<table>
		 	<tr>
				<td style="text-align: right;vertical-align: top;" >Key Responsibility Area<sup>*</sup></td>
				<td><s:textarea name="kra" cssClass="validateRequired" id="kra" rows="4" cols="64"></s:textarea></td>
			</tr>
			<tr>
				<td style="text-align: right;vertical-align: top;">Description<sup>*</sup></td>
				<td><s:textarea name="kraDesc" cssClass="validateRequired" id="kra" rows="4" cols="64"></s:textarea></td>
			</tr>
			<tr> 
				<td style="text-align: right;">Attribute<sup>*</sup></td>
				<td><s:select theme="simple" name="strAttribute" list="attributeList" listKey="id"
                                        id="strAttribute" listValue="name" headerKey=""
                                        headerValue="select Attribute" required="true" cssClass="validateRequired"></s:select></td>
			</tr>
			<tr>
				<td style="text-align: right;">Levels<sup>*</sup></td>
				<td><s:select theme="simple" name="strLevel" list="levelList" listKey="levelId"
                                        id="strLevel" listValue="levelCodeName" headerKey=""
                                        headerValue="select Level" required="true" cssClass="validateRequired" multiple="true" size="4"></s:select></td>
			</tr>
			<tr>
				<td style="text-align: right;">Measurable<sup>*</sup></td>
				<td><s:select theme="simple" name="measurable" list="#{'1':'Yes', '0':'No'}" />
				</td>
			</tr>
		
			<tr>
				<td colspan="2">
				<div style="float: left; width: 100%; text-align: center;">
					<s:submit name="submit" value="Save" cssClass="input_button"></s:submit>
				</div>
				</td>
			</tr>

		</table>

	</s:form>
</div>
