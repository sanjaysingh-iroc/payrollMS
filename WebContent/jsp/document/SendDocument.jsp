<%@taglib prefix="s" uri="/struts-tags" %>

<script>
function setMailOptions(isSendMail){
	
	if(isSendMail.checked){
		document.getElementById("row1").style.display = "table-row";
		document.getElementById("row2").style.display = "table-row";
		document.getElementById("row3").style.display = "table-row";
		document.getElementById("row4").style.display = "block";
	}else{
		document.getElementById("row1").style.display = "none";
		document.getElementById("row2").style.display = "none";
		document.getElementById("row3").style.display = "none";
		document.getElementById("row4").style.display = "none";
	}
	
}
</script>

<div class="cat_heading"><h3>Send Document:</h3></div>

<s:form name="" theme="simple" action="SendDocument">







<table style="float:left; margin:10px 0px">
<s:hidden name="doc_id"/>
<s:hidden name="orgId"/>

	<tr>
		<td>Select Employee:</td>
		<td><s:select list="empList" name="strEmpId" listKey="employeeId" listValue="employeeCode" headerKey="0" headerValue="Choose Employee"></s:select> </td>
	</tr>
	
	<tr>
		<td>Send Mail:</td>
		<td><s:checkbox name="isSendMail" onclick="setMailOptions(this);"></s:checkbox> </td>
	</tr>
	
	<tr id="row1" style="display: none"> 
		<td>Subject:</td>
		<td><s:textfield name="strSubject" cssStyle="width:385px"/></td>
	</tr>
	
	<tr id="row2" style="display: none"> 
		<td colspan="2">Mail Body:</td>
	</tr> 

	<tr id="row3" style="display: none">
		<td colspan="2"><s:textarea name="strMailBody" rows="100" cols="5" cssStyle="width:500px;height:200px;"> </s:textarea></td>
	</tr>

	
	
	<tr>
		<td colspan="2" align="center"><s:submit value="Send Document" cssClass="input_button"></s:submit></td>
	</tr>
</table>

	
	
	<div style="float: left;margin-left: 10px;margin-top: 65px;font-size: 11px;display:none;" id="row4">
	
					[EMPCODE]<br/>
					[EMPFNAME]<br/>
					[EMPLNAME]<br/>
					[JOINING_DATE]<br/>
					[DATE]<br/>
					[EMP_CTC]<br/>
					[DESIGNATION]<br/>
					[LEVEL]<br/>
					[GRADE]<br/>
					[WLOCATION]<br/>
					<br/><br/>
	
	</div>
	
	
	
	
	

</s:form>