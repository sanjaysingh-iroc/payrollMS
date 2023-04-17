<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script>
	$(function() {
		$("#deadline").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		jQuery("#formID").validationEngine();
	});
	addLoadEvent(prepareInputsForHints);
</script>
<div class="leftbox reportWidth">
	<center>
		<form class="formcss" id="formID" action="EditProjectDetails.action"
			method="post" enctype="multipart/form-data">
			<center>
				<b>Edit Project Details</b>
			</center>
			<s:hidden name="pro_id" />
			<table class="formcss">
				<tr>
					<td class="txtlabel alignRight"><s:textfield
							label="Project Name " cssClass="validateRequired" id="pro_name"
							name="pro_name" required="true" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight"><s:textfield
							label="Service Name " cssClass="validateRequired" id="service"
							name="service" required="true" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight" valign="top"></td>
					<td><s:textarea name="description" cols="50" rows="05"
							label="Description" cssClass="validateRequired" required="true" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight"><s:textfield
							label="IdealTime " cssClass="validateRequired" id="idealtime"
							name="idealtime" required="true" />
					</td>
				</tr>
				<tr>
					<td class="txtlabel alignRight"><s:textfield label="DeadLine"
							cssClass="validateRequired" id="deadline" name="deadline"
							required="true" />
					</td>
				</tr>
				<tr>
					<s:file name="document" label="Upload Document" />
				</tr>
			</table>
			<center>
				<input type="submit" class="input_button" value="Update"
					name="submit" onclick="return showd();" />
			</center>
		</form>
	</center>
</div>