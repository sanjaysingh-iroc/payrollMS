<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">
function changeBreakPolicy(DATE,EID,SID,AS,AE,divid,startTime,endTime,strE){
	var typeOfbreak=document.getElementById("typeOfbreak").value;
	if(typeOfbreak==""){
		alert('Please Select break policy.');
	}else{
		var strServiceId=SID;
		var strEmpId=EID;
		var strDate=DATE;
		var strStatus='';
		
		var action='ChangeBreakPolicyType.action?strDate='+DATE+'&empid='+EID+'&serviceId='+SID+'&strAS='+AS+'&strAE='+AE+'&divid='+divid+'&status=change&typeOfbreak='+typeOfbreak;
		getContent(divid,action);
		
		$("#the_div").dialog('close');
	}
	 
}
</script>

<div class="aboveform">

<s:form theme="simple" name="frm" action="ChangeBreakPolicyType" id="frm" cssClass="formcss" method="post">
		<s:hidden name="strDate"></s:hidden>
		<s:hidden name="empid"></s:hidden>
		<s:hidden name="serviceId"></s:hidden>
		<s:hidden name="strAS"></s:hidden>
		<s:hidden name="strAE"></s:hidden>
		<s:hidden name="divid"></s:hidden>
		
		<table border="0" class="formcss">
			<tr>
				<td class="txtlabel alignRight" nowrap="nowrap">Break Policy:<sup>*</sup></td>
				<td><s:select name="typeOfbreak" id="typeOfbreak" cssClass="validateRequired text-input" listKey="breakTypeId" listValue="breakTypeName" 
				 headerKey="" headerValue="Select Break Type" list="empBreakTypeList" key="" required="true" /></td>
			</tr>
			  
			<tr>  
				<td>&nbsp;</td>
				<td>
					<input type="button" class="input_button" value="Save" 
					onclick="changeBreakPolicy('<%=request.getParameter("strDate") %>','<%=request.getParameter("empid") %>','<%=request.getParameter("serviceId") %>','<%=request.getParameter("strAS") %>','<%=request.getParameter("strAE") %>','<%=request.getParameter("divid") %>');"/>
				</td>
			</tr>
		
		</table>

</s:form>

</div>

				