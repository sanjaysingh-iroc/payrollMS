<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>


<%if(request.getAttribute("pagefrom")==null){ %>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Interview Schedule" name="title"/>
</jsp:include>

<%} %>    


		<%		
				UtilityFunctions uF = new UtilityFunctions();	
				Map<String, String> hmSelectedDate=(Map)request.getAttribute("hmSelectedDate");
				Map<String, String> hmRejectedDate=(Map)request.getAttribute("hmRejectedDate");
				
				Map<String, String> hmDates=(Map)request.getAttribute("hmDateMap");
				Map<String, String> hmTime=(Map)request.getAttribute("hmTimeMap");
					
			/* 	Map<String, String> hmDates = (Map<String, String>) request.getAttribute("hmDates"); 	
			String dateSelected=(String)request.getAttribute("SelectedDate");  */
		%>

 
	<div style="text-align: center; padding: 10px;float:left;">
		<s:form theme="simple" action="CandidateInterviewSchedule"
			method="post" cssClass="formcss">
			<s:hidden name="candidateID"></s:hidden>
			<s:hidden name="recruitID"></s:hidden>
			<s:hidden name="pagefrom"></s:hidden>
			<s:hidden name="panelEmpID"></s:hidden>
		
			<table style="width:320px" class="tb_style">
				<tr>
					<th>&nbsp;</th>
					<th class="txtlabel alignRight">Date</th>
					<th class="txtlabel" align="left">Time</th>
					<th class="txtlabel" align="left">Choose option</th>
				</tr>

				<%
						int count = 0;
						Iterator<String> itr = hmDates.keySet().iterator();
						while (itr.hasNext()) {
						String id = itr.next();
				%>
				<tr>
					<td class="txtlabel alignRight">Option <%=count + 1%></td>
					<td align="right">
					<%-- <input type="text" name="strDate"
						value="<%=uF.showData(hmDates.get(id), "")%>" style="width: 100px;">
					 --%>
					<%=uF.showData(hmDates.get(id), "Not added")%> </td>
					<td>
					<%-- <input type="text" name="strTime" style="width: 100px;"
						value="<%=uF.showData(hmTime.get(id), "")%>">
					 --%>
					 <%=uF.showData(hmTime.get(id), "Not given")%></td>

					<td align="right"><input type="radio" name="selectionDate"
						value="<%=id%>" <%if (hmSelectedDate.get(id) != null) {%>
						checked="checked" <%}%>></td>
				</tr>


				<%count++;
						}
				%>
				

	           <%if(hmDates.keySet().size()==0){ %>
                <tr><td colspan="3"> No Further Dates Added by Candidate </td></tr>
                 
                 <%}else{ %>
				<%-- <tr>
					<td class="txtlabel alignRight" colspan="3">Specify other Dates</td>
					<td  style="float: left; padding: 30px;"><input
						type="radio" name="selectionDate" value="0">
					</td>
				</tr>

				<tr>
					<td colspan="3">
					<s:submit cssClass="input_button"
							name="acceptDate" value="Send Confirmation">
							</s:submit>
					</td>
				</tr> --%> 
				<%
				}
				%>


			</table>
			
			
			<%if(hmDates.keySet().size()==0){ %>
                
                 <%}else{ %>
				<div>
					<span>Specify other Dates<input type="radio" name="selectionDate" value="0" onclick="openOtherdate();"></span><br/>
					<span>
			<div id="otherDateDiv" style="display: none;">
				
				<script type="text/javascript">
					$(function() {
						$("input[name=interviewdate]").datepicker({dateFormat: 'dd/mm/yy'});
						$( "input[name=interviewTime]" ).timepicker({});
					});
				</script>
				
				<table style="width:320px" class="tb_style">
					<tr>
						<td class="label">Date: 
						<input type="text" name="interviewdate" id="interviewdate" style="width: 100px"/>
						</td>

						<td class="label">Time: <input type="text" name="interviewTime" id="interviewTime" style="width: 100px;" />
						</td>
					</tr>
				</table>
			</div>
					<input type="submit" class="input_button" value="Send Confirmation" name="acceptDate" id="CandidateInterviewSchedule_acceptDate">
					</span>
				</div>
				<%
				}
				%>
			</div>

               
        
		</s:form>
	</div>
	
	
         <%if(hmRejectedDate.keySet().size()>0){ %>
     	<div id="rejectedDates" style=" float:left;width: 175px; margin-top: 51px;padding-left:70px;">
     	
	<table class=" tb_style">
			<thead>
				<tr>
					<th>S.No</th>
					<th>Rejected Dates</th>
				</tr>
			</thead>
			<tbody>
				<%int count=0;
			Iterator<String> itr = hmRejectedDate.keySet().iterator();
			while (itr.hasNext()) {
				String id = itr.next();
				%>
				<tr>
					<td><%=count+1 %></td>
					<td><%=uF.showData(hmRejectedDate.get(id),"") %></td>
				</tr>

				<%count++;} %>

			</tbody>

		</table>
     	
     	</div>
      <%} %>
   
   
   <%if(hmSelectedDate.keySet().size()>0){ %>
     	<div id="selectedDates" style=" float:left;width: 175px; margin-top: 51px;padding-left:70px;">
     	
	<table class="display tb_style">
			<thead>
				<tr>
					<th>S.No</th>
					<th>Selected Dates</th>
				</tr>
			</thead>
			<tbody>
				<%int count=0;
			Iterator<String> itr = hmSelectedDate.keySet().iterator();
			while (itr.hasNext()) {
				String id = itr.next();
				%>
				<tr>
					<td><%=count+1 %></td>
					<td><%=uF.showData(hmSelectedDate.get(id),"") %></td>
				</tr>

				<%count++;} %>

			</tbody>

		</table>
     	
     	</div>
   
		<%} %>

