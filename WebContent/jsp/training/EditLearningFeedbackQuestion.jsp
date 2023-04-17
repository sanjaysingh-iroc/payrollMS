
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<%
	Map hmScoreDetailsMap = (Map) request.getAttribute("hmScoreDetailsMap");
	String fromPage = (String) request.getAttribute("fromPage");
	Map<String, String> questMp = (Map<String, String>) request.getAttribute("questMp");
	Map<String, Map<String,String>> memberMp =(Map<String, Map<String,String>>) request.getAttribute("memberMp");
	UtilityFunctions uF = new UtilityFunctions();
	
%>

<div id="mainDiv" style="float: left; margin: 10px 0px 0px 0px; width: 100%;">
	<s:form action="editLearningFeedbackQuestion" name="formEditLearningFeedbackQuestion" id="formEditLearningFeedbackQuestion" method="POST" theme="simple">
		<s:hidden name="ID"></s:hidden>
		<s:hidden name="operation"></s:hidden>
		<s:hidden name="step"></s:hidden>
		<s:hidden name="queID"></s:hidden>
		<s:hidden name="queAnstype"></s:hidden>
		<input type="hidden" name="fromPage" id="fromPage" value="<%=fromPage %>"/>
		<%
			int counter = 0;
			String anstype = (String) request.getAttribute("anstype"); 
			List<String> queDetailsList = (List<String>) request.getAttribute("queDetailsList");	

		%>
		<ul class="ul_class">
			<li style="margin-left: 0px;">
				<table class="table table_no_border" width="100%">
					<tr>
						<th><%=request.getAttribute("queno") %></th><th style="text-align: right;">Edit Question<sup>*</sup></th>
						<td colspan="3">
							<input type="hidden" name="ansType" id="ansType<%=counter %>" value="<%=request.getAttribute("queAnstype") %>"/>
							<input type="hidden" name="questionID" value="<%=queDetailsList.get(0)%>"/>
							<span id="newquespan<%=counter %>" style="float: left;">
								<textarea rows="2" name="question" id="question<%=counter %>" class="validateRequired" style="width: 330px;" required><%=queDetailsList.get(1) %></textarea>
							</span>
							<span style="float: left; margin-left: 10px;"><input type="hidden" name="orientt" value="0"/></span>
							<span style="float: left; margin-left: 10px;">
								<a href="javascript:void(0)" title="Select from Question Bank" onclick="openQuestionBank('<%=counter%>','');" > +Q </a></span>
								 <span id="checkboxspan<%=counter %>" style="float: left; margin-left: 10px;"><input name="addFlag" type="checkbox" id="addFlag<%=counter %>" 
								 <%if(queDetailsList.get(10) != null && uF.parseToBoolean(queDetailsList.get(10))) { %>checked <%} %>title="Add to Question Bank" onclick="changeStatus('<%=counter%>')"/>
								<input type="hidden" id="status<%=counter %>" name="status" value="0"/>
							</span>
							<%
									int getanstype = uF.parseToInt(queDetailsList.get(9));
									if(getanstype == 1 || getanstype == 2 || getanstype == 8) { %>
										<tr id="answerType<%=counter %>">
											<th></th>
											<th></th>
											<td>a)&nbsp;<input type="text" name="optiona" value="<%=queDetailsList.get(4)%>"/> <input type="radio" value="a" name="correct<%=counter %>"
												<%if(queDetailsList.get(8).contains("a")){ %> checked="checked" <%} %> /> </td>
											<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=queDetailsList.get(5)%>"/><input type="radio" name="correct<%=counter %>" value="b" 
												<%if(queDetailsList.get(8).contains("b")){ %> checked="checked" <%} %>/></td>
										</tr>
										<tr id="answerType1<%=counter %>">
											<th></th>
											<th></th>
											<td>c)&nbsp;<input type="text" name="optionc" value="<%=queDetailsList.get(6)%>"/> <input type="radio" name="correct<%=counter %>" value="c"
												<%if(queDetailsList.get(8).contains("c")){ %> checked="checked" <%} %> /></td>
											<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=queDetailsList.get(7)%>"/> <input type="radio" name="correct<%=counter %>" value="d"
												<%if(queDetailsList.get(8).contains("d")){ %> checked="checked" <%} %> /></td>
										</tr>
									<%} else if(getanstype == 9){ %>
											<tr id="answerType<%=counter %>">
												<th></th>
												<th></th>
												<td>a)&nbsp;<input type="text" name="optiona" value="<%=queDetailsList.get(4)%>"/> <input type="checkbox" value="a" name="correct<%=counter %>"
														<%if(queDetailsList.get(8).contains("a")) { %> checked="checked" <%} %> /> </td>
												<td colspan="2">b)&nbsp;<input type="text" name="optionb" value="<%=queDetailsList.get(5)%>"/> <input type="checkbox" name="correct<%=counter %>" value="b" 
														<%if(queDetailsList.get(8).contains("b")){ %> checked="checked" <%} %> /></td>
											</tr>
											<tr id="answerType1<%=counter %>">
												<th></th>
												<th></th>
												<td>c)&nbsp;<input type="text" name="optionc" value="<%=queDetailsList.get(6)%>"/> <input type="checkbox" name="correct<%=counter %>" value="c"
													<%if(queDetailsList.get(8).contains("c")){ %> checked="checked" <%} %> /></td>
												<td colspan="2">d)&nbsp;<input type="text" name="optiond" value="<%=queDetailsList.get(7)%>"/> <input type="checkbox" name="correct<%=counter %>" value="d" 
													<%if(queDetailsList.get(8).contains("d")){ %> checked="checked" <%} %> /></td>
											</tr>
									<%}else if(getanstype == 6){ %>
											<tr id="answerType<%=counter %>">
												<th></th>
												<th></th>
												<td>
													<input type="hidden" name="optiona" value="<%=queDetailsList.get(4)%>"/><input type="hidden" name="optionb" value="<%=queDetailsList.get(5)%>"/>
													<input type="hidden" name="optionc" value="<%=queDetailsList.get(6)%>"/><input type="hidden" name="optiond" value="<%=queDetailsList.get(7)%>"/>
													<input type="radio" name="correct<%=counter %>" value="1"  <%if(queDetailsList.get(8).contains("1")){ %> checked="checked" <%} %> >True&nbsp;
													<input type="radio" name="correct<%=counter %>" value="0" <%if(queDetailsList.get(8).contains("0")){ %> checked="checked" <%} %> >False
												</td>
											</tr>
									<%}else if(getanstype == 5){ %>
											<tr id="answerType<%=counter %>">
												<th></th>
												<th></th>
												<td>
													<input type="hidden" name="optiona" value="<%=queDetailsList.get(4)%>"/><input type="hidden" name="optionb" value="<%=queDetailsList.get(5)%>"/>
													<input type="hidden" name="optionc" value="<%=queDetailsList.get(6)%>"/><input type="hidden" name="optiond" value="<%=queDetailsList.get(7)%>"/>
													<input type="radio" name="correct<%=counter %>" value="1"  <%if(queDetailsList.get(8).contains("1")){ %> checked="checked" <%} %> >Yes&nbsp; 
													<input type="radio" name="correct<%=counter %>" value="0"  <%if(queDetailsList.get(8).contains("0")){ %> checked="checked" <%} %> >No
											   </td>
											</tr>
									<%} %>
							</table>
						</li>
				</ul>
				<% counter++; %>

			<div align="center">
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
				<%-- <input type="button" value="Cancel" class="cancel_button" name="cancel" onclick="closeEditDiv('<%=request.getAttribute("quediv") %>')"/> --%>
			</div>
	</s:form>
</div>
<script>
<%if(fromPage != null && fromPage.equals("LD")){ %>
$("#formEditLearningFeedbackQuestion").submit(function(event){
	event.preventDefault();
	var form_data = $("#formEditLearningFeedbackQuestion").serialize();
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   $.ajax({
  		url: "editLearningFeedbackQuestion.action",
  		data: form_data+"&submit=Save",
  	    success: function(result){
  	    	$("#divResult").html(result);
  		}
    });
});

<%}%>
</script>
