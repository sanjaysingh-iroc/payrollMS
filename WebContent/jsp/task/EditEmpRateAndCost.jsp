<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript">

	$("#formEditEmpRateAndCost").submit(function(e){
		e.preventDefault();
		var proId = document.getElementById("proID").value;
		var form_data = $("form[name='formEditEmpRateAndCost']").serialize();
		//alert("form_data ===>> " + form_data);
     	$("#subSubDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
     	$.ajax({
 			url : "EditEmpRateAndCost.action?submit=Save",
 			data: form_data,
 			cache : false,
 			type : "POST",
 			success : function(res) {
 				$("#subSubDivResult").html(res);
 			}
 		});
     	$("#modalInfo").hide();
     	
     	$.ajax({
			url: 'PreAddNewProject1.action?pro_id='+proId+'&operation=E&step=1',
			cache: true,
			success: function(result){
				$("#subSubDivResult").html(result);
	   		}
		});
	});
	
</script>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF=new UtilityFunctions();
	String type = (String)request.getAttribute("type");
	String billingType = (String)request.getAttribute("billingType");
%>
<!-- <div class="leftbox reportWidth" style="font-size: 12px;"> -->
	<s:form id="formEditEmpRateAndCost" name="formEditEmpRateAndCost" theme="simple" action="EditEmpRateAndCost" method="POST">
<!-- <div style="width: 100%; float: left; margin: 7px;"></div> -->
	<s:hidden name="proID" id="proID"/>
	<s:hidden name="empID"/>
	<s:hidden name="type"/>
	<s:hidden name="billingType"/>
	
		<table class="table table_bordered">
		  
		  <%if(type != null && type.equals("rate")) { %>
		  <% if(billingType != null && billingType.equals("H")) { %>
		  <tr>
		   	<td valign="top">Rate/Hour</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empRatePerHour" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } else if(billingType != null && billingType.equals("M")) { %>
		  <tr>
		   	<td valign="top">Rate/Month</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empRatePerMonth" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } else { %>
		  <tr>
		   	<td valign="top">Rate/Day</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empRatePerDay" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } %>
		  <%-- <tr> 	
		   	<td valign="top"><s:textfield name="empRateOverheadsLbl"/> </td>
		   	<td valign="top"><s:textfield name="empRateOverheadsAmt"/> </td>
		  </tr> --%>
		  <% } else if(type != null && type.equals("cost")) { %>
		  <% if(billingType != null && billingType.equals("H")) { %>
		  <tr>
		   	<td valign="top" >Cost/Hour</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empActualRatePerHour" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } else if(billingType != null && billingType.equals("M")) { %>
		  <tr>
		   	<td valign="top">Cost/Month</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empActualRatePerMonth" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } else { %>
		  <tr>
		   	<td valign="top">Cost/Day</td>
		   	<td valign="top"><%=(String)request.getAttribute("strShortCurrency") %> <s:textfield name="empActualRatePerDay" cssStyle="width: 120px !important;"></s:textfield> </td>
		  </tr>
		  <% } %>
		 <%--  <tr> 	
		   	<td valign="top"><s:textfield name="empActualRateOverheadsLbl"/> </td>
		   	<td valign="top"><s:textfield name="empActualRateOverheadsAmt"/> </td>
		  </tr> --%>
		  <% } %>
		  
		  <tr><td colspan="2" align="center"> <s:submit name="submit" cssClass="btn btn-primary" value="Save"/></td></tr>
		</table>

	</s:form>
<!-- </div> -->	

