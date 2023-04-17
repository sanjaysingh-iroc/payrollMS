<%@page import="java.util.List"%>
<% 
		List<List<String>> desigKraDetails = (List<List<String>>) request.getAttribute("desigKraDetails");
		//System.out.println("desigKraDetails --->> " + desigKraDetails);
	%> 
	<table id="tbl_desig_kras" class="table" style="padding-left: 10px;">
	
	<tr><td><b>Designation's Key Responsibility Areas</b></td></tr>
	<% if(desigKraDetails != null && desigKraDetails.size() > 0) {
		for(int i=0; i<desigKraDetails.size(); i++) {
			List<String> innerList = desigKraDetails.get(i);
		%>
		<tr><td style="border-bottom: 1px solid #B6B6B6;">
		
			<span style="float: left; margin-right: 10px;">
				<select name="desigElements" style="width: 130px;" disabled="disabled">
				<option value="">Select Element</option><%=innerList.get(2) %></select>
				<input type="hidden" name="desigElements" value="<%=innerList.get(3) %>"/>
			</span>
			<span style="float: left;"><select name="desigElementAttribute" style="width: 130px;" disabled="disabled">
			<option value="">Select Attribute</option><%=innerList.get(4) %></select>
			<input type="hidden" name="desigElementAttribute" value="<%=innerList.get(5) %>"/>
			</span>
	
			<%if(!innerList.get(8).equals("")) { %>
				<input type="hidden" name="desigEmpKraId" id="desigEmpKraId" value="<%=innerList.get(8) %>"/>
			<% } %>
			<%if(!innerList.get(9).equals("")) { %>
				<input type="hidden" name="desigEmpKraTaskId" id="desigEmpKraTaskId" value="<%=innerList.get(9) %>"/>
			<% } %>
			<input type="hidden" name="desigKraId" id="desigKraId" value="<%=innerList.get(0) %>"/>
			<span style="float: left; width: 100%;">KRA: 
				<input type="text" name="designKRA" style="margin: 5px 0px;" value="<%=innerList.get(1) %>" readonly="readonly"/>
				<span id="checkboxspan<%=i %>" style="margin-left: 7px;"><input name="addFlag" type="checkbox" id="addFlag<%=i %>" <%=innerList.get(7) %> title="Assign this KRA" onclick="changeStatus('<%=i %>')" />
				<input type="hidden" id="status<%=i %>" name="status" value="<%=innerList.get(6) %>"/></span>
			</span>
			<span style="float: left; width: 100%;">Task: 
				<input type="text" name="designKRATask" style="margin: 5px 0px;" value="<%=innerList.get(10) %>" readonly="readonly"/>
				<!-- <a href="javascript:void(0)" style="margin: 0px;" onclick="removeKRA(this.parentNode.parentNode.rowIndex)" class="remove" >&nbsp;</a>
				<a href="javascript:void(0)" style="float: right; margin: 0px;" onclick="addNewKRA();" class="add">&nbsp;</a> -->
			</span>
		</td></tr>
		<% } %>	
	<% } else {%>
		<tr><td>
		<span><a href="javascript:void(0);" onclick="editDesig();"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add KRAs for this Designation</a></span>
		<div class="nodata msg"> <span>No KRA available for this designation.</span> </div></td></tr>
	<% } %>
	</table>
	