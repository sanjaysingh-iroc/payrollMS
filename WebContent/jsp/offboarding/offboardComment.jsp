<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@page import="java.util.*"%>
<script>
function sendComment(){
	var comment = document.getElementById("commentArea").value;
	var docid = document.getElementById("docid").value;
	var userId = document.getElementById("userId").value;
	
	//alert("docid ===>> " + docid + "userId ===>> " + userId);
	var xhr = $.ajax({ 
		url : "DeleteOffboardDocument.action?docid="+docid+"&userId="+userId+"&operation=B&comment=" + comment,
		cache: false,
		
	success: function(data){
		$("#comment").dialog('close');
		}
	});	
	
	
}
</script>
</head>
<body>
	<div class="tmln_holder" style="width: 100%;">

		<div style="float: left; border: solid 0px #ccc; width: 93%; margin: 0px 0px 0px 10px">
			<div class="lholder">
				<%
				UtilityFunctions uF = new UtilityFunctions();
				String status = (String) request.getAttribute("status");
				List<List<String>> outerList=(List<List<String>>)request.getAttribute("outerList");
				String currentComment = (String) request.getAttribute("currentComment");
             for(List<String> a:outerList) {
             %>
				<div class="lblock" style="float: none;">
					<div class="lp"></div>
					<div class="tm_container"
						style="height: 40px; width: 55%; min-height: 100px;">
						<div>
							<%=a.get(0) %>
						</div>
						<div style="font-size: 9px; background-color: #EFEFEF;" align="center">
							<b><%=a.get(2) %></b>
						</div>
					</div>
				</div>
				<div class="clr"></div>
				<% } %>


			</div>
			<div class="rholder">

				<%List<List<String>> outerList1=(List<List<String>>)request.getAttribute("outerList1");
             for(List<String> a:outerList1) {
             %>
				<div class="rblock" style="float: none;">
					<div class="lp_r"></div>
					<div class="tm_container_r" style="height: 40px; width: 55%; min-height: 100px;">
						<div>
							<%=a.get(0) %>
						</div>
						<div style="font-size: 9px; background-color: #EFEFEF;"
							align="center">
							<b> <%=a.get(2) %></b>
						</div>
					</div>
				</div>
				<div class="clr"></div>
				<% } %>



			</div>
		</div>

	</div>

	<div class="clr"></div>

	<% if(status == null || !uF.parseToBoolean(status)) { %>
		<div style="float: left; width: 100%; margin-top: 50px;">
			<s:form action="DeleteOffboardDocument.action" theme="simple">
				<s:hidden name="opration" value="B" />
				<s:hidden name="docid" id="docid"/>
				<s:hidden name="userId" id="userId"/>
				
				<table cellpadding="0" cellspacing="0" width="90%" align="center">
					<tr><td align="right"><h3>Add Or Update Comment: </h3> </td>
						<td valign="top"><textarea style="margin-left: 5px;" rows="3" cols="50" id="commentArea"><%=uF.showData(currentComment, "") %> </textarea></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><input type="button" class="input_button" value="Save Comment" onclick="sendComment()" />
						</td>
					</tr>
				</table>
			</s:form>
		</div>
	<% } %>
	
</body>
</html>