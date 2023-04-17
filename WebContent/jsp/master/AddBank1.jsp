<%@page import="com.konnect.jpms.select.FillUserStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});

</script>
 
	<s:form theme="simple" id="formAddNewRow" action="AddBank1" method="POST" cssClass="formcss">
	<s:hidden name="bankId" />
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
		<table border="0" class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">Bank Code:<sup>*</sup></th>
				<td>
					<s:textfield name="bankCode" id="levelCode" cssClass="validateRequired"/> 
					<span class="hint">Bank Code<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
		 
			<tr>
				<th class="txtlabel alignRight">Bank Name:<sup>*</sup></th>
				<td>
					<s:textfield name="bankName" id="levelName" cssClass="validateRequired" /> 
					<span class="hint">Bank Name<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
			
			<tr>
				<th class="txtlabel alignRight">Bank Description:</th>
				<td>
					<s:textfield name="bankDesc" id="levelDesc" /> 
					<span class="hint">Bank Description<span class="hint-pointer">&nbsp;</span></span>
				</td>
			</tr>
	
			<tr>
				<td></td>
				<td>
					<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk" /> 
				</td>
			</tr>
		</table>
	</s:form>

