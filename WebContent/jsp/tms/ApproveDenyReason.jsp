<%@ taglib uri="/struts-tags" prefix="s"%>


<%
	String strServiceId=request.getParameter("SID");
	String strEmpId=request.getParameter("EID");
	String strDate=request.getParameter("DATE");
	String exceptionMode = (String) request.getAttribute("exceptionMode");
%>



<script>

$(function () {
	<% if(exceptionMode!=null && exceptionMode.equals("IN")) { %>
    	$("input[name='strStartTime']").datetimepicker({format: 'HH:mm'});
    <% } else if(exceptionMode!=null && exceptionMode.equals("OUT")) { %>
    	$("input[name='strEndTime']").datetimepicker({format: 'HH:mm'});
    <% } else if(exceptionMode!=null && exceptionMode.equals("IN_OUT")) { %>
   		$("input[name='strStartTime']").datetimepicker({format: 'HH:mm'});
    	$("input[name='strEndTime']").datetimepicker({format: 'HH:mm'});
    <% } %>
});


<%-- function validateReason(frmAddReason){
	alert("aasjkdg");
	if(frmAddReason.strReason.value==""){
		alert('Please enter the valid reason');
		//return false;
	}else{
	
		var EMPID='<%=request.getAttribute("EMPID") %>';
		var DT='<%=request.getAttribute("DT") %>';
		var SID='<%=request.getAttribute("SID") %>';
		var S='<%=request.getAttribute("S") %>';
		var AST='<%=request.getAttribute("AST") %>';
		var AET='<%=request.getAttribute("AET") %>';
		var divid='<%=request.getAttribute("divid") %>';
		
		var action='UpdateException.action?S='+S+'&SID='+SID+'&EMPID='+EMPID+'&DT='+DT+'&AST='+AST+'&AET='+AET+'&strReason='+frmAddReason.strReason.value;
		alert(action);
		//getContent(divid,action);
	}
	//return true;
} --%>

</script>

<% String exceptionType = (String) request.getAttribute("exceptionType"); 
//===start parvez date: 31-01-2022===
	String empReason = (String)request.getAttribute("employeeReason");
	if(empReason != null){
		empReason = empReason.replaceAll("'","");
	}
	//System.out.println("ADR.jsp/57--empReason : "+empReason);
//===end parvez date: 31-01-2022===
%>

<s:form action="UpdateException" name="frmAddReason" theme="simple" method="POST">
	<s:hidden name="EMPID"></s:hidden>
	<s:hidden name="DT"></s:hidden>
	<s:hidden name="SID"></s:hidden>
	<s:hidden name="S"></s:hidden>
	<s:hidden name="AST"></s:hidden>
	<s:hidden name="AET"></s:hidden>
	<s:hidden name="divid"></s:hidden>
	<s:hidden name="exceptionType"></s:hidden>
	<s:hidden name="exceptionMode"></s:hidden>
	<s:hidden name="employeeReason"></s:hidden><!-- Created By Dattatray date:12-11-21  -->
	<table class="table table_no_border">
		<% if(exceptionMode!=null && exceptionMode.equals("IN")) { %>
			<tr>
				<td align="right">Start Time:</td>
				<td nowrap="nowrap">
					<s:textfield name="strStartTime" id="strStartTime" cssStyle="width:65px !important;"/>
					<s:hidden name="strEndTime" id="strEndTime"></s:hidden>
				</td>
			</tr>
		<% } else if(exceptionMode!=null && exceptionMode.equals("OUT")) { %>	
			<tr>
				<td align="right">End Time:</td>
				<td>
					<s:textfield name="strEndTime" id="strEndTime" cssStyle="width:65px !important;"/>
					<s:hidden name="strStartTime" id="strStartTime"></s:hidden>
				</td>
			</tr>
	<!-- Started By Dattatray Date: 10-11-21 -->
		<% } else if(exceptionMode!=null && exceptionMode.equals("IN_OUT")) { %>	
			<tr>
				<td align="right">Start Time:</td>
				<td nowrap="nowrap">
					<s:textfield name="strStartTime" id="strStartTime" cssStyle="width:65px !important;"/>
				</td>
			</tr>
			<tr>
				<td align="right">End Time:</td>
				<td>
					<s:textfield name="strEndTime" id="strEndTime" cssStyle="width:65px !important;"/>
				</td>
			</tr>
		<% } %>
		<!-- Ended By Dattatray Date: 10-11-21 -->
		<tr>
			<td colspan="2" align="center"><s:textarea name="strReason" id="strReason" rows="2" cssStyle="width: 300px !important;"></s:textarea> </td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<% if(exceptionType != null && (exceptionType.equals("FD") || exceptionType.equals("HD"))) { %>
				<input type="button" class="btn btn-primary" value="Enter Reason" onclick="validateHDFDReason(this.form.strReason.value,'<%=request.getAttribute("EMPID") %>','<%=request.getAttribute("DT") %>','<%=request.getAttribute("SID") %>','<%=request.getAttribute("S") %>','<%=request.getAttribute("divid") %>', '<%=exceptionType %>');"/>
			<% } else { %>
				<!-- Created By Dattatray date:12-11-21 Note : added employeeReason-->
				<%-- <input type="button" class="btn btn-primary" value="Enter Reason" onclick="validateReason(this.form.strStartTime.value, this.form.strEndTime.value, this.form.strReason.value,'<%=request.getAttribute("EMPID") %>','<%=request.getAttribute("DT") %>','<%=request.getAttribute("SID") %>','<%=request.getAttribute("S") %>',this.form.AST.value,'<%=request.getAttribute("AET") %>','<%=request.getAttribute("divid") %>','<%=request.getAttribute("exceptionMode") %>','<%=(String)request.getAttribute("employeeReason") %>');" /> --%>
				
			<!-- ====start parvez date: 31-01-2022=== -->	
				<input type="button" class="btn btn-primary" value="Enter Reason" onclick="validateReason(this.form.strStartTime.value, this.form.strEndTime.value, this.form.strReason.value,'<%=request.getAttribute("EMPID") %>','<%=request.getAttribute("DT") %>','<%=request.getAttribute("SID") %>','<%=request.getAttribute("S") %>',this.form.AST.value,'<%=request.getAttribute("AET") %>','<%=request.getAttribute("divid") %>','<%=request.getAttribute("exceptionMode") %>','<%=empReason %>');" />
			<!-- ====end parvez date: 31-01-2022=== -->
			<% } %>
			</td>
		</tr>
	</table>	
<%-- <s:submit cssClass="input_button" value="Enter Reason" onclick="return validateReason(this.form);"></s:submit> document.getElementById('strReason').value <%=request.getAttribute("AST") %> --%>
</s:form>