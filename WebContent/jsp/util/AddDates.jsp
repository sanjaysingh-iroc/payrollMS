
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" charset="utf-8">
$(document).ready( function () {
	jQuery("#frmAddDates").validationEngine();
});
</script>

<%
	String strMessage = (String)request.getAttribute("MESSAGE");
	if(strMessage==null){
		strMessage = "";
	}
%>

	<section class="content box">
	    <div class="row">
	        <div class="col-lg-12 col-md-12 col-sm-12"> 
				<div class="pagetitle col-lg-12 col-md-12 col-sm-12">
					<span>Add Dates</span>
				</div>
				
				<div class="message" style="width:20%"><%=strMessage%></div>
				<div class="col-lg-4 col-md-6 col-sm-12">
					<s:form theme="simple" name="frmAddDates" id="frmAddDates" action="AddDates">
						<table class="table table_no_border">
							<tr>
								<td>Enter Password</td>
								<td><s:password name="strPassword" cssClass="validateRequired"/></td>
							</tr>
							<tr>
								<td>Enter No Of Days</td>
								<td><s:textfield name="strNoOfDays" cssClass="validateRequired"/></td>
							</tr>
							<tr>
								<td colspan="2"><s:submit cssClass="btn btn-primary" value="Enter Dates"/></td>
							</tr>
						</table>
					</s:form>
				</div>	
			</div>
		</div>
	</section>