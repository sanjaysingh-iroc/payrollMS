<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<%String from = (String)request.getAttribute("from");
String fromPage = (String)request.getAttribute("fromPage");

//===start parvez date: 18-01-2022===
	String dataType = request.getParameter("dataType");
System.out.println("CJ.jsp/13--dataType="+dataType);
//===end parvez date: 18-01-2022===

%>
<script>
	jQuery(document).ready(function() {
		// binds form submission and fields to the validation engine
		/* jQuery("#formID").validationEngine(); */
	});
	
		$("#formID").submit(function(event){
			event.preventDefault();
			var fromPage = '<%=from%>';
			var form_data = $("#formID").serialize();
			$.ajax({
				type :'POST',
				url  :'CloseJob.action',
				data :form_data,
				cache:true/* ,
				success : function(result) {
					if(fromPage != "" && fromPage == "WF") {
						$("#divWFResult").html(result);
					} else {
						$("#divResult").html(result);
					}
				} */
			});
			
			if(fromPage != "" && fromPage == "WF") {
				$.ajax({
					url: 'RecruitmentDashboard.action?fromPage='+fromPage,
					cache: true,
					success: function(result){
						$("#divWFResult").html(result);
			   		}
				});
			} else {
				$.ajax({
					url: 'RecruitmentDashboardData.action',
					cache: true,
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
			}
		});
	
</script>

<%String closeReason = (String) request.getAttribute("closeReason");
%>
<div id="closeJobDiv">
<% if(closeReason != null && !closeReason.equals("")) { %>
	<table border="0" class="formcss" style="width: 97%; float: left">
			<tr>
				<td colspan=2><s:fielderror/>
				</td>
			</tr>
			<tr>
				<td class="txtlabel alignRight" style="width: 50px;" valign="top">Reason:</td>
				<td><%=(closeReason==null ? "" : closeReason)%></td>
			</tr>
		</table>
	<% } else { %>
	
	<s:form id="formID" name="frmCloseJob" theme="simple" action="CloseJob" method="POST" cssClass="formcss" enctype="multipart/form-data">

		<s:hidden name="recruitmentId"></s:hidden> 
		<s:hidden name="orgID"></s:hidden> 
       	<s:hidden name="wlocID"></s:hidden>
       	<s:hidden name="desigID"></s:hidden>
       	<s:hidden name="checkStatus"></s:hidden>
       	<s:hidden name="fdate"></s:hidden>
       	<s:hidden name="tdate"></s:hidden>
       	<s:hidden name="fromPage"></s:hidden>
       	<s:hidden name="operation" value="update"></s:hidden>
   <!-- ===start parvez date: 18-01-2022=== -->    	
       	<s:hidden name="dataType"></s:hidden>
   <!-- ===end parvez date: 18-01-2022=== -->    	
        <input type="hidden" name="from" id="from" value="<%=from%>" />
        
		<table border="0" class="table" style="width: 97%; float: left">
			<s:fielderror />

			<tr>
				<td class="txtlabel alignRight" valign="top">Reason:</td>
		<!-- ===start parvez date: 18-01-2022=== -->		
				<%-- <td><textarea name="closeReason" id="closeReason" cols="26" rows="4"><%=(closeReason==null ? "" : closeReason)%></textarea></td> --%>
				
				<% if(dataType != null && dataType.equals("reopen")){ %>
					<td><textarea name="reopenReason" id="reopenReason" cols="26" rows="4"></textarea></td>
				<% } else{ %>
					<td><textarea name="closeReason" id="closeReason" cols="26" rows="4"><%=(closeReason==null ? "" : closeReason)%></textarea></td>
				<% } %>	
		<!-- ===end parvez date: 18-01-2022=== -->		
			</tr>
			<tr>
				<td colspan="2" align="center">
					 <s:submit cssClass="btn btn-primary" value="Submit" align="center" />
				</td>
			</tr>
		</table>
	</s:form>
	<% } %>
</div>



