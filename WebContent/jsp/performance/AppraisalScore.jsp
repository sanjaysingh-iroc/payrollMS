<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<%-- 
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Staff Appraisal Report" name="title" />
</jsp:include> --%>
<%
	/* List<List<String>> levelRemark = (List<List<String>>) request
			.getAttribute("levelRemark"); */

	/* String oriented_type = (String) request
			.getAttribute("oriented_type"); */
	UtilityFunctions uF = new UtilityFunctions();

	/* CommonFunctions cf = new CommonFunctions(); */
	/* Map<String, String> empMap = (Map<String, String>) request
			.getAttribute("appraisalMp");
	Map<String, String> hmEmpName = cf.getEmpNameMap(null, null);
	Map<String, String> hmEmpCode = cf.getEmpCodeMap();
	Map<String, String> hmEmpCodeDesig = cf.getEmpDesigMap();

	String empid = (String) request.getAttribute("empid");
	Map<String, String> hmAppLevelName = (Map<String, String>) request
			.getAttribute("hmAppLevelName"); */  
	
	
	
/* 	Map<String, String> hmScoreName = (Map<String, String>)request.getAttribute("hmScoreName");
	Map<String, String> hmQuestion = (Map<String,String>)request.getAttribute("hmQuestion"); */
	List<String> memberList=(List<String> )request.getAttribute("memberList");
	Map<String,String> orientationMemberMp=(Map<String,String> )request.getAttribute("orientationMemberMp");
	Map<String,Map<String,Map<String,String>>> questionMp=(Map<String,Map<String,Map<String,String>>>)request.getAttribute("questionMp");
	Map<String, String> questionDetailsMp =(Map<String, String> )request.getAttribute("questionDetailsMp");
%>
<%
	String strSessionUserType = (String) session
			.getAttribute(IConstants.USERTYPE);
%>
<div class="leftbox reportWidth">

	<s:form action="#" id="formID" method="POST" theme="simple">
		<!-- <div class="holder"> -->
		
					<%-- <table class="tb_style" width="100%">
						<tr>
							<th style="width: 20%;" align="right">Name of the Appraisal</th>
							<td valign="top"><%=empMap.get("APPRAISAL")%></td>
						</tr>
						<tr>
							<th align="right">Orientation</th>
							<td valign="top"><%=empMap.get("ORIENT")%>&deg;</td>
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
							<td valign="top"><%=empMap.get("FROM")%></td>
						</tr>
						<tr>
							<th align="right">To</th>
							<td valign="top"><%=empMap.get("TO")%></td>
						</tr>
					</table>		 --%>
					<table class="tb_style" width="100%">
						<tr>
							<th width="20%">Question</th>
							<%for(int i=0;i<memberList.size();i++){ %>
							<th><%=orientationMemberMp.get(memberList.get(i)) %></th>
							<%} %>
										
						</tr>
						<%
						Set<String> keys=questionMp.keySet();
						Iterator<String> it=keys.iterator();
						while(it.hasNext()){
							String key=it.next();%>
							
							
							<tr>
							<td><%=questionDetailsMp.get(key)%></td>
							<%Map<String,Map<String,String>> userType=questionMp.get(key);
							if(userType==null)userType=new HashMap<String,Map<String,String>>();
							
							%>
							<%for(int i=0;i<memberList.size();i++){ 
							
								Map<String,String> innerList=userType.get(memberList.get(i));
								if(innerList ==null)innerList= new HashMap<String,String>();
							%>
							<td><%=uF.showData(innerList.get("MARKS"),"0") %>/<%=uF.showData(innerList.get("WEIGHTAGE"),"0") %></td>
							<%} %>
							</tr>
							
							
							
						<%}%>
						
						
						<%-- for(int i=0;levelRemark!=null && i<levelRemark.size();i++){
							List<String> innerList=levelRemark.get(i);
							
						%>						
						<tr>
							<td><%=uF.showData(hmQuestion.get(innerList.get(1)),"") %></td>
							<%for(int j=0;j<memberList.size();j++){ %>
							
							<td align="center"><%=uF.showData(value.get(memberList.get(j)),"0")%></td>
							<%} %>
						</tr>
						<%} %> --%>
					</table>
	</s:form>
</div>

