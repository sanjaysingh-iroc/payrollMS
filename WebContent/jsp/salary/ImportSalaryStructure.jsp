<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	int nSalaryStrucuterType = uF.parseToInt((String)request.getAttribute("salaryStructure"));
 %>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<script>

$(function() {
	$("#strGrade").multiselect().multiselectfilter();
}); 

$("#btnAddNewRowOk").click(function(){
	$(".validateRequired").prop('required',true);
});

/* $("#downloadFile").click(function(){
	$(".validateRequired").prop('required',true);
}); */

//addLoadEvent(prepareInputsForHints);

<%-- function generateSalaryExcel(){
	var strOrg = document.getElementById("strOrg").value;
	var level = document.getElementById("level").value;
	
	<%if(nSalaryStrucuterType == IConstants.S_GRADE_WISE){ %>
		var strGrade = getSelectedValue("strGrade");

		if(parseInt(level) > 0 && strGrade != ''){
			window.location="ImportSalaryStructure.action?strOrg="+strOrg+"&level="+level+"&strGrade="+strGrade;
		} else {
			alert('Please select the Grade.');
		}
	<%} else { %>
		if(parseInt(level) > 0){
			window.location="ImportSalaryStructure.action?strOrg="+strOrg+"&level="+level;
		} else {
			alert('Please select the Level.');
		}
	<%} %>
	
	
} --%>



function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}

function getGradeList(){
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var strOrg = document.getElementById("strOrg").value;
		var level = document.getElementById("level").value;
		var action = 'GetGradeListByLevel.action?strOrg='+strOrg+'&levelId='+level;
		
		var xhr = $.ajax({
			url : action,
			cache : false,
			success : function(data) {
				document.getElementById('tdGrade').innerHTML = data;
				$("#strGrade").multiselect().multiselectfilter();
			}
		});
	}
}

function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
            // code for IE6, IE5
            return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
}

</script>
 
<s:form theme="simple" id="formImportSalaryStructure" action="ImportSalaryStructure" method="POST" cssClass="formcss" enctype="multipart/form-data">
	<s:hidden name="strOrg" id="strOrg"></s:hidden>
	<table class="table table_no_border form-table">
		<tr>
			<td class="alignRight"><label for="level_Name">Level:<sup>*</sup></label><br/></td>
			<td id="idLevel">
			 	<%if(nSalaryStrucuterType == IConstants.S_GRADE_WISE){ %>
					<s:select list="levelList" name="level" id="level" listKey="levelId" listValue="levelCodeName" 
			 			cssClass="validateRequired form-control autoWidth" headerKey="" headerValue="Select Level" onchange="getGradeList();"></s:select>
				<%} else { %>
					<s:select list="levelList" name="level" id="level" listKey="levelId" listValue="levelCodeName" 
			 			cssClass="validateRequired form-control autoWidth" headerKey="" headerValue="Select Level"></s:select>
			 	<%} %>
			 </td> 
		</tr>
		<%if(nSalaryStrucuterType == IConstants.S_GRADE_WISE){ %>
			<tr>
				<td class="alignRight">Grade:<sup>*</sup></td>
				<td id="tdGrade">
					<s:select theme="simple" name="strGrade" id="strGrade" cssClass="validateRequired form-control autoWidth" list="gradeList" 
						listKey="gradeId" listValue="gradeCode" required="true" multiple="true"/>
				 </td> 
			</tr>
		<%} %>
		<tr>
			<td class="alignRight">Upload:<sup>*</sup></td>
			<td><s:file name="uploadFile" accept=".xls" id="uploadFile" cssClass="validateRequired"></s:file></td>
		</tr>
		 
		<tr>
			<td>&nbsp;</td>
			<td>
				<s:submit cssClass="btn btn-primary" name="submit" value="Import" id="btnAddNewRowOk" />
				<!-- <input type="button" name="submit" id="btnAddNewRowOk" value="Import" class="btn btn-primary" onclick="importSalaryStructure()"/> -->
			</td>
		</tr>
		<tr>
			<td class="alignRight">&nbsp;</td>
			<td><s:submit cssClass="btn btn-primary" name="downloadFile" value="Download Sample File" id="downloadFile" />
				<!-- <a title="Download File" onclick="generateSalaryExcel();" href="javascript:void(0)">Download Sample File</a> -->
			</td>
		</tr>  
	</table>	
</s:form>