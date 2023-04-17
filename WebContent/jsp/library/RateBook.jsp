<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.min.js"> </script>

<%
	UtilityFunctions uF = new UtilityFunctions();
    String empId = (String)session.getAttribute(IConstants.EMPID);
    String bookId = (String)request.getAttribute("bookId");
    double empRating = uF.parseToDouble((String)request.getAttribute("empRating"));
    String empComment = (String)request.getAttribute("empComment");
    List<List<String>> alReviews = (List<List<String>>) request.getAttribute("alReviews");
    String operation = (String) request.getAttribute("operation");
    
%>

<% if(operation!= null && operation.equals("VIEW")) { %>
<div style="float:left;width:100%; padding: 10px;">
	<% for(int i=0; alReviews!=null && !alReviews.isEmpty() && i<alReviews.size(); i++) { 
		List<String> innerList = alReviews.get(i);
	%>
		<div style="float:left;width:100%; margin: 5px 0px;">
			<div style="float:left;width:100%;">
				<div style="float: left; margin-right: 5px; font-weight: bold;"><%=innerList.get(1) %>: </div>
				<div id="starPrimaryS_<%=innerList.get(0) %>" style="float: left; margin-left: 5px; line-height: 12px;"></div>
				<script type="text/javascript">
						$('#starPrimaryS_<%=innerList.get(0) %>').raty({
		                    readOnly: true,
		                    start:	<%=innerList.get(2) %>,
		                    half: true,
		                    targetType: 'number'
						});
				</script>
			</div>
			
			<div style="float:left; width:100%; color: gray; margin-left: 15px;"><strong>comment:</strong> <%=innerList.get(3) %></div>
		</div>
	<% } %>
</div>

<% } else { %>
<div style="float:left;width:100%;">	
	<s:form id = "frmAddRate" name="frmAddDate" action="RateBook" method = "POST" theme ="simple">
		<input type = "hidden" name="bookId" value = "<%=bookId %>"/>
		<input type = "hidden" name="empId" value = "<%=empId %>"/>
		
		<div id="bookRatingDiv_<%=empId %>_<%=bookId%>" style="display:block;width:100%;float: left; padding-left: 10px;">
			
			<input type="hidden" name="bookRating" id="bookRating">
			<div id="starBookRating" style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
			<script type="text/javascript">
		        	$('#starBookRating').raty({
		        		readOnly: false,
		        		start: <%=empRating %>,
		        		half: true,
		        		targetType: 'number',
		        		click: function(score, evt) {
		        				$('#bookRating').val(score);
							}
						});
			</script>
			<br/>
			<div style="float: left;width:100%; margin: 0px 0px 5px 0px;">
				<div style = "float:left;width:98%;">Comment:</div>
				<div style = "float:left;width:98%;"><textarea rows="3" cols="20" style="width:75%;"  class=" form-control autoWidth" name="strComment" id="strComment"><%=uF.showData(empComment, "") %></textarea></div>
			</div>
			<% if(empRating>0) { %>
				<div style="float: left; margin: 0px 0px 5px 7px;">
					<input type="submit" class="btn btn-primary"  name="strUpdate" style="margin-top: 5px;" value="Update">
				</div>
			<% } else { %>
				<div style="float: left; margin: 0px 0px 5px 7px;">
					<input type="submit" class="btn btn-primary"  name="strSubmit" style="margin-top: 5px;" value="Submit">
				</div>
			<% } %>
		</div>
	</s:form>
</div>
<% } %>
