<div id="divResult">

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>

<style type="text/css">
	#textlabel{
	white-space:pre-line;
	}	
</style>
<%
	UtilityFunctions uF = new UtilityFunctions();
	List<String> alInboxData = (List<String>) request.getAttribute("alInboxData");
	if(alInboxData == null) alInboxData = new ArrayList<String>();
%>
<%-- <section class="content"> --%>
	<div class="row jscroll">
		<div class="col-md-12">
			<div class="box box-primary">
				<div class="box-body">	<!-- style="padding: 5px; overflow-y: auto; min-height: 500px;" -->
					<% if(alInboxData.size() > 0){ %>
						<div style="width: 100%"><h4><b><%=alInboxData.get(5) %></b></h4></div>
						<table border="0" class="table table_no_border">
							<tr>
								<td><img height="25" width="25" class="lazy img-circle zoom" src="userImages/avatar_photo.png" />&nbsp;&nbsp;<b><%=alInboxData.get(3) %></b></td>
								<%-- <td class="txtlabel alignRight"><%=alInboxData.get(7) %></td> --%>
								<td class="txtlabel alignRight"><div><%=alInboxData.get(7) %><div style="float:right; width:50%"><%=alInboxData.get(2) %></div></td>
							</tr>
							<tr>
								<td colspan="2"><span id="textlabel"><%=alInboxData.get(6) %></span></td>
							</tr>
						</table>
					<% } %>
				</div>
			</div>
		</div>
	</div>
<%-- </section> --%>

</div>