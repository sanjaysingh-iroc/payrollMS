<%@ taglib prefix="s" uri="/struts-tags"%>
<s:select theme="simple" name="reimbursementCTCHead" id="reimbursementCTCHead" listKey="reimbursementCTCHeadId" 
		listValue="reimbursementCTCHeadName" list="reimbursementCTCHeadList" key="" cssClass="validateRequired" onchange="getReimbursementCTCHeadPaycycle();"/>