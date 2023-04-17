<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.konnect.jpms.util.*"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <script type="text/javascript" src="scripts/customAjax.js"></script> --%>

<%
Map<String,List<String>> hmperspectiveDetails = (Map<String,List<String>>)request.getAttribute("hmperspectiveDetails");
if(hmperspectiveDetails == null) hmperspectiveDetails = new HashMap<String,List<String>>();
int count1 = hmperspectiveDetails.size();
String strSearchJob = (String) request.getAttribute("strSearchJob");
String orgId = (String) request.getAttribute("f_org");
String location = (String) request.getAttribute("strLocation");
String department = (String) request.getAttribute("strDepartment");
String dataType = (String) request.getAttribute("dataType"); 
String currUserType = (String) request.getAttribute("currUserType");
String level = (String) request.getAttribute("strLevel");
String strSearch = (String) request.getAttribute("strSearchJob");

%>

<div class="leftbox reportWidth">
	<s:form id="formEditBsc" name="formEditBsc" theme="simple" action="EditAndDeleteBSC" method="POST" cssClass="formcss" >
		<input type="hidden" name="perspectiveoldCount" id="perspectiveCount" value="<%=count1%>" />
		<input type="hidden" name="strBscId" id="strBscId" value = "<%=(String)request.getAttribute("strBscId") %>" />
		<input type="hidden" name="perspectiveCount" id="perspectiveCount" value="<%=count1%>" />
		<input type="hidden" name="perspectiveCount1" id="perspectiveCount1" />
			
		<table id="tabper" class="table table_no_border">
			<tr>
				<th class="txtlabel alignRight">BSC Name:<sup>*</sup></th>
				<td>
					<input type="text" name="strBscName" id="strBscName" value = "<%=(String)request.getAttribute("strBscName") %>" class="validateRequired" style="width:300px !important"/>
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Vision:<sup>*</sup></th>
				<td colspan="3">
					<input type="text" name="strBscVision" id="strBscVision" value = "<%=(String)request.getAttribute("strBscVision") %>" class="validateRequired" style="width:300px !important" />
				</td>
			</tr>
			<tr>
				<th class="txtlabel alignRight">Mission:</th>
				<td colspan="3">
					<input type="text" name="strBscMission" id="strBscMission" value = "<%=(String)request.getAttribute("strBscMission") %>" style="width:300px !important"/>
				</td>
			</tr>
			<%-- <%
				int count2 = 0;
			   Iterator<String> itperspective = hmperspectiveDetails.keySet().iterator();
				 while(itperspective.hasNext()) {
               	String bsc_perspective_id = itperspective.next();
               List<String> gInnerList = hmperspectiveDetails.get(bsc_perspective_id);
	//			System.out.println("gInnerList::"+gInnerList);
				++count2;
			%>
			<input type="hidden" name="perspectiveId" id="perspectiveId" value = "<%=bsc_perspective_id %>" />
			<tr>
				<th class="txtlabel alignRight">Perspective:<sup>*</sup></th>
					<td  colspan="3">
					
					<div id="perspectivediv_<%=count2%>" style="width: 100%; float: left; margin-bottom: 3px;">
						<div style="width: 100%; float: left; margin-bottom: 3px;">
							<input type="text"  name="strPerspective_<%=count2 %>" id="strPerspective_<%=count2 %>" value = "<%=gInnerList.get(0)%>" class="validateRequired" style="width: 240px !important" />&nbsp;&nbsp;
							<input type="color" name="strPerspectiveColourCode_<%=count2 %>" id="strPerspectiveColourCode_<%=count2 %>" value = "<%=gInnerList.get(3) %>" rel="4"  class="required" style="width: 50px !important" />
						</div>
					
					</div>	
					</td>
			</tr>
			<tr id="perspectiveTR_<%=count2 %>" >
			<th class="txtlabel alignRight">Weightage(%):<sup>*</sup></th>
				<td colspan="3" id = "perspectiveTD" >
					<input type="text" class="validateRequired form-control" style="width: 40px !important;" name="perspectiveWeightage_<%=count2 %>" id="perspectiveWeightage_<%=count2 %>" value = "<%=gInnerList.get(1)%>" value="100" onkeypress="return isNumberKey(event)"  onkeyup="validatePerspectiveScore(this.value,'perspectiveWeightage_0');"/>&nbsp;&nbsp;
					<textarea rows="3" cols="72"  style="height: 40px; width:185px !important" name="strPerspectiveDescription_<%=count2 %>" id="strPerspectiveDescription_<%=count2 %>" ><%=gInnerList.get(2)%></textarea>
						<a href="javascript:void(0)" class="" title="Add Perspective" onclick="addPerspective(<%=count1%>);">
						<i class="fa fa-plus-circle"></i></a>
						<a href="javascript:void(0)" title ="Remove Perspective" onclick="removePerspective('<%=count2%>',<%=bsc_perspective_id %>);" class="close-font">"
						</a>
				</td>
			
			</tr>
			<%} %> --%>
			
		</table>
		
		<div id ="divperId">
			<%
				int count2 = 0;
			   Iterator<String> itperspective = hmperspectiveDetails.keySet().iterator();
				 while(itperspective.hasNext()) {
               	String bsc_perspective_id = itperspective.next();
               List<String> gInnerList = hmperspectiveDetails.get(bsc_perspective_id);
	//			System.out.println("gInnerList::"+gInnerList);
				++count2;
			%>
			<div id ="perspectivediv_<%=count2 %>">
				<table class="table table_no_border">
					<tr>
						<th class="txtlabel alignRight">Perspective:<sup>*</sup>
							<input type="hidden" name="perspectiveId" id="perspectiveId" value="<%=bsc_perspective_id %>" />
						</th>
						<td>
							<input type="text"  name="strPerspective_<%=count2 %>" id="strPerspective_<%=count2 %>" value = "<%=gInnerList.get(0)%>" class="validateRequired" style="width: 240px !important" />&nbsp;&nbsp;
							<input type="color" name="strPerspectiveColourCode_<%=count2 %>" id="strPerspectiveColourCode_<%=count2 %>" value = "<%=gInnerList.get(3) %>" rel="4"  class="required" style="width: 50px !important" />
						</td>
					</tr>
					<tr>
					<th class="txtlabel alignRight">Weightage(%):<sup>*</sup></th>
						<td>
							<input type="text" class="validateRequired form-control" style="width: 40px !important;" name="perspectiveWeightage_<%=count2 %>" id="perspectiveWeightage_<%=count2 %>" value = "<%=gInnerList.get(1)%>" value="100" onkeypress="return isNumberKey(event)"  onkeyup="validatePerspectiveScore(this.value,'perspectiveWeightage_0');"/>&nbsp;&nbsp;
							<textarea rows="1" cols="72" style="width:185px !important" name="strPerspectiveDescription_<%=count2 %>" id="strPerspectiveDescription_<%=count2 %>" ><%=gInnerList.get(2)%></textarea>
							<a href="javascript:void(0)" title="Add Perspective" onclick="addPerspective(<%=count1%>);"><i class="fa fa-plus-circle"></i></a>
							<% if(count2>1) { %>
								<a href="javascript:void(0)" title ="Remove Perspective" onclick="removePerspective('<%=count2%>',<%=bsc_perspective_id %>);" class="close-font"></a>
							<% } %>
						</td>
					</tr>
				</table>
			</div>
			<%} %>
		
		
		</div>
	
		<div style="float: left; width: 100%; text-align: center;">
			<s:submit value="Update" cssClass="btn btn-primary" name="submit"></s:submit>
		</div>
	
		</s:form>
	
</div>
<script type="text/javascript">

	$("#formEditBsc").submit(function(event){
		event.preventDefault();
	
		var form_data = $("#formEditBsc").serialize();
		$.ajax({ 
			type : 'POST',
			url: "EditAndDeleteBSC.action",
			data: form_data+"&submit=update",
			success: function(result){
				getBSCView('BscView','BSC','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');
			},
			error: function(result){
				getBSCView('BscView','BSC','<%=currUserType %>','<%=orgId %>','<%=location %>','<%=department %>','<%=level %>','<%=strSearch %>');
			}
		});
	});

	

function removePerspective(count,id) {
	var perspectiveCnt = document.getElementById("perspectiveCount").value;
	var row_skill = document.getElementById("perspectivediv_"+count);
	if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
	}
	
	var row_skill = document.getElementById("perspectiveTR_"+count);
	if (row_skill && row_skill.parentNode && row_skill.parentNode.removeChild) {
		row_skill.parentNode.removeChild(row_skill);
		}
	--count;
	document.getElementById("perspectiveCount1").value = count;
	document.getElementById("perspectiveCount").value += ","+count+" "+id;
	
	
}

	function addPerspective(count) {
			count++;
			var totweight=0;
			for(var i=1; i<=parseInt(count); i++) {
				var weight = document.getElementById("perspectiveWeightage_"+i);
				if (weight == null) {
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
			divtag.setAttribute("style", "width: 100%; float: left;");
			divtag.id = divid;
			var data = "<table class=\"table table_no_border\">"
				+"<tr><th nowrap align=\"right\"  style=\"text-align:center\";>Perspective:<sup>*</sup></th>"
				+"<td>"
				+"<input type=\"text\"  name=\"strPerspective_"+count+"\" id=\"strPerspective_"+count+"\" style=\"width: 240px !important;text-align:center; \" />&nbsp;&nbsp"
				+"<input type=\"color\" name=\"strPerspectiveColourCode_"+count+"\" id=\"strPerspectiveColourCode_"+count+"\" rel=\"4\" class=\"required\" style=\"width: 50px !important\" />"
				+"</td></tr>"
				+"<tr>"
				+"<th nowrap align=\"right\">Weightage(%):</th>"
				+"<td>"
				+"<input type=\"text\" class=\"validateRequired form-control \" style=\"width: 40px !important;\" name=\"perspectiveWeightage_"+count+"\" id=\"perspectiveWeightage_"+count+"\" value=\" "+remainweight+"\" onkeyup=\"validatePerspectiveScore(this.value,perspectiveWeightage_"+count+");\" onkeypress=\"return isNumberKey(event)\"/>"
				+"<textarea rows=\"1\" cols=\"72\" name=\"strPerspectiveDescription_"+count+"\" id=\"strPerspectiveDescription_"+count+"\" style=\"height: 40px;margin-left:10px; width:185px !important\" placeholder=\"Perspective description\"></textarea>"
				+"<a href=\"javascript:void(0)\" title =\"Add Perspective\" onclick=\"addPerspective("+ count+ ");\"><i class=\"fa fa-plus-circle\"></i></a>&nbsp;"
				+"<a href=\"javascript:void(0)\" title =\"Remove Perspective\" onclick=\"removePerspective('" + count + "','');\" class=\"close-font\">"
				+"</a>"
				+"</td></tr>"
				+"</table>";
			
			divtag.innerHTML = data;
			document.getElementById("divperId").appendChild(divtag);
		} 
			document.getElementById("perspectiveCount1").value = count;
	}
</script>

