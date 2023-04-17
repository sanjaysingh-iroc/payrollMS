<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="empNamesList!=null">
		<s:if test="type!=null && type == 'HR'">
			<s:if test="hrValidReq!=null && hrValidReq != ''">
				<s:select name="HR" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
					headerValue="Select HR" list="empNamesList" key="" />
			</s:if>
			<s:else>
				<s:select name="HR" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey=""  
					headerValue="Select HR" list="empNamesList" key="" />
			</s:else>
        </s:if>

		<s:elseif test="type!=null && type == 'HOD'">
			<s:if test="hodValidReq!=null && hodValidReq != ''">
				<s:select name="hod" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
					headerValue="Select HOD" list="empNamesList" key="" />
			</s:if>
			<s:else>
				<s:select name="hod" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey=""  
					headerValue="Select HOD" list="empNamesList" key="" />
			</s:else>
        </s:elseif>
        
        <s:elseif test="type!=null && type == 'SUPERVISOR'">
			<s:if test="supervisorValidReq!=null && supervisorValidReq != ''">
				<%-- <s:select name="supervisor" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
					headerValue="Select Supervisor" list="empNamesList" key="" /> --%>
					
				<s:select name="supervisor" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey="" cssClass="validateRequired" 
					headerValue="Select Manager" list="empNamesList" key="" />
			</s:if>
			<s:else>
				<%-- <s:select name="supervisor" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey=""  
					headerValue="Select Supervisor" list="empNamesList" key="" /> --%>
				<s:select name="supervisor" listKey="employeeId" theme="simple" listValue="employeeCode" headerKey=""  
					headerValue="Select Manager" list="empNamesList" key="" />
			</s:else>
        </s:elseif>
        
        <s:elseif test="project!=null">
			<s:select theme="simple" name="strSelectedEmpId1" listKey="employeeId" listValue="employeeName" headerKey="0" 
				headerValue="Select Employee" list="empNamesList" key="" required="true" 
				onchange="getContent('typeC', 'GetEmpClientListAjax.action?empId='+document.frm_MyReimbursements.strSelectedEmpId1.options[document.frm_MyReimbursements.strSelectedEmpId1.selectedIndex].value)" />
        </s:elseif>
        
        <s:elseif test="multiple==null">
			<s:select theme="simple" label="Select Single Employee" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" 
				headerKey="0"  headerValue="Select Employee" list="empNamesList" key="" required="true" onchange="submitForm('2');"/>
        </s:elseif>
        
        <s:elseif test="multiple!=null && multiple== 'LblAll'">
			<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" listValue="employeeName" 
				headerKey="0" headerValue="All Employee" list="empNamesList" key="" required="true" onchange="submitForm('2');"/>
        </s:elseif>
        
        <s:elseif test="fromPage!=null && fromPage== 'EMP_VARI_FORM'">
        	<s:select name="employee" list="empNamesList" theme="simple" listKey="employeeId" id="employee" listValue="employeeName" required="true" />
        </s:elseif>
        
        <s:elseif test="fromPage!=null && fromPage== 'ACTIVITY_HISTORY_REPORT'">
        	<s:select name="strEmpId" list="empNamesList" theme="simple" headerKey="" headerValue="Select Employee" listKey="employeeId" id="strEmpId" listValue="employeeName" />
        </s:elseif>
        
        <s:else>
			<s:select theme="simple" label="Select Employees" name="empIds" listKey="employeeId"  size="6" 
	           listValue="employeeName" headerKey="0" multiple="true" list="empNamesList" key="" required="true" />
        </s:else>
		
	
</s:if>
