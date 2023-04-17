<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.policies.WorkFlowPolicy"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://granule.com/tags" prefix="g"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready( function () {
	$("#submitButton").click(function(){
		$(".validateRequired").prop('required',true);
	});
});		

function checkAnyOne(){

	var anyone=document.getElementById("anyone");
	var userid = document.getElementsByName('userid'); 
	
	//var desigid = document.getElementsByName('desigid');	document.getElementById("myCheck").disabled = true;
	
	if(anyone.checked==true){
		 for(var i=0;i<userid.length;i++){
			 userid[i].checked = false;			 
		 }
		/*  for(var i=0;i<desigid.length;i++){
			 desigid[i].checked = false;			 
		 } */
	}else{		
		for(var i=0;i<userid.length;i++){
			 userid[i].checked = false;			 
		 }
		/*  for(var i=0;i<desigid.length;i++){
			 desigid[i].checked = false;			 
		 } */
		 
	}
}

function getGroupType(){
    
	var groupType = document.getElementsByName('groupType');
	 if(groupType.length > 0){
	
		//alert("groupType ===>> " + groupType);
		//alert("groupType[1].checked ===>> " + groupType[1].checked +" -- groupType[1].value ===>> " + groupType[1].value);
	     /* if(groupType[1].checked && groupType[1].value == '2'){
	    
	    	document.getElementById("trUser").style.display= 'none';
			//document.getElementById("trDesig").style.display= 'table-row';
			document.getElementById("trAnyOne").style.display= 'none';
	    } else  */
	    if(groupType[1].checked && groupType[1].value == '3'){
	    	
	    	//alert("in if  groupType[1].checked ===>> " + groupType[1].checked +" -- groupType[1].value ===>> " + groupType[1].value);
			document.getElementById("trUser").style.display= 'none';
			//document.getElementById("trDesig").style.display= 'none';
			document.getElementById("trAnyOne").style.display= 'table-row';
		} else {
			//alert(" in else ");
			document.getElementById("trUser").style.display= 'table-row';
			//document.getElementById("trDesig").style.display= 'none';
			document.getElementById("trAnyOne").style.display= 'none';
		} 
	} else {
		document.getElementById("trUser").style.display= 'table-row';
		//document.getElementById("trDesig").style.display= 'none';
		document.getElementById("trAnyOne").style.display= 'none';
	}  
}




/* function updateGroup(operation, group_id) {
	
alert("update data successfully"+group_id);

 	removeLoadingDiv('the_div');
	
	  var dialogEdit = '#workflowmemdivid';
	  dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 700,
			width :  600,
						
			modal : true,
			title : 'Work Flow Group',
			open : function() {
				var xhr = $.ajax({
					url : "AddWorkFlowMember.action?operation="+operation+"&group_id="+group_id+"&groupName="+groupName+"&groupType="+groupType+"&type="+type,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
			
			xhr = null;
			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open'); 
	} */







</script>

<%
	UtilityFunctions uF=new UtilityFunctions();
	List<List<String>> userTypeList=(List<List<String>>)request.getAttribute("userTypeList");
	
	//List<List<String>> desigList=(List<List<String>>)request.getAttribute("desigList");
	Map<String, String> hmGroup=(Map<String, String>)request.getAttribute("hmGroup");
	String type=(String)request.getAttribute("type");
	String policyCnt=(String)request.getAttribute("policyCnt");
	//System.out.println("++++====================="+policyCnt);
	
     
%>

<div class="aboveform">
	<s:form theme="simple"  id="formAddNewRow1" action="AddWorkFlowMember" method="POST" cssClass="formcss">
	<s:hidden name="operation"></s:hidden>
	<s:hidden name="group_id"></s:hidden>
	<s:hidden name="organization"></s:hidden>
	<s:hidden name="location"></s:hidden>
	<s:hidden name="type"></s:hidden>
	<s:hidden name="policyCnt"></s:hidden>
	<s:hidden name="userscreen" />
	<s:hidden name="navigationId" />
	<s:hidden name="toPage" />
		<div>
				<table border="0" class="table table_no_border">	
					<tr>
						<td nowrap="nowrap" class="txtlabel" align="right" style="width: 20%">Group Name:<sup>*</sup> </td>
						<td><s:textfield name="groupName" id="groupName" cssClass="validateRequired text-input" required="true"></s:textfield></td>
					</tr>
					<tr> 
					
						<td nowrap="nowrap" class="txtlabel" align="right">Group Type: </td>
						<td>
							<%-- <s:radio name="groupType" id="groupType" list="#{'1':'User','2':'Designation','3':'Any One'}" onclick="getGroupType();"/> --%>
							 <% if(uF.parseToInt(policyCnt) == 0) { %>
							 	<s:radio name="groupType" id="groupType" list="#{'1':'User Profile','3':'Any One'}" onclick="getGroupType();"/>
							 <% } else { %>         
								<s:radio name="groupType" id="groupType" list="#{'1':'User Profile','3':'Any One'}" disabled="true"/>
							<% } %>
						</td>	
					</tr>
					<tr id="trUser">
						<td nowrap="nowrap" class="txtlabel" align="right" valign="top">User Profile : </td>
						<td>
						<%
							for(int i=0;userTypeList!=null && i<userTypeList.size();i++){
								List<String> innerList=userTypeList.get(i);
						%>
					<%-- 	<p><input type="checkbox" name="userid" id="userid<%=i%>" value="<%=innerList.get(0)%>"   
						<%if(hmGroup!=null && hmGroup.get(innerList.get(0).trim())!=null){ %>checked="checked"<%} %>><%=innerList.get(1)%>
						</p> 
					
						<%} %>
						</td> --%>
					 	<p><input type="checkbox" name="userid" id="userid<%=i%>" style="margin-right: 4px;" value="<%=innerList.get(0)%>"   
							<%if(hmGroup!=null && hmGroup.get(innerList.get(0).trim())!=null){ %>checked="checked"<%} %> <% if(uF.parseToInt(policyCnt) > 0) { %> disabled="disabled" <% } %>><%=innerList.get(1)%>
						</p> 
						<%} %>
						</td> 
						
					
						
					</tr>
					
					<%-- <tr id="trDesig" style="display:none;">
						<td nowrap="nowrap" class="txtlabel" align="right" valign="top">Designation : </td>
						<td>
						<%
							for(int i=0;desigList!=null && i<desigList.size();i++){
								List<String> innerList=desigList.get(i);
						%>
						<p><input type="checkbox" name="desigid" id="desigid<%=i%>" value="<%=innerList.get(0)%>"
						<%if(hmGroup!=null && hmGroup.get(innerList.get(0).trim())!=null){ %>checked="checked"<%} %>>[<%=innerList.get(1)%>] <%=innerList.get(2)%>
						</p>
						<%} %>						
						</td>
					</tr> --%>
					
					<tr id="trAnyOne" style="display:none;">
						<td nowrap="nowrap" class="txtlabel" align="right" valign="top">&nbsp;</td>
						<td><input type="checkbox" name="anyone" id="anyone" style="margin-right: 4px;" value="0"
						<%if(hmGroup!=null && hmGroup.get("0")!=null){ %>checked="checked"<%} %> onclick="checkAnyOne();">Any One						
						</td>
					</tr>
					
					<% if(type==null || type.equals("0")) { %>
					
					<tr>
						<td>&nbsp;</td>
						<td align="left"><s:submit cssClass="btn btn-primary" value="Save" name="submit" id="submitButton"></s:submit></td>
					</tr>
					<%} else if(uF.parseToInt(policyCnt) == 0) {  %> 
		   				
		   			<tr>
						<td>&nbsp;</td>
						<td align="left"><s:submit cssClass="btn btn-primary" value="Update" name="submit" id="submitButton"></s:submit></td>
					</tr>
		   			                                          
					<%
						}
					%> 
	 				</table>
		
				
		</div>
	</s:form>
</div>

<script type="text/javascript">
getGroupType();
</script>
