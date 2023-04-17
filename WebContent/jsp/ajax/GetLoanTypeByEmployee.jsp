<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	Map<String, String> hmMemberOption = (Map<String, String>) request.getAttribute("hmMemberOption");
    if(hmMemberOption == null) hmMemberOption=new LinkedHashMap<String,String>();
    boolean isEmpLoanAutoApprove = uF.parseToBoolean((String)request.getAttribute("isEmpLoanAutoApprove"));
%>
	<s:select theme="simple" name="strLoanCode" id="strLoanCode" listKey="loanId" cssStyle="float:left;margin-right: 10px;" cssClass="validateRequired form-control"
		list="loanList" listValue="loanCode" headerKey="" headerValue="Select Loan" 
		onchange="getLoanTypeRelatedData(this.value,document.frmLoanApplication1.strEmpId.value)" />
		::::
		<table class="table table_no_border form-table">
		<%	
			if(uF.parseToBoolean(CF.getIsWorkFlow()) && !isEmpLoanAutoApprove) {		
				if(hmMemberOption != null && !hmMemberOption.isEmpty()) {
					Iterator<String> it1 = hmMemberOption.keySet().iterator();
	           		while(it1.hasNext()) {
	           			String memPosition = it1.next();
	           			String optiontr = hmMemberOption.get(memPosition);					
	           			out.println(optiontr); 
	           		}
			%>
			        <tr id="trSubmit">
			            <td>&nbsp;</td>
			            <td><input type="submit" name="submit" id="submit" value="Apply For Loan" class="btn btn-primary"/></td>
			        </tr>
		        <% } else { %>
			        <tr>
			            <td colspan="2">Your work flow is not defined. Please, speak to your hr for your work flow.</td>
			        </tr>
		     	<% } %>
	        <% } else { %>
		        <tr id="trSubmit">
		            <td>&nbsp;</td>
		            <td><input type="submit" name="submit" id="submit" value="Apply For Loan" class="btn btn-primary"/></td>
		        </tr>
	        <% } %>
	        
		</table>
		::::
		<%=(String)request.getAttribute("policy_id") %>