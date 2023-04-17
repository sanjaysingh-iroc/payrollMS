<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Staff Appraisal Report" name="title" />
</jsp:include>
<%
	String oriented_type = (String) request.getAttribute("oriented_type");
	UtilityFunctions uF = new UtilityFunctions();

	Map<String, String> empMap = (Map<String, String>) request.getAttribute("appraisalMp");
	
	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
	Map<String, String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");

	String empid = (String) request.getAttribute("empid");
	Map<String,String> hmAppLevelName=(Map<String,String>)request.getAttribute("hmAppLevelName");
	List<String> memberList=(List<String> )request.getAttribute("memberList");
	Map<String,String> orientationMemberMp=(Map<String,String> )request.getAttribute("orientationMemberMp");
%>
<% String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);%>
<div class="leftbox reportWidth">

	<s:form action="#" id="formID" method="POST" theme="simple">
		<!-- <div class="holder"> -->
		<table class="tb_style" width="100%">
			<tr>
				<td>
					<table class="tb_style" width="100%">
						<tr>
							<th style="width: 20%;" align="right">Name of the Appraisal</th>
							<td valign="top"><%=empMap.get("APPRAISAL")%></td>
						</tr>
						<tr>
							<th align="right">Orientation</th>
							<td valign="top"><%=empMap.get("ORIENT")%></td>
						</tr>
						<tr>
							<th align="right">Name of Employee</th>
							<td valign="top"><%=hmEmpName.get(empid)%></td>
						</tr>
						<tr>
							<th align="right">Frequency</th>
							<td valign="top"><%=empMap.get("FREQUENCY")%></td>
						</tr>
						<tr>
							<th align="right">From</th>
							<td valign="top"><%=empMap.get("FREQ_START_DATE")%></td>
						</tr>
						<tr>
							<th align="right">To</th>
							<td valign="top"><%=empMap.get("FREQ_END_DATE")%></td>
						</tr>
					</table>
				</td>
			</tr>
			<!-- 		</div>
 -->
			<tr>
				<td>
					<table class="tb_style" width="100%">
						<tr>
							<th width="20%">Level</th>
							<%for(int i=0;i<memberList.size();i++){ %>
							<th><%=orientationMemberMp.get(memberList.get(i)) %></th>
							<%} %>
							
							<%-- <%if(uF.parseToInt(oriented_type)>=180){ %>
							<th>Manager</th>
							<%} %>
							
							<%if(uF.parseToInt(oriented_type)>=270){ %>
							<th>Peer</th>
							<%} %>
							<th>Self</th> --%>
						</tr>
						<%
				
						Map<String,Map<String,String>> outerMp=(Map<String,Map<String,String>> )request.getAttribute("outerMp");
						Map<String, String> hmUserTypeID =(Map<String, String>)request.getAttribute("hmUserTypeID");
						
						Set<String> keys=hmAppLevelName.keySet();
						Iterator<String> it=keys.iterator();
						while(it.hasNext()){
							String key=it.next();
							Map<String,String> value=outerMp.get(key);
							 if(value==null)value=new HashMap<String,String>();
							%>	
							<tr>
								<td valign="top" align="center"><%=hmAppLevelName.get(key) %></td>
								
								<%for(int j=0;j<memberList.size();j++){
									
									%>
										
							<td align="center"><%=uF.showData(value.get(memberList.get(j)),"0")%></td>
							<%} %>
							
							
								<%-- <td align="center"><%=uF.showData(value.get(hmUserTypeID.get(IConstants.HRMANAGER)),"0")%></td>
								<%if(uF.parseToInt(empMap.get("ORIENT"))>=180){ %>
								<td align="center"><%=uF.showData(value.get(hmUserTypeID.get(IConstants.MANAGER)),"0")%></td>
								<%} %>					
								<td align="center"><%=uF.showData(value.get(hmUserTypeID.get(IConstants.EMPLOYEE)),"0")%></td>
								<%if(uF.parseToInt(empMap.get("ORIENT"))>=270){ %>
								<td align="center"><%=uF.showData(value.get(hmUserTypeID.get(IConstants.EMPLOYEE)),"0")%></td> 
								<%} %>
								--%>
							</tr>			
							<%-- <%}
					for (int i = 0; levelRemark != null && i < levelRemark.size(); i++) {
						List<String> innerList=levelRemark.get(i);
						selfTotal+=uF.parseToDouble(innerList.get(4));
						managerTotal+=uF.parseToDouble(innerList.get(2));
						hrTotal+=uF.parseToDouble(innerList.get(1));
						peerTotal+=uF.parseToDouble(innerList.get(3));
				%>	
				<tr>
					<td valign="top" align="center"><%=innerList.get(0) %></td>
					<td align="center"><%=innerList.get(4)%></td>
					<%if(uF.parseToInt(empMap.get("ORIENT"))>=180){ %>
					<td align="center"><%=innerList.get(2)%></td>
					<%} %>					
					<td align="center"><%=innerList.get(1)%></td>
					<%if(uF.parseToInt(empMap.get("ORIENT"))>=270){ %>
					<td align="center"><%=innerList.get(3)%></td>
					<%} %>
					
				</tr>		 --%>	
				<%}
				
				%>
				<%-- <tr>
					<th width="20%"><span style="float: right">Total</span></th>
					
					<%for(int j=0;j<memberList.size();j++){
									
									
									%>
										
							<td align="center"><%=uF.showData(totalMp.get(memberList.get(j)),"0")%></td>
							<%} %> --%>
					
					
					<!-- 	</tr> -->
					</table></td>
			</tr>
			<% if(strSessionUserType!=null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.ADMIN))){ %>
			<tr>
				<td>
					<table class="tb_style" width="100%">
						<tr>
							<th width="20%">Final Remark</th>
							<td colspan="5"><textarea name="finalRemark"
									id="finalRemark" rows="4" cols="125"></textarea>
							</td>
						</tr>
						<tr>
							<th width="20%"></th>
							<td colspan="5"><s:submit  cssClass="btn btn-primary" value="Submit"></s:submit></td>
						</tr>
					</table></td>
			</tr>
			<%} %>
		</table>
	</s:form>
</div>

