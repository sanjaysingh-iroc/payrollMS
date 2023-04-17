<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.List"%>

<script type="text/javascript">

function showCommentbox(id){
	//alert("showCommentbox : "+ id);
		var cmtboxstatus = document.getElementById("commentboxstatus"+id).value;
		if(cmtboxstatus == 'O'){
			document.getElementById("commentboxstatus"+id).value = 'C';
			document.getElementById("quecommentdiv"+id).style.display='block';
		}else{
			document.getElementById("quecommentdiv"+id).style.display='none';
			document.getElementById("commentboxstatus"+id).value = 'O';
		}
	}
</script>
<%String fromPage = (String) request.getAttribute("fromPage");
String appid = (String) request.getAttribute("appid");
String appFreqId = (String) request.getAttribute("appFreqId");%>
<s:form action="addAllSingleOpenWithoutMarkQueStatus" name="formAllSingleOpenWithoutMarkQueStatus" id="formAllSingleOpenWithoutMarkQueStatus" method="POST" theme="simple">
	<s:hidden name="appid" id="appid"></s:hidden>
	<s:hidden name="empId" id="empId"></s:hidden>
	<s:hidden name="usertypeId" id="usertypeId"></s:hidden>
	<s:hidden name="appFreqId" id="appFreqId"></s:hidden>
	<s:hidden name="fromPage" id="fromPage"></s:hidden>
	<div> 
		<table class="table" width="100%">
			<%
			List<List<String>> queDetailsList = (List<List<String>>) request.getAttribute("queDetailsList");
			//System.out.println("queDetailsList "+queDetailsList);
			String readstatus ="R";
			for (int i = 0; queDetailsList != null && !queDetailsList.isEmpty() && i < queDetailsList.size(); i++) {
				List<String> queInnerList = queDetailsList.get(i);
				readstatus = queInnerList.get(5);
				System.out.println("queInnerList " + queInnerList);
		%>
				<tr>
					<th>Q<%=i+1%> </th>
					<td><input type="hidden" name="queid" id="queid" value="<%=queInnerList.get(2) %>"><%=queInnerList.get(0)%></td>
				</tr>
				<tr>
					<th>Ans:</th>
					 <td><%=queInnerList.get(1)%></td>
				</tr>
				<tr>
					<th>Comment:</th>
					<td><%=queInnerList.get(6)%></td>
				</tr>
				<tr>
					<th width="16%">
						<%if(queInnerList.get(3) != null && queInnerList.get(3).equals("1")) { %>
							<input type="hidden" name="status<%=queInnerList.get(2) %>" id="status<%=queInnerList.get(2) %>" value="1">
							<input type="checkbox" name="showstatus<%=queInnerList.get(2) %>" id="showstatus<%=queInnerList.get(2) %>" checked="checked" disabled="disabled">
						<%}else { %>
							<input type="checkbox" name="status<%=queInnerList.get(2) %>" id="status<%=queInnerList.get(2) %>">
							<a href="javascript: void(0)" onclick="showCommentbox('<%=queInnerList.get(2) %>');" title="Click here for comment">
							<img src="images1/pen.png" style="width:10px;height:10px;"title="Add Remark"></a>
							<input type="hidden" name="commentboxstatus<%=queInnerList.get(2) %>" id="commentboxstatus<%=queInnerList.get(2) %>" value="O">
						<%} %>
					 </th>
					<td>
						<% if(queInnerList.get(3) != null && queInnerList.get(3).equals("1")) { %>
							<input type="hidden" name="commenttext<%=queInnerList.get(2) %>" id="commenttext<%=queInnerList.get(2) %>" value="<%=queInnerList.get(4)%>">
							<div>Remark : <%=queInnerList.get(4) %></div>
						<%}else { %>
							<div id="quecommentdiv<%=queInnerList.get(2) %>" style="display: none">
								<span style="float: left; margin-right: 5px;">Remark :</span> 
								<textarea name="commenttext<%=queInnerList.get(2) %>" id="commenttext<%=queInnerList.get(2) %>" rows="2" style="width: 540px;"></textarea> 
							</div>
						<%} %>
					</td>
				</tr>
		   <%}%>
		</table>
		<% if(queDetailsList == null || queDetailsList.isEmpty()){%>
				<div style="float:left;width:94%" class="tdDashLabel">No Reviews Available</div>
		<% }%>
		
		<% if(queDetailsList != null && !readstatus.equals("R")){%>
			<div>
				<%-- <%if(fromPage != null && fromPage.equals("AD")) { %>
					<input type="button" value="Save" class="btn btn-primary" name="submit" onclick="submitAnswer('<%=appid %>','<%=appFreqId%>','<%=fromPage%>')" />
				<%}else { %> --%>
					<s:submit value="Save" cssClass="btn btn-primary" name="submit" />
				<%-- <%} %> --%>
				<input type="button" value="Cancel" class="btn btn-danger" name="cancel" onclick="closePopup();" />
			</div>
		<%}%>
   </div>
</s:form>


<script>
	$("#formAllSingleOpenWithoutMarkQueStatus").submit(function(event) {
		<% if(fromPage != null && fromPage.equals("AD")) { %>
			event.preventDefault();
			var form_data = $("#formAllSingleOpenWithoutMarkQueStatus").serialize();
			var fromPage = '<%=fromPage%>';
			var appId = '<%=appid%>';
			var appFreqId = '<%=appFreqId%>';
//			alert("form_data ===>> " + form_data);
			$.ajax({ 
				type : 'POST',
				url: "addAllSingleOpenWithoutMarkQueStatus.action",
				data: form_data+"&submit=Save",
				success: function(result){
					if(fromPage != "" && fromPage == "LD") {
					} else {
						getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
					}
				},
				error: function(result){
					if(fromPage != "" && fromPage == "LD") {
					} else {
						getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
					}
				}
			});
		<% } %>
		
		/* var form_data = $("#formID").serialize();
		var submitBtn = $('input[name = "submit"]').val();
		$.ajax({
			type:'POST',
			url:'addAllSingleOpenWithoutMarkQueStatus.action',
			data:form_data+"&submit="+submit,
			success:function(result){
				$("#divLPDetailsResult").html(result);
			}
		}); */
	});

 <%-- function submitAnswer(appId,appFreqId,fromPage) {
	var form_data = $("#formID").serialize();
	var from = '<%=fromPage%>';
	//alert("form_data ===>> " + form_data);
	$.ajax({ 
		type : 'POST',
		url: "addAllSingleOpenWithoutMarkQueStatus.action",
		data: form_data+"&submit=Save",
		success: function(result){
			if(from != "" && from == "LD"){
			}else {
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
			}
		},
		error: function(result){
			if(from != "" && from == "LD"){
			}else {
				getReviewStatus('AppraisalStatus',appId,appFreqId,fromPage);
			}
		}
	});
} --%>
		
</script>
