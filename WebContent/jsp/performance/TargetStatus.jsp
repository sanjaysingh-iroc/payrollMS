<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%-- <script src='scripts/calender/jquery.min.js'></script> --%>
<script type="text/javascript" charset="utf-8">
function saveRemark(targetID){
		if(confirm('Are you sure, you want to add remark?')){
			var targetRemark = document.getElementById("targetRemark").value; 
			getContent('remarkDiv', "UpdateTargetRemark.action?targetID="+targetID+"&targetRemark=" + targetRemark);
		}
	}
</script>

<%
CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF=new UtilityFunctions();
List<List<String>> targetStatusList=(List<List<String>>)request.getAttribute("targetStatusList");
String measure_type = (String)request.getAttribute("measure_type");
String type = (String)request.getAttribute("type");
String assignedTarget = (String)request.getAttribute("assignedTarget");
String measureType = (String)request.getAttribute("measureType");
String strCurrency = (String) request.getAttribute("strCurrency"); 


%>

<div style="width: 100%; float: left; margin: 7px;"> <b>Target:</b> 
<!-- updated by kalpana on 17/10/2016 changed measureType-----measure_type-----------start -->
		<%if(measure_type != null && measure_type.equals("Amount")){ %>
		
			<%=uF.showData(strCurrency,"")%>&nbsp;<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(assignedTarget)) %>
		<%} else if(measure_type != null && measure_type.equals("Percentage")){ %>
 			<%=assignedTarget%>%
 		<%} else{ %>
 			<%=assignedTarget %>
 		<%} %>
 	<!-- end -->
 </div>
<table class="table table-bordered" width="100%">
  <tr>
    <th>Date</th>
    <th>Employee Name</th>
    <th><%=measure_type %></th><!-- updated by kalpana on 17/10/2016  -->
    <th>Remarks</th>
  </tr>
  <%for(int i=0;targetStatusList!=null && !targetStatusList.isEmpty() && i<targetStatusList.size();i++){
		List<String> innerList=targetStatusList.get(i);  
	%>
  <tr>
   	<td width="10%" valign="top"><%=innerList.get(2) %></td>
   	<td width="35%" valign="top" align="left" style="padding-right:10px"><%=innerList.get(0) %></td>
   	<%if(measure_type != null && measure_type.equals("Percentage")) { %>
   		<td width="20%" valign="top" align="right" style="padding-right:10px"><%=innerList.get(1) %>%</td>
   	<% }else { %>
   		<td width="20%" valign="top" align="right" style="padding-right:10px"><%=innerList.get(1) %></td>
   	<% } %>
   	<td width="34%" valign="top" align="left" style="padding-right:10px">
   	<%if(type != null && type.equals("status")) { %>
   		<%=innerList.get(3) %>
   	<% } else { %>
	   	<%if(innerList.get(3) != null && !innerList.get(3).equals("")) { %>
	   		<%=innerList.get(3) %>
	   	<% } else if(i == 0) { //  %>
	   			<span id="remarkDiv" style="text-align: center; float: left;">
	   				<textarea id="targetRemark" name="targetRemark" class="validateRequired form-control autoWidth" rows="2"></textarea>
		  		 
		   			<input type="button" class="btn btn-primary" name="submit" id="submitBtn" value="Save" onclick="saveRemark('<%=innerList.get(4) %>');"/>
	   			</span>
	   	<% } %>
   	<% } %>
   	</td>
  </tr>
  <% } %>
</table>

<script>
$("#submitBtn").click(function(){
	$(".validateRequired").prop('required',true);
});
</script>
