<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%List alKRAs = (List)request.getAttribute("alKRAs"); %>

<script>
$( "#idEffectiveDate" ).datepicker({dateFormat: 'dd/mm/yy'});
var cnt=0;
<% if (alKRAs!=null) {%>
cnt=<%=alKRAs.size()%>;
<%}else{%>
cnt =0;
<%}%>

function addKRA() {
	
	cnt++;
	var divTag = document.createElement("div");
    divTag.id = "row_kra"+cnt;
	divTag.innerHTML = 	"<div style=\"float:left\">"+
						"<textarea rows=\"1\" cols=\"100\" style=\"width:80%\" name=\"empKra\"></textarea>"+
    			    	"<a href=\"javascript:void(0)\" onclick=\"addKRA()\" class=\"add\" style=\"float:right\">Add</a>" +
    			    	"<a href=\"javascript:void(0)\" onclick=\"removeKRA(this.id)\" id=\""+cnt+"\" class=\"remove\">Remove</a>"+
    			    	"</div>"+
    			    	"<div class=\"clr\"/>"; 
    document.getElementById("div_kras").appendChild(divTag);
    
}

function removeKRA(removeId) {
	
	var remove_elem = "row_kra"+removeId;
	var row_kra = document.getElementById(remove_elem); 
	document.getElementById("div_kras").removeChild(row_kra);
	
}

</script>

<div class="leftbox reportWidth" style="width:92%">
	

	<s:form id="formID" name="frmLeave" theme="simple"	action="AddKRA" method="POST" cssClass="formcss">
	<input type="hidden" name="empId" value="<%=request.getParameter("empId") %>" />
	<s:hidden name="strCurrentIds"></s:hidden>
	<s:hidden name="strCurrentEffectiveDate"></s:hidden>
	<s:hidden name="type"></s:hidden>
		
		<div style="float:right;padding-right: 37px;">
			Effective Date  : &nbsp;
			<s:textfield name="strEffectiveDate" id="idEffectiveDate" cssStyle="width:100px"></s:textfield>
		</div>
		<div id="div_kras" style="float:left">
			<%int cnt=1;for(int i=0; alKRAs!=null && i<alKRAs.size(); i++){ %>
			<div id="row_kra<%=cnt%>" style="float:left">
				<input type="hidden" name="empKraId" value="<%=(String)alKRAs.get(i++)%>"/>
				<textarea id="row_kra<%=cnt%>" rows="1" cols="100" style="width:92%" name="empKra"><%=(String)alKRAs.get(i)%></textarea>
				<a href="javascript:void(0)" onclick="removeKRA(this.id)" id="<%=cnt%>" class="remove">Remove</a>
			</div>
			<div class="clr"/>
			<%cnt++;} %>
			
			<div style="float:left">
				<textarea id="row_kra<%=cnt%>" rows="1" cols="100" style="width:92%" name="empKra"></textarea>
				<a href="javascript:void(0)" onclick="addKRA()" class="add" style="float:right">Add</a>
			</div>
		</div>
		
		
		<div style="float:left">
			<s:submit value="Save" name="kraSubmit" cssClass="input_button"></s:submit>
		</div>
	</s:form>
 </div>






				