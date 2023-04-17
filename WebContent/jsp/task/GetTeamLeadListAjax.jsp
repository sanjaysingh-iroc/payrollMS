<%@taglib prefix="s" uri="/struts-tags" %>



<td valign="top">
<s:select theme="simple" label="Select TeamLead" name="teamleadId" listKey="employeeId"  size="6" 
	        cssClass="validateRequired" listValue="employeeName" headerKey="" headerValue="Select Team Leader" multiple="true" list="teamleadNamesList" key="" required="true" />
	</td>
  


<%-- <td id="empListID" style="display:none;" class="txtlabel alignRight" > <strong>Select Team Leaders</strong></td>
	
	<td valign="top">
<s:select theme="simple" label="Select Employees" name="empId" listKey="employeeId"  size="6" 
	           listValue="employeeName" headerKey="" headerValue="Select Employee" multiple="true" list="empNamesList" key="" required="true" />
	    </td>   --%>
