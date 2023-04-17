<%@page import="java.util.Map"%>

<script type="text/javascript">

function isNumberKey(evt) {
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46)
	      return false;
	
	   return true;
	}

function checkPercentage(value) {
	//alert("value ===>> " + value);
	if(parseFloat(value) > 100) {
		alert("Please check, completion percentage greater than 100");
		document.getElementById("percent").value = '0';
	}
}

</script>
<%
Map hmTaskDetails = (Map)request.getAttribute("hmTaskDetails"); 
%>

<h5>Update completion status in % of <%=hmTaskDetails.get("TASK_NAME")%> assigned to <%=hmTaskDetails.get("EMP_NAME")%></h5> 
<br/>
Completion Status: <input type="text" name="completion" value="<%=hmTaskDetails.get("COMPLETED")%>" id="percent" style="width:80px !important;" onkeyup="checkPercentage(this.value);" onkeypress="return isNumberKey(event)"/> %
<br/><br/>
<%-- <input type="button" value="Save" class="input_button" style="margin-left:130px" onclick="saveStatus(<%=hmTaskDetails.get("TASK_ID")%>, document.getElementById('percent').value, '#myPer<%=hmTaskDetails.get("TASK_ID")%>')" /> --%>
<input type="button" value="Save" class="btn btn-primary" style="margin-left:50px" onclick="saveStatus(<%=hmTaskDetails.get("TASK_ID")%>, document.getElementById('percent').value, '<%=(String)request.getAttribute("divId")%>', '<%=(String)request.getAttribute("proId")%>', '')" />
<input type="button" value="100 % Complete" class="btn btn-primary" onclick="saveStatus(<%=hmTaskDetails.get("TASK_ID")%>, document.getElementById('percent').value, '<%=(String)request.getAttribute("divId")%>', '<%=(String)request.getAttribute("proId")%>', 'complete')" />

