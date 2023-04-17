<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <script src="<%= request.getContextPath()%>/scripts/color.js" type="text/javascript"></script>

<script type="text/JavaScript">
	var cp = new ColorPicker('window'); 
	var cp2 = new ColorPicker('window');

</script>  --%>

<%

String strSearchJob = (String) request.getAttribute("strSearchJob");
String orgId = (String) request.getAttribute("f_org");
String location = (String) request.getAttribute("strLocation");
String department = (String) request.getAttribute("strDepartment");
String dataType = (String) request.getAttribute("dataType"); 
String currUserType = (String) request.getAttribute("currUserType");
String level = (String) request.getAttribute("strLevel");
String strSearch = (String) request.getAttribute("strSearchJob");

String operation = (String) request.getAttribute("operation");

%>

<div class="leftbox reportWidth">
	<s:form id="frmAddBsc" name="frmAddBsc" theme="simple" action="GoalKRABsc" method="POST" cssClass="formcss" >
		<s:hidden name="operation" />
		<s:hidden name="strBscId" />
		<table class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">BSC Name:<sup>*</sup></th>
				<td>
					<s:textfield name="bscName" id="bscName" cssClass="validateRequired" cssStyle="width:300px !important" />
					<!-- <input type="text" name="bscName" id="bscName" class="validateRequired" style="width:300px !important"/> -->
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Vision:<sup>*</sup></th>
				<td>
					<s:textfield name="bscVision" id="bscVision" cssClass="validateRequired" cssStyle="width:300px !important" />
					<!-- <input type="text" name="bscVision" id="bscVision" class="validateRequired" style="width:300px !important" /> -->
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Mission:</th>
				<td>
					<s:textfield name="bscMision" id="bscMision" cssStyle="width:300px !important" />
					<!-- <input type="text" name="bscMision" id="bscMision" style="width:300px !important"/> -->
				</td>
			</tr>
		
		</table>
		<div id ="divperId">
			
			<%
				int count = 0;
				Map<String, List<String>> hmBSCPerspectives = (Map<String, List<String>>) request.getAttribute("hmBSCPerspectives");
				if(hmBSCPerspectives==null) hmBSCPerspectives = new HashMap<String, List<String>>();
			   	Iterator<String> it = hmBSCPerspectives.keySet().iterator();
				while(it.hasNext()) {
	               	String perspectiveId = it.next();
	               	List<String> innerList = hmBSCPerspectives.get(perspectiveId);
			%>
			<div id ="perspectivediv_<%=count %>">
				<table class="table table_no_border">
					<tr>
						<th class="txtlabel alignRight">Perspective:<sup>*</sup>
							<input type="hidden" name="perspectiveCount" id="perspectiveCount" value="<%=count %>" />
							<input type="hidden" name="perspectiveId_<%=count %>" id="perspectiveId_<%=count %>" value="<%=perspectiveId %>" />
						</th>
						<td>
							<input type="text"  name="strPerspective_<%=count %>" id="strPerspective_<%=count %>" value = "<%=innerList.get(0)%>" class="validateRequired" style="width: 240px !important" />&nbsp;&nbsp;
							<input type="color" name="strPerspectiveColourCode_<%=count %>" id="strPerspectiveColourCode_<%=count %>" value = "<%=innerList.get(3) %>" rel="4" class="required" style="width: 50px !important" />
						</td>
					</tr>
					<tr>
					<th class="txtlabel alignRight">Weightage(%):<sup>*</sup></th>
						<td>
							<input type="text" class="validateRequired form-control" style="width: 40px !important;" name="perspectiveWeightage_<%=count %>" id="perspectiveWeightage_<%=count %>" value = "<%=innerList.get(1)%>" value="100" onkeypress="return isNumberKey(event)"  onkeyup="validatePerspectiveScore(this.value,'perspectiveWeightage_0');"/>&nbsp;&nbsp;
							<textarea rows="1" cols="72" style="width:185px !important" name="strPerspectiveDescription_<%=count %>" id="strPerspectiveDescription_<%=count %>" ><%=innerList.get(2)%></textarea>
							<a href="javascript:void(0)" title="Add Perspective" onclick="addPerspective();"><i class="fa fa-plus-circle"></i></a>
							<% if(count>0) { %>
								<a href="javascript:void(0)" title ="Remove Perspective" onclick="removePerspective('<%=count %>');" class="close-font"></a>
							<% } %>
						</td>
					</tr>
				</table>
			</div>
			<% count++;
			} %>
		
			<input type="hidden" name="persCnt" id="persCnt" value="<%=hmBSCPerspectives.size() %>" />
			
			<% if(hmBSCPerspectives.size()==0) { %>
				<div id ="perspectivediv_0">
					<table class="table table_no_border">
					<tr>
						<th class="txtlabel alignRight">Perspective:<sup>*</sup></th>
						<td>
							<input type="hidden" name="perspectiveCount" id="perspectiveCount" value="0" />
							<input type="hidden" name="perspectiveId" id="perspectiveId" value="0" />
							<input type="text"  name="strPerspective_0" id="strPerspective_0" class="validateRequired" style="width: 240px !important" />&nbsp;&nbsp;
							<input type="color" name="strPerspectiveColourCode_0" id="strPerspectiveColourCode_0" rel="4" class="required" style="width: 50px !important" />
						</td>
					</tr>
					<tr>
						<th class="txtlabel alignRight">Weightage(%):<sup>*</sup></th>
						<td>
							<input type="text" class="validateRequired form-control" style="width: 40px !important;" name="perspectiveWeightage_0" id="perspectiveWeightage_0" value="100" onkeypress="return isNumberKey(event)"  onkeyup="validatePerspectiveScore(this.value,'perspectiveWeightage_0');"/>&nbsp;&nbsp;
							<textarea rows="1" cols="72" name="strPerspectiveDescription_0" id="strPerspectiveDescription_0" style="width:185px !important" placeholder="Perspective description"></textarea>
							<a href="javascript:void(0)" class="" title="Add Perspective" onclick="addPerspective(0);">
							<i class="fa fa-plus-circle"></i></a>
						</td>
					</tr>
					
				</table>
			</div>
		<% } %>
	</div>
	
		<div style="float: left; width: 100%; text-align: center;">
			<% if(operation !=null && operation.equals("U")) { %>
				<s:submit value="Update" cssClass="btn btn-primary" name="submit"></s:submit>
			<% } else { %>
				<s:submit value="Save" cssClass="btn btn-primary" name="submit"></s:submit>
			<% } %>
		</div>
	
		</s:form>
	
</div>

<script type="text/javascript">
	$("#frmAddBsc").submit(function(event){
		var form_data = $("#frmAddBsc").serialize();
		$.ajax({ 
			type : 'POST',
			url: "GoalKRABsc.action",
			data: form_data+"&submit=Save",
			success: function(result){
				//alert(11);
				getBSCView('BscView','BSC','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');
			},
			error: function(result){
				getBSCView('BscView','BSC','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');
			}
		});
	
	});


	function addPerspective() {
		var persCnt = document.getElementById("persCnt").value;
		count = persCnt;
		count++;
		var totweight=0;
		for(var i=0; i<=parseInt(persCnt); i++) {
			var weight = document.getElementById("perspectiveWeightage_"+i);
			if (weight == null || weight == undefined) {
				continue;	
			}
			weight = document.getElementById("perspectiveWeightage_"+i).value;
			if(weight == undefined) {
				weight = 0;
			}
			totweight = totweight + parseFloat(weight);
		}
		var remainweight = 100 - parseFloat(totweight);
		if(remainweight <= 0) {
			alert("Unable to add Perspective because of no weightage available");			
		} else {
			var divid = "perspectivediv_"+count;
			var divtag = document.createElement('div');
			divtag.setAttribute("style", "width: 100%; margin-bottom: 3px;"); //border-bottom: 1px solid #F1F1F1;
			divtag.id = divid;
			var data = "<table class=\"table table_no_border\">"
				+"<tr><th nowrap align=\"right\">Perspective:<sup>*</sup></th>"
				+"<td><input type=\"hidden\" name=\"perspectiveCount\" id=\"perspectiveCount\" value=\""+count+"\" />"
				+"<input type=\"hidden\" name=\"perspectiveId\" id=\"perspectiveId\" value=\"0\" />"
				+"<input type=\"text\" name=\"strPerspective_"+count+"\" id=\"strPerspective_"+count+"\" class=\"validateRequired\" style=\"width: 240px !important;\" />&nbsp;&nbsp"
				+"<input type=\"color\" name=\"strPerspectiveColourCode_"+count+"\" id=\"strPerspectiveColourCode_"+count+"\" rel=\"4\" class=\"required\" style=\"width: 50px !important\" />"
				+"</td></tr>"
				+"<tr>"
				+"<th nowrap align=\"right\" style=\"text-align: center;\">Weightage(%):</th>"
				+"<td>"
				+"<input type=\"text\" class=\"validateRequired form-control\" style=\"width: 40px !important;\" name=\"perspectiveWeightage_"+count+"\" id=\"perspectiveWeightage_"+count+"\" value=\" "+remainweight+"\" onkeyup=\"validatePerspectiveScore(this.value,perspectiveWeightage_"+count+");\" onkeypress=\"return isNumberKey(event)\"/>&nbsp;&nbsp;"
				+"<textarea rows=\"1\" cols=\"72\" name=\"strPerspectiveDescription_"+count+"\" id=\"strPerspectiveDescription_"+count+"\" style=\"width:185px !important\" placeholder=\"Perspective description\"></textarea>"
				+"<a href=\"javascript:void(0)\" title =\"Add Perspective\" onclick=\"addPerspective("+ count+ ");\"><i class=\"fa fa-plus-circle\"></i></a>&nbsp;"
				+"<a href=\"javascript:void(0)\" title =\"Remove Perspective\" onclick=\"removePerspective('" + count + "');\" class=\"close-font\">"
				+"</a>"
				+"</td></tr>"
				+"</table>";
			
			divtag.innerHTML = data;
			document.getElementById("divperId").appendChild(divtag);
			document.getElementById("persCnt").value = count;
			
		}
	}


	function validatePerspectiveScore(value, weightageId) {
		
		var perspectiveCount = document.getElementById("persCnt").value;
		var totweight=0;
		for(var i=0; i<=parseInt(persCnt); i++) {
			var checkCurrId = "perspectiveWeightage_"+i;
			
			var weight = document.getElementById("perspectiveWeightage_"+i);
			if (weight == null) {
				continue;	
			}
			if(weightageId == checkCurrId) {
				//	alert("same id");
			} else {
				weight = document.getElementById("perspectiveWeightage_"+i).value;
				if(weight == undefined) {
					weight = 0;
				}
				totweight = totweight + parseFloat(weight);
			}
		}
		
		var remainweight = 100 - parseFloat(totweight);
	   
		if(parseFloat(value) > parseFloat(remainweight)) {
			alert("Entered value greater than Weightage");
			document.getElementById(weightageId).value = remainweight;
		} else if(parseFloat(value) <= 0) {
			alert("Invalid Weightage");
			document.getElementById(weightageId).value = remainweight;
		}
		
	}


	/* function removePerspective(id) {
		var persCnt = document.getElementById("persCnt").value;
		var row_skill = document.getElementById(id);
		//alert(row_skill);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
			if(parseInt(persCnt) > 0) {
				persCnt--;
			}
			document.getElementById("persCnt").value = persCnt;
		}
	} */
	
	
	function removeDivision(count) {
		//var perspectiveCnt = document.getElementById("perspectiveCount").value;
		var row_skill = document.getElementById("perspectivediv_"+count);
		if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
			row_skill.parentNode.removeChild(row_skill);
		}
	}
	

	$(function() {
		$("input[type='submit']").click(function(){
			$("#frmAddBsc").find('.validateRequired').filter(':hidden').prop('required',false);
			$("#frmAddBsc").find('.validateRequired').filter(':visible').prop('required',true);
				
		});
	 
	 });
		 
	 
		
</script>