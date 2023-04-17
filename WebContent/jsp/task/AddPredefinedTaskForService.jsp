<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript">
$(document).ready( function () {
	$("#formAddPredefinedTaskForService").click(function(){
		$(".validateRequired").prop('required', true);
	});
});
</script>

		<s:form id="formAddPredefinedTaskForService" action="AddPredefinedTaskForService" method="post" theme="simple">
			<s:hidden name="strServiceTaskId" />
			<s:hidden name="strServiceId" />
			<s:hidden name="userscreen" />
			<s:hidden name="navigationId" />
			<s:hidden name="toPage" />
			<s:hidden name="operation"/>
			<table border="0" class="table table_no_border">
				<tr>
					<th class="txtlabel alignRight">Task Name:<sup>*</sup></th>
					<td><s:textfield name="strTaskName" cssClass="validateRequired"/></td>
				</tr>
				<tr>	
					<th class="txtlabel alignRight">Task Description:</th>
					<td><s:textarea name="strTaskDesc"></s:textarea></td>
				</tr>	
				<tr>
					<td colspan="4" align="center"><s:submit value="Save Task" cssClass="btn btn-primary" /></td>
				</tr>
			</table>

		</s:form>
	