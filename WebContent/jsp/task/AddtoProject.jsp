<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script>
$(document).ready( function () {
	$("#btnAddNewRowOk").click(function(){
		$(".validateRequired").prop('required',true);
	});
});	

function addProject(){
	var strProject = document.getElementById("strProject").value;
	if(parseInt(strProject) > 0){
		var strEmpId = document.getElementById("strEmpId").value;
		/* var action = "PreAddNewProject1.action?operation=E&pro_id="+strProject+"&step=1"; */
		var action = "ViewAllProjects.action?submitType=ADDTOPROJECT&proId="+strProject+"&step=1";
	} else {
		alert("Please select the project!");
	}
}

/* $("#formAddNewProject").submit(function(e){
	e.preventDefault();
	var form_data = $("form[name='formAddNewProject']").serialize();
   	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
   	$.ajax({
		url : "AddtoProject.action",
		data: form_data,
		cache : false,
		success : function(res) {
			$("#divResult").html(res);
		}
	});
}); */

</script>

<s:form theme="simple" name="formAddNewProject" id="formAddNewProject" action="AddtoProject" method="POST" cssClass="formcss">
	<s:hidden name="strEmpId" id="strEmpId"></s:hidden>
	<s:hidden name="operation" value="E"></s:hidden>
	<table class="table form-table table_no_border">
		<tr>
			<td class="txtlabel alignRight">Project<sup>*</sup>:</td>
			<td>
				<s:select theme="simple" name="strProject" id="strProject" listKey="projectID" cssClass="validateRequired" listValue="projectName" headerKey="" headerValue="Select Project"		
						list="projectList" key="" required="true" />				
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<s:submit cssClass="btn btn-primary" name="submit" value="Submit" id="btnAddNewRowOk" />
				<!-- <input type="button" align="right" value="Save" class="input_button" onclick="addProject();"> --> 
			</td>
		</tr>

	</table>
	
</s:form>