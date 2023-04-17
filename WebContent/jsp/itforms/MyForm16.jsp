<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title" />
</jsp:include> --%>

	<!-- <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;"> -->
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th>Financial Year</th>
					<th>Approved By</th>
					<th>Approved Date</th>
					<th>Download</th>
				</tr>
			</thead>
	
			<tbody>
				<%
					List<Map<String, String>> alForm16 = (List<Map<String, String>>)request.getAttribute("alForm16");
					if(alForm16 == null) alForm16 = new ArrayList<Map<String,String>>();
					
					int nForm16Size = alForm16.size();
					for (int i = 0; i < nForm16Size; i++) {
						Map<String, String> hmEmpForm16 = alForm16.get(i);
				%>
					<tr>
						<td><%=uF.showData(hmEmpForm16.get("FINANCIAL_YEAR_START"),"") %> - <%=uF.showData(hmEmpForm16.get("FINANCIAL_YEAR_END"),"") %></td>
						<td><%=uF.showData(hmEmpForm16.get("APPROVED_BY"),"") %></td>
						<td><%=uF.showData(hmEmpForm16.get("APPROVED_DATE"),"") %></td>
						<td>
							<a target="blank" class="fa fa-file-pdf-o" href="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_DOCUMENT+"/"+IConstants.I_FORM16+"/"+hmEmpForm16.get("EMP_ID")+"/"+hmEmpForm16.get("FORM16_NAME")%>" title="Download Form16"></a>
						</td>
					</tr>
				<% } if(nForm16Size==0) { %>
					<tr>
						<td colspan="4" class="alignLeft"><div class="nodata msg" style="width: 96%;">No form 16 available.</div></td>
					</tr>
				<% } %>
			</tbody>
		</table>
	<!-- </div> -->
