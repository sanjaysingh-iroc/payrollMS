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

	UtilityFunctions uF = new UtilityFunctions();

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
		<s:hidden name="appFreqId"></s:hidden>
					<table class="table table-striped table-bordered">
						<tr>
							<th width="30%">Question</th>
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
						
					</table>
	</s:form>
</div>

