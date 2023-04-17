<%@page import="javax.swing.Icon"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Target" name="title" />
</jsp:include>
<%
/* Map<String, String> hmEmpList = (Map<String, String>) request.getAttribute("hmEmpList"); */
Map<String, String> hmMesures = (Map<String, String>) request.getAttribute("hmMesures");
Map<String, String> hmMesuresType = (Map<String, String>) request.getAttribute("hmMesuresType");

Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");

/* Map<String, List<List<String>>> hmGoalTitle = (Map<String, List<List<String>>>) request.getAttribute("hmGoalTitle"); */

Map<String, List<List<String>>> hmKRA =(Map<String, List<List<String>>>)request.getAttribute("hmKRA");

List<String> memberList = (List<String>) request.getAttribute("memberList");
Map<String, String> orientationMemberMp = (Map<String, String>) request.getAttribute("orientationMemberMp");

String strUserTypeId = (String) session.getAttribute(IConstants.USERTYPEID);
String id=request.getParameter("id");
String empid=request.getParameter("empid");

Map<String, String> hmTarget =(Map<String, String>)request.getAttribute("hmTarget");

String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);

UtilityFunctions uf = new UtilityFunctions();
%>

<div class="leftbox reportWidth">
<%-- <s:hidden name="id"></s:hidden>
<s:hidden name="empid"></s:hidden> --%>
<%
			if(!hmKRA.isEmpty()){
			%>
	<s:form action="Target" id="formID" method="POST" theme="simple">
	<s:hidden name="id"></s:hidden>
	<s:hidden name="empid"></s:hidden>

		<table class="tb_style" style="float: left; width: 100%;">
			<tr>
				<th style="width:60%;">Target</th>
				<th>Measures</th>
				<%
					for (int i = 0; i < memberList.size(); i++) {
				%>
				<th><%=orientationMemberMp.get(memberList.get(i))%></th>
				<%
					}
				%>
			</tr>
			<%
			Iterator<String> it = hmKRA.keySet().iterator();
			int count=0;
			while(it.hasNext()){
				String key = it.next();
				List<List<String>> outerList = hmKRA.get(key);
				for(int i=0;outerList!=null && i<outerList.size();i++){
					List<String> innerList=outerList.get(i);
			%>
			<tr>
				<td>
				<input type="hidden" name="goalid" value="<%=innerList.get(1)%>" />  
				<input type="hidden" name="goalkraid" value="<%=innerList.get(0)%>" /> 
				<input type="hidden" name="weightage" value="<%=innerList.get(9)%>" /> 
				<span style="font-size: 12px;"><%=innerList.get(7)%></span>&nbsp;
				</td>
				<td nowrap="nowrap" align="right">
				<input type="hidden"name="measures" value="<%=hmMesuresType.get(innerList.get(1))%>" />
				<input type="hidden" name="measuresValue"value="<%=hmMesures.get(innerList.get(1))%>" /> 
				<%=hmMesures.get(innerList.get(1))%>
				</td>
				
				<%
					for (int j = 0; j < memberList.size(); j++) {
						boolean flag=false;
						String usertypeid=null;
						if (strUserTypeId != null && strUserTypeId.equals(memberList.get(j))) {
							flag=true;
							usertypeid= strUserTypeId;
						}
						//System.out.println("flag====>"+flag);
						
						String targetdata=hmTarget.get(innerList.get(1)+innerList.get(0)+empid+id+strSessionEmpId+memberList.get(j));
						//System.out.println("selfKraData===>"+selfKraData);
						String target_id=null;
						String amt_percentage=null;
						if(targetdata!=null && !targetdata.equals("")){
							String[] tempselfKraData=targetdata.split(":_:");
							target_id=tempselfKraData[0];
							amt_percentage=tempselfKraData[1];
						}
						
						
				%>				
				<td align="right">
				<%if(flag==true){ %>
				<input type="hidden" name="usertype" value="<%=usertypeid%>" />
				<input type="hidden" name="target_id" value="<%=target_id%>" />
				<%} %>
				
				 <div <%if(flag==false){ %> style="opacity: 0.4; filter: alpha(opacity = 40);" <%} %>>
				<%-- <input type="hidden" name="<%=orientationMemberMp.get(memberList.get(j))%>" value="<%=memberList.get(j)%>" /> --%>
				<input type="text" name="amount<%=memberList.get(j)%>" value="<%=amt_percentage != null ? amt_percentage : ""%>" <%if(flag==false){ %> style="width: 75px;pointer-events: none;" readonly="readonly"<%}else{ %>style="width: 75px;"<%} %>/>
					  <select name="amtPercentage<%=memberList.get(j)%>"
						style="width: 100px;" <%if(flag==false){ %>disabled="disabled"<%} %>> 
							<option value="a">Amount</option>
							<option value="p">Percentage</option>
							<%
								if (hmMesuresType.get(innerList.get(1)) != null && hmMesuresType.get(innerList.get(1)).equals("Effort")) {
							%>
							<option value="d" selected="selected">Days & Hrs</option>
							<%
								} else {
							%>
							<option value="d">Days & Hrs</option>
							<%
								}
							%>
					</select>
											        </div>
				
				</td>
				<%
				count++;} %>				
			</tr>
			<%
			}
				}%>
		</table>

		<div>
			<s:submit value="Submit" cssClass="input_button" name="submit"></s:submit>

		</div>		
	</s:form>
	<%
			}else{
		%>
		<div class="nodata msg">No KRA assigned to you</div>
		<%
			}
		%>
</div>
