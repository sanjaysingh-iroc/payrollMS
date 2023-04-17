
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- <script type="text/javascript">

function changePanelRound(empId,roundId,recruitID,strRound){
	if(confirm("Are you sure, do you want to change round?")){
		/* alert("You can not modify this employee list ..."); */
		getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?empId='+empId+'&recruitID='+recruitID+'&roundId='+roundId+'&strRound='+strRound+'&mode=emproundchange');
		$("changeRoundDiv").dialog('close');
		addpanel(recruitID);
	}else{
		/* getContent('idEmployeeInfo', 'AddAndGetPanelRound.action?chboxStatus='+checked+'&selectedEmp='+value+'&recruitmentID=<s:property value="recruitID"/>') */	
	}
}
</script> --%>

<div id="offerAcccept">

 <form id="frm_roundchange" name="frm_roundchange" action="ChangePanelRound.action" method="post">
	<s:hidden name="empID" />
	<s:hidden name="roundID" />
	<s:hidden name="recruitID" />
	<s:hidden name="updateRound" value="Update"/>
	<div style="float:left; margin:10px; width: 100%; text-align: center;">
		Select Round : <%-- <sup>*</sup> --%>
		<s:select theme="simple" name="strRound" listKey="roundId" listValue="roundName" headerKey="" 
		headerValue="All Round" list="roundList" key="" required="true"/>
	</div>
	<div style="float:left; margin:10px; width: 100%; text-align: center;">
	<input type="submit" class="input_button" name="update" value="Change Round" />
	<%-- <input type="button" class="input_button" name="update" value="Update" 
	onclick="changePanelRound(<%=request.getAttribute("empID") %>,<%=request.getAttribute("roundID") %>,<%=request.getAttribute("recruitID") %>
	,<s:property value="strRound"/>);"/> --%>
	</div>
</form> 
</div>
