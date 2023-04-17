<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

	<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>>  memberList=(List<List<String>>  )request.getAttribute("memberList");
	Map<String, List<String>> mp = (Map<String, List<String>>)request.getAttribute("mp"); 
	if(mp == null) mp = new HashMap<String, List<String>>();
	String operation = (String) request.getAttribute("operation");
	%>
<script type="text/javascript">
	jQuery(document).ready(function() {
	    $("#submitButton").click(function(){
	    	$(".validateRequired").prop('required',true);
	    });
	});
	
	function checkMemberStatus(memberId) {
		var val = document.getElementById(memberId).checked;
		if(val == 'true' || val == true) {
			document.getElementById(memberId+"_viewEditDiv").style.display = "block";
			document.getElementById(memberId+"_view").checked = true;
			document.getElementById(memberId+"_edit").checked = false;
		} else {
			document.getElementById(memberId+"_viewEditDiv").style.display = "none";
		}
	}
	

	function orientValidation(val) {
   	  //alert("val ===>> " + val + " -- val1 ===>> " + val1)
   	  	var hideOrientName = document.getElementById("hideOrientName").value;
    	if(val == hideOrientName) {
    		document.getElementById("orientValidatorMessege").innerHTML='';
    		return;
    	}
    	 var xmlhttp = GetXmlHttpObject();
   		if (xmlhttp == null) {
   			alert("Browser does not support HTTP Request");
   			return;
   		} else {
   			var xhr = $.ajax({
   				url : 'EmailValidation.action?strOrientName='+val,
   				cache : false,
   				success : function(data) {
   					document.getElementById("orientValidatorMessege").innerHTML = data;
   					if(data.length > 1) {
   						document.getElementById("orientName").value = '';
   					}
   				}
   			});
   		}
      }

	
	function checkOrientMembers() {
		var cnt=0;
		<% for(int j=0; memberList!=null && j<memberList.size(); j++) {
			List<String> memberInner = memberList.get(j); %>
			var v1 = '<%=memberInner.get(2) %>';
			if ($('input[name='+v1+']:checked').length > 0) {
			    cnt++;
			}
		<%}%>
	 	//alert("cnt ===>> " + cnt);
	 	if(parseInt(cnt) == 0) {
		    alert('Please select atleast one orientation member');
		    return false;
	    }
	    return true;
    }
	
     function GetXmlHttpObject() {
		if (window.XMLHttpRequest) {
			return new XMLHttpRequest();
		}
		if (window.ActiveXObject) {
			return new ActiveXObject("Microsoft.XMLHTTP");
		}
	return null;
	}
     
</script>

<div> 
	
	<s:form theme="simple" name="frmAddOrientation" id="frmAddOrientation" method="POST" action="AddOrientation" onsubmit="return checkOrientMembers();">
		<s:hidden name="operation" />
		<s:hidden name="ID" id="ID" />
		<s:hidden name="userscreen" />
		<s:hidden name="navigationId" />
		<s:hidden name="toPage" />
		<s:hidden name="strOrg" />
		<input type="hidden" name="hideOrientName" id="hideOrientName" value="<%=uF.showData((String)request.getAttribute("orientName"), "") %>" />
		<table border="0" class="table table_no_border">
		<tr><td class="txtlabel alignRight">Orientation Name:<sup>*</sup></td>
			<td><s:textfield theme="simple" name="orientName" id="orientName" cssClass="validateRequired" onchange="orientValidation(this.value)" />
				<div id="orientValidatorMessege"></div>
			</td>
		</tr>
		<%for(int j=0; j<memberList.size(); j++) {
			List<String> memberInner = memberList.get(j);
			List<String> orientMemberInner = mp.get(memberInner.get(2)); 
			%>
			<tr><td class="txtlabel alignRight" style="padding: 0px 10px"><%= memberInner.get(1)%></td>
			<td><div style="float: left;">
			<input type="checkbox" name="<%=memberInner.get(2) %>" id="<%=memberInner.get(2) %>" value=""
			<%
			String accessDisp = "none";
			if(mp != null && mp.get(memberInner.get(2))!=null) { 
				accessDisp = "block";
			%>
			checked="checked"
			<%} %> onclick="checkMemberStatus('<%=memberInner.get(2) %>');" /></div>
			<div id="<%=memberInner.get(2) %>_viewEditDiv" style="float: left; margin-left: 10px; display: <%=accessDisp %>;">
			<input type="checkbox" name="<%=memberInner.get(2) %>_view" id="<%=memberInner.get(2) %>_view" value="" 
			<%if(orientMemberInner != null && uF.parseToBoolean(orientMemberInner.get(2))) { %>
				checked="checked"
			<%} %> /> View &nbsp;&nbsp;
			<input type="checkbox" name="<%=memberInner.get(2) %>_edit" id="<%=memberInner.get(2) %>_edit" value="" 
			<%if(orientMemberInner != null && uF.parseToBoolean(orientMemberInner.get(3))) { %>
				checked="checked"
			<%} %> /> Edit</div>
		</td></tr>
		<%} %>
		<tr>
			<td colspan="2" align="center">
			<% if(operation != null && operation.equals("A")) { %>
				<s:submit name="btnSubmit" value="Submit" cssClass="btn btn-primary" id="submitButton"/>
			<% } else if(operation != null && operation.equals("E")) { %>
				<s:submit name="btnSubmit" value="Update" cssClass="btn btn-primary" id="submitButton"/>
			<% } %>
			</td>
		</tr>
	</table>
	
	</s:form>
</div>
