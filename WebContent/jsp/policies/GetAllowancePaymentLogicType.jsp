<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>    
<s:select name="strAllowancePaymentLogic" cssClass="validateRequired" headerKey="" headerValue="Select Payment Logic"
	list="allowancePaymentLogicList" listKey="paymentLogicId" listValue="paymentLogicName" onchange="showAllowancePaymentLogic(this.value);"></s:select>