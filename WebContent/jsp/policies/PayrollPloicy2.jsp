<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/struts-tags" prefix="s"%>

<%	
	String EMPNAME = (String)request.getAttribute("empName");
	String EMPID_P = (String)request.getAttribute("empId");
    //System.out.println("EMPNAME==>"+EMPNAME+"==>empId==>"+EMPID_P); 
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	//EncryptionUtils EU = new EncryptionUtils();// Created By Dattatray Date : 21-July-2021 Note : Encryption
%>
<%--
<% if (strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)) {%>
 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Employee" name="title"/>
</jsp:include>
 
<%}%>--%>
 
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
        	<div class="box box-primary">
				<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
					<% if (strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)) {%>
					<!-- Created By Dattatray Date : 20-July-2021 Note : empId Encrypt -->
						<p>You have successfully added <strong><%=(EMPNAME!=null)? EMPNAME : ""%></strong>. <a href="MyProfile.action?empId=<%=EMPID_P %>">Click here to view the factsheet for this employee.</a></p> 
						<p>Please <a href="AddEmployee.action">click here</a> to add new Employee <a href="Roster.action?callFrom=FA"> or click here</a> to assign the Shift for this employee or <a href="People.action?callFrom=MP">click here</a> to go to the Employee Database. </p> 
		
					<%}else {%>
						<div class="nodata msg">
							<h1>Thank you <%=request.getAttribute("EMP_FNAME") %>!</h1>
							<br/><br/>
							<p>Thank you for filling up the information, we have received your information.</p>
							<p>We shall process your given information and send you the notification for next steps.</p> 
							<p>Till then, should you require any further information please contact us at <a href="mailto:<%=request.getAttribute("ADDED_BY_EMAIL") %>"><%=request.getAttribute("ADDED_BY_EMAIL") %></a> for further details.</p>
						</div>
					<%}%>
				</div>
		   </div>
		</section>
	</div>
</section>