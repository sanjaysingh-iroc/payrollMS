<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script>
	$(document).ready( function () {
		$("#btnAddNewRowOk").click(function(){
			$(".validateRequired").prop('required', true);
		});
	});
	
</script>


	<% 
		UtilityFunctions uF = new UtilityFunctions();
		String classId = (String)request.getAttribute("classId");
		String sbDivLevelList = (String)request.getAttribute("sbDivLevelList");
	%>

		<s:form theme="simple" action="AddEditDeleteClassAndDivision" method="POST" cssClass="formcss" id="formAddEditDeleteClassAndDivision" name="formAddEditDeleteClassAndDivision">
			<s:hidden name="classId" />
			<s:hidden name="userscreen"></s:hidden>
			<s:hidden name="navigationId"></s:hidden>
			<s:hidden name="toPage"></s:hidden>

			<table class="table table_no_border">
				<tr><td colspan=2><s:fielderror/></td></tr>
				<tr>
					<th class="txtlabel alignRight">Select Organization:<sup>*</sup></th>
					<td><s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" headerKey="" headerValue="Select Organisation"
					 cssClass="validateRequired"></s:select></td> 
				</tr>
				
				<%-- <tr>   
					<th class="txtlabel alignRight">Class Code:</th>
					<td><s:textfield name="serviceCode" cssClass="validateRequired" /><span class="hint">Short code of the class.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				 --%>
				<tr>
					<th class="txtlabel alignRight">Class Name:<sup>*</sup></th>
					<td><s:textfield name="className" cssClass="validateRequired"/><span class="hint">Name of the class.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<th class="txtlabel alignRight" valign="top">Description:</th>
					<td><s:textarea name="classDescription" rows="3" cols="22"/><span class="hint">Description of the class.<span class="hint-pointer">&nbsp;</span></span></td> 
				</tr>
				
				<tr>
					<th class="txtlabel alignRight" valign="top">Class Level:<sup>*</sup></th>
					<td><s:select list="classLevelList" name="classLevel" listKey="classOrDivId" listValue="classOrDivName" headerKey="" headerValue="Select Level" cssClass="validateRequired"></s:select></td> 
				</tr>
				
				<tr>
					<td colspan="2">
						<div id ="divperId">
			
							<%
								int count = 0;
								Map<String, List<String>> hmClassDiv = (Map<String, List<String>>) request.getAttribute("hmClassDiv");
								if(hmClassDiv==null) hmClassDiv = new HashMap<String, List<String>>();
							   	Iterator<String> it = hmClassDiv.keySet().iterator();
								while(it.hasNext()) {
					               	String divId = it.next();
					               	List<String> innerList = hmClassDiv.get(divId);
							%>
							<div id ="divisionDiv_<%=count %>">
								<table class="table table_no_border">
									<tr>
										<th class="txtlabel alignRight">Division/ Section:<sup>*</sup>
											<input type="hidden" name="divCount" id="divCount" value="<%=count %>" />
											<input type="hidden" name="divId_<%=count %>" id="divId_<%=count %>" value="<%=divId %>" />
										</th>
										<td>
											<input type="text"  name="strDiv_<%=count %>" id="strDiv_<%=count %>" value = "<%=innerList.get(1)%>" class="validateRequired" />
										</td>
									</tr>
									<tr>
									<th class="txtlabel alignRight">Division Level:<sup>*</sup></th>
										<td><select name="divLevel_<%=count %>" id="divLevel_<%=count %>" class="validateRequired" >
												<%=innerList.get(2) %>
											</select>
											<a href="javascript:void(0)" title="Add Division" onclick="addDivision();"><i class="fa fa-plus-circle"></i></a>
											<% if(count>0) { %>
												<a href="javascript:void(0)" title ="Remove Division" onclick="removeDivision('<%=count %>');" class="close-font"></a>
											<% } %>
										</td>
									</tr>
								</table>
							</div>
							<% count++;
							} %>
							<input type="hidden" name="divCnt" id="divCnt" value="<%=hmClassDiv.size() %>" />
							<% if(hmClassDiv.size()==0) { %>
								<div id ="divisionDiv_0">
									<table class="table table_no_border">
									<tr>
										<th class="txtlabel alignRight">Division/ Section:<sup>*</sup>
											<input type="hidden" name="divCount" id="divCount" value="<%=count %>" />
											<input type="hidden" name="divId_0" id="divId_0" value="0" />
										</th>
										<td>
											<input type="text"  name="strDiv_0" id="strDiv_0" class="validateRequired" />
										</td>
									</tr>
									<tr>
									<th class="txtlabel alignRight">Division Level:<sup>*</sup></th>
										<td>
											<s:select list="divLevelList" name="divLevel_0" id="divLevel_0" listKey="classOrDivId" listValue="classOrDivName" headerKey="" headerValue="Select Level" cssClass="validateRequired"></s:select>
											<a href="javascript:void(0)" title="Add Division" onclick="addDivision();"><i class="fa fa-plus-circle"></i></a>
											<% if(count>0) { %>
												<a href="javascript:void(0)" title ="Remove Division" onclick="removeDivision('<%=count %>');" class="close-font"></a>
											<% } %>
										</td>
									</tr>
								</table>
							</div>
						<% } %>
					</div>
					
					</td>
				</tr>
				
				<tr>
					<td colspan="2" align="center">
					<% if(uF.parseToInt(classId) > 0) { %>
						<s:submit cssClass="btn btn-primary" value="Update" id="btnAddNewRowOk"/>
					<% } else { %>
						<s:submit cssClass="btn btn-primary" value="Save" id="btnAddNewRowOk"/>
					<% } %>	
					</td>
				</tr>
				
			</table>
		
		</s:form>




<script>

function addDivision() {
	var divCnt = document.getElementById("divCnt").value;
	count = divCnt;
	count++;
	var divid = "divisionDiv_"+count;
	var divtag = document.createElement('div');
	divtag.setAttribute("style", "width: 100%; margin-bottom: 3px;"); //border-bottom: 1px solid #F1F1F1;
	divtag.id = divid;
	var data = "<table class=\"table table_no_border\">"
		+"<tr><th nowrap align=\"right\">Division/ Section:<sup>*</sup></th>"
		+"<td><input type=\"hidden\" name=\"divCount\" id=\"divCount\" value=\""+count+"\" />"
		+"<input type=\"hidden\" name=\"divId_"+count+"\" id=\"divId_"+count+"\" value=\"0\" />"
		+"<input type=\"text\" name=\"strDiv_"+count+"\" id=\"strDiv_"+count+"\" class=\"validateRequired\"/>"
		+"</td></tr>"
		+"<tr>"
		+"<th nowrap align=\"right\" style=\"text-align: center;\">Division Level:<sup>*</sup></th>"
		+"<td>"
		+"<select name=\"divLevel_"+count+"\" id=\"divLevel_"+count+"\" class=\"validateRequired\" >"+
		"<option value=''>Select Level</option>"+
		"<%=sbDivLevelList %>"+
		"</select>"
		+"<a href=\"javascript:void(0)\" title =\"Add Division\" onclick=\"addDivision("+ count+ ");\"><i class=\"fa fa-plus-circle\"></i></a>&nbsp;"
		+"<a href=\"javascript:void(0)\" title =\"Remove Division\" onclick=\"removeDivision('" + count + "');\" class=\"close-font\">"
		+"</a>"
		+"</td></tr>"
		+"</table>";
	
	divtag.innerHTML = data;
	document.getElementById("divperId").appendChild(divtag);
	document.getElementById("divCnt").value = count;
		
}


function removeDivision(count) {
	//var divCount = document.getElementById("divCount").value;
	var row_skill = document.getElementById("divisionDiv_"+count);
	if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
	}
}

</script>