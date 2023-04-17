<%@taglib prefix="s" uri="/struts-tags" %>


<%-- <td id="employeeListID" style="display:none;" class="txtlabel alignRight" >
<td valign="top">Select TeamLeader
<s:select theme="simple" label="Select Employees" name="teamleadId" listKey="employeeId"  size="6" 
	           listValue="employeeName" headerKey="" headerValue="Select Team Leader" multiple="true" list="empNamesList" key="" required="true" />
	         <br/> Select Team <s:select theme="simple" label="Select Employees" name="empId" listKey="employeeId"  size="6" 
	           listValue="employeeName" headerKey="" headerValue="Select Employee" multiple="true" list="empNamesList" key="" required="true" />
	           
	</td>
		</td>   --%>

 
	
	<td valign="top">
<s:select theme="simple" label="Select Employees" name="empId" listKey="employeeId"  size="6" 
	         cssClass="validateRequired" listValue="employeeName" headerKey="" headerValue="Select Employee" multiple="true" list="empNamesList" key="" required="true" />
	    </td>   
 